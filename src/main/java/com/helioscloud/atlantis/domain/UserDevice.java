package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.helioscloud.atlantis.util.deserialize.CustomDateTimeDeserializer;
import com.helioscloud.atlantis.util.serialize.CustomDateTimeSerializer;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by markfredchen on 16/3/1.
 */
@Data
@TableName("art_user_device")
public class UserDevice {
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("user_oid")
    private UUID userOID;

    @TableField("device_id")
    private String deviceID;

    @TableField("vendor_type_id")
    private Integer vendorTypeID;

    @TableField("platform_id")
    private Integer platformID;

    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private DateTime createdDate;

    private String osVersion;

    private String appVersion;

    private String pixelRatio;

    private String deviceBrand;

    private String deviceModel;

    private String deviceName;

    private String remark;

    private Integer status;
}
