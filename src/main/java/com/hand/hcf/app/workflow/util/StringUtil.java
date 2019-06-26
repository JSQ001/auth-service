package com.hand.hcf.app.workflow.util;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Created by yangqi on 2016/7/13.
 */
public class StringUtil {

    private static final String SET = "set";

    /**
     * 拼接字符串
     * @version 1.0
     * @author mh.z
     * @date 2019/05/06
     *
     * @param params
     * @return
     */
    public static String concat(Object... params) {
        StringBuffer buffer = new StringBuffer();

        for (Object param : params) {
            buffer.append(param);
        }

        return buffer.toString();
    }

    public static String getStringValue(String str) {
        return StringUtils.isEmpty(str) ? "" : str;
    }

    public static Boolean isNullOrEmpty(String str) {
        if (null == str) {
            return true;
        } else {
            return null != str && str.isEmpty();
        }
    }

    /**
     * 转义sql查询特殊字符
     * @param str
     * @return
     */
    public static String escapeSpecialCharacters(String str) {
        //特殊字符 用于sql模糊查询时特殊字符的转义标识符 (й)
        String s = null;
        try {
            s = new String(new byte[]{-48, -71}, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        return str.replaceAll(" ", "")
                .replaceAll("'", "''")
                .replaceAll("%", s + "%")
                .replaceAll("_", s + "_");
    }

    /**
     * @author mh.z
     * @date 2019/01/18
     * @description 返回UUID的字符串表示
     *
     * @param uuid
     * @return
     */
    public static String getUuidString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    /**
     * @author mh.z
     * @date 2019/01/18
     * @description 返回用标准日期格式（ISO8601）格式化的日期字符串
     *
     * @param dateTime
     * @return
     */
    public static String getStandardDateString(ZonedDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return dateTime.format(dateTimeFormatter);
    }

}
