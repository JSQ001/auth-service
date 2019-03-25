package com.hand.hcf.app.base.userRole.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.userRole.domain.MenuButton;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface MenuButtonMapper extends BaseMapper<MenuButton> {
    // 根据菜单Id，删除菜单的按钮(逻辑删除)
    void deleteMenuButtonByMenuId(@Param("menuId") Long menuId);

    // 根据菜单Id，批量删除菜单的按钮(逻辑删除)
    void deleteMenuButtonByMenuIds(@Param("menuIds") List<Long> menuId);
    // 根据菜单ID，用户ID，返回菜单在用户已分配角色中分配的按钮 用于界面菜单的按钮显示控制
    List<MenuButton> getMenuButtonsByMenuIdAndUserId(@Param("menuId") Long menuId, @Param("userId") Long userId);
}
