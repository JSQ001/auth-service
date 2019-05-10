package com.hand.hcf.app.workflow.constant;

/**
 *
 * @author houyin.zhang@hand-china.com
 * @date 2018/8/13
 */
public interface LocaleMessageConstants extends com.hand.hcf.app.core.util.RespCode,
        com.hand.hcf.app.workflow.approval.constant.MessageConstants,
        com.hand.hcf.app.workflow.brms.constant.MessageConstants {
    // 错误信息统一要求大写，以模块名开头，系统通用部分统一使用SYS开头，后续拼接带有业务的信息，用’_’下划线分割单词。
    // 备注：brms包用到的多语言写到com.hand.hcf.app.workflow.brms.constant.MessageConstants，
    //       approval包用到的多语言写到com.hand.hcf.app.workflow.approval.constant.MessageConstants。

    /** 没有找到多语言"{0}"！ */
    String NOT_FIND_THE_MESSAGE = "WORKFLOW_NOT_FIND_THE_MESSAGE";

    String SYS_PARAM_CANT_BE_NULL = "SYS_PARAM_CAN_NOT_BE_NULL";

    //
    // 表单
    //
    String CUSTOM_FORM_NAME_EXIST = "CUSTOM_FORM_NAME_EXIST";

    /** 已选审批人超过26个，无法提交 */
    String APPROVER_MUST_LT_26 = "APPROVER_MUST_LESS_THAN_26";

    //
    // 审批
    //
    /** 没有找到单据！ */
    String NOT_FOUND_THE_DOCUMENT = "WORKFLOW_NOT_FOUND_THE_DOCUMENT";

    /** 审批链为空 */
    String SYS_APPROVAL_NO_APPROVER = "SYS_APPROVAL_NO_APPROVER";

    /** 替换申请人出错 */
    String SYS_APPROVAL_CHANGE_APPLICANT_ERROR = "SYS_APPROVAL_CHANGE_APPLICANT_ERROR";

    /** 下一环节无符合条件的审批人，请联系管理员 */
    String SYS_APPROVAL_CHAIN_IS_NULL = "SYS_APPROVAL_CHAIN_IS_NULL";

    /** 快捷回复信息大于500! */
    String QUICK_REPLY_REPLY_MORE_THAN_500 = "QUICK_REPLY_REPLY_MORE_THAN_500";

    /** 只有审批通过的单据才能复核拒绝！ */
    String ONLY_AUDIT_REJECT_PASSED_DOCUMENT = "WORKFLOW_ONLY_AUDIT_REJECT_PASSED_DOCUMENT";

    /** 该接口只能复核拒绝单据！ */
    String THE_API_ONLY_SUPPORT_AUDIT_REJECT = "WORKFLOW_THE_API_ONLY_SUPPORT_AUDIT_REJECT";

    //
    // 代理授权
    //
    String WORKFLOW_TRANSFER_AUTHORIZATION_REPEAT = "WORKFLOW_TRANSFER_AUTHORIZATION_REPEAT";
    String WORKFLOW_TRANSFER_NOT_EXIST = "WORKFLOW_TRANSFER_NOT_EXIST";

    //
    // 监控
    //
    String WORKFLOW_RULEAPPROVALNODE_NOT_EXIST = "WORKFLOW_RULEAPPROVALNODE_NOT_EXIST";
    String WORKFLOW_RULEAPPROVALNODE_NOT_TYPE_ROBOT = "WORKFLOW_RULEAPPROVALNODE_NOT_TYPE_ROBOT";
}
