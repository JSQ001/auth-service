package com.hand.hcf.app.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @date 2019/4/29 14:17
 */

@Data
@Component
@ConfigurationProperties(prefix = "hcf.storage")
public class HcfOssProperties {
            private String endpoint;

            private String accessKeyId;

            private String accessKeySecret;

            private String bucketName;

            private String fileHost;
        }

