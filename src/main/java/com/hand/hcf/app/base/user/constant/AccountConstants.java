package com.hand.hcf.app.base.user.constant;

/**
 * comment
 * Created by fanfuqiang 2018/11/20
 */
public class AccountConstants {
    public static final int BIND_TYPE_EMAIL = 1001;
    public static final int BIND_TYPE_MOBILE = 1002;

    public static final int DEFAULT_NOTICETYPE = 1001;
    public static final int DEFAULT_DIMISSION_DELAY_DAYS = 0;
    public static final String DEFAULT_PASSWORD_RULE = "1010";
    public static final int DEFAULT_PASSWORD_LENGTH_MIN = 6;
    public static final int DEFAULT_PASSWORD_LENGTH_MAX = 32;
    public static final int DEFAULT_PASSWORD_REPEAT_TIME = 1;
    public static final int DEFAULT_PASSWORD_EXPIRE_DAYS = 0;
    public static final int DEFAULT_CRATE_DATA_TYPE = 1001;
    public static final String DEFAULT_PASSWORD="123654";

    public static final int NOTICE_TYPE_EMAIL = 1001;
    public static final int NOTICE_TYPE_MOBILE= 1002;
    public static final int NOTICE_TYPE_EMAIL_AND_MOBILE = 1003;

    public static final String NOT_AUTHORIZED = "NOT_AUTHORIZED";

    public final static String REDIS_NAME_SPACE_PREFIX = "authorize.code.";

    //包含小写字母
    public final static String regexLowerCase = ".*[a-z]+.*";
    //包含大写字母
    public final static String regexUpperCase = ".*[A-Z]+.*";
    //包含数字
    public final static String regenNum = ".*[\\d]+.*";
    //包含特殊字符
    public final static String regexSpecialChar = ".*[`~!@#$%^&*()+=|{}':;'-,\"\\\\\\[\\].<>/?_]+.*";

    public final static String INCLUDE_FLAG_CHAR = "1";

}

