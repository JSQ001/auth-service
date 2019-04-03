package com.hand.hcf.app.demo.book.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.demo.book.domain.ExpenseBookDemo;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jiancheng.li@hand-china.com
 * @description
 * @create 2019/2/21 14:15
 * @version: 1.0.0
 */
@Component
public interface ExpenseBookMapperDemo extends BaseMapper<ExpenseBookDemo> {

}
