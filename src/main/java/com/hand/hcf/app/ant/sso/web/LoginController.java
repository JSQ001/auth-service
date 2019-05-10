package com.hand.hcf.app.ant.sso.web;

import com.alipay.fc.fcbuservice.open.SdkConfig;
import com.alipay.fc.fcbuservice.open.compoments.cert.AlipaySsoUtil;
import com.alipay.fc.fcbuservice.open.domain.LoginUser;
import com.hand.hcf.app.ant.sso.dto.TokenDTO;
import com.hand.hcf.app.auth.security.BaseTokenService;
import com.hand.hcf.app.auth.service.AuthUserService;
import com.hand.hcf.app.core.security.domain.PrincipalLite;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/sso")
public class LoginController {

    @Autowired
    BaseTokenService baseTokenService;

    @Autowired
    AuthUserService userService;
    /**
     * 获取用户信息
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public TokenDTO getLoginUser(HttpServletRequest request,
                                 HttpServletResponse response) {




        //根据重定向后到统一登录中心获取登录信息
        LoginUser user = AlipaySsoUtil.getFromRequest(request, response,
                getSdkConfig());

        //返回登录用户ID给前端
        if (null != user) {
            String userName=user.getName();
            OAuth2AccessToken token= getToken(userName);
            TokenDTO tokenDTO=new TokenDTO();
            tokenDTO.setUserName(userName);
            tokenDTO.setToken(token);
            return tokenDTO;
        }
        return null;
    }

    private OAuth2AccessToken getToken(String userName)
    {
        List<GrantedAuthority> authorities= AuthorityUtils.createAuthorityList("ROLE_ADMIN");
        Map<String,String> paramters=new HashMap<>();
        paramters.put("username",userName);
        paramters.put("grant_type","password");
        paramters.put("scope","read write");
        Set<String> scope=new HashSet<>();
        scope.add("read");
        scope.add("write");
        UsernamePasswordAuthenticationToken authentication=
                new UsernamePasswordAuthenticationToken(userService.loadUserByUsername(userName),null,authorities);
        authentication.setDetails(paramters);
        OAuth2Request request =new OAuth2Request(paramters,"ArtemisWeb",
                authorities,true,scope,new HashSet<>(),null,
                new HashSet<>(),new HashMap<>());
        OAuth2Authentication auth2Authentication=new OAuth2Authentication(request,authentication);
        OAuth2AccessToken oAuth2AccessToken = baseTokenService.createAccessToken(auth2Authentication);
//        try {
//            oAuth2AccessToken= tokenEndpoint.postAccessToken(authenticationToken,paramters).getBody();
//        } catch (HttpRequestMethodNotSupportedException e) {
//            e.printStackTrace();
//        }
        return oAuth2AccessToken;
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
