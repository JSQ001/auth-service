package com.hand.hcf.app.mdata.contact.enums;

public class ContactBankAccountImportCode {

    //起始行
    public static final int EXCEL_BASEROW = 3;
    //错误信息起始行
    public static final int EXCEL_BASEROW_ERROR = 3;

    public static final String TEMPLATE_PATH = "/templates/import_newContactBankAccountTemplate.xlsx";

    public static final String IMPORT_ERROR_PATH = "/templates/import_error_newContactBankAccountTemplate.xlsx";

    public static final String TEMPLATE_SHEET_KEYWORD = "银行帐号信息导入";
    public static final String TEMPLATE_SHEET_KEYWORD_EN = "Bank Account Info";

    public static final int ROW_NUMBER_ERROR = 0;
    public static final int EMPLOYEE_ID_ERROR = 1;
    public static final int BANK_ACCOUNT_NAME_ERROR = 2;
    public static final int BANK_ACCOUNT_NO_ERROR = 3;
    public static final int ACCOUNT_LOCATION_ERROR = 4;
    public static final int BRANCH_NAME_ERROR = 5;
    public static final int ENABLED_STR_ERROR = 6;
    public static final int PRIMARY_STR_ERROR = 7;







}
