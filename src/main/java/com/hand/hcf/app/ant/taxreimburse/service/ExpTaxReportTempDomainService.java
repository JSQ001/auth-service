package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.SqlHelper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReport;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReportTempDomain;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpTaxReportMapper;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpTaxReportTempDomainMapper;
import com.hand.hcf.app.ant.taxreimburse.utils.TaxReimburseConstans;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.expense.common.utils.StringUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.itextpdf.text.io.StreamUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金申报信导入临时domain--service
 * @date 2019/6/18 19:30
 */
@Service
public class ExpTaxReportTempDomainService extends BaseService<ExpTaxReportTempDomainMapper, ExpTaxReportTempDomain> {

    @Autowired
    ExpTaxReportTempDomainMapper expTaxReportTempDomainMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    ExpTaxReportMapper expTaxReportMapper;

    /**
     * 删除之前全部数据
     */
    @Transactional
    public void deleteHistoryData() {
        baseMapper.delete(new EntityWrapper<ExpTaxReportTempDomain>().le("created_date", ZonedDateTime.now()));
    }

    /**
     * 导入数据校验
     *
     * @param importDataList
     * @param batchNumber
     */
    public void checkImportData(List<ExpTaxReportTempDomain> importDataList, UUID batchNumber) {
        //初始化数据
        importDataList.stream().forEach(item -> {
            item.setBatchNumber(batchNumber.toString());
            item.setErrorFlag(false);
            item.setErrorDetail("错误详情:");
        });
        //非空校验
        importDataList.stream().filter(item -> StringUtil.isNullOrEmpty(item.getRowNumber())
                || StringUtil.isNullOrEmpty(item.getCompanyCode())
                || StringUtil.isNullOrEmpty(item.getTaxCategoryName())
                || StringUtil.isNullOrEmpty(item.getRequestPeriod())
                || StringUtil.isNullOrEmpty(item.getRequestAmount())
                || StringUtil.isNullOrEmpty(item.getBusinessSubcategoryName())
                || StringUtil.isNullOrEmpty(item.getBusinessSubcategoryCode())
        )
                .forEach(item -> {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + "必输字段不能为空;");
                });
        //有效性校验
        importDataList.stream().forEach(item -> {
            //公司合法校验
            String companyCode = item.getCompanyCode();
            if(StringUtils.isNotEmpty(companyCode)){
                CompanyCO companyCO = companyService.getByCompanyCode(companyCode);
                if(companyCO != null){
                    Long companyId = companyCO.getId();
                    item.setCompanyId(companyId);
                    //根据导入数据的OU+期间+税种查询系统中是否已经存在“已勾兑”状态以及金额差值是否相等的相同数据
                    String taxCategoryName = item.getTaxCategoryName();
                    String requestPeroid = item.getRequestPeriod();
                    String requestAmountStr = item.getRequestAmount();
                    if (StringUtils.isNotEmpty(taxCategoryName) && StringUtils.isNotEmpty(requestPeroid)) {
                        Wrapper wrapper = new EntityWrapper<ExpTaxReport>()
                                .eq(companyId != null, "company_id", companyId)
                                .eq(taxCategoryName != null, "tax_category_name", taxCategoryName)
                                .eq(requestPeroid != null, "request_period", requestPeroid)
                                .eq("blend_status", 1);
                        List<ExpTaxReport> taxReportList = expTaxReportMapper.selectList(wrapper);
                        BigDecimal sumAmount = BigDecimal.ZERO;
                        if (taxReportList.size() > 0) {
                            for (ExpTaxReport taxReport : taxReportList) {
                                sumAmount = sumAmount.add(taxReport.getRequestAmount());
                            }
                            BigDecimal requestAmount = new BigDecimal(requestAmountStr);
                            BigDecimal value = requestAmount.subtract(sumAmount);
                            if (Math.abs(value.doubleValue()) == 0) {
                                item.setErrorFlag(true);
                                item.setErrorDetail(item.getErrorDetail() +item.getCompanyCode()+"+"+item.getTaxCategoryName()+"+"+item.getRequestPeriod()+"存在已勾兑申报数据;");
                            }
                        }
                    }
                }else {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() +item.getCompanyCode()+":"+ "此OU对应公司不存在;");
                }
            }
            //税种的合法性--待校验

            //申报金额必须大于零
            if(StringUtils.isNotEmpty(item.getRequestAmount())){
                if (Double.valueOf(item.getRequestAmount())<=0) {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() +item.getRequestAmount()+":"+"申报金额必须大于零;");
                }
            }
        });
    }


    /**
     * 验证该批次号数据是否存在
     * @param transactionID
     * @return
     */
    public Boolean varifyBatchNumberExsits(String transactionID) {
        boolean flag = false;
        List<ExpTaxReportTempDomain> expTaxReportTempDomainList = selectList(
                new EntityWrapper<ExpTaxReportTempDomain>()
                        .eq("batch_number", transactionID));
        if(expTaxReportTempDomainList.size()>0){
            flag =true;
        }
        return flag;
    }

    /**
     * 根据批次号查询结果
     * @param transactionID
     * @return
     */
    public ImportResultDTO queryResultInfo(String transactionID) {
        return baseMapper.queryInfo(transactionID);
    }


    //导入失败后,导出错误信息
    public byte[] exportFailedData(String transactionID) {
        List<ExpTaxReportTempDomain> expTaxReportTempDomainList = selectList(
                new EntityWrapper<ExpTaxReportTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 1));
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;
        try {
            in = StreamUtil.getResourceStream(TaxReimburseConstans.TAX_REPORT_IMPORT_ERROR_TEMPLATE_PATH);
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = TaxReimburseConstans.EXCEL_BASEROW_ERROR;
            Row row = null;
            Cell cell = null;
            for (ExpTaxReportTempDomain expTaxReportTempDomain : expTaxReportTempDomainList) {
                row = sheet.createRow(startRow++);
                //行号
                cell = row.createCell(TaxReimburseConstans.ROW_NUMBER_ERROR);
                cell.setCellValue(expTaxReportTempDomain.getRowNumber());
                //其他字段
                cell = row.createCell(TaxReimburseConstans.COMPANY_CODE_ERROR);
                cell.setCellValue(expTaxReportTempDomain.getCompanyCode());

                cell = row.createCell(TaxReimburseConstans.TAX_CATEGORY_NAME_ERROR);
                cell.setCellValue(expTaxReportTempDomain.getTaxCategoryName());

                cell = row.createCell(TaxReimburseConstans.REQUST_PEROID_ERROR);
                cell.setCellValue(expTaxReportTempDomain.getRequestPeriod());

                cell = row.createCell(TaxReimburseConstans.REQUEST_AMOUNT);
                cell.setCellValue(expTaxReportTempDomain.getRequestAmount());

                cell = row.createCell(TaxReimburseConstans.BUSINESS_SUBCATEGORY_NAME_ERROR);
                cell.setCellValue(expTaxReportTempDomain.getBusinessSubcategoryName());

                cell = row.createCell(TaxReimburseConstans.BUSINESS_SUBCATEGORY_CODE_ERROR);
                cell.setCellValue(expTaxReportTempDomain.getBusinessSubcategoryCode());

                cell = row.createCell(row.getLastCellNum());
                cell.setCellValue(expTaxReportTempDomain.getErrorDetail());
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
        return baseMapper.delete(new EntityWrapper<ExpTaxReportTempDomain>().eq("batch_number", transactionID));
    }

    /**
     * 根据批次号查询税金申报信息临时表中的数据
     *
     * @param transactionID
     * @param page
     * @return
     */
    public List<ExpTaxReportTempDomain> listImportMessageByTransactionID(String transactionID, Page page) {
        return baseMapper.selectPage(page,
                new EntityWrapper<ExpTaxReportTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", false));
    }


}
