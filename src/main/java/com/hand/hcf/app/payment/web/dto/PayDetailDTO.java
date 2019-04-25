package com.hand.hcf.app.payment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 付款详情DTO
 * Created by 刘亮 on 2017/12/20.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayDetailDTO {
    //付款批次号
    private String customerBatchNo;

    //付款流水号
    private String billcode;

    //币种
    private String currency;

    //付款金额
    private BigDecimal payAmount;


    //付款方式
    private String paymentTypeName;

    //描述
    private String remark;

    /*付方信息：付方机构 & 付方户名 & 付款账号*/

    //付款方银行账号
    private String  draweeAccountNumber;

    //付款机构
    private String draweeCompanyName;

    // 付款方银行户名
    private String draweeAccountName;

    //出纳：
    private Long draweeId;
    private String draweeName;

    //账户信息：币种 & 汇率
    private Double exchangeRate;

    //收方类型：对公付款
    private String partnerCategory;
    private String partnerCategoryName;

    //收方信息：户名 & 收款账号
    //收款方银行账号
    private String payeeAccountNumber;

    // 收款方银行户名
    private String payeeAccountName;

}
