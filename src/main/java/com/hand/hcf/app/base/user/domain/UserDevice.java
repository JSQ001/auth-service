package com.hand.hcf.app.base.user.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by markfredchen on 16/3/1.
 */
@Data
@TableName("sys_user_device")
public class UserDevice {
    @TableId

    private Long id;

    @TableField("user_oid")
    private UUID userOid;

    @TableField("device_id")
    private String deviceID;

    @TableField("vendor_type_id")
    private Integer vendorTypeID;

    @TableField("platform_id")
    private Integer platformID;

    private ZonedDateTime createdDate;

    private String osVersion;

    private String appVersion;

    private String pixelRatio;

    private String deviceBrand;

    private String deviceModel;

    private String deviceName;

    private String remark;

    private Integer status;
}
