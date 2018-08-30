package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.Component;
import com.helioscloud.atlantis.domain.MenuButton;
import com.helioscloud.atlantis.persistence.ComponentMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 组件Service
 */
@Service
public class ComponentService extends BaseService<ComponentMapper, Component> {

    private final ComponentMapper componentMapper;

    private final MenuButtonService menuButtonService;

    public ComponentService(ComponentMapper componentMapper, MenuButtonService menuButtonService) {
        this.componentMapper = componentMapper;
        this.menuButtonService = menuButtonService;
    }

    /**
     * 创建组件
     *
     * @param component
     * @return
     */
    @Transactional
    public Component createComponent(Component component) {
        //校验
        if (component == null || component.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (component.getComponentName() == null || "".equals(component.getComponentName())) {
            throw new BizException(RespCode.COMPONENT_NAME_NULL);
        }
        if (component.getComponentType() == null) {
            component.setComponentType("2");//默认为界面
        }
        if (!"1".equals(component.getComponentType()) && !"2".equals(component.getComponentType())) {
            throw new BizException(RespCode.COMPONENT_TYPE_INVALID);
        }
        List<MenuButton> resultButtons = null;
        //用于保存菜单的按钮
        if (component.getButtonList() != null && component.getButtonList().size() > 0 && component.getMenuId() != null && component.getMenuId() > 0) {
            resultButtons = component.getButtonList();
            resultButtons.forEach(e -> {
                e.setMenuId(component.getMenuId());
            });
            resultButtons = menuButtonService.batchSaveAndUpdateMenuButton(component.getButtonList());
        }
        componentMapper.insert(component);
        if (resultButtons != null) {
            component.setButtonList(resultButtons);
        }
        return component;
    }

    /**
     * 更新组件
     *
     * @param component
     * @return
     */
    @Transactional
    public Component updateComponent(Component component) {
        //校验
        if (component == null || component.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        if (component.getComponentName() == null || "".equals(component.getComponentName())) {
            throw new BizException(RespCode.COMPONENT_NAME_NULL);
        }
        if (component.getComponentType() == null) {
            component.setComponentType("2");//默认为界面
        }
        // 1为组件，2为界面
        if (!"1".equals(component.getComponentType()) && !"2".equals(component.getComponentType())) {
            throw new BizException(RespCode.COMPONENT_TYPE_INVALID);
        }
        //校验ID是否在数据库中存在
        Component rr = componentMapper.selectById(component.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (component.getIsEnabled() == null || "".equals(component.getIsEnabled())) {
            component.setIsEnabled(rr.getIsEnabled());
        }
        if (component.getIsDeleted() == null || "".equals(component.getIsDeleted())) {
            component.setIsDeleted(rr.getIsDeleted());
        }
        List<MenuButton> resultButtons = null;

        //用于保存菜单的按钮
        if (component.getButtonList() != null && component.getButtonList().size() > 0 && component.getMenuId() != null && component.getMenuId() > 0) {
            resultButtons = component.getButtonList();
            resultButtons.forEach(e -> {
                e.setMenuId(component.getMenuId());
            });
            resultButtons = menuButtonService.batchSaveAndUpdateMenuButton(component.getButtonList());
        }
        if (resultButtons != null) {
            component.setButtonList(resultButtons);
        } else {
            //根据菜单ID，取菜果对应的所有按钮
            List<MenuButton> buttonList = menuButtonService.getMenuButtons(rr.getMenuId(), null, PageUtil.getPage(0, 20));
            component.setButtonList(buttonList);
        }
        component.setCreatedBy(rr.getCreatedBy());
        component.setCreatedDate(rr.getCreatedDate());
        this.updateById(component);
        component.setVersionNumber(component.getVersionNumber() + 1);
        return component;
    }

    /**
     * @param id 删除组件（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteComponent(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除组件（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchComponent(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 所有组件 分页
     *
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Component> getComponentsByIsEnabled(Boolean isEnabled, Page page) {
        return componentMapper.selectPage(page, new EntityWrapper<Component>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .orderBy("id"));
    }

    /**
     * 根据ID，获取对应的组件信息
     *
     * @param id
     * @return
     */
    public Component getComponentById(Long id) {
        Component component = componentMapper.selectById(id);
        if (component != null && component.getMenuId() != null && component.getMenuId() > 0) {
            //根据菜单ID，取菜果对应的所有按钮
            List<MenuButton> buttonList = menuButtonService.getMenuButtons(component.getMenuId(), null, PageUtil.getPage(0, 20));
            component.setButtonList(buttonList);
        }
        return component;
    }

    /**
     * 根据MenuID，获取对应的组件信息
     *
     * @param menuId
     * @return
     */
    public Component getComponentByMenuId(Long menuId) {
        List<Component> list = componentMapper.selectList(new EntityWrapper<Component>().eq("menu_id", menuId));
        Component component = null;
        if (list != null && list.size() > 0) {
            component = list.get(0);
            //根据菜单ID，取菜果对应的所有按钮
            if(component.getMenuId()!= null && component.getMenuId() > 0){
                List<MenuButton> buttonList = menuButtonService.getMenuButtons(component.getMenuId(), null, PageUtil.getPage(0, 20));
                component.setButtonList(buttonList);
            }
            return component;
        }
        return null;
    }
}
