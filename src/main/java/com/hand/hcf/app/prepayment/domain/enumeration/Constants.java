package com.hand.hcf.app.prepayment.domain.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by markfredchen on 16/10/28.
 */
public final class Constants {

    public static final String HUI_LIAN_YI = "汇联易";

    // Spring profile for development, production and "fast", see http://jhipster.github.io/profiles.html
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String SPRING_PROFILE_PRODUCTION_NODIDI = "prodnodidi";

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

    public static final String COST_CENTER_TEMPLATE = "/templates/costcenterTemplate.xlsx";
    //导入成本中心项-模板
    public static final String COST_CENTER_ITEM_TEMPLATE = "/templates/HLY-CostCenterItem_DataImportTemplate.xlsx";

    public static final String COMPANY_TEMPLATE_PATH = "/templates/companyTemplate.xlsx";

    public static final String FEEDBACK_TEMPLATE = "/templates/feedbackTemplate.xlsx";

    public static final String REIMBURSEMENT_TEMPLATE = "/templates/reimbursementTemplate.xlsx";

    public static final String CTRIP_FLIGHT_ORDER_TEMPLATE = "/templates/ctripFlightOrderTemplate.xlsx";

    public static final String CTRIP_HOTEL_INVOICE_TEMPLATE = "/templates/ctripHotelInvoiceTemplate.xlsx";

    public static final String DIDI_INVOICE_TEMPLATE = "/templates/didiInvoiceTemplate.xlsx";

    public static final String COMPANY_USER_WITH_BANK_ACCOUNT_TEMPLATE = "/templates/userWithBankAccountTemplate.xlsx";

    public static final String REIMBURSEMENT_BATCH_TEMPLATE = "/templates/reimbursementBatch.xlsx";

    public static final String NACHA_TEMPLATE = "/templates/nacha.xlsx";

    public static final String DEPARTMENT_BUDGET_TEMPLATE = "/templates/departmentBudgetTemplate.xlsx";

    public static final String TRAVEL_BOOK_TEMPLATE = "/templates/travelbookTemplate.xlsx";

    public static final String TRAVEL_APPLICATION_TEMPLATE = "/templates/travelapplicationTemplate.xlsx";//导出差旅申请

    //文泉驿等宽微米黑,开源字体
    public static final String FONT_WQY_DKWMH = "/font/wqy_dkwmh.ttf";

    //滴滴生成费用参数
    public static final long COMPANY_ID = 2;
    public static final String DIDI_LOGO_OID = "32c3a354-eac4-4c56-8d57-9e278d43b521";
    public static final String DIDI_EXPENSE_TYPE_OID = "fcf5878d-0857-4c7e-8350-b0faded4fb9e";
    public static final String DIDI_TRANSPORT_EXPENSE_TYPE_OID = "d30023be-45d5-4fed-994d-91a2d51d2de5";

    public static final String DIDI_DAIJIA_COMMENT = " (滴滴代驾交通费用需自行向滴滴企业索要发票进行报销!)";

    //携程酒店费用生成参数
    public static final String CTRIP_HOTEL_EXPENSE_TYPE_OID = "ae8981d2-74e7-11e6-9639-00ffa3fb4c67";
    //携程机票费用生成参数
    public static final String CTRIP_FLIGHT_EXPENSE_TYPE_OID = "a3eebf8e-74e7-11e6-9639-00ffa3fb4c67";

    public static final int MAX_PAGE_SIZE = 100;

    public static final String OSS_COMPANY_LOGO_FOLDER = "company/logo/";

    public static final List<String> IMAGES_EXTENSION = Arrays.asList("jpeg", "jpg", "png", "bmp");
    public static final String PDF_EXTENSION = "pdf";

    public static final List<String> VIDEO_EXTENSION = Arrays.asList("mp4", "mov", "mpeg", "avi", "rmvb");


    public static final String DEFAULT_LANGUAGE = "zh_CN";

    public static final String DEFAULT_CURRENCY = "CNY";

    //费用类型字段key值定义

    public static final String TRANSPORT_EXPENSE_TYPE_KEY = "expense.type.transportation"; //交通费用

    public static final String HOTEL_EXPENSE_TYPE_KEY = "expense.type.hotel"; //酒店费用

    public static final String FLIGHT_EXPENSE_TYPE_KEY = "expense.type.air.tickets"; //机票费用

    public static final String FIELD_KEY_CITY = "city"; //城市(酒店，滴滴)

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
    public static final int BAOKU_SYNCHRONOUS_MAX_COUNT = 500; //宝库同步接口单次数量最大值
    public static final String FIELD_KEY_TICKET_TYPE = "ticket.type";  //机票类型
    public static final String FIELD_KEY_SEAT_CLASS = "shipping.space"; //舱位
    public static final String FIELD_KEY_PASSENGER = "passenger"; //乘机人

    //表单可见费用范围
    public static final Integer VISIBLE_EXPENSE_TYPE_SCOPE_ALL = 1001;
    public static final Integer VISIBLE_EXPENSE_TYPE_SCOPE_CUSTOM = 1002;
    public static final Integer VISIBLE_EXPENSE_TYPE_SCOPE_BY_APPLICATION_SELECTED = 1003;
    public static final Integer VISIBLE_EXPENSE_TYPE_SCOPE_BY_APPLICATION = 1004;

    //人员组适用范围
    public static final Integer VISIBLE_USER_SCOPE_ALL = 1001;
    public static final Integer VISIBLE_USER_SCOPE_CUSTOM = 1002;
    public static final Integer VISIBLE_USER_SCOPE_BY_APPLICATION = 1004;

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;

    //系统公司oid
    public static final UUID SYSTEM_COMPANYOID = UUID.fromString("e4b4a421-0355-4449-a610-26ff99322ab1");

    public static final String DIDI_INVOICE_REFERENCE_SPLIT = "|";

    /**
     * 金额字段默认保留小数位数
     */
    public static final int DECIMAL_NUM = 6;

    public static final  String COUNTERSIGN_APPROVER = "countersignApprover"; //受会签规则影响
    public static final  String APPROVER = "approver"; //不受会签规则影响
    private Constants() {
    }

    // 预付款单据类型
    public static final int PREPAYMENT_DOCUMENT_TYPE = 801003;
}
