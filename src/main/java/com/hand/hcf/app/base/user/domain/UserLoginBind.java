package com.hand.hcf.app.base.user.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@TableName(value = "sys_user_login_bind")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginBind extends DomainLogicEnable {

    @NotNull
    @TableField(value = "user_oid")
    protected UUID userOid;

    @NotNull
    protected String login;

    @NotNull
    protected int bindType;

    protected boolean isActive;


}
