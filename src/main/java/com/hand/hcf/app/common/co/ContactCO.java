package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  员工对外co
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/20
 */
@Data
public class ContactCO implements Serializable {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户oid
     */
    private String userOid;
    /**
     * 登陆手机号
     */

    private String login;
    /**
     * 用户名
     */
    private String fullName;

    private String title;

    /**
     * 员工工号
     */
    private String employeeCode;

    private Long companyId;

    private Long tenantId;

    private String gender;

    private String phoneNumber;

    private String email;

    private String status;

    private String dutyCode;

    private String rankCode;
}
