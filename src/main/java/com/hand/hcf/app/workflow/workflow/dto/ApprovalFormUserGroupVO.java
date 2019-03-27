package com.hand.hcf.app.workflow.workflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by lichao on 2017/7/10.
 */
@Getter
@Setter
public class ApprovalFormUserGroupVO {
    private UUID userGroupOID;
    private String name;
}
