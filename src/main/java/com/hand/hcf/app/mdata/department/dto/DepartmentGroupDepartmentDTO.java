package com.hand.hcf.app.mdata.department.dto;

import lombok.Data;

/**
 * Created by 刘亮 on 2017/9/18.
 */
@Data
public class DepartmentGroupDepartmentDTO {
    private Long departmentDetailId;
    private Long departmentId;
    private String departmentOid;
    private String name;
    private String path;
    private Integer status;
    private String departmentCode;
}
