package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowPassService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;

/**
 * 通过任务动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowPassTaskAction implements WorkflowAction {
    private WorkflowPassService service;
    /** 实例 */
    private WorkflowInstance instance;
    /** 用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "passTask";
    /** 通过成功 */
    public static final String RESULT_PASS_SUCCESS = "passSuccess";

    public WorkflowPassTaskAction(WorkflowPassService service, WorkflowInstance instance, WorkflowUser user, String remark) {
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
        // 通过任务
        return service.passTask(instance, user, remark);
    }

}
