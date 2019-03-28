package com.hand.hcf.app.base.userRole.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.userRole.domain.RoleMenuButton;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RoleMenuButtonMapper extends BaseMapper<RoleMenuButton> {
    //根据角色ID，按钮ID集合，删除角色与按钮ID集合的关联 物理删除
    void deleteRoleMenuButtonByRoleIdAndButtonIds(@Param("roleId") Long roleId, @Param("buttonIds") List<Long> buttonIds);

    //根据角色ID，按钮ID集合，删除角色与菜单ID集合的关联 物理删除
    void deleteRoleMenuButtonByRoleIdAndMenuIds(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    // 根据角色ID，返回已分配的菜单ID的集合
    List<String> getMenuButtonIdsByRoleId(@Param("roleId") Long roleId);

}
