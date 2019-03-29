package com.hand.hcf.app.mdata.company.conver;

import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.dto.CompanyGroupAssignBudgetDTO;

/**
 * Created by fanfuqiang 2018/11/21
 */
public class CompanyGroupAssignBudgetCover {

    public static CompanyGroupAssignBudgetDTO toDTO(Company company){
        CompanyGroupAssignBudgetDTO dto = new CompanyGroupAssignBudgetDTO();
        dto.setId(company.getId());
        dto.setCompanyCode(company.getCompanyCode());
        dto.setName(company.getName());
        dto.setIsEnabled(company.getEnabled());
        dto.setTenantId(company.getTenantId());
        dto.setSetOfBooksId(company.getSetOfBooksId());
        dto.setLegalEntityId(company.getLegalEntityId());
        dto.setCompanyLevelId(company.getCompanyLevelId());
        dto.setParentCompanyId(company.getParentCompanyId());
        dto.setCompanyTypeCode(company.getCompanyTypeCode());
        dto.setStartDateActive(company.getStartDateActive());
        dto.setEndDateActive(company.getEndDateActive());
        return dto;
    }
}
