package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.RoleMenu;
import com.helioscloud.atlantis.dto.RoleMenuDTO;
import com.helioscloud.atlantis.persistence.RoleMenuMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        if(roleMenu.getIsEnabled() == null){
            roleMenu.setIsEnabled(roleMenu1.getIsEnabled());
        }
        if(roleMenu.getIsDeleted() == null){
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
        if(id != null){
            roleMenuMapper.deleteById(id);
        }
        /*RoleMenu roleMenu = roleMenuMapper.selectById(id);
        roleMenu.setIsDeleted(true);
        roleMenuMapper.updateById(roleMenu);*/
    }

    /**
     * @param ids 批量删除角色菜单（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchRoleMenu(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
            /*List<RoleMenu> result = null;
            List<RoleMenu> list = roleMenuMapper.selectBatchIds(ids);
            if (list != null && list.size() > 0) {
                result = list.stream().map(roleMenu -> {
                    roleMenu.setIsDeleted(true);
                    return roleMenu;
                }).collect(Collectors.toList());
            }
            if(result != null){
                this.updateBatchById(result);
            }*/
        }
    }


    /**
     * 根据角色Id，获取分配的所有菜单
     *
     * @param roleId    角色ID
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @param page
     * @return
     */
    public List<RoleMenuDTO> getRoleMenusByRoleId(Long roleId,Boolean isEnabled, Page page) {
        List<RoleMenuDTO> result = new ArrayList<RoleMenuDTO>();
        List<RoleMenu> list = roleMenuMapper.selectPage(page, new EntityWrapper<RoleMenu>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("role_id", roleId));

        /* if (isDeleted == null) {
            list = roleMenuMapper.selectPage(page, new EntityWrapper<RoleMenu>()
                    .eq("is_deleted", false)
                    .eq(isEnabled != null, "is_enabled", isEnabled)
                    .eq("role_id", roleId));
        } else {
            list = roleMenuMapper.selectPage(page, new EntityWrapper<RoleMenu>()
                    .eq("is_deleted", isDeleted)
                    .eq(isEnabled != null, "is_enabled", isEnabled)
                    .eq("role_id", roleId));
        }*/
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

}
