

package com.hand.hcf.app.core.util;

import com.hand.hcf.app.core.component.ApplicationContextProvider;
import org.springframework.context.i18n.LocaleContextHolder;

public final class MessageUtil {
    public static String getMessage(String messageKey, Object... args) {
        return ApplicationContextProvider.getApplicationContext().getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }
}
