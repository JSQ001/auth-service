package com.hand.hcf.app.core.locale;

import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/15
 */
public class HcfResolveLocale {

    /**
     * 获取语言信息
     * 优先级：token中携带的语言信息 -> 请求头中携带的语言信息 -> 默认语言信息
     * @return
     */
    public static Locale resolveLocale() {
        //获取浏览器语言
        String language =null;
        try {
            //这里获取语言的逻辑与LoginInformationUtil中相同，都是取当前用户信息中的language
            language = LoginInformationUtil.getCurrentLanguage();
            if (!StringUtils.isEmpty(language)) {
                return new Locale(language.toLowerCase());
            } else {
                return new Locale(Locale.CHINA.toString().toLowerCase());
            }
        } catch (Exception e) {
            return new Locale(Locale.CHINA.toString().toLowerCase());
        }
    }
}
