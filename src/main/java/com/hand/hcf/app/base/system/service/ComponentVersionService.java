package com.hand.hcf.app.base.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.ComponentVersion;
import com.hand.hcf.app.base.system.persistence.ComponentVersionMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 组件版本Service
 */
@Service
public class ComponentVersionService extends BaseService<ComponentVersionMapper, ComponentVersion> {

    private final ComponentVersionMapper componentVersionMapper;

    public ComponentVersionService(ComponentVersionMapper componentVersionMapper) {
        this.componentVersionMapper = componentVersionMapper;
    }

    /**
     * 创建组件版本
     *
     * @param componentVersion
     * @return
     */
    @Transactional
    public ComponentVersion createComponentVersion(ComponentVersion componentVersion) {
        //校验
        if (componentVersion == null || componentVersion.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        componentVersionMapper.insert(componentVersion);
        return componentVersion;
    }

    /**
     * 更新模块
     *
     * @param componentVersion
     * @return
     */
    @Transactional
    public ComponentVersion updateComponentVersion(ComponentVersion componentVersion) {
        //校验
        if (componentVersion == null || componentVersion.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        //校验ID是否在数据库中存在
        ComponentVersion rr = componentVersionMapper.selectById(componentVersion.getId());
        if (rr == null) {
            throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
        }
        if (componentVersion.getEnabled() == null ) {
            componentVersion.setEnabled(rr.getEnabled());
        }
        if (componentVersion.getDeleted() == null ) {
            componentVersion.setDeleted(rr.getDeleted());
        }
        componentVersion.setCreatedBy(rr.getCreatedBy());
        componentVersion.setCreatedDate(rr.getCreatedDate());
        this.updateById(componentVersion);
        return componentVersion;
    }

    /**
     * @param id 删除组件版本（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteComponentVersion(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除组件版本（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchComponentVersion(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 根据组件ID，获取其所有组件版本 分页
     *
     * @param page
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<ComponentVersion> getComponentVersionsByComponentId(Long componentId, Boolean enabled, Page page) {
        return componentVersionMapper.selectPage(page, new EntityWrapper<ComponentVersion>()
                .eq(enabled != null, "enabled", enabled)
                .eq("component_id", componentId)
                .orderBy("id"));
    }

    /**
     * 根据ID，获取对应的组件信息
     *
     * @param id
     * @return
     */
    public ComponentVersion getComponentVersionById(Long id) {
        return componentVersionMapper.selectById(id);
    }

    //通过菜单id 获取组件最后一个版本的contents
    public  ComponentVersion getLatestComponentVersionByMenuId(Long menuId){
        return componentVersionMapper.getLatestComponentVersionByMenuId(menuId);
    }

}
