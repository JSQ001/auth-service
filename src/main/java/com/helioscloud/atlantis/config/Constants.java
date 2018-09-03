package com.helioscloud.atlantis.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Application constants.
 */
public final class Constants {

    // Spring profile for development, production and "fast", see http://jhipster.github.io/profiles.html
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String SPRING_PROFILE_PRODUCTION_NODIDI = "prodnodidi";
    public static final String SPRING_PROFILE_PRODUCTION_ATL = "atlprod";

    public static final String SPRING_PROFILE_FAST = "fast";
    // Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
    public static final String SPRING_PROFILE_CLOUD = "cloud";

    public static final String SPRING_PROFILE_UAT = "uat";
    // Spring profile used when deploying to Heroku
    public static final String SPRING_PROFILE_HEROKU = "heroku";

    public static final String SYSTEM_ACCOUNT = "system";
    //batch import
    // 部门分隔符
    public static final String DEPARTMENT_SPLIT = "|";

    public static final String EXTERNAL_PARTICIPANT_SPLIT = ",";

    /**
     * 费用类型分隔符
     */
    public static final String EXPENSE_TYPE_SPLIT = ":";

    //
    public static final int USER_BATCH_INSERT_NUM = 50;

    public static final String DEPARTMENT_MANAGER_FLAG = "是";

    public static final int COMPANY_EXCEL_BASEROW = 3;

    public static final int DEPARTMENT_BUDGET_EXCEL_BASEROW = 2;

    public static final int REIMBURSEMENT_EXCEL_BASEROW = 1;

    public static final int DATA_EXCEL_BASEROW = 2;

    public static final int FLIGHT_EXCEL_BASEROW = 3;

    public static final int EXPENSE_EXCEL_BASEROW = 2;

    public static final String PAID_EXPENSE_TEMPLATE = "/templates/paidExpenseTemplate.xlsx";

    public static final String COMPANY_TEMPLATE = "/templates/companyTemplate.xlsx";

    public static final String TRAVELSTANDARD_TEMPLATE = "/templates/travelStandardTemplate.xlsx";

    public static final String COST_CENTER_TEMPLATE = "/templates/costcenterTemplate.xlsx";
    //导入成本中心项-模板
    public static final String COST_CENTER_ITEM_TEMPLATE = "/templates/HLY-CostCenterItem_DataImportTemplate.xlsx";

    public static final String COMPANY_TEMPLATE_PATH = "/templates/companyTemplate.xlsx";

    public static final String FEEDBACK_TEMPLATE = "/templates/feedbackTemplate.xlsx";

    public static final String REIMBURSEMENT_TEMPLATE = "/templates/reimbursementTemplate.xlsx";

    public static final String CTRIP_FLIGHT_ORDER_TEMPLATE = "/templates/ctripFlightOrderTemplate.xlsx";

    public static final String CTRIP_HOTEL_INVOICE_TEMPLATE = "/templates/ctripHotelInvoiceTemplate.xlsx";

    public static final String CTRIP_TRAIN_INVOICE_TEMPLATE = "/templates/ctriptrainInvoiceTemplate.xlsx";

    public static final String DIDI_INVOICE_TEMPLATE = "/templates/didiInvoiceTemplate.xlsx";

    public static final String COMPANY_USER_WITH_BANK_ACCOUNT_TEMPLATE = "/templates/userWithBankAccountTemplate.xlsx";

    //参与人错误信息导出模板
    public static final String PARTICIPANT_ERROR_EXPORT_TEMPLATE = "/templates/exportParticipantsErrorTemplate.xlsx";

    //参与人导出模板
    public static final String PARTICIPANT_IMPORT_TEMPLATE = "/templates/importParticipantsTemplate.xlsx";

    public static final String REIMBURSEMENT_BATCH_TEMPLATE = "/templates/reimbursementBatch.xlsx";

    //批次借款单导出明细模版
    public static final String REIMBURSEMENT_BATCH_LOAN_APPLICATION_TEMPLATE = "/templates/reimbursement_batch_loan_application.xlsx";

    public static final String NACHA_TEMPLATE = "/templates/nacha.xlsx";

    public static final String BOX_PAY_NACHA_TEMPLATE = "/templates/boxpayNacha.xlsx";

    //借款单报销单联和导出明细模版
    public static final String REIMBURSEMENT_LOAN_APPLICATION_TEMPLATE = "/templates/reimbursement_loan_application.xlsx";
    public static final String DEPARTMENT_BUDGET_TEMPLATE = "/templates/departmentBudgetTemplate.xlsx";

    public static final String TRAVEL_BOOK_TEMPLATE = "/templates/travelbookTemplate.xlsx";

    public static final String TRAVEL_BOOKER_ORDER_TEMPLATE = "/templates/travelBookerOrderTemplate.xlsx";
    public static final String TRAVEL_BOOKER_ORDER_TEMPLATE_EN = "/templates/travelBookerOrderTemplateEN.xlsx";

    public static final String TRAVEL_APPLICATION_TEMPLATE = "/templates/travelapplicationTemplate.xlsx";//导出差旅申请

    public static final String ORDERDETAIL_BILLINGTIME = "/templates/orderDetailBillingTime.xlsx";//订单明细和开票详情模板

    public static final String CLOUD_HELIOS_HOTEL_ORDER_DETAIL = "/templates/cloud_helios_hotel_order_detail.xlsx";//甄选酒店开票订单明细导出excel

    public static final String JD_ORDER_DETAIL_TEMPLATE = "/templates/jdOrderDetailTemplate.xlsx";// 京东开票详情模板
    /**
     * 导出excel 通用空模板
     */
    public static final String EMPTY_EXCEL_TEMPLATE = "/templates/EMPTY.xlsx";

    /**
     * 寄出单据模板
     */
    public static final String SEND_OFF_EXPENSE_REPORT = "/templates/sendOffExpenseReport.xlsx";

    //文泉驿等宽微米黑,开源字体
    public static final String FONT_WQY_DKWMH = "/font/wqy_dkwmh.ttf";
    //宋体
    public static final String SIMSUN = "/font/simsun.ttf";

    //滴滴生成费用参数
    public static final long COMPANY_ID = 2;
    public static final String DIDI_LOGO_OID = "32c3a354-eac4-4c56-8d57-9e278d43b521";
    /**
     * 滴滴供应商费用类型
     */
    public static final String DIDI_EXPENSE_TYPE_OID = "fcf5878d-0857-4c7e-8350-b0faded4fb9e";
    /**
     * 汉得交通费用类型
     */
    public static final String DIDI_TRANSPORT_EXPENSE_TYPE_OID = "d30023be-45d5-4fed-994d-91a2d51d2de5";

    public static final String DIDI_DAIJIA_COMMENT = " (滴滴代驾交通费用需自行向滴滴企业索要发票进行报销!)";

    //携程酒店费用生成参数
    /**
     * 携程酒店
     */
    public static final String CTRIP_HOTEL_EXPENSE_TYPE_OID = "ae8981d2-74e7-11e6-9639-00ffa3fb4c67";
    //携程机票费用生成参数
    /**
     * 携程机票
     */
    public static final String CTRIP_FLIGHT_EXPENSE_TYPE_OID = "a3eebf8e-74e7-11e6-9639-00ffa3fb4c67";
    //携程机票费用生成参数
    //不存在的oid
    @Deprecated
    public static final String CTRIP_TRAIN_EXPENSE_TYPE_OID = "aaec0e65-bce2-46fc-9378-fcbfe15fbb0c";

    // Fasco 机票费用生成参数
    /**
     * Fasco 机票费用 暂不推送费用
     */
    public static final String FASCO_FLIGHT_EXPENSE_TYPE_OID = "4ed60234-f1c6-11e6-8dea-00163e000c55";

    // 中旅 机票费用生成参数
    public static final String CTSHO_FLIGHT_EXPENSE_TYPE_OID = "0ccbff20-0306-11e7-92ed-00163e000c55";

    public static final int MAX_PAGE_SIZE = 100;

    public static final String OSS_COMPANY_LOGO_FOLDER = "company/logo/";

    public static final List<String> IMAGES_EXTENSION = Collections.unmodifiableList(Arrays.asList("jpeg", "jpg", "png", "bmp"));
    public static final String PDF_EXTENSION = "pdf";

    public static final List<String> VIDEO_EXTENSION = Collections.unmodifiableList(Arrays.asList("mp4", "mov", "mpeg", "avi", "rmvb"));


    public static final String DEFAULT_LANGUAGE = "zh_CN";

    public static final String DEFAULT_CURRENCY = "CNY";

    //费用类型字段key值定义

    public static final String TRANSPORT_EXPENSE_TYPE_KEY = "expense.type.transportation"; //交通费用

    public static final String HOTEL_EXPENSE_TYPE_KEY = "expense.type.hotel"; //酒店费用

    public static final String FLIGHT_EXPENSE_TYPE_KEY = "expense.type.air.tickets"; //机票费用

    public static final String FIELD_KEY_CITY = "city"; //城市(酒店，滴滴)

    public static final String FIELD_KEY_CITY_CODE = "location"; //城市code(酒店，滴滴)

    public static final String FIELD_KEY_REQUIRE_LEVEL = "require.level"; //车型

    public static final String FIELD_KEY_HOTEL_NAME = "hotel.name"; // 酒店名称(酒店)

    public static final String FIELD_KEY_HOTEL_DAYS = "days"; //入住天数(酒店)

    public static final String FIELD_KEY_CHECK_IN = "checkin"; //入住日期(酒店)

    public static final String FIELD_KEY_CHECK_OUT = "checkout"; //离店日期(酒店)

    public static final String FIELD_KEY_START_LOCATION = "departure.location"; //出发地(机票,滴滴)

    public static final String FIELD_KEY_END_LOCATION = "destination.location"; //目的地(机票,滴滴)

    public static final String FIELD_KEY_START_TIME = "start.time"; //开始时间(机票,滴滴)

    public static final String FIELD_KEY_END_TIME = "end.time"; //结束时间(机票,滴滴)

    public static final String FIELD_KEY_VOUCHER = "coupon.amount"; //优惠券折扣

    public static final String FIELD_KEY_CAR_TYPE = "vehicle.type"; //用车方式(滴滴)

    public static final String FIELD_KEY_TOTAL_AMOUNT = "total.amount"; //总金额(滴滴)
    public static final int EXPENSE_REPORT_MAX_INVOICE_COUNT = 200; //报销单中最大费用数量
    public static final String BAOKU_EMPLOYEE_GROUP_NAME = "普通员工"; //宝库系统中用户的角色，用于页面跳转
    public static final int BAOKU_SYNCHRONOUS_MAX_COUNT = 1; //宝库同步接口单次数量最大值
    public static final String FIELD_KEY_TICKET_TYPE = "ticket.type";  //机票类型
    public static final String FIELD_KEY_SEAT_CLASS = "shipping.space"; //舱位
    public static final String FIELD_KEY_PASSENGER = "passenger"; //乘机人

    //表单可见费用范围
    public static final Integer VISIBLE_EXPENSE_TYPE_SCOPE_ALL = 1001;
    public static final Integer VISIBLE_EXPENSE_TYPE_SCOPE_CUSTOM = 1002;
    /**
     * 报销单表单配置：表示和关联的申请单一致<br>
     * 1）提交单据，根据申请单选择的费用类型的范围
     * 2）表单配置，根据申请单可选的费用类型的范围
     */
    public static final Integer VISIBLE_EXPENSE_TYPE_SCOPE_BY_APPLICATION_SELECTED = 1003;
    public static final Integer VISIBLE_EXPENSE_TYPE_SCOPE_BY_APPLICATION = 1004;

    //人员组适用范围
    public static final Integer VISIBLE_USER_SCOPE_ALL = 1001;
    public static final Integer VISIBLE_USER_SCOPE_CUSTOM = 1002;
    public static final Integer VISIBLE_DEPARTMENT_SCOPE_CUSTOM = 1003;
    public static final Integer VISIBLE_USER_SCOPE_BY_APPLICATION = 1004;

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;

    //系统公司oid
    public static final UUID SYSTEM_COMPANYOID = UUID.fromString("e4b4a421-0355-4449-a610-26ff99322ab1");
    public static final Long SYSTEM_COMPANYID = 1L;

    public static final String APP_ID = "wxacfd8b05dc448523";
    public static final String APP_SECRET = "6bc0f148948ddb2a7363ef97c452ece3";

    public static final String DIDI_INVOICE_REFERENCE_SPLIT = "|";

    /**
     * 金额字段默认保留小数位数
     */
    public static final int DECIMAL_NUM = 6;

    //报销单汇总金额默认保留2位小数
    public static final int TWO_DECIMAL_PLACES = 2;
    //保留三位小数
    public static final int THREE_DECIMAL_PLACES = 3;
    public static final int SEVEN_DECIMAL_PLACES = 7;
    public static final int FOUR_DECIMAL_PLACES = 4;


    //是否
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String SMALL_YES = "y";
    public static final String SMALL_NO = "n";

    //用户类型 1：参与人，2：申请人 3：创建人
    public static final Integer PARTICIPANT = 1;
    public static final Integer APPLICANT = 2;
    public static final Integer CREATOR = 3;

    //费用类型-支付方式
    public static final Integer PERSONL_PAYMENAT_AMOUNT = 1001;//个人支付
    public static final Integer COMPANY_PAYMENAT_AMOUNT = 1002;//公司支付

    //对公付款单FORM_CODE
    public static final String VENDOR_PAYMENT_FORM_CODE = "vendor_payment";

    public static final String DEPARTURE = "departure"; //出发地

    public static final String DESTINATION = "destination"; //目的地

    public static final String INTENTION_FLIGHT = "intention_flight"; //意向航班

    public static final String SUPPLEMENT = "supplement"; //是否补录

    //发票报销状态
    //发票已锁定
    public static final String INVOICE_LOCKED = "INVOICE_REIMBURSE_LOCK";
    //发票已激活
    public static final String INVOICE_INIT = "INVOICE_REIMBURSE_INIT";
    //发票已使用
    public static final String INVOICE_USED = "INVOICE_REIMBURSE_CLOSURE";

    //票加加
    public static final String PJJ_CARD_SIGN = "PJJCARDSIGN";
    //支付宝
    public static final String ALIPAY_CARD_SIGN = "ALICARDSIGN";
    //微信-APP
    public static final String APP_CARD_SIGN = "APPCARDSIGN";
    //微信-公众号
    public static final String JS_CARD_SIGN = "JSCARDSIGN";

    //消息推送
    //发票消息提醒
    public static final String INVOICE_SCHEDULED_NOTICE_TASK = "InvoiceScheduledNoticeTask";
    public static final String TRAVEL_APPLICATION_NOTICE_TASK = "TravelApplicationNoticeTask";
    public static final String EXPENSE_REPORT_PAY_NOTICE_TASK = "ExpenseReportPayNoticeTask";

    //规则用户组的类型
    public static final String DEFAULT_TYPE = "default";
    public static final String CONDITION_TYPE = "condition";

    public static final String ROLE_TENANT = "TENANT";

    //配置
    public static final Integer FROM_CONFIG = 1001;//表单配置
    public static final Integer ITINERARY_FROM_CONFIG = 1002;//行程表单设置

    //汇率来源
    //    *********20180112需求 1455 撤销参考汇率&公司汇率更新定时任务
//    public static final String BOC="BOC";//中行
//    public static final String European = "european";//欧行
//    //中行汇率类型
//    public static final String BOC_HUI_IN ="bocHuiIn";//现汇买入价
//    public static final  String BOC_CHAO_IN = "bocChaoIn";//现钞买入价
//    public static  final String BOC_HUI_OUT = "bocHuiOut";//现汇卖出价
//    public static final String BOC_CHAO_OUT="bocChaoOut";//现钞卖出价
//    public static final String BOC_Mid_PRICE = "bocMidPrice";//中间价
//    //手动更新
//    public static final String  MANUAL_UPDATE_EMAIL= "手动更新";
//    //汇率更新定时任务Code
//    public static final String CURRENCY_RATE_UPDATE_TASK_CODE = "CompanyCurrencyRateUpdateTask";
//    //人民币简码
//    public static final String CNY = "CNY";
//    //今日汇率邮件标题
//    public static final String CURRENT_RATE_EMAIL_SUBJECT ="今日汇率";
//    //汇率更新邮件标题
//    public static final String RATE_UPDATE_EMAIL_SUBJECT = "汇率更新";
//    //汇率更新偏差警示邮件标题
//    public static final String RATE_WARN_EMAIL_SUBJECT = "货币汇率偏差超过警示百分比，请确认汇率是否正确";
//    //
//    public static final String ENGLISH= "english";
//    public static final String CHINEESE = "chinese";


    public static final String COUNTERSIGN_APPROVER = "countersignApprover"; //受会签规则影响
    public static final String APPROVER = "approver"; //不受会签规则影响
    public static final String APPORTIONMENT_DEPARTMENTS = "apportionmentDepartments"; //分摊的部门
    public static final String APPORTIONMENT_COST_CENT_ITEMS = "apportionmentCostCentItems"; //分摊的成本中心


    //商务卡相关
    public static final String BANK_CARD_TASK = "BankCardTask";
    public static final String APP = "APP";
    public static final String EMAIL = "EMAIL";
    public static final String BANK_TRAN_NOT_CREATED_INVOICE = "BANK_TRAN_NOT_CREATED_INVOICE";
    public static final String BANK_TRAN_NOT_REFERENCED_REPORT = "BANK_TRAN_NOT_REFERENCED_REPORT";
    public static final String BANK_TRAN_NOT_PASSED_REPORT = "BANK_TRAN_NOT_PASSED_REPORT";
    public static final String CURRENT_MONTH = "CURRENT_MONTH";
    public static final String LAST_MONTH = "LAST_MONTH";
    public static final String PULLED_BANK_TRAN = "PULLED_BANK_TRAN";


    public static final String TENANT_ADDITION = "-租户";


    public static final String start = "start";
    public static final String end = "end";
    public static final String internationalStart = "internationalStart";
    public static final String internationalEnd = "internationalEnd";
    public static final String preBookDays = "preBookDays";
    public static final Integer defaultPreBookDays = 0;


    //其他供应商
    public static final String hotelOther = "90660c40-72bb-11e7-9523-1664b14e714d";
    public static final String trainOther = "93f0713e-72bb-11e7-9523-1664b14e714d";
    public static final String flightOther = "8031c652-72bb-11e7-9523-1664b14e714d";

    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";

    public static final String HORIZONTAL_SPLIT = "-";

    public static final Integer mileageSubsidy = 15;//里程补贴（私车公用）

    public static final String CITY_COUNTRY_CODE_CHN = "CHN";

    public static final String CITY_COUNTRY_CODE_CN = "CN";

    public static final String CITY_CLIENT_LANGUAGE_CHN = "zh_cn";

    public static final String LOCATION_VENDOR_TYPE_CTRIP_HOTEL = "ctrip_hotel";

    public static final String LOCATION_VENDOR_TYPE_CTRIP_AIR = "ctrip_air";

    public static final String SPECIAL_VAT_FAPIAO = "SPECIAL_VAT_FAPIAO";
    public static final String MOTOR_VEHICLE_SALES_FAPIAO = "MOTOR_VEHICLE_SALES_FAPIAO";
    public static final String NORMAL_VAT_FAPIAO = "NORMAL_VAT_FAPIAO";
    public static final String ELECTRONIC_VAT_FAPIAO = "ELECTRONIC_VAT_FAPIAO";
    public static final String ROLL_PRINTED_VAT_FAPIAO = "ROLL_PRINTED_VAT_FAPIAO";
    public static final String SPACE = " ";
    /**
     * 其他城市级别OID
     */
    public static final String OTHER_CITY_LEVEL_OID = "111111111";

    /**
     * 通用城市级别OID
     */
    public static final String COMMON_CITY_LEVEL_OID = "000000000";

    public static final String LOGIN_ATTEMPT_PREFIX = "LOGIN_ATTEMPT_PREFIX_";

    public static final String APPROVAL_MAIL_TITLE = "approval.mail.title";
    public static final String APPROVAL_MAIL_DETAIL = "approval.mail.detail";
    public static final String APPROVAL_MAIL_USER_MESSAGE = "approval.mail.user.message";
    public static final String APPROVAL_MAIL_WARN_MESSAGE = "approval.mail.warn.message";
    public static final String APPROVAL_MAIL_WARN = "approval.mail.warn";
    public static final String APPROVAL_MAIL_TIPS = "approval.mail.tips";
    public static final String APPROVAL_MAIL_APPLICATION_TITLE = "approval.mail.application.title";
    public static final String APPROVAL_MAIL_BTNA = "approval.mail.btna";
    public static final String APPROVAL_MAIL_BTNR = "approval.mail.btnr";
    public static final String APPROVAL_MAIL_PROXY_TITLE = "approval.mail.proxy.title";

    public static final String FUNCTION_PROFILE_THRIDPARTIES = "thridParties";

    public static final String FUNCTION_PROFILE_THRIDPARTIES_VENDOR = "vendor";

    public static final String FUNCTION_PROFILE_THRIDPARTIES_VENDOR_MESSAGEKEY = "messageKey";

    public static final String DATE_FORMAT = "yyyy-MM-dd";


    /**
     * 大版本维护，针对于全局 租户id和公司id 默认为0
     */
    public static final Long DEFAULT_TENANT_ID = 0L;

    public static final Long DEFAULT_COMPANY_ID = 0L;

    public static final String ZH_CN = "zh_cn";

    public static final String I18N = "i18n";

    public static final String LANGUAGE = "language";

    public static final String VALUE = "value";

    private Constants() {
    }

    /**
     * securityUtils.getCurrentUserOid取到的滴滴中间件client方式登录的userOid
     */
    public static final UUID DIDI_CLIETN_USER_OID = UUID.fromString("d7f51394-6b0e-328b-96f4-8778fb672651");
}
