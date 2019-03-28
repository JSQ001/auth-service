package com.hand.hcf.app.mdata.department.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * 部门角色关联用户视图对象
 * Created by Strive on 18/7/20.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DepartmentPositionUserDTO {

    private Long id;

    private Long tenantId;

    private Long positionId;
    private String path;

    private Long departmentId;

    private Long userId;
    private Boolean enabled;
    private Boolean deleted;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastUpdatedDate;
    private String createdBy;
    private String lastUpdatedBy;
}
