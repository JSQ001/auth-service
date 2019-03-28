package com.hand.hcf.app.mdata.department.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_department_position")
public class DepartmentPosition extends DomainI18nEnable implements Serializable {
    private static final long serialVersionUID = 6598280566761479766L;
    @TableField(value = "tenant_id")

    private Long tenantId;
    @TableField(value = "position_code")
    private String positionCode;
    @I18nField
    @TableField(value = "position_name")
    private String positionName;
}
