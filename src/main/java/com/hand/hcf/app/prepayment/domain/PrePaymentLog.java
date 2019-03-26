package com.hand.hcf.app.prepayment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Created by 刘亮 on 2018/1/26.
 */
@TableName("pre_prepayment_log")
@Data
public class PrePaymentLog {
    @TableId
    @JsonSerialize(using=ToStringSerializer.class)
    private Long id;//日志表ID

    @JsonSerialize(using=ToStringSerializer.class)
    @TableField(value = "header_id")
    private Long headerId;//头ID

    @JsonSerialize(using=ToStringSerializer.class)
    @TableField(value = "user_id")
    private Long userId;//操作用户ID

    @TableField(value = "operation_type")
    private int operationType;//操作类型

    @TableField(value = "operation_time")
    private ZonedDateTime operationTime;//操作时间

    @TableField(value = "operation_message")
    private String operationMessage;//操作意见

}
