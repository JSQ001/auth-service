package com.helioscloud.atlantis.dto;

import lombok.Data;

/**
 * @Author: 魏胜
 * @Description:
 * @Date: 2018/2/28 11:20
 */
@Data
public class TrialUserDTO {

    private String userOid;

    private String openId;

    private String mobile;

    private Boolean finishFlag;

    private String testMobile;

    private String testEmployeeID;

    private String testEmail;

    private String state;
}
