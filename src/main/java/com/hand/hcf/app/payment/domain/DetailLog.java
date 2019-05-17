package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Created by 刘亮 on 2018/4/4.
 */
@ApiModel(description = "支付明细反冲日志表实体类")
@TableName("csh_detail_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailLog {

    @ApiModelProperty(value = "主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "操作意见")
    @TableField("operation_message")
    private String operationMessage;

    @ApiModelProperty(value = "操作时间")
    @TableField("operation_time")
    private ZonedDateTime operationTime;

    @ApiModelProperty(value = "操作类型")
    @TableField("operation_type")
    private Integer operationType;

    @ApiModelProperty(value = "操作用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "支付明细id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("detail_id")
    private Long detailId;


}
