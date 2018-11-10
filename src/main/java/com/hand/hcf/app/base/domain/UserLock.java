package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

/**
 * Created by caixiang on 2017/7/19.
 */
@Data
@Builder
@TableName("atl_user_lock")
public class UserLock {
    private Long id;
    @TableField(value = "user_id", strategy = FieldStrategy.NOT_NULL)
    private Long userId;
    @TableField(value = "client_ip", strategy = FieldStrategy.NOT_NULL)
    private String clientIp;
    @TableField(value = "user_agent", strategy = FieldStrategy.NOT_NULL)
    private String userAgent;
    private Boolean isDeleted;
    private DateTime lockedDate;

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
