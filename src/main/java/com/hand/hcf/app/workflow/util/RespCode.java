package com.hand.hcf.app.workflow.util;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 */
public interface RespCode extends com.hand.hcf.app.core.util.RespCode {
    String SUCESS = "0000";// 成功
    String FAILED = "0001";// 失败
    String SYS_PARAM_CANT_BE_NULL="SYS_PARAM_CAN_NOT_BE_NULL";
    String SYS_SUCCESS = "SYS_SUCCESS";
    String ROLE_CODE_NULL = "10010";//角色代码不允许为空
    String ROLE_NAME_NULL = "10011";// 角色名称不允许为空
    String CODE_NOT_UNION_IN_TENANT = "10012";// 当前租户已存在该角色代码
    String SYS_ID_NULL = "SYS_ID_NULL"; //id 不应该为空!
    String SYS_ID_NOT_NULL = "SYS_ID_NOT_NULL"; //ID 应该为空
    String SYS_DATASOURCE_CANNOT_FIND_OBJECT = "SYS_DATASOURCE_CANNOT_FIND_OBJECT"; //数据库不存在
    String SYS_READ_FILE_ERROR = "SYS_READ_FILE_ERROR"; // 读取文件异常 to

    String MENU_CODE_NULL = "10020";//菜单代码不允许为空
    String MENU_NAME_NULL = "10021";// 菜单名称不允许为空
    String CODE_NOT_UNION = "10022";// 菜单代码已经存在
    String HAVING_CHILD_MENU = "10023";// 菜单代码已经存在
    String MENU_FUNCTION_PARENT_MUST_BE_CATALOG = "10024";// 功能的上级菜单必须是目录
    String MENU_PARENT_CATALOG_ERROR = "10025";//功能只能添加到最底级的目录
    String ROOT_CATALOG_MUST_BE_CATALOG = "10026";//功能只能添加到最底级的目录
    String HAS_CHILD_CATALOG_CAN_NOT_BE_FUNCTION = "10027";//当前菜单存在子目录，不允许由目录变更为功能

    String USER_ROLE_EXISTS = "10031"; // 用户角色组合已经存在
    String ROLE_MENU_EXISTS = "10041"; // 角色菜单组合已经存在

    String BUTTON_CODE_NULL = "10050";//按钮代码不允许为空
    String BUTTON_CODE_NOT_UNION = "10051";// 按钮代码在菜单中已经存在
    String BUTTON_NAME_NULL = "10052";//按钮名称不允许为空

    String ROLE_MENU_BUTTON_EXISTS = "10061"; // 角色菜单按钮组合已经存在

    String COMPONENT_NAME_NULL = "10071";// 组件名称不允许为空
    String COMPONENT_TYPE_INVALID = "10072";// 组件类型值无效,只能为1或2.

    String MODULE_CODE_NULL = "10081";// 模块代码不允许为空
    String MODULE_NAME_NULL = "10082";// 模块名称不允许为空
    String MODULE_CODE_NOT_UNION = "10083";// 模块代码已经存在

    String AUTH_FRONT_KEY_NULL = "AUTH_FRONT_KEY_NULL";// Key不允许为空
    String AUTH_MODULE_ID_NULL = "AUTH_MODULE_ID_NULL";// 必须关联模块
    String AUTH_FRONT_KEY_NOT_UNION = "AUTH_FRONT_KEY_NOT_UNION";// KEY必须唯一

    String REQUEST_CODE_NULL = "10110";// 请求名称不允许为空
    String REQUEST_INTERFACE_NULL = "10111";// 请求必须关联接口

    String RESPONSE_CODE_NULL = "10120";// 响应名称不允许为空
    String RESPONSE_INTERFACE_NULL = "10121";// 响应必须关联接口

    String LANGUAGE_CODE_NULL = "10131";// 语言代码不允许为空
    String LANGUAGE_NAME_NULL = "10132";// 语言名称不允许为空
    String LANGUAGE_CODE_NOT_UNION = "10133";// 语言代码已经存在

    String ERROR_CODE_NULL = "10141";// 报错代码不允许为空
    String ERROR_MESSAGE_NULL = "10142";// 报错信息不允许为空

    String APPROVER_MUST_LT_26="APPROVER_MUST_LESS_THAN_26";

    String MOBILE_VALIDATE_DUPLICATED_CODE = "220001";
    String MOBILE_VALIDATE_FORMAT_ERROR = "220002";
    String UNKNOWN_ERROR="6047018";//其他未知错误

    String CODE_CANT_CHINESE="6057001";//编码不能包含汉字
    String CODE_CANT_ILLEGAL="6057002";//编码不能包含非法字符
    String CODE_LENGTH_LT_36="6057003";//编码长度不能超过36位
    String CODE_CANT_MODIFIED="6057004";//编码不允许修改


    String TENANT_NOT_EXIST = "6017001"; //租户不存在
    String TENANT_6017002 = "6017002"; //租户已经存在
    String TENANT_6017003 = "6017003";
    String TENANT_6017004 = "6017004";
    String TENANT_NAME_NULL = "6017005"; //租户名称不能为空
    String TENANT_6017006 = "6017006";
    String TENANT_PROTOCOL_TITLE_TO0_LONG = "6060001";//自定义协议标题不能为空或超过50个字符
    String TENANT_PROTOCOL_CONTENT_TO0_LONG = "6060002";//自定义协议内容不能为空或超过10000个字符
    String TENANT_MULTI_PROTOCOL = "6060003"; //一个租户包含多个自定义协议
    String COMPANY_NOT_EXIST = "121301";  //公司不存在

    String LEGAL_ENTITY_NULL = "120301";     //法人为空
    String PARENT_LEGAL_ENTITY_NULL = "120304";    //父级法人为空
    String LEGAL_ENTITY_NAME_NULL = "120305";   //法人名称为空
    String TAX_PAYER_NUMBER_NULL = "120306";    //纳税人识别号为空
    String LEGAL_ENTITY_ACCOUNT_NUMBER_EXIST = "120309";//法人实体银行卡号已存在
    /**
     * 账户操作
     */
    String ACCOUNT_6052001 = "6052001";
    String ACCOUNT_6052002 = "6052002";
    String ACCOUNT_6052003 = "6052003";
    String ACCOUNT_6052004 = "6052004";
    String ACCOUNT_6052005 = "6052005";
    String ACCOUNT_6052006 = "6052006";
    String ACCOUNT_6052007 = "6052007";
    String ACCOUNT_6052008 = "6052008";
    String ACCOUNT_6052009 = "6052009";
    String ACCOUNT_6052010 = "6052010";
    String ACCOUNT_6052011 = "6052011";
    String ACCOUNT_6052012 = "6052012";
    String ACCOUNT_6052013 = "6052013";
    String ACCOUNT_6052014 = "6052014";
    String ACCOUNT_6052015 = "6052015";
    String ACCOUNT_6052016 = "6052016";
    String ACCOUNT_6052017 = "6052017";
    String ACCOUNT_6052018 = "6052018";
    String ACCOUNT_6052019 = "6052019";

    String AUTHORITY_6031001 = "6031001";
    String AUTHORITY_6031002 = "6031002";
    String SYS_USER_LANG_IS_NULL="SYS_USER_LANG_IS_NULL";//用户语言不能为空
    String USER_6037001 = "6037001";
    String USER_NOT_EXIST = "6037002";
    String USER_NOT_ACTIVATE = "6037003";//用户未激活
    String USER_6037004 = "6037004";
    String USER_6037005 = "6037005";
    String USER_OID_NOT_NULL="6045004";//员工OID不能为空
    String EMAIL_IS_NULL = "6047007";//邮箱为空
    String EMPLOYEE_EXISTS = "120201";
    String EMPLOYEE_ID_EXISTS = "EMPLOYEE_ID_EXISTS"; //员工ID已存在

    String USER_GROUP_OID_NOT_NULL = "6043001";//创建人员组OID不为空
    String USER_GROUP_6043002 = "6043002";
    String USER_GROUP_CODE_LT_36 = "6043003";//人员组代码长度不能超过36位
    String USER_GROUP_6043004 = "6043004";
    String USER_GROUP_CODE_EXIST = "6043005";
    String USER_GROUP_CONDITION_NOSEQ = "6043006";//人员组规则更新没有序号
    String USER_GROUP_MULTI_CONDITION = "6043007";
    String USER_GROUP_EXIST = "6043008";//人员组已经存在
    String USER_GROUP_NOT_EXIST = "6043009";//人员组不存在

    String USER_GROUP_CONDITION_EXIST = "121508";//人员组条件属性已存在，勿重复添加
    String USER_GROUP_NAME_LENGTH_LT_50 = "121509";//人员组名称长度不能超过50位
    String USER_GROUP_DESC_LENGTH_LT_100 = "121510";//人员组描述长度不能超过100位
    String USER_GROUP_USER_NOT_NULL = "121511";//人员组添加员工不能为空
    String USER_GROUP_CODE_NOT_MODIFIED = "121512";//人员组代码不可修改
    String USER_GROUP_PERMISSSION_NOT_EXIST = "6039002";//人员权限组不存在


    String CARD_TYPE_NOT_NULL = "sys_card_type_not_null";//证件类型不能为空
    String CARD_LAST_NAME_NOT_NULL = "sys_card_last_name_not_null";//证件姓不能为空
    String CARD_OID_MUST_NULL="sys_card_oid_must_be_null";//证件OID必须为空
    String CARD_OID_NOT_NULL="sys_card_oid_cant_be_null";//证件OID不能为空
    String CARD_FIRST_NAME_NOT_NULL = "250005";//证件名不能为空
    String CARD_EXIST = "250006";//证件已存在
    String CARD_DEFAULT_NOT_DISABLE = "250008";//证件不能设置禁用默认
    String CARD_NATIONAL_NOT_EXIST = "250009";//国籍编码不存在
    String CARD_MUST_HAVE_DEFAULT = "250010";//证件必须有一张默认
    String CARD_NUM_NOT_REPEAT = "250011";//证件号不允许重复
    String CARD_TYPE_INVALID = "6047026";//证件类型不符

    /**
     * 银行
     */
    String BANK_CODE_NOT_NULL = "sys_bank_code_not_null";  //银行代码不能为空
    String BANK_NAME_NOT_NULL = "sys_bank_name_not_null";  //银行名称不能为空
    String BANK_BRANCH_NAME_NOT_NULL = "sys_bank_branch_name_not_null";  //银行支行名称不能为空
    String BANK_CODE_LT_36 = "SYS_BANK_CODE_LESS_THAN_36";  //银行代码长度不能超过36位
    String BANK_IMPORT_ENABLE_ILLEGAL = "BANK_IMPORT_ENABLE_ILLEGAL";//是否启用输入不合法！

    String BANK_CODE_MUST_NUMBER = "SYS_BANK_CODE_MUST_BE_NUMBER";  //银行代码必须是数字
    String BANK_CODE_EXIST = "SYS_BANK_CODE_EXIST";  //银行代码已经存在

    String SWIFT_CODE_LT_36 = "SYS_SWIFT_CODE_LESS_THAN_36";  //Swift代码长度不能超过36位

    String SWIFT_CODE_MUST_NUMBER = "SYS_SWIFT_CODE_MUST_BE_NUMBER";  //Swift代码必须是数字
    String BANK_BRANCH_AND_CODE_EXIST = "SYS_BANK_BRANCH_AND_CODE_EXIST"; //银行分支名称和银行代码已存在


    String BANK_ACC_NO_NOT_NULL = "sys_bank_account_no_not_null";  //银行账户代码不能为空
    String BANK_ACC_NAME_NOT_NULL = "sys_bank_account_name_not_null";  //银行账户名称不能为空
    String BANK_ACC_NO_USED = "sys_bank_account_no_used"; //银行账号已被占用

    String BANK_ACC_NOT_DISABLE_DEFAULT = "sys_bank_account_not_disable_default"; //银行账户不能设置禁用默认
    String BANK_NOT_EXIST = "sys_bank_not_exist";//银行不存在
    String BANK_ACC_MUST_DEFAULT_CARD = "sys_bank_account_must_have_default_card";//银行账户必须有一张卡默认
    /**
     * 账套
     */
    String SETOFBOOKS_18001 = "6018001";
    String SETOFBOOKS_18002 = "6018002";
    String SETOFBOOKS_18003 = "6018003";
    String SETOFBOOKS_CODE_NULL = "6018004";  //账套为空
    String SETOFBOOKS_PERIODSETCODE_NULL = "6018005";
    String SETOFBOOKS_FCURRENCYCODE_NULL = "6018006";
    String SETOFBOOKS_18007 = "6018007";
    String SETOFBOOKS_TENANTID_NULL = "6018008";
    String SETOFBOOKS_ID_NULL = "6018009";
    String SETOFBOOKS_18010 = "6018010";
    String SETOFBOOKS_NAME_NULL = "6018011";
    String SETOFBOOKS_18012 = "6018012";
    String SETOFBOOKS_18013 = "6018013";

    /**
     * 币种汇率
     */
    String CURRENCY_5000 = "CURRENCY_5000";
    String CURRENCY_5001 = "CURRENCY_5001";
    String CURRENCY_5002 = "CURRENCY_5002";
    String CURRENCY_5003 = "CURRENCY_5003";
    String CURRENCY_5004 = "CURRENCY_5004";
    String CURRENCY_5005 = "CURRENCY_5005";
    String CURRENCY_5006 = "CURRENCY_5006";
    String CURRENCY_5007 = "CURRENCY_5007";
    String CURRENCY_5008 = "CURRENCY_5008";
    String CURRENCY_5009 = "CURRENCY_5009";
    String CURRENCY_5010 = "CURRENCY_5010";
    String CURRENCY_5011 = "CURRENCY_5011";
    String CURRENCY_5012 = "CURRENCY_5012";
    String CURRENCY_5013 = "CURRENCY_5013";
    String CURRENCY_5014 = "CURRENCY_5014";
    String CURRENCY_5015 = "CURRENCY_5015";


    /**
     * 公司
     */
    String COMPANY_6030001 = "6030001";
    String COMPANY_6030002 = "6030002";
    String COMPANY_6030003 = "6030003";
    String COMPANY_6030004 = "6030004";
    String COMPANY_6030005 = "6030005";
    String COMPANY_6030006 = "6030006";
    String COMPANY_6030007 = "6030007";
    String COMPANY_6030008 = "6030008";

    String E_121301 = "121301";
    String E_121302 = "121302";
    String E_121303 = "121303";
    String E_121304 = "121304";
    String E_120307 = "120307";


    String COMPANY_BANK_NOT_FOUND_22001 = "6022001";//公司银行账户不存在
    String COMPANY_BANK_CODE_OR_NUM_22002 = "6022002";//公司银行账户账号重复
    String COMPANY_BANK_CODE_OR_NUM_LENGTH_MORE_THEN_LIMIT_22003 = "6022003";//公司银行账户名称或银行账户账号长度超限制
    String COMPANY_ID_NOT_FOUND_22004 = "6022004";
    String COMPANY_GROUP_27001 = "6027001";
    String COMPANY_GROUP_27002 = "6027002";
    String COMPANY_GROUP_27003 = "6027003";
    String COMPANY_GROUP_27005 = "6027005";
    String COMPANY_GROUP_27007 = "6027007";
    String COMPANY_GROUP_27008 = "6027008";
    String COMPANY_GROUP_27009 = "6027009";
    String COMPANY_GROUP_27010 = "6027010";
    String COMPANY_GROUP_27011 = "6027011";
    String COMPANY_GROUP_ASSIGN_28001 = "6028001";
    String COMPANY_GROUP_ASSIGN_28002 = "6028002";
    String COMPANY_GROUP_ASSIGN_28003 = "6028003";
    String COMPANY_GROUP_ASSIGN_28004 = "6028004";
    String COMPANY_GROUP_ASSIGN_28005 = "6028005";
    String COMPANY_GROUP_ASSIGN_28006 = "6028006";
    String COMPANY_GROUP_ASSIGN_28007 = "6028007";
    String COMPANY_GROUP_ASSIGN_28008 = "6028008";
    String COMPANY_GROUP_ASSIGN_28009 = "6028009";
    String COMPANY_GROUP_ASSIGN_28010 = "6028010";
    String COMPANY_GROUP_ASSIGN_28011 = "6028011";
    String COMPANY_LEVEL_CODE_NULL_20001 = "6020001";//公司级别code为null
    String COMPANY_LEVEL_CODE_REPEAT_20002 = "6020002";//公司级别code重复
    String COMPANY_LEVEL_NOT_FOUND_20003 = "6020003";//公司级别不存在
    String COMPANY_LEVEL_CODE_LENGTH_MORE_THEN_LIMIT_0R_NOT_INNEGAL_20004 = "6020004";//公司级别code长度超过限制或字符非法
    String COMPANY_LEVEL_DESCRIPTION_IS_NULL_20005 = "6020005";//公司级别描述为空
    String COMPANY_LEVEL_HAS_BEEN_ENABLED_6020006 = "6020006";//公司级别已被启用
    String COMPANY_BANK_AUTH_EXIT = "31003";

    String PAYMENT_METHOD_NOT_FOUNT_31001 = "31001";//公司银行付款方式没找到
    String PAYMENT_METHOD_EXIT_31002 = "31002";//该银行账户已存在该付款方式
    String PAYMENT_PRIORTY_EXIT_22005 = "6022005";//优先级已经存在
    String PAYMENT_PRIORTY_CONNOT_MODIFY = "6022006";//优先级不可修改
    String PAYMENT_MONEY_31004 = "31004";//金额错误
    String PAYMENT_MONEY_31005 = "31005";//该借款单不是已付款、还款中状态，不能还款
    String PAYMENT_MONEY_31006 = "31006";//该借款单是已还款完成状态,不能继续还款
    String PAYMENT_MONEY_31007 = "31007";//还款单参数错误
    String PAYMENT_MONEY_31008 = "31008";//找不到借款单
    String PAYMENT_MONEY_31009 = "31009";//还款单创建错误
    String UPDATE_SIZE_TOO_BIG = "31010";//只允许更新一条

    String E_120712 = "120712";

    //表单
    String CUSTOM_FORM_NAME_EXIST="CUSTOM_FORM_NAME_EXIST";


    //系统代码
    String SYS_CODE_CODE_IS_NULL = "SYS_CODE_CODE_IS_NULL"; // 系统代码的代码标识不允许为空
    String SYS_CODE_TYPE_NOT_ALLOW_UPDATE = "SYS_CODE_TYPE_NOT_ALLOW_UPDATE"; // 系统代码的类型不允许更新
    String SYS_CODE_NOT_EXISTS = "SYS_CODE_NOT_EXISTS"; // 系统代码不存在
    String SYS_CODE_CODE_IS_EXISTS = "SYS_CODE_CODE_IS_EXISTS"; // 系统代码的代码标识已经存在！
    String SYS_CODE_VALE_CODE_IS_EXISTS = "SYS_CODE_VALE_CODE_IS_EXISTS"; // 系统代码的值代码已经存在

    String REQUIRED_120003 = "120003";
    String LENGTH_120004 = "120004";
    String E_120210 = "120210";
    String E_120002 = "120002";

    String SYS_APPROVAL_CHAIN_GET_ERROR="SYS_APPROVAL_CHAIN_GET_ERROR";
    String SYS_APPROVAL_NO_APPROVER="SYS_APPROVAL_NO_APPROVER"; //下一环节无符合条件的审批人，请联系管理员
    String SYS_APPROVAL_CHAIN_IS_NULL="SYS_APPROVAL_CHAIN_IS_NULL";//审批链为空
    String SYS_APPROVAL_CHANGE_APPLICANT_ERROR="SYS_APPROVAL_CHANGE_APPLICANT_ERROR";//替换申请人出错
    /**
     * 部门
     */
    String ASSOCIATED_USER_DEPARTMENT_FAILED = "7002";    //关联用户部门失败

    String DEPARTMENT_ALREADY_EXISTS = "120101";  //部门已存在
    //    String E_120102 = "120102";  //部门存在子部门或者员工无法禁用
//    String E_120103 = "120103";  //部门不存在
//    String E_120104 = "120104";  //传入部门OID为空
//    String E_120105 = "120105";  //传入部门名称不能为空或大于50个字符
//    String E_120106 = "120106";  //传入部门路径不能为空或大于1000个字符
    String DEPARTMENT_CODE_ALREADY_EXISTS = "120107";  //部门编码已存在
//    String E_120108 = "120108";  //部门编码不能为空
//    String E_120109 = "120109";  //部门编码为空
//    String E_120110 = "120110";  //部门编码不存在
//    String E_120111 = "120111";  //父部门编码不存在
//    String E_120112 = "120112";  //部门不存在:{0}

    String THIS_DEPARTMENT_PATH_ALREADY_EXISTS = "6044001";             //公司已存在此部门路径
    String DEPARTMENT_HAS_SUB_DEPARTMENTS = "6044002";                  //部门存在子部门
    String DEPARTMENT_HAS_EMPLOYEES = "6044003";                        //部门存在员工
    String THIS_DEPARTMENT_NOT_EXIST = "6044004";                        //公司不存在此部门
    String DEPARTMENT_CODES_CONTAINS_ONLY_LETTERS_AND_NUMBERS = "6044005";//部门编码只能包含字母和数字
    String DEPARTMENT_CODE_LENGTH_CANNOT_EXCEED_100_DIGITS = "6044006"; //部门编码长度不能超过100位
    String DEPARTMENT_NAME_EMPTY_OR_MORE_THEN_50_CHARACTERS = "6044007";//部门名不能为空或者超过50个字符
    String MAIN_DEPARTMENT_DOES_NOT_EXIST = "6044008";                   //主部门不存在
    String SUBORDINATE_DEPARTMENT_DOES_NOT_EXIST = "6044009";            //从属部门不存在
    String DEPARTMENT_CODE_REQUIRED = "6044010";                        //部门编码必填

    String DEPARTMENT_NAME_CANNOT_BE_EMPTY_OR_CONTAIN_BAR = "32010";//部门名称不能为空或包含'|'

    String ILLEGAL_DEPARTMENT_NAME = "2006";       //部门名字不合法

    /**
     * 部门组
     */
    String DEPARTMENT_GROUP_NOT_FOUND_23001 = "6023001";//对应的部门组不存在
    String DEPARTMENT_GROUP_DELETED_23002 = "6023002";//对应的部门组已被删除
    String DEPARTMENT_GROUP_CODE_NULL_23003 = "6023003";//部门组代码为空
    String DEPARTMENT_GROUP_CODE_REPEAT_23004 = "6023004";//部门组代码重复
    String DEPARTMENT_GROUP_CODE_LENGTH_MORE_THEN_LIMIT_OR_NOT_INNEGAL_23005 = "6023005";//部门组代码长度超过限制或非法
    String DEPARTMENT_CODE_NULL_23006 = "6023006";//部门代码为空
    String EMP_OID_IS_NULL = "6023007";//员工oid为空
    String DEPARTMENT_GROUP_DESCRIPTION_IS_NULL_23008 = "6023008";
    String DEPARTMENT_CODE_NOT_NULL_23009 = "6023009";//部门代码不能为空

    String DEPARTMENT_GROUP_DETAIL_NULL_21001 = "6021001";//部门组明细为空
    String DEPARTMENT_GROUP_DETAIL_EXIT_21002 = "6021002";//部门组明细已经存在

    /**
     * 部门职位
     */
    String DEPARTMENT_ROLE_NAME_EXISTS = "32001";//部门角色名称已存在
    String DEPARTMENT_ROLE_NAME_NOT_EXISTS = "32002";//部门角色不存在
    String DEPARTMENT_ROLE_CODE_EMPTY = "32003";//部门角色编码不能为空
    String DEPARTMENT_ROLE_CODE_LENGTH_EXCEEDS_LIMIT_OR_ILLEGAL = "32004";//部门角色编码长度超过限制或字符非法（code只能由数字组成，且长度位4-9位）
    String DEPARTMENT_ROLE_CODE_REPEAT = "32005";//部门角色编码重复
    String DEPARTMENT_ROLE_NOT_EXISTS = "32006";//部门角色不存在,租户ID:{0},部门角色编码:{1}
    String DEPARTMENT_ROLE_CODE_CANNOT_EXCEED_13 = "32007";//部门角色编码不能超过13个
    String WRONG_START_CODE = "32008";//0,1和6开头编码为系统保留，请使用其他数字
    String DEPARTMENT_ROLE_NAME_EMPTY = "32009";//部门角色名称不能为空

    String READ_FILE_FAILED = "7005";    //读取文件失败

    /**
     * 编码规则
     */
    String ID_NOT_ALLOWED_21001 = "21001";   //创建数据不允许有ID
    String ID_REQUIRED_21002 = "21002";   //更新数据ID必填
    String BUDGET_CODING_RULE_DETAIL_OPERATION = "6013022";//启用中的编码规则下的明细不能操作编码规则定义无应用公司时的固定字符值必须唯一!
    String BUDGET_CODING_RULE_IS_USED = "6013005";//编码规则已被引用!
    String BUDGET_CODING_RULE_OBJECT_CODE_NOT_UNIQUE = "6013001";//同一租户下的单据类别、单据类型、公司代码的组合只能有一个!
    String BUDGET_CODING_RULE_ONE_ENABLED = "6013003";//只能有一条启用的编码规则!
    String BUDGET_CODING_RULE_CODE_NOT_UNIQUE = "6013004";//同一编码规则定义下的编码规则代码不能重复!
    String BUDGET_CODING_RULE_ENABLED_EXCEPTION = "6013006";//编码规则启用多个或没有!
    String BUDGET_CODING_RULE_DETAIL_SEQUENCE_NOT_UNIQUE = "6013007";//同一编码规则下的序号不唯一!
    String BUDGET_CODING_RULE_DETAIL_NOT_FOUND = "6013008";//该编码规则下无编码规则明细！
    String BUDGET_CODING_RULE_DETAIL_SEQUENCE_NOT_FOUND = "6013012";//规则明细中不存在序列号，请先添加!
    String BUDGET_CODING_NOT_FOUND = "6013016";//该数据不存在
    String BUDGET_CODING_RULE_DETAIL_DATE_FORMAT_NOT_FOUND = "6013017";//该重置频率下编码规则明细必须有日期格式!
    String BUDGET_CODING_RULE_DETAIL_SEGMENT_TYPE_NOT_UNIQUE = "6013018";//同一编码规则下的参数名称不能重复!
    String BUDGET_CODING_RULE_DETAIL_SEGMENT_TYPE_40 = "6013019";//编码规则定义有应用公司必须建立一条公司代码明细!
    String BUDGET_CODING_RULE_DETAIL_SYNTHESIS_NOT_UNIQUE = "6013021";//当前租户下该编码规则生成出来的编号与其它编号重复!
    String BUDGET_CODING_RULE_DETAILSYNTHESIS = "6013023";//预期生成出来的编码规则号过长，请修改!
    String BUDGET_CODING_RULE_DETAIL_COMPANY_CODE = "6013024";//编码规则定义无应用公司时不能使用公司代码！
    String BUDGET_CODING_RULE_DETAIL_DATE_FORMAT_YEAR = "6013025";//为防止单号重复,每年频率下日期格式必须有年！
    String BUDGET_CODING_RULE_DETAIL_DATE_FORMAT_MONTH = "6013026";//为防止单号重复,每月频率下日期格式必须有年加月！
    String BUDGET_CODING_RULE_VALUE_COMPANY_CODE_NOT_FOUND = "6013014";//公司代码不能为空!
    String BUDGET_CODING_RULE_VALUE_DOCUMENT_TYPE_NOT_FOUND = "6013015";//单据类型代码不能为空!
    String BUDGET_CODING_RULE_OPERATION_DATE_FORMAT_EXCEPTION = "6013009";//日期格式为yyy-MM-dd!
    String BUDGET_CODING_RULE_OBJECT_NOT_ENABLED = "6013002";//没有启用的编码规则定义!
    String BUDGET_CODING_RULE_DATE_FORMAT_EXCEPTION = "6013010";//规则明细中日期参数格式不正确！
    String BUDGET_CODING_RULE_CURRENT_VALUE_OVERFLOW = "6013011";//序列号位数溢出!
    String BUDGET_CODING_RULE_ORDER_NUMBER_LENGTH_NO_MORE_THAN_30 = "6013013";//单据编号不能超过30!

    String DataFilteringUtil_29001 = "6029001";
    String DataFilteringUtil_29002 = "6029002";
    String DataFilteringUtil_29003 = "6029003";
    String DataFilteringUtil_29004 = "6029004";

    /**
     * 会计期间
     */
    String PERIOD_CODE_REPEAT = "6024001";  //一个租户下，会计期代码重复 ！
    String TENANT_ID_CANNOT_BE_EMPTY = "6024002";  //租户id 不能为空 ！
    String TOTAL_NUMBER_SHOULD_MORE_THEN_12_AND_LESS_THEN_20 = "6024003";  //期间总数应该大于等于12小于等于20！
    String PERIOD_NAME_ATTACHED_CANNOT_BE_EMPTY = "6024004";  //期间期间名称附加不能为空！

    String ADJUSTMENT_PERIOD_MAINTENANCE_FORMAT_ERROR = "6025001"; //调整期维护格式错误，请检查！
    String MONTHS_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_12 = "6025002"; //月份应小于等于12!
    String QUARTER_SHOULD_BE_LESS_THAN_OR_EQUAL_TO_4 = "6025003"; //基础应该小于等于4!
    String THE_DATE_SHOULD_BE_LESS_THAN_31 = "6025004"; //{0}月的日期从应该小于31 ！
    String THE_DATE_SHOULD_BE_LESS_THAN_30 = "6025005"; //{0}月的日期从应该小于30 ！
    String THE_DATE_SHOULD_BE_LESS_THAN_29 = "6025006"; //{0}月的日期从应该小于29 ！
    String MAINTENANCE_IS_INCOMPLETE = "6025007"; //非调整期间汇总数<12条，期间维护不完整 ！
    String PERIOD_ADDITIONAL_NAME_IS_EMPTY = "6025008"; //期间名附加不能为空!
    String PERIOD_ADDITIONAL_NAME_IS_TOO_LONG = "6025009"; //期间名附加长度超出预定范围!
    String ACCOUNTING_PERIOD_IS_EMPTY = "6025010"; //会计期获取为空！
    String TIME_NAME_ADDITION_CANNOT_BE_EMPTY = "6025011"; //期间名称附加不能为空 ！
    String MONTH_FROM_CANT_BE_EMPTY = "6025012"; //月份从不能为空！
    String MONTH_TO_CANT_BE_EMPTY = "6025013"; //月份至不能为空！
    String DATE_FROM_CANT_BE_EMPTY = "6025014"; //日期从不能为空 ！
    String DATE_TO_CANT_BE_EMPTY = "6025015"; //日期至不能为空！
    String QUARTER_CANT_BE_EMPTY = "6025016"; //季度不能为空！
    String DATE_MAINTENANCE_IS_INCOMPLETE = "6025017"; //日期维护不完整，请调整！

    String LAST_PERIOD_UNOPENED = "6026001"; //当前期间的上一期间未被打开，请先打开上一期间！
    String NEXT_PERIOD_UNCLOSED = "6026002"; //请先打开当前期间的后一个期间，否则当前期间无法重新打开！
    String CLOSE_THE_PRIOR_PERIOD_FIRST = "6026003"; //请先关闭前一个期间，否则不能关闭当前期间！
    String YEAR_FROM_SHOULD_BE_LESS_THEN_YEAR_TO = "6026004"; //年度从的时间应该小于或等于年度到！
    String ACCOUNTING_ENTITY_DOES_NOT_EXIST = "6026005"; //会计期实体不存在！
    String ACCOUNTING_RULES_DO_NOT_EXIST = "6026006"; //会计期规则不存在!
    String FILLIN_YEAR_REPEAT = "6026007"; //包含已经录入的年度，请重新填写年份！
    String PERIODSETCODE_IS_EMPTY = "6026008"; //会计期代码不能为空
    String Year_IS_EMPTY = "6026009"; //年度不能为空
    String YEAR_FROM_SHOULD_BE_GREATER_THEN_CURRENT_YEAR = "6026010"; //年度从应该大于等于当前年份！
    String YEAR_EXCEEDS_THE_PREDEFINED_TIME_LIMIT_OF_TIME_STAMP = "6026011"; //年度超出时间戳定义年限!

    String OBJECT_NOT_FOUND = "4001";   //对象没找到
    String REQUEST_FREQUENCY_TOO_FAST = "9960";   //请求频率过快!

    //科目表明细
    String ACCOUNTS_ID_NOT_NULL = "6033001";  //科目表明细ID应为空
    String ACCOUNTS_ID_NULL = "6033002";  //科目表ID不能为空
    String ACCOUNTS_CODE_NULL = "6033003";  //科目代码不能为空
    String ACCOUNTS_NAME_NULL = "6033004";  //科目名称不能为空
    String ACCOUNTS_TENANT_ID_NULL = "6033005";  //租户ID不能为空
    String ACCOUNTS_CODE_EXISTS = "6033006";  //当前科目代码已存在
    String ACCOUNTS_NOT_EXISTS = "6033007";  //当前科目不存在
    String ACCOUNTS_NAME_EXISTS = "6033008";  //当前科目名称已存在
    String ACCOUNTS_HAS_SUB_ACCONUT = "6033009";  //不能改变汇总标志,当前科目下存在子科目

    //科目表设置
    String ACCOUNTSET_EXISTS = "6019001";  //当前科目表已存在
    String ACCOUNTSET_NOT_EXISTS = "6019002";  //当前科目表不存在
    String ACCOUNTSET_CODE_EXISTS = "6019003";  //当前科目表代码已存在
    String ACCOUNTSET_CODE_NULL = "6019004";  //科目表代码不能为空
    String ACCOUNTSET_DESC_NULL = "6019005";  //科目表描述不能为空
    String ACCOUNTSET_ID_NOT_NULL = "6019006";  //当前科目表ID应该为空值
    String ACCOUNTSET_ID_NULL = "6019007";  //当前科目表ID不能为空值
    String ACCOUNTSET_TENANT_ID_NULL = "6019008";  //当前科目表租户ID不能为空

    //科目表层级
    String ACCOUNTSHIERARCHY_ID_NOT_NULL = "6034001";  //科目层级ID应为空
    String ACCOUNTSHIERARCHY_PARENT_ID_NULL = "6034002";  //父级科目ID不能为空
    String ACCOUNTSHIERARCHY_SUB_ID_NULL = "6034003";  //子级科目ID不能空
    String ACCOUNTSHIERARCHY_TENANT_ID_NULL = "6034004";  //租户ID不能空
    String ACCOUNTSHIERARCHY_HAS_SAME_SUB_ACCOUNT = "6034005";  //当前父级科目下已存在相同子科目
    String ACCOUNTSHIERARCHY_NOT_EXISTS = "6034006";  //科目层级不存在

    //国际地点组
    String LEVEL_6035001 = "6035001";
    String LEVEL_6035002 = "6035002";
    String LEVEL_6035003 = "6035003";
    String LEVEL_6035004 = "6035004";
    String LEVEL_6035005 = "6035005";
    String LEVEL_6035006 = "6035006";
    String LEVEL_6035007 = "6035007";

    String SERVICE_6001 = "6001";   //服务无效
    String STATUS_ERROR_200003 = "200003"; //单据状态错误


    //维度
    String SYS_FIELD_IS_NULL = "SYS_FIELD_IS_NULL";  //必输字段不允许为空！
    String DIMENSION_CODE_REPEAT = "DIMENSION_CODE_REPEAT";  //账套下维度代码不允许重复！
    String DIMENSION_NAME_REPEAT = "DIMENSION_NAME_REPEAT";  //账套下维度名称不允许重复！
    String DIMENSION_SEQUENCE_REPEAT = "DIMENSION_SEQUENCE_REPEAT";  //账套下维度序号不允许重复！
    String DIMENSION_SEQUENCE_MUST_BETWEEN_1_AND_20 = "DIMENSION_SEQUENCE_MUST_BETWEEN_1_AND_20";  //维度序号必须在1到20之间！
    String DIMENSION_QUANTITY_MORE_THEN_20 = "DIMENSION_QUANTITY_MORE_THEN_20";  //账套下维度数量不能超过20！
    String DIMENSION_ASSIGN_COMPANY_CODE_REPEAT = "DIMENSION_ASSIGN_COMPANY_CODE_REPEAT";  //该公司已经分配，不允许重新分配！
    String DIMENSION_ITEM_CODE_REPEAT = "DIMENSION_ITEM_CODE_REPEAT";  //同一维度下维值代码不允许重复！
    String DIMENSION_ITEM_GROUP_CODE_REPEAT = "DIMENSION_ITEM_GROUP_CODE_REPEAT";  //同一维度下维值组代码不允许重复！
    String DIMENSION_ITEM_GROUP_NAME_REPEAT = "DIMENSION_ITEM_GROUP_NAME_REPEAT";  //同一维度下维值组名称不允许重复！
    String DIMENSION_NOT_EXIST = "DIMENSION_NOT_EXIST"; //维度不存在！
    String DIMENSION_ITEM_COMPANY_NOT_EXIST = "DIMENSION_ITEM_COMPANY_NOT_EXIST"; //维值关联公司不存在！
    String DIMENSION_SETOFBOOKS_NOT_EXIST = "DIMENSION_SETOFBOOKS_NOT_EXIST"; //该账套不存在！
    String DIMENSION_ITEM_NOT_EXIST = "DIMENSION_ITEM_NOT_EXIST"; //维值不存在！

    String TENANT_CONFIG_ID_NOT_NULL = "TENANT_CONFIG_ID_NOT_NULL"; //新建租户配置ID不为空
    String TENANT_CONFIG_ID_NULL = "TENANT_CONFIG_ID_NULL"; //更新租户配置ID为空
    String TENANT_CONFIG_EXISTS = "TENANT_CONFIG_EXISTS"; //租户配置已存在
    String TENANT_NOT_EXISTS = "TENANT_NOT_EXISTS"; //当前租户不存在
    String SETOFBOOKS_NOT_EXIST = "SETOFBOOKS_NOT_EXIST"; //当前账套不存在

    //数据权限
    String AUTH_DATA_AUTHORITY_CITED = "AUTH_DATA_AUTHORITY_CITED";        //该数据权限已被引用，不可删除！
    String AUTH_DATA_AUTHORITY_RULE_EXISTS = "AUTH_DATA_AUTHORITY_RULE_EXISTS";  //同一权限下，规则名称不能重复！
    String AUTH_DATA_AUTHORITY_RULE_DETAIL_VALUE_NONE = "AUTH_DATA_AUTHORITY_RULE_DETAIL_VALUE_NONE";   //数据权限为手工选择时，请至少选择一条数据！
    String AUTH_DATA_AUTHORITY_RULE_DETAIL_EXISTS = "AUTH_DATA_AUTHORITY_RULE_DETAIL_EXISTS";   //数据权限规则数据类型已存在，请勿重复保存！
    String AUTH_DATA_AUTHORITY_EXISTS = "AUTH_DATA_AUTHORITY_EXISTS";   //数据权限已存在，请勿重复保存！

    //数据权限参数配置
    String AUTH_DATA_AUTH_TABLE_PROPERTY_DATA_TYPE_EXISTS = "AUTH_DATA_AUTH_TABLE_PROPERTY_DATA_TYPE_EXISTS";//此参数类型在该表名下已经存在!
    String AUTH_DATA_AUTH_TABLE_PROPERTY_COLUMN_NAME_EXISTS = "AUTH_DATA_AUTH_TABLE_PROPERTY_COLUMN_NAME_EXISTS";//此参数名称在该表名下已经存在!

    //公告信息
    String Carousel_6041001 = "6041001";
    String Carousel_6041002 = "6041002";
    String Carousel_6041003 = "6041003";
    String Carousel_6041004 = "6041004"; //carouselOIDs or companyIDs,RequestParam can't be null

    //责任中心
    String RESPONSIBILITY_CENTER_CONFIGURE_REPEAT="RESPONSIBILITY_CENTER_CONFIGURE_REPEAT"; //此账套配置项重复，请修改配置
    String RESPONSIBILITY_CENTER_NOT_EXIST="RESPONSIBILITY_CENTER_NOT_EXIST"; //"当前责任中心不存在"
    String RESPONSIBILITY_CENTER_GROUP_NOT_EXIST="RESPONSIBILITY_CENTER_GROUP_NOT_EXIST";//"当前责任中心组不存在"
    String RESPONSIBILITY_CENTER_COMPANY_NOT_EXIST="RESPONSIBILITY_CENTER_COMPANY_NOT_EXIST";//责任中心关联公司不存在!
    String RESPONSIBILITY_CENTER_CODE_REPEAT="RESPONSIBILITY_CENTER_CODE_REPEAT";//一个账套下责任中心代码不可重复
    String RESPONSIBILITY_CENTER_GROUP_CODE_REPEAT="RESPONSIBILITY_CENTER_GROUP_CODE_REPEAT";//一个账套下责任中心组代码不可重复

    //参数定义
    String PARAMETER_VALUES_NOT_EXIST="PARAMETER_VALUES_NOT_EXIST";//当前参数值不存在!
    String PARAMETER_SETTING_NOT_EXIST="PARAMETER_SETTING_NOT_EXIST";//当前参数明细不存在!

    //工作流
    String WORKFLOW_TRANSFER_AUTHORIZATION_REPEAT="WORKFLOW_TRANSFER_AUTHORIZATION_REPEAT";
    String WORKFLOW_TRANSFER_NOT_EXIST="WORKFLOW_TRANSFER_NOT_EXIST";

    //快捷回复
    String QUICK_REPLY_REPLY_MORE_THAN_500 = "QUICK_REPLY_REPLY_MORE_THAN_500";//快捷回复信息大于500!
}
