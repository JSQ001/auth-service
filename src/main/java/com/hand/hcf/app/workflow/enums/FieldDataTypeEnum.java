package com.hand.hcf.app.workflow.enums;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */


import com.hand.hcf.core.domain.enumeration.BusinessEnum;

/**
 * Created by lichao on 18/5/15.
 * 字段数据类型
 */
public enum FieldDataTypeEnum implements BusinessEnum {
    /**
     * 文本
     */
    TEXT("TEXT"),
    /**
     * 整数
     */
    LONG("LONG"),
    /**
     * 浮点数
     */
    DOUBLE("DOUBLE"),
    /**
     * 日期
     */
    DATE("DATE"),
    /**
     * 布尔值
     */
    BOOLEAN("BOOLEAN"),
    ;
    FieldDataTypeEnum(String key){
        this.key = key;
    }
    private String key;
    @Override
    public String getKey() {
        return this.key;
    }

    /**
     * 根据key返回类型
     * @param key
     * @return
     */
    public static FieldDataTypeEnum parse(String key) {
        for (FieldDataTypeEnum fieldDataType : FieldDataTypeEnum.values()) {
            if (fieldDataType.getKey().equals(key)) {
                return fieldDataType;
            }
        }
        return null;
    }
}
