package com.hand.hcf.app.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
@ApiModel(description = "审批响应数据")
public class ApprovalResDTO {
    @ApiModelProperty(value = "成功数量")
    private Integer successNum;
    @ApiModelProperty(value = "失败数量")
    private Integer failNum;
    @ApiModelProperty(value = "完成标志")
    private Boolean finishFlag;
    @ApiModelProperty(value = "失败原因")
    private Map<String, String> failReason = new HashMap<>();

}
