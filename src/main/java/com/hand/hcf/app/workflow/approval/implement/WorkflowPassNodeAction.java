package com.hand.hcf.app.workflow.approval.implement;

import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.service.WorkflowPassService;
import com.hand.hcf.app.workflow.approval.util.WorkflowAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;

/**
 * 通过节点动作
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowPassNodeAction implements WorkflowAction {
    private WorkflowPassService service;
    /** 节点 */
    private WorkflowNode node;
    /** 用户 */
    private WorkflowUser user;
    /** 备注 */
    private String remark;

    /** 动作名称 */
    public static final String ACTION_NAME = "passNode";
    /** 通过成功 */
    public static final String RESULT_PASS_SUCCESS = "passSuccess";
    /** 通过待定 */
    public static final String RESULT_PASS_PEND = "passPend";

    public WorkflowPassNodeAction(WorkflowPassService service, WorkflowNode node, WorkflowUser user, String remark) {
        this.service = service;
        this.node = node;
        this.user = user;
        this.remark = remark;
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public WorkflowResult execute() {
        // 通过节点
        return service.passNode(node, user, remark);
    }

}
