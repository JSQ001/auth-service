package com.hand.hcf.app.workflow.workflow.dto;

import com.hand.hcf.app.workflow.workflow.enums.ApprovalFormUserScopeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Created by lichao on 2017/7/4.
 * 表单可见人员范围
 */
@Getter
@Setter
public class ApprovalFormUserScopeDTO {
    //表单oid
    private UUID formOid;
    //默认表单可见范围：1002字定义
    private Integer visibleScope = ApprovalFormUserScopeEnum.USER_GROUP.getId();
    //用户组oid
    List<UUID> userGroupOids;
    //部门oid
    List<UUID> departmentOids;
    //for view
    private List<ApprovalFormDepartmentVO> departments;
    private List<ApprovalFormUserGroupVO> userGroups;
}
