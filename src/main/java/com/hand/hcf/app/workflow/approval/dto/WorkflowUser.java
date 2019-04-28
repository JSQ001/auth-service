package com.hand.hcf.app.workflow.approval.dto;

import java.util.UUID;

/**
 * 工作流用户
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowUser {
    /*
    只提供获取/设置通用的字段
     */

    /** 用户oid */
    private UUID userOid;

    public WorkflowUser(UUID userOid) {
        this.userOid = userOid;
    }

    /**
     * 返回用户oid
     *
     * @return
     */
    public UUID getUserOid() {
        return userOid;
    }

}
