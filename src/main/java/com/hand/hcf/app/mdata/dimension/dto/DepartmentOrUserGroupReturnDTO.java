package com.hand.hcf.app.mdata.dimension.dto;

import lombok.Data;

@Data
public class DepartmentOrUserGroupReturnDTO {
    //部门或人员组id
    private Long id;

    //部门path 或 人员组name
    private String pathOrName;
}
