package com.hand.hcf.app.mdata.company.conver;

import com.hand.hcf.app.mdata.company.domain.CompanyGroup;
import com.hand.hcf.app.mdata.company.dto.CompanyGroupDTO;

/**
 * Created by fanfuqiang 2018/11/21
 */
public class CompanyGroupCover {

    public static CompanyGroupDTO toDTO(CompanyGroup companyGroup){
        CompanyGroupDTO dto = new CompanyGroupDTO();
        dto.setId(companyGroup.getId());
        dto.setCompanyGroupCode(companyGroup.getCompanyGroupCode());
        dto.setCompanyGroupName(companyGroup.getCompanyGroupName());
        dto.setSetOfBooksId(companyGroup.getSetOfBooksId());
        dto.setEnabled(companyGroup.getEnabled());
        dto.setVersionNumber(companyGroup.getVersionNumber());
        return dto;
    }
}
