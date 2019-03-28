
package com.hand.hcf.app;

import com.hand.hcf.app.auth.config.AppCenterProperties;
import com.hand.hcf.core.annotation.EnableHcfCache;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableHcfCache
@EnableConfigurationProperties({RedisProperties.class, AppCenterProperties.class, DataSourceProperties.class, MybatisProperties.class})
@ComponentScan(value = {"com.hand.hcf"})
@MapperScan("com.hand.hcf.app.**.persistence*")
public class AuthServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceApplication.class);

    public static void main(String[] args) throws UnknownHostException {git ad
        SpringApplication app = new SpringApplication(AuthServiceApplication.class);
        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        Environment env = app.run(args).getEnvironment();
        log.info("Access URLs:\n----------------------------------------------------------\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }

}

