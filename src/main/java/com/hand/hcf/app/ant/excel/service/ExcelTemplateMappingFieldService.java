package com.hand.hcf.app.ant.excel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplate;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateField;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateFixedField;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateMappingField;
import com.hand.hcf.app.ant.excel.dto.ExcelTemplateMappingDTO;
import com.hand.hcf.app.ant.excel.persistence.ExcelTemplateFieldMapper;
import com.hand.hcf.app.ant.excel.persistence.ExcelTemplateFixedFieldMapper;
import com.hand.hcf.app.ant.excel.persistence.ExcelTemplateMapper;
import com.hand.hcf.app.ant.excel.persistence.ExcelTemplateMappingFieldMapper;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.type.domain.ExpenseTypeExpandField;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeExpandFieldMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/10
 */
@Service
public class ExcelTemplateMappingFieldService extends BaseService<ExcelTemplateMappingFieldMapper, ExcelTemplateMappingField> {
    @Autowired
    ExcelTemplateMappingFieldMapper excelTemplateMappingFieldMapper;
    @Autowired
    ExcelTemplateFixedFieldMapper excelTemplateFixedFieldMapper;
    @Autowired
    ExpenseTypeExpandFieldMapper expenseTypeExpandFieldMapper;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExcelTemplateMapper excelTemplateMapper;
    @Autowired
    private ExcelTemplateFieldMapper excelTemplateFieldMapper;
    @Autowired
    private ExcelTemplateFieldService excelTemplateFieldService;


    public List<ExcelTemplateMappingField> queryExcelTemplateMappingField(Long mappingTemplateId) {

        Wrapper wrapper = new EntityWrapper<ExcelTemplateMappingField>()
                .eq("mapping_template_id", mappingTemplateId);

        List<ExcelTemplateMappingField> excelTemplateMappingFields = excelTemplateMappingFieldMapper.selectList(wrapper);
        excelTemplateMappingFields.stream().forEach(s -> {
            Wrapper wrapper1 = new EntityWrapper<ExcelTemplateField>()
                    .eq("field_code", s.getValueField());
            ExcelTemplateField excelTemplateField = excelTemplateFieldService.selectOne(wrapper1);
            if (excelTemplateField != null) {
                s.setValueFieldName(excelTemplateField.getFieldName());
            }
            if (s.getValueSource() != null) {
                SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("VALUE_LOGIC", s.getValueSource());
                if (sysCodeValueCO != null) {
                    s.setValueSourceName(sysCodeValueCO.getName());
                }
            }
        });
        return excelTemplateMappingFields;
    }

    public ExcelTemplateMappingField saveExcelTemplateInfo(ExcelTemplateMappingField excelTemplateMappingField, Long id) {

        if (null == id) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        excelTemplateMappingField.setMappingTemplateId(id);
        insertOrUpdate(excelTemplateMappingField);
        return excelTemplateMappingField;
    }

    public void deleteExcelTemplateMappingField(Long id) {
        if (null == id) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        deleteById(id);

    }

    public List<ExcelTemplateMappingDTO> getMappingFieldByCond(Long expenseTypeId, Page page) {
        List<ExcelTemplateMappingDTO> list = new ArrayList<>();

        List<ExcelTemplateFixedField> list1 = excelTemplateFixedFieldMapper.selectPage(page,
                new EntityWrapper<ExcelTemplateFixedField>());

        List<ExpenseTypeExpandField> list2 = expenseTypeExpandFieldMapper.selectPage(page,
                new EntityWrapper<ExpenseTypeExpandField>()
                        .eq(expenseTypeId != null, "expense_type_id", expenseTypeId));
        list1.stream().forEach(excelTemplateFixedField -> {
            ExcelTemplateMappingDTO excelTemplateMappingDTO = new ExcelTemplateMappingDTO();
            String columnField = excelTemplateFixedField.getColumnField();
            String columnName = excelTemplateFixedField.getColumnName();
            excelTemplateMappingDTO.setColumnCode(columnField);
            excelTemplateMappingDTO.setColumnName(columnName);
            list.add(excelTemplateMappingDTO);
        });
        list2.stream().forEach(expenseTypeExpandField -> {
            ExcelTemplateMappingDTO excelTemplateMappingDTO = new ExcelTemplateMappingDTO();
            String columnField = expenseTypeExpandField.getFieldCode();
            String columnName = expenseTypeExpandField.getFieldName();
            excelTemplateMappingDTO.setColumnCode(columnField);
            excelTemplateMappingDTO.setColumnName(columnName);
            list.add(excelTemplateMappingDTO);
        });

        return list;
    }

    public List<ExcelTemplateField> getTemplateFieldByCond(Long expenseTypeId, Page page) {

        List<ExcelTemplate> excelTemplates = excelTemplateMapper.selectPage(page,
                new EntityWrapper<ExcelTemplate>()
                        .eq(expenseTypeId != null, "expense_type_id", expenseTypeId));
        List<ExcelTemplateField> excelTemplateFieldList = new ArrayList<>();
        excelTemplates.stream().forEach(excelTemplate -> {
            List<ExcelTemplateField> excelTemplateFields = excelTemplateFieldMapper.selectPage(page,
                    new EntityWrapper<ExcelTemplateField>()
                            .eq("excel_template_id", excelTemplate.getId()));
            excelTemplateFields.stream().forEach(excelTemplateField -> {
                excelTemplateFieldList.add(excelTemplateField);
            });
        });
        return excelTemplateFieldList;

    }

}
