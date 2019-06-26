package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.UniqueField;
import com.hand.hcf.app.core.domain.DomainLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @description: 中控多语言表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "前端多语言表")
@TableName("sys_front_locale")
public class FrontLocale extends DomainLogic{

    @ApiModelProperty(value = "应用ID")
    @NotNull
    @TableField("application_id")
    private Long applicationId;

    @ApiModelProperty(value = "应用代码")
    @NotNull
    @TableField("application_code")
    private String applicationCode;

    @ApiModelProperty(value = "界面key值")
    @NotNull
    @UniqueField
    @TableField("key_code")
    private String keyCode;

    @ApiModelProperty(value = "key描述")
    @NotNull
    @TableField("key_description")
    private String keyDescription;

    @ApiModelProperty(value = "语言")
    @NotNull
    @UniqueField
    @TableField("language")
    private String language;

    @ApiModelProperty(value = "租户id")
    @TableField("tenant_id")
    private Long tenantId;

}
