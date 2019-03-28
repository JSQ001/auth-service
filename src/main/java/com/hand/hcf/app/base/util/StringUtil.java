package com.hand.hcf.app.base.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangqi on 2016/7/13.
 */
public class StringUtil {

    private static final String SET = "set";

    public static String getGPSString(String location, Double latitude, Double longitude) {
        return "{\"address\":\"" + location + "\",\"latitude\":" + latitude + ",\"longitude\":" + longitude + "}";
    }

    public static String listToString(List<String> list) {
        String ls = "";
        for (String s : list) {
            if ("".equals(ls)) {
                ls = s;
            } else {
                ls = ls + "," + s;
            }
        }
        return ls;
    }

  

    /**
     * 过滤特殊字符方法
     *
     * @param str
     * @return
     */
    public static String filterSpecialCharacters(String str) {
        //特殊字符过滤
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    /**
     * 转义sql查询特殊字符
     * @param str
     * @return
     */
    public static String escapeSpecialCharacters(String str){
        //特殊字符 用于sql模糊查询时特殊字符的转义标识符 (й)
        String s = null;
        try {
            s = new String(new byte[]{-48,-71},"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if(StringUtils.isEmpty(str)){
            return "";
        }
        return str.replaceAll(" ","")
            .replaceAll("'","''")
            .replaceAll("%",s+"%")
            .replaceAll("_",s+"_");
    }

    /**
     * 根据分隔符，将字符拆分成为字符数组
     *
     * @param str
     * @param separator
     * @return
     */
    public static List<String> stringToList(String str, String separator) {
        if(StringUtils.isEmpty(str)) {
            return null;
        }
        String[] strings = str.split(separator);
        List<String> stringList = new ArrayList<>(strings.length);
        Collections.addAll(stringList, strings);
        return stringList;
    }

    /**
     * 根据分隔符，将字符数组拼接成字符
     *
     * @param stringList
     * @param separator
     * @return
     */
    public static String listToString(List<String> stringList, String separator) {
        if(CollectionUtils.isEmpty(stringList)) {
            return null;
        }
        String s = "";
        StringBuilder stringBuilder = new StringBuilder(s);
        for (String s1 : stringList) {
            if (StringUtils.isEmpty(s)) {
                s = stringBuilder.append(s1).toString();
            } else {
                s = stringBuilder.append(separator).append(s1).toString();
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 半角转全角
     *
     * @param str
     * @return
     */
    public static String ToSBC(String str) {
        if(StringUtils.isEmpty(str)){
            return null;
        }
        char c[] = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param str
     * @return
     */
    public static String ToDBC(String str) {
        char c[] = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);
        return returnString;
    }

    /**
     * 获取对应属性的set方法名
     *
     * @param name
     * @return
     */
    public static String getFieldSetMethod(String name) {
        return StringUtils.isBlank(name) ? "" : SET.concat(name.substring(0,1).toUpperCase().concat(name.substring(1)));
    }

    public static int[] parseString(String s){
        String[] strings = s.split("\\.");
        int[] result = new int[strings.length];
        for(int i=0; i<strings.length; i++){
            result[i] = Integer.valueOf(strings[i]);
        }
        return result;
    }

    /**
     * if(s1>=s2) return true; if(s1<s2) return false;
     * @param s1
     * @param s2
     * @return
     */
    public static boolean judgeVersion(String s1, String s2){
        if(StringUtils.isBlank(s1) || StringUtils.isBlank(s2)){
            return !StringUtils.isBlank(s1) || StringUtils.isBlank(s2);
        }
        int[] i1 = parseString(s1);
        int[] i2 = parseString(s2);
        int min = i1.length>i2.length?i2.length:i1.length;
        for(int i=0; i<min; i++){
            if(i1[i] > i2[i]){
                return true;
            } else if(i1[i] < i2[i]){
                return false;
            }
            if(i+1 == min){
                return i1.length>i2.length;
            }
        }
        return true;
    }
}
