package com.hand.hcf.app.payment.web.adapter;

import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.payment.domain.CompanyBank;
import com.hand.hcf.app.payment.domain.CompanyBankAuth;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.service.CompanyBankService;
import com.hand.hcf.app.payment.web.dto.CompanyBankAuthDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by 刘亮 on 2017/9/29.
 */
@Component
public class CompanyBankAuthAdapter {

    @Autowired
    private CompanyBankService companyBankService;

    @Autowired
    private PaymentOrganizationService organizationService;

    public CompanyBankAuthDTO toDTO(CompanyBankAuth companyBankAuth) {
        CompanyBankAuthDTO dto = new CompanyBankAuthDTO();
        CompanyCO companyDTO = organizationService.getById(companyBankAuth.getAuthorizeCompanyId());
        dto.setCompanyId(companyDTO.getId());
        dto.setCompanyCode(companyDTO.getCompanyCode());
        dto.setCompanyName(companyDTO.getName());
        if (companyBankAuth.getAuthorizeDepartmentId() != null) {
            DepartmentCO department = organizationService.getDepartmentById(companyBankAuth.getAuthorizeDepartmentId());
            dto.setDepartmentId(department.getId());
            dto.setDepartmentCode(department.getDepartmentCode());
            dto.setDepartmentName(department.getName());
        }
        if (companyBankAuth.getAuthorizeEmployeeId() != null) {
            ContactCO user = organizationService.getByUserCode(companyBankAuth.getEmployeeCode());
            dto.setAuthorizeEmployeeId(UUID.fromString(user.getUserOid()));
            dto.setEmployee(user.getLogin());
            dto.setEmployeeCode(user.getEmployeeCode());
            dto.setEmployeeJob(user.getTitle());
            dto.setEmployeeName(user.getFullName());
        }


        dto.setAuthorizeDateFrom(companyBankAuth.getAuthorizeDateFrom());
        dto.setAuthorizeDateTo(companyBankAuth.getAuthorizeDateTo());
        dto.setEnabled(companyBankAuth.getEnabled());
        dto.setId(companyBankAuth.getId());
        dto.setCreatedBy(companyBankAuth.getCreatedBy());
        dto.setCreatedDate(companyBankAuth.getCreatedDate());
        if (companyBankAuth.getBankAccountId() != null) {
            dto.setBankAccountId(companyBankAuth.getBankAccountId());
            CompanyBank companyBank = new CompanyBank();
            companyBank = companyBankService.selectById(companyBankAuth.getBankAccountId());
            CompanyCO one = organizationService.getById(companyBank.getCompanyId());
            dto.setBankAccountCompanyId(one.getId());
            dto.setBankAccountCompanyCode(one.getCompanyCode());
            dto.setBankAccountCompanyName(one.getName());
            dto.setCompanyBank(companyBank);
        }
        return dto;
    }

}
