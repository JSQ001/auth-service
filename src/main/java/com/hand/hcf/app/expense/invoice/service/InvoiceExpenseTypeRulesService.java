package com.hand.hcf.app.expense.invoice.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.SetOfBooksInfoCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.handler.ExcelImportHandler;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.ExcelImportService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.StringUtil;
import com.hand.hcf.app.expense.invoice.domain.InvoiceExpenseTypeRules;
import com.hand.hcf.app.expense.invoice.domain.enums.InvoiceExpenseTypeRulesImportCode;
import com.hand.hcf.app.expense.invoice.domain.temp.InvoiceExpenseTypeRulesTemp;
import com.hand.hcf.app.expense.invoice.persistence.InvoiceExpenseTypeRulesMapper;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.itextpdf.text.io.StreamUtil;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author sq.l
 * @date 2019/04/22
 */
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class InvoiceExpenseTypeRulesService extends BaseService<InvoiceExpenseTypeRulesMapper, InvoiceExpenseTypeRules> {
    @Autowired
    private final InvoiceExpenseTypeRulesMapper invoiceExpenseTypeRulesMapper;

    @Autowired
    private final ExpenseTypeService ExpenseTypeService;

    @Autowired
    private final OrganizationService OrganizationService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private InvoiceExpenseTypeRulesTempService InvoiceExpenseTypeRulesTempService;

    @Autowired
    private ExcelImportService excelImportService;

    /**
     * 新建 发票费用映射规则表
     * @author sq.l
     * @date 2019/04/22
     *
     * @param invoiceExpenseTypeRules
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InvoiceExpenseTypeRules insertInvoiceExpenseRules(InvoiceExpenseTypeRules invoiceExpenseTypeRules) {
        Long tenantId = OrgInformationUtil.getCurrentTenantId();
        invoiceExpenseTypeRules.setTenantId(tenantId);
        invoiceExpenseTypeRulesMapper.insert(invoiceExpenseTypeRules);
        return invoiceExpenseTypeRules;
    }


    /**
     * 新建发票费用映射规则表
     * @author sq.l
     * @date 2019/04/22
     *
     * @param setOfBooksId
     * @param goodsName
     * @param ExpenseTypeCode
     * @param ExpenseTypeName
     * @param enabled
     * @param startDate
     * @param endDate
     * @param myPage
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<InvoiceExpenseTypeRules> selectInvoiceExpenseRules(Page myPage,Long setOfBooksId, String goodsName,
                                                                   String ExpenseTypeCode, String ExpenseTypeName, Boolean enabled,
                                                                   String startDate, String endDate,boolean dataAuthFlag) {
        ZonedDateTime startDateTime = startDate != null ? TypeConversionUtils.getStartTimeForDayYYMMDD(startDate) : null;
        ZonedDateTime endDateTime = startDate != null ? TypeConversionUtils.getStartTimeForDayYYMMDD(endDate) : null;
        Long tenantId = OrgInformationUtil.getCurrentTenantId();

        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME,"invoice_expense_type_rules");
            map.put(DataAuthorityUtil.TABLE_ALIAS,"ietr");
            map.put(DataAuthorityUtil.SOB_COLUMN,"set_of_books_id");
            map.put(DataAuthorityUtil.EMPLOYEE_COLUMN,"tenant_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }

        List<InvoiceExpenseTypeRules> InvoiceExpenseTypeRules = invoiceExpenseTypeRulesMapper
                .selectInvoiceExpenseRules(tenantId, setOfBooksId, goodsName, ExpenseTypeCode, ExpenseTypeName, enabled, startDateTime, endDateTime,dataAuthLabel, myPage);

        InvoiceExpenseTypeRules.stream().forEach(invoiceExpenseTypeRules -> {
            //给“费用类型名称”赋值
            ExpenseType expenseTypeName = ExpenseTypeService.selectById(invoiceExpenseTypeRules.getExpenseTypeId());
            if (null != expenseTypeName) {
                invoiceExpenseTypeRules.setExpenseTypeName(expenseTypeName.getName());
            }
            //给费用类型代码赋值
            invoiceExpenseTypeRules.setExpenseTypeCode(expenseTypeName.getCode());
            //给账套名称赋值
            SetOfBooksInfoCO setOfBooksInfoCO = OrganizationService.getSetOfBooksById(invoiceExpenseTypeRules.getSetOfBooksId());
            invoiceExpenseTypeRules.setSetOfBooksName(setOfBooksInfoCO.getSetOfBooksName());
            //给账套代码赋值
            invoiceExpenseTypeRules.setSetOfBooksCode(setOfBooksInfoCO.getSetOfBooksCode());
        });

        return InvoiceExpenseTypeRules;
    }

    /**
     * 根据id更新数据
     * @author sq.l
     * @date 2019/04/22
     *
     * @param invoiceExpenseTypeRules
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public InvoiceExpenseTypeRules updateInvoiceExpebseById(InvoiceExpenseTypeRules invoiceExpenseTypeRules) {
        InvoiceExpenseTypeRules result = mapperFacade.map(invoiceExpenseTypeRules, InvoiceExpenseTypeRules.class);
        if (result.getId() == null) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }

        invoiceExpenseTypeRulesMapper.updateById(result);
        return null;
    }


    /**
     * 导入发票费用映射规则临时表
     * @author sq.l
     * @date 2019/04/22
     *
     * @param in
     * @param setOfBooksId 账套Id
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public UUID importExpenseMappingRules(InputStream in, Long setOfBooksId) throws Exception {
        UUID batchNumber = UUID.randomUUID();
        ExcelImportHandler<InvoiceExpenseTypeRulesTemp> excelImportHandler = new ExcelImportHandler<InvoiceExpenseTypeRulesTemp>() {
            @Override
            public void clearHistoryData() {
                InvoiceExpenseTypeRulesTempService.deleteHistoryData();
            }

            @Override
            public Class<InvoiceExpenseTypeRulesTemp> getEntityClass() {
                return InvoiceExpenseTypeRulesTemp.class;
            }

            @Override
            public List<InvoiceExpenseTypeRulesTemp> persistence(List<InvoiceExpenseTypeRulesTemp> list) {
                //导入数据
                for (InvoiceExpenseTypeRulesTemp item : list) {
                    if ("Y".equals(item.getEnabledStr())) {
                        item.setEnabled(true);
                    } else {
                        item.setEnabled(false);
                    }

                    if (StringUtils.isNotEmpty(item.getStartDate())) {
                        item.setStartDateTime(DateUtil.stringToZonedDateTime(item.getStartDate()));
                    }

                    if (StringUtils.isNotEmpty(item.getEndDate())) {
                        item.setEndDateTime(DateUtil.stringToZonedDateTime(item.getEndDate()));
                    }
                }

                InvoiceExpenseTypeRulesTempService.insertBatch(list);
                return list;
            }

            @Override
            public void check(List<InvoiceExpenseTypeRulesTemp> importData) {
                checkImportData(importData, batchNumber, setOfBooksId);
            }
        };

        excelImportService.importExcel(in, false, 2, excelImportHandler);
        return batchNumber;
    }

    /**
     * 导入数据校验
     * @author sq.l
     * @date 2019/04/22
     *
     * @param importData   导入数据
     * @param batchNumber  批次号
     * @param setOfBooksId 账套Id
     */
    public void checkImportData(List<InvoiceExpenseTypeRulesTemp> importData, UUID batchNumber, Long setOfBooksId) {
        //初始化数据
        importData.stream().forEach(e -> {
            e.setBatchNumber(batchNumber.toString());
            e.setErrorFlag(false);
            e.setErrorMsg("1");
            e.setSetOfBooksId(setOfBooksId);
        });

        //非空校验
        importData.stream().filter(e -> StringUtil.isNullOrEmpty(e.getRowNumber())
                || StringUtil.isNullOrEmpty(e.getEnabledStr())
                || StringUtil.isNullOrEmpty(e.getExpenseTypeCode())
                || StringUtil.isNullOrEmpty(e.getStartDate())
                || StringUtil.isNullOrEmpty(e.getDescription())
                || StringUtil.isNullOrEmpty(e.getGoodsName()))
                .forEach(e -> {
                    e.setErrorFlag(true);
                    e.setErrorMsg("必输字段不能为空");
                });
    }

    /**
     * 【发票费用映射规则-导入】查询导入结果
     * @author sq.l
     * @date 2019/04/22
     *
     * @param transactionOid
     * @return
     */
    public ImportResultDTO queryResultInfo(String transactionOid) {
        return InvoiceExpenseTypeRulesTempService.queryResultInfo(transactionOid);
    }

    /**
     * 【发票费用映射规则-导入】确认导入
     * @author sq.l
     * @date 2019/04/22
     *
     * @param transactionId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmImport(String transactionId) {
        return InvoiceExpenseTypeRulesTempService.confirmImport(transactionId);
    }

    /**
     * 【发票费用-导入】取消导入（删除临时表数据）
     * @author sq.l
     * @date 2019/04/22
     *
     * @param transactionId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteImportData(String transactionId) {
        return InvoiceExpenseTypeRulesTempService.delete(new EntityWrapper<InvoiceExpenseTypeRulesTemp>()
                .eq("batch_number", transactionId));
    }

    /**
     * 导出错误信息
     * @author sq.l
     * @date 2019/04/22
     *
     * @param transactionId 批次号
     * @return
     */
    public byte[] exportFailedData(String transactionId) {
        List<InvoiceExpenseTypeRulesTemp> customEnumerationItemTemps = InvoiceExpenseTypeRulesTempService.selectList(
                new EntityWrapper<InvoiceExpenseTypeRulesTemp>()
                        .eq("batch_number", transactionId)
                        .eq("error_flag", 1));
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        XSSFWorkbook workbook = null;

        try {
            in = StreamUtil.getResourceStream(InvoiceExpenseTypeRulesImportCode.ERROR_TEMPLATE_PATH);
            workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int startRow = InvoiceExpenseTypeRulesImportCode.EXCEL_BASEROW_ERROR;
            Row row = null;
            Cell cell = null;

            for (InvoiceExpenseTypeRulesTemp importDTO : customEnumerationItemTemps) {
                row = sheet.createRow(startRow++);
                cell = row.createCell(InvoiceExpenseTypeRulesImportCode.ROW_NUMBER);
                cell.setCellValue(importDTO.getRowNumber());
                cell = row.createCell(InvoiceExpenseTypeRulesImportCode.Responsibility_Center_Code);
                cell.setCellValue(importDTO.getExpenseTypeCode());
                cell = row.createCell(InvoiceExpenseTypeRulesImportCode.ENABLED);
                cell.setCellValue(importDTO.getEnabledStr());
                cell = row.createCell(InvoiceExpenseTypeRulesImportCode.ERROR_DETAIL);
                cell.setCellValue(importDTO.getErrorMsg());
            }

            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.flush();
            workbook.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new BizException(RespCode.READ_FILE_FAILED);
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (workbook != null) {
                    workbook.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new BizException(RespCode.READ_FILE_FAILED);
            }
        }
    }
}
