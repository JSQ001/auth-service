package com.hand.hcf.app.base.user.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@TableName(value = "sys_user_version")
public class UserVersion {
    private Long id;
    private UUID userOid;
    private String platform;
    private String appVersion;
    private ZonedDateTime createDate;
    //APP小版本
    private String subAppVersion;
}
