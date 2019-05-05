package com.hand.hcf.app.common.co;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description
 * @Version: 1.0
 * @author: LIg
 * @date: 2019/4/18 16:39
 */
@ApiModel("发票认证接口发送行信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceAuthenticationSendLineCO {
    /**
     * 发票认证接口发票信息 Dto，
     * invoiceCode  发票代码
     * invoiceNo 发票号码
     * applyTaxPeriod 申请认证税款所属期
     * buyerTaxNo 购方税号
     * applyRzlx 认证类型
     */
    private Long id;
    private String invoiceCode;
    private String invoiceNo;
    private String applyTaxPeriod;
    private String buyerTaxNo;
    private String applyRzlx;

}
