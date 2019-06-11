package com.hand.hcf.app.expense.application.enums;


import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

/**
 * <p>
 *  关闭状态枚举类
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/2/26
 */
public enum ClosedTypeEnum implements IEnum {

    /* 关闭状态枚举类*/
    NOT_CLOSED(1001, "未关闭"),
    PARTIAL_CLOSED(1002, "部分关闭"),
    CLOSED(1003, "已关闭");
    private Integer id;
    private String desc;
    ClosedTypeEnum(Integer id, String desc){
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public Serializable getValue() {
        return this.id;
    }

}
