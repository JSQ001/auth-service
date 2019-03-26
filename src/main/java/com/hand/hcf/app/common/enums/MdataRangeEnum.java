package com.hand.hcf.app.common.enums;

import com.hand.hcf.core.enums.SysEnum;

/**
 * @Autnor shouting.cheng
 * @date 2018/12/26
 */
public enum MdataRangeEnum implements SysEnum {
    ALL(2),
    SELECTED(1), //已选
    UN_SELECTED(0), //未选
    ;

    private Integer id;

    MdataRangeEnum(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
