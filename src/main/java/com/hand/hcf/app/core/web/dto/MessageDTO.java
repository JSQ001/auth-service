package com.hand.hcf.app.core.web.dto;

import lombok.Data;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/12/10 16:23
 * @remark 消息DTO
 */
@Data
public class MessageDTO {

    /**
     * 消息代码
     */
    private String keyCode;
    /**
     * 语言
     */
    private String language;
    /**
     * 语言描述
     */
    private String keyDescription;
    /**
     * 应用代码
     */
    private String applicationCode;
    /**
     * 消息类型
     */
    private String category;

}
