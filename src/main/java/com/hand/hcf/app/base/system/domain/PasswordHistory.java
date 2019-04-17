package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.util.UUID;

/**
 * Created by Transy on 2017/5/17.
 */
@Data
@TableName(value = "sys_password_history")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordHistory extends Domain {
    @TableField(value = "user_oid")
    protected UUID  userOid;


    @TableField(value = "password_hash")
    protected String passwordHash;

}
