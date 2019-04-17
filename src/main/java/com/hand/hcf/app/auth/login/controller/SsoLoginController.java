package com.hand.hcf.app.auth.login.controller;

import com.alibaba.common.logging.spi.LoggerUtil;
import com.alipay.fc.fcbuservice.open.SdkConfig;
import com.alipay.fc.fcbuservice.open.compoments.cert.AlipaySsoUtil;
import com.alipay.fc.fcbuservice.open.domain.LoginUser;
import com.alipay.fc.fcbuservice.open.util.commons.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @date 2019/4/15 16:31
 */
@RestController
@RequestMapping("/api/sso")
public class SsoLoginController {


    /**
     * 从前端获取用户名，然后对传过来的用户名的状态进行验证
     * 若此用户处于登陆状态，则直接进入我们的报账系统主页面
     * 若此用户没有处于登陆状态，则跳转连接到一个阿里的登陆页面进行登陆
     */
    /**流程：
         拦截器从当前请求获取登录用户  首先从自己保持的登录态的方式中获取，再从AlipaySsoUtil.getFromRequest获取
         如果获取不到用户，则 AlipaySsoUtil.toSSoLoginPage 生成重定向url
         bumng登录完成后会回跳应用，然后拦截器 首先从自己保持的登录态的方式中获取，再从AlipaySsoUtil.getFromRequest获取，这个时候就能获取到
     */

    /**
     * 实现蚂蚁单点登录
     *  1.从本地获取登录信息
     *  2.从sso获取登录信息
     *  3.如果都获取不到则跳转登录
     * @param request
     * @param response
     * @throws Exception
     */
    private static Logger log = LoggerFactory.getLogger(SsoLoginController.class);
    /**
     * 获取蚂蚁默认的ID
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/login")
    public String getLoginName(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        LoginUser loginUser = AlipaySsoUtil.getFromRequest(request, response, getSdkConfig());
        if (loginUser == null) {
            log.info( "未获取到用户，需要重定向URL");
            toSSoLoginPage(request,response);
            return "";
        }
            log.info("登录ID为 :" + loginUser.getOutUsrNo());
        return loginUser.getOutUsrNo();
    }

    /**
     * 调整登录页面 --重定向
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public void toSSoLoginPage(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        String goUrl = parseGoUrl(request);
        log.info( "sso login go url page :" + goUrl);//获取到url:http://bumng.stable.alipay.net
        String sSoLoginPage = AlipaySsoUtil.toSSoLoginPage(request, response, goUrl, getSdkConfig());
        //http://bumng.stable.alipay.net/commonlogin/ssoLogin.htm?GOTO=http%3A%2F%2Fbumng.stable.alipay.net&REQ_SN=9922a094c5b4e12c43feb1bab4d66532&V=1.0.0&APP_CODE=
        log.info("sso login page :" + sSoLoginPage);
        //如果是tab页中的链接，则预先执行一段js后重定向
        sendRedirectInFrameFullUrl(request, response, sSoLoginPage);

        //bumng登录成功后，拦截后回跳到报账登录页面
        String localUrl = "http://IT-4CV9042LDV:8000";
        log.info( "报账登录页面");
        log.info( "hcf login url :" + localUrl);
        String LoginPage = AlipaySsoUtil.toSSoLoginPage(request, response, localUrl, getSdkConfig1());
        log.info( "hcf login page :" + LoginPage);
        sendRedirectInFrameFullUrl(request, response, LoginPage);

        //重新获取登录ID
        //getLoginName(request,response);
    }

       private String parseGoUrl(HttpServletRequest request) {
        //防止阿里内外任务详情页未登录情况直接跳转到系统首页
        if (StringUtil.isNotBlank(request.getParameter("redirct"))) {
            StringBuffer url = request.getRequestURL().append("?").append(request.getQueryString());
            return url.toString();
        }
        //首页
        return "http://bumng.stable.alipay.net";
    }


    /**
     * 获取sdk的配置值
     * @return
     */
    private SdkConfig getSdkConfig() {
        SdkConfig sdkConfig = new SdkConfig();
        sdkConfig.setSite("http://bumng.stable.alipay.net");
        sdkConfig.setAppCode("");
        sdkConfig.setSsoMode("REMOTE");
        sdkConfig.setSalt("");//开发环境为"" 线上请新增salt
        return sdkConfig;
    }

    private SdkConfig getSdkConfig1() {
        SdkConfig sdkConfig = new SdkConfig();
        sdkConfig.setSite("http://IT-4CV9042LDV:8000");
        sdkConfig.setAppCode("");
        sdkConfig.setSsoMode("REMOTE");
        sdkConfig.setSalt("");
        return sdkConfig;
    }

    private void sendRedirectInFrameFullUrl(HttpServletRequest request,
                                            HttpServletResponse response,
                                            String url) throws IOException {
        StringBuffer basePath = new StringBuffer(200);
        basePath.append("<script>");
        basePath.append("top.location = \"").append(url).append("\"</script>");
        log.info( "basePath :" + basePath);
        InputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new ByteArrayInputStream(basePath.toString().getBytes());
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[1024];
            int bytesRead;
            while (-1 != (bytesRead = (bis.read(buff)))) {
                bos.write(buff, 0, bytesRead);
            }
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                //bos.close();
                bos.flush();
            }
        }
    }

}
