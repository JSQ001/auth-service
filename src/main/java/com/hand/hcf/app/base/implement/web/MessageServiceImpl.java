package com.hand.hcf.app.base.implement.web;

import com.hand.hcf.app.core.handler.MessageHandler;
import com.hand.hcf.app.core.web.dto.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class MessageServiceImpl implements MessageHandler {
    @Override
    public MessageDTO getMessageDTO(String messageCode) {
        return null;
    }

    @Override
    public MessageDTO getMessageDTO(String messageCode, Locale locale) {
        return null;
    }

    @Override
    public List<MessageDTO> getMessageDTOS(String messageCode) {
        return null;
    }
}
