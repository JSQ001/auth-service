package com.hand.hcf.app.ant.excel.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.persistence.ExpenseTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/5
 */
@Service
public class ExpenseTypeExcelService extends BaseService<ExpenseTypeMapper, ExpenseType> {
    @Autowired
    ExpenseTypeMapper expenseTypeMapper;
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private OrganizationService organizationService;

    /**
     *  自定义条件查询 费用小类(分页)
     * @param setOfBooksId
     * @param code
     * @param name
     * @param enabled
     * @param page
     * @return
     */
    public List<ExpenseType> getExpenseTypeByCond(Long setOfBooksId, String code, String name, Boolean enabled, Page page) {
        List<ExpenseType> list = new ArrayList<>();
        if (setOfBooksId == null) {
            return list;
        }
        list = expenseTypeMapper.selectPage(page,
                new EntityWrapper<ExpenseType>()
                        .where("deleted = false")
                        .eq("set_of_books_id", setOfBooksId)
                        .like(code != null, "code", code, SqlLike.DEFAULT)
                        .like(name != null, "name", name, SqlLike.DEFAULT)
                        .eq(enabled != null, "enabled", enabled)
                        .orderBy("enabled", false)
                        .orderBy("code")
        );
        return list;
    }

}
