package com.hand.hcf.app.mdata.bank.enums;

/**
 * Created by yangqi on 2017/1/11.
 */
public final class BankAccountImportCode {
    //起始行
    public final static int EXCEL_BASEROW = 3;

    public final static String TEMPLATE_PATH = "/templates/contactBankAccountTemplate.xlsx";

    public final static String TEMPLATE_SHEET_KEYWORD = "银行帐号信息";
    public final static String TEMPLATE_SHEET_KEYWORD_EN = "Bank Account Info.";


//    public static int COMPANY_NAME = 0;

    public final static int EMPLOYEE_ID = 0;    //工号

    public final static int BANK_ACCOUNT_NO = EMPLOYEE_ID+1;   //银行帐号

    public final static int BANK_ACCOUNT_NAME = BANK_ACCOUNT_NO+1;     //开户名

    public final static int BRANCH_NAME = BANK_ACCOUNT_NAME+1;       //支行名称

//    public final static int BANK_NAME = 4;         //银行名称
//
//    public final static int BANK_CODE = 5;          //银行编号

    public final static int ACCOUNT_LOCATION = BRANCH_NAME+1;      //开户地

    public final static int ERROR_DETAIL = ACCOUNT_LOCATION+1;       //错误说明


}
