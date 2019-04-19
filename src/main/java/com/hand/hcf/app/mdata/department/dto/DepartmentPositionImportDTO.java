package com.hand.hcf.app.mdata.department.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuchuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentPositionImportDTO {

    private String tenantId;

    private String positionCode;

    private String positionName;

    private String enabled;

    private String deleted;
}
