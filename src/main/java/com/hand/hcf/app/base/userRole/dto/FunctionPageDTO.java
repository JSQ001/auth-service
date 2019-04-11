package com.hand.hcf.app.base.userRole.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FunctionPageDTO {
    //功能id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long functionId;



    //页面id
    private Long pageId;

    //页面名称
    private String pageName;

    //页面对应本地文件的地址
    private String filePath;

    //目录路由
    private String contentRouter;

    //功能路由
    private String functionRouter;

    //页面路由
    private String pageRouter;

    //页面地址
    private String pageUrl;

    /**
     * 全路由
     */
    private String fullRouter;

    private Long contentId;

    private String fullUrl;

}
