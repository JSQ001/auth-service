package com.hand.hcf.app.core.domain.enumeration;

public enum LanguageEnum implements BusinessEnum {
    ZH_CN("zh_cn"),
    EN_US("en_us"), //英语
    MS("ms"), //马来语
    JA("jp");//日本语言


    private String key;

    LanguageEnum(String key) {
        this.key = key;
    }

    public static LanguageEnum parse(String key) {
        for (LanguageEnum fieldType : LanguageEnum.values()) {
            if (fieldType.getKey().equals(key)) {
                return fieldType;
            }
        }
        return null;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
