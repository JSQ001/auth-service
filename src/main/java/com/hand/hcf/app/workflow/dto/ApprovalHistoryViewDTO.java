package com.hand.hcf.app.workflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by lichao on 2017/7/6.
 */
@Getter
@Setter
public class ApprovalHistoryViewDTO {

    private Integer operationType;

    private Integer operation;

    private UUID operatorOid;

    private String operationDetail;

    private String remark;

    private ZonedDateTime createdDate;

    private ZonedDateTime lastUpdatedDate;

    private String fullName;
    private String employeeId;
}
