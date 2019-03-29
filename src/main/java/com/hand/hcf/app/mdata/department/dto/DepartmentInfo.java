package com.hand.hcf.app.mdata.department.dto;

import lombok.Data;

import javax.persistence.Id;

/**
 * A Company.
 */
@Data
public class DepartmentInfo {

    @Id

    private Long id;  //部门id
    private String departmentOid;//部门oid

    private Long parentId;//父部门id
    private String name;  //公司名称
    private String path;  //部门路径
    private Integer status;//部门状态

    private Long tenantId;
    private String dataSource;
    private String departmentCode;// 部门编码


}
