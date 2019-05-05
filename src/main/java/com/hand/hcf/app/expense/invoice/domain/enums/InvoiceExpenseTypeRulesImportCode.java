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
     * 货物名称
     */
    public final static int RESPONSIBILITY_GOOD_NAME = ROW_NUMBER + 1;

    /**
     * 费用类型代码
     */

    public final static int RESPONSIBILITY_EXPENSE_TYPE_CODE = RESPONSIBILITY_GOOD_NAME + 1;

    /**
     * 描述
     */
    public final static int RESPONSIBILITY_DESCRIPTIONS = RESPONSIBILITY_EXPENSE_TYPE_CODE + 1;

    /**
     * 开始时间
     */

    public final static int  RESPONSIBILITY_START_DATE = RESPONSIBILITY_DESCRIPTIONS + 1;

    /**
     * 结束时间
     */

    public final  static int RESPONSIBILITY_END_DATE = RESPONSIBILITY_START_DATE + 1;

    /**
     * 是否启用
     */
    public final static int ENABLED = RESPONSIBILITY_END_DATE + 1;

    /**
     * 错误说明
     */
    public final static int ERROR_DETAIL = ENABLED + 1;

    public final static String NAME_LENGTH_REGEX = "^.{0,200}$";

}
