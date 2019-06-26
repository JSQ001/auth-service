package com.hand.hcf.app.common.co;

import lombok.Data;

import java.util.List;

@Data
public class DepartmentGroupCO {

    private Long id;
    private String deptGroupCode;
    private String description;
    private List<Long> departmentIdList;

}
