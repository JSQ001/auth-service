package com.hand.hcf.app.workflow.workflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by lichao on 2016/8/2.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Integer entityType;

    private UUID entityOid;

    private Integer operationType;

    private Integer operation;

    private UUID operatorOid;

    private UUID currentApplicantOid;

    private String operationDetail;
    @JsonIgnore
    private Long stepID;

    private String remark;

    private ZonedDateTime createdDate;

    private ZonedDateTime lastUpdatedDate;
    /**
     * 审批人
     */
    private UserApprovalDTO operator;

    /**
     * 当前提交人
     */
    private UserApprovalDTO currentApplicant;

    private Integer countersignType;

    private boolean apportionmentFlag;

    private Long refApprovalChainId;

    private UUID chainApproverOid;

    private UserApprovalDTO chainApprover;

    /**
    * 操作描述
    */
    private String operationDescription;

    private String employeeName;

    private String employeeID;

    private String approvalNodeName;

    private UUID ruleApprovalNodeOid;

    private UUID  approvalNodeOid;

}
