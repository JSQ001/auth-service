package com.hand.hcf.app.auth.service;

import com.hand.hcf.app.auth.dto.TrialUserDTO;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 * @Author: 魏胜
 * @Description:
 * @Date: 2018/2/28 10:55
 */
@Service
public class UELoginUserDetailService {

    private final Logger log = LoggerFactory.getLogger(UELoginUserDetailService.class);

    private TrialServiceCilent trialServiceCilent;

    @Autowired
    private UserService userService;

    public UELoginUserDetailService(TrialServiceCilent trialServiceCilent) {
        this.trialServiceCilent = trialServiceCilent;
    }

    public UserDetails loadUserByUsername(String code) {
        TrialUserDTO trialUserDTO = trialServiceCilent.getTrialUser(code);
        if (trialUserDTO == null) {
            log.error("Trial user not found, code : {}", code);
            throw new UsernameNotFoundException("user.not.found");
        } else {
            if("expired".equals(trialUserDTO.getState())) {
                throw new AccountExpiredException("wechat.code.expired");
            }
        }

        return userService.loadUserByUserOid(UUID.fromString(trialUserDTO.getUserOid()));
    }

    @Component
    public static class TrialServiceCilent {

        @Value("${trialService.api.get_trialUser:}")
        private String getTrialUserURL;

        @Value("${trialService.api.bind_wechat_user:}")
        private String bindWechatUserURL;

        private RestTemplate restTemplate = new RestTemplate();

        public TrialUserDTO getTrialUser(String code) {
            String url = getTrialUserURL + "?code={code}";
            Map<String, Object> requestParams = new HashedMap();
            requestParams.put("code", code);
            TrialUserDTO trialUserDTO;
            try {
                trialUserDTO = restTemplate.getForEntity(url, TrialUserDTO.class, requestParams).getBody();
            } catch (RestClientException e) {
                throw new RuntimeException("TrialService getTrialUser api exception : " + e.getMessage());
            }
            return trialUserDTO;

        }

        public TrialUserDTO bindWechatUser(String code, String mobile, String name, String enterpriseName){
            String url = bindWechatUserURL+"?code={code}&mobile={mobile}&name={name}&enterpriseName={enterpriseName}";
            Map<String, Object> requestParams = new HashedMap();
            requestParams.put("code", code);
            requestParams.put("mobile", mobile);
            requestParams.put("name", name);
            requestParams.put("enterpriseName", enterpriseName);
            TrialUserDTO trialUserDTO;
            try {
                trialUserDTO = restTemplate.getForEntity(url, TrialUserDTO.class, requestParams).getBody();
            } catch (Exception e) {
                throw new RuntimeException("TrialService bindWechatUser api exception : " + e.getMessage());
            }
            return trialUserDTO;
        }
    }

}
