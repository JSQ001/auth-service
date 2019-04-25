package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.payment.domain.CashPaymentMethod;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by 刘亮 on 2017/9/6.
 */
@Mapper
public interface CashPaymentMethodMapper extends BaseMapper<CashPaymentMethod> {

    CashPaymentMethod getById(@Param("id") Long id, @Param("paymentMethod") String paymentMethod);

}
