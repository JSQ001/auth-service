package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.payment.domain.PaymentRequisitionHeader;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 10:16 2018/1/24
 * @Modified by
 */
public interface PaymentRequisitionHeaderMapper extends BaseMapper<PaymentRequisitionHeader> {

    List<PaymentRequisitionHeader> listHeaders(@Param("ew") Wrapper<PaymentRequisitionHeader> wrapper);
    List<PaymentRequisitionHeader> listHeaders(Pagination page, @Param("ew") Wrapper<PaymentRequisitionHeader> wrapper);
    List<PaymentRequisitionHeader> listHeaders(RowBounds rowBounds, @Param("ew") Wrapper<PaymentRequisitionHeader> wrapper);
    PaymentRequisitionHeader getHeaderById(@Param("id") Long id);

    /**
     * getHeadersByCond : 根据付款申请单头表ID查询对应行表关联的报账单行表ID集合
     */
    List<Long> getReportLineIds(@Param("headId") Long headId);

    /**
     * 付款申请单财务查询
     */
    List<PaymentRequisitionHeader> queryHeaders(Pagination page, @Param("ew") Wrapper<PaymentRequisitionHeader> wrapper);
}
