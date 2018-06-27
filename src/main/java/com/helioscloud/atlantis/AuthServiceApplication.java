/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis;

/**
 * Project Name:auth-service
 * Package Name:com.helioscloud.atlantis
 * Date:2018/5/12
 * Create By:zongyun.zhou@hand-china.com
 */

import com.cloudhelios.atlantis.annotation.EnableHeliosBasedConfiguration;
import com.helioscloud.atlantis.config.Constants;
import com.helioscloud.atlantis.config.HeliosCloudProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
//@EnableMyBatis
//@EnableHeliosBasedConfiguration
@EnableConfigurationProperties({RedisProperties.class, HeliosCloudProperties.class,DataSourceProperties.class, MybatisProperties.class})
@EnableDiscoveryClient
@MapperScan("com.helioscloud.atlantis.persistence*")
public class AuthServiceApplication {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceApplication.class);
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(AuthServiceApplication.class);
        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        addDefaultProfile(app, source);
        Environment env = app.run(args).getEnvironment();
        log.info("Access URLs:\n----------------------------------------------------------\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }
    /**
     * If no profile has been configured, set by default the "dev" profile.
     */
    private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("spring.profiles.active") &&
                !System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {

            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        }
    }
}

