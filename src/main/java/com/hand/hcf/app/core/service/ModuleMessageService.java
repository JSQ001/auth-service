package com.hand.hcf.app.core.service;


import com.hand.hcf.app.core.locale.HcfResolveLocale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ModuleMessageService {

    @Autowired
    MessageSource moduleMessageSource;

    public String getMessageDetailByCode(String messageCode, Object... objs) {

        try {
            return moduleMessageSource.getMessage(messageCode, objs, getLocale());
        } catch (NoSuchMessageException e) {
            return "";
        } catch (Exception e) {
            throw e;
        }
    }

    private Locale getLocale() {
        return HcfResolveLocale.resolveLocale();
    }

    public String getMessage(String messageCode, String message, Object... objs){
        String messageDetailByCode = getMessageDetailByCode(messageCode, objs);
        return StringUtils.isEmpty(messageDetailByCode) ? message : messageDetailByCode;
    }

}
