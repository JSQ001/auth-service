package com.hand.hcf.app.workflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by lichao on 2017/7/7.
 * 下一个审批人（会签）
 */
@Getter
@Setter
public class ApprovalChainView {
    private UUID approverOid;
    private Integer sequence;
    private String approverName;
    private String approverEmployeeID;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastUpdatedDate;
}
