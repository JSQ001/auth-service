package com.hand.hcf.app.workflow.dto;

import com.hand.hcf.app.workflow.enums.BackTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@Data
@ApiModel(description = "审批退回请求对象")
public class SendBackDTO {

    @ApiModelProperty(value = "单据OID")
    private UUID entityOid;
    @ApiModelProperty(value = "单据大类")
    private Integer entityType;

    @ApiModelProperty(value = "退回节点OID")
    private UUID approvalNodeOid;

    @ApiModelProperty(value = "退回审批类型")
    private BackTypeEnum backTypeEnum;

    @ApiModelProperty(value="退回意见")
    private String approvalText;

}
