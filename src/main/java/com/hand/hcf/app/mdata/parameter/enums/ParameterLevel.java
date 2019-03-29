package com.hand.hcf.app.mdata.parameter.enums;

import com.baomidou.mybatisplus.enums.IEnum;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/27 09:41
 */
public enum ParameterLevel implements IEnum {
    TENANT("2001","tenant"),//租户级
    SOB("2002","sob"),//账套级
    COMPANY("2003","company");//公司级
    private String value;

    private String desc;

    ParameterLevel(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public String getDesc(){
        return this.desc;
    }
}
