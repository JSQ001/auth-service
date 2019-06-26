

package com.hand.hcf.app.payment.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
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
}
