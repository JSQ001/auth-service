package com.hand.hcf.app.workflow.dto.dashboard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="审批仪表盘记录")
public class ApprovalDashboardDetailDTO {
    @ApiModelProperty(value="属性名称")
    private String name;
    @ApiModelProperty(value="数量")
    private Integer count;
    @ApiModelProperty(value="类型")
    private String type;
}
