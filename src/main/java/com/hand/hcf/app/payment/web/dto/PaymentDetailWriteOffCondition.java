package com.hand.hcf.app.payment.web.dto;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2018/3/28 16:13
 */

@Data
public class PaymentDetailWriteOffCondition {
    private Boolean sameContract;//相同合同
    private Long contractHeaderId;//合同ID
    private Boolean sameApplicationForm;//相同申请
    private List<Long> applicationIdList;//申请单ID集合
    private Long tenantId;//租户ID
    private Long companyId;//公司ID
    private String partnerCategory;//收款对象
    private Long partnerId;//收款对象ID
    private Long documentHeaderId;    //单据头ID
    private String documentType;      //单据类型
    private Long documentLineId;      //单据计划付款行ID
    private String currencyCode;      //单据币种
}
