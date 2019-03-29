package com.hand.hcf.app.mdata.company.conver;

import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.dto.CompanyDTO;
import com.hand.hcf.app.mdata.company.dto.CompanyInfo;
import com.hand.hcf.app.mdata.company.dto.CompanySobDTO;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * comment
 * Created by fanfuqiang 2018/11/20
 */
public class CompanyCover {

    @Autowired
    private MapperFacade mapper;

    public static CompanyDTO companyToCompanyDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        BeanUtils.copyProperties(company, dto);
        return dto;
    }

    public static Company companyDTOToCompany(CompanyDTO dto) {
        Company company = new Company();
        BeanUtils.copyProperties(dto, company);
        return company;
    }

    public CompanySobDTO companyToCompanySobDTO(Company company) {
        if (company == null) {
            return null;
        }
        CompanySobDTO companyDTO = new CompanySobDTO();
        mapper.map(company, companyDTO);
        return companyDTO;
    }

    public static CompanySobDTO companyToCompanyDTOV2(Company company) {
        if (company == null) {
            return null;
        }
        CompanySobDTO companyDTOV2 = new CompanySobDTO();
        BeanUtils.copyProperties(company, companyDTOV2);
        return companyDTOV2;
    }

    public static CompanyInfo companyDTOtoCompanyInfo(CompanyDTO companyDTO) {
        CompanyInfo companyInfo = new CompanyInfo();
        companyInfo.setId(companyDTO.getId());
        companyInfo.setCompanyCode(companyDTO.getCompanyCode());
        companyInfo.setName(companyDTO.getName());
        companyInfo.setTenantId(companyDTO.getTenantId());
        companyInfo.setEnabled(companyDTO.getEnabled());
        companyInfo.setCompanyOid(companyDTO.getCompanyOid().toString());
        companyInfo.setCompanyLevelId(companyDTO.getCompanyLevelId());
        companyInfo.setCompanyTypeCode(companyDTO.getCompanyTypeCode());
        companyInfo.setCompanyTypeName(companyDTO.getCompanyTypeName());
        companyInfo.setDoneRegisterLead(companyDTO.getDoneRegisterLead());
        companyInfo.setTaxId(companyDTO.getTaxId());
        companyInfo.setSetOfBooksId(companyDTO.getSetOfBooksId());
        companyInfo.setLegalEntityId(companyDTO.getLegalEntityId());
        return companyInfo;
    }
}
