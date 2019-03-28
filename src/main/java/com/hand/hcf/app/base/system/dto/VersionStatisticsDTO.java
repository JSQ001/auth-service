package com.hand.hcf.app.base.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @Author: 魏胜
 * @Description: 版本统计
 * @Date: 2018/5/16 17:06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionStatisticsDTO {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 操作平台
     */
    private String platform;

    /**
     * 大本版
     */
    private String appVersion;

    /**
     * 小版本
     */
    private String subAppVersion;

    /**
     * 更新人数
     */
    private Integer counts;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 租户名称
     */
    private String tenantName;
}
