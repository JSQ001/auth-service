package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Created by 刘亮 on 2018/1/26.
 */
@Data
public class PrePaymentLogDTO {
    @JsonSerialize(using=ToStringSerializer.class)
    private Long id;//日志表ID

    @JsonSerialize(using=ToStringSerializer.class)
    private Long headerId;//头ID

    @JsonSerialize(using=ToStringSerializer.class)
    private Long userId;//操作用户ID

    private String employeeName;
    private String employeeId;

    private Integer operation;//操作类型

    private Integer operationType;

    private String operationTypeName;//操作类型名称

    private ZonedDateTime lastUpdatedDate;//操作时间

    private String operationMessage;//操作意见
}
