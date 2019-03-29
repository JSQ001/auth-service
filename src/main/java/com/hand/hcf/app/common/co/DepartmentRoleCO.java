package com.hand.hcf.app.common.co;

import lombok.Data;

import java.util.UUID;

@Data
public class DepartmentRoleCO {

    private Long departmentId;

    private UUID managerOid;        //部门经理

    private UUID chargeManagerOid;  //部门总监

    private UUID hrbpOid;

    private UUID financialBPOid;    //财务bp

    private UUID financialAPOid;   //财务ap

    private UUID legalReviewOid;    //法务

    private UUID administrativeReviewOid;   //行政

    private UUID financialDirectorOid;  //财务总监
    /*
    vice_manager
    general_manager
    director
    vice_president
    president
    financial_vp
    financial_manager
    hr
     */

    private UUID viceManagerOid;    //副经理

    private UUID departmentManagerOid;  //部门主管

    private UUID vicePresidentOid;      //副总裁

    private UUID presidentOid;  //总裁

    private UUID financialManagerOid;   //财务经理
}
