package com.hand.hcf.app.workflow.constant;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hcf.app.workflow.brms.dto.RuleEnumDTO;
import com.hand.hcf.app.workflow.workflow.dto.FormFieldDTO;
import com.hand.hcf.app.workflow.workflow.dto.FormValueDTO;
import com.hand.hcf.app.workflow.workflow.dto.FormValueI18nDTO;
import com.hand.hcf.app.workflow.workflow.enums.ApprovalMode;
import com.hand.hcf.app.workflow.workflow.enums.FieldType;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;

import java.util.*;

public final class RuleConstants {
    //审批规则Rule
    //默认sequence
    public static final int RULE_SEQUENCE_DEFAULT = 10;
    //累加数量
    public static final int RULE_SEQUENCE_INCREMENT = 10;
    //batchCode
    public static final Long RULE_BATCH_CODE_DEFAULT = 1L;
    //累加数量
    public static final Long RULE_BATCH_CODE_INCREMENT = 1L;
    //最大值
    public static final int RULE_SEQUENCE_MAX = 10000;

    //条件类型-表单
    public static final int CONDITION_TYPE_FORM = 10001;
    //条件类型-差标类型
    public static final int CONDITION_TYPE_TRAVEL_STANDARD = 10005;
    //条件类型-转交
    public static final int CONDITION_TYPE_FORM_TRANFORM = 10002;

    //审批者类型
    //人员
    public static final int APPROVAL_TYPE_USER = 6001;
    //成本中心主管
    public static final int APPROVAL_TYPE_COST_CENTER_MANAGER = 6002;
    //单据上的成本中心的主要部门经理审批
    public static final int APPROVAL_TYPE_COST_CENTER_PRIMARY_DEPARTMENT_MANAGER = 6004;
    //人员组
    public static final int APPROVAL_TYPE_USERGROUP = 6003;
    //提交人所在的组织架构审批
    //直属领导
    public static final int APPROVAL_TYPE_APPLICANT_DIRECT_MANAGER = 6100;
    //部门经理
    public static final int APPROVAL_TYPE_DEPARTMENT_MANAGER = 6101;
    // 副经理
    public static final int APPROVAL_TYPE_DEPARTMENT_VICE_MANAGER = 6102;
    //分管领导
    public static final int APPROVAL_TYPE_DEPARTMENT_CHARGE_MANAGER = 6103;
    //部门主管
    public static final int APPROVAL_TYPE_DEPARTMENT_DEPARTMENT_MANAGER = 6104;
    //财务BP
    public static final int APPROVAL_TYPE_DEPARTMENT_FINANCIAL_BP = 6105;
    //财务总监
    public static final int APPROVAL_TYPE_DEPARTMENT_FINANCIAL_DIRECTOR = 6106;
    // 财务经理
    public static final int APPROVAL_TYPE_DEPARTMENT_FINANCIAL_MANAGER = 6107;
    //HRBP
    public static final int APPROVAL_TYPE_DEPARTMENT_HRBP = 6108;
    // 副总裁
    public static final int APPROVAL_TYPE_DEPARTMENT_VICE_PRESIDENT = 6109;
    // 总裁
    public static final int APPROVAL_TYPE_DEPARTMENT_PRESIDENT = 6110;
    // modify by mh.z 20190226 机器人审批人类型的值是1003
    // 机器人
    public static final int APPROVAL_TYPE_DEPARTMENT_ROBOT = 1003;
    // 机器人
    //public static final int APPROVAL_TYPE_DEPARTMENT_ROBOT = 6111;
    // END modify by mh.z

    // 外部接口获取审批人
    public static final int APPROVAL_TYPE_DEPARTMENT_INTERFACE = 1004;

    //机器人对象标识符
    public static  final String APPROVER_ROBOT_NAME ="机器人";
    public static  final String APPROVER_TYPE_ROBOT_OID="00000000-0000-0000-0000-000000000000";
    public static  final String APPROVER_TYPE_ROBOT_NAME="系统审批";
    public static  final String APPROVER_TYPE_ROBOT_NAME_ENGLISH="Approved by system";

    public static  final String APPROVER_ROBOT_PASS_Detail="系统自动通过";
    public static  final String APPROVER_ROBOT_PASS_Detail_ENGLISH="Passed by system automatically";
    public static  final String APPROVER_ROBOT_REJECT_Detail="系统自动驳回";
    public static  final String APPROVER_ROBOT_REJECT_Detail_ENGLISH="Rejected by system automatically";

    public static  final String CANNOT_FIND_CURRENT_APPROVAL="无审批人，请联系管理员";

    /**
     * 机器人节点并且审批结果为驳回
     *
     */
    public static  final Integer ROBOT_NODE_AND_APPROVAL_REJECT_RESULT =1;

    public static  final Integer ROBOT_NODE_AND_APPROVAL_PASS_RESULT=2;

    /**
     *  加签 和 过滤规则常量
     */

    public static final Integer RULE_CONUTERSIGN_ALL = 0;  //所有人审批通过
    public static final Integer RULE_CONUTERSIGN_ANY = 1;  // 一人审批通过
    public static final Integer RULE_SEQUENCE = 2; //顺序审批

    public static final Integer APPROVER_FILTER = 1;     //审批人过滤
    public static final Integer COUNTERSIGN_APPROVER_FILTER = 2;  //加签审批人过滤
    public static final Integer ALL_REPEATED_FILTER = 3;  //所有重复的都过滤
    public static final Integer HISTORY_FILTER = 1;  //比对审批历史过滤
    public static final Integer APPROVAL_CHAIN_FILTER = 2;  //比对审批链过滤

    public static final Integer APPROVER_ADD_SIGN = 1;     //审批人加签
    public static final Integer ADD_SIGN_APPROVER_ADD_SIGN = 2;  //加签审批人再加签

    //表单字段类型-默认
    public static final int CUSTOM_FILED_TYPE_ID_DEFAULT = 100;
    //表单字段类型-管控
    public static final int CUSTOM_FILED_TYPE_ID_CONTROL = 200;

    public static final int FORM_TYPE_TRAVLE_ELEMENT = 4001;//差旅要素

    public static final int FORM_TYPE_CONTACT_ATTACH_INFO = 5001;//个人信息扩展表单

    //排除的表单类型
    public static final List<Integer> excludeFormTypes = new ArrayList();

    //单据所选在的组织架构审批
    //部门经理
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_MANAGER = 6201;
    // 副经理
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_VICE_MANAGER = 6202;
    //分管领导
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_CHARGE_MANAGER = 6203;
    //部门主管
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_DEPARTMENT_MANAGER = 6204;
    //财务BP
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_FINANCIAL_BP = 6205;
    //财务总监
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_FINANCIAL_DIRECTOR = 6206;
    // 财务经理
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_FINANCIAL_MANAGER = 6207;
    //HRBP
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_HRBP = 6208;
    // 副总裁
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_VICE_PRESIDENT = 6209;
    // 总裁
    public static final int APPROVAL_TYPE_BIZ_DEPARTMENT_PRESIDENT = 6210;

    //节点审批操作
    //节点类型  打印节点
    public static final Integer NODE_TYPE_PRINT = 1004;
    //审批通过
    public static final int ACTION_APPROVAL_PASS = 8001;
    //审批驳回
    public static final int ACTION_APPROVAL_REJECT = 8002;
    //转交
    public static final int ACTION_APPROVAL_TRANSFER = 8003;
    //加签
    //前置加签
    public static final int ACTION_COUNTERSIGNED_PRE = 8004;
    //后置加签
    public static final int ACTION_COUNTERSIGNED_SUFF = 8005;
    //同级加签
    public static final int ACTION_COUNTERSIGNED = 8006;

    //为空规则-跳过
    public static final Integer RULE_NULLABLE_SKIP = 2001;
    //为空规则-报错
    public static final Integer RULE_NULLABLE_THROW = 2002;

    /**
     * 多语言与账号语言对应
     * key：账号语言
     * value：多语言
     */
    public static final Map<String,String> LANGUAGE_MAPPING = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("zh_cn","zh_cn");
        put("en_us","en_us");
    }});
    public static final Map<UUID,List<FormValueI18nDTO>> CUSTOM_ROLE_DTO_MAP;


    //金额字段Oid
    public static final UUID DEFAULT_AMOUNT_FIELD_OID = UUID.fromString("fc5ae695-609f-4947-b7e1-5a135b14251e");
    //本币金额字段Oid
    public static final UUID DEFAULT_FUNCTION_AMOUNT_FIELD_OID = UUID.fromString("1607618f-02fe-4269-82ad-57c264839896");
    //费用类型Oid
    public static final UUID DEFAULT_EXPENSE_TYPE_FIELD_OID = UUID.fromString("ab1c1407-7a57-4f3c-974d-83cc75fee9d9");
    //部门层级
    public static final UUID DEFAULT_DEPARTMENT_LEVEL_OID = UUID.fromString("28cb7b2d-b331-48fc-8cf8-455d24799d58");
    //部门路径
    public static final UUID DEFAULT_DEPARTMENT_PATH_OID = UUID.fromString("906f27c5-1604-4b55-893f-e614b237be0e");
    //部门
    public static final UUID DEFAULT_DEPARTMENT_OID = UUID.fromString("401a910e-6ced-4279-9e41-9a44d8050889");
    //部门角色
    public static final UUID DEFAULT_DEPARTMENT_ROLE_OID = UUID.fromString("cddb88db-2219-4f35-8bba-7b8d4ec43c22");
    //管控字段-超差标
    public static final UUID CONTROL_BEYOUND_TRAVEL_STANDARD_OID = UUID.fromString("4f49f1cf-e3a2-4550-8b42-2d23dac18776");
    //管控字段-超预算
    public static final UUID CONTROL_BEYOUND_BUDGET_OID = UUID.fromString("d2abbff4-cde3-43ab-9ed8-f3926b4835ac");
    //管控字段-超额度
    public static final UUID CONTROL_BEYOUND_POSITION_OID = UUID.fromString("e61816a0-e64c-4d71-8fb2-7f47b31fc491");
    //管控字段-超申请
    public static final UUID CONTROL_BEYOUND_APPLICATION_OID = UUID.fromString("316761c7-0e7a-4f01-9203-fe92ebe1bfba");
    //部门字段Oid
    public static final UUID DEFAULT_DEPARTMENT_FIELD_OID = UUID.fromString("48be4384-d986-4f56-9bc7-3d932ddef67d");
    //默认字段-返回前端审批人的fieldOid
    public static final UUID DEFAULT_APPROVAL_FIELD_OID = UUID.fromString("87f979b0-4163-4481-8313-1cda2f844001");
    //默认字段-返回前端申请人的fieldOid
    public static final UUID DEFAULT_APPLICANT_FIELD_OID = UUID.fromString("87f979b0-4163-4481-8313-1cda2f844002");

    public static final String I18N_LANGUAGE="language";
    public static final String I18N_VALUE="value";
    public static final String I18N_FIELDCONTENT="fieldContent";
    public static final String FORM_ID_NEW="100000";
    //申请人公司
    public static final UUID DEFAULT_APPLICANT_COMPANY_OID = UUID.fromString("d2a7e02a-5f09-11e8-9c2d-fa7ae01bbebc");


    //部门类型  1:来源申请人
    public static final Integer DEPARTMENT_TYPE_BY_APPLICANT = 1;

    //部门类型  2:来源单据
    public static final Integer DEPARTMENT_TYPE_BY_BILLS = 2;
    /**
     * 操作符
     * > >=  <  <=  ==  !=  contains / not contains / memberOf / not memberOf /matches/ not matches/range
     */
    //大于
    public static final int SYMBOL_GT = 9001;
    //大于等于
    public static final int SYMBOL_GT_OR_EQ = 9002;
    //小于
    public static final int SYMBOL_LT = 9003;
    //小于等于
    public static final int SYMBOL_LT_OR_EQ = 9004;
    //等于
    public static final int SYMBOL_EQ = 9005;
    //不等于
    public static final int SYMBOL_NOT_EQ = 9006;
    //包含
    public static final int SYMBOL_CONTAINS = 9007;
    //不包含
    public static final int SYMBOL_NOT_CONTAINS = 9008;
    //in
    public static final int SYMBOL_MEMBEROF = 9009;
    //not in
    public static final int SYMBOL_NOT_MEMBEROF = 9010;
    //range
    public static final int SYMBOL_RANGE = 9011;
    //为true
    public static final int SYMBOL_IS_TRUE = 9012;
    //为false
    public static final int SYMBOL_IS_FALSE = 9013;
    //in
    @Deprecated
    public static final int SYMBOL_IN = 9014;
    //is Null
    public static final int SYMBOL_IS_NULL = 9015;
    //is not null
    public static final int SYMBOL_IS_NOT_NULL = 9016;

    //默认字段-金额
    public static FormFieldDTO DEFAULT_AMOUNT_FIELD = null;
    public static FormValueDTO DEFAULT_AMOUNT_VALUE = null;
    //默认字段-本币金额
    public static FormFieldDTO DEFAULT_FUNCTION_AMOUNT_FIELD = null;
    public static FormValueDTO DEFAULT_FUNCTION_AMOUNT_VALUE = null;
    //默认字段-费用类型
    public static FormFieldDTO DEFAULT_EXPENSE_TYPE_FIELD = null;
    public static FormValueDTO DEFAULT_EXPENSE_TYPE_VALUE = null;
    //默认字段-部门层级
    public static FormFieldDTO DEFAULT_DEPARTMENT_LEVEL_FIELD = null;
    public static FormValueDTO DEFAULT_DEPARTMENT_LEVEL_VALUE = null;
    //默认字段-部门路径
    public static FormFieldDTO DEFAULT_DEPARTMENT_PATH_FIELD = null;
    public static FormValueDTO DEFAULT_DEPARTMENT_PATH_VALUE = null;
    //默认字段-部门角色
    public static FormFieldDTO DEFAULT_DEPARTMENT_ROLE_FIELD = null;
    public static FormValueDTO DEFAULT_DEPARTMENT_ROLE_VALUE = null;
    //管控字段-超差标,报销单中任一费用为warning
    public static FormFieldDTO CONTROL_BEYOUND_TRAVEL_STANDARD_FIELD = null;
    public static FormValueDTO CONTROL_BEYOUND_TRAVEL_STANDARD_VALUE = null;
    //管控字段-超预算
    public static FormFieldDTO CONTROL_BEYOUND_BUDGET_FIELD = null;
    public static FormValueDTO CONTROL_BEYOUND_BUDGET_VALUE = null;
    //管控字段-超申请,报销单金额超过申请单限额
    public static FormFieldDTO CONTROL_BEYOUND_APPLICATION_FIELD = null;
    public static FormValueDTO CONTROL_BEYOUND_APPLICATION_VALUE = null;

    //map
    //审批类型
    public static final Map<RuleEnumDTO, List<RuleEnumDTO>> approvalTypes = new HashMap<>();
    //审批模式
    public static final List<RuleEnumDTO> approvalModes = new ArrayList<>();
    //操作符
    public static final List<RuleEnumDTO> symbols = new ArrayList<>();
    //操作
    public static final List<RuleEnumDTO> actions = new ArrayList<>();
    //字段组
    public static final Map<Integer, Integer> fieldTypeGroups = new HashMap<>();
    //排除的字段
    public static final List<String> excludeFieldKeys = new ArrayList();
    //白名单字段（不排除）
    public static final List<String> showFieldKeys = new ArrayList();
    //默认的字段
    public static final List<String> defaultFieldKeys = new ArrayList();
    //管控字段
    public static final List<String> controlFieldKeys = new ArrayList();

    //管控字段-超额度
    public static FormFieldDTO CONTROL_BEYOUND_POSITION_FIELD = null;
    public static FormValueDTO CONTROL_BEYOUND_POSITION_VALUE = null;
    //默认字段-部门
    public static FormFieldDTO DEFAULT_DEPARTMENT_FIELD = null;
    public static FormValueDTO DEFAULT_DEPARTMENT_VALUE = null;
    //默认字段-申请人公司
    public static FormFieldDTO DEFAULT_APPLICANT_COMPANY_FIELD = null;
    public static FormValueDTO DEFAULT_APPLICANT_COMPANY_VALUE = null;

    //默认字段-备注
    public static FormFieldDTO DEFAULT_REMARK_FIELD = null;
    public static FormValueDTO DEFAULT_REMARK_VALUE = null;

    //备注
    public static final UUID DEFAULT_REMARK_FIELD_OID = UUID.fromString("24b45c67-b197-4284-a0b1-951bdaeb3c68");
    //默认字段-币种
    public static FormFieldDTO DEFAULT_CURRENCY_FIELD = null;
    public static FormValueDTO DEFAULT_CURRENCY_VALUE = null;
    public static final UUID DEFAULT_CURRENCY_FIELD_OID = UUID.fromString("6eb7f095-727d-44fe-9f09-f516794e3bf1");
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_CURRENCY = "currency_code";

    //单据部门 20181211
    public static FormFieldDTO DEFAULT_DOCUMENT_DEPARTMENT_FIELD = null;
    public static FormValueDTO DEFAULT_DOCUMENT_DEPARTMENT_VALUE = null;
    public static final UUID DEFAULT_DOCUMENT_DEPARTMENT_FIELD_OID = UUID.fromString("192b0f1c-6123-450a-b2e8-270f1f37b6ac");
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_DOCUMENT_DEPARTMENT = "select_department";

    //单据公司 20181211
    public static FormFieldDTO DEFAULT_DOCUMENT_COMPANY_FIELD = null;
    public static FormValueDTO DEFAULT_DOCUMENT_COMPANY_VALUE = null;
    public static final UUID DEFAULT_DOCUMENT_COMPANY_FIELD_OID = UUID.fromString("293b0f1c-6123-450a-b2e8-270f1f37b7ac");
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_DOCUMENT_COMPANY = "select_company";
    public static final RuleEnumDTO directManagerEnumDTO = new RuleEnumDTO(APPROVAL_TYPE_APPLICANT_DIRECT_MANAGER,"直属领导","");
    public static final RuleEnumDTO directManagerEnumDTOEnglish = new RuleEnumDTO(APPROVAL_TYPE_APPLICANT_DIRECT_MANAGER,"Direct Manager","");
    //不能有打印节点的表单类型
    public static List<Integer> getNotPrintCustomFormType = new ArrayList<>();
    //不能修改核定金额的表单类型
    public static List<Integer> getNotUpdateCustomFormType = new ArrayList<>();
    //金额
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_AMOUNT = "default_amount";
    //本币金额
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_FUNCTION_AMOUNT = "default_function_amount";
    //部门
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT = "default_user_department";
    //费用类型
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_EXPENSETYPE = "default_expense_type";
    //法人实体
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_CORPORATION = "select_corporation_entity";
    //供应商
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SUPPLIER = "select_air_ticket_supplier";
    //币种
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_CURRENCY_CODE = "currency_code";
    //图片
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_IMAGE = "image";
    //附件
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_ATTACHMENT = "attachment";
    //费用分摊
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_APPORTIONMENT = "exp_allocate";
    //预算明细
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_BUDGET_DETAIL = "budget_detail";
    //参与人员
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_PARTICIPANT = "select_participant";
    //外部参与人
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_OUT_PARTICIPANT_NAME = "out_participant_name";
    //选人审批
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_APPROVER = "select_approver";

    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_VAT_INVOICE = "vat_invoice";

    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_DESTINATION = "destination";
    //银行账户
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_CONTACT_BANK_ACCOUNT = "contact_bank_account";
    //预算总金额
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_TOTAL_BUDGET = "total_budget";
    //预算平均金额
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_AVERAGE_BUDGET = "average_budget";
    //是否冲销
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_WRITEOFF_FLAG = "writeoff_flag";
    //选择框
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_BOX = "select_box";
    //联动开关
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_LINKAGE_SWITCH = "linkage_switch";
    //个人信息扩展字段
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_EMPLOYEE_EXPAND = "employee_expand";

    //部门层级
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_LEVEL = "default_department_level";
    //部门路径
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_PATH = "default_department_path";
    //部门角色
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_ROLE = "default_department_role";
    //管控字段-超差标
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_TRAVEL_STANDARD = "control_beyound_travel_standard";
    //管控字段-超预算
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_BUDGET = "control_beyound_budget";
    //管控字段-超申请
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_APPLICATION =  "control_beyound_application";
    //管控字段-超额度
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_POSITION = "control_beyound_position";
    //管控字段-未还款金额
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_UN_REFUND_AMOUNT = "control_un_refund_amount";
    //默认字段-申请人公司
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_APPLICANT_COMPANY = "default_applicant_company";
    //时间
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_COMMON_DATE = "common.date";
    //自定义列表
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_CUST_LIST = "cust_list";
    //时间
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_DATE = "date";
    //数字
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_NUMBER = "number";
    //备注
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_REMARK = "remark";
    //公司
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_COMPANY = "select_company";
    //法人实体
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_CORPORATION_ENTITY = "select_corporation_entity";
    //成本中心
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_COST_CENTER = "select_cost_center";
    //单据部门
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_DOCUMENT_DEPARTMENT = "select_department";
    // 申请人部门
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_DEPARTMENT="default_user_department";
    //开关
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SWITCH = "switch";
    //是由
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_TITLE = "title";
    //多行输入框
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_TEXT_AREA = "text_area";
    //替票
    public static final String CUSTOM_FILED_TYPE_MESSAGE_KEY_SUBSTITUTION_INVOICE = "substitution_invoice";


    static {
        //approvalType
        approvalTypes.put(new RuleEnumDTO(0, "按提交人所在的组织架构审批", ""),
                new ArrayList() {{
                    //部门经理
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_MANAGER, "部门经理", ""));
                    // 副经理
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_VICE_MANAGER, "副经理", ""));
                    //分管领导
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_CHARGE_MANAGER, "分管领导", ""));
                    //部门主管
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_DEPARTMENT_MANAGER, "部门主管", ""));
                    //财务BP
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_FINANCIAL_BP, "财务BP", ""));
                    //财务总监
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_FINANCIAL_DIRECTOR, "财务总监", ""));
                    // 财务经理
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_FINANCIAL_MANAGER, "财务经理", ""));
                    //HRBP
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_HRBP, "HRBP", ""));
                    // 副总裁
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_VICE_PRESIDENT, "副总裁", ""));
                    // 总裁
                    add(new RuleEnumDTO(APPROVAL_TYPE_DEPARTMENT_PRESIDENT, "总裁", ""));
                }});
        approvalTypes.put(new RuleEnumDTO(0, "按单据上的组织架构审批", ""),
                new ArrayList() {{
                    //部门经理
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_MANAGER, "部门经理", ""));
                    // 副经理
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_VICE_MANAGER, "副经理", ""));
                    //分管领导
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_CHARGE_MANAGER, "分管领导", ""));
                    //部门主管
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_DEPARTMENT_MANAGER, "部门主管", ""));
                    //财务BP
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_FINANCIAL_BP, "财务BP", ""));
                    //财务总监
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_FINANCIAL_DIRECTOR, "财务总监", ""));
                    // 财务经理
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_FINANCIAL_MANAGER, "财务经理", ""));
                    //HRBP
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_HRBP, "HRBP", ""));
                    // 副总裁
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_VICE_PRESIDENT, "副总裁", ""));
                    // 总裁
                    add(new RuleEnumDTO(APPROVAL_TYPE_BIZ_DEPARTMENT_PRESIDENT, "总裁", ""));
                }});
        approvalTypes.put(new RuleEnumDTO(0, "按单据上的成本中心主管审批", ""),
                new ArrayList() {{
                    add(new RuleEnumDTO(APPROVAL_TYPE_COST_CENTER_MANAGER, "按单据上的成本中心主管审批", ""));
                }});
        approvalTypes.put(new RuleEnumDTO(0, "指定人员审批", ""),
                new ArrayList() {{
                    add(new RuleEnumDTO(APPROVAL_TYPE_USER, "指定人员审批", ""));
                }});
        approvalTypes.put(new RuleEnumDTO(0, "指定人员组审批", ""),
                new ArrayList() {{
                    add(new RuleEnumDTO(APPROVAL_TYPE_USERGROUP, "指定人员组审批", ""));
                }});

        //action
        actions.add(new RuleEnumDTO(ACTION_APPROVAL_PASS, "审批通过", ""));
        actions.add(new RuleEnumDTO(ACTION_APPROVAL_REJECT, "审批驳回", ""));
        actions.add(new RuleEnumDTO(ACTION_APPROVAL_TRANSFER, "转交", ""));
        actions.add(new RuleEnumDTO(ACTION_COUNTERSIGNED_PRE, "前置加签", ""));
        actions.add(new RuleEnumDTO(ACTION_COUNTERSIGNED_SUFF, "后置加签", ""));
        //actionMap.put(ACTION_COUNTERSIGNED,"同级加签");

        //rulesymbol
        symbols.add(new RuleEnumDTO(SYMBOL_GT, ">", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_GT_OR_EQ, "≥", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_LT, "<", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_LT_OR_EQ, "≤", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_EQ, "=", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_NOT_EQ, "!=", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_CONTAINS, "contains", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_NOT_CONTAINS, "not contains", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_MEMBEROF, "in", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_NOT_MEMBEROF, "not in", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_RANGE, "range", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_IS_TRUE, "isTrue", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_IS_FALSE, "isFalse", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_IS_NULL, "isNull", ""));
        symbols.add(new RuleEnumDTO(SYMBOL_IS_NOT_NULL, "isNotNull", ""));

        //approvalMode
        approvalModes.add(new RuleEnumDTO(ApprovalMode.DEPARTMENT.getId(), "部门经理审批", "由提交人所在部门的所有领导审批"));
        approvalModes.add(new RuleEnumDTO(ApprovalMode.USER_PICK.getId(), "选人审批", "由提交人选择人员依次进行审批"));
        approvalModes.add(new RuleEnumDTO(ApprovalMode.CUSTOM.getId(), "自定义审批", "可配置多样的审批流"));

        //fieldTypeGroup
        fieldTypeGroups.put(FieldType.TEXT.getId(), FieldType.TEXT.getId());
        fieldTypeGroups.put(FieldType.LONG.getId(), FieldType.DOUBLE.getId());
        fieldTypeGroups.put(FieldType.DOUBLE.getId(), FieldType.DOUBLE.getId());
        //fieldTypeGroups.put(FieldType.DATETIME.getID(), FieldType.DATETIME.getID());
        fieldTypeGroups.put(FieldType.DATE.getId(), FieldType.DATE.getId());
        fieldTypeGroups.put(FieldType.CUSTOM_ENUMERATION.getId(), FieldType.CUSTOM_ENUMERATION.getId());
        //fieldTypeGroups.put(FieldType.GPS.getID(), FieldType.GPS.getID());
        fieldTypeGroups.put(FieldType.BOOLEAN.getId(), FieldType.BOOLEAN.getId());


        //默认字段组
        defaultFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_DEPARTMENT);//申请人部门
        defaultFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_EXPENSETYPE);
        defaultFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_LEVEL);
        defaultFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_PATH);
        defaultFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_ROLE);
        defaultFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_APPLICANT_COMPANY);

        //管控字段组
        controlFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_TRAVEL_STANDARD);
        controlFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_BUDGET);
        controlFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_APPLICATION);
        controlFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_UN_REFUND_AMOUNT);
        controlFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_POSITION);

        /**
         * 特殊类型表单
         */
        excludeFormTypes.add(FORM_TYPE_TRAVLE_ELEMENT);
        excludeFormTypes.add(FORM_TYPE_CONTACT_ATTACH_INFO);

        /**
         * 屏蔽字段
         * 先不支持
         * excludeMessageKey
         */

        //excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT);
        //excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_CORPORATION);
        //不支持类型
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_IMAGE);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_ATTACHMENT);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_APPORTIONMENT);
        //json
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_PARTICIPANT);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_BUDGET_DETAIL);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_OUT_PARTICIPANT_NAME);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_APPROVER);
        //excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_AMOUNT);
        //excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SUPPLIER);
        //excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_CURRENCY_CODE);
        //暂不支持类型
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_VAT_INVOICE);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_DESTINATION);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_CONTACT_BANK_ACCOUNT);
        //excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_TOTAL_BUDGET);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_AVERAGE_BUDGET);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_BOX);
        excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_EMPLOYEE_EXPAND);
        //excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_WRITEOFF_FLAG);
        //excludeFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_REMARK);
        excludeFieldKeys.addAll(defaultFieldKeys);
        excludeFieldKeys.addAll(controlFieldKeys);


        /**
         * 白名单字段字段
         * 支持
         * showFieldKeys
         */
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_COMMON_DATE);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_CUST_LIST);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_DATE);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_NUMBER);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_COMPANY);//单据公司
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_DOCUMENT_DEPARTMENT);//20181212添加 单据部门
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_CURRENCY_CODE);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_REMARK);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_CORPORATION_ENTITY);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SELECT_COST_CENTER);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_SWITCH);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_TITLE);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_TOTAL_BUDGET);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_WRITEOFF_FLAG);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_LINKAGE_SWITCH);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_LINKAGE_SWITCH);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_AMOUNT);
        showFieldKeys.add(CUSTOM_FILED_TYPE_MESSAGE_KEY_FUNCTION_AMOUNT);


        //2018-11-24
        DEFAULT_REMARK_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_REMARK_FIELD_OID)
                .fieldName("备注")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_REMARK)
                .fieldType(FieldType.TEXT).build();
        DEFAULT_REMARK_VALUE = FormValueDTO.builder()
                .fieldOid(RuleConstants.DEFAULT_REMARK_FIELD_OID)
                .fieldName("备注")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_REMARK)
                .fieldType(FieldType.TEXT).build();

        DEFAULT_CURRENCY_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_CURRENCY_FIELD_OID)
                .fieldName("币种")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_CURRENCY)
                .fieldType(FieldType.TEXT).build();
        DEFAULT_CURRENCY_VALUE = FormValueDTO.builder()
                .fieldOid(RuleConstants.DEFAULT_CURRENCY_FIELD_OID)
                .fieldName("币种")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_CURRENCY)
                .fieldType(FieldType.TEXT).build();

        DEFAULT_DOCUMENT_COMPANY_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_DOCUMENT_COMPANY_FIELD_OID)
                .fieldName("单据公司")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DOCUMENT_COMPANY)
                .fieldType(FieldType.TEXT).build();
        DEFAULT_DOCUMENT_COMPANY_VALUE = FormValueDTO.builder()
                .fieldOid(RuleConstants.DEFAULT_DOCUMENT_COMPANY_FIELD_OID)
                .fieldName("单据公司")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DOCUMENT_COMPANY)
                .fieldType(FieldType.TEXT).build();

        DEFAULT_AMOUNT_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_AMOUNT_FIELD_OID)
                .fieldName("原币金额")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_AMOUNT)
                .fieldType(FieldType.TEXT).build();
        DEFAULT_AMOUNT_VALUE = FormValueDTO.builder()
                .fieldOid(RuleConstants.DEFAULT_AMOUNT_FIELD_OID)
                .fieldName("原币金额")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_AMOUNT)
                .fieldType(FieldType.TEXT).build();
        DEFAULT_FUNCTION_AMOUNT_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_FUNCTION_AMOUNT_FIELD_OID)
                .fieldName("本币金额")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_FUNCTION_AMOUNT)
                .fieldType(FieldType.TEXT).build();
        DEFAULT_FUNCTION_AMOUNT_VALUE = FormValueDTO.builder()
                .fieldOid(RuleConstants.DEFAULT_FUNCTION_AMOUNT_FIELD_OID)
                .fieldName("本币金额")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_FUNCTION_AMOUNT)
                .fieldType(FieldType.TEXT).build();
        DEFAULT_EXPENSE_TYPE_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_EXPENSE_TYPE_FIELD_OID)
                .fieldName("费用类型")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_EXPENSETYPE)
                .fieldType(FieldType.LIST).build();
        DEFAULT_EXPENSE_TYPE_VALUE = FormValueDTO.builder()
                .fieldOid(RuleConstants.DEFAULT_EXPENSE_TYPE_FIELD_OID)
                .fieldName("费用类型")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_EXPENSETYPE)
                .fieldType(FieldType.LIST).build();
        DEFAULT_DEPARTMENT_LEVEL_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_DEPARTMENT_LEVEL_OID)
                .fieldName("部门层级")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_LEVEL)
                //.dataSource("{\"department.level\":10}")
                .fieldType(FieldType.LONG).build();
        DEFAULT_DEPARTMENT_LEVEL_VALUE = FormValueDTO.builder()
                .fieldOid(RuleConstants.DEFAULT_DEPARTMENT_LEVEL_OID)
                .fieldName("部门层级")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_LEVEL)
                .fieldType(FieldType.LONG).build();
        DEFAULT_DEPARTMENT_PATH_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_DEPARTMENT_PATH_OID)
                .fieldName("部门路径")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_PATH)
                .fieldType(FieldType.TEXT).build();
        DEFAULT_DEPARTMENT_PATH_VALUE = FormValueDTO.builder()
                .fieldOid(DEFAULT_DEPARTMENT_PATH_OID)
                .fieldName("部门路径")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_PATH)
                .fieldType(FieldType.TEXT).build();

        DEFAULT_DEPARTMENT_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_DEPARTMENT_OID)
                .fieldName("申请人部门")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT)
                .fieldType(FieldType.TEXT).build();

        DEFAULT_DEPARTMENT_VALUE = FormValueDTO.builder()
                .fieldOid(DEFAULT_DEPARTMENT_FIELD_OID)
                .fieldName("申请人部门")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT)
                .fieldType(FieldType.TEXT).build();

        DEFAULT_DOCUMENT_DEPARTMENT_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_DOCUMENT_DEPARTMENT_FIELD_OID)
                .fieldName("单据部门")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DOCUMENT_DEPARTMENT)
                .fieldType(FieldType.TEXT).build();
        DEFAULT_DOCUMENT_DEPARTMENT_VALUE = FormValueDTO.builder()
                .fieldOid(DEFAULT_DOCUMENT_DEPARTMENT_FIELD_OID)
                .fieldName("单据部门")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DOCUMENT_DEPARTMENT)
                .fieldType(FieldType.TEXT).build();

        DEFAULT_APPLICANT_COMPANY_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_APPLICANT_COMPANY_OID)
                .fieldName("申请人公司")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_APPLICANT_COMPANY)
                .fieldType(FieldType.LONG).build();
        DEFAULT_APPLICANT_COMPANY_VALUE = FormValueDTO.builder()
                .fieldOid(RuleConstants.DEFAULT_APPLICANT_COMPANY_OID)
                .fieldName("申请人公司")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_APPLICANT_COMPANY)
                .fieldType(FieldType.LONG).build();

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_MANAGER);
        jsonObject.put("name", "部门经理");

        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_VICE_MANAGER);
        jsonObject.put("name", "副经理");
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_CHARGE_MANAGER);
        jsonObject.put("name", "分管领导");
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_DEPARTMENT_MANAGER);
        jsonObject.put("name", "部门主管");
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_FINANCIAL_BP);
        jsonObject.put("name", "财务BP");
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_FINANCIAL_DIRECTOR);
        jsonObject.put("name", "财务总监");
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_FINANCIAL_MANAGER);
        jsonObject.put("name", "财务经理");
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_HRBP);
        jsonObject.put("name", "HRBP");
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_VICE_PRESIDENT);
        jsonObject.put("name", "副总裁");
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("id", RuleConstants.APPROVAL_TYPE_DEPARTMENT_PRESIDENT);
        jsonObject.put("name", "总裁");
        jsonArray.add(jsonObject);

        DEFAULT_DEPARTMENT_ROLE_FIELD = FormFieldDTO.builder()
                .fieldOid(DEFAULT_DEPARTMENT_ROLE_OID)
                .fieldName("部门角色")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_ROLE)
                .fieldContent(jsonArray.toString())
                .fieldType(FieldType.LIST).build();

        DEFAULT_DEPARTMENT_ROLE_VALUE = FormValueDTO.builder()
                .fieldOid(DEFAULT_DEPARTMENT_ROLE_OID)
                .fieldName("部门角色")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_ROLE)
                .fieldContent(jsonArray.toString())
                .fieldType(FieldType.LIST).build();

        CONTROL_BEYOUND_TRAVEL_STANDARD_FIELD = FormFieldDTO.builder()
                .fieldOid(CONTROL_BEYOUND_TRAVEL_STANDARD_OID)
                .fieldName("超差标")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_TRAVEL_STANDARD)
                .fieldType(FieldType.BOOLEAN).build();
        CONTROL_BEYOUND_TRAVEL_STANDARD_VALUE = FormValueDTO.builder()
                .fieldOid(CONTROL_BEYOUND_TRAVEL_STANDARD_OID)
                .fieldName("超差标")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_TRAVEL_STANDARD)
                .fieldType(FieldType.BOOLEAN).build();
        CONTROL_BEYOUND_BUDGET_FIELD = FormFieldDTO.builder()
                .fieldOid(CONTROL_BEYOUND_BUDGET_OID)
                .fieldName("超预算")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_BUDGET)
                .fieldType(FieldType.BOOLEAN).build();
        CONTROL_BEYOUND_BUDGET_VALUE = FormValueDTO.builder()
                .fieldOid(CONTROL_BEYOUND_BUDGET_OID)
                .fieldName("超预算")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_BUDGET)
                .fieldType(FieldType.BOOLEAN).build();
        CONTROL_BEYOUND_APPLICATION_FIELD = FormFieldDTO.builder()
                .fieldOid(CONTROL_BEYOUND_APPLICATION_OID)
                .fieldName("超申请")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_APPLICATION)
                .fieldType(FieldType.BOOLEAN).build();
        CONTROL_BEYOUND_APPLICATION_VALUE = FormValueDTO.builder()
                .fieldOid(CONTROL_BEYOUND_APPLICATION_OID)
                .fieldName("超申请")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_APPLICATION)
                .fieldType(FieldType.BOOLEAN).build();
        CONTROL_BEYOUND_POSITION_FIELD = FormFieldDTO.builder()
                .fieldOid(CONTROL_BEYOUND_POSITION_OID)
                .fieldName("超额度")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_POSITION)
                .fieldType(FieldType.BOOLEAN).build();
        CONTROL_BEYOUND_POSITION_VALUE = FormValueDTO.builder()
                .fieldOid(CONTROL_BEYOUND_POSITION_OID)
                .fieldName("超额度")
                .messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_POSITION)
                .fieldType(FieldType.BOOLEAN).build();
    }
    static {

        Map<UUID,List<FormValueI18nDTO>> CUSTOM_ROLE_DTO_MAP_TEMP = new HashMap<>();
        CUSTOM_ROLE_DTO_MAP_TEMP.put(DEFAULT_APPLICANT_COMPANY_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("申请人公司").fieldOid(DEFAULT_APPLICANT_COMPANY_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_APPLICANT_COMPANY).build());
            add(FormValueI18nDTO.builder().fieldName("Applicant company").fieldOid(DEFAULT_APPLICANT_COMPANY_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_APPLICANT_COMPANY).build());
            add(FormValueI18nDTO.builder().fieldName("申請者所属会社").fieldOid(DEFAULT_APPLICANT_COMPANY_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_APPLICANT_COMPANY).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(DEFAULT_DEPARTMENT_LEVEL_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("部门层级").fieldOid(DEFAULT_DEPARTMENT_LEVEL_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_LEVEL).build());
            add(FormValueI18nDTO.builder().fieldName("Department level").fieldOid(DEFAULT_DEPARTMENT_LEVEL_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_LEVEL).build());
            add(FormValueI18nDTO.builder().fieldName("部門階層").fieldOid(DEFAULT_DEPARTMENT_LEVEL_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_LEVEL).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(DEFAULT_DEPARTMENT_PATH_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("部门路径").fieldOid(DEFAULT_DEPARTMENT_PATH_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_PATH).build());
            add(FormValueI18nDTO.builder().fieldName("Department path").fieldOid(DEFAULT_DEPARTMENT_PATH_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_PATH).build());
            add(FormValueI18nDTO.builder().fieldName("部門パス").fieldOid(DEFAULT_DEPARTMENT_PATH_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_PATH).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(DEFAULT_DEPARTMENT_ROLE_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("部门角色").fieldOid(DEFAULT_DEPARTMENT_ROLE_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_ROLE).build());
            add(FormValueI18nDTO.builder().fieldName("Department role").fieldOid(DEFAULT_DEPARTMENT_ROLE_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_ROLE).build());
            add(FormValueI18nDTO.builder().fieldName("部門ロール").fieldOid(DEFAULT_DEPARTMENT_ROLE_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_DEPARTMENT_ROLE).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(DEFAULT_AMOUNT_FIELD_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("金额").fieldOid(DEFAULT_AMOUNT_FIELD_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_AMOUNT).build());
            add(FormValueI18nDTO.builder().fieldName("Amount").fieldOid(DEFAULT_AMOUNT_FIELD_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_AMOUNT).build());
            add(FormValueI18nDTO.builder().fieldName("金額").fieldOid(DEFAULT_AMOUNT_FIELD_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_AMOUNT).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(DEFAULT_FUNCTION_AMOUNT_FIELD_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("本币金额").fieldOid(DEFAULT_FUNCTION_AMOUNT_FIELD_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_FUNCTION_AMOUNT).build());
            add(FormValueI18nDTO.builder().fieldName("Base Amount").fieldOid(DEFAULT_FUNCTION_AMOUNT_FIELD_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_FUNCTION_AMOUNT).build());
            add(FormValueI18nDTO.builder().fieldName("元貨金額").fieldOid(DEFAULT_FUNCTION_AMOUNT_FIELD_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_FUNCTION_AMOUNT).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(DEFAULT_EXPENSE_TYPE_FIELD_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("费用类型").fieldOid(DEFAULT_EXPENSE_TYPE_FIELD_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_EXPENSETYPE).build());
            add(FormValueI18nDTO.builder().fieldName("Expense Type").fieldOid(DEFAULT_EXPENSE_TYPE_FIELD_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_EXPENSETYPE).build());
            add(FormValueI18nDTO.builder().fieldName("費目").fieldOid(DEFAULT_EXPENSE_TYPE_FIELD_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_EXPENSETYPE).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(CONTROL_BEYOUND_TRAVEL_STANDARD_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("超差标").fieldOid(CONTROL_BEYOUND_TRAVEL_STANDARD_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_TRAVEL_STANDARD).build());
            add(FormValueI18nDTO.builder().fieldName("Exceed travel policy").fieldOid(CONTROL_BEYOUND_TRAVEL_STANDARD_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_TRAVEL_STANDARD).build());
            add(FormValueI18nDTO.builder().fieldName("超出張基準").fieldOid(CONTROL_BEYOUND_TRAVEL_STANDARD_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_TRAVEL_STANDARD).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(CONTROL_BEYOUND_BUDGET_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("超预算").fieldOid(CONTROL_BEYOUND_BUDGET_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_BUDGET).build());
            add(FormValueI18nDTO.builder().fieldName("Exceed budget").fieldOid(CONTROL_BEYOUND_BUDGET_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_BUDGET).build());
            add(FormValueI18nDTO.builder().fieldName("超予算").fieldOid(CONTROL_BEYOUND_BUDGET_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_BUDGET).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(CONTROL_BEYOUND_APPLICATION_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("超申请").fieldOid(CONTROL_BEYOUND_APPLICATION_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_APPLICATION).build());
            add(FormValueI18nDTO.builder().fieldName("Exceed related request").fieldOid(CONTROL_BEYOUND_APPLICATION_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_APPLICATION).build());
            add(FormValueI18nDTO.builder().fieldName("超申請").fieldOid(CONTROL_BEYOUND_APPLICATION_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_APPLICATION).build());
        }});

        CUSTOM_ROLE_DTO_MAP_TEMP.put(CONTROL_BEYOUND_POSITION_OID,new ArrayList<FormValueI18nDTO>(){{add(FormValueI18nDTO.builder().fieldName("超额度").fieldOid(CONTROL_BEYOUND_POSITION_OID).language(LanguageEnum.ZH_CN.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_POSITION).build());
            add(FormValueI18nDTO.builder().fieldName("Exceed related request").fieldOid(CONTROL_BEYOUND_POSITION_OID).language(LanguageEnum.EN_US.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_POSITION).build());
            add(FormValueI18nDTO.builder().fieldName("超閾値").fieldOid(CONTROL_BEYOUND_POSITION_OID).language(LanguageEnum.JA.getKey()).messageKey(RuleConstants.CUSTOM_FILED_TYPE_MESSAGE_KEY_BEYOUND_POSITION).build());
        }});

        CUSTOM_ROLE_DTO_MAP = Collections.unmodifiableMap(CUSTOM_ROLE_DTO_MAP_TEMP);

    }
    private RuleConstants() {
    }
}
