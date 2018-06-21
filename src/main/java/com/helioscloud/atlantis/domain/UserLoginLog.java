package com.helioscloud.atlantis.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.helioscloud.atlantis.util.deserialize.CustomDateTimeDeserializer;
import com.helioscloud.atlantis.util.serialize.CustomDateTimeSerializer;
import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author Created by Transy on 2017-12-21.
 */
@Data
public class UserLoginLog implements Serializable {
    private UUID userOID;

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

    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private DateTime createdDate = DateTime.now();

}
