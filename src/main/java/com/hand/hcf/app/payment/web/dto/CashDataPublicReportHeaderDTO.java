package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @Author: bin.xie
 * @Description: 付款申请单关联通用待付(报账单)数据DTO
 * @Date: Created in 14:53 2018/4/25
 * @Modified by
 */
@Data
public class CashDataPublicReportHeaderDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long reportHeadId;//报账单头ID

    private String reportNumber;//报账单编号

    private String reportTypeName;//报账单类型
    @JsonSerialize(using = ToStringSerializer.class)
    private Long reportTypeId;// 报账单类型ID

    private List<CashDataPublicReportLineDTO> lineList;//报账单计划付款行

    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;//员工ID
    private String employeeName;//员工名称

    private ZonedDateTime requisitionDate; // 申请日期
}
