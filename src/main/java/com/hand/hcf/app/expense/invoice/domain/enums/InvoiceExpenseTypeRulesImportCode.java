package com.hand.hcf.app.expense.invoice.domain.enums;

public final class InvoiceExpenseTypeRulesImportCode {

    /**
     * 导入起始行
     */
    public final static int EXCEL_BASEROW = 4;

    /**
     * 错误数据起始行
     */
    public final static int EXCEL_BASEROW_ERROR = 2;

    public final static String IMPORT_TEMPLATE_PATH = "/templates/InvoiceExpenseTypeRulesTemplate.xlsx";

    public final static String ERROR_TEMPLATE_PATH = "/templates/InvoiceExpenseTypeRulesTemplate.xlsx";

    /**
     * 行号
     */
    public final static int ROW_NUMBER = 0;

    /**
     * 维值编码
     */
    public final static int Responsibility_Center_Code = ROW_NUMBER + 1;

    /**
     * 维值名称
     */
    public final static int Responsibility_Center_Name = Responsibility_Center_Code + 1;

    /**
     * 是否启用
     */
    public final static int ENABLED = Responsibility_Center_Name + 1;

    /**
     * 错误说明
     */
    public final static int ERROR_DETAIL = ENABLED + 1;

    public final static String NAME_LENGTH_REGEX = "^.{0,200}$";

}
