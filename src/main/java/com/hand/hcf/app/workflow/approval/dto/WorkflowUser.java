package com.hand.hcf.app.workflow.approval.dto;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * 创建用户对象
     *
     * @param userOidList
     * @return
     */
    public static List<WorkflowUser> createUsers(List<UUID> userOidList) {
        List<WorkflowUser> userList = new ArrayList<WorkflowUser>();

        for (UUID userOid : userOidList) {
            userList.add(new WorkflowUser(userOid));
        }

        return userList;
    }

}
