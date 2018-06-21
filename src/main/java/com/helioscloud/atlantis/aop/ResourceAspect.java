/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.aop;


import com.helioscloud.atlantis.util.HttpRequestUtil;
import com.helioscloud.atlantis.util.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author elvis.xu
 * @since 2016-06-21 10:45
 */
@Component
@Aspect
@Order(1)
public class ResourceAspect {
    private static Logger log = LoggerFactory.getLogger(ResourceAspect.class);

    @Around("within(com.helioscloud.atlantis.web..*)")
    protected Object aroudAdivce(ProceedingJoinPoint jp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        RequestContext.clear();
        HttpRequestUtil.resolveContext(request);

        String argsString = "";
        if (log.isInfoEnabled() || log.isDebugEnabled()) {
            if (Arrays.asList(jp.getArgs()).stream().filter(arg -> arg instanceof File || arg instanceof InputStream || arg instanceof InputStreamSource || arg instanceof byte[]).count() == 0) {
                argsString = Arrays.toString(jp.getArgs());
            }
        }

        log.debug("REQUEST BEGIN.......: {} , args={} ", RequestContext.getLogString(), argsString);

        Object rt = null;

        // 版本检测等公共行为

        rt = jp.proceed();

        long cost = System.currentTimeMillis() - RequestContext.getTimeStart();
        if (log.isInfoEnabled()) {
            String resStatus = "200 OK";
            if (rt instanceof ResponseEntity) {
                ResponseEntity resEnt = (ResponseEntity) rt;
                resStatus = resEnt.getStatusCode().value() + " " + resEnt.getStatusCode().getReasonPhrase();
            }


            log.info("REQUEST END  [cost={}] : {} , args={} , res={}", StringUtils.leftPad(cost + "", 5), RequestContext.getLogString(), argsString, resStatus);
        }
        RequestContext.clear();
        return rt;
    }
}
