package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.domain.RoleMenu;
import com.helioscloud.atlantis.dto.RoleAssignMenuButtonDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    // 根据角色ID集合，返回角色已分配的菜单
    List<Menu> getMenusByRoleIds(@Param("roleIds") List<Long> roleIds);


    // 根据用户ID，返回用户的所有角色已分配的菜单
    List<Menu> getMenusByUserId(@Param("userId") Long userId);

    //根据角色ID，菜单ID集合，删除角色与菜单ID集合的关联 物理删除
    void deleteRoleMenuByRoleIdAndMenuIds(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    // 根据菜单ID集合，返回菜单的父菜单的集合
    List<Long> getParentMenuIdsByRoleIds(@Param("menuIds") List<Long> menuIds);

    // 根据角色ID，返回已分配的菜单ID及按钮ID的集合
    List<String> getMenuIdsAndButtonIdsByRoleId(@Param("roleId") Long roleId);

    // 返回所有菜单和菜单按钮
    List<RoleAssignMenuButtonDTO> getAllMenuAndButton();

    //根据菜单ID,角色ID，判断是否有其子菜单分配了该角色的菜单权限，根据判断的结果，去删除没有子菜单但又分配了菜单权限的菜单
    Integer hasAssignChildrenRoleMenu(@Param("menuId") Long menuId,@Param("roleId") Long roleId);
}
