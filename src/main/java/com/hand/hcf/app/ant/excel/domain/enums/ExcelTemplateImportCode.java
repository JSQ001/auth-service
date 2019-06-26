package com.hand.hcf.app.ant.excel.domain.enums;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/20
 */
public final class ExcelTemplateImportCode {
    public final static int EXCEL_BASEROW = 3;  // 导入起始行

    public final static int EXCEL_BASEROW_ERROR = 3;  // 错误数据起始行

    public final static String IMPORT_TEMPLATE_PATH = "/templates/ExcelTemplate.xlsx";
    public final static String ERROR_TEMPLATE_PATH = "/templates/ExcelTemplate.xlsx";

    public final static int ROW_NUMBER = 0;  //行号
    public final static int Responsibility_Center_Code = ROW_NUMBER + 1;   // 维值编码

    public final static int Responsibility_Center_Name = Responsibility_Center_Code + 1;    // 维值名称

    public final static int REMARK_ERROR = 1;

    public final static int COMPANY_CODE_ERROR = 2;

    public final static int RESPONSIBILITY_CENTER_CODE  = 3;

    public final static int ACCOUNT_CODE_ERROR  = 4;

    public final static int ENTERED_AMOUNT_DR_ERROR  = 5;

    public final static int ENTERED_AMOUNT_CR_ERROR  = 6;


    public final static String NAME_LENGTH_REGEX = "^.{0,200}$";
}
