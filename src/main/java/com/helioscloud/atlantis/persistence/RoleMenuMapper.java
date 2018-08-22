package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.domain.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    // 根据角色ID集合，返回角色已分配的菜单
    List<Menu> getMenusByRoleIds(@Param("roleIds") List<Long> roleIds);


    // 根据用户ID，返回用户的所有角色已分配的菜单
    List<Menu> getMenusByUserId(@Param("userId") Long userId);

}
