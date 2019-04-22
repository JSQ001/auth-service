package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.annotation.UniqueField;
import com.hand.hcf.app.core.domain.DomainI18n;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 功能表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
@Data
@TableName("sys_function_list")
public class FunctionList extends DomainI18n {
    //功能名称
    @NotNull
    @I18nField
    @UniqueField
    @TableField("function_name")
    private String functionName;

    //功能路由
    @NotNull
    @UniqueField
    @TableField("function_router")
    private String functionRouter;

    //功能参数
    @TableField("param")
    private String param;

    //优先级
    @NotNull
    @TableField("sequence_number")
    private Integer sequenceNumber;

    //页面id
    @TableField("page_id")
    private Long pageId;

    //功能图标
    @TableField("function_icon")
    private String functionIcon;

    //应用ID
    @TableField("application_id")
    private Long applicationId;

    private Long tenantId;

    private Long sourceId;
}
