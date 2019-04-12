package com.hand.hcf.app.workflow.util;

/**
 *
 * @author houyin.zhang@hand-china.com
 * @date 2018/8/13
 */
public class ExceptionCode implements com.hand.hcf.core.util.RespCode {
    public static final String SYS_PARAM_CANT_BE_NULL="SYS_PARAM_CAN_NOT_BE_NULL";


    public static final String APPROVER_MUST_LT_26="APPROVER_MUST_LESS_THAN_26";

    //表单
    public static final String CUSTOM_FORM_NAME_EXIST="CUSTOM_FORM_NAME_EXIST";



    public static final String SYS_APPROVAL_CHAIN_GET_ERROR="SYS_APPROVAL_CHAIN_GET_ERROR";
    public static final String SYS_APPROVAL_NO_APPROVER="SYS_APPROVAL_NO_APPROVER"; //下一环节无符合条件的审批人，请联系管理员
    public static final String SYS_APPROVAL_CHAIN_IS_NULL="SYS_APPROVAL_CHAIN_IS_NULL";//审批链为空
    public static final String SYS_APPROVAL_CHANGE_APPLICANT_ERROR="SYS_APPROVAL_CHANGE_APPLICANT_ERROR";//替换申请人出错


    public static final String SERVICE_6001 = "6001";   //服务无效
    public static final String STATUS_ERROR_200003 = "200003"; //单据状态错误

    //工作流
    public static final String WORKFLOW_TRANSFER_AUTHORIZATION_REPEAT="WORKFLOW_TRANSFER_AUTHORIZATION_REPEAT";
    public static final String WORKFLOW_TRANSFER_NOT_EXIST="WORKFLOW_TRANSFER_NOT_EXIST";

    //快捷回复
    public static final String QUICK_REPLY_REPLY_MORE_THAN_500 = "QUICK_REPLY_REPLY_MORE_THAN_500";//快捷回复信息大于500!
}