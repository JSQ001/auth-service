package com.hand.hcf.app.mdata.contact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hcf.app.mdata.contact.utils.UserInfoEncryptUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by yangqi on 2017/1/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class ContactBankAccountImportDTO {

    private UUID userOid;

//    private String companyName;
    private String employeeId;      //工号

    private String bankAccountNo;   //银行账号

    private String bankAccountName;     //开户名

    private String branchName;      //开户支行名称

    private String bankName;        //银行名称

    private String bankCode;        //银联号

    private String accountLocation;     //开户地

    private String errorDetail;     //错误描述

    private Integer rowNum;
    private Long tenantId;

    public ContactBankAccountImportDTO(String employeeId, String bankAccountNo, String bankAccountName, String branchName, String bankName, String bankCode, String accountLocation) {
        this.employeeId = employeeId;
        this.bankAccountNo = bankAccountNo;
        this.bankAccountName = bankAccountName;
        this.branchName = branchName;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.accountLocation = accountLocation;
    }

    public static ContactBankAccountDTO contactBankAccountImportDTOToDTO(ContactBankAccountImportDTO contactBankAccountImportDTO) {
        ContactBankAccountDTO contactBankAccountDTO = new ContactBankAccountDTO();
        contactBankAccountDTO.setUserOid(contactBankAccountImportDTO.getUserOid());
        contactBankAccountDTO.setBankAccountNo(UserInfoEncryptUtil.encrypt(contactBankAccountImportDTO.getBankAccountNo()));
        contactBankAccountDTO.setBankAccountName(contactBankAccountImportDTO.getBankAccountName());
        contactBankAccountDTO.setBranchName(contactBankAccountImportDTO.getBranchName());
        contactBankAccountDTO.setBankName(contactBankAccountImportDTO.getBankName());
        contactBankAccountDTO.setBankCode(contactBankAccountImportDTO.getBankCode());
        contactBankAccountDTO.setAccountLocation(contactBankAccountImportDTO.getAccountLocation());
        contactBankAccountDTO.setTenantId(contactBankAccountImportDTO.getTenantId());
        return contactBankAccountDTO;
    }
}
