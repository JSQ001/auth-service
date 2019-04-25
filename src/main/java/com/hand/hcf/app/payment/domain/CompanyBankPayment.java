package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;


/**
 * Created by 刘亮 on 2017/9/28.
 */
@Data
@TableName("csh_company_bank_payment")
public class CompanyBankPayment  extends DomainLogicEnable {
    private static final long serialVersionUID = -5081085953519624065L;
    @TableField("bank_account_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bankAccountId;
    @TableField("payment_method_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentMethodId;
    @TableField("payment_method_category")
    private String paymentMethodCategory;
    @TableField("payment_method_code")
    private String paymentMethodCode;
}
