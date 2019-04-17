package com.hand.hcf.app.auth.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hcf.app.auth.dto.HaimaUserDTO;
import com.hand.hcf.app.core.exception.core.UserNotActivatedException;
import com.hand.hcf.app.core.security.domain.PrincipalLite;
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
    public  String haimaUsermappingUrl;
    @Value("${haima.server.userInfo:}")
    public  String haimaUserinfoUrl;

    @Autowired
    private AuthUserService authUserService;

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

        UserDetails userDetails= authUserService.loadUserByEmail(email);

        Boolean isMapping = (Boolean) userInfo.get("isMapping");
        if(!isMapping){
            //回调海马服务做和haima的用户关系映射
            PrincipalLite u=(PrincipalLite) userDetails;
            HaimaUserDTO haimaUserDTO = new HaimaUserDTO();
            haimaUserDTO.setTenantId(u.getTenantId());
            haimaUserDTO.setUserOid(u.getUserOid().toString());
            haimaUserDTO.setUserId(first.get("userId").toString());
            if(first.containsKey("userName")){
                haimaUserDTO.setUserName(first.get("userName").toString());
            }
            if(first.containsKey("accountNumber")) {
                haimaUserDTO.setAccountNumber(first.get("accountNumber").toString());
            }
            try {
                restTemplate.postForObject(haimaUsermappingUrl,haimaUserDTO, Void.class);
            } catch (RestClientException e) {
                e.printStackTrace();
                throw new RuntimeException("user.not.bind");
            }
        }

        return userDetails;
    }

    public JSONObject authenticateHaiMa(String code, String tenantId) {
        try {

            String userUrl = haimaUserinfoUrl + "?tenantId=" + tenantId +"&code=" + code;
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
