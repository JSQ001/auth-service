package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Created by 刘亮 on 2018/4/4.
 */
@TableName("csh_detail_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailLog {
    @JsonSerialize(using = ToStringSerializer.class)

    private Long id;

    @TableField("operation_message")
    private String operationMessage;

    @TableField("operation_time")
    private ZonedDateTime operationTime;

    @TableField("operation_type")
    private Integer operationType;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("user_id")
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("detail_id")
    private Long detailId;


}
