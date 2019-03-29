package com.hand.hcf.app.mdata.company.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by silence on 2017/10/19.
 */
@Data
public class CompanyGroupAndCompanyDTO {
    private Long companyGroupId;
    private List<CompanyGroupAssignDTO> companies;
}
