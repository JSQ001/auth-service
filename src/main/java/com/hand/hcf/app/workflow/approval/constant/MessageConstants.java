package com.hand.hcf.app.workflow.approval.constant;

/**
 * @author mh.z
 * @date 2019/04/07
 */
public class MessageConstants {
    /*
    错误信息统一要求大写，以模块名开头，系统通用部分统一使用SYS开头，后续拼接带有业务的信息，用’_’下划线分割单词
     */

    /*
    审批链
     */
    /** 单据大类未配置审批流或有多条审批流！ */
    public static final String FAIL_TO_GET_FORM_BY_TYPE = "WORKFLOW_FAIL_TO_GET_FORM_BY_TYPE";

    /** 该审批流不允许撤回！ */
    public static final String FORM_RULE_CANNOT_WITHDRAW = "WORKFLOW_FORM_RULE_CANNOT_WITHDRAW";

    /** 没有审批人！ */
    public static final String NODE_EMPTY_NOT_SKIP = "NODE_EMPTY_NOT_SKIP";

    /** 没有审批人！ */
    public static final String CHAIN_NOT_EXISTS_TASK = "CHAIN_NOT_EXISTS_TASK";

    /*
    实例
     */
    /** 不能提交已审批或通过的单据！ */
    public static final String INSTANCE_STATUS_CANNOT_SUBMIT = "INSTANCE_STATUS_CANNOT_SUBMIT";

    /** 只能撤销审批中的单据！*/
    public static final String INSTANCE_STATUS_CANNOT_WITHDRAW = "INSTANCE_STATUS_CANNOT_WITHDRAW";

    /** 可能单据已经被删除！ */
    public static final String NOT_FIND_THE_INSTANCE = "NOT_FIND_THE_INSTANCE";

    /** 该审批流有审批记录无法撤回！ */
    public static final String ONLY_WITHDRAW_NONE_APPROVAL_HISTORY = "WORKFLOW_ONLY_WITHDRAW_NONE_APPROVAL_HISTORY";

    /*
    任务
     */
    /** 可能审批节点已经被其他人通过或驳回！ */
    public static final String NOT_FIND_THE_TASK = "NOT_FIND_THE_TASK";

    /** 只能操作审批中的任务 */
    public static final String TASK_STATUS_CANNOT_OPERATE = "WORKFLOW_TASK_STATUS_CANNOT_OPERATE";

    /** 无需重复审批 */
    public static final String NO_REPEAT_APPROVE_REMARK = "WORKFLOW_NO_REPEAT_APPROVE_REMARK";

    /**
     * 加签
     */
    /** 可能单据已经被通过或驳回！ */
    public static final String INSTANCE_STATUS_CANNOT_ADDSIGN = "WORKFLOW_INSTANCE_STATUS_CANNOT_ADDSIGN";

    /** 该审批节点不允许加签！ */
    public static final String NODE_RULE_CANNOT_ADDSIGN = "WORKFLOW_NODE_RULE_CANNOT_ADDSIGN";

    /** 加签人不能为空！ */
    public static final String ADDSIGN_USER_IS_EMPTY = "WORKFLOW_ADDSIGN_USER_IS_EMPTY";

    /** 只能加签给同租户下的用户！ */
    public static final String ADDSIGN_USER_TENANT_DIFFERENT = "WORKFLOW_ADDSIGN_USER_TENANT_DIFFERENT";

    /** 不能加签给自己！ */
    public static final String CANNOT_ADDSIGN_TO_ME = "WORKFLOW_CANNOT_ADDSIGN_TO_ME";

    /** 按顺序节点前加签至 {0} {1} */
    public static final String ADDSIGN_ORDER_APPROVE_BEFORE = "WORKFLOW_ADDSIGN_ORDER_APPROVE_BEFORE";

    /** 按顺序节点后加签至 {0} {1} */
    public static final String ADDSIGN_ORDER_APPROVE_AFTER = "WORKFLOW_ADDSIGN_ORDER_APPROVE_AFTER";

    /** 平行审批节点前加签至 {0} {1} */
    public static final String ADDSIGN_PARALLEL_APPROVE_BEFORE = "WORKFLOW_ADDSIGN_PARALLEL_APPROVE_BEFORE";

    /** 平行审批节点后加签至 {0} {1} */
    public static final String ADDSIGN_PARALLEL_APPROVE_AFTER = "WORKFLOW_ADDSIGN_PARALLEL_APPROVE_AFTER";

    /** 平行审批平行于节点至 {0} {1} */
    public static final String ADDSIGN_PARALLEL_APPROVE_PARALLEL = "WORKFLOW_ADDSIGN_PARALLEL_APPROVE_PARALLEL";

    /**
     * 转交
     */
    /** 历史转交信息 */
    public static final String DELIVER_REMARK = "WORKFLOW_DELIVER_REMARK";

    /**
     * 跳转
     */
    public static final String JUMP_REMARK = "WORKFLOW_JUMP_REMARK";
}
