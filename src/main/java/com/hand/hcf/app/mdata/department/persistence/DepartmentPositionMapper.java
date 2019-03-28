package com.hand.hcf.app.mdata.department.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.department.domain.DepartmentPosition;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface DepartmentPositionMapper extends BaseMapper<DepartmentPosition> {
    List<DepartmentPosition> selectDepartmentPositionByUserAndDepartment(@Param("departmentId") Long departmentId, @Param("userOid") UUID userOid);

    List<DepartmentPosition> getDepartmentPositionList(Pagination page, @Param(value = "tenantId") long tenantId, @Param(value = "enabled") boolean enabled);
}
