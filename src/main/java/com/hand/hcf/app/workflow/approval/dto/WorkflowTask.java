package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;

/**
 * 工作流任务
 * @author mh.z
 * @date 2019/04/07
 */
public class WorkflowTask {
    /*
    只提供获取/设置通用的字段
     */

    private ApprovalChain approvalChain;
    /** 任务关联的实例 */
    private WorkflowInstance instance;
    /** 任务关联的节点 */
    private WorkflowNode node;
    /** 任务关联的用户 */
    private WorkflowUser user;

    /** 审批状态-未激活 */
    public static final Integer APPROVAL_STATUS_GENERAL = 1000;
    /** 审批状态-审批中 */
    public static final Integer APPROVAL_STATUS_APPROVAL = 2000;
    /** 审批状态-已审批 */
    public static final Integer APPROVAL_STATUS_APPROVED = 3000;

    /** 任务状态-记录有效 */
    public static final Integer STATUS_NORMAL = ApprovalChainStatusEnum.NORMAL.getId();
    /** 任务状态-记录无效 */
    public static final Integer STATUS_INVALID = ApprovalChainStatusEnum.INVALID.getId();

    public WorkflowTask(ApprovalChain approvalChain, WorkflowInstance instance, WorkflowNode node, WorkflowUser user) {
        this.approvalChain = approvalChain;
        this.instance = instance;
        this.node = node;
        this.user = user;
    }

    public ApprovalChain getApprovalChain() {
        return approvalChain;
    }

    public void setApprovalChain(ApprovalChain approvalChain) {
        this.approvalChain = approvalChain;
    }

    /**
     * 返回任务关联的实例
     *
     * @return
     */
    public WorkflowInstance getInstance() {
        return instance;
    }

    /**
     * 返回任务关联的节点
     *
     * @return
     */
    public WorkflowNode getNode() {
        return node;
    }

    /**
     * 返回任务关联的用户
     *
     * @return
     */
    public WorkflowUser getUser() {
        return user;
    }

    /**
     * 返回任务id
     *
     * @return
     */
    public Long getId() {
        return approvalChain.getId();
    }

    /**
     * 返回任务状态
     *
     * @return
     */
    public Integer getStatus() {
        return approvalChain.getStatus();
    }

    /**
     * 设置任务状态
     *
     * @param status
     */
    public void setStatus(Integer status) {
        approvalChain.setStatus(status);
    }

    /**
     * 返回审批状态
     *
     * @return
     */
    public Integer getApprovalStatus() {
        Integer approvalStatus = null;
        Boolean currentFlag = approvalChain.getCurrentFlag();
        Boolean finishFlag = approvalChain.getFinishFlag();

        if (Boolean.FALSE.equals(currentFlag) && Boolean.FALSE.equals(finishFlag)) {
            approvalStatus = APPROVAL_STATUS_GENERAL;
        } else if (Boolean.TRUE.equals(currentFlag) && Boolean.FALSE.equals(finishFlag)) {
            approvalStatus = APPROVAL_STATUS_APPROVAL;
        } else if (Boolean.FALSE.equals(currentFlag) && Boolean.TRUE.equals(finishFlag)) {
            approvalStatus = APPROVAL_STATUS_APPROVED;
        } else {
            String format = "ApprovalChain.currentFlag(%B),ApprovalChain.finishFlag(%B) invalid";
            throw new IllegalArgumentException(String.format(format, currentFlag, finishFlag));
        }

        return approvalStatus;
    }

    /**
     * 设置审批状态
     *
     * @param approvalStatus
     */
    public void setApprovalStatus(Integer approvalStatus) {
        Boolean currentFlag = null;
        Boolean finishFlag = null;

        if (APPROVAL_STATUS_GENERAL.equals(approvalStatus)) {
            currentFlag = false;
            finishFlag = false;
        } else if (APPROVAL_STATUS_APPROVAL.equals(approvalStatus)) {
            currentFlag = true;
            finishFlag = false;
        } else if (APPROVAL_STATUS_APPROVED.equals(approvalStatus)) {
            currentFlag = false;
            finishFlag = true;
        } else {
            String format = "WorkflowTask.approvalStatus(%s) invalid";
            throw new IllegalArgumentException(String.format(format, approvalStatus));
        }

        approvalChain.setCurrentFlag(currentFlag);
        approvalChain.setFinishFlag(finishFlag);
    }

    /**
     * 返回任务组编号
     *
     * @return
     */
    public Integer getGroup() {
        return approvalChain.getGroupNumber();
    }

}
