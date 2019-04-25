package com.hand.hcf.app.mdata.company.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/26
 */
@Data
@Builder
public class CompanyQO {

    private UUID companyOid;
    private Long tenantId;
    private Long setOfBooksId;
    private Long legalEntityId;
    private String companyCode;
    private String name;
    private Long companyLevelId;
    private String companyCodeFrom;
    private String companyCodeTo;
    private Long parentCompanyId;
    private Boolean enabled;
    private String dataAuthLabel;
}
