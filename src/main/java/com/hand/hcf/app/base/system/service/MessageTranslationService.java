package com.hand.hcf.app.base.system.service;

import com.hand.hcf.app.base.user.domain.User;
import com.hand.hcf.app.base.user.service.UserService;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class MessageTranslationService {
    @Autowired
    MessageSource messageSource;
    @Autowired
    private UserService userService;

    /**
     * 根据用户oid翻译
     * @param isUseCompanyLanguage 是否使用公司主语言
     * @param userOid
     * @param messageKey
     * @param objs
     * @return
     */
    public String getMessageDetailByCode(Boolean isUseCompanyLanguage, UUID userOid, String messageKey, Object... objs) {
        //userOid为空默认使用中文翻译
        if (userOid == null){
            return messageSource.getMessage(messageKey, objs, Locale.CHINA);
        }

        User user = userService.getByUserOid(userOid);

        String language = null;
        if (isUseCompanyLanguage) {
            language = userService.getLanguageByUser(user);
        }else{
            //用户查询不到，默认使用zh_CN
            language = user==null ? LanguageEnum.ZH_CN.getKey() : user.getLanguage();
        }

        Locale locale = (null == language) ? Locale.CHINA : new Locale(language);
        try {
            if (user != null && user.getLanguage() != null) {
                return messageSource.getMessage(messageKey, objs, locale);
            }
            return messageSource.getMessage(messageKey, objs, Locale.CHINA);
        } catch (NoSuchMessageException e) {
            return "";
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 根据语言翻译
     * @param language
     * @param messageKey
     * @param objs
     * @return
     */
    public String getMessageDetailByCode(String language, String messageKey, Object... objs) {
        Locale locale = (null == language) ? Locale.CHINA : new Locale(language);
        try {
            return messageSource.getMessage(messageKey, objs, locale);
        } catch (NoSuchMessageException e) {
            return "";
        } catch (Exception e) {
            throw e;
        }
    }
}
