package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Component;
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

    public ComponentService(ComponentMapper componentMapper) {
        this.componentMapper = componentMapper;
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
        componentMapper.insert(component);
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
        component.setCreatedBy(rr.getCreatedBy());
        component.setCreatedDate(rr.getCreatedDate());
        this.updateById(component);
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
     * 模块下所有组件 分页
     *
     * @param moduleId
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Component> getComponentsByModuleId(Long moduleId, Boolean isEnabled, Page page) {
        return componentMapper.selectPage(page, new EntityWrapper<Component>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("module_id", moduleId)
                .orderBy("id"));
    }

    /**
     * 根据ID，获取对应的组件信息
     *
     * @param id
     * @return
     */
    public Component getComponentById(Long id) {
        return componentMapper.selectById(id);
    }

    /**
     * 根据MenuID，获取对应的组件信息
     * @param menuId
     * @return
     */
    public Component getComponentByMenuId(Long menuId) {
        List<Component> list = componentMapper.selectList(new EntityWrapper<Component>().eq("menu_id",menuId));
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
}
