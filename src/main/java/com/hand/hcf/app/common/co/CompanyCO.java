package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


/**
 * A Company.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyCO {

    private Long id;
    /**
     * 公司Oid
     */
    private UUID companyOid;

    private String name;
    /**
     * 账套id
     */

    private Long setOfBooksId;

    private String setOfBooksName;

    /**
     * 法人实体id
     */

    private Long legalEntityId;
    /**
     * 公司编码
     */
    private String companyCode;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 公司级别id
     */

    private Long companyLevelId;
    /**
     * 上级公司id
     */

    private Long parentCompanyId;


    private String companyTypeCode;
    private String companyTypeName;
    /**
     * 租户Id
     */

    private Long tenantId;

    private String baseCurrency;
}
