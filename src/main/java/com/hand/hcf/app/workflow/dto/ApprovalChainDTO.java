package com.hand.hcf.app.workflow.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ApprovalChainDTO {

    @JsonIgnore
    private Long id;

    private Integer entityType;

    private UUID entityOid;

    private Integer sequence;

    private UUID approverOid;

    private Boolean currentFlag;

    private Boolean finishFlag;

    private Integer status;

    private ZonedDateTime createdDate;

    private ZonedDateTime lastUpdatedDate;

    // extend
    private String approverName;

    private String approverEmployeeID;

    private boolean apportionmentFlag;

    private Integer countersignType;

    private UUID operatorOid;

    private List<UUID> proxyApproverOids;

    private List<UserApprovalDTO> proxyApprovers;

    private Integer invoiceAllowUpdateType;

    private boolean allFinished;

    private Long sourceApprovalChainId;

    private Integer isAddSign;
}
