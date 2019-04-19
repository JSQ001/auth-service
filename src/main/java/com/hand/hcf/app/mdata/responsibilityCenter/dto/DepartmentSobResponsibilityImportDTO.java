package com.hand.hcf.app.mdata.responsibilityCenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuchuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentSobResponsibilityImportDTO {
    private String tenantId;
    private String setOfBooksCode;
    private String companyCode;
    private String departmentCode;
    private String responsibilityCenterCode;
    private String defaultResponsibilityCenter;
    private String allResponsibilityCenter;
}
