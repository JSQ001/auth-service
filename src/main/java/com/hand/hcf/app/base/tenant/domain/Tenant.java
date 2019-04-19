package com.hand.hcf.app.base.tenant.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description = "租户定义表")
@TableName("sys_tenant")
@Data
public class Tenant extends DomainI18nEnable implements Serializable{
    private static final long serialVersionUID = -7982080729279166839L;
    @ApiModelProperty(value = "租户名称")
    @I18nField
    private String tenantName;
    @ApiModelProperty(value = "租户简称")
    private String tenantShortName;
    @ApiModelProperty(value = "license上线")
    private Integer licenseLimit;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "租户编码")
    private String tenantCode;
    @ApiModelProperty(value = "logoId")
    private Long logoId;
    @ApiModelProperty(value = "是否显示自定义Logo")
    private Boolean showCustomLogo;
    @ApiModelProperty(value = "utmSource")
    private String utmSource;
    @ApiModelProperty(value = "utmCampaign")
    private String utmCampaign;
    @ApiModelProperty(value = "utmMedium")
    private String utmMedium;
    @ApiModelProperty(value = "币种")
    private String countryCode;
    @ApiModelProperty(value = "logo地址")
    @TableField(exist = false)
    private String logoURL;
    @ApiModelProperty(value = "是否启用新中控")
    private Boolean enableNewControl;
    @ApiModelProperty(value = "付费标记")
    private Boolean licensed;
    @ApiModelProperty(value = "是否系统级租户")
    private Boolean systemFlag;
}
