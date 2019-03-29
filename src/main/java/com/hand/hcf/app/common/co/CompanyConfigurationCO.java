package com.hand.hcf.app.common.co;

import lombok.Data;

@Data
public class CompanyConfigurationCO {
    private Integer approvalMode = 1003;//默认选人审批
    private Integer maxApprovalChain = -1;//审批链长度
    private Integer approvalPathMode = 1001;//获取审批人模式,全链
    private Integer departmentLevel = 1;//选部门审批部门级
}
