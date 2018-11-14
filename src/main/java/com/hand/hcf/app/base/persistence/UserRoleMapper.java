package com.hand.hcf.app.base.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.domain.Role;
import com.hand.hcf.app.base.domain.UserRole;
import org.apache.ibatis.annotations.Param;

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
     * @param userId
     * @return
     */
    Integer userHasRole(@Param("userId") Long userId);
}
