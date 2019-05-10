package com.hand.hcf.app.workflow.dto;

import com.hand.hcf.app.workflow.approval.enums.BackTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@ApiModel(description = "工作流提交数据")
public class ApprovalReqDTO {

    @NotNull
    @ApiModelProperty(value = "单据实体数组")
    private List<Entity> entities;
    @ApiModelProperty(value = "审批备注")
    private String approvalTxt;
    @ApiModelProperty(value = "审批OID")
    private UUID approvalOid;
    @ApiModelProperty(value = "审批表单类型OID")
    private UUID formOid;

    @ApiModelProperty(value = "驳回类型")
    private BackTypeEnum backTypeEnum;

    @ApiModelProperty(value = "驳回后再次提交处理")
    private Integer rejectRule;

    @Data
    @ApiModel(description = "单据实体")
    public static class Entity {
        @ApiModelProperty(value = "单据OID")
        private String entityOid;
        @ApiModelProperty(value = "审批流类型")
        private Integer entityType;
        @ApiModelProperty(value = "加签人员OID列表")
        private List<UUID> countersignApproverOids;
        //是否进行机票价格审核（针对订票申请单）
        private boolean priceAuditor = false;
        //chain上的审批人
        @ApiModelProperty(value = "审批人OID")
        private String approverOid;


    }
}
