package com.hand.hcf.app.base.user.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * A UserSearchHistory .
 */
@Data
@TableName("sys_user_search_history")
public class UserSearchHistory {

    protected Long id;

    @NotNull
    private UUID searchBy;

    @NotNull
    @TableField( "user_oid")
    private UUID userOid;

    @NotNull
    @JsonIgnore
    private ZonedDateTime createdDate;
}
