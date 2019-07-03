package com.hand.hcf.app.ant.excel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplate;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateField;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateMappingField;
import com.hand.hcf.app.ant.excel.domain.enums.ExcelTemplateImportCode;
import com.hand.hcf.app.ant.excel.domain.temp.ExcelTemplateTempDomain;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.handler.ExcelImportHandler;
import com.hand.hcf.app.core.service.ExcelImportService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import com.itextpdf.text.io.StreamUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/17
 */
@Service
public class ExcelDynamicImportService {

    @Autowired
    ExcelTemplateService excelTemplateService;
    @Autowired
    private ExcelTemplateFieldService excelTemplateFieldService;
    @Autowired
    private ExcelTemplateTempService excelTemplateTempService;
    @Autowired
    private ExcelImportService excelImportService;
    @Autowired
    private ExcelTemplateMappingFieldService excelTemplateMappingFieldService;
    @Autowired
    private ExpenseTypeService expenseTypeService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public List<ExcelTemplateField> exportExcelTemplateDetail(String typeId) {
        List<ExcelTemplateField> excelTemplateFields = new ArrayList<>();
        if (StringUtils.isNotEmpty(typeId)) {
            Long expenseTypeId = Long.valueOf(typeId);
            Wrapper<ExcelTemplate> wrapper = new EntityWrapper<ExcelTemplate>()
                    .eq(expenseTypeId != null, "expense_type_id", expenseTypeId);
            List<ExcelTemplate> excelTemplates = excelTemplateService.selectList(wrapper);
            Long excelTemplateId = null;
            if (excelTemplates.size() > 0) {
                excelTemplateId = excelTemplates.get(0).getId();
            }
            Wrapper<ExcelTemplateField> wrapper1 = new EntityWrapper<ExcelTemplateField>()
                    .eq(excelTemplateId != null, "excel_template_id", excelTemplateId);
            excelTemplateFields = excelTemplateFieldService.selectList(wrapper1);

        }
        return excelTemplateFields;
    }

    public byte[] exportTemp(String expenseTypeId) {

        List<ExcelTemplateField> excelTemplateFields = exportExcelTemplateDetail(expenseTypeId);
        logger.info("import excel template");
        ByteArrayOutputStream bos = null;
        InputStream inputStream = null;
        try {
            inputStream = StreamUtil.getResourceStream(ExcelTemplateImportCode.IMPORT_TEMPLATE_PATH);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            CellStyle css = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            css.setDataFormat(format.getFormat("@"));
           /* XSSFRow row1 = sheet.createRow(0);
            XSSFRow row2 = sheet.createRow(1);*/
            XSSFRow titleRow1 = sheet.getRow(ExcelTemplateImportCode.EXCEL_BASEROW - 2);
            XSSFRow titleRow2 = sheet.getRow(ExcelTemplateImportCode.EXCEL_BASEROW - 1);
            if (CollectionUtils.isNotEmpty(excelTemplateFields)) {
                excelTemplateFields.stream().sorted(Comparator.comparing(ExcelTemplateField::getFieldCode)).forEach(u -> {

                   /* XSSFCell cell1 = row1.createCell(row1.getLastCellNum() == -1 ? 0 : row1.getLastCellNum(), CellType.STRING);
                    XSSFCell cell2 = row2.createCell(row2.getLastCellNum() == -1 ? 0 : row2.getLastCellNum(), CellType.STRING);*/
                    XSSFCell cell1 = titleRow1.createCell(titleRow1.getLastCellNum(), CellType.STRING);
                    XSSFCell cell2 = titleRow2.createCell(titleRow2.getLastCellNum(), CellType.STRING);
                    cell1.setCellValue(u.getFieldCode());
                    cell2.setCellValue(u.getFieldName());
                    sheet.setDefaultColumnStyle(cell1.getColumnIndex(), css);
                    sheet.setDefaultColumnStyle(cell2.getColumnIndex(), css);
                });
            }
            bos = new ByteArrayOutputStream();
            workbook.write(bos);
            bos.flush();
            workbook.close();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new ValidationException(new ValidationError("file", "read file failed"));
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (IOException e) {
                throw new ValidationException(new ValidationError("file", "read file failed"));
            }
        }
    }

    @Transactional
    public UUID importexcelTemp(InputStream in, Long headId) throws Exception {
        UUID batchNumber = UUID.randomUUID();
        ExcelImportHandler<ExcelTemplateTempDomain> excelImportHandler = new ExcelImportHandler<ExcelTemplateTempDomain>() {
            @Override
            public void clearHistoryData() {
                excelTemplateTempService.deleteHistoryData();
            }

            @Override
            public Class getEntityClass() {
                return ExcelTemplateTempDomain.class;
            }

            @Override
            public List<ExcelTemplateTempDomain> persistence(List<ExcelTemplateTempDomain> list) {
                // 导入数据
                excelTemplateTempService.insertBatch(list);
                // 重复数据校验,此处原表除ID外无唯一键,因此无需校验
                return list;
            }

            @Override
            public void check(List<ExcelTemplateTempDomain> list) {
                checkImportData(list, headId, batchNumber.toString());
            }
        };
        excelImportService.importExcel(in, false, 2, excelImportHandler);
        //临时表数据处理
        dataHandle(batchNumber.toString(), headId);
        return batchNumber;

    }

    /**
     * 导入数据校验
     *
     * @param importData  导入数据
     * @param batchNumber 批次号
     * @param headId
     */
    public void checkImportData(List<ExcelTemplateTempDomain> importData, Long headId, String batchNumber) {
        //初始化数据
        importData.stream().forEach(e -> {
            e.setBatchNumber(batchNumber.toString());
            e.setErrorFlag(false);
            e.setErrorDetail("");
        });
        //非空校验
        /*importData.stream().filter(e -> StringUtil.isNullOrEmpty(e.getRowNumber())
                || StringUtil.isNullOrEmpty(e.getEnabledStr())
                || StringUtil.isNullOrEmpty(e.getFieldCode())
                || StringUtil.isNullOrEmpty(e.getFieldName()))
                .forEach(e -> {
                    e.setErrorFlag(true);
                    e.setErrorDetail("必输字段不能为空");
                });*/
    }

    public void dataHandle(String batchNumber, Long expenseTypeId) throws InvocationTargetException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException {
        //临时表中数据处理
        List<ExcelTemplateTempDomain> excelTemplateTempDomains = excelTemplateTempService.selectList(
                new EntityWrapper<ExcelTemplateTempDomain>()
                        .eq("batch_number", batchNumber)
                        .eq("error_flag", 0));
        //底稿规则
        ExcelTemplateMappingField excelTemplateMappingFields = excelTemplateMappingFieldService.selectOne(
                new EntityWrapper<ExcelTemplateMappingField>()
                        .eq("expense_type_id", "1131836011387617282")
                        .eq("enable_flag", 1)
        );
        for (ExcelTemplateTempDomain importDTO : excelTemplateTempDomains) {
            //业务小类
            if (excelTemplateMappingFields.getFieldCode().equals("expense_type_id")) {
                if (excelTemplateMappingFields.getValueSource().equals("002")) {
                    //查询模板中对应的字段名
                    String fieldName = excelTemplateMappingFields.getValueField();
                    // String fieldNumber = fieldName.substring(7);
                    // 将属性的首字母大写
                    fieldName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1)
                            .toUpperCase());
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    Method m = importDTO.getClass().getMethod("get" + fieldName);
                    // 调用getter方法获取属性值
                    String value = (String) m.invoke(importDTO);
                    //判断填写的是code还是name
                    ExpenseType expenseType = expenseTypeService.selectOne(new EntityWrapper<ExpenseType>()
                            .eq("name", value));
                    if (expenseType == null) {
                        expenseType = expenseTypeService.selectOne(new EntityWrapper<ExpenseType>()
                                .eq("code", value));
                        if (expenseType == null) {
                            importDTO.setErrorFlag(true);
                            importDTO.setErrorDetail("业务小类填写有误!");
                        } else {
                            importDTO.setExpenseTypeId(expenseType.getId());
                        }
                    } else {
                        importDTO.setExpenseTypeId(expenseType.getId());
                    }
                    excelTemplateTempService.updateById(importDTO);
                }
            }

        }
    }

    public ImportResultDTO queryImportResultInfo(String transactionOid) {
        return excelTemplateTempService.queryImportResultInfo(transactionOid);
    }

}
