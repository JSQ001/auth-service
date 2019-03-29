package com.hand.hcf.app.mdata.dimension.domain.enums;

public final class DimensionItemImportCode {

    public final static int EXCEL_BASEROW = 4;  // 导入起始行

    public final static int EXCEL_BASEROW_ERROR = 2;  // 错误数据起始行

    public final static String IMPORT_TEMPLATE_PATH = "/templates/DimensionItemTemplate.xlsx";

    public final static String ERROR_TEMPLATE_PATH = "/templates/DimensionItemErrorTemplate.xlsx";

    public final static int ROW_NUMBER = 0;  //行号

    public final static int Dimension_Item_CODE = ROW_NUMBER + 1;   // 维值编码

    public final static int Dimension_Item_name = Dimension_Item_CODE + 1;    // 维值名称

    public final static int ENABLED = Dimension_Item_name + 1; // 是否启用

    public final static int ERROR_DETAIL = ENABLED + 1; // 错误说明

    public final static String NAME_LENGTH_REGEX = "^.{0,200}$";
}
