package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

/**
 * 用户简易信息
 *
 * @author liuchuang
 */
@Data
public class UserSimpleInfoDTO {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 员工代码
     */
    private String employeeCode;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 机构id
     */
    private Long companyId;
    /**
     * 机构代码
     */
    private String companyCode;
    /**
     * 机构名称
     */
    private String companyName;
    /**
     * 部门id
     */
    private Long departmentId;
    /**
     * 部门代码
     */
    private String departmentCode;
    /**
     * 部门名称
     */
    private String departmentName;
}
