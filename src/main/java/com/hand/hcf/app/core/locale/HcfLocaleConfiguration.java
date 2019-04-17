package com.hand.hcf.app.core.locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

@Configuration
public class HcfLocaleConfiguration {

    @Autowired(required = false)
    private IHcfLocaleService localeService;

    @Bean
    public LocaleResolver localeResolver() {
       HcfLocaleResolver hcfLocaleResolver = new HcfLocaleResolver();
        if (localeService != null) {
            hcfLocaleResolver.setDelegate(localeService);
        }
        return hcfLocaleResolver;
    }
}
