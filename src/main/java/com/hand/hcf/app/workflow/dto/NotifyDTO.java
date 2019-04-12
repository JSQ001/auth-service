package com.hand.hcf.app.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@ApiModel(description = "审批节点通知请求对象")
public class NotifyDTO {

    @ApiModelProperty(value = "单据OID")
    private String entityOid;
    @ApiModelProperty(value = "单据大类")
    private Integer entityType;

    @ApiModelProperty(value = "加签人员OID")
    private List<UUID> userOids;

    @ApiModelProperty(value = "备注")
    private String remark;

}
