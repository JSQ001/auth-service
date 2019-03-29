package com.hand.hcf.app.mdata.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/12 15:05
 */
public class MyStringUtils {

    private static final String JAVA_LANG_STRING = "java.lang.String";

    private static final String GET = "get";

    private static final String SET = "set";

    private static final Logger LOGGER = LoggerFactory.getLogger(MyStringUtils.class);

    /**
     * 用于字段格式化 模糊查询
     *
     * @param target
     * @return
     */
    public static String formatFuzzyQuery(String target) {
        return StringUtils.isNotBlank(target) ? "%".concat(target).concat("%") : null;
    }

    /**
     * Object对象中所有String类型属性：去除值中的空格
     *
     * @param object
     */
    public static void deleteStringTypeFieldTrim(Object object) {
        Class target = object.getClass();
        Method[] methods = target.getMethods();
        if (!CollectionUtils.isEmpty(Arrays.asList(methods))) {
            for (Method method : methods) {
                if (method.getName().startsWith(GET) && JAVA_LANG_STRING.equals(method.getGenericReturnType().getTypeName())) {
                    Method methodSet = null;
                    try {
                        methodSet = target.getMethod(SET.concat(method.getName().substring(3)), String.class);
                    } catch (NoSuchMethodException e) {
                        LOGGER.error("No such method exception : {}", e.getMessage());
                    }
                    if (methodSet != null) {
                        try {
                            Object result = method.invoke(object);
                            if (result != null) {
                                methodSet.invoke(object, result.toString().replace(" ", ""));
                            }
                        } catch (IllegalAccessException e) {
                            LOGGER.error("Illegal access exception : {}", e.getMessage());
                        } catch (InvocationTargetException e) {
                            LOGGER.error("Invocation target exception : {}", e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
