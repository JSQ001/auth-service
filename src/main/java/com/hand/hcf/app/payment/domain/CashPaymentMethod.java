package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * Created by 刘亮 on 2017/9/6.
 */
@Data
@TableName("csh_payment_method")
public class CashPaymentMethod extends DomainLogicEnable {

    @TableField("payment_method_code")
    private String paymentMethodCode;
    @TableField("tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonIgnore
    private Long tenantId;
    //付款方式类型只能为确定的3个值----SpecificationUtil里面
    @TableField("payment_method_category")
    private String paymentMethodCategory;
    @TableField("description")
    private String description;
    @TableField(exist = false)
    private String paymentMethodCategoryName;
//    @TableField("ebanking_flag")
//    private boolean ebankingFlag;//线上支付标志

    @TableField(value = "create_type")
    private String createType;
}
