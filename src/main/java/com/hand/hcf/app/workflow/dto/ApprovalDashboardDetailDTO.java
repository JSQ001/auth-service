package com.hand.hcf.app.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="�����Ǳ��̼�¼")
public class ApprovalDashboardDetailDTO {
    @ApiModelProperty(value="��������")
    private String name;
    @ApiModelProperty(value="����")
    private Integer count;
    @ApiModelProperty(value="����")
    private String type;
}
