package com.hand.hcf.app.mdata.company.domain;

import lombok.Data;

/**
 * @author zhongyan.zhao
 */
@Data
public class CompanyImportDTO {
    private String rowNumber;
    private String setOfBooksCode;
    private String companyCode;
    private String name;
    private String companyLevelCode;
    private String legalEntityId;
    private String parentCompanyCode;
    private String startDateActive;
    private String endDateActive;
    private String companyTypeCode;
    private String address;
    private String enabled;
}
