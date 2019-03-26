package com.hand.hcf.app.mdata.company.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * A Company.
 */
@Data
@TableName("sys_company")
public class Company extends DomainI18nEnable implements Serializable {

    private static final long serialVersionUID = -8465413236662463622L;

    @NotNull
    @TableField("company_oid")
    private UUID companyOid;

    @NotNull
    @Size(max = 255)
    @I18nField
    private String name;

    private Boolean doneRegisterLead = false;

    private String taxId;       //税务证件号

    private Boolean initFinance;//是否创建好财务默认角色，默认FALSE

    @TableField("group_company_oid")
    private UUID groupCompanyOid;


    private Long tenantId;      // 租户id


    private Long setOfBooksId;  // 账套id


    private Long legalEntityId; // 法人实体id

    private String companyCode; // 公司编码

    private String address;     // 公司地址

    @TableField(exist = false)
    private String companyCodeName; //公司代码-名称

    @TableField(strategy = FieldStrategy.IGNORED)
    private Long companyLevelId;// 公司级别id

    @TableField(strategy = FieldStrategy.IGNORED)
    private Long parentCompanyId;// 上级公司id


    private String companyTypeCode;

    private ZonedDateTime startDateActive;// 开始有效日期

    @TableField(strategy = FieldStrategy.IGNORED)
    private ZonedDateTime endDateActive;// 结束有效日期

    private String path;

    private Integer depth;

    private Boolean showCustomLogo;


    private Long logoId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Company company = (Company) o;
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
