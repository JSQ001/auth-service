package com.hand.hcf.app.workflow.constant;

import java.util.UUID;

public final class WorkflowConstants {


    //是否启用规则审批
    public static final String APPROVAL_RULE_ENABLED = "approval.rule.enabled";

    //是否跳过自审批,自审批规则(5001 跳过,5002 不跳过,5003 上级部门经理审批,5004 部门分管领导审批)
    public static final String APPROVAL_RULE_SELF_SKIP = "approval.rule.self.skip";

    //workflow
    public static final String WORKFLOW_PATH_PREFIX = "/workflow/";
    public static final String WORKFLOW_APPROVAL_SPLIT = ":";
    public static final String WORKFLOW_COST_CENTER_ITEM_OID_KEY = "costCenterItemOid";
    public static final String WORKFLOW_DEPARTMENT_OID_KEY = "departmentOid";
    public static final String WORKFLOW_COMPANY_OID_KEY = "companyOid";
    //workflow reimbursement
    public static final String WORKFLOW_NEXT_APPROVER_KEY = "nextApprover";
    public static final String WORKFLOW_APPLICANT_KEY = "applicant";
    public static final String WORKFLOW_DRAFT_FLAG_KEY = "draftFlag";
    public static final String WORKFLOW_CANCEL_FLAG_KEY = "cancelFlag";
    public static final String WORKFLOW_APPROVAL_FLAG_KEY = "approvalFlag";
    public static final String WORKFLOW_ENABLE_BPO_VERIFICATION_KEY = "enableInvoiceVerification";
    public static final String WORKFLOW_ENABLE_FINANCE_VERIFICATION_KEY = "enableFinanceInvoiceVerification";
    public static final String WORKFLOW_BPO_VERIFICATION_FLAG_KEY = "bpoVerificationFlag";
    public static final String WORKFLOW_FINANCE_VERIFY_FLAG_KEY = "financeVerifyFlag";
    public static final String WORKFLOW_ENABLE_FINANCE_LOAN_KEY = "enableFinanceLoan";
    public static final String WORKFLOW_AUTO_APPROVAL_KEY = "autoApproval";
    public static final String WORKFLOW_WITH_RECEIPET_KEY = "withReceipt";
    public static final String WORKFLOW_POST_TO_BPO_APPROVAL_KEY = "postToBpoApproval";
    public static final String WORKFLOW_POST_TO_FINANCE_LOAN_KEY = "postToFinanceLoan";
    public static final String WORKFLOW_POST_TO_ARCHIVE_KEY = "postToArchive";
    public static final String WORKFLOW_BPO_USER = "bpoUser";
    public static final String WORKFLOW_FINANCE_APPROVAL_USER = "financeApprovalUser";
    public static final String WORKFLOW_FINANCE_LOAN_USER = "financeLoanUser";

    public static final UUID WORKFLOW_IMPLEMENT_USER_OID = UUID.fromString("a7afa6e9-3750-4d17-a569-edb8d8055748");

    public static final String WORKFLOW_EXPENSE_REPORT_PREFIX = "ER";
    public static final String WORKFLOW_TRAVEL_APPLICATION_PREFIX = "TA";
    public static final String WORKFLOW_EXPENSE_APPLICATION_PREFIX = "EA";
    public static final String WORKFLOW_TRAVEL_BOOK_APPLICATION_PREFIX = "BA";
    public static final String WORKFLOW_JINGDONG_ORDER_APPLICATION_PREFIX = "JA";
    public static final String WORKFLOW_LOAN_APPLICATION_PREFIX = "LA";
    public static final String WORKFLOW_TREPAY_APPLICATION_PREFIX = "TR";
    public static final String WORKFLOW_CREPAY_APPLICATION_PREFIX = "CR";
    public static final String WORKFLOW_UNKNOW_PREFIX = "XX";
    //workflow reimbursement depoly V1
    public static final String WORKFLOW_REIMBURSEMENT_PREFIX = "R";
    public static final String WORKFLOW_REIMBURSEMENT_PATH = "/workflow/reimbursement.bpmn";
    public static final String WORKFLOW_REIMBURSEMENT_BUSINESSOBJ_KEY = "reimbursementDomain";
    public static final String WORKFLOW_REIMBURSEMENT_PROCESS_KEY = "reimbursementProcess";

    //workflow reimbursement deploy V2
    public static final String WORKFLOW_REIMBURSEMENT_PATH_V2 = "/workflow/reimbursementProcessV2_4.bpmn";

    //workflow travel depoly
    public static final String WORKFLOW_TRAVEL_PREFIX = "T";
    public static final String WORKFLOW_TRAVEL_PATH = "/workflow/travelProcess.bpmn";
    public static final String WORKFLOW_TRAVEL_BUSINESSOBJ_KEY = "travelDomain";
    public static final String WORKFLOW_TRAVEL_PROCESS_KEY = "travelProcess";
    public static final String WORKFLOW_VERIFY_FLAG_KEY = "verifyFlag";
    public static final String WORKFLOW_TRAVEL_APPLY_TYPE_KEY = "applyType";
    public static final int WORKFLOW_SET_APPROVAL_BEFORE_DAYS = 4;


    //workflow process depoly
    public static final String WORKFLOW_CUSTOM_PROCESS_PREFIX = "P";
    public static final String WORKFLOW_CUSTOM_PROCESS_PATH = "/workflow/customProcessV1_4.bpmn";
    public static final String WORKFLOW_CUSTOM_PROCESS_BUSINESSOBJ_KEY = "customProcessDomain";
    public static final String WORKFLOW_CUSTOM_PROCESS_PROCESS_KEY = "customProcess";
    public static final String WORKFLOW_CUSTOM_PROCESS_APPLY_TYPE_KEY = "applyType";


    public static final String COUNTERSIGN_APPROVER = "countersignApprover"; //受会签规则影响
    public static final String APPROVER = "approver"; //不受会签规则影响
    public static final String APPORTIONMENT_DEPARTMENTS = "apportionmentDepartments"; //分摊的部门
    public static final String APPORTIONMENT_COST_CENT_ITEMS = "apportionmentCostCentItems"; //分摊的成本中心
    public static final String APPROVAL_CHAIN_NOTICE_FLAG = "#";//只通知标记

    /**
     * 审批链长度
     */
    public static final int DEFAULT_MAX_APPROVAL_CHAIN = -1;



    private WorkflowConstants() {
    }
}
