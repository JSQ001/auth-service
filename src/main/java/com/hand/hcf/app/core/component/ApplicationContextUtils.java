package com.hand.hcf.app.core.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtils {
    private static ApplicationContextUtils instance;
    @Autowired
    ApplicationContext applicationContext;

    public ApplicationContextUtils() {
        instance = this;
    }

    public static ApplicationContext getApplicationContext() {
        return instance.applicationContext;
    }

}
