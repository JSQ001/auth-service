package com.hand.hcf.app.base.userRole.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.userRole.domain.MenuButton;
import com.hand.hcf.app.base.userRole.persistence.MenuButtonMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 菜单按钮 Service
 */
@Service
public class MenuButtonService extends BaseService<MenuButtonMapper, MenuButton> {

    private final MenuButtonMapper menuButtonMapper;

    public MenuButtonService(MenuButtonMapper menuButtonMapper) {
        this.menuButtonMapper = menuButtonMapper;
    }

    /**
     * 创建菜单按钮
     *
     * @param menuButton
     * @return
     */
    @Transactional
    public MenuButton createMenuButton(MenuButton menuButton) {
        //校验
       /* if (menuButton == null || menuButton.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }*/
        if (menuButton.getButtonCode() == null || "".equals(menuButton.getButtonCode())) {
            throw new BizException(RespCode.BUTTON_CODE_NULL);
        }
        if (menuButton.getButtonName() == null || "".equals(menuButton.getButtonName())) {
            throw new BizException(RespCode.BUTTON_NAME_NULL);
        }
        //检查按钮代码在菜单里是否已经存在
        Integer count = getMenuButtonCountByButtonCode(menuButton.getMenuId(), menuButton.getButtonCode());
        if (count != null && count > 0) {
            menuButton = this.menuButtonMapper.selectOne(menuButton);
            //throw new BizException(RespCode.BUTTON_CODE_NOT_UNION);
        } else {
            menuButtonMapper.insert(menuButton);
        }
        return menuButton;
    }

    /**
     * 批量菜单按钮保存或更新
     *
     * @param buttonList
     * @return
     */
    @Transactional
    public List<MenuButton> batchSaveAndUpdateMenuButton(List<MenuButton> buttonList) {
        //校验
        if (buttonList == null || buttonList.size() == 0) {
            return null;
        }
        //需要保存和更新
        List<MenuButton> toSaveList = new ArrayList<>();
        //需要删除
        List<Long> toDeleteListIds = new ArrayList<>();

        //flag;创建:1001，删除:1002
        buttonList.stream().filter(b -> "1001".equals(b.getFlag())).forEach(button -> {
            toSaveList.add(button);
        });
        buttonList.stream().filter(b -> "1002".equals(b.getFlag())).forEach(button -> {
            toDeleteListIds.add(button.getId());
        });
        //批量删除
        this.deleteBatchMenuButton(toDeleteListIds);
        if (toSaveList.size() > 0) {
            //处理保存
            toSaveList.forEach(button -> {
                button = this.createMenuButton(button);
            });
        }
        return toSaveList;
    }

    /**
     * 更新按钮菜单
     *
     * @param menuButton
     * @return
     */
    @Transactional
    public MenuButton updateMenuButton(MenuButton menuButton) {
        //校验
        if (menuButton == null || menuButton.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        //校验ID是否在数据库中存在
        MenuButton mb = menuButtonMapper.selectById(menuButton.getId());
        if (mb == null) {
            throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
        }
        if (menuButton.getButtonName() == null || "".equals(menuButton.getButtonName())) {
            menuButton.setButtonName(mb.getButtonName());
        }
        if (menuButton.getButtonCode() == null || "".equals(menuButton.getButtonCode())) {
            menuButton.setButtonCode(mb.getButtonCode());
        }
        if (menuButton.getEnabled() == null || "".equals(menuButton.getEnabled())) {
            menuButton.setEnabled(mb.getEnabled());
        }
        if (menuButton.getDeleted() == null || "".equals(menuButton.getDeleted())) {
            menuButton.setDeleted(mb.getDeleted());
        }
        menuButton.setCreatedBy(mb.getCreatedBy());
        menuButton.setCreatedDate(mb.getCreatedDate());
        this.updateById(menuButton);
        return menuButton;
    }

    /**
     * 按钮代码，检查是否存在相同的按钮代码在同一个菜单里
     *
     * @param menuId
     * @param buttonCode
     * @return
     */
    public Integer getMenuButtonCountByButtonCode(Long menuId, String buttonCode) {
        return menuButtonMapper.selectCount(new EntityWrapper<MenuButton>()
                .eq("button_code", buttonCode)
                .eq("menu_id", menuId));
    }

    /**
     * @param id 删除菜单按钮（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteMenuButton(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param menuId 删除菜单按钮（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteMenuButtonByMenuId(Long menuId) {
        if (menuId != null) {
            menuButtonMapper.deleteMenuButtonByMenuId(menuId);
        }
    }

    /**
     * @param menuId 删除菜单按钮（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteMenuButtonByMenuIds(List<Long> menuId) {
        if (menuId != null) {
            menuButtonMapper.deleteMenuButtonByMenuIds(menuId);
        }
    }

    /**
     * @param ids 批量删除菜单按钮（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchMenuButton(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }

    /**
     * 根据菜单，取得所有的菜单按钮 分页
     *
     * @param menuId
     * @param page
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<MenuButton> getMenuButtons(Long menuId, Boolean enabled, Page page) {
        return menuButtonMapper.selectPage(page, new EntityWrapper<MenuButton>()
                .eq(enabled != null && !"".equals(enabled), "enabled", enabled)
                .eq("menu_id", menuId)
                .orderBy("button_code"));
    }

    /**
     * 根据ID，获取对应的菜单按钮
     *
     * @param id
     * @return
     */
    public MenuButton getMenuButtonById(Long id) {
        return menuButtonMapper.selectById(id);
    }

    /**
     * 根据菜单，取得所有的菜单按钮 不分页
     *
     * @param menuId
     * @return
     */
    public List<MenuButton> getMenuButtonsByMenuId(Long menuId) {
        return menuButtonMapper.selectList(new EntityWrapper<MenuButton>()
                .eq("menu_id", menuId));
    }

    /**
     * 根据菜单ID，角色ID集合，返回菜单在角色中分配的按钮 用于界面菜单的按钮显示控制
     *
     * @param menuId
     * @param userId 当前登录用户的ID
     * @return
     */
    public List<MenuButton> getMenuButtonsByMenuIdAndUserId(Long menuId, Long userId) {
        return menuButtonMapper.getMenuButtonsByMenuIdAndUserId(menuId, userId);
    }

}
