package com.hand.hcf.app.mdata.department.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class DepartmentAssignUserDTO {
    @NotNull
    private UUID departmentOid;

    private List<UUID> userOids;

    private List<UUID> oldDepartmentList;
}
