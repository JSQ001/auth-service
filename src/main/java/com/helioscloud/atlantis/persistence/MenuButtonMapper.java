package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.MenuButton;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface MenuButtonMapper extends BaseMapper<MenuButton> {
    // 根据菜单Id，删除菜单的按钮(逻辑删除)
    void deleteMenuButtonByMenuId(@Param("menuId") Long menuId);

    // 根据菜单Id，批量删除菜单的按钮(逻辑删除)
    void deleteMenuButtonByMenuIds(@Param("menuIds") List<Long> menuId);
}
