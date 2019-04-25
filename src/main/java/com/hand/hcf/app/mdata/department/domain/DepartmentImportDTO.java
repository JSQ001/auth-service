package com.hand.hcf.app.mdata.department.domain;

import lombok.Data;

/**
 * @author zhongyan.zhao
 */
@Data
public class DepartmentImportDTO {
    private String rowNumber;
    private String departmentCode;
    private String name;
    private String parentCode;
    private String companyCode;
    private String status;
}
