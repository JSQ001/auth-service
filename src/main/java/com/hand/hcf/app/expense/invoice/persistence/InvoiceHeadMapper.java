package com.hand.hcf.app.expense.invoice.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
public interface InvoiceHeadMapper extends BaseMapper<InvoiceHead>{
    /**
     *  分页查询未关联的发票头
     * @param queryPage
     * @param wrapper
     * @return
     */
    List<InvoiceHead> pageInvoiceByCond(Page queryPage,
                                        @Param("ew") Wrapper<InvoiceHead> wrapper);
}
