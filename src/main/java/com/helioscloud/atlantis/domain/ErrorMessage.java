package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.security.PrivateKey;

/**
 * @description: 报错信息
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/10/18
 */
@Data
@TableName("sys_error_message")
public class ErrorMessage extends Domain {
    //模块代码
    @NotNull
    @TableField("module_code")
    private String moduleCode;

    //语言
    @NotNull
    @TableField("language")
    private String language;

    //报错信息代码
    @NotNull
    @TableField("error_code")
    private String errorCode;

    //报错信息
    @NotNull
    @TableField("error_message")
    private String errorMessage;
}
