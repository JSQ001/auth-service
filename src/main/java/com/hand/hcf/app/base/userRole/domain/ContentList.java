package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.annotation.UniqueField;
import com.hand.hcf.core.domain.DomainI18n;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
@Data
@TableName("sys_content_list")
public class ContentList extends DomainI18n {
    //目录名称
    @NotNull
    @I18nField
    @UniqueField
    @TableField("content_name")
    private String contentName;

    //目录路由
    @NotNull
    @UniqueField
    @TableField("content_router")
    private String contentRouter;

    //图标
    @NotNull
    @TableField("icon")
    private String icon;

    //上级目录id
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("parent_id")
    private Long parentId;

    //优先级
    @NotNull
    @TableField("sequence_number")
    private Integer sequenceNumber;

    //是否有子目录
    @TableField("has_son_content")
    private Boolean hasSonContent;
}
