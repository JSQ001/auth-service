package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Interface;
import com.helioscloud.atlantis.persistence.InterfaceMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 接口Service
 */
@Service
public class InterfaceService extends BaseService<InterfaceMapper, Interface> {

    private final InterfaceMapper interfaceMapper;

    public InterfaceService(InterfaceMapper moduleMapper) {
        this.interfaceMapper = moduleMapper;
    }

    /**
     * 创建接口
     *
     * @param anInterface
     * @return
     */
    @Transactional
    public Interface createInterface(Interface anInterface) {
        //校验
        if (anInterface == null || anInterface.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        interfaceMapper.insert(anInterface);
        return anInterface;
    }

    /**
     * 更新接口
     *
     * @param anInterface
     * @return
     */
    @Transactional
    public Interface updateInterface(Interface anInterface) {
        //校验
        if (anInterface == null || anInterface.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        //校验ID是否在数据库中存在
        Interface rr = interfaceMapper.selectById(anInterface.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (anInterface.getIsEnabled() == null || "".equals(anInterface.getIsEnabled())) {
            anInterface.setIsEnabled(rr.getIsEnabled());
        }
        if (anInterface.getIsDeleted() == null || "".equals(anInterface.getIsDeleted())) {
            anInterface.setIsDeleted(rr.getIsDeleted());
        }
        anInterface.setCreatedBy(rr.getCreatedBy());
        anInterface.setCreatedDate(rr.getCreatedDate());
        this.updateById(anInterface);
        return anInterface;
    }

    /**
     * @param id 删除接口（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteInterface(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除接口（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchInterface(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }

    /**
     * 根据模块Id,取所有接口 分页
     *
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Interface> getInterfacesByModuleId(Long moduleId, Boolean isEnabled, Page page) {
        return interfaceMapper.selectPage(page, new EntityWrapper<Interface>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("module_id", moduleId)
                .orderBy("id"));
    }

    /**
     * 根据ID，获取对应的接口信息
     *
     * @param id
     * @return
     */
    public Interface getInterfaceById(Long id) {
        return interfaceMapper.selectById(id);
    }
}
