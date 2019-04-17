package com.hand.hcf.app.base.system.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.UniqueField;
import com.hand.hcf.app.core.domain.DomainI18n;
import com.hand.hcf.app.core.domain.DomainLogic;
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
@TableName("sys_serve_locale")
public class ServeLocale extends DomainLogic {
    //应用ID
    @NotNull
    @TableField("application_id")
    private Long applicationId;

    //应用代码
    @NotNull
    @TableField("application_code")
    private String applicationCode;

    //界面key值
    @NotNull
    @UniqueField
    @TableField("key_code")
    private String keyCode;

    //key描述
    @NotNull
    @TableField("key_description")
    private String keyDescription;

    //多语言类型
    @NotNull
    @TableField("category")
    private String category;

    //语言
    @NotNull
    @UniqueField
    @TableField("language")
    private String language;

    //多语言类型名称
    @TableField(exist = false)
    private String categoryName;
}
