package com.hand.hcf.app.mdata.department.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * A DTO for the Department entity.
 */
@Data
public class DepartmentDTO implements Serializable {
    private static final long serialVersionUID = -3233618557600166062L;

    private Long id ;

    private UUID departmentOid;


    private Long parentDepartmentId;

    private UUID parentDepartmentOid;

    private String name;

    private String path;

    private UUID companyOid;

    private String companyName;

    private UUID managerOid;

    private String fullName;

    private boolean hasChildrenDepartments;

    private boolean hasUsers;

    private DepartmentRoleDTO departmentRole;

    private int status;

    private ZonedDateTime lastUpdatedDate;

    private Map<String, List<Map<String, String>>> i18n;

    private List<DepartmentPositionDTO> departmentPositionDTOList;


    private Long tenantId;

    private String dataSource;

    private String departmentCode;

}
