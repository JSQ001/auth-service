package com.hand.hcf.app.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 控制撤回、加签、转交、通知等跟审批有关的按钮是否显示
 * @author mh.z
 * @date 2019/04/29
 */
@ApiModel("工作流操作")
@Data
public class WorkflowOperationDTO {
    @ApiModelProperty(value = "true可以撤回，false不能撤回")
    private Boolean withdrawFlag;
}
