package com.hand.hcf.app.base.user.domain;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class UserLoginLog implements Serializable {
    private String id;
    private UUID userOid;

    private String deviceId;

    private String login;

    private String loginType;

    private int status;

    private String osVersion;

    private String appVersion;

    private String pixelRatio;

    private String deviceBrand;

    private String deviceModel;

    private String deviceName;

    private ZonedDateTime createdDate = ZonedDateTime.now();
}
