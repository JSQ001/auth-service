package com.hand.hcf.app.payment.web.dto;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * 确认支付dto
 * Created by 刘亮 on 2017/10/9.
 */
@Data
public class CashPayDTO {
    private ZonedDateTime payDate;//付款日期
    private Long payCompanyBankId;//公司银行付款账户Id
    private String payCompanyBankName;//公司银行付款账户名称
    private String payCompanyBankNumber;//公司银行付款账户账号
    private Long paymentTypeId;//付款方式id
    private String paymentTypeCode;//付款方式代码
    private String paymentMethodCategory;//付款方式类型---3个系统值
    private String paymentDescription;//付款方式名称
    private String currency;   //币种
    private Double exchangeRate; //汇率
    private String remark;        //备注
    private String chequeNumber;//支票号

}
