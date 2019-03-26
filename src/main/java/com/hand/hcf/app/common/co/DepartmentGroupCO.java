package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentGroupCO {

    private Long id;
    private String deptGroupCode;
    private String description;
    private List<Long> departmentIdList;

}
