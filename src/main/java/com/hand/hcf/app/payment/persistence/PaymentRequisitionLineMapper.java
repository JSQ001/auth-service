package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.payment.domain.PaymentRequisitionLine;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionNumberWebDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 15:32 2018/1/24
 * @Modified by
 */
public interface PaymentRequisitionLineMapper extends BaseMapper<PaymentRequisitionLine> {

     BigDecimal selectAcpRequisitionLineTotalAmount(@Param("headerId") Long headerId);

     List<PaymentRequisitionNumberWebDTO> countAmountByCurrency(@Param("headerId") Long headerId);
}
