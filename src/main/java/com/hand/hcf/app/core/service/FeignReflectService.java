package com.hand.hcf.app.core.service;

import com.hand.hcf.app.core.handler.FeignReflectHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/9/14 11:45
 * @remark
 */
@Slf4j
@Service
public class FeignReflectService {

    @Autowired(required = false)
    private FeignReflectHandler feignReflectHandler;

    /**
     * 通过feign调用请求
     * @param resultType    返回值类型
     * @param requestUrl    调用api
     * @param requestMethod 调用方法
     * @param respCode      调用失败，多语言报错代码
     * @param parameters    参数
     * @param <T>
     * @return
     */
    public <T extends Object> T doRestForParameters(Class<T> resultType, String applicationName, String requestUrl, RequestMethod requestMethod, String respCode, Object ... parameters) throws NoSuchMethodException {
        return feignReflectHandler.doRestForParameters(resultType,applicationName,requestUrl,requestMethod,respCode,parameters);
    }

    /**
     * 通过feign调用请求
     * @param resultType    返回值类型
     * @param requestUrl    调用api
     * @param requestMethod 调用方法
     * @param respCode      调用失败，多语言报错代码
     * @param parameterMap    参数名称 - 参数值
     * @param <T>
     * @return
     */
    public <T extends Object> T doRestForParameterMap(Class<T> resultType, String applicationName, String requestUrl, RequestMethod requestMethod, String respCode, Map<String,Object> parameterMap) throws NoSuchMethodException {
        return feignReflectHandler.doRestForParameterMap(resultType,applicationName,requestUrl,requestMethod,respCode,parameterMap);
    }
}
