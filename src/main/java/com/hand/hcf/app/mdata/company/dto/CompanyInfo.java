package com.hand.hcf.app.mdata.company.dto;

import lombok.Data;

import javax.persistence.Id;
import java.util.Objects;

/**
 * A Company.
 */
@Data
public class CompanyInfo {

    @Id

    private Long id;
    private String companyOid;
    private String name;
    private boolean doneRegisterLead = false;
    private String taxId;       //税务证件号
    private Boolean isInitFinance;//是否创建好财务默认角色，默认FALSE
    private Boolean enabled;
    private String groupCompanyOid;

    private Long tenantId;      // 租户id

    private Long setOfBooksId;  // 账套id


    private Long legalEntityId; // 法人实体id

    private String companyCode; // 公司编码


    private Long companyLevelId;// 公司级别id


    private Long parentCompanyId;// 上级公司id


    private String companyTypeCode;


    private String companyTypeName;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanyInfo company = (CompanyInfo) o;
        return Objects.equals(id, company.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Company{" +
            "id=" + id +
            ", companyOid='" + companyOid + "'" +
            ", name='" + name + "'" +
            '}';
    }

}
