package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.ComponentVersion;
import com.helioscloud.atlantis.persistence.ComponentVersionMapper;
import com.helioscloud.atlantis.util.RespCode;
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
            throw new BizException(RespCode.ID_NOT_NULL);
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
            throw new BizException(RespCode.ID_NULL);
        }
        //校验ID是否在数据库中存在
        ComponentVersion rr = componentVersionMapper.selectById(componentVersion.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (componentVersion.getIsEnabled() == null || "".equals(componentVersion.getIsEnabled())) {
            componentVersion.setIsEnabled(rr.getIsEnabled());
        }
        if (componentVersion.getIsDeleted() == null || "".equals(componentVersion.getIsDeleted())) {
            componentVersion.setIsDeleted(rr.getIsDeleted());
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
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<ComponentVersion> getComponentVersionsByComponentId(Long componentId, Boolean isEnabled, Page page) {
        return componentVersionMapper.selectPage(page, new EntityWrapper<ComponentVersion>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
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
}
