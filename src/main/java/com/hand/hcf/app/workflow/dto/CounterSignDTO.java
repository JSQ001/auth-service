package com.hand.hcf.app.workflow.dto;

import com.hand.hcf.app.workflow.enums.ApprovalOrderEnum;
import com.hand.hcf.app.workflow.enums.CounterSignOrderEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@ApiModel(description = "审批加签请求对象")
public class CounterSignDTO {

    @ApiModelProperty(value = "单据OID")
    private String entityOid;
    @ApiModelProperty(value = "单据大类")
    private Integer entityType;

    @ApiModelProperty(value = "加签人员OID列表")
    private List<UUID> userOids;

    @ApiModelProperty(value = "加签顺序")
    private CounterSignOrderEnum counterSignOrder;

    @ApiModelProperty(value="审批顺序")
    private ApprovalOrderEnum approvalOrder;

    @ApiModelProperty(value = "备注")
    private String remark;

}
