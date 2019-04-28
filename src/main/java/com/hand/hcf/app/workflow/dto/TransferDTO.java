package com.hand.hcf.app.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@Data
@ApiModel(description = "审批转交请求对象")
public class TransferDTO {

    @ApiModelProperty(value = "单据OID")
    private UUID entityOid;
    @ApiModelProperty(value = "单据大类")
    private Integer entityType;


    @ApiModelProperty(value = "指定人员OID")
    private UUID userOid;

    @ApiModelProperty(value = "备注")
    private String remark;

}
