package com.hand.hcf.app.mdata.department.dto;

import lombok.Data;

/**
 * @author liuchuang
 */
@Data
public class DepartmentPositionUserBeginningImportDTO {
    private String tenantId;

    private String positionCode;

    private String departmentCode;

    private String userCode;

    private String enabled;

    private String deleted;
}
