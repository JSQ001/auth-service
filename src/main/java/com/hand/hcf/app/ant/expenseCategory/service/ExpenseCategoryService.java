package com.hand.hcf.app.ant.expenseCategory.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.ant.expenseCategory.dto.ExpenseCategory;
import com.hand.hcf.app.ant.expenseCategory.mapper.ExpenseCategoryMapper;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseCategoryService extends BaseService<ExpenseCategoryMapper,ExpenseCategory> {

    @Autowired
    private ExpenseCategoryMapper expenseCategoryMapper;

    public List<ExpenseCategory> queryPages(Page page){
        return  expenseCategoryMapper.selectPage(page,new EntityWrapper<ExpenseCategory>()
                .eq("setOfBooksId",1)
        );
    }

}
