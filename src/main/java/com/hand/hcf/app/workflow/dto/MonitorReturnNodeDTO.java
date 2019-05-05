package com.hand.hcf.app.workflow.dto;

import com.hand.hcf.app.workflow.brms.dto.ReturnNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
public class MonitorReturnNodeDTO {
    @ApiModelProperty(value = "节点列表")
    private List<MonitorNode> approvalNodeDTOList;
}
