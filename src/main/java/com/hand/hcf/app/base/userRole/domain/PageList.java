package com.hand.hcf.app.base.userRole.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.Domain;
import com.hand.hcf.app.core.domain.DomainI18n;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 页面表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/28
 */
@Data
@TableName("sys_page_list")
public class PageList extends DomainI18n {
    //页面名称
    @NotNull
    @I18nField
    @TableField("page_name")
    private String pageName;

    //页面对应本地文件的地址
    @NotNull
    @TableField("file_path")
    private String filePath;

    //页面路由
    @NotNull
    @TableField("page_router")
    private String pageRouter;

    //功能路由
    @TableField("function_router")
    private String functionRouter;

    //目录路由
    @TableField("content_router")
    private String contentRouter;

    //页面地址
    @NotNull
    @TableField("page_url")
    private String pageUrl;
    /**
     * 全路由
     */
    private String fullRouter;

    private String fullUrl;
}
