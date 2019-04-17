package com.hand.hcf.app.core.redisLock;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.redisLock.annotations.LockedObject;
import com.hand.hcf.app.core.redisLock.annotations.SyncLock;
import com.hand.hcf.app.core.redisLock.enums.CredentialTypeEnum;
import com.hand.hcf.app.core.redisLock.util.OptimumReentrantRedisLock;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Aspect
@Slf4j
@Order
public class SyncLockAspect {
    private static final String DEFAULT_KEY = "DEFAULT_KEY";
    private static final String ANNOTATION = "ANNOTATION";
    private static final String OBJECT = "OBJECT";

    @Autowired
    private RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.hand.hcf.app.core.redisLock.annotations.SyncLock)")
    public void pointcutExp() {
    }

    @Around("pointcutExp()")
    public Object aroundMethod(ProceedingJoinPoint jp) throws Throwable {
        log.debug("Sync lock aop start");
        MethodSignature joinPointObject = (MethodSignature) jp.getSignature();
        Method method = joinPointObject.getMethod();
        SyncLock syncLock = method.getAnnotation(SyncLock.class);
        //没加锁注解,直接过
        if (syncLock == null) {
            return jp.proceed();
        }
        Object[] args = jp.getArgs();
        String classMethodName = method.getDeclaringClass().getSimpleName() + "_" + method.getName();
        Annotation[][] annotations = method.getParameterAnnotations();
        Map<String, Object> optAnnotationObject = getLockedObject(args, annotations);
        //如果没配置LockedObject则prefix拼接逻辑为:syncLock.prefix + className + methodName + processCredentialPrefix的结果 + LOCK
        String objectValue = "";
        //如果配置了LockedObject则走另外一套prefix拼接逻辑:syncLock.prefix + lockedFiledValue + LOCK
        if (!optAnnotationObject.isEmpty()) {
            //如果使用LockedObject,则去除key中className+methodName的拼接，采用空字符串,为了不同方法并发控制-0426
            classMethodName = StringUtils.EMPTY;
            LockedObject auditedObjAnnotation = (LockedObject) optAnnotationObject.get(ANNOTATION);
            List objList = (List)optAnnotationObject.get(OBJECT);
            for(Object obj : objList){
                if(! "".equals(objectValue)){
                    objectValue = objectValue + ";";
                }
                String objectValueTemp = "";
                Reflector reflector = new Reflector(obj.getClass());
                //LockedObject不配置属性,取obj.toString
                String[] lockKeyFields = auditedObjAnnotation.lockKeyField();
                if(lockKeyFields == null || lockKeyFields.length == 0){
                    lockKeyFields = auditedObjAnnotation.value();
                }
                if(lockKeyFields != null && lockKeyFields.length > 0){
                    for(String lockKeyField : lockKeyFields){
                        if (!StringUtils.equalsIgnoreCase("", lockKeyField)) {
                            Object getValue = null;
                            Object objTemp = obj;
                            Reflector reflectorTemp = reflector;
                            // 实现 属性.属性获取明细值
                            if(lockKeyField.indexOf(".") > 0){
                                String[] split = lockKeyField.split("\\.");
                                for(int index = 0;index < split.length; index ++){
                                    Invoker getInvoker = reflectorTemp.getGetInvoker(split[index]);
                                    getValue = getInvoker.invoke(objTemp, new Object[]{});
                                    objTemp = getValue;
                                    reflectorTemp = new Reflector(objTemp.getClass());
                                }
                            }else{
                                Invoker getInvoker = reflectorTemp.getGetInvoker(lockKeyField);
                                getValue = getInvoker.invoke(objTemp, new Object[]{});
                            }
                            if(getValue == null){
                                throw new RuntimeException("Invalid usage of LockedObject, for value of lockedField is null");
                            }
                            if(objectValueTemp.equals("")){
                                objectValueTemp = "{" + lockKeyField + "=" + getString(getValue);
                            }else{
                                objectValueTemp = objectValueTemp + "," + lockKeyField + "=" + getString(getValue);
                            }

                        }
                    }
                } else {
                    if(objectValueTemp.equals("")){
                        objectValueTemp = "{" + getString(obj);
                    }else{
                        objectValueTemp = objectValueTemp + "," + getString(obj);
                    }
                }
                objectValueTemp = objectValueTemp + "}";
                objectValue = objectValue + objectValueTemp;
            }
        }else{
            objectValue = processCredentialValue(syncLock.credential());
        }

        String lockPrefix = syncLock.lockPrefix();
        OptimumReentrantRedisLock optimumReentrantRedisLock = new OptimumReentrantRedisLock(redisTemplate, lockPrefix, classMethodName.toUpperCase(), objectValue, syncLock.timeOut(), syncLock.expireTime(),syncLock.waiting(),syncLock.intervalTime());
        boolean result = optimumReentrantRedisLock.lock();
//        if (!result) {
//            // 等待时间 (等待时间不能超过锁失效时间，防止出现锁失效但程序还在运行，这样锁的意义也就不存在了)
//            Long failureTime = System.currentTimeMillis() + Long.valueOf(Math.min(syncLock.expireTime(),syncLock.waitingTime()) - syncLock.intervalTime());
//            if(syncLock.waiting()){
//                while(true){
//                    if(System.currentTimeMillis() > failureTime){
//                        throw new BizException(RespCode.SYS_FAILED_TO_GET_RESOURCE_REQUEST_TIMEOUT);
//                    }
//                    result = optimumReentrantRedisLock.lock();
//                    if(result){
//                        break;
//                    }
//                    Thread.sleep(Long.valueOf(syncLock.intervalTime()));
//                }
//            }else {
//                throw new BizException(syncLock.errorMessage());
//            }
//        }
        if(!result){
            throw new BizException(syncLock.errorMessage());
        }
        try {
            return jp.proceed();
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                //提取更细粒度的异常信息
                InvocationTargetException invocationTargetException = (InvocationTargetException) e;
                Throwable throwable = invocationTargetException.getTargetException();
                throw throwable;
            }
            throw e;

        } finally {
            log.debug("Sync lock aop exit");
            optimumReentrantRedisLock.unlock();
        }
    }


    /**
     * 目前仅支持USER_OID或者COMPANY_OID,默认NULL返回"DEFAULT_KEY"
     *
     * @param credentialTypeEnum 凭证类型Enum
     * @return
     */
    private String processCredentialValue(CredentialTypeEnum credentialTypeEnum) {
        String objectValue;
        Object key;
        switch (credentialTypeEnum) {
            case NULL:
                objectValue = DEFAULT_KEY;
                break;
            case USER_OID:
                key =LoginInformationUtil.getCurrentUserOid();
                objectValue = key != null ? credentialTypeEnum.name() + "_" + key.toString() : DEFAULT_KEY;
                break;
            case TENANT_ID:
                key = LoginInformationUtil.getCurrentTenantId();
                objectValue = key != null ? credentialTypeEnum.name() + "_" + key.toString() : DEFAULT_KEY;
                break;
            default:
                throw new RuntimeException("Not support this type temporarily");
        }
        return objectValue;
    }

    /**
     * 获取带LockedObject注解的参数,组装成Map
     *
     * @param args        方法参数数组
     * @param annotations 注解
     * @return
     */
    private Map<String, Object> getLockedObject(Object[] args, Annotation[][] annotations) {
        Map<String, Object> result = new HashMap<>();
        if (args == null || args.length == 0) {
            return result;
        }
        if (annotations == null || annotations.length == 0) {
            return result;
        }
        // 目前不支持锁多个参数，只支持锁第一个标记为LockedObject的参数
        int index = -1;
        List parameterValues = new ArrayList();
        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
                if (annotations[i][j] instanceof LockedObject) {
                    result.put(ANNOTATION, annotations[i][j]);
//                    index = i;
                    parameterValues.add(args[i]);
                    break;
                }
            }
//            if (index != -1) {
//                break;
//            }
        }
//        if (index != -1) {
//            result.put(OBJECT, args[index]);
//        }
        result.put(OBJECT, parameterValues);
        return result;
    }

    private String getString(Object value){
        if(value == null){
            return "";
        }
        return value.toString();
    }

}
