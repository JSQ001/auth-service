package com.hand.hcf.app.workflow.workflow.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkflowDocumentDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Integer  type;
    private String  code;
    private String  name;
    private String remark;
    private String createdTime;//申请日期
    private String currency;
    private BigDecimal amount;
    private Integer statusCode;
    private String statusName;
    private String rejecterName;
    private String nodeName;
}
