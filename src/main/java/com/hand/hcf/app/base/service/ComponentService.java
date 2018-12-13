package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.domain.Component;
import com.hand.hcf.app.base.domain.ComponentButton;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.persistence.ComponentMapper;
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

    private final RoleMenuService roleMenuService;

    private final ComponentButtonService componentButtonService;

    public ComponentService(ComponentMapper componentMapper, MenuButtonService menuButtonService, RoleMenuService roleMenuService, ComponentButtonService componentButtonService) {
        this.componentMapper = componentMapper;
        this.menuButtonService = menuButtonService;
        this.roleMenuService = roleMenuService;
        this.componentButtonService = componentButtonService;
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
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (component.getComponentName() == null || "".equals(component.getComponentName())) {
            throw new BizException(RespCode.AUTH_COMPONENT_NAME_NULL);
        }
        if (component.getComponentType() == null) {
            component.setComponentType("2");//默认为界面
        }
        if (!"1".equals(component.getComponentType()) && !"2".equals(component.getComponentType())) {
            throw new BizException(RespCode.AUTH_COMPONENT_TYPE_INVALID);
        }
        //用于保存菜单的按钮
        /*if (component.getButtonList() != null && component.getButtonList().size() > 0 && component.getMenuId() != null && component.getMenuId() > 0) {
            resultButtons = component.getButtonList();
            resultButtons.forEach(e -> {
                e.setMenuId(component.getMenuId());
            });
            Long menuId = component.getMenuId();
            Component cc = this.getComponentByMenuId(menuId);
            if(cc != null){
                this.deleteComponent(cc.getId());
                //删除按钮与角色的关联
                List<RoleMenu> roleMenus = roleMenuService.getRoleMenuListByMenuId(menuId);
                if(roleMenus != null && roleMenus.size() > 0){
                    List<Long> roleMenusId = roleMenus.stream().map(d -> {return d.getId();}).collect(Collectors.toList());
                    roleMenuService.deleteBatchRoleMenu(roleMenusId);
                }
                //删除按钮与菜单的关联
                List<MenuButton>  menuButtons = menuButtonService.getMenuButtonsByMenuId(menuId);
                if(menuButtons != null && menuButtons.size() > 0){
                    List<Long> menuButtonIds = menuButtons.stream().map(d -> {return d.getId();}).collect(Collectors.toList());
                    menuButtonService.deleteBatchMenuButton(menuButtonIds);
                }
            }
            resultButtons = menuButtonService.batchSaveAndUpdateMenuButton(component.getButtonList());
        }*/
        componentMapper.insert(component);
        //保存组件与按钮
        List<ComponentButton> buttonList = component.getButtonList();
        if (buttonList != null && buttonList.size() > 0) {
            buttonList.stream().forEach(b -> {
                b.setComponentId(component.getId());
            });
            componentButtonService.insertBatch(buttonList, 10);
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
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        if (component.getComponentName() == null || "".equals(component.getComponentName())) {
            throw new BizException(RespCode.AUTH_COMPONENT_NAME_NULL);
        }
        if (component.getComponentType() == null) {
            component.setComponentType("2");//默认为界面
        }
        // 1为组件，2为界面
        if (!"1".equals(component.getComponentType()) && !"2".equals(component.getComponentType())) {
            throw new BizException(RespCode.AUTH_COMPONENT_TYPE_INVALID);
        }
        //校验ID是否在数据库中存在
        Component rr = componentMapper.selectById(component.getId());
        if (rr == null) {
            throw new BizException(RespCode.SYS_DB_NOT_EXISTS);
        }
        if (component.getEnabled() == null || "".equals(component.getEnabled())) {
            component.setEnabled(rr.getEnabled());
        }
       /* if (component.getDeleted() == null || "".equals(component.getDeleted())) {
            component.setDeleted(rr.getDeleted());
        }*/
        List<ComponentButton> resultButtons = null;

        //用于保存菜单的按钮
        if (component.getButtonList() != null && component.getButtonList().size() > 0) {
            resultButtons = component.getButtonList();
            resultButtons.forEach(e -> {
                e.setComponentId(component.getId());
            });
            resultButtons = componentButtonService.batchSaveAndUpdateMenuButton(resultButtons);
        }
        if (resultButtons != null) {
            component.setButtonList(resultButtons);
        } else {
            //根据组件ID，取组件对应的所有按钮
            List<ComponentButton> buttonList = componentButtonService.getComponentButtonsByComponentId(component.getId());
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
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Component> getComponentsByEnabled(Boolean enabled, Page page) {
        return componentMapper.selectPage(page, new EntityWrapper<Component>()
                .eq(enabled != null, "enabled", enabled)
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
            List<ComponentButton> buttonList = componentButtonService.getComponentButtonsByComponentId(id);
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
            List<ComponentButton> buttonList = componentButtonService.getComponentButtonsByComponentId(component.getId());
            component.setButtonList(buttonList);
            return component;
        }
        return null;
    }
}
