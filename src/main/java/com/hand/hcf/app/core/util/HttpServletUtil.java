package com.hand.hcf.app.core.util;

import com.hand.hcf.app.core.web.util.HttpRequestUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/8/29 15:04
 * @remark http请求信息获取工具类
 */
public final class HttpServletUtil {

    public final static String HTTP_HEADER_USER = "HCF-User";

    public final static String HTTP_MENU_ID = "X-Menu-Id";

    public final static String HTTP_MENU_PARAM = "X-Menu-Params";

    private HttpServletUtil(){

    }

    /**
     * 获取请求头信息
     * @param key
     * @return
     */
    public static String getHeaderMessage(String key){
        try {
            HttpServletRequest request = getHttpServletRequest();
            return request.getHeader(key);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取 HttpServletRequest
     * @return
     */
    public static HttpServletRequest getHttpServletRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取请求IP
     * @return
     */
    public static String getHttpClientId(){
        return HttpRequestUtil.getRealRemoteAddr(getHttpServletRequest());
    }
}
