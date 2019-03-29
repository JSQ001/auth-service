package com.hand.hcf.app.mdata.department.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18n;
import lombok.Data;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A Department.
 */
@Data
@TableName("sys_department")
public class Department extends DomainI18n {

    private static final long serialVersionUID = 5398247354231221545L;

    @NotNull
    @TableField(value = "department_oid")
    private UUID departmentOid;

    @TableField(exist = false)
    private Department parent;

    @TableField(exist = false)
    private List<Department> children;

    @NotNull
    @Size(max = 50)
    @I18nField
    private String name;

    @NotNull
    @Size(max = 1000)
    @I18nField
    private String path;


    @TableField(exist = false)
    private Company company;

    @TableField(exist = false)
    private UserDTO manager;

    @Transient
    private Long managerId;

    @Transient
    @TableField("parent_id")

    private Long parentId;


    @TableField(exist = false)
    private Set<UserDTO> users = new LinkedHashSet<UserDTO>();


    private Integer status;//部门状态

    /*@TableField(exist = false)
    private Set<FinanceRole> financeRoles = new HashSet<FinanceRole>();*/

    private Long tenantId;

    private String dataSource;

    @TableField(value = "department_code")
    private String departmentCode;


}
