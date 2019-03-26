package com.hand.hcf.app.mdata.company.dto;

import lombok.Data;

/**
 * Created by silence on 2017/9/19.
 */
@Data
public class CompanyGroupAssignDTO {

    private Long id;  //  公司组分配明细ID

    private Long companyId;     //  公司ID
    private String companyCode;  //  公司代码
    private String companyName;  //  公司名称
    private String companyTypeName;  //  公司类型名称
}
