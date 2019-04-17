package com.hand.hcf.app.core.service;


import com.hand.hcf.app.core.handler.MessageHandler;
import com.hand.hcf.app.core.locale.HcfResolveLocale;
import com.hand.hcf.app.core.util.StringUtil;
import com.hand.hcf.app.core.web.dto.MessageDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class MessageService {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    MessageHandler messageHandler;

    public String getMessageDetailByCode(String messageCode, Object... objs) {
        if(StringUtils.isEmpty(messageCode)){
            return "";
        }
        MessageDTO messageDTO = getMessage(messageCode, objs);
        if(messageDTO != null){
            return messageDTO.getKeyDescription();
        }
        return "";
    }

    public MessageDTO getMessageDTOByCode(String messageCode, Object... objs) {
        MessageDTO message = getMessage(messageCode, objs);
        return message;
    }

    public String getMessageFromSource(String code, Object... objs){
        return messageSource.getMessage(code, objs, getLocale());
    }

    private Locale getLocale() {
        return HcfResolveLocale.resolveLocale();
    }

    public String getMessage(String messageCode, String message, Object... objs){
        String messageDetailByCode = getMessageDetailByCode(messageCode, objs);
        return StringUtils.isEmpty(messageDetailByCode) ? message : messageDetailByCode;
    }

    /**
     * 获取多语言信息，消息模块代码默认为message，语言默认为当前语言
     * @param messageCode   消息代码
     * @return
     */
    public MessageDTO getMessage(String messageCode, Object... objs){
        if(StringUtils.isEmpty(messageCode)){
            return null;
        }
        MessageDTO message = messageHandler.getMessageDTO(messageCode);
        if(message == null){
            return null;
        }
        message.setKeyDescription(StringUtil.format(message.getKeyDescription(),objs));
        return message;
    }

    /**
     * 获取多语言信息，消息模块代码默认为message
     * @param messageCode   消息代码
     * @param locale        语言
     * @return
     */
    public MessageDTO getMessage(String messageCode, Locale locale, Object... objs){
        if(StringUtils.isEmpty(messageCode)){
            return null;
        }
        MessageDTO message = messageHandler.getMessageDTO(messageCode,locale);
        if(message == null){
            return null;
        }
        message.setKeyDescription(StringUtil.format(message.getKeyDescription(),objs));
        return message;
    }

    /**
     * 获取多语言信息，返回全部语言信息,消息模块代码默认为message
     * @param messageCode
     * @return
     */
    public List<MessageDTO> getMessages(String messageCode){
        if(StringUtils.isEmpty(messageCode)){
            return new ArrayList<>();
        }
        List<MessageDTO> messageDTOS = messageHandler.getMessageDTOS(messageCode);
        if(messageDTOS == null){
            return new ArrayList<>();
        }
        return messageDTOS;
    }

}
