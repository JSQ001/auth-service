package com.hand.hcf.app.base.user.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.util.UUID;

@Data
@TableName(value = "sys_user_device_history")
public class UserDeviceHistory extends Domain {

    private Long id;
    @TableField(value = "user_oid", strategy = FieldStrategy.NOT_NULL)
    private UUID userOid;
    @TableField(value = "device_id", strategy = FieldStrategy.NOT_NULL)
    private String deviceID;
    @TableField(value = "vendor_type_id", strategy = FieldStrategy.NOT_NULL)
    private Integer vendorTypeID;
    @TableField(value = "platform_id")
    private Integer platformID;

    @TableField(value = "os_version")
    private String osVersion;
    @TableField(value = "app_version")
    private String appVersion;
    @TableField(value = "pixel_ratio")
    private String pixelRatio;
    @TableField(value = "device_brand")
    private String deviceBrand;
    @TableField(value = "device_model")
    private String deviceModel;
}
