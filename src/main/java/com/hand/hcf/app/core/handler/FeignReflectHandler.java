package com.hand.hcf.app.core.handler;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/9/14 11:33
 * @remark feign 逆向调用处理器
 */
public interface FeignReflectHandler {

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
    <T extends Object> T doRestForParameters(Class<T> resultType, String applicationName, String requestUrl, RequestMethod requestMethod, String respCode, Object... parameters) throws NoSuchMethodException;

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
    <T extends Object> T doRestForParameterMap(Class<T> resultType, String applicationName, String requestUrl, RequestMethod requestMethod, String respCode, Map<String, Object> parameterMap) throws NoSuchMethodException;
}
