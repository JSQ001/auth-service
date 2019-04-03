package com.hand.hcf.app.workflow.enums;

import com.hand.hcf.core.enums.SysEnum;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
public enum FieldType implements SysEnum {
    TEXT(101),//文本
    LONG(102),//整数
    DATETIME(103),//时间
    DOUBLE(104),//浮点数
    DATE(105),//日期
    CUSTOM_ENUMERATION(106),//值列表
    GPS(107),//gps
    BOOLEAN(108),//boolean
    LIST(109),//list
    LOCATION(110),//地点控件
    PARTICIPANT(111),//同行人
    PARTICIPANTS(112),//参与人
    MONTH(113),//月份
    POSITIVE_INTEGER(114),//正整数
    START_DATE_AND_END_DATE(115)//开始结束日期
    ;
    private Integer id;

    FieldType(Integer id) {
        this.id = id;
    }

    public static FieldType parse(Integer id) {
        for (FieldType fieldType : FieldType.values()) {
            if (fieldType.getId().equals(id)) {
                return fieldType;
            }
        }
        return null;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * 返回字段的数据类型
     * @return
     */
    public FieldDataTypeEnum getFieldDataTypeEnum(){
        FieldDataTypeEnum fieldDataType = null;
        switch (this){
            case TEXT:
            case LIST:
            case LOCATION:
            case PARTICIPANT:
            case PARTICIPANTS:
            case CUSTOM_ENUMERATION:
            case START_DATE_AND_END_DATE:
            case GPS:
                fieldDataType = FieldDataTypeEnum.TEXT;
                break;
            case LONG:
            case POSITIVE_INTEGER:
                fieldDataType = FieldDataTypeEnum.LONG;
                break;
            case DOUBLE:
                fieldDataType = FieldDataTypeEnum.DOUBLE;
                break;
            case DATE:
            case MONTH:
            case DATETIME:
                fieldDataType = FieldDataTypeEnum.DATE;
                break;
            case BOOLEAN:
                fieldDataType = FieldDataTypeEnum.BOOLEAN;
                break;
            default:fieldDataType = FieldDataTypeEnum.DATE;
        }
        return fieldDataType;
    }
}
