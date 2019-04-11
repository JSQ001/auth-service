package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowSubmitService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;

/**
 * 提交实例动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowSubmitInstanceAction implements WorkflowAction {
    private WorkflowSubmitService workflowSubmitService;
    /** 实例 */
    private WorkflowInstance instance;
    /** 用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "submitInstance";
    /** 提交成功 */
    public static final String RESULT_SUBMIT_SUCCESS = "submitSuccess";

    public WorkflowSubmitInstanceAction(WorkflowSubmitService workflowSubmitService, WorkflowInstance instance, WorkflowUser user, String remark) {
        this.workflowSubmitService = workflowSubmitService;
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
        // 提交实例
        return workflowSubmitService.submitInstance(instance, user, remark);
    }

}
