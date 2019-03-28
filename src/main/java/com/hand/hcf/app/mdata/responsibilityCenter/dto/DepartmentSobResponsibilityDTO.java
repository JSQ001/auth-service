package com.hand.hcf.app.mdata.responsibilityCenter.dto;

import com.hand.hcf.app.mdata.responsibilityCenter.domain.ResponsibilityCenter;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentSobResponsibilityDTO extends Domain {
    //账套ID
    private Long setOfBooksId;

    //账套code
    private String setOfBooksCode;

    //账套名称
    private String setOfBooksName;

    //公司id
    private Long companyId;

    //公司code
    private String companyCode;

    //公司
    private String companyName;

    //默认责任中心id
    private String defaultResponsibilityCenter;

    //默认责任中心
    private String defaultResponsibilityCenterName;

    //默认责任中心code
    private String defaultResponsibilityCenterCode;

    //Y 全部 N部分
    private String allResponsibilityCenter;

    //已选个数
    private Long allResponsibilityCenterCount;

    //部门id
    private Long departmentId;

    //租户id
    private Long tenantId;

    //责任中心ids
    private List<Long> ids;

    //已选责任中心
    private List<ResponsibilityCenter> responsibilityCentersList;
}
