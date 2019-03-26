package com.hand.hcf.app.common.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

/**
 * <p>
 *  创建方式
 * </p>
 *
 * @Author: weishan
 * @Date: 2019/2/27
 */
public enum UserStatusEnum implements IEnum {
    /**
     * 创建方式
     */
    VALID(1001,"有效"),
    TRANSIT(1002,"待失效"),
    INVALID(1003,"失效");

    private Integer id;
    private String desc;

    UserStatusEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }
    public String getDesc() {
        return desc;
    }

    @Override
    public Serializable getValue() {
        return this.id;
    }}
