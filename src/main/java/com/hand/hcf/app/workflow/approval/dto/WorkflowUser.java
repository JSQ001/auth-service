package com.hand.hcf.app.workflow.approval.dto;

import java.util.UUID;

/**
 * 工作流用户
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowUser {
    /** 用户oid */
    private UUID userOid;

    public WorkflowUser(UUID userOid) {
        this.userOid = userOid;
    }

    public UUID getUserOid() {
        return userOid;
    }

}
