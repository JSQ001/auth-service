package com.hand.hcf.app.workflow.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(value = "可退回节点列表")
public class BackNodesDTO {


    @ApiModelProperty(value = "是否展示退回审批选项")
    private Boolean  backFlag;

    @ApiModelProperty(value = "节点列表")
    private List<ApprovalNodeDTO> approvalNodeDTOList;

}
