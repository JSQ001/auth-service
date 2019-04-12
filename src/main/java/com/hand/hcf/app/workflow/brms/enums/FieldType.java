package com.hand.hcf.app.workflow.brms.enums;

import com.hand.hcf.app.workflow.enums.FieldDataTypeEnum;
import com.hand.hcf.core.enums.SysEnum;
import lombok.AllArgsConstructor;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/7
 */
@AllArgsConstructor
public enum FieldType implements SysEnum {
    TEXT(101,"String.valueOf(value) "),//文本
    LONG(102,"Long.valueOf(value) "),//整数
    DATETIME(103, "value"),//时间
    DOUBLE(104, "Double.valueOf(value)"),//浮点数
    DATE(105, "value"),//日期
    CUSTOM_ENUMERATION(106, "String.valueOf(value) "),//值列表
    GPS(107, "value"),//gps
    BOOLEAN(108, "Boolean.valueOf(value) "),//boolean
    LIST(109, "String.valueOf(value) "),//list
    LOCATION(110, "String.valueOf(value) "),//地点控件
    PARTICIPANT(111, "String.valueOf(value) "),//同行人
    PARTICIPANTS(112, "String.valueOf(value) "),//参与人
    MONTH(113, "String.valueOf(value) "),//月份
    POSITIVE_INTEGER(114, "Long.valueOf(value) "),//正整数
    START_DATE_AND_END_DATE(115, "String.valueOf(value) ")//开始结束日期
    ;
    private Integer id;
    private String value;


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

    public String getValue(){return this.value;}

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
