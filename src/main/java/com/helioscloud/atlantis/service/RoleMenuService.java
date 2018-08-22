package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.domain.RoleMenu;
import com.helioscloud.atlantis.dto.MenuDTO;
import com.helioscloud.atlantis.dto.RoleMenuDTO;
import com.helioscloud.atlantis.persistence.RoleMenuMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 用户角色Service
 */
@Service
public class RoleMenuService extends BaseService<RoleMenuMapper, RoleMenu> {

    private final RoleMenuMapper roleMenuMapper;

    private final MenuService menuService;

    public RoleMenuService(RoleMenuMapper roleMenuMapper, MenuService menuService) {
        this.roleMenuMapper = roleMenuMapper;
        this.menuService = menuService;
    }

    /**
     * 保存角色菜单
     *
     * @param roleMenu
     * @return
     */
    @Transactional
    public RoleMenu createRoleMenu(RoleMenu roleMenu) {
        //校验
        if (roleMenu == null || roleMenu.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        //检查用户角色组合
        Integer count = getRoleMenuCountByMenuIdAndRoleId(roleMenu.getMenuId(), roleMenu.getRoleId());
        if (count != null && count > 1) {
            throw new BizException(RespCode.ROLE_MENU_EXISTS);
        }
        roleMenuMapper.insert(roleMenu);
        return roleMenu;
    }

    /**
     * 更新用户角色菜单
     *
     * @param roleMenu
     * @return
     */
    @Transactional
    public RoleMenu updateRole(RoleMenu roleMenu) {
        //校验
        if (roleMenu == null || roleMenu.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        //校验ID是否在数据库中存在
        RoleMenu roleMenu1 = roleMenuMapper.selectById(roleMenu.getId());
        if (roleMenu1 == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (roleMenu1 == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (roleMenu.getIsEnabled() == null || "".equals(roleMenu.getIsEnabled())) {
            roleMenu.setIsEnabled(roleMenu1.getIsEnabled());
        }
        if (roleMenu.getIsDeleted() == null || "".equals(roleMenu.getIsDeleted())) {
            roleMenu.setIsDeleted(roleMenu1.getIsDeleted());
        }
        roleMenu.setCreatedBy(roleMenu1.getCreatedBy());
        roleMenu.setCreatedDate(roleMenu1.getCreatedDate());
        this.updateById(roleMenu);
        return roleMenu;
    }

    /**
     * 检查角色和菜单的组合是否已经存在
     *
     * @param menuId
     * @param roleId
     * @return
     */
    public Integer getRoleMenuCountByMenuIdAndRoleId(Long menuId, Long roleId) {
        return roleMenuMapper.selectCount(new EntityWrapper<RoleMenu>()
                .eq("menu_id", menuId)
                .eq("role_id", roleId));
    }

    /**
     * @param id 删除角色菜单（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteRoleMenu(Long id) {
        if (id != null) {
            roleMenuMapper.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除角色菜单（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchRoleMenu(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 根据角色Id，获取分配的所有菜单
     * @param roleId    角色ID
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @param page
     * @return
     */
    public List<RoleMenuDTO> getRoleMenusByRoleId(Long roleId, Boolean isEnabled, Page page) {
        List<RoleMenuDTO> result = new ArrayList<RoleMenuDTO>();
        List<RoleMenu> list = roleMenuMapper.selectPage(page, new EntityWrapper<RoleMenu>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("role_id", roleId)
                .orderBy("last_updated_date"));
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(e -> {
                RoleMenuDTO roleMenuDto = new RoleMenuDTO();
                roleMenuDto.setId(e.getId());
                roleMenuDto.setMenuId(e.getMenuId());
                roleMenuDto.setRoleId(e.getRoleId());
                roleMenuDto.setMenu(menuService.getMenuById(e.getMenuId()));
                result.add(roleMenuDto);
            });
        }
        return result;
    }

    /**
     * 根据ID，获取对应的角色菜单信息
     *
     * @param id
     * @return
     */
    public RoleMenu getRoleMenuById(Long id) {
        return roleMenuMapper.selectById(id);
    }

    /**
     * 根据用户ID，取对应所有角色分配的菜单
     * @param userId
     * @return
     */
    public List<MenuDTO> getMenusByUserId(Long userId) {
        List<MenuDTO> dtos = new ArrayList<>();
        Map<Long,MenuDTO> resultMenu = new HashMap<>();
        if(userId != null){
            //获取用户ID，取对应的所有去重的菜单 （已启用且未被删除的数据）
            List<Menu> list = roleMenuMapper.getMenusByUserId(userId);
            if(list != null && list.size() > 0 ){
                //将list 转成 map 且以 ID 为map的key
                Map<Long,Menu> menuMap = new HashMap<>();
                list.stream().forEach(d -> {
                    menuMap.put(d.getId(),d);
                });
                //构造菜单数据
                list.stream().forEach(e -> {
                    createMenuRecursive(resultMenu,menuMap,e.getId());
                });
            }
        }
        // 将结果菜单map转成List，返回到前端 只返回parent为null的数据
        dtos = resultMenu.entrySet().stream().filter(e->e.getValue().getParent() == null).map(m-> {
            return m.getValue();
        }).collect(Collectors.toList());
        return dtos;
    }

    /**
     * 根据角色集合，取对应的菜单信息
     * @param roleIds
     * @return
     */
    public List<MenuDTO> getMenusByRolesId(List<Long> roleIds) {
        List<MenuDTO> dtos = new ArrayList<>();
        Map<Long,MenuDTO> resultMenu = new HashMap<>();
        if(roleIds != null && roleIds.size() > 0 ){
            //获取角色集合对应的所有去重的菜单 （已启用且未被删除的数据）
            List<Menu> list = roleMenuMapper.getMenusByRoleIds(roleIds);
            if(list != null && list.size() > 0 ){
                //将list 转成 map 且以 ID 为map的key
                Map<Long,Menu> menuMap = new HashMap<>();
                list.stream().forEach(d -> {
                    menuMap.put(d.getId(),d);
                });
                //构造菜单数据
               list.stream().forEach(e -> {
                   createMenuRecursive(resultMenu,menuMap,e.getId());
               });
            }
        }
        // 将结果菜单map转成List，返回到前端 只返回parent为null的数据
        dtos = resultMenu.entrySet().stream().filter(e->e.getValue().getParent() == null).map(m-> {
            return m.getValue();
        }).collect(Collectors.toList());
        return dtos;
    }

    /**
     * @param resultMap 返回的菜单
     * @param menuMap  角色对应的所有菜单
     * @param menuId  当前菜单ID
     * @return 功能菜单
     */
    private MenuDTO createMenuRecursive(Map<Long, MenuDTO> resultMap, Map<Long, Menu> menuMap, Long menuId) {
        MenuDTO menuItem = resultMap.get(menuId);
        if (menuItem == null) {
            menuItem = new MenuDTO();
            Menu menu = menuMap.get(menuId);// 根据ID从所有菜单中取菜单对象
            if (menu == null) {
                return null;
            }
            //将Domain转成DTO对象
            BeanUtils.copyProperties(menu,menuItem);
            // 以ID为Key，将菜单存到map中去
            resultMap.put(menuId, menuItem);
            Long parentId = menu.getParentMenuId();
            //如果菜单的上级菜单不为空,且大于0（根目录的上级菜单为0）
            if (parentId != null && parentId > 0) {
                //递归设置上级菜单
                MenuDTO parentMenuItem = createMenuRecursive(resultMap, menuMap, parentId);
                if (parentMenuItem != null) {
                    List<MenuDTO> children = parentMenuItem.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parentMenuItem.setChildren(children);
                    }
                    menuItem.setParent(parentMenuItem);
                    children.add(menuItem);
                }
            }
        }
        return menuItem;
    }
}
