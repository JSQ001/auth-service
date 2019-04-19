package com.hand.hcf.app.mdata.responsibilityCenter.dto;

import lombok.Data;

/**
 * <p>
 *  公司部门默认责任中心dto
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/16
 */
@Data
public class ResponsibilityDefaultDTO {
    private Long id;
    private String responsibilityCenterName;
    private String responsibilityCenterCode;
    private String codeName;
    private Long companyId;
    private Long departmentId;
}
