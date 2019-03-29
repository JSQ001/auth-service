package com.hand.hcf.app.mdata.bank.enums;

/**
 * 银行导入编码
 * Created by Strive on 18/3/22.
 */
public final class BankInfoImportCode {
    // 起始行
    public final static int EXCEL_BASEROW = 5;

    public final static String IMPORT_TEMPLATE_PATH = "/templates/customBankInfoTemplate.xlsx";

    public final static String ERROR_TEMPLATE_PATH = "/templates/customBankInfoErrorTemplate.xlsx";

    public final static int ROW_NUMBER = 0;
    public final static int COUNTRY_CODE = 1;
    public final static int BANK_CODE = 2;
    public final static int SWIFT_CODE = 3;
    public final static int BANK_NAME = 4;
    public final static int BANK_BRANCH_NAME = 5;
    public final static int OPEN_ACCOUNT = 5;
    public final static int DETAIL_ADDRESS = 6;
    public final static int ENABLED = 7;
    public final static int ERROR_DETAIL = 8;
    public final static int IMPORT_ENABLED = 6;
    public final static int IMPORT_ERROR_DETAIL = 7;
}
