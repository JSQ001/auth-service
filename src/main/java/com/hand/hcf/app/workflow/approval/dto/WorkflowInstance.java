package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import lombok.Data;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.UUID;

/**
 * 工作流实例
 * @author mh.z
 * @date 2019/04/07
 */
@Data
public class WorkflowInstance {
    /** 实例id */
    private Long id;

    private Integer entityType;
    private UUID entityOid;

    /** 审批状态 */
    private String status;
    /** 表单oid */
    private UUID formOid;
    /** 申请人oid */
    private UUID applicantOid;

    /** 未提交 */
    public static final String STATUS_GENERAL = "general";
    /** 审批中 */
    public static final String STATUS_APPROVAL = "approval";
    /** 已通过 */
    public static final String STATUS_PASS = "pass";
    /** 已驳回 */
    public static final String STATUS_REJECT = "reject";
    /** 已撤回 */
    public static final String STATUS_WITHDRAW = "withdraw";

    /** 状态映射 */
    private static final DualHashBidiMap<String, Integer> statusMap;

    static {
        statusMap = new DualHashBidiMap<String, Integer>();
        statusMap.put(STATUS_GENERAL, DocumentOperationEnum.GENERATE.getId());
        statusMap.put(STATUS_APPROVAL, DocumentOperationEnum.APPROVAL.getId());
        statusMap.put(STATUS_WITHDRAW, DocumentOperationEnum.WITHDRAW.getId());
        statusMap.put(STATUS_PASS, DocumentOperationEnum.APPROVAL_PASS.getId());
        statusMap.put(STATUS_REJECT, DocumentOperationEnum.APPROVAL_REJECT.getId());
    }

    public static WorkflowInstance toInstance(WorkFlowDocumentRef workFlowDocumentRef) {
        if (workFlowDocumentRef == null) {
            return null;
        }

        WorkflowInstance instance = new WorkflowInstance();
        instance.setId(workFlowDocumentRef.getId());
        instance.setEntityType(workFlowDocumentRef.getDocumentCategory());
        instance.setEntityOid(workFlowDocumentRef.getDocumentOid());
        instance.setFormOid(workFlowDocumentRef.getFormOid());
        instance.setApplicantOid(workFlowDocumentRef.getApplicantOid());
        // 审批状态
        instance.setStatus(getStatusKey(workFlowDocumentRef.getStatus()));

        return instance;
    }

    /**
     * 状态值映射
     *
     * @param value
     * @return
     */
    public static String getStatusKey(Integer value) {
        if (value == null) {
            return null;
        }

        String key = statusMap.getKey(value);
        if (key == null) {
            String format = "WorkFlowDocumentRef.status(%d) invalid";
            throw new IllegalArgumentException(String.format(format, value));
        }

        return key;
    }

    /**
     * 状态值映射
     *
     * @param key
     * @return
     */
    public static Integer getStatusValue(String key) {
        if (key == null) {
            return null;
        }

        Integer value = statusMap.get(key);
        if (value == null) {
            String format = "WorkflowInstance.status(%s) invalid";
            throw new IllegalArgumentException(String.format(format, key));
        }

        return value;
    }

}
