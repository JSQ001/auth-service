package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.domain.RoleMenuButton;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.persistence.RoleMenuButtonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 角色按钮 Service
 */
@Service
public class RoleMenuButtonService extends BaseService<RoleMenuButtonMapper, RoleMenuButton> {

    private final RoleMenuButtonMapper roleMenuButtonMapper;

    private final MenuService menuService;

    public RoleMenuButtonService(RoleMenuButtonMapper roleMenuButtonMapper, MenuService menuService) {
        this.roleMenuButtonMapper = roleMenuButtonMapper;
        this.menuService = menuService;
    }

    /**
     * 保存角色菜单按钮
     *
     * @param roleMenuButton
     * @return
     */
    @Transactional
    public RoleMenuButton createRoleMenuButton(RoleMenuButton roleMenuButton) {
        //校验
        if (roleMenuButton == null || roleMenuButton.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        //检查角色菜单按钮组合
        Integer count = getRoleButtonCountByButtonIdAndRoleId(roleMenuButton.getButtonId(), roleMenuButton.getRoleId());
        if (count != null && count > 1) {
            throw new BizException(RespCode.AUTH_ROLE_MENU_BUTTON_EXISTS);
        }
        roleMenuButtonMapper.insert(roleMenuButton);
        return roleMenuButton;
    }

    /**
     * 批量保存角色菜单按钮
     * @param menuButtonList
     * @return
     */
    @Transactional
    public void batchSaveRoleMenuButton(List<RoleMenuButton> menuButtonList) {
        //校验
        if (menuButtonList == null || menuButtonList.size() == 0) {
            return ;
        }else{
            menuButtonList.stream().forEach(m -> {
                //检查角色菜单按钮组合
                Integer count = getRoleButtonCountByButtonIdAndRoleId(m.getButtonId(), m.getRoleId());
                //不存在，则插入
                if (count == null || count == 0) {
                    roleMenuButtonMapper.insert(m);
                }
            });
        }
        return ;
    }
    /**
     * 更新角色菜单按钮
     *
     * @param roleMenuButton
     * @return
     */
    @Transactional
    public RoleMenuButton updateRoleMenuButton(RoleMenuButton roleMenuButton) {
        //校验
        if (roleMenuButton == null || roleMenuButton.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        //校验ID是否在数据库中存在
        RoleMenuButton roleMenu1 = roleMenuButtonMapper.selectById(roleMenuButton.getId());
        if (roleMenu1 == null) {
            throw new BizException(RespCode.SYS_DB_NOT_EXISTS);
        }
        if (roleMenuButton.getEnabled() == null || "".equals(roleMenuButton.getEnabled())) {
            roleMenuButton.setEnabled(roleMenu1.getEnabled());
        }
        roleMenuButton.setCreatedBy(roleMenu1.getCreatedBy());
        roleMenuButton.setCreatedDate(roleMenu1.getCreatedDate());
        this.updateById(roleMenuButton);
        return roleMenuButton;
    }

    /**
     * 检查角色和菜单按钮的组合是否已经存在
     *
     * @param buttonId
     * @param roleId
     * @return
     */
    public Integer getRoleButtonCountByButtonIdAndRoleId(Long buttonId, Long roleId) {
        return roleMenuButtonMapper.selectCount(new EntityWrapper<RoleMenuButton>()
                .eq("button_id", buttonId)
                .eq("role_id", roleId));
    }

    /**
     * @param id 删除角色菜单按钮（物理删除）
     * @return
     */
    @Transactional
    public void deleteRoleMenuButton(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
        /*RoleMenuButton roleMenuButton = roleMenuButtonMapper.selectById(id);
        roleMenuButton.setDeleted(true);
        roleMenuButtonMapper.updateById(roleMenuButton);*/
    }

    /**
     * @param ids 批量删除角色菜单按钮（物理删除）
     * @return
     */
    @Transactional
    public void deleteBatchRoleMenuButton(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
            /*List<RoleMenuButton> result = null;
            List<RoleMenuButton> list = roleMenuButtonMapper.selectBatchIds(ids);
            if (list != null && list.size() > 0) {
                result = list.stream().map(menu -> {
                    menu.setDeleted(true);
                    return menu;
                }).collect(Collectors.toList());
            }
            if(result != null){
                this.updateBatchById(result);
            }*/
        }
    }


    /**
     * 根据角色Id，获取分配的所有菜单按钮
     *
     * @param roleId    角色ID
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @param page
     * @return
     */
    public List<RoleMenuButton> getRoleMenuButtonByRoleId(Long roleId, Boolean enabled, Page page) {
        List<RoleMenuButton> list = roleMenuButtonMapper.selectPage(page, new EntityWrapper<RoleMenuButton>()
                .eq(enabled != null, "enabled", enabled)
                .eq("role_id", roleId)
                .orderBy("last_updated_date"));
        /*if (isDeleted == null) {
            list = roleMenuButtonMapper.selectPage(page, new EntityWrapper<RoleMenuButton>()
                    .eq("deleted", false)
                    .eq(enabled != null, "enabled", enabled)
                    .eq("role_id", roleId));
        } else {
            list = roleMenuButtonMapper.selectPage(page, new EntityWrapper<RoleMenuButton>()
                    .eq("deleted", isDeleted)
                    .eq(enabled != null, "enabled", enabled)
                    .eq("role_id", roleId));
        }*/
        return list;
    }

    /**
     * 根据ID，获取对应的角色菜单按钮信息
     *
     * @param id
     * @return
     */
    public RoleMenuButton getRoleMenuButtonById(Long id) {
        return roleMenuButtonMapper.selectById(id);
    }

    /**
     * 根据角色ID，按钮ID集合，删除角色与按钮ID集合的关联 物理删除
     * @param roleId
     * @param buttonIds
     */
    public void deleteRoleMenuButtonByRoleIdAndButtonIds(Long roleId,List<Long> buttonIds){
        roleMenuButtonMapper.deleteRoleMenuButtonByRoleIdAndButtonIds(roleId,buttonIds);
    }

    /**
     * 根据角色ID，菜单ID集合，删除角色与按钮ID集合的关联 物理删除
     * @param roleId
     * @param menuIds
     */
    public void deleteRoleMenuButtonByRoleIdAndMenuIds(Long roleId,List<Long> menuIds){
        roleMenuButtonMapper.deleteRoleMenuButtonByRoleIdAndMenuIds(roleId,menuIds);
    }

    /**
     * 根据角色ID，返回已分配的菜单按钮ID的集合（只取功能，不取目录）
     * @param roleId
     * @return
     */
    public List<String> getMenuButtonIdsByRoleId(Long roleId) {
        return roleMenuButtonMapper.getMenuButtonIdsByRoleId(roleId);
    }
}
