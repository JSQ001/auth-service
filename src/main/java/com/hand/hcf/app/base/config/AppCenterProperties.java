package com.hand.hcf.app.base.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author qingsheng.chen
 * @date 2017/12/21 16:54
 * @description AppCenter配置
 */
@Configuration
@ConfigurationProperties(prefix = "app-center")
public class AppCenterProperties {
    private Authentication authentication;

    public Authentication getAuthentication() {
        return authentication;
    }

    public AppCenterProperties setAuthentication(Authentication authentication) {
        this.authentication = authentication;
        return this;
    }

    /**
     * 扫码登录授权配置
     */
    public static class Authentication {
        /**
         * 过期时间，单位 s
         */
        private int expireSecond = 300;
        /**
         * 应用下载链接
         */
        private String downloadUrl;
        /**
         * PC端 clientId
         */
        private String clientId;
        /**
         * 查询间隔，单位 ms
         */
        private int interval = 100;
        /**
         * 查询持续时间，单位 ms
         */
        private int duration = 8000;

        public int getExpireSecond() {
            return expireSecond;
        }

        public Authentication setExpireSecond(int expireSecond) {
            this.expireSecond = expireSecond;
            return this;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public Authentication setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }

        public String getClientId() {
            return clientId;
        }

        public Authentication setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public int getInterval() {
            return interval;
        }

        public Authentication setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        public int getDuration() {
            return duration;
        }

        public Authentication setDuration(int duration) {
            this.duration = duration;
            return this;
        }
    }

}
