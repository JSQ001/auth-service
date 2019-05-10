package com.hand.hcf.app.workflow.dto.chain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic = true)
@ApiModel(value = "审批链节点")
public class ApprovalNodeDTO {

    @ApiModelProperty(value = "审批节点oid")
    private UUID ruleApprovalNodeOid;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "审批链oid")
    private UUID ruleApprovalChainOid;
    @ApiModelProperty(value = "代码")
    private String code;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "类型")
    @JsonProperty("type")
    private Integer typeNumber;

    @ApiModelProperty(value="序号")
    private Integer sequenceNumber;

    @ApiModelProperty(value="是否可退回")
    private Boolean backable=Boolean.FALSE;

    @ApiModelProperty(value="是否可加签")
    private Boolean addSignable=Boolean.FALSE;

    @ApiModelProperty(value="是否可转交")
    private Boolean transferable=Boolean.FALSE;

    @ApiModelProperty(value = "驳回后再次提交处理")
    private Integer rejectRule;
}
