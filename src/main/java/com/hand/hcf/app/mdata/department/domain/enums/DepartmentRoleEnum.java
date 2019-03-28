package com.hand.hcf.app.mdata.department.domain.enums;

/**
 * 部门角色枚举
 */
public enum DepartmentRoleEnum {
    MANAGER,    //部门主管=部门经理(晶科)
    CHARGE_MANAGER, //部门分主管=部门总监(晶科)
    HRBP, //HRBP=HR
    FINANCIAL_BP,    //财务BP
    FINANCIAL_AP,   //财务AP
    LEGAL_REVIEW,       //法务审核
    ADMINISTRATIVE_REVIEW,      //行政审核
    FINANCIAL_DIRECTOR,          //财务主管=财务总监(晶科)
    //晶科项目新增职位
    VICE_MANAGER,           // 副经理
    DEPARTMENT_MANAGER,     // 部门主管
    VICE_PRESIDENT,         // 副总裁
    PRESIDENT,              // 总裁
    FINANCIAL_MANAGER,      // 财务经理(晶科)
    NORMAL,                  // 普通员工(仅用于标识，没有实际字段对应，反射时候需要跳过)
}
