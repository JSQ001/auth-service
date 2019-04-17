package com.hand.hcf.app.mdata.responsibilityCenter.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_department_res_center")
public class DepartmentResponsibilityCenter extends Domain {

    //租户id
    private Long tenantId;

    //账套id
    private Long setOfBooksId;

    //部门id
    private Long departmentId;

    //关联id
    private Long relationId;

    //责任中心id
    @TableField("responsibility_center_id")
    private Long responsibilityCenterId;

}
