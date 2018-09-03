package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Menu;
import com.helioscloud.atlantis.domain.RoleMenu;
import com.helioscloud.atlantis.domain.RoleMenuButton;
import com.helioscloud.atlantis.domain.enumeration.FlagEnum;
import com.helioscloud.atlantis.domain.enumeration.MenuTypeEnum;
import com.helioscloud.atlantis.dto.MenuTreeDTO;
import com.helioscloud.atlantis.dto.RoleAssignMenuButtonDTO;
import com.helioscloud.atlantis.dto.RoleMenuButtonDTO;
import com.helioscloud.atlantis.dto.RoleMenuDTO;
import com.helioscloud.atlantis.persistence.RoleMenuMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 用户角色Service
 */
@Service
public class RoleMenuService extends BaseService<RoleMenuMapper, RoleMenu> {

    private final RoleMenuMapper roleMenuMapper;

    private final MenuService menuService;

    private final MenuButtonService menuButtonService;

    private final RoleMenuButtonService roleMenuButtonService;

    public RoleMenuService(RoleMenuMapper roleMenuMapper, MenuService menuService, MenuButtonService menuButtonService, RoleMenuButtonService roleMenuButtonService) {
        this.roleMenuMapper = roleMenuMapper;
        this.menuService = menuService;
        this.menuButtonService = menuButtonService;
        this.roleMenuButtonService = roleMenuButtonService;
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
     * 根据菜单ID集合，返回 其所有父菜单的ID集合
     *
     * @param menuIds
     * @return
     */
    public List<Long> getParentMenusByIds(List<Long> menuIds) {
        return roleMenuMapper.getParentMenuIdsByRoleIds(menuIds);
    }

    /**
     * 角色在分配菜单时，每次都是把分配的菜单全量的传到后端保存
     * 保存时，前端只传hasChildCatalog 为 false的数据
     * flag：创建:1001，删除:1002
     *
     * @param roleMenuDTO
     * @return
     */
    @Transactional
    public void roleAssignMenu(RoleMenuButtonDTO roleMenuDTO) {
        Long roleId = null;

        List<RoleAssignMenuButtonDTO> assignMenuButton = null;
        //需要删除的
        List<Long> deleteMenuIds = new ArrayList<>();
        //需要保存的
        List<RoleMenu> roleMenuList = new ArrayList<>();

        List<Long> roleMenuIdList = new ArrayList<>();
        //菜单按钮
        List<RoleMenuButton> buttonList = new ArrayList<RoleMenuButton>();
        List<Long> deleteButtonIdList = new ArrayList<>();
        if (roleMenuDTO != null) {
            roleId = roleMenuDTO.getRoleId();
            assignMenuButton = roleMenuDTO.getAssignMenuButtonList();
        }
        if (assignMenuButton != null) {
            // 所有需要新增的菜单ID
            Long finalRoleId = roleId;
            //取所有的菜单ID
            assignMenuButton.stream().filter(m -> MenuTypeEnum.DIRECTORY.toString().equals(m.getType())).forEach(d -> {
                if (FlagEnum.DELETE.getID().toString().equals(d.getFlag())) {
                    // 需要删除的菜单ID
                    deleteMenuIds.add(d.getId());
                } else if (FlagEnum.CREATE.getID().toString().equals(d.getFlag())) {
                    // 需要添加的
                    //检查是否已经存在，已经存在的，则不更新
                    Integer exists = roleMenuMapper.selectCount(new EntityWrapper<RoleMenu>().eq("role_id", finalRoleId).eq("menu_id", d.getId()));
                    if (exists == null || exists == 0) {
                        roleMenuIdList.add(d.getId());
                    }
                }
            });
            // 取菜单的按钮
            assignMenuButton.stream().filter(m -> MenuTypeEnum.BUTTON.toString().equals(m.getType())).forEach(d -> {
                if (FlagEnum.CREATE.getID().toString().equals(d.getFlag())) {
                    // 需要创建的按钮ID
                    RoleMenuButton bm = new RoleMenuButton();
                    bm.setButtonId(d.getId());
                    bm.setRoleId(finalRoleId);
                    buttonList.add(bm);
                    //将按钮对应的菜单Id，加入到需要添加的菜单集合中去
                    Long menuId = d.getParentId();
                    if (!roleMenuIdList.contains(menuId) && menuId > 0) {
                        roleMenuIdList.add(menuId);
                    }
                } else if (FlagEnum.DELETE.getID().toString().equals(d.getFlag())) {
                    // 需要删除的按钮ID
                    deleteButtonIdList.add(d.getId());
                }
            });
            if (deleteMenuIds.size() > 0) {
                // 删除角色关联菜单
                roleMenuMapper.deleteRoleMenuByRoleIdAndMenuIds(roleId, deleteMenuIds);
                //删除角色关联按钮
                roleMenuButtonService.deleteRoleMenuButtonByRoleIdAndMenuIds(roleId, deleteMenuIds);
            }
            //所有父菜单ID集合
            List<Long> parentMenuIds = new ArrayList<>();
            boolean flag = true;
            int i = 0;
            List<Long> tempList = null;
            while (flag) {
                if (i == 0) {
                    tempList = getParentMenusByIds(roleMenuIdList);
                } else {
                    tempList = getParentMenusByIds(tempList);
                }
                if (tempList != null && tempList.size() > 0) {
                    parentMenuIds.addAll(tempList);
                } else {
                    flag = false;
                }
                i++;
            }
            if (parentMenuIds.size() > 0) {
                //去重，并将父菜单ID 添加进去 取父菜单ID大于0的
                parentMenuIds.stream().forEach(parentId -> {
                    if (!roleMenuIdList.contains(parentId) && parentId > 0) {
                        roleMenuIdList.add(parentId);
                    }
                });
            }
            roleMenuIdList.stream().forEach(menuId -> {
                Integer count = this.getRoleMenuCountByMenuIdAndRoleId(menuId,finalRoleId);
                if(count == null || count == 0){
                    RoleMenu rm = new RoleMenu();
                    rm.setRoleId(finalRoleId);
                    rm.setMenuId(menuId);
                    roleMenuList.add(rm);
                }
            });
            if (roleMenuList.size() > 0) {
                //保存角色与菜单的关联
                this.insertBatch(roleMenuList);
            }
            //处理菜单的按钮
            if (buttonList != null && buttonList.size() > 0) {
                //删除角色与按钮的关联
                roleMenuButtonService.deleteRoleMenuButtonByRoleIdAndButtonIds(roleId, deleteButtonIdList);
                //保存角色关联菜单的按钮
                roleMenuButtonService.batchSaveRoleMenuButton(buttonList);
            }
        }
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
        if (roleMenu.getEnabled() == null || "".equals(roleMenu.getEnabled())) {
            roleMenu.setEnabled(roleMenu1.getEnabled());
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
     * @param id 删除角色菜单（物理删除）
     * @return
     */
    @Transactional
    public void deleteRoleMenu(Long id) {
        if (id != null) {
            roleMenuMapper.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除角色菜单（物理删除）
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
     *
     * @param roleId    角色ID
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @param page
     * @return
     */
    public List<RoleMenuDTO> getRoleMenusByRoleId(Long roleId, Boolean enabled, Page page) {
        List<RoleMenuDTO> result = new ArrayList<RoleMenuDTO>();
        List<RoleMenu> list = roleMenuMapper.selectPage(page, new EntityWrapper<RoleMenu>()
                .eq(enabled != null, "enabled", enabled)
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
     * 根据角色集合，取对应的菜单树信息
     *
     * @param roleIds
     * @return
     */
    public List<MenuTreeDTO> getMenuTreeByRolesId(List<Long> roleIds) {
        List<MenuTreeDTO> dtos = new ArrayList<>();
        Map<Long, MenuTreeDTO> resultMenu = new HashMap<>();
        if (roleIds != null && roleIds.size() > 0) {
            //获取角色集合对应的所有去重的菜单 （已启用且未被删除的数据）
            List<Menu> list = roleMenuMapper.getMenusByRoleIds(roleIds);
            if (list != null && list.size() > 0) {
                //将list 转成 map 且以 ID 为map的key
                Map<Long, Menu> menuMap = new HashMap<>();
                list.stream().forEach(d -> {
                    menuMap.put(d.getId(), d);
                });
                //构造菜单数据
                list.stream().forEach(e -> {
                    createMenuTreeRecursive(resultMenu, menuMap, e.getId());
                });
            }
        }
        // 将结果菜单map转成List，返回到前端 只返回parent为null的数据
        dtos = resultMenu.entrySet().stream().filter(e -> e.getValue().getParent() == null).map(m -> {
            return m.getValue();
        }).collect(Collectors.toList());
        return dtos;
    }

    /**
     * 根据用户ID，取对应所有角色分配的菜单树
     *
     * @param userId
     * @return
     */
    public List<MenuTreeDTO> getMenuTreeByUserId(Long userId) {
        List<MenuTreeDTO> dtos = new ArrayList<>();
        Map<Long, MenuTreeDTO> resultMenu = new HashMap<>();
        if (userId != null) {
            //获取用户ID，取对应的所有去重的菜单 （已启用且未被删除的数据）
            List<Menu> list = roleMenuMapper.getMenusByUserId(userId);
            if (list != null && list.size() > 0) {
                //将list 转成 map 且以 ID 为map的key
                Map<Long, Menu> menuMap = new HashMap<>();
                list.stream().forEach(d -> {
                    menuMap.put(d.getId(), d);
                });
                //构造菜单数据
                list.stream().forEach(e -> {
                    createMenuTreeRecursive(resultMenu, menuMap, e.getId());
                });
            }
        }
        // 将结果菜单map转成List，返回到前端 只返回parent为null的数据
        dtos = resultMenu.entrySet().stream().filter(e -> e.getValue().getParent() == null).map(m -> {
            return m.getValue();
        }).collect(Collectors.toList());
        return dtos;
    }

    /**
     * @param resultMap 返回的菜单
     * @param menuMap   角色对应的所有菜单树
     * @param menuId    当前菜单ID
     * @return 功能菜单
     */
    private MenuTreeDTO createMenuTreeRecursive(Map<Long, MenuTreeDTO> resultMap, Map<Long, Menu> menuMap, Long menuId) {
        MenuTreeDTO menuItem = resultMap.get(menuId);
        if (menuItem == null) {
            menuItem = new MenuTreeDTO();
            Menu menu = menuMap.get(menuId);// 根据ID从所有菜单中取菜单对象
            if (menu == null) {
                return null;
            }
            //将Domain转成DTO对象
            BeanUtils.copyProperties(menu, menuItem);
            // 以ID为Key，将菜单存到map中去
            resultMap.put(menuId, menuItem);
            Long parentId = menu.getParentMenuId();
            //如果菜单的上级菜单不为空,且大于0（根目录的上级菜单为0）
            if (parentId != null && parentId > 0) {
                //递归设置上级菜单
                MenuTreeDTO parentMenuItem = createMenuTreeRecursive(resultMap, menuMap, parentId);
                if (parentMenuItem != null) {
                    List<MenuTreeDTO> children = parentMenuItem.getChildren();
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

    /**
     * 用于登录
     * 根据用户ID，取对应所有角色分配的菜单
     *
     * @param userId
     * @return
     */
    public List<Menu> getMenuListByUserId(Long userId) {
        if (userId != null) {
            //获取用户ID，取对应的所有去重的菜单 （已启用且未被删除的数据）
            return roleMenuMapper.getMenusByUserId(userId);
        }
        return null;
    }

    /**
     * 取所有的菜单及按钮
     *
     * @return
     */
    public List<RoleAssignMenuButtonDTO> getAllMenuAndButton() {
        return roleMenuMapper.getAllMenuAndButton();
    }

    /**
     * 根据角色ID，返回已分配的菜单ID的集合（只取功能，不取目录）
     *
     * @param roleId
     * @return
     */
    public List<String> getMenuIdsAndButtonIdsByRoleId(Long roleId) {
        return roleMenuMapper.getMenuIdsAndButtonIdsByRoleId(roleId);
    }

    /**
     * 根据角色ID，返回已分配的菜单按钮ID的集合
     *
     * @param roleId
     * @return
     */
    public List<String> getMenuButtonIdsByRoleId(Long roleId) {
        return roleMenuButtonService.getMenuButtonIdsByRoleId(roleId);
    }

}
