package com.hand.hcf.app.mdata.contact.enums;

public final class UserImportCode {
    //起始行
    public final static int EXCEL_BASEROW = 3;
    //错误信息起始行
    public final static int EXCEL_BASEROW_ERROR = 3;

    public final static String TEMPLATE_PATH = "/templates/import_newUserInfoTemplate.xlsx";

    public final static String IMPORT_ERROR_PATH = "/templates/import_error_newUserInfoTemplate.xlsx";

    public static final String NORMAL_STATUS = "在职";
    public static final String LEAVING_STATUS = "待离职";
    public static final String LEAVED_STATUS = "离职";

    public final static String TEMPLATE_SHEET_KEYWORD = "员工信息导入";
    public final static String TEMPLATE_SHEET_KEYWORD_EN = "Employee Info";

    public static final int ROW_NUMBER_ERROR = 0;
    public static final int EMPLOYEE_ID_ERROR = 1;
    public static final int FULL_NAME_ERROR = 2;
    public static final int COMPANY_CODE_ERROR = 3;
    public static final int DEPARTMENT_CODE_ERROR = 4;
    public static final int EMAIL_ERROR = 5;
    public static final int MOBILE_AREA_CODE_ERROR = 6;
    public static final int MOBILE_ERROR = 7;
    public static final int DIRECT_MANAGER_ERROR = 8;
    public static final int DUTY_CODE_ERROR = 9;
    public static final int TITLE_ERROR = 10;
    public static final int EMPLOYEE_TYPE_CODE_ERROR = 11;
    public static final int RANK_CODE_ERROR = 12;
    public static final int GENDER_CODE_ERROR = 13;
    public static final int BIRTHDAY_STR = 14;
    public static final int ENTRYDATE_STR = 15;


    public static String userStatusToViewStatus(Integer status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case 1001:
                return NORMAL_STATUS;
            case 1002:
                return LEAVING_STATUS;
            case 1003:
                return LEAVED_STATUS;
        }
        return null;
    }

    public static Integer userViewStatusToStatus(String viewStatus) {
        if (viewStatus == null) {
            return EmployeeStatusEnum.NORMAL.getId();
        }
        switch (viewStatus) {
            case NORMAL_STATUS:
                return EmployeeStatusEnum.NORMAL.getId();
            case LEAVING_STATUS:
                return EmployeeStatusEnum.LEAVING.getId();
            case LEAVED_STATUS:
                return EmployeeStatusEnum.LEAVED.getId();
        }
        return null;
    }
}
