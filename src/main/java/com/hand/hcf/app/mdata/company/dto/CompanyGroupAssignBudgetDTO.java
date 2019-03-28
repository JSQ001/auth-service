package com.hand.hcf.app.mdata.company.dto;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Created by silence on 2018/3/12.
 */
@Data
public class CompanyGroupAssignBudgetDTO {

    private Long id;

    private String companyCode; // 公司编码

    private String name; // 公司名称

    private Boolean isEnabled; // 启用标志


    private Long tenantId;      // 租户id


    private Long setOfBooksId;  // 账套id
    private String setOfBooksName; // 账套名称


    private Long legalEntityId; // 法人实体id
    private String legalEntityName; // 法人实体名称


    private Long companyLevelId;// 公司级别id
    private String companyLevelName; // 公司级别名称


    private Long parentCompanyId;// 上级公司id
    private String parentCompanyName; //  上级公司名称


    private String companyTypeCode; // 公司类型id
    private String companyTypeName; // 公司类型名称

    private ZonedDateTime startDateActive;// 开始有效日期

    private ZonedDateTime endDateActive;// 结束有效日期

}
