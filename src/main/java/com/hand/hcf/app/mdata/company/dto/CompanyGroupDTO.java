package com.hand.hcf.app.mdata.company.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by silence on 2017/9/19.
 */
@Data
public class CompanyGroupDTO {

    private Long id; //  公司组ID
    private String companyGroupCode; //  公司组代码
    private String companyGroupName; //  公司组名称

    private Long setOfBooksId; //  账套ID
    private String setOfBooksCode; //  账套代码
    private String setOfBooksName;  // 账套名称
    private Boolean enabled; //  启用标志
    private Map<String, List<Map<String, String>>> i18n;  //  多语言字段


}
