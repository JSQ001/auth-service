package com.hand.hcf.app.core.handler;

import com.hand.hcf.app.core.web.dto.MessageDTO;

import java.util.List;
import java.util.Locale;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/12/10 14:34
 * @remark 多语言信息处理器
 */
public interface MessageHandler {

    /**
     * 获取多语言信息，消息模块代码默认为message，语言默认为当前语言
     * @param messageCode   消息代码
     * @return
     */
    MessageDTO getMessageDTO(String messageCode);


    /**
     * 获取多语言信息，消息模块代码默认为message
     * @param messageCode   消息代码
     * @param locale        语言
     * @return
     */
    MessageDTO getMessageDTO(String messageCode, Locale locale);


    /**
     * 获取多语言信息，返回全部语言信息,消息模块代码默认为message
     * @param messageCode
     * @return
     */
    List<MessageDTO> getMessageDTOS(String messageCode);

}
