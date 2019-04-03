package com.hand.hcf.app.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by houyin.zhang@hand-china.com on 2018/9/11.
 */
@Configuration
public class BaiDuTransferConfig {
    @Value("${baidu.transferUrl:}")
    private String transferApiHost;
    @Value("${baidu.appId:}")
    private  String appId;
    @Value("${baidu.securityKey:}")
    private String securityKey;

    public String getTransferApiHost() {
        return transferApiHost;
    }

    public void setTransferApiHost(String transferApiHost) {
        this.transferApiHost = transferApiHost;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }
}
