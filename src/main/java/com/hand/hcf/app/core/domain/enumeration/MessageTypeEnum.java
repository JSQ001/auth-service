package com.hand.hcf.app.core.domain.enumeration;

public enum MessageTypeEnum implements BusinessEnum {
    ERROR("ERROR"),
    WARN("WARN"),
    NORMAL("NORMAL");


    private String key;

    MessageTypeEnum(String key) {
        this.key = key;
    }

    public static MessageTypeEnum parse(String key) {
        for (MessageTypeEnum fieldType : MessageTypeEnum.values()) {
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
