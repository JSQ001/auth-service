package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.payment.domain.PaymentDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: bin.xie
 * @Description: 支付往核算模块发送支付成功数据
 * @Date: Created in 9:47 2018/3/5
 * @Modified by
 */
@Data
public class PaymentDetailDto implements Serializable{

    private Long createdBy;
    private List<PaymentDetail> paymentDetails;
}
