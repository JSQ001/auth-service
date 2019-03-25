package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.domain.Domain;
import com.hand.hcf.core.domain.DomainI18n;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 功能页面关联表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
@Data
@TableName("sys_function_page_relation")
public class FunctionPageRelation extends Domain {
    //功能id
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("function_id")
    private Long functionId;

    //页面id
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("page_id")
    private Long pageId;



    //功能名称
    @TableField(exist = false)
    private String functionName;

    //页面名称
    @TableField(exist = false)
    private String pageName;
}
