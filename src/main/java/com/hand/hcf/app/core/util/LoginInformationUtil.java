package com.hand.hcf.app.core.util;


import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.security.AuthoritiesConstants;
import com.hand.hcf.app.core.security.UserAuthentication;
import com.hand.hcf.app.core.security.domain.Authority;
import com.hand.hcf.app.core.security.domain.PrincipalLite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @description: 获取登陆信息工具类
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/9/15 10:48
 */
@Slf4j
public class LoginInformationUtil {

    static final String ROLE_TENANT = "TENANT";

    public LoginInformationUtil() {
    }

    //获取当前用户id
    public static Long getCurrentUserId() {
        return getUser().getId();
    }

    //获取用户OID
    public static UUID getCurrentUserOid() {

        return getUser().getUserOid();
    }

    //获取租户
    public static Long getCurrentTenantId() {

        return getUser().getTenantId();
    }

    //获取当前语言环境
    public static String getCurrentLanguage() {
        PrincipalLite principal = getUser();
        return principal == null ? LanguageEnum.ZH_CN.getKey() : principal.getLanguage();
    }

    public static void setAuthentication(PrincipalLite principalLite) {
        UserAuthentication userAuthentication = new UserAuthentication(null);

        userAuthentication.setDetails(principalLite);
        SecurityContextHolder.getContext().setAuthentication(userAuthentication);
    }

    /**
     * 第三方接口设置用户信息，依据请求头信息
     *
     * @return
     */
    private static PrincipalLite setClientAuth() {
        String user = HttpServletUtil.getHeaderMessage(HttpServletUtil.HTTP_HEADER_USER);
        PrincipalLite principal = new PrincipalLite();
        ;
        log.info("clientCredentials http header:" + HttpServletUtil.HTTP_HEADER_USER + ":" + user);
        if (!StringUtils.isEmpty(user)) {
            principal =readObjectFromString(user, PrincipalLite.class);
        }
        setAuthentication(principal);
        return principal;
    }


    //获取当前用户信息
    public static PrincipalLite getUser() {

        Authentication userAuth = SecurityContextHolder.getContext().getAuthentication();
        PrincipalLite principal = new PrincipalLite();
        if (userAuth instanceof UsernamePasswordAuthenticationToken) {
            return principal;
        } else if (userAuth instanceof OAuth2Authentication) {
            userAuth = ((OAuth2Authentication) userAuth).getUserAuthentication();
        }

        if (userAuth != null) {

            Object details = userAuth.getDetails();

            if (details instanceof PrincipalLite) {
                principal = (PrincipalLite) details;
            } else if (details instanceof Map) {
                Map resultMap = (Map) details;
                if (resultMap.get("id") != null) {
                    principal = mapToUser(resultMap);
                    setAuthentication(principal);
                } else {
                    Object principalLite = userAuth.getPrincipal();
                    if (principalLite instanceof PrincipalLite) {
                        principal = (PrincipalLite) userAuth.getPrincipal();
                    }
                }
            }
        }

        if (principal == null || principal.getId() == null) {
            principal = setClientAuth();
        }
        return principal;


    }

    private static String getMapValue(Map<String, Object> resultMap, String name, String defaultValue) {
        return PubItg.getValue(name, resultMap.get(name), defaultValue);
    }

    /**
     * 获取登录信息，第三方信息只包含用户ID以及语言信息
     *
     * @param resultMap
     * @return
     */
    private static PrincipalLite mapToUser(Map<String, Object> resultMap) {
        PrincipalLite principalLite = new PrincipalLite();

        principalLite.setId(Long.parseLong((String) resultMap.get("id")));
        principalLite.setLogin((String) resultMap.get("login"));
        principalLite.setUserOid(UUID.fromString((String) resultMap.get("userOid")));
        principalLite.setActivated((Boolean) resultMap.get("activated"));
        principalLite.setEmail((String) resultMap.get("email"));
        principalLite.setMobile((String) resultMap.get("mobile"));
        principalLite.setLanguage((String) resultMap.get("language"));
        principalLite.setTenantId(Long.parseLong(getMapValue(resultMap, "tenantId", "-1")));
        principalLite.setAuthorities(new HashSet<>((Collection<? extends Authority>) resultMap.get("authorities")));
        return principalLite;
    }

    public static String getSerializedUser() {
        PrincipalLite principalLite = getUser();
        return principalLite == null ? "" : writeObjectToString(principalLite);
    }

    /**
     * If the current user has a specific authority (security role).
     * <p>
     * <p>The name of this method comes from the isUserInRole() method in the Servlet API</p>
     */
    public static boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof PrincipalLite) {
                PrincipalLite principal = (PrincipalLite) authentication.getPrincipal();
                return principal.getAuthorities().contains(new Authority(authority));
            }
        }
        return false;
    }

    public static boolean hasTenantAuthority(String roleType) {
        Boolean isTenantAdmin = isCurrentUserInRole(AuthoritiesConstants.ROLE_TENANT_ADMIN);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(roleType) && ROLE_TENANT.equals(roleType) && isTenantAdmin) {
            return true;
        } else {
            return false;
        }
    }

    public static String getCurrentClientId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
            if (oAuth2Authentication != null) {
                OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
                if (oAuth2Request != null) {
                    String clientId = oAuth2Request.getClientId();
                    return clientId;
                }
            }

        }
        return "0";
    }

    /**
     * 将对象序列化为 String
     * 利用了 Base64 编码
     *
     * @param obj 任意对象
     * @param <T> 对象的类型
     * @return 序列化后的字符串
     */
    private static <T> String writeObjectToString(T obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(obj);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将 String 反序列化为原对象
     * 利用了 Base64 编码
     *
     * @param str   writeToString 方法序列化后的字符串
     * @param clazz 原对象的 Class
     * @param <T>   原对象的类型
     * @return 原对象
     */
    private static <T> T readObjectFromString(String str,Class<T> clazz) {
        byte[] bytes = Base64.getDecoder().decode(str);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(byteArrayInputStream);
            T instance = (T) inputStream.readObject();
            return instance;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
 