package com.hand.hcf.app.ant.taxreimburse.utils;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 常量类
 * @date 2019/6/18 19:08
 */
public class TaxReimburseConstans {

    public final static String WARNING = "不可删除已勾兑的数据！";

    public final static String WARNING1 = "存在其他已勾兑的数据未勾选！";

    public final static String WARNING2 = "未勾兑的数据不可报账！";

    public final static String WARNING3 = "已报账的数据不可再次报账！";

    //起始行
    public final static int EXCEL_BASEROW = 3;
    //错误信息起始行
    public final static int EXCEL_BASEROW_ERROR = 3;

    public static final String TEMPLATE_SHEET_KEYWORD1 = "税金申报数据导入";

    public final static String TEMPLATE_SHEET_KEYWORD2 = "银行流水数据导入";

    public final static String TAX_REPORT_IMPORT_TEMPLATE_PATH = "/templates/taxreportdataimportTemplate.xlsx";

    public final static String BANK_FLOW_IMPORT_TEMPLATE_PATH = "/templates/bankflowdataimportTemplate.xlsx";

    public final static String TAX_REPORT_IMPORT_ERROR_TEMPLATE_PATH = "/templates/taxreporterrordataImportTemplate.xlsx";

    public final static String BANK_FLOW_IMPORT_ERROR_TEMPLATE_PATH = "/templates/bankflowerrordataimportTemplate.xlsx";

    public static final int ROW_NUMBER_ERROR = 0;
    public static final int COMPANY_CODE_ERROR = 1;
    public static final int TAX_CATEGORY_NAME_ERROR = 2;
    public static final int REQUST_PEROID_ERROR = 3;
    public static final int REQUEST_AMOUNT = 4;
    public static final int BUSINESS_SUBCATEGORY_NAME_ERROR = 5;
    public static final int BUSINESS_SUBCATEGORY_CODE_ERROR = 9;


    public static final int BANK_ROW_NUMBER_ERROR = 0;
    public static final int BANK_COMPANY_CODE_ERROR = 1;
    public static final int pay_date_error = 2;
    public static final int fund_flow_number_error = 3;
    public static final int bank_account_name_error = 5;
    public static final int flow_amount_lender_error = 7;
    public static final int flow_amount_debit_error = 8;
    public static final int currency_code_error = 9;
    public static final int bank_remark_error = 13;



}
