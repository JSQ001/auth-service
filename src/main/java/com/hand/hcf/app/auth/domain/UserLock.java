package com.hand.hcf.app.auth.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.core.domain.DomainLogic;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Created by caixiang on 2017/7/19.
 */
@Data
@Builder
@TableName("sys_user_lock")
public class UserLock extends DomainLogic {
    @TableField(value = "user_id", strategy = FieldStrategy.NOT_NULL)
    private Long userId;
    @TableField(value = "client_ip", strategy = FieldStrategy.NOT_NULL)
    private String clientIp;
    @TableField(value = "user_agent", strategy = FieldStrategy.NOT_NULL)
    private String userAgent;
    private ZonedDateTime lockedDate;

}
