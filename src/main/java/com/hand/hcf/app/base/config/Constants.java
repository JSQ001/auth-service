package com.hand.hcf.app.base.config;

/**
 * Application constants.
 */
public final class Constants {

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    public static final String start = "start";
    public static final String end = "end";


    public static final String DATE_FORMAT = "yyyy-MM-dd";


    /**
     * 大版本维护，针对于全局 租户id和公司id 默认为0
     */
    public static final Long DEFAULT_TENANT_ID = 0L;

    public static final Long DEFAULT_COMPANY_ID = 0L;

    public static final String ZH_CN = "zh_cn";

    public static final String I18N = "i18n";

    public static final String LANGUAGE = "language";

    public static final String VALUE = "value";

    private Constants() {
    }

}
