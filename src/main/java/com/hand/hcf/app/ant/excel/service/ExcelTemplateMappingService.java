package com.hand.hcf.app.ant.excel.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplateMapping;
import com.hand.hcf.app.ant.excel.persistence.ExcelTemplateMappingMapper;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/14
 */
@Service
public class ExcelTemplateMappingService extends BaseService<ExcelTemplateMappingMapper, ExcelTemplateMapping> {
    @Autowired
    private ExpenseTypeService expenseTypeService;
    @Autowired
    private ExcelTemplateMappingMapper excelTemplateMappingMapper;
    @Autowired
    private OrganizationService organizationService;

    public List<ExcelTemplateMapping> pageExcelTemplateMappingByCond(Long expenseTypeId,String expenseAttribute, Page page) {

        List<ExcelTemplateMapping> list = excelTemplateMappingMapper.selectPage(page,
                new EntityWrapper<ExcelTemplateMapping>()
                        .eq(expenseTypeId != null, "expense_type_id", expenseTypeId)
                        .like(expenseAttribute != null, "expense_attribute", expenseAttribute, SqlLike.DEFAULT)
                        .orderBy("created_date")
        );
        list.stream().forEach(s -> {
            ExpenseType expenseType = expenseTypeService.selectById(s.getExpenseTypeId());
            if(s.getExpenseAttribute()!=null){
                SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("EXPENSE_ATTRIBUTE", s.getExpenseAttribute());
                if(sysCodeValueCO != null) {
                    s.setExpenseAttributeName(sysCodeValueCO.getName());
                }
            }
            if (expenseType != null) {
                s.setExpenseTypeName(expenseType.getName());
            }
        });
        return list;
    }

    /**
     * 新增Excel底稿映射模板
     *
     * @param excelTemplateMapping
     * @return
     */
    public ExcelTemplateMapping insertExcelTemplateMapping(ExcelTemplateMapping excelTemplateMapping) {
        ExpenseType expenseType = expenseTypeService.selectById(excelTemplateMapping.getExpenseTypeId());
        if (expenseType == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        insertOrUpdate(excelTemplateMapping);
        return excelTemplateMapping;
    }

    public void deleteExcelTemplateMapping(Long id) {
        if (null == id) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        deleteById(id);

    }

}
