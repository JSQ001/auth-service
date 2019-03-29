

package com.hand.hcf.app.mdata.system.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CacheConstants {
    public static final String PREFIX = "HCF_";//统一前缀
    public static final String USER = PREFIX + "USER";   //用户
    public static final String TENANT = PREFIX + "TENANT";   // 租户
    public static final String COMPANY = PREFIX + "COMPANY";   // 租户
    public static final String ATTACHMENT = PREFIX + "ATTACHMENT"; //附件
    public static final String SYSTEM_PROFILE_PREFIX = PREFIX+"SYSTEM_PROFILE:";
    public static final String LEGAL_ENTITY = PREFIX + "LEGAL_ENTITY";  //法人
    public static final String MOBILE_VALIDATE = PREFIX + "MOBILE_VALIDATE"; //电话号码验证规则
    public static final String SET_OF_BOOKS = PREFIX + "SET_OF_BOOKS";   // 账套
    public static final String COMPANY_REGISTER_VERIFY_CODE_PREFIX = PREFIX + "VERIFY_CODE:";  //公司注册图片验证码
    public static final String CUSTOM_ENUMERATION = PREFIX + "CUSTOM_ENUMERATION";   // 值列表
    public static final String DEPARTMENT_POSITION = PREFIX + "DEPARTMENT_POSITION";   // 值列表

    public static final String COMPANY_SUBMIT = "COMPANY_SUBMIT";   // 公司提交

    public static final String PHONE_KEY_PREFIX = PREFIX + "Phone:";
    public static final String EXPORT_KEY_PREFIX = PREFIX + "Export:";

    public static final Map<String, Long> cacheExpireMap;
    public static final String CODING_RULE_OBJECT = PREFIX + "CODING_RULE_OBJECT"; //编码规则定义
    public static final String CODING_RULE_DETAIL = PREFIX+"CODING_RULE_DETAIL"; //编码规则明细
    public static final String CODING_RULE = PREFIX+"CODING_RULE"; //编码规则

    static {
        Map<String, Long> tempMap = new HashMap<>();
        cacheExpireMap = Collections.unmodifiableMap(tempMap);
    }
}
