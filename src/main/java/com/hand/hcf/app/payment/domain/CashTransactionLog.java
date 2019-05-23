package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Created by 韩雪 on 2017/9/30.
 */
@ApiModel(description = "通用支付平台日志实体类")
@Data
@TableName("csh_transaction_log")
public class CashTransactionLog {

    @ApiModelProperty(value = "通用支付平台日志表ID")
    @TableId
    @JsonSerialize(using=ToStringSerializer.class)
    private Long id;//通用支付平台日志表ID

    @ApiModelProperty(value = "支付明细表ID")
    @JsonSerialize(using=ToStringSerializer.class)
    @TableField(value = "payment_detail_id")
    private Long paymentDetailId;//支付明细表ID

    @ApiModelProperty(value = "操作用户ID")
    @JsonSerialize(using=ToStringSerializer.class)
    @TableField(value = "user_id")
    private Long userId;//操作用户ID

    @ApiModelProperty(value = "操作类型")
    @TableField(value = "operation_type")
    private String operationType;//操作类型

    @ApiModelProperty(value = "操作时间")
    @TableField(value = "operation_time")
    private ZonedDateTime operationTime;//操作时间

    //备注
    @ApiModelProperty(value = "备注")
    @TableField(value = "remark")
    private String remark;

    //操作用户姓名
    @ApiModelProperty(value = "操作用户姓名")
    @TableField(exist = false)
    private String userName;

    //操作类型名称
    @ApiModelProperty(value = "操作类型名称")
    @TableField(exist = false)
    private String operationTypeName;

    //银行报文
    @ApiModelProperty(value = "银行报文")
    @TableField("bank_message")
    private byte[] bankMessage;
}
