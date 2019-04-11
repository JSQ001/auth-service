package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowWithdrawService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;

/**
 * 撤回实例动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowWithdrawInstanceAction implements WorkflowAction {
    private WorkflowWithdrawService service;
    /** 实例 */
    private WorkflowInstance instance;
    /** 用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "withdrawInstance";
    /** 撤回成功 */
    public static final String RESULT_WITHDRAW_SUCCESS = "withdrawSuccess";

    public WorkflowWithdrawInstanceAction(WorkflowWithdrawService service, WorkflowInstance instance, WorkflowUser user, String remark) {
        this.service = service;
        this.instance = instance;
        this.user = user;
        this.remark = remark;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 撤回实例
        return service.withdrawInstance(instance, user, remark);
    }

}
