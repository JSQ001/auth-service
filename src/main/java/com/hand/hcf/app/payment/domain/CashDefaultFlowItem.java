package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * Created by 韩雪 on 2017/9/7.
 */
@Data
@TableName("csh_default_flowitem")
public class CashDefaultFlowItem extends DomainLogicEnable {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "transaction_class_id")
    private Long transactionClassId;//现金事务分类ID

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "cash_flow_item_id")
    private Long cashFlowItemId;//现金流量项ID

    @TableField(value = "default_flag")
    private Boolean defaultFlag;//默认现金流量项

    //现金事务分类code
    @TableField(exist = false)
    private String transactionClassCode;

    //现金事务分类name
    @TableField(exist = false)
    private String transactionClassName;

    //现金流量项code
    @TableField(exist = false)
    private String cashFlowItemCode;

    //现金流量项name
    @TableField(exist = false)
    private String cashFlowItemName;
}
