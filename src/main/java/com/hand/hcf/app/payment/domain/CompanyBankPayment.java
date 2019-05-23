package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;
import io.swagger.annotations.*;


/**
 * Created by 刘亮 on 2017/9/28.
 */
@ApiModel(description = "公司银行付款方式实体类")
@Data
@TableName("csh_company_bank_payment")
public class CompanyBankPayment  extends DomainLogicEnable {
    private static final long serialVersionUID = -5081085953519624065L;

    @ApiModelProperty(value = "公司银行账户id")
    @TableField("bank_account_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bankAccountId;

    @ApiModelProperty(value = "支付方式id")
    @TableField("payment_method_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentMethodId;

    @ApiModelProperty(value = "付款方式类型（大类）")
    @TableField("payment_method_category")
    private String paymentMethodCategory;

    @ApiModelProperty(value = "付款方式code")
    @TableField("payment_method_code")
    private String paymentMethodCode;
}
