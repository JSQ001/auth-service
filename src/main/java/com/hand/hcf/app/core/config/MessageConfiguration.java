package com.hand.hcf.app.core.config;


import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * @description:
 * @version: 1.0
 * @author: zhiyu.liu@hand-china.com
 * @date: 2017/7/21 10:59
 */
@Configuration
public class MessageConfiguration implements WebMvcConfigurer, EnvironmentAware {

    private Integer cacheSeconds;
    @Override
    public void setEnvironment(Environment environment) {
       this.cacheSeconds= Binder.get(environment).bind("spring.messages.cache-duration", Integer.class).orElse(1000);

    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(cacheSeconds);
        return messageSource;
    }
    @Bean
    public MessageSource exceptionSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/i18n/exceptions");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(cacheSeconds);
        return messageSource;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        registry.addInterceptor(localeChangeInterceptor);
    }


    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        return messageSourceAccessor;
    }

    @Bean
    public MessageSource moduleMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/i18n/moduleMessages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(cacheSeconds);
        return messageSource;
    }

}
