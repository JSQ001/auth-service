package com.hand.hcf.app.mdata.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/4/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyLovQueryParams {
    private Long id;
    private String companyCode;
    private String companyCodeFrom;
    private String companyCodeTo;
    private String companyName;
    private Long departmentId;
    private String codeName;
    private Long tenantId;
    private Long setOfBooksId;
    private Boolean enabled;
    private Boolean associateDepartmentFlag;
}
