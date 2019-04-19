package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.UniqueField;
import com.hand.hcf.app.core.domain.DomainI18n;
import com.hand.hcf.app.core.domain.DomainLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @description: 服务端多语言表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "服务端多语言表")
@TableName("sys_serve_locale")
public class ServeLocale extends DomainLogic {

    @NotNull
    @ApiModelProperty(value = "应用ID")
    @TableField("application_id")
    private Long applicationId;

    @NotNull
    @ApiModelProperty(value = "应用代码")
    @TableField("application_code")
    private String applicationCode;

    @NotNull
    @UniqueField
    @ApiModelProperty(value = "界面key值")
    @TableField("key_code")
    private String keyCode;

    @NotNull
    @ApiModelProperty(value = "key描述")
    @TableField("key_description")
    private String keyDescription;

    @NotNull
    @ApiModelProperty(value = "多语言类型")
    @TableField("category")
    private String category;

    @NotNull
    @UniqueField
    @ApiModelProperty(value = "语言")
    @TableField("language")
    private String language;

    @ApiModelProperty(value = "多语言类型名称")
    @TableField(exist = false)
    private String categoryName;

    @ApiModelProperty(value = "租户id")
    @TableField("tenant_id")
    private Long tenantId;
}
