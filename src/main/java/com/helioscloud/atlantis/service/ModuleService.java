package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Module;
import com.helioscloud.atlantis.persistence.ModuleMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 模块Service
 */
@Service
public class ModuleService extends BaseService<ModuleMapper, Module> {

    private final ModuleMapper moduleMapper;

    public ModuleService(ModuleMapper moduleMapper) {
        this.moduleMapper = moduleMapper;
    }

    /**
     * 创建模块
     *
     * @param module
     * @return
     */
    @Transactional
    public Module createModule(Module module) {
        //校验
        if (module == null || module.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (module.getModuleCode() == null || "".equals(module.getModuleCode())) {
            throw new BizException(RespCode.MODULE_CODE_NULL);
        }
        if (module.getModuleName() == null || "".equals(module.getModuleName())) {
            throw new BizException(RespCode.MODULE_NAME_NULL);
        }
        //检查模块代码是否唯一
        Integer count = getModuleCountByModuleCode(module.getModuleCode());
        if (count != null && count > 0) {
            throw new BizException(RespCode.MODULE_CODE_NOT_UNION);
        }
        moduleMapper.insert(module);
        return module;
    }

    /**
     * 更新模块
     *
     * @param module
     * @return
     */
    @Transactional
    public Module updateModule(Module module) {
        //校验
        if (module == null || module.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        if (module.getModuleName() == null || "".equals(module.getModuleName())) {
            throw new BizException(RespCode.ROLE_NAME_NULL);
        }
        //校验ID是否在数据库中存在
        Module rr = moduleMapper.selectById(module.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (module.getIsEnabled() == null || "".equals(module.getIsEnabled())) {
            module.setIsEnabled(rr.getIsEnabled());
        }
        if (module.getIsDeleted() == null || "".equals(module.getIsDeleted())) {
            module.setIsDeleted(rr.getIsDeleted());
        }
        module.setCreatedBy(rr.getCreatedBy());
        module.setCreatedDate(rr.getCreatedDate());
        module.setModuleCode(rr.getModuleCode());
        this.updateById(module);
        return module;
    }

    /**
     * 检查是否存在相同的模块代码
     *
     * @param moduleCode
     * @return
     */
    public Integer getModuleCountByModuleCode(String moduleCode) {
        return moduleMapper.selectCount(new EntityWrapper<Module>()
                .eq("module_code", moduleCode));
    }

    /**
     * @param id 删除模块（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteModule(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除模块（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchModule(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 所有模块 分页
     *
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Module> getModules(Boolean isEnabled, Page page) {
        return moduleMapper.selectPage(page, new EntityWrapper<Module>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .orderBy("module_code"));
    }

    /**
     * 根据ID，获取对应的模块信息
     *
     * @param id
     * @return
     */
    public Module getModuleById(Long id) {
        return moduleMapper.selectById(id);
    }
}
