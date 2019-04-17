package com.hand.hcf.app.mdata.system.constant;

import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Application constants.
 */
public final class Constants {

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

    public static final String start = "start";
    public static final String end = "end";

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;

    public static final Long SYSTEM_ACCOUNT = 0L;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DEFAULT_LANGUAGE = LanguageEnum.ZH_CN.getKey();

    public static final String DEFAULT_CURRENCY = "CNY";
    /**
     * 大版本维护，针对于全局 租户id和公司id 默认为0
     */
    public static final Long DEFAULT_TENANT_ID = 0L;

    public static final Long DEFAULT_COMPANY_ID = 0L;


    public static final String I18N = "i18n";

    public static final String LANGUAGE = "language";

    public static final String VALUE = "value";

    public static final String LOGIN_ATTEMPT_PREFIX = "LOGIN_ATTEMPT_PREFIX_";
    public static final ZonedDateTime MIN_DATE_TIME_IN_DB = ZonedDateTime.parse("1970-01-01T00:00:01+00:00",DateTimeFormatter.ISO_DATE_TIME);//parse("1970-01-01 08:00:01");

    //租户模式
    public static final String ROLE_TENANT = "TENANT";
    //公司模式
    public static final String ROLE_COMPANY = "COMPANY";

    public static final String TENANT_ADDITION = "-租户";

    //用户
    public static final String LEAVED = "LEAVED";
    public static final String DELETED = "DELETED";

    // 部门分隔符
    public static final String DEPARTMENT_SPLIT = "|";

    private Constants() {
    }

    /**
     * 选择模式
     * 100 普通选择
     */
    public static final String MODE_DEFAULT = "default";
    /**
     * 选择模式
     * 101 全选全部
     */
    public static final String MODE_ALL_PAGE = "all_page";
    /**
     * 选择模式
     * 102 全选当页
     */
    public static final String MODE_CURRENT_PAGE = "current_page";

    //是否
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String SMALL_YES = "y";
    public static final String SMALL_NO = "n";


}
