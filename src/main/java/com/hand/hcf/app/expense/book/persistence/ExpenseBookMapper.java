package com.hand.hcf.app.expense.book.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @create 2019/2/21 14:15
 * @version: 1.0.0
 */
@Component
public interface ExpenseBookMapper extends BaseMapper<ExpenseBook> {
    /**
     * 根据我的账本获取发票
     * @return
     */
    List<InvoiceHead> getInvoiceByBookId(@Param("expenseBookId") Long expenseBookId);

    /**
     * 根据条件筛选账本信息
     * @param rowBounds
     * @param wrapper
     * @return
     */
    List<ExpenseBook> pageExpenseBookByCond(RowBounds rowBounds,
                                            @Param("ew") Wrapper wrapper);
}
