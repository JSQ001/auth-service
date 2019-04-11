package com.hand.hcf.app.workflow.approval.dto;

import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import lombok.Data;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.UUID;

/**
 * 工作流节点
 * @author mh.z
 * @date 2019/04/07
 */
@Data
public class WorkflowNode {
    /** 节点id */
    private Long id;
    /** 节点名称 */
    private String name;
    /** 节点oid */
    private UUID nodeOid;
    /** 实例 */
    private WorkflowInstance instance;
    /** 审批链oid */
    private UUID chainOid;
    /** 节点序号 */
    private Integer sequence;
    /** 节点类型 */
    private String type;
    /** 为空规则 */
    private Boolean skipEmpty;
    /** 会签规则 */
    private String countersign;
    /** 审批动作（机器人用到） */
    private String approvalAction;
    /** 审批意见（机器人用到） */
    private String approvalText;

    /** 审批节点 */
    public static final String TYPE_USER = "user";
    /** 机器人节点 */
    public static final String TYPE_ROBOT = "robot";
    /** 结束节点 */
    public static final String TYPE_END = "end";
    /** 通知节点 */
    public static final String TYPE_NOTICE = "notice";

    /** 会签规则-所有审批人（所有审批人审批通过则单据审批通过，任一审批人审批驳回则单据被驳回） */
    public static final String COUNTERSIGN_ALL_PASS_OR_ANY_REJECT = "allPassOrAnyReject";
    /** 会签规则-任一人（任一审批人审批通过则单据被审批通过，所有审批人都审批驳回则单据才被驳回） */
    public static final String COUNTERSIGN_ANY_PASS_OR_ALL_REJECT = "anyPassOrAllReject";
    /** 会签规则-一票通过/一票否决（一位审批人审批通过则单据被审批通过，一位审批人审批驳回后则单据被驳回） */
    public static final String COUNTERSIGN_ANY_PASS_OR_ANY_REJECT = "anyPassOrAnyReject";

    /** 审批通过 */
    public static final String ACTION_APPROVAL_PASS = "pass";
    /** 审批驳回 */
    public static final String ACTION_APPROVAL_REJECT = "reject";

    /** 节点类型映射 */
    private static final DualHashBidiMap<String, Integer> typeMap;
    /** 为空规则映射 */
    private static final DualHashBidiMap<Boolean, Integer> skipEmptyMap;
    /** 会签规则映射 */
    private static final DualHashBidiMap<String, Integer> countersignMap;
    /** 审批动作映射 */
    private static final DualHashBidiMap<String, String> approvalActionMap;

    static {
        typeMap = new DualHashBidiMap<String, Integer>();
        typeMap.put(TYPE_USER, RuleApprovalEnum.NODE_TYPE_APPROVAL.getId());
        typeMap.put(TYPE_ROBOT, RuleApprovalEnum.NODE_TYPE_ROBOT.getId());
        typeMap.put(TYPE_END, RuleApprovalEnum.NODE_TYPE_EED.getId());
        typeMap.put(TYPE_NOTICE, RuleApprovalEnum.NODE_TYPE_NOTICE.getId());

        skipEmptyMap = new DualHashBidiMap<Boolean, Integer>();
        skipEmptyMap.put(true, RuleApprovalEnum.RULE_NULLABLE_SKIP.getId());
        skipEmptyMap.put(false, RuleApprovalEnum.RULE_NULLABLE_THROW.getId());

        countersignMap = new DualHashBidiMap<String, Integer>();
        countersignMap.put(COUNTERSIGN_ALL_PASS_OR_ANY_REJECT, RuleApprovalEnum.RULE_CONUTERSIGN_ALL.getId());
        countersignMap.put(COUNTERSIGN_ANY_PASS_OR_ANY_REJECT, RuleApprovalEnum.RULE_CONUTERSIGN_ANY.getId());
        countersignMap.put(COUNTERSIGN_ANY_PASS_OR_ALL_REJECT, RuleApprovalEnum.RULE_CONUTERSIGN_ALL_REJECT.getId());

        approvalActionMap = new DualHashBidiMap<String, String>();
        approvalActionMap.put(ACTION_APPROVAL_PASS, String.valueOf(RuleConstants.ACTION_APPROVAL_PASS));
        approvalActionMap.put(ACTION_APPROVAL_REJECT, String.valueOf(RuleConstants.ACTION_APPROVAL_REJECT));
    }

    public static WorkflowNode toNode(RuleApprovalNode ruleApprovalNode) {
        if (ruleApprovalNode == null) {
            return null;
        }

        WorkflowNode node = new WorkflowNode();
        node.setId(ruleApprovalNode.getId());
        node.setName(ruleApprovalNode.getName());
        node.setNodeOid(ruleApprovalNode.getRuleApprovalNodeOid());
        node.setChainOid(ruleApprovalNode.getRuleApprovalChainOid());
        node.setSequence(ruleApprovalNode.getSequenceNumber());
        node.setApprovalText(ruleApprovalNode.getComments());
        // 节点类型
        node.setType(typeMap.getKey(ruleApprovalNode.getTypeNumber()));
        // 为空规则
        node.setSkipEmpty(skipEmptyMap.getKey(ruleApprovalNode.getNullableRule()));
        // 会签规则
        node.setCountersign(getCountersignKey(ruleApprovalNode.getCountersignRule()));
        // 审批动作
        node.setApprovalAction(approvalActionMap.getKey(ruleApprovalNode.getApprovalActions()));

        return node;
    }

    /**
     * 会签规则映射
     *
     * @param value
     * @return
     */
    public static String getCountersignKey(Integer value) {
        if (value == null) {
            return null;
        }

        String key = countersignMap.getKey(value);
        if (key == null) {
            String format = "ApprovalChain.countersignRule(%d) invalid";
            throw new IllegalArgumentException(String.format(format, value));
        }

        return key;
    }

    /**
     * 会签规则映射
     *
     * @param key
     * @return
     */
    public static Integer getCountersignValue(String key) {
        if (key == null) {
            return null;
        }

        Integer value = countersignMap.get(key);
        if (value == null) {
            String format = "WorkflowNode.countersign(%s) invalid";
            throw new IllegalArgumentException(String.format(format, key));
        }

        return value;
    }

}
