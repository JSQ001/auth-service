/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

/**
 * Project Name:artemis
 * Package Name:com.handchina.yunmart.artemis.config
 * Date:2018/3/29
 * Create By:zongyun.zhou@hand-china.com
 */
@Configuration
public class ElasticsearchConfig {
    @Value("${elasticsearch.host:}")
    private String elasticHost;
    @Value("${elasticsearch.tcp-port:}")
    private String elasticTcpPort;
    @Value("${elasticsearch.cluster-name:}")
    private String clusterName;
    @Value("${elasticsearch.user:}")
    private String elasticUserName;
    @Value("${elasticsearch.password:}")
    private String password;
    @Value("${elasticsearch.enable:}")
    private boolean enable;

    @Bean
    public TransportClient transportClient() {
        if (enable) {
            try {
                Settings settings = Settings.builder()
                    .put("cluster.name", clusterName)
                    .put("xpack.security.user", elasticUserName.concat(":").concat(password))
                    .put("client.transport.sniff", false).build();
                return new PreBuiltXPackTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticHost), Integer.parseInt(elasticTcpPort)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
