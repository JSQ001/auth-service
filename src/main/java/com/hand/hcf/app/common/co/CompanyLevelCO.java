package com.hand.hcf.app.common.co;

import lombok.Data;

@Data
public class CompanyLevelCO {

    //租户id
    private Long tenantId;

    //公司级别代码
    private String companyLevelCode;

    //公司级别描述
    private String description;

}
