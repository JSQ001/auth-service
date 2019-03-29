package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class DepartmentGroupDepartmentCO {
    @JsonSerialize(
            using = ToStringSerializer.class
    )
    private Long id;
    @JsonSerialize(
            using = ToStringSerializer.class
    )
    private Long departmentDetailId;
    @JsonSerialize(
            using = ToStringSerializer.class
    )
    private Long departmentId;
    private String departmentOid;
    private String departmentCode;
    private String name;
    private String path;
    private Integer status;
}
