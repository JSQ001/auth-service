package com.hand.hcf.app.core.async;

import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class HcfAsyncUncaughtExceptionHandler extends SimpleAsyncUncaughtExceptionHandler {


    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        super.handleUncaughtException(ex, method, params);
    }

}
