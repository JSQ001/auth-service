package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowRejectService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;

/**
 * 驳回任务动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowRejectTaskAction implements WorkflowAction {
    private WorkflowRejectService service;
    /** 实例 */
    private WorkflowInstance instance;
    /** 用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "rejectTask";
    /** 驳回成功 */
    public static final String RESULT_REJECT_SUCCESS = "rejectSuccess";

    public WorkflowRejectTaskAction(WorkflowRejectService service, WorkflowInstance instance, WorkflowUser user, String remark) {
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
        // 驳回任务
        return service.rejectTask(instance, user, remark);
    }

}
