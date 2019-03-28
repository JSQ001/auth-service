package com.hand.hcf.app.base.user.dto;

import lombok.Data;

/**
 * 通过机构部门来获取员工员工工号员工姓名来获取员工信息
 *
 * @author liuchuang
 * @date 2019-02-25
 */
@Data
public class UserWorkbenchDTO {
    /**
     * 员工id
     */
    private Long id;
    /**
     * 员工代码
     */
    private String employeeCode;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 机构id
     */
    private Long companyId;
    /**
     * 机构名称
     */
    private String companyDisplay;
    /**
     * 部门id
     */
    private Long departmentId;
    /**
     * 部门名称
     */
    private String unitDisplay;
}
