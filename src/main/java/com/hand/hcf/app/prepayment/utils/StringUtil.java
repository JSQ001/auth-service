package com.hand.hcf.app.prepayment.utils;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by yangqi on 2016/7/13.
 */
public class StringUtil {

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

}
