package com.hand.hcf.app.mdata.department.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@TableName("sys_department_group_detail")
public class DepartmentGroupDetail extends DomainLogicEnable implements Serializable {

    @NotNull
    @TableField("department_group_id")

    private Long departmentGroupId;
    @NotNull

    @TableField("department_id")
    private Long departmentId;
    @TableField("tenant_id")

    @JsonIgnore
    private Long tenantId;

}
