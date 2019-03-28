package com.hand.hcf.app.mdata.department.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.DomainLogicEnable;
import lombok.Data;

@Data
@TableName("sys_department_position_user")
public class DepartmentPositionUser extends DomainLogicEnable {

    @TableField(value = "tenant_id")

    private long tenantId;
    @TableField(value = "position_id")

    private long positionId;
    @TableField(value = "department_id")

    private long departmentId;
    @TableField(value = "user_id")

    private long userId;
}
