

package com.hand.hcf.app.auth.service;

import com.alibaba.fastjson.JSONObject;
import com.hand.hcf.core.exception.core.UserNotActivatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * 钉钉业务逻辑实现类
 * Created by Strive on 17/8/31.
 */
@Service
public class DingTalkServiceImpl {

    private static Logger log = LoggerFactory.getLogger(DingTalkServiceImpl.class);

    @Value("${dingtalk.server.getDingTalkUserInfoURL:}")
    private String getDingTalkUserInfoURL;  // 获取钉钉用户信息URL

    private RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private AuthUserService authUserService;


    public JSONObject authenticate(String code, String corpId) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity httpEntity = new HttpEntity(httpHeaders);
            String url = getDingTalkUserInfoURL + "?code=" + code + "&corpId=" + corpId;
            log.info("调用钉钉服务根据code和企业id获取用户信息,URL:{},参数：code：{},corpId：{}", url, code, corpId);
            ResponseEntity<JSONObject> jsonObject = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class);
            log.info("调用钉钉服务查询用户响应结果：{}", jsonObject.getBody());
            return jsonObject.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserDetails loadDingTalkUserByCodeAndCorpId(String code, String corpId)  {
        log.info("Authenticating code：{},corpId：{} dingtalk start ", code, corpId);

        /*code是钉钉的免登授权码,方案:
        1、根据code和corpId调用钉钉免登接口、保证钉钉号码/邮箱的信息必须存在，可通过获取帐号调钉钉接口获取号码;
        2、数据库表中建立帐号与用户对应关系，由帐号确认用户身份*/
        JSONObject userInfo = this.authenticate(code, corpId);//员工在钉钉中的帐号

        if (null == userInfo) {
            throw new UserNotActivatedException("user.not.bind");
        }
        String email = userInfo.getString("email");     // 邮箱

        // 判断邮箱是否为空
        if (StringUtils.isEmpty(email)) {
            throw new UserNotActivatedException("email.is.empty");
        }
        return authUserService.loadUserByEmail(email);
    }
}
