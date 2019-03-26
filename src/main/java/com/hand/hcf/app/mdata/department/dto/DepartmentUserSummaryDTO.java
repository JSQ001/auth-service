package com.hand.hcf.app.mdata.department.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;
import com.hand.hcf.app.mdata.department.domain.Department;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Created by lichao on 17/9/25.
 * 查询部门用户
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentUserSummaryDTO {
    private UUID userOid;
    private String fullName;
    private List<Department> departments;
    private List<UserDTO> users;
}
