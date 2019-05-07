package com.hand.hcf.app.ant.sso.web;


import com.alipay.fc.fcbuservice.open.SdkConfig;
import com.alipay.fc.fcbuservice.open.compoments.cert.AlipaySsoUtil;
import com.alipay.fc.fcbuservice.open.domain.LoginUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/sso")
public class LoginController {

    /**
    获取用户信息
    */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public LoginUser getLoginUser(HttpServletRequest request,
                                  HttpServletResponse response) {

        return AlipaySsoUtil.getFromRequest(request, response,
                getSdkConfig());
    }

    /**
     获取重定向地址
     */
    @RequestMapping(value = "/redirect", method = RequestMethod.POST)
    public String getRedirectURL(HttpServletRequest request,
                                 HttpServletResponse response) {

        String goUrl = "http://gfbrmp.alipay.net";

        return AlipaySsoUtil.toSSoLoginPage(request, response, goUrl,
                getSdkConfig());

    }

    /**
     * 获取sdk的配置值
     *
     * @return
     */
    private SdkConfig getSdkConfig() {
        SdkConfig sdkConfig = new SdkConfig();
        sdkConfig.setSite("http://bumng.stable.alipay.net");
        sdkConfig.setAppCode("gfbrmp");
        sdkConfig.setSsoMode("REMOTE");
        sdkConfig.setSalt("");//开发环境为"" 线上请新增salt
        return sdkConfig;
    }
}
