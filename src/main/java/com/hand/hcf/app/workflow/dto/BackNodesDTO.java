package com.hand.hcf.app.workflow.dto;


import com.hand.hcf.app.workflow.brms.dto.ReturnNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(value = "可退回节点列表")
public class BackNodesDTO {


    @ApiModelProperty(value = "退回后重新审批选项：1001-全部重新审批，1002-直接跳回本节点，1003-退回人自主判断")
    private Integer  backFlag;

    @ApiModelProperty(value = "允许退回指定节点")
    private Boolean allowBackNode;

    @ApiModelProperty(value = "退回选项：1001-本节点前任意节点，1002-自选节点")
    private Integer returnType;


    @ApiModelProperty(value = "节点列表")
    private List<ReturnNode> approvalNodeDTOList;

}
