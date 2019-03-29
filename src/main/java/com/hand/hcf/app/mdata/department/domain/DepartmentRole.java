package com.hand.hcf.app.mdata.department.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@TableName("sys_department_role")
public class DepartmentRole extends Domain {
    // 在反射获取getter时需要忽略不转换小写的字段片段
    public static final List<String> IGNORE_UPPER_CASE_LIST = Arrays.asList("AP", "BP", "VP");
    // getter方法名后缀
    public static final String SUFFIX = "Oid";


    private Long departmentId;
    @TableField("manager_oid")

    private UUID managerOid;        //部门经理
    @TableField("charge_manager_oid")

    private UUID chargeManagerOid;  //部门总监
    @TableField("hrbp_oid")

    private UUID hrbpOid;
    @TableField("financial_bp_oid")

    private UUID financialBPOid;    //财务bp
    @TableField("financial_ap_oid")

    private UUID financialAPOid;   //财务ap
    @TableField("legal_review_oid")

    private UUID legalReviewOid;    //法务
    @TableField("administrative_review_oid")

    private UUID administrativeReviewOid;   //行政
    @TableField("financial_director_oid")

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
    @TableField("vice_manager_oid")

    private UUID viceManagerOid;    //副经理
    @TableField("department_manager_oid")

    private UUID departmentManagerOid;  //部门主管
    @TableField("vice_president_oid")

    private UUID vicePresidentOid;      //副总裁
    @TableField("president_oid")

    private UUID presidentOid;  //总裁
    @TableField("financial_manager_oid")

    private UUID financialManagerOid;   //财务经理
}
