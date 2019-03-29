package com.hand.hcf.app.mdata.responsibilityCenter.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

@Data
@TableName("sys_department_sob_res")
public class DepartmentSobResponsibility extends Domain {
    //租户id
    private Long tenantId;

    //账套id
    private Long setOfBooksId;

    //部门id
    private Long departmentId;

    //公司id
    private Long companyId;

    @TableField("default_responsibility_center")
    private Long defaultResponsibilityCenter;

    @TableField("all_responsibility_center")
    private String allResponsibilityCenter;

}
