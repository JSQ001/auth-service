package com.hand.hcf.app.core.locale;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Data
@Component
public class HcfLocaleResolver extends AcceptHeaderLocaleResolver {

    private IHcfLocaleService delegate;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        if (delegate != null) {
            return delegate.resolveLocale();
        }
        return HcfResolveLocale.resolveLocale();
        //locale from browser,default Locale.CHINA
//        return super.resolveLocale(request);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException(
                "Cannot change locale rules - use a different locale resolution strategy");
    }

}
