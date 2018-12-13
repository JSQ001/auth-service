package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.domain.Language;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.persistence.LanguageMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/13.
 * 语言Service
 */
@Service
public class LanguageService extends BaseService<LanguageMapper, Language> {

    private final LanguageMapper languageMapper;

    public LanguageService(LanguageMapper languageMapper) {
        this.languageMapper = languageMapper;
    }

    /**
     * 创建语言
     * @param language
     * @return
     */
    @Transactional
    public Language createLanguage(Language language) {
        //校验
        if (language == null || language.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (language.getLanguage() == null || "".equals(language.getLanguage())) {
            throw new BizException(RespCode.AUTH_LANGUAGE_CODE_NULL);
        }
        if (language.getLanguageName() == null || "".equals(language.getLanguageName())) {
            throw new BizException(RespCode.AUTH_LANGUAGE_NAME_NULL);
        }
        //检查是否已经存在该语言代码
        Integer count = getLanguageByCode(language.getLanguage());
        if (count != null && count > 0) {
            throw new BizException(RespCode.AUTH_LANGUAGE_CODE_NOT_UNION);
        }
        languageMapper.insert(language);
        return language;
    }
    /**
     * 更新语言
     *
     * @param language
     * @return
     */
    @Transactional
    public Language updateLanguage(Language language) {
        //校验
        if (language == null || language.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        if (language.getLanguage() == null || "".equals(language.getLanguage())) {
            throw new BizException(RespCode.AUTH_LANGUAGE_CODE_NULL);
        }
        if (language.getLanguageName() == null || "".equals(language.getLanguageName())) {
            throw new BizException(RespCode.AUTH_LANGUAGE_NAME_NULL);
        }
        //校验ID是否在数据库中存在
        Language rr = languageMapper.selectById(language.getId());
        if (rr == null) {
            throw new BizException(RespCode.SYS_DB_NOT_EXISTS);
        }
        this.updateById(language);
        return language;
    }

    /**
     * 检查是否存在相同的语言代码
     *
     * @param language
     * @return
     */
    public Integer getLanguageByCode(String language) {
        return languageMapper.selectCount(new EntityWrapper<Language>()
                .eq("language", language));
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
     * 所有语言 分页
     * @param page
     * @return
     */
    public List<Language> getLanguages(Page page) {
        return languageMapper.selectPage(page, new EntityWrapper<Language>().orderBy("id"));
    }
    /**
     * 根据ID，获取对应的语言信息
     *
     * @param id
     * @return
     */
    public Language getLanguageById(Long id) {
        return languageMapper.selectById(id);
    }
}
