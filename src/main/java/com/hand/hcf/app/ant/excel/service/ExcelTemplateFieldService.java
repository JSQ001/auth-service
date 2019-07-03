package com.hand.hcf.app.ant.excel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateField;
import com.hand.hcf.app.ant.excel.persistence.ExcelTemplateFieldMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/10
 */
@Service
public class ExcelTemplateFieldService extends BaseService<ExcelTemplateFieldMapper, ExcelTemplateField> {

    @Autowired
    private ExcelTemplateFieldMapper excelTemplateMapperField;

    public List<ExcelTemplateField> queryExcelTemplateField(Long excelTemplateId) {

        Wrapper wrapper = new EntityWrapper<ExcelTemplateField>()
                .eq("excel_template_id", excelTemplateId);

        List<ExcelTemplateField> ExcelTemplateFields = excelTemplateMapperField.selectList(wrapper);
        return ExcelTemplateFields;
    }

    public ExcelTemplateField saveExcelTemplateInfo(ExcelTemplateField excelTemplateField, Long excelTemplateId) {

        if (null == excelTemplateId) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        excelTemplateField.setExcelTemplateId(excelTemplateId);
        insertOrUpdate(excelTemplateField);
        return excelTemplateField;
    }

}
