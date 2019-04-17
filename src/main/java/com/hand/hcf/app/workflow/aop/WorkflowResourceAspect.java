

package com.hand.hcf.app.workflow.aop;


import com.hand.hcf.app.core.web.util.HttpRequestUtil;
import com.hand.hcf.app.core.web.util.RequestContext;
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

@Component
@Aspect
@Order(1)
public class WorkflowResourceAspect {
    private static Logger log = LoggerFactory.getLogger(WorkflowResourceAspect.class);

    @Around("within(com.hand.hcf.app.workflow..*.web..*)")
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
