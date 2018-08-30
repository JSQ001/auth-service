package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.MenuButton;
import com.helioscloud.atlantis.persistence.MenuButtonMapper;
import com.helioscloud.atlantis.util.RespCode;
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
     * @param menuButton
     * @return
     */
    @Transactional
    public MenuButton createMenuButton(MenuButton menuButton) {
        //校验
        if (menuButton == null || menuButton.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (menuButton.getButtonCode() == null || "".equals(menuButton.getButtonCode())) {
            throw new BizException(RespCode.BUTTON_CODE_NULL);
        }
        if (menuButton.getButtonName() == null || "".equals(menuButton.getButtonName())) {
            throw new BizException(RespCode.BUTTON_NAME_NULL);
        }
        //检查按钮代码在菜单里是否已经存在
        Integer count = getMenuButtonCountByButtonCode(menuButton.getMenuId(), menuButton.getButtonCode());
        if (count != null && count > 0) {
            throw new BizException(RespCode.BUTTON_CODE_NOT_UNION);
        }
        menuButtonMapper.insert(menuButton);
        return menuButton;
    }

    /**
     * 批量菜单按钮保存或更新
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
        if(toSaveList.size() > 0){
            //处理保存
            toSaveList.forEach(button -> {
                if(button.getId() != null && button.getId() > 0){
                    this.updateMenuButton(button);
                }else{
                    this.createMenuButton(button);
                }
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
            throw new BizException(RespCode.ID_NULL);
        }
        //校验ID是否在数据库中存在
        MenuButton mb = menuButtonMapper.selectById(menuButton.getId());
        if (mb == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (menuButton.getButtonName() == null || "".equals(menuButton.getButtonName())) {
            menuButton.setButtonName(mb.getButtonName());
        }
        if (menuButton.getButtonCode() == null || "".equals(menuButton.getButtonCode())) {
            menuButton.setButtonCode(mb.getButtonCode());
        }
        if (menuButton.getIsEnabled() == null || "".equals(menuButton.getIsEnabled())) {
            menuButton.setIsEnabled(mb.getIsEnabled());
        }
        if (menuButton.getIsDeleted() == null || "".equals(menuButton.getIsDeleted())) {
            menuButton.setIsDeleted(mb.getIsDeleted());
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
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<MenuButton> getMenuButtons(Long menuId, Boolean isEnabled, Page page) {
        return menuButtonMapper.selectPage(page, new EntityWrapper<MenuButton>()
                .eq(isEnabled != null && !"".equals(isEnabled), "is_enabled", isEnabled)
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

}
