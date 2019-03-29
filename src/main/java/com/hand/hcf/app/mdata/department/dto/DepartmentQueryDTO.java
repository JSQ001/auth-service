package com.hand.hcf.app.mdata.department.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;


/**
 * A DTO for the Department entity.
 */
@Data
public class DepartmentQueryDTO implements Serializable {

    private UUID companyOid;
    private int status;
    private boolean statusNotEquals;
    private Long tenantId;
    private Long companyId;
    private boolean companyScope;

}
