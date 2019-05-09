package com.hand.hcf.app.ant.sso.web;

import com.alipay.fc.fcbuservice.open.SdkConfig;
import com.alipay.fc.fcbuservice.open.compoments.cert.AlipaySsoUtil;
import com.alipay.fc.fcbuservice.open.domain.LoginUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/sso")
public class LoginController {

    /**
     * 获取用户信息
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String getLoginUser(HttpServletRequest request,
                                    HttpServletResponse response) {

        //根据重定向后到统一登录中心获取登录信息
        LoginUser user = AlipaySsoUtil.getFromRequest(request, response,
                getSdkConfig());

        //返回登录用户ID给前端
        if (null != user) {
            return user.getName();
        }
        return null;
    }

    /**
     * 获取重定向地址
     */
    @RequestMapping(value = "/redirect", method = RequestMethod.POST)
    public String getRedirectURL(HttpServletRequest request,
                                 HttpServletResponse response) {

        String goUrl = "http://gfbrmp.alipay.net";
        //String goUrl = "http://IT-4CV9042LHJ.hz.ali.com:8000";

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
