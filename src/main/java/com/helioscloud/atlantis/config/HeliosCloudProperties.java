/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.config;

/**
 * Project Name:auth-service
 * Package Name:com.helioscloud.atlantis.config
 * Date:2018/5/12
 * Create By:zongyun.zhou@hand-china.com
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(
        prefix = "helios"//,
      //  exceptionIfInvalid = false
)
public class HeliosCloudProperties {
    private final HeliosCloudProperties.Datasource datasource = new HeliosCloudProperties.Datasource();
    private final HeliosCloudProperties.MyBatisPlus mybatisPlus = new HeliosCloudProperties.MyBatisPlus();
    private final HeliosCloudProperties.Mail mail = new HeliosCloudProperties.Mail();
    private final HeliosCloudProperties.Cache cache = new HeliosCloudProperties.Cache();

    public HeliosCloudProperties() {
    }

    public HeliosCloudProperties.Datasource getDatasource() {
        return this.datasource;
    }

    public HeliosCloudProperties.Mail getMail() {
        return this.mail;
    }

    public HeliosCloudProperties.Cache getCache() {
        return this.cache;
    }

    public HeliosCloudProperties.MyBatisPlus getMybatisPlus() {
        return this.mybatisPlus;
    }

    public static class MyBatisPlus {
        private boolean enablePerformanceInterceptor = true;
        private boolean enableSqlExplainInterceptor = false;

        public MyBatisPlus() {
        }

        public boolean isEnablePerformanceInterceptor() {
            return this.enablePerformanceInterceptor;
        }

        public void setEnablePerformanceInterceptor(boolean enablePerformanceInterceptor) {
            this.enablePerformanceInterceptor = enablePerformanceInterceptor;
        }

        public boolean isEnableSqlExplainInterceptor() {
            return this.enableSqlExplainInterceptor;
        }

        public void setEnableSqlExplainInterceptor(boolean enableSqlExplainInterceptor) {
            this.enableSqlExplainInterceptor = enableSqlExplainInterceptor;
        }
    }

    public static class Cache {
        private long expireTime = 1800L;

        public Cache() {
        }

        public long getExpireTime() {
            return this.expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }
    }

    public static class Mail {
        private String mock;

        public Mail() {
        }

        public String getMock() {
            return this.mock;
        }

        public void setMock(String mock) {
            this.mock = mock;
        }
    }

    public static class Datasource {
        private String dbType = "mysql";
        private int maxActive = 8;
        private int initialSize = 0;
        private int maxWait = -1;
        private int minIdle = 0;
        private long timeBetweenEvictionRunsMills = 60000L;

        public Datasource() {
        }

        public String getDbType() {
            return this.dbType;
        }

        public void setDbType(String dbType) {
            this.dbType = dbType;
        }

        public int getMaxActive() {
            return this.maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public int getInitialSize() {
            return this.initialSize;
        }

        public void setInitialSize(int initialSize) {
            this.initialSize = initialSize;
        }

        public int getMaxWait() {
            return this.maxWait;
        }

        public void setMaxWait(int maxWait) {
            this.maxWait = maxWait;
        }

        public int getMinIdle() {
            return this.minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public long getTimeBetweenEvictionRunsMills() {
            return this.timeBetweenEvictionRunsMills;
        }

        public void setTimeBetweenEvictionRunsMills(long timeBetweenEvictionRunsMills) {
            this.timeBetweenEvictionRunsMills = timeBetweenEvictionRunsMills;
        }
    }
}
