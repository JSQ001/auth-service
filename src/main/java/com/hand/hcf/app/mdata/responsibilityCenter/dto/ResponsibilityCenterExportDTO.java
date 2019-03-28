package com.hand.hcf.app.mdata.responsibilityCenter.dto;

import lombok.Data;

@Data
public class ResponsibilityCenterExportDTO {
    //租户id
    private Long tenantId;

    //账套id
    private Long setOfBooksId;

    //责任中心代码
    private String responsibilityCenterCode;

    //责任中心名称
    private String responsibilityCenterName;

    //责任中心类型
    private String responsibilityCenterType;
    //是否启用
    private String enabled;
}
