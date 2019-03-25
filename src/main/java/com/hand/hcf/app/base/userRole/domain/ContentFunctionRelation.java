package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.DomainI18n;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 目录功能关联表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
@Data
@TableName("sys_content_function_rel")
public class ContentFunctionRelation extends DomainI18n {
    //目录id
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("content_id")
    private Long contentId;

    //功能id
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("function_id")
    private Long functionId;



    //目录名称
    @TableField(exist = false)
    private String contentName;

    //功能名称
    @TableField(exist = false)
    private String functionName;
}
