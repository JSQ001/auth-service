package com.hand.hcf.app.mdata.contact.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user_group_user")
public class UserGroupUser implements Serializable {

    public UserGroupUser(UUID userOid, UUID userGroupOId) {
        this.userOid = userOid;
        this.userGroupOId = userGroupOId;
    }

    @TableField("user_id")
    private Long userId;
    @TableField("user_group_id")
    private Long userGroupId;
    @TableField(exist = false)
    private UUID userOid;
    @TableField(exist = false)
    private UUID userGroupOId;
}
