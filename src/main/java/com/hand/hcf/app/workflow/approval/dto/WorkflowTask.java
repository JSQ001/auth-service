package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;
import lombok.Data;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 * 工作流任务
 * @author mh.z
 * @date 2019/04/07
 */
@Data
public class WorkflowTask {
    /** 任务id */
    private Long id;
    /** 实例 */
    private WorkflowInstance instance;
    /** 用户 */
    private WorkflowUser user;
    /** 节点 */
    private WorkflowNode node;
    /** 任务状态 */
    private String status;
    /** 有效标志 */
    private Boolean enabled;

    /** 未激活 */
    public static final String STATUS_GENERAL = "general";
    /** 审批中 */
    public static final String STATUS_APPROVAL = "approval";
    /** 已审批 */
    public static final String STATUS_APPROVED = "approved";

    /** 有效标志映射 */
    private static final DualHashBidiMap<Boolean, Integer> enabledMap;

    static {
        enabledMap = new DualHashBidiMap<Boolean, Integer>();
        enabledMap.put(true, ApprovalChainStatusEnum.NORMAL.getId());
        enabledMap.put(false, ApprovalChainStatusEnum.INVALID.getId());
    }

    public static WorkflowTask toTask(ApprovalChain approvalChain) {
        if (approvalChain == null) {
            return null;
        }

        WorkflowTask task = new WorkflowTask();
        task.setId(approvalChain.getId());
        // 有效标志
        task.setEnabled(getEnabledKey(approvalChain.getStatus()));
        // 任务状态
        Boolean currentFlag = approvalChain.getCurrentFlag();
        Boolean finishFlag = approvalChain.getFinishFlag();
        task.setStatus(getStatusKey(currentFlag, finishFlag));

        return task;
    }

    /**
     * 有效标志映射
     *
     * @param value
     * @return
     */
    public static Boolean getEnabledKey(Integer value) {
        if (value == null) {
            return null;
        }

        Boolean key = enabledMap.getKey(value);
        if (key == null) {
            String format = "ApprovalChain.status(%d) invalid";
            throw new IllegalArgumentException(String.format(format, value));
        }

        return key;
    }

    /**
     * 有效标志映射
     *
     * @param key
     * @return
     */
    public static Integer getEnabledValue(Boolean key) {
        if (key == null) {
            return null;
        }

        Integer value = enabledMap.get(key);
        if (value == null) {
            String format = "WorkflowTask.enabled(%B) invalid";
            throw new IllegalArgumentException(String.format(format, key));
        }

        return value;
    }

    /**
     * 状态值映射
     *
     * @param currentFlag
     * @param finishFlag
     * @return
     */
    public static String getStatusKey(Boolean currentFlag, Boolean finishFlag) {
        String key = null;

        if (Boolean.FALSE.equals(currentFlag) && Boolean.FALSE.equals(finishFlag)) {
            key = STATUS_GENERAL;
        } else if (Boolean.TRUE.equals(currentFlag) && Boolean.FALSE.equals(finishFlag)) {
            key = STATUS_APPROVAL;
        } else if (Boolean.FALSE.equals(currentFlag) && Boolean.TRUE.equals(finishFlag)) {
            key = STATUS_APPROVED;
        } else {
            String format = String.format("ApprovalChain.currentFlag(%B),ApprovalChain.finishFlag(%B) invalid");
            throw new IllegalArgumentException(String.format(format, currentFlag, finishFlag));
        }

        return key;
    }

    /**
     * 状态值映射
     *
     * @param approvalChain
     * @param key
     */
    public static void getStatusValue(ApprovalChain approvalChain, String key) {
        if (STATUS_GENERAL.equals(key)) {
            approvalChain.setCurrentFlag(false);
            approvalChain.setFinishFlag(false);
        } else if (STATUS_APPROVAL.equals(key)) {
            approvalChain.setCurrentFlag(true);
            approvalChain.setFinishFlag(false);
        } else if (STATUS_APPROVED.equals(key)) {
            approvalChain.setCurrentFlag(false);
            approvalChain.setFinishFlag(true);
        } else {
            String format = "WorkflowTask.status(%s) invalid";
            throw new IllegalArgumentException(String.format(format, key));
        }
    }

}
