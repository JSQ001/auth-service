package com.hand.hcf.app.auth.domain.enumeration;

/**
 * @author qingsheng.chen
 * @date 2018/1/9 15:31
 * @description 功能字符常量
 */
public enum Function {
    /**
     * QR : 扫一扫功能
     * PC_LOGIN : 扫码登录
     */
    QR_PC_LOGIN("PC_LOGIN");

    private static final String URL_PREFIX = "&function=";

    private String content;

    Function(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String urlContent() {
        return URL_PREFIX + this.content;
    }
}
