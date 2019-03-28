package com.hand.hcf.app.base.user.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户权限关联表实体
 * Created by Strive on 18/3/14.
 */
@TableName(value = "sys_user_authority")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority implements Serializable {
    @TableField(value = "user_id")
    private Long userId;
    @TableField(value = "authority_name")
    private String authorityName;
}
