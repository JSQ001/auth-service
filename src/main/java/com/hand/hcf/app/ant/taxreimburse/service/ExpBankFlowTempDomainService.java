package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpBankFlow;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpBankFlowTempDomain;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpBankFlowTempDomainMapper;
import com.hand.hcf.app.ant.taxreimburse.utils.TaxReimburseConstans;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.expense.common.utils.StringUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyI18nMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.itextpdf.text.io.StreamUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 银行流水数据导入临时domain--service
 * @date 2019/6/18 19:30
 */
@Service
public class ExpBankFlowTempDomainService extends BaseService<ExpBankFlowTempDomainMapper, ExpBankFlowTempDomain> {

    @Autowired
    ExpBankFlowTempDomainMapper expBankFlowTempDomainMapper;

    @Autowired
    private  ExpBankFlowService expBankFlowService;

    @Autowired
    private CurrencyI18nMapper currencyI18nMapper;

    @Autowired
    private CompanyService companyService;

    /**
     * 删除之前全部数据
     */
    @Transactional
    public void deleteHistoryData() {
        baseMapper.delete(new EntityWrapper<ExpBankFlowTempDomain>().le("created_date", ZonedDateTime.now()));
    }

    /**
     * 校验数据合法性
     *
     * @param importDataList
     * @param batchNumber
     */
    public void checkImportData(List<ExpBankFlowTempDomain> importDataList, String batchNumber) {
        //初始化数据
        importDataList.stream().forEach(item -> {
            item.setBatchNumber(batchNumber.toString());
            item.setErrorFlag(false);
            item.setErrorDetail("错误详情:");
        });
        //非空校验
        importDataList.stream().filter(item -> StringUtil.isNullOrEmpty(item.getRowNumber())
                        || StringUtil.isNullOrEmpty(item.getCompanyCode())
                        || StringUtil.isNullOrEmpty(item.getPayDate())
                        || StringUtil.isNullOrEmpty(item.getFundFlowNumber())
                        || StringUtil.isNullOrEmpty(item.getBankAccountName())
                        || StringUtil.isNullOrEmpty(item.getCurrencyCode())
                        || StringUtil.isNullOrEmpty(item.getBankRemark())
                //|| (StringUtil.isNullOrEmpty(item.getFlowAmountDebit())|| StringUtil.isNullOrEmpty(item.getFlowAmountLender()))
        )
                .forEach(item -> {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + "必输字段不能为空;");
                });
        importDataList.stream().forEach(item -> {
            //银行流水号必须是唯一的
            if(StringUtil.isNullOrEmpty(item.getFundFlowNumber())){
                Long bankFlowNumber = Long.valueOf(item.getFundFlowNumber());
                Wrapper wrapper = new EntityWrapper<ExpBankFlow>()
                        .eq( "fund_flow_number", bankFlowNumber);
                List<ExpBankFlow> expBankFlowList = expBankFlowService.selectList(wrapper);
                if(expBankFlowList.size()>0){
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() +item.getFundFlowNumber()+":"+"银行流水号不能重复;");
                }
            }

            //公司合法校验
            String companyCode = item.getCompanyCode();
            if(!com.hand.hcf.app.mdata.utils.StringUtil.isNullOrEmpty(companyCode)){
                CompanyCO companyCO = companyService.getByCompanyCode(companyCode);
                if(companyCO != null){
                    item.setCompanyId(companyCO.getId());
                }else {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() +item.getCompanyCode()+":"+"此OU对应公司不存在;");
                }
            }

            //贷方金额和借方金额不能同时为空
            if (StringUtil.isNullOrEmpty(item.getFlowAmountDebit()) && StringUtil.isNullOrEmpty(item.getFlowAmountLender())) {
                item.setErrorFlag(true);
                item.setErrorDetail(item.getErrorDetail() + "贷方金额和借方金额不能同时为空;");
            }

            //币种校验
            if(StringUtil.isNullOrEmpty(item.getCurrencyCode())){
                CurrencyI18n currencyI18n = new CurrencyI18n();
                currencyI18n.setCurrencyCode(item.getCurrencyCode());
                String language = OrgInformationUtil.getCurrentLanguage();
                currencyI18n.setLanguage(language);
                CurrencyI18n currencyOne = currencyI18nMapper.selectOne(currencyI18n);
                if(null == currencyOne ){
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() +item.getCurrencyCode()+":"+ "此币种代码对应币种不存在;");
                }
            }

            //银行交易日期格式校验，应该为yyyyMMdd格式，例如“20190101”;
            if (StringUtil.isNullOrEmpty(item.getPayDate())) {
                boolean convertSuccess = true;
                // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                try {
                    // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
                    format.setLenient(false);
                    format.parse(item.getPayDate());
                } catch (ParseException e) {
                     e.printStackTrace();
                    // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
                    convertSuccess = false;
                }
                if(convertSuccess==false){
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() +item.getPayDate()+":"+  "银行交易日期格式不正确，应该为yyyyMMdd格式，例如“20190101”;");
                }
            }
        });

    }

    /**
     * 根据批次号查询数据是否存在
     *
     * @param transactionID
     * @return
     */
    public Boolean varifyBatchNumberExsits(String transactionID) {
        boolean flag = false;
        List<ExpBankFlowTempDomain> expBankFlowTempDomainList = selectList(
                new EntityWrapper<ExpBankFlowTempDomain>()
                        .eq("batch_number", transactionID));
        if(expBankFlowTempDomainList.size()>0){
            flag =true;
        }
        return flag;
    }

    /**
     * 根据批号查询导入结果
     *
     * @param transactionID
     * @return
     */
    public ImportResultDTO queryResultInfo(String transactionID) {
        return baseMapper.queryInfo(transactionID);
    }


    /**
     * 导入失败后,导出错误信息
     * @param transactionID
     * @return
     */
    public byte[] exportFailedData(String transactionID) {

        List<ExpBankFlowTempDomain> expBankFlowTempDomainList = selectList(
                new EntityWrapper<ExpBankFlowTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 1));
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;
        try {
            in = StreamUtil.getResourceStream(TaxReimburseConstans.BANK_FLOW_IMPORT_ERROR_TEMPLATE_PATH);
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = TaxReimburseConstans.EXCEL_BASEROW_ERROR;
            Row row = null;
            Cell cell = null;
            for (ExpBankFlowTempDomain expBankFlowTempDomain : expBankFlowTempDomainList) {
                row = sheet.createRow(startRow++);
                //行号
                cell = row.createCell(TaxReimburseConstans.BANK_ROW_NUMBER_ERROR);
                cell.setCellValue(expBankFlowTempDomain.getRowNumber());
                //其他字段
                cell = row.createCell(TaxReimburseConstans.BANK_COMPANY_CODE_ERROR);
                cell.setCellValue(expBankFlowTempDomain.getCompanyCode());

                cell = row.createCell(TaxReimburseConstans.pay_date_error);
                cell.setCellValue(expBankFlowTempDomain.getPayDate());

                cell = row.createCell(TaxReimburseConstans.fund_flow_number_error);
                cell.setCellValue(expBankFlowTempDomain.getFundFlowNumber());

                cell = row.createCell(TaxReimburseConstans.bank_account_name_error);
                cell.setCellValue(expBankFlowTempDomain.getBankAccountName());

                cell = row.createCell(TaxReimburseConstans.flow_amount_debit_error);
                cell.setCellValue(expBankFlowTempDomain.getFlowAmountDebit());

                cell = row.createCell(TaxReimburseConstans.flow_amount_lender_error);
                cell.setCellValue(expBankFlowTempDomain.getFlowAmountLender());

                cell = row.createCell(TaxReimburseConstans.currency_code_error);
                cell.setCellValue(expBankFlowTempDomain.getCurrencyCode());

                cell = row.createCell(TaxReimburseConstans.bank_remark_error);
                cell.setCellValue(expBankFlowTempDomain.getBankRemark());

                cell = row.createCell(row.getLastCellNum());
                cell.setCellValue(expBankFlowTempDomain.getErrorDetail());
            }
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.flush();
            workbook.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new BizException(RespCode.SYS_READ_FILE_ERROR);
        }
    }

    //根据批次号删除临时表中的数据
    @Transactional
    public Integer deleteImportData(String transactionID) {
        return baseMapper.delete(new EntityWrapper<ExpBankFlowTempDomain>().eq("batch_number", transactionID));
    }

    /**
     * 根据批次号查询税金申报信息临时表中的数据
     *
     * @param transactionID
     * @param page
     * @return
     */
    public List<ExpBankFlowTempDomain> listImportMessageByTransactionID(String transactionID, Page page) {
        return baseMapper.selectPage(page,
                new EntityWrapper<ExpBankFlowTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", false));
    }


}
