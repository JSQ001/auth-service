package com.hand.hcf.app.ant.taxreimburse.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxCalculation;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxCalculationTempDomain;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpenseTaxReimburseHead;
import com.hand.hcf.app.ant.taxreimburse.persistence.ExpTaxCalculationTempDomainMapper;
import com.hand.hcf.app.ant.taxreimburse.utils.TaxReimburseConstans;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.expense.common.utils.StringUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.currency.domain.CurrencyI18n;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyI18nMapper;
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
import java.util.stream.Collectors;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金计提临时类service
 * @date 2019/6/25 11:33
 */
@Service
public class ExpTaxCalculationTempDomainService extends BaseService<ExpTaxCalculationTempDomainMapper, ExpTaxCalculationTempDomain> {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CurrencyI18nMapper currencyI18nMapper;

    @Autowired
    private ExpTaxCalculationService expTaxCalculationService;

    @Autowired
    private ExpenseTaxReimburseHeadService expenseTaxReimburseHeadService;


    /**
     * 删除之前全部数据
     */
    @Transactional
    public void deleteHistoryData() {
        baseMapper.delete(new EntityWrapper<ExpTaxCalculationTempDomain>().le("created_date", ZonedDateTime.now()));
    }

    /**
     * 导入数据校验
     *
     * @param importDataList
     * @param batchNumber
     */
    public void checkImportData(List<ExpTaxCalculationTempDomain> importDataList, UUID batchNumber) {
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
                || StringUtil.isNullOrEmpty(item.getCurrencyCode())
                || StringUtil.isNullOrEmpty(item.getRequestAmount())
        )
                .forEach(item -> {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + "必输字段不能为空;");
                });
        //有效性校验
        importDataList.stream().forEach(item -> {
            //公司合法校验
            String companyCode = item.getCompanyCode();
            if (StringUtils.isNotEmpty(companyCode)) {
                CompanyCO companyCO = companyService.getByCompanyCode(companyCode);
                if (companyCO != null) {
                    Long companyId = companyCO.getId();
                    item.setCompanyId(companyId);
                } else {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + item.getCompanyCode() + ":" + "此OU对应公司不存在;");
                }
            }
            //税种的合法性--待校验

            //币种校验
            if (StringUtil.isNullOrEmpty(item.getCurrencyCode())) {
                CurrencyI18n currencyI18n = new CurrencyI18n();
                currencyI18n.setCurrencyCode(item.getCurrencyCode());
                String language = OrgInformationUtil.getCurrentLanguage();
                currencyI18n.setLanguage(language);
                CurrencyI18n currencyOne = currencyI18nMapper.selectOne(currencyI18n);
                if (null == currencyOne) {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + item.getCurrencyCode() + ":" + "此币种代码对应币种不存在;");
                }
            }

            //计提金额必须大于零
            if (StringUtils.isNotEmpty(item.getRequestAmount())) {
                if (Double.valueOf(item.getRequestAmount()) <= 0) {
                    item.setErrorFlag(true);
                    item.setErrorDetail(item.getErrorDetail() + item.getRequestAmount() + ":" + "计提金额必须大于零;");
                }
            }
        });
    }

    /**
     * 根据批次号查询结果
     *
     * @param transactionID
     * @return
     */
    public ImportResultDTO queryResultInfo(String transactionID) {
        return baseMapper.queryInfo(transactionID);
    }

    //根据批次号删除临时表中的数据
    @Transactional
    public Integer deleteImportData(String transactionID) {
        return baseMapper.delete(new EntityWrapper<ExpTaxCalculationTempDomain>().eq("batch_number", transactionID));
    }

    /**
     * 根据批次号查询税金申报信息临时表中的数据
     *
     * @param transactionID
     * @param page
     * @return
     */
    public List<ExpTaxCalculationTempDomain> listImportMessageByTransactionID(String transactionID, Page page) {
        return baseMapper.selectPage(page,
                new EntityWrapper<ExpTaxCalculationTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", false));
    }

    /**
     * 导入失败后,导出错误信息
     *
     * @param transactionID
     * @return
     */
    public byte[] exportFailedData(String transactionID) {
        List<ExpTaxCalculationTempDomain> expTaxCalculationTempDomainList = selectList(
                new EntityWrapper<ExpTaxCalculationTempDomain>()
                        .eq("batch_number", transactionID)
                        .eq("error_flag", 1));
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;
        try {
            in = StreamUtil.getResourceStream(TaxReimburseConstans.TAX_CALCULATION_IMPORT_ERROR_TEMPLATE_PATH);
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = TaxReimburseConstans.EXCEL_BASEROW_ERROR;
            Row row = null;
            Cell cell = null;
            for (ExpTaxCalculationTempDomain expTaxCalculationTempDomain : expTaxCalculationTempDomainList) {
                row = sheet.createRow(startRow++);
                //行号
                cell = row.createCell(TaxReimburseConstans.CALCULATION_ROW_NUMBER_ERROR);
                cell.setCellValue(expTaxCalculationTempDomain.getRowNumber());
                //其他字段
                cell = row.createCell(TaxReimburseConstans.CALCULATION_COMPANY_CODE_ERROR);
                cell.setCellValue(expTaxCalculationTempDomain.getCompanyCode());

                cell = row.createCell(TaxReimburseConstans.CALCULATION_TAX_CATEGORY_NAME_ERROR);
                cell.setCellValue(expTaxCalculationTempDomain.getTaxCategoryName());

                cell = row.createCell(TaxReimburseConstans.CALCULATION_CURRENCY_CODE_ERROR);
                cell.setCellValue(expTaxCalculationTempDomain.getCurrencyCode());

                cell = row.createCell(TaxReimburseConstans.CALCULATION_REQUEST_AMOUNT);
                cell.setCellValue(expTaxCalculationTempDomain.getRequestAmount());

                cell = row.createCell(TaxReimburseConstans.CALCULATION_BUSINESS_REMARK_ERROR);
                cell.setCellValue(expTaxCalculationTempDomain.getRemark());

                cell = row.createCell(row.getLastCellNum());
                cell.setCellValue(expTaxCalculationTempDomain.getErrorDetail());
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

    /**
     * 将全部通过校验没有报错的数据全部导入正式的税金计提明细表中
     *
     * @param transactionID
     * @param page
     * @return
     */
    public boolean taxCalaulationConfirmImport(String transactionID, Long headId, Page page) {
        ImportResultDTO importResultDTO = this.queryResultInfo(transactionID);
        boolean flag = false;
        if (page == null) {
            page = PageUtil.getPage(0, 30);
        }
        //当错误结果为零/空的时候才可正式导入到正式表中
        if (null != importResultDTO) {
            if (importResultDTO.getFailureEntities() == 0 && importResultDTO.getErrorData().size() == 0) {
                List<ExpTaxCalculationTempDomain> expTaxCalculationTempDomainList = this.listImportMessageByTransactionID(transactionID, page);
                List<ExpTaxCalculation> expTaxCalculationList = expTaxCalculationTempDomainList.stream().map(expTaxCalculationTempDomain -> {
                    ExpTaxCalculation expTaxCalculation = new ExpTaxCalculation();
                    //将公司code的公司名称映射到数据表中
                    if (StringUtils.isNotEmpty(expTaxCalculationTempDomain.getCompanyCode())) {
                        CompanyCO companyCO = companyService.getByCompanyCode(expTaxCalculationTempDomain.getCompanyCode());
                        Long companyId = null;
                        if (companyCO != null) {
                            companyId = companyCO.getId();
                        }
                        expTaxCalculation.setCompanyId(companyId);
                    }
                    //ToDo 转化税种code和name--暂时将name也加入到数据表中，先不存放code,待确定业务小类的取值
                    expTaxCalculation.setTaxCategoryName(expTaxCalculationTempDomain.getTaxCategoryName());

                    //币种--映射
                    if (StringUtils.isNotEmpty(expTaxCalculationTempDomain.getCurrencyCode())) {
                        expTaxCalculation.setCurrencyCode(expTaxCalculationTempDomain.getCurrencyCode());
                    }

                    //计提金额映射
                    if (StringUtils.isNotEmpty(expTaxCalculationTempDomain.getRequestAmount())) {
                        expTaxCalculation.setRequestAmount(new BigDecimal(expTaxCalculationTempDomain.getRequestAmount()));
                    }
                    //备注
                    if (StringUtils.isNotEmpty(expTaxCalculationTempDomain.getRemark())) {
                        expTaxCalculation.setRemark(expTaxCalculationTempDomain.getRemark());
                    }
                    //设置计提明细外键headId
                    expTaxCalculation.setExpReimburseHeaderId(headId);
                    return expTaxCalculation;
                }).collect(Collectors.toList());
                //将数据批量插入税金计提正式表中
                flag = expTaxCalculationService.insertBatch(expTaxCalculationList);

                //导入完毕后删除临时表的数据r
                if (flag) {
                    this.deleteImportData(transactionID);
                }

                //导入完成之后更新关联的报账单头表中的总金额
                BigDecimal sumAmount = BigDecimal.ZERO;
                for (ExpTaxCalculation expTaxCalculation : expTaxCalculationList) {
                    BigDecimal requestAmount = expTaxCalculation.getRequestAmount();
                    sumAmount = sumAmount.add(requestAmount);
                }
                ExpenseTaxReimburseHead expenseTaxReimburseHead = expenseTaxReimburseHeadService.selectById(headId);

                if (null != expenseTaxReimburseHead) {
                    BigDecimal calculationAmount = expenseTaxReimburseHead.getTotalAmount();
                    //更新金额
                    sumAmount = sumAmount.add(calculationAmount);
                    expenseTaxReimburseHead.setTotalAmount(sumAmount);
                    //ToDo 本币金额和原币金额需要根据汇率来计算
                    expenseTaxReimburseHead.setFunctionalAmount(sumAmount);
                }
                expenseTaxReimburseHeadService.updateById(expenseTaxReimburseHead);
            }
        }
        return flag;
    }

}
