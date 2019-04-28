package com.hand.hcf.app.base.userRole.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.Role;
import com.hand.hcf.app.base.userRole.domain.UserRole;
import com.hand.hcf.app.base.userRole.dto.UserAssignRoleDataAuthority;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface UserRoleMapper extends BaseMapper<UserRole> {
    //根据角色ID，菜单ID集合，删除角色与菜单ID集合的关联 物理删除
    void deleteUserRoleByUserIdAndRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 根据租户ID，[角色代码]，[角色名称] 获取所有角色 分页
     */
    List<Role> getAllRolesByCond(@Param("tenantId") Long tenantId, @Param("roleCode") String roleCode, @Param("roleName") String roleName, Page page);

    /**
     * 根据用户ID，[角色代码]，[角色名称] 获取已分配的角色 分页
     */
    List<Role> getSelectedRolesByCond(@Param("userId") Long user_id, @Param("roleCode") String roleCode, @Param("roleName") String roleName, Page page);

    /**
     * 判断用户是否含有启用的角色
     *
     * @param userId
     * @return
     */
    Integer userHasRole(@Param("userId") Long userId);

    /**
     * 根据用户ID，[角色代码]，[角色名称],[数据权限名称]获取已分配的角色 分页
     */
    List<UserAssignRoleDataAuthority> listSelectedUserRolesByCond(@Param("userId") Long user_id, @Param("roleCode") String roleCode, @Param("roleName") String roleName,
                                                                  @Param("dataAuthorityName") String dataAuthorityName,
                                                                  @Param("validDateFrom") ZonedDateTime validDateFrom,
                                                                  @Param("validDateTo") ZonedDateTime validDateTo,
                                                                  Page page);

    /**
     * 根据租户ID，[角色代码]，[角色名称] 获取所有角色 不分页
     */
    List<Role> getAllRolesByCond(@Param("tenantId") Long tenantId, @Param("roleCode") String roleCode, @Param("roleName") String roleName);

    List<Long> getRoleIdByUserIdAndTime(@Param("userId") Long userId,
                                        @Param("now") ZonedDateTime now);

    /**
     * 根据用户及功能ID获取数据权限ID
     *
     * @param userId
     * @param now
     * @param functionId
     * @return
     */
    List<Long> listDataAuthIdByFunctionId(@Param("userId") Long userId,
                                          @Param("now") ZonedDateTime now,
                                          @Param("functionId") Long functionId);

    /**
     * 校验数据权限规则是否被使用
     *
     * @param id 数据权限id
     * @return count值
     */
    Integer dataAuthHasUsed(@Param("dataAuthority_id") Long id);

    /**
     * jiu.zhao 根据数据权限ID获取数据权限code和name
     */
    UserAssignRoleDataAuthority getDataAuthName(@Param("id") Long id);
}
