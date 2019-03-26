package com.hand.hcf.app.mdata.contact.enums;

public class ContactCardImportCode {

    //起始行
    public static final int EXCEL_BASEROW = 3;
    //错误信息起始行
    public static final int EXCEL_BASEROW_ERROR = 3;

    public static final String TEMPLATE_PATH = "/templates/import_newContactCardTemplate.xlsx";

    public static final String IMPORT_ERROR_PATH = "/templates/import_error_newContactCardTemplate.xlsx";

    public static final String TEMPLATE_SHEET_KEYWORD = "证件信息导入";
    public static final String TEMPLATE_SHEET_KEYWORD_EN = "Card Info";

    public static final int ROW_NUMBER_ERROR = 0;
    public static final int EMPLOYEE_ID_ERROR = 1;
    public static final int FIRST_NAME_ERROR = 2;
    public static final int LAST_NAME_ERROR = 3;
    public static final int NATIONALITY_ERROR = 4;
    public static final int CARD_TYPE_CODE_ERROR = 5;
    public static final int CARD_NO_ERROR = 6;
    public static final int CARD_EXPIRED_TIME_ERROR = 7;
    public static final int ENABLED_STR_ERROR = 8;
    public static final int PRIMARY_STR_ERROR = 9;



}
