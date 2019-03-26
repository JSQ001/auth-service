package com.hand.hcf.app.mdata.parameter.enums;

import com.baomidou.mybatisplus.enums.IEnum;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/27 09:15
 */
public enum ParameterValueTypeEnum implements IEnum {
    VALUE_LIST("1001"),//值列表
    API("1002"),//接口
    TEXT("1003"),//文本
    NUMBER("1004"),//数字
    DATE("1005");//日期

    private String value;

    ParameterValueTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

}
