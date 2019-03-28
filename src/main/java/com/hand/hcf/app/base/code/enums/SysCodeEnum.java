package com.hand.hcf.app.base.code.enums;

import com.baomidou.mybatisplus.enums.IEnum;

import java.io.Serializable;


/**
 * 系统代码代码类型枚举类
 */
public enum SysCodeEnum implements IEnum {
    CUSTOM(1001, "用户定义"),
    INIT(1002, "租户级初始化"),
    SYSTEM(1003, "系统级");

    private Integer id;

    private String desc;

    SysCodeEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static SysCodeEnum parse(Integer id) {
        for (SysCodeEnum sysCodeEnum : SysCodeEnum.values()) {
            if (sysCodeEnum.getId().equals(id)) {
                return sysCodeEnum;
            }
        }
        return null;
    }


    @Override
    public Serializable getValue() {
        return this.id;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }}


