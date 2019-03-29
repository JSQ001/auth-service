package com.hand.hcf.app.mdata.department.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.department.domain.DepartmentPositionUser;
import com.hand.hcf.app.mdata.department.dto.DepartmentPositionUserDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentPositionUserMapper extends BaseMapper<DepartmentPositionUser> {
    Long selectUser(@Param("tenantId") Long tenantId, @Param("departmentId") Long departmentId, @Param("positionCode") String positionCode);
    Long selectUserByDepartmentOidAndPositionCode(@Param("departmentOid") String departmentOid, @Param("positionCode") String positionCode);

    /**
     * 根据租户id、部门状态查询部门角色用户信息
     * @param tenantId：租户id
     * @param departmentStatus：部门状态
     * @return
     */
    List<DepartmentPositionUserDTO> selectByTenantIdAndDepartmentStatus(@Param("tenantId") Long tenantId, @Param("departmentStatus") Integer departmentStatus);
}
