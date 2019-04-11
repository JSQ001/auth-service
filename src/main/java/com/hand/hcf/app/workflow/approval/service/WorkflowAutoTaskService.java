package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowAutoApproveAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowNextNodeAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowPassTaskAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowRejectTaskAction;
import com.hand.hcf.app.workflow.approval.util.WorkflowResult;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * 工作流自动任务逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowAutoTaskService {
    @Autowired
    private WorkflowMoveNodeService workflowMoveNodeService;

    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private WorkflowPassService workflowPassService;

    @Autowired
    private WorkflowRejectService workflowRejectService;

    /** 机器人oid */
    public static final UUID ROBOT_OID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    /**
     * 自动审批
     *
     * @param node
     * @param skip
     * @return
     */
    public WorkflowResult autoApprove(WorkflowNode node, Boolean skip) {
        Assert.notNull(node, "node null");
        Assert.notNull(skip, "skip null");
        Assert.notNull(node.getInstance(), "node.instance null");
        Assert.notNull(node.getApprovalAction(), "node.approvalAction null");
        WorkflowInstance instance = node.getInstance();
        String approvalAction = node.getApprovalAction();
        String approvalText = node.getApprovalText();

        WorkflowResult result = new WorkflowResult();
        result.setEntity(node);

        if (!skip) {
            WorkflowUser user = new WorkflowUser(ROBOT_OID);
            // 保存任务
            workflowBaseService.saveTask(node, user);

            // 审批动作
            if (WorkflowNode.ACTION_APPROVAL_PASS.equals(approvalAction)) {
                if (StringUtils.isEmpty(approvalText)) {
                    approvalText = getRobotPassDetail();
                }

                WorkflowPassTaskAction action = new WorkflowPassTaskAction(workflowPassService, instance, user, approvalText);
                result.setStatus(WorkflowAutoApproveAction.RESULT_PASS_NODE);
                result.setNext(action);
            } else if (WorkflowNode.ACTION_APPROVAL_REJECT.equals(approvalAction)) {
                if (StringUtils.isEmpty(approvalText)) {
                    approvalText = getRobotRejectDetail();
                }

                WorkflowRejectTaskAction action = new WorkflowRejectTaskAction(workflowRejectService, instance, user, approvalText);
                result.setStatus(WorkflowAutoApproveAction.RESULT_REJECT_NODE);
                result.setNext(action);
            } else {
                throw new IllegalArgumentException(String.format("node.approvalAction(%s) invalid", approvalAction));
            }
        } else {
            // 跳过节点
            WorkflowNextNodeAction action = new WorkflowNextNodeAction(workflowMoveNodeService, instance, node);
            result.setStatus(WorkflowAutoApproveAction.RESULT_SKIP_NODE);
            result.setNext(action);
        }

        return result;
    }


    /**
     * 返回机器人通过审批意见
     * @author mh.z
     * @date 2019/04/07
     *
     * @return
     */
    protected String getRobotPassDetail() {
        String detail = null;

        if (Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
            detail = RuleConstants.APPROVER_ROBOT_PASS_Detail;
        } else {
            detail = RuleConstants.APPROVER_ROBOT_PASS_Detail_ENGLISH;
        }

        return detail;
    }

    /**
     * 返回机器人驳回审批意见
     * @author mh.z
     * @date 2019/04/07
     *
     * @return
     */
    protected String getRobotRejectDetail() {
        String detail = null;

        if (Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
            detail = RuleConstants.APPROVER_ROBOT_REJECT_Detail;
        } else {
            detail = RuleConstants.APPROVER_ROBOT_REJECT_Detail_ENGLISH;
        }

        return detail;
    }

}
