package com.hand.hcf.app.base.implement;

import com.hand.hcf.core.handler.MessageHandler;
import com.hand.hcf.core.web.dto.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class messageServiceImpl implements MessageHandler {
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
