package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Created by 韩雪 on 2017/9/30.
 */
@Data
@TableName("csh_transaction_log")
public class CashTransactionLog {
    @TableId
    @JsonSerialize(using=ToStringSerializer.class)
    private Long id;//通用支付平台日志表ID

    @JsonSerialize(using=ToStringSerializer.class)
    @TableField(value = "payment_detail_id")
    private Long paymentDetailId;//支付明细表ID

    @JsonSerialize(using=ToStringSerializer.class)
    @TableField(value = "user_id")
    private Long userId;//操作用户ID

    @TableField(value = "operation_type")
    private String operationType;//操作类型

    @TableField(value = "operation_time")
    private ZonedDateTime operationTime;//操作时间

    //备注
    @TableField(value = "remark")
    private String remark;

    //操作用户姓名
    @TableField(exist = false)
    private String userName;

    //操作类型名称
    @TableField(exist = false)
    private String operationTypeName;

    //银行报文
    @TableField("bank_message")
    private byte[] bankMessage;
}
