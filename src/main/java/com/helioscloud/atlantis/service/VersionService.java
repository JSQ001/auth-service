package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Version;
import com.helioscloud.atlantis.persistence.VersionMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 版本Service
 */
@Service
public class VersionService extends BaseService<VersionMapper, Version> {

    private final VersionMapper versionMapper;

    public VersionService(VersionMapper versionMapper) {
        this.versionMapper = versionMapper;
    }

    /**
     * 创建版本
     *
     * @param version
     * @return
     */
    @Transactional
    public Version createVersion(Version version) {
        //校验
        if (version == null || version.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        versionMapper.insert(version);
        return version;
    }

    /**
     * 更新模块
     *
     * @param version
     * @return
     */
    @Transactional
    public Version updateVersion(Version version) {
        //校验
        if (version == null || version.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        //校验ID是否在数据库中存在
        Version rr = versionMapper.selectById(version.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (version.getIsEnabled() == null || "".equals(version.getIsEnabled())) {
            version.setIsEnabled(rr.getIsEnabled());
        }
        if (version.getIsDeleted() == null || "".equals(version.getIsDeleted())) {
            version.setIsDeleted(rr.getIsDeleted());
        }
        version.setCreatedBy(rr.getCreatedBy());
        version.setCreatedDate(rr.getCreatedDate());
        this.updateById(version);
        return version;
    }

    /**
     * @param id 删除版本（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteVersion(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除版本（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchVersion(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 根据组件ID，获取其所有版本 分页
     *
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Version> getVersionsByComponentId(Long componentId, Boolean isEnabled, Page page) {
        return versionMapper.selectPage(page, new EntityWrapper<Version>()
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
    public Version getVersionById(Long id) {
        return versionMapper.selectById(id);
    }
}
