package com.hand.hcf.app.mdata.contact.dto;

import lombok.Data;

import java.util.List;

/**
 * @author liuchuang
 */
@Data
public class UserSimpleInfoNotExcludeIdsDTO {
    /**
     * 排除的用户id
     */
    private List<Long> ids;

    /**
     * 员工代码
     */
    private String employeeCode;

    /**
     * 员工名称
     */
    private String name;

    /**
     * 机构id
     */
    private Long companyId;

    /**
     * 部门id
     */
    private Long unitId;

}
