package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.Language;
import com.helioscloud.atlantis.persistence.LanguageMapper;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 多语言Service
 */
@Service
public class LanguageService extends BaseService<LanguageMapper, Language> {

    private final LanguageMapper languageMapper;

    public LanguageService(LanguageMapper moduleMapper) {
        this.languageMapper = moduleMapper;
    }

    /**
     * 创建语言
     *
     * @param language
     * @return
     */
    @Transactional
    public Language createLanguage(Language language) {
        //校验
        if (language == null || language.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (language.getCode() == null || "".equals(language.getCode())) {
            throw new BizException(RespCode.MODULE_CODE_NULL);
        }
        if (language.getModuleId() == null || "".equals(language.getModuleId())) {
            throw new BizException(RespCode.MODULE_ID_NULL);
        }
        //检查语言代码是否唯一
        Integer count = getLanguageCountByModuleIdAndCode(language.getModuleId(), language.getCode());
        if (count != null && count > 0) {
            throw new BizException(RespCode.LANGUAGE_CODE_NOT_UNION);
        }
        languageMapper.insert(language);
        return language;
    }

    /**
     * 更新语言（代码不允许修改）
     *
     * @param language
     * @return
     */
    @Transactional
    public Language updateLanguage(Language language) {
        //校验
        if (language == null || language.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        if (language.getCode() == null || "".equals(language.getCode())) {
            throw new BizException(RespCode.MODULE_CODE_NULL);
        }
        if (language.getModuleId() == null || "".equals(language.getModuleId())) {
            throw new BizException(RespCode.MODULE_ID_NULL);
        }
        //校验ID是否在数据库中存在
        Language rr = languageMapper.selectById(language.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (language.getIsEnabled() == null || "".equals(language.getIsEnabled())) {
            language.setIsEnabled(rr.getIsEnabled());
        }
        if (language.getIsDeleted() == null || "".equals(language.getIsDeleted())) {
            language.setIsDeleted(rr.getIsDeleted());
        }
        if (language.getModuleId() == null || "".equals(language.getModuleId())) {
            language.setModuleId(rr.getModuleId());
        }
        language.setCreatedBy(rr.getCreatedBy());
        language.setCreatedDate(rr.getCreatedDate());
        language.setCode(rr.getCode());
        this.updateById(language);
        return language;
    }

    /**
     * 检查是否存在相同的语言代码
     *
     * @param code
     * @return
     */
    public Integer getLanguageCountByModuleIdAndCode(Long moduleId, String code) {
        return languageMapper.selectCount(new EntityWrapper<Language>()
                .eq("module_id", moduleId)
                .eq("code", code));
    }

    /**
     * @param id 删除语言（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteLanguage(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param ids 批量删除语言（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchLanguage(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
        }
    }


    /**
     * 根据模块，取所有多语言 分页
     *
     * @param moduleId  模块Id
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Language> getLanguagesByModuleId(Long moduleId, Boolean isEnabled, Page page) {
        return languageMapper.selectPage(page, new EntityWrapper<Language>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .eq("module_id", moduleId)
                .orderBy("code"));
    }

    /**
     * 取所有多语言 分页
     *
     * @param page
     * @param isEnabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<Language> getLanguages(Boolean isEnabled, Page page) {
        return languageMapper.selectPage(page, new EntityWrapper<Language>()
                .eq(isEnabled != null, "is_enabled", isEnabled)
                .orderBy("code"));
    }

    /**
     * 根据ID，获取对应的多语言信息
     *
     * @param id
     * @return
     */
    public Language getLanguageById(Long id) {
        return languageMapper.selectById(id);
    }
}
