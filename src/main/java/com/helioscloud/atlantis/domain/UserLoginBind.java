/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Transy on 2017/5/17.
 */
@TableName(value = "art_user_login_bind")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserLoginBind implements Serializable {


    protected Long id;

    @TableField(value = "user_oid")
    protected UUID userOID;


    protected String login;


    protected int bindType;

    protected boolean isActive;

    @TableField(value = "is_enabled")
    private Boolean enabled;
    @TableField(value = "is_deleted")
    private Boolean deleted;


}
