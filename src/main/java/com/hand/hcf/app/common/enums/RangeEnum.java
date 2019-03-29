package com.hand.hcf.app.common.enums;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/13
 */
public enum RangeEnum {

    ALL("全部") ,
    SELECTED("已选"),
    NOTCHOOSE("未选");


    /**
     * 描述
     */
    private final String desc;

    RangeEnum(final String desc) {
        this.desc = desc;
    }


    public String getDesc() {
        return this.desc;
    }

}
