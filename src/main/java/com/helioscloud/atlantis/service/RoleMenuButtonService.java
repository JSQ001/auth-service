package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.RoleMenuButton;
import com.helioscloud.atlantis.persistence.RoleMenuButtonMapper;
import com.helioscloud.atlantis.util.RespCode;
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
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        //检查角色菜单按钮组合
        Integer count = getRoleButtonCountByButtonIdAndRoleId(roleMenuButton.getButtonId(), roleMenuButton.getRoleId());
        if (count != null && count > 1) {
            throw new BizException(RespCode.ROLE_MENU_BUTTON_EXISTS);
        }
        roleMenuButtonMapper.insert(roleMenuButton);
        return roleMenuButton;
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
            throw new BizException(RespCode.ID_NULL);
        }
        //校验ID是否在数据库中存在
        RoleMenuButton roleMenu1 = roleMenuButtonMapper.selectById(roleMenuButton.getId());
        if (roleMenu1 == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if(roleMenuButton.getIsEnabled() == null){
            roleMenuButton.setIsEnabled(roleMenu1.getIsEnabled());
        }
        if(roleMenuButton.getIsDeleted() == null){
            roleMenuButton.setIsDeleted(roleMenu1.getIsDeleted());
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
     * @param id 删除角色菜单按钮（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteRoleMenuButton(Long id) {
        if(id != null){
            this.deleteById(id);
        }
        /*RoleMenuButton roleMenuButton = roleMenuButtonMapper.selectById(id);
        roleMenuButton.setIsDeleted(true);
        roleMenuButtonMapper.updateById(roleMenuButton);*/
    }

    /**
     * @param ids 批量删除角色菜单按钮（逻辑删除）
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
                    menu.setIsDeleted(true);
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
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @param page
     * @return
     */
    public List<RoleMenuButton> getRoleMenuButtonByRoleId(Long roleId,Boolean isEnabled, Page page) {
        List<RoleMenuButton> list = roleMenuButtonMapper.selectPage(page, new EntityWrapper<RoleMenuButton>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("role_id", roleId)
                .orderBy("last_updated_date"));
        /*if (isDeleted == null) {
            list = roleMenuButtonMapper.selectPage(page, new EntityWrapper<RoleMenuButton>()
                    .eq("is_deleted", false)
                    .eq(isEnabled != null, "is_enabled", isEnabled)
                    .eq("role_id", roleId));
        } else {
            list = roleMenuButtonMapper.selectPage(page, new EntityWrapper<RoleMenuButton>()
                    .eq("is_deleted", isDeleted)
                    .eq(isEnabled != null, "is_enabled", isEnabled)
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

}
