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
public class ContentFunctionDTO {

    //目录id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contentId;

    //目录名称
    private String contentName;

    //目录路由
    private String contentRouter;

    //图标
    private String icon;

    //父目录id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    //优先级
    private Integer contentSequenceNumber;

    //是否有子目录
    private Boolean hasSonContent;



    //功能id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long functionId;

    //功能名称
    private String functionName;

    //功能路由
    private String functionRouter;

    //功能参数
    private String param;

    //优先级
    private Integer functionSequenceNumber;

    //页面id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long pageId;

    //功能图标
    private String functionIcon;

    //应用ID
    private Long applicationId;

    //应用代码
    private String applicationCode;
}
