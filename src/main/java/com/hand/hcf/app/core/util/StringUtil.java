package com.hand.hcf.app.core.util;

import java.text.MessageFormat;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/12/14 15:24
 * @remark String工具类
 */
public final class StringUtil {

    /**
     * 获取子字符串在字符串中出现的次数
     * @param str
     * @param substring
     * @return
     */
    public static int substringCount(String str,String substring){
        int i = 0;
        while(str.lastIndexOf(substring) > -1){
            str = str.substring(0,str.lastIndexOf(substring));
            i++;
        }
        return i;
    }

    /**
     * 格式化字符串，对字符串中的占位符进行替换
     * @param format   需要格式化的字符串
     * @param orgs     格式化数据源
     * @return
     */
    public static String format(String format,Object ... orgs) {
        MessageFormat messageFormat = new MessageFormat(format);
        return messageFormat.format(orgs);
    }
}
