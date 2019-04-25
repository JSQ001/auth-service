package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.payment.domain.PaymentRequisitionTypes;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypesToRelated;
import com.hand.hcf.app.payment.domain.PaymentRequisitionTypesToUsers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description: 付款申请单类型定义DTO保存用
 * @Date: Created in 15:29 2018/1/22
 * @Modified by
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PaymentRequisitionTypesAllDTO {

    private PaymentRequisitionTypes paymentRequisitionTypes; //付款申请单类型

    private List<PaymentRequisitionTypesToRelated> paymentRequisitionTypesToRelateds; //付款申请单关联报账单

    private  List<PaymentRequisitionTypesToUsers> paymentRequisitionTypesToUsers;//付款申请单分配员工


}
