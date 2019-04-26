

package com.hand.hcf.app.payment.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.hand.hcf.app.payment.job")
public class XxlJobConfig {
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    @Value("${xxl.job.admin.addresses:}")
    private String addresses;

    @Value("${xxl.executor.appname:}")
    private String appname;

    @Value("${xxl.executor.ip:}")
    private String ip;

    @Value("${xxl.executor.port:22}")
    private int port;

    @Value("${xxl.executor.logpath:}")
    private String logpath;

    @Value("${xxl.accessToken:}")
    private String accessToken;

    @Value("${xxl.job.enabled:false}")
    private Boolean enabled;

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobExecutor xxlJobExecutor() {
        if (enabled) {
            logger.info(">>>>>>>>>>> xxl-job config init.");
            XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
            xxlJobExecutor.setIp(ip);
            xxlJobExecutor.setPort(port);
            xxlJobExecutor.setAppName(appname);
            xxlJobExecutor.setAdminAddresses(addresses);
            xxlJobExecutor.setLogPath(logpath);
            xxlJobExecutor.setAccessToken(accessToken);
            return xxlJobExecutor;
        }else{
            return null;
        }
    }
}
