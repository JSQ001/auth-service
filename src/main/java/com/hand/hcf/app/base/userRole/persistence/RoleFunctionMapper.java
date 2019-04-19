package com.hand.hcf.app.base.userRole.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.userRole.domain.RoleFunction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/28
 */
public interface RoleFunctionMapper extends BaseMapper<RoleFunction>{

    /**
     * 更新功能id
     * @param tenantId
     */
     void updateRoleFunction(@Param("tenantId") Long tenantId);

    /**
     * 根据租户id查询系统管理员分配的功能
     * @param adminRoleId
     * @param tenantId
     * @param roleId
     * @return
     */
     List<RoleFunction> getRoleFunctionByAdminRoleIdAndTenantId(@Param("adminRoleId") Long adminRoleId,@Param("tenantId") Long tenantId,@Param("roleId") Long roleId);
}
