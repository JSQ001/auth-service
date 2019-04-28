package com.hand.hcf.app.workflow.approval.dto;

/**
 * 工作流审批
 * @author mh.z
 * @date 2019/04/27
 */
public class WorkflowApproval {
    /** 审批关联的任务 */
    private WorkflowTask task;
    /** 操作类型 */
    private Integer operation;
    /** 备注 */
    private String remark;
    /** 审批顺序 */
    private Long approvalSequence;

    /** 操作类型-通过 */
    public static final Integer OPERATION_PASS = 1;
    /** 操作类型-驳回 */
    public static final Integer OPERATION_REJECT = 2;

    public WorkflowApproval(WorkflowTask task, Integer operation, String remark) {
        this.task = task;
        this.operation = operation;
        this.remark = remark;
        this.approvalSequence = 0L;
    }

    /**
     * 返回审批关联的任务
     *
     * @return
     */
    public WorkflowTask getTask() {
        return task;
    }

    /**
     * 返回操作类型
     *
     * @return
     */
    public Integer getOperation() {
        return operation;
    }

    /**
     * 返回备注
     *
     * @return
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 返回审批顺序
     * +
     * @return
     */
    public Long getApprovalSequence() {
        return approvalSequence;
    }

    /**
     * 设置审批顺序
     *
     * @param approvalSequence
     */
    public void setApprovalSequence(Long approvalSequence) {
        this.approvalSequence = approvalSequence;
    }

}
