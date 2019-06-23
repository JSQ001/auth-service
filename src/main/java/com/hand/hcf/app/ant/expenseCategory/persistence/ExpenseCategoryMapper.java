package com.hand.hcf.app.ant.expenseCategory.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.expenseCategory.dto.ExpenseCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface ExpenseCategoryMapper extends BaseMapper<ExpenseCategory> {
}
