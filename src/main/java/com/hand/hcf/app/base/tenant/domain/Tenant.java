package com.hand.hcf.app.base.tenant.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;

import java.io.Serializable;

@TableName("sys_tenant")
@Data
public class Tenant extends DomainI18nEnable implements Serializable{
    private static final long serialVersionUID = -7982080729279166839L;
    @I18nField
    private String tenantName;
    private String tenantShortName;
    private Integer licenseLimit;
    private String status;
    private String tenantCode;

    private Long logoId;
    private Boolean showCustomLogo;

    //UTM
    private String utmSource;
    private String utmCampaign;
    private String utmMedium;
    private String countryCode;

    @TableField(exist = false)
    private String logoURL;

    private Boolean enableNewControl;

    /**
     * 付费标记
     */
    private Boolean licensed;
}
