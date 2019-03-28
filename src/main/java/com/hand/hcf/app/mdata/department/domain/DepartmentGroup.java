package com.hand.hcf.app.mdata.department.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@TableName("sys_department_group")
public class DepartmentGroup extends DomainI18nEnable implements Serializable {
    @TableField("tenant_id")

    @JsonIgnore
    private Long tenantId;
    @NotNull
    @TableField("dept_group_code")
    private String deptGroupCode;
    @I18nField
    @TableField("description")
    private String description;
    @TableField("company_id")

    private UUID companyId;
}
