package com.hand.hcf.app.ant.excel.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.excel.domain.ExcelTemplate;
import com.hand.hcf.app.ant.excel.persistence.ExcelTemplateMapper;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.service.ExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/6
 */
@Service
public class ExcelTemplateService extends BaseService<ExcelTemplateMapper, ExcelTemplate> {
    @Autowired
    ExcelTemplateMapper excelTemplateMapper;

    @Autowired
    private ExpenseTypeService expenseTypeService;

    @Autowired
    private OrganizationService organizationService;

    /**
     * 新增Excel导入模板
     *
     * @param excelTemplate
     * @return
     */
    @Transactional
    public ExcelTemplate insertExcelTemplate(ExcelTemplate excelTemplate) {
       //判断费用类型
        ExpenseType expenseType = expenseTypeService.selectById(excelTemplate.getExpenseTypeId());
        if (expenseType == null) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        this.insert(excelTemplate);
        return excelTemplate;
    }

    /**
     *  查询Excel模板
     * @param expenseTypeId
     * @param expenseAttribute
     * @param page
     * @return
     */
    public List<ExcelTemplate> pageExcelTemplateByCond(Long expenseTypeId, String expenseAttribute, Page page) {

        List<ExcelTemplate> list = excelTemplateMapper.selectPage(page,
                new EntityWrapper<ExcelTemplate>()
                        .eq(expenseTypeId != null, "expense_type_id", expenseTypeId)
                        .like(expenseAttribute != null, "expense_attribute", expenseAttribute, SqlLike.DEFAULT)
                .orderBy("created_date")
        );
        list.stream().forEach(s->{
            ExpenseType expenseType = expenseTypeService.selectById(s.getExpenseTypeId());
            if(s.getExpenseAttribute()!=null){
                SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("EXPENSE_ATTRIBUTE", s.getExpenseAttribute());
                if(sysCodeValueCO != null) {
                    s.setExpenseAttributeName(sysCodeValueCO.getName());
                }
            }
            if(expenseType != null){
                s.setExpenseTypeName(expenseType.getName());
            }
        });
        return list;
    }


}
