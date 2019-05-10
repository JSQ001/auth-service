package com.hand.hcf.app.common.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Application constants.
 */
public final class Constants {


    public static final List<String> IMAGES_EXTENSION = Collections.unmodifiableList(Arrays.asList("jpeg", "jpg", "png", "bmp"));
    public static final String PDF_EXTENSION = "pdf";
    public static final List<String> WORD_EXTENSION = Collections.unmodifiableList(Arrays.asList("xls", "xlsx", "csv"));

    public static final List<String> VIDEO_EXTENSION = Collections.unmodifiableList(Arrays.asList("mp4", "mov", "mpeg", "avi", "rmvb"));


    public static final String DEFAULT_LANGUAGE = "zh_cn";

    public static final String DEFAULT_CURRENCY = "CNY";
    // 部门分隔符
    public static final String DEPARTMENT_SPLIT = "|";

    //租户模式
    public static final String ROLE_TENANT = "TENANT";
    //公司模式
    public static final String ROLE_COMPANY = "COMPANY";

    public static final String PREFIX = "HCF:CLIENT:";//统一前缀
    public static final String USER = PREFIX + "USER";   //用户
    public static final String COMPANY = PREFIX + "COMPANY";   //公司
    public static final String ATTACHMENT = PREFIX + "ATTACHMENT"; //附件
    public static final String SYSTEM_PROFILE_PREFIX = PREFIX+"SYSTEM_PROFILE:";
    public static final String LEGAL_ENTITY = PREFIX + "LEGAL_ENTITY";  //法人
    public static final String MOBILE_VALIDATE = PREFIX + "MOBILE_VALIDATE"; //电话号码验证规则
    public static final String SET_OF_BOOKS = PREFIX + "SET_OF_BOOKS";   // 账套
    public static final String COMPANY_REGISTER_VERIFY_CODE_PREFIX = PREFIX + "VERIFY_CODE:";  //公司注册图片验证码
    public static final String CUSTOM_ENUMERATION = PREFIX + "CUSTOM_ENUMERATION";   // 值列表
    public static final String DEPARTMENT_POSITION = PREFIX + "DEPARTMENT_POSITION";   // 值列表
}
