package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.common.co.ContactCO;
import lombok.Data;

import java.util.UUID;

/**
 * 工作流用户
 * @author mh.z
 * @date 2019/04/07
 */
@Data
public class WorkflowUser {
    /** 用户oid */
    UUID userOid;

    public WorkflowUser() {

    }

    public WorkflowUser(UUID userOid) {
        this.userOid = userOid;
    }

    /**
     * 转换成工作流用户
     *
     * @param contactCO
     * @return
     */
    public static WorkflowUser toUser(ContactCO contactCO) {
        if (contactCO == null) {
            return null;
        }

        WorkflowUser user = new WorkflowUser();
        String userOidStr = contactCO.getUserOid();

        if (userOidStr != null) {
            user.setUserOid(UUID.fromString(userOidStr));
        }

        return user;
    }

}
