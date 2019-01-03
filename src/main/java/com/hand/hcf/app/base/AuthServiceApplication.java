
package com.hand.hcf.app.base;

import com.hand.hcf.app.base.config.AppCenterProperties;
import com.hand.hcf.app.base.config.Constants;
import com.hand.hcf.core.annotation.EnableHcfCache;
import com.hand.hcf.core.config.MailConfiguration;
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
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableHcfCache
@EnableConfigurationProperties({RedisProperties.class, AppCenterProperties.class,DataSourceProperties.class, MybatisProperties.class})
@EnableDiscoveryClient
@ComponentScan(value={"com.hand.hcf.core","com.hand.hcf.base","com.hand.hcf.app.base","com.hand.hcf.app.client.system"},
        excludeFilters={@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value= { MailConfiguration.class})})
@EnableFeignClients({"com.hand.hcf.app.client.system"})
@MapperScan("com.hand.hcf.app.base.persistence*")
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

