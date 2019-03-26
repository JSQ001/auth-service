package com.hand.hcf.app.mdata.system.enums;

/**
 * @author : mawei
 * @description : 汇率来源枚举类
 * @since : 2018/3/19
 */
public enum CurrencyRateSourceEnum {
    /**
     * 欧行
     */
    ECB("ECB"),
    /**
     * 手工
     */
    MANUAL("MANUAL"),
    /**
     * openAPI导入
     */
    OPEN_API("OPEN_API");

    private String source;

    CurrencyRateSourceEnum(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
