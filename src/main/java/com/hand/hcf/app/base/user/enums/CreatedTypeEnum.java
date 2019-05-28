package com.hand.hcf.app.base.user.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;

/**
 * <p>
 *  创建方式
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/1/31
 */
public enum CreatedTypeEnum implements IEnum {
    /**
     * 创建方式
     */
    MANUAL(1001,"手工创建"),
    INIT_TENANT(1003, "创建租户初始化"),
    INTERFACE(1002,"来源接口");

    private Integer id;
    private String desc;

    CreatedTypeEnum(Integer id, String desc) {
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
