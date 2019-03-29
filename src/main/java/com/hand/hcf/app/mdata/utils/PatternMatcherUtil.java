package com.hand.hcf.app.mdata.utils;

import com.hand.hcf.core.exception.BizException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatcherUtil {

    // 模式对象
    private static Pattern pattern = null;
    // code统一验证表达式
    public final static String CODE_VALIDATION_REGEX = "^[A-Za-z0-9_`~!@#$%^&*()+=|{}:;,\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：、·\"”“’。，、？—\\-～¥《》ⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫXIIIXIVXVXVI]{1,36}$";
    // code包含中文表达式
    public final static String CODE_INCLUDES_CHINESE_REGEX = "[\u4e00-\u9fbb]+";

    public final static String NUMBER_REGEX = "^-?[1-9]\\d*$";

    public final static String DOUBLE_REGEX = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";

    // 序号正则表达式，最多允许20位
    public final static String SEQUENCE_NUMBER_REGEX = "^[1-9]{1}[0-9]{0,19}$";

    /**
     * 根据输入的文本和表达式验证是否符合
     * @param text：文本
     * @param regex：表达式
     * @return：true 符合 false 不符合
     */
    public static boolean validationPatterMatcherRegex(String text, String regex){
        pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(text);
        return match.matches();
    }

    /**
     * 验证编码统一规则
     * @param code：编码
     * @return
     */
    public static boolean validateCodeRegex(String code){
        return validationPatterMatcherRegex(code,CODE_VALIDATION_REGEX);
    }

    public static void commonCodeCheck(String code){
        if(code.length() > 36){
            throw new BizException(RespCode.CODE_LENGTH_LT_36);
        }else if(PatternMatcherUtil.isChineseCharacterRegex(code)) {
            throw new BizException(RespCode.CODE_CANT_CHINESE);
        }else if (!PatternMatcherUtil.validateCodeRegex(code)) {
            throw new BizException(RespCode.CODE_CANT_ILLEGAL);
        }
    }

    public static void commonCodeCheckReg(String code){
        if(PatternMatcherUtil.isChineseCharacterRegex(code)) {
            throw new BizException(RespCode.CODE_CANT_CHINESE);
        }else if (!PatternMatcherUtil.validateCodeRegex(code)) {
            throw new BizException(RespCode.CODE_CANT_ILLEGAL);
        }
    }

    /**
     * 验证编码是否包含中文
     * @param code：编码
     * @return
     */
    public static boolean isChineseCharacterRegex(String code) {
        for (int i = 0; i < code.length(); i++) {
            if (code.substring(i, i + 1).matches(CODE_INCLUDES_CHINESE_REGEX)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证字符串是否为数字
     * @param str：字符串值
     * @return
     */
    public static boolean isNumberRegex(String str) {
        //采用正则表达式的方式来判断一个字符串是否为数字，这种方式判断面比较全
        //可以判断正负、整数小数

        boolean isInt = validationPatterMatcherRegex(str,NUMBER_REGEX);
        boolean isDouble = validationPatterMatcherRegex(str,DOUBLE_REGEX);

        return isInt || isDouble;
    }

    /**
     * 验证序号
     * @param sequenceNumber：序号
     * @return
     */
    public static boolean validateSequenceNumberRegex(String sequenceNumber){
        return validationPatterMatcherRegex(sequenceNumber,SEQUENCE_NUMBER_REGEX);
    }
}
