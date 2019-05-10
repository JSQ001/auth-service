package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.approval.dto.*;
import com.hand.hcf.app.workflow.brms.constant.RuleConstants;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 工作流机器人审批逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowRobotApproveService {
    @Autowired
    private WorkflowBaseService workflowBaseService;

    /**
     * 返回机器人审批操作
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 机器人节点
     * @return 机器人审批操作
     */
    public WorkflowApproval getRobotApproval(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");

        WorkflowInstance instance = node.getInstance();
        WorkflowUser user = new WorkflowUser(UUID.fromString(RuleConstants.APPROVER_TYPE_ROBOT_OID));
        // 获取下一个任务组编号
        int group = workflowBaseService.nextGroup(instance);
        // 创建审批任务
        WorkflowTask task = workflowBaseService.createTask(node, user, group);
        // 保存审批任务
        workflowBaseService.saveTask(task);

        // 获取操作类型
        Integer operation = getApprovalAction(node);

        String remark = null;
        // 获取审批意见
        if (WorkflowApproval.OPERATION_PASS.equals(operation)) {
            // 获取审批通过意见
            remark = getPassRemark(node);
        } else if (WorkflowApproval.OPERATION_REJECT.equals(operation)) {
            // 获取审批驳回意见
            remark = getRejectRemark(node);
        } else {
            String format = "getApprovalAction return value(%s) invalid";
            throw new RuntimeException(String.format(format, operation));
        }

        WorkflowApproval approval = new WorkflowApproval(task, operation, remark);
        return approval;
    }

    /**
     * 返回机器人的审批操作类型
     * @author mh.z
     * @date 2019/04/21
     *
     * @param node 机器人节点
     * @return 操作类型
     */
    protected Integer getApprovalAction(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");

        Integer approvalAction = null;
        // 获取机器人节点设置的节点类型
        RuleApprovalNode ruleApprovalNode = node.getRuleApprovalNode();
        String ruleApprovalNodeApprovalAction = ruleApprovalNode.getApprovalActions();

        if (String.valueOf(RuleConstants.ACTION_APPROVAL_PASS).equals(ruleApprovalNodeApprovalAction)) {
            approvalAction = WorkflowApproval.OPERATION_PASS;
        } else if (String.valueOf(RuleConstants.ACTION_APPROVAL_REJECT).equals(ruleApprovalNodeApprovalAction)) {
            approvalAction = WorkflowApproval.OPERATION_REJECT;
        } else {
            String format = "node.ruleApprovalNode.approvalActions(%s) invalid";
            throw new IllegalArgumentException(String.format(format, ruleApprovalNodeApprovalAction));
        }

        return approvalAction;
    }

    /**
     * 返回机器人审批通过意见
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 机器人节点
     * @return 审批通过意见
     */
    protected String getPassRemark(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");

        String passRemark = null;
        // 获取机器人节点设置的审批意见
        RuleApprovalNode ruleApprovalNode = node.getRuleApprovalNode();
        String ruleApprovalNodeComment = ruleApprovalNode.getComments();

        if (StringUtils.isNotEmpty(ruleApprovalNodeComment)) {
            // 如果机器人节点上设置了审批意见，则视为这个审批意见就是机器人审批通过意见
            passRemark = ruleApprovalNodeComment;
        } else if (Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
            passRemark = RuleConstants.APPROVER_ROBOT_PASS_DETAIL;
        } else {
            passRemark = RuleConstants.APPROVER_ROBOT_PASS_DETAIL_ENGLISH;
        }

        return passRemark;
    }

    /**
     * 返回机器人审批驳回意见
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 机器人节点
     * @return 审批驳回意见
     */
    protected String getRejectRemark(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");

        String rejectRemark = null;
        // 获取机器人节点设置的审批意见
        RuleApprovalNode ruleApprovalNode = node.getRuleApprovalNode();
        String ruleApprovalNodeComment = ruleApprovalNode.getComments();

        if (StringUtils.isNotEmpty(ruleApprovalNodeComment)) {
            // 如果机器人节点上设置了审批意见，则视为这个审批意见就是机器人审批驳回意见
            rejectRemark = ruleApprovalNodeComment;
        } else if (Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
            rejectRemark = RuleConstants.APPROVER_ROBOT_REJECT_DETAIL;
        } else {
            rejectRemark = RuleConstants.APPROVER_ROBOT_REJECT_DETAIL_ENGLISH;
        }

        return rejectRemark;
    }

}
