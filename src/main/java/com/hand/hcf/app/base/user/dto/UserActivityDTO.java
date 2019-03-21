
package com.hand.hcf.app.base.user.dto;

import lombok.Data;

@Data
public class UserActivityDTO {
    private String client;
    private String clientVersion;
    private String appVersion;
    private String username;//中文名
    private int userid;
    private String tenantName;//租户
    private String loginDate;//登录日期
    private String loginDateTime;//登录时间
    private int loginYear;//年
    private int loginMonth;//月
    private int loginWeek;//第{N}周
    private String id;//userid+loginDate
}
