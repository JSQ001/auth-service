package com.hand.hcf.app.core.web.util;

import java.util.HashMap;
import java.util.Map;

public class RequestContext {

    private static final String KEY_CLIENT_IP = RequestContext.class.getName() + ".client.ip";
    private static final String KEY_REQ_URI = RequestContext.class.getName() + ".req.uri";
    private static final String KEY_REQ_METHOD = RequestContext.class.getName() + ".req.method";
    private static final String KEY_LOG_STRING = RequestContext.class.getName() + ".log.string";
    private static final String KEY_TIME_START = RequestContext.class.getName() + ".time.start";

    protected static ThreadLocal<Map<String, Object>> requestThreadLocal = new ThreadLocal<Map<String, Object>>();

    private RequestContext() {
    }

    public static Map<String, Object> get() {
        Map<String, Object> map = requestThreadLocal.get();
        if (map == null) {
            map = new HashMap<String, Object>();
            requestThreadLocal.set(map);
        }
        return map;
    }

    public static void put(String key, Object val) {
        get().put(key, val);
    }

    public static Object get(String key) {
        return get().get(key);
    }

    public static String getAsString(String key) {
        return (String) get().get(key);
    }

    public static void putClientIp(String clientIp) {
        put(KEY_CLIENT_IP, clientIp);
    }

    public static String getClientIp() {
        return getAsString(KEY_CLIENT_IP);
    }

    public static void putReqUri(String uri) {
        put(KEY_REQ_URI, uri);
    }

    public static String getReqUri() {
        return getAsString(KEY_REQ_URI);
    }

    public static void putReqMethod(String method) {
        put(KEY_REQ_METHOD, method);
    }

    public static String getReqMethod() {
        return getAsString(KEY_REQ_METHOD);
    }

    public static void putLogString(String logString) {
        put(KEY_LOG_STRING, logString);
    }

    public static String getLogString() {
        return getAsString(KEY_LOG_STRING);
    }

    public static void putTimeStart(Long start) {
        put(KEY_TIME_START, start);
    }

    public static Long getTimeStart() {
        Object r = get(KEY_TIME_START);
        return r == null ? null : (Long) r;
    }

    public static void clear() {
        requestThreadLocal.remove();
    }

}
