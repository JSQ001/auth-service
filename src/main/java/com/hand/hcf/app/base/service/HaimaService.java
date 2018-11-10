package com.hand.hcf.app.base.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hcf.core.security.domain.PrincipalLite;
import com.hand.hcf.app.base.dto.UserDTO;
import com.hand.hcf.app.base.exception.UserNotActivatedException;
import com.hand.hcf.app.base.util.PrincipalBuilder;
import com.hand.hcf.app.base.dto.HaimaUserDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ray Ma on 2018/1/2.
 */
@Service
public class HaimaService {

    private static final Logger log = LoggerFactory.getLogger(HaimaService.class);

    @Value("${haima.server.userMapping:}")
    public  String HAIMA_USERMAPPING_URL;
    @Value("${haima.server.userInfo:}")
    public  String HAIMA_USERINFO_URL;

    @Autowired
    private UserService userService;

    private RestTemplate restTemplate = new RestTemplate();

    public UserDetails loadHaimaUserByClientIdAndCode(String code, String tenantId) {
        log.info("Authenticating code:{},tenantId:{} haima start ", code, tenantId);
        //员工在海马里的信息
        JSONObject userInfo = this.authenticateHaiMa(code, tenantId);
        if (userInfo == null) {
            throw new UserNotActivatedException("user.not.bind");
        }
        if(!(Boolean) userInfo.get("success")){
            throw new RuntimeException("code.is.invalid");
        }
        List rows = (List) userInfo.get("rows");
        HashMap first = (HashMap) rows.get(0);
        String email = (String) first.get("email");
        // 判断邮箱是否为空
        if (StringUtils.isEmpty(email)) {
            throw new UserNotActivatedException("email.is.empty");
        }
        UserDTO u = userService.findOneByContactEmail(email);
        if (u == null) {
            //匹配保存失败，发邮件,根据公司OID获取公司管理员邮箱，发邮件。
//            dingTalkLoginServiceImpl.sendMatchErrorEmail(,mobile,email,dingtalkUserId,name);
            throw new UserNotActivatedException("user.not.found");
        }

        //公共检查2.用户离职 3，用户锁定 4.密码过期
        userService.loginCommonCheck(u);

        Boolean isMapping = (Boolean) userInfo.get("isMapping");
        if(!isMapping){
            //回调海马服务做artemis和haima的用户关系映射
            HaimaUserDTO haimaUserDTO = new HaimaUserDTO();
            haimaUserDTO.setTenantId(u.getTenantId());
            haimaUserDTO.setUserOid(u.getUserOID().toString());
            haimaUserDTO.setUserId(first.get("userId").toString());
            if(first.containsKey("userName")){
                haimaUserDTO.setUserName(first.get("userName").toString());
            }
            if(first.containsKey("accountNumber")) {
                haimaUserDTO.setAccountNumber(first.get("accountNumber").toString());
            }
            try {
                restTemplate.postForObject(HAIMA_USERMAPPING_URL,haimaUserDTO, Void.class);
            } catch (RestClientException e) {
                e.printStackTrace();
                throw new RuntimeException("user.not.bind");
            }
        }

        return PrincipalBuilder.builder(u);
    }

    public JSONObject authenticateHaiMa(String code, String tenantId) {
        try {
//            String tokenUrl = HAIMA_SUB_ACCESSTOKEN + "?clientId=" + clientId + "&clientSecret=" + clientSecret + "&grantType=client_credentials";
//            log.debug("海马子应用端获取token with url:{}, clientId:{},clientSecret:{}",tokenUrl, clientId, clientSecret);
//            String access_token = restTemplate.getForObject(tokenUrl,String.class, new HashMap<>());

            String userUrl = HAIMA_USERINFO_URL + "?tenantId=" + tenantId +"&code=" + code;
            log.info("调用海马服务根据code和clientId获取用户信息,URL:{},参数:code:{},clientId:{}", userUrl, code,tenantId );
            JSONObject body = restTemplate.getForObject(userUrl, JSONObject.class, new HashMap<>());
            log.info("海马服务查询用户返回值:{}", body);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
