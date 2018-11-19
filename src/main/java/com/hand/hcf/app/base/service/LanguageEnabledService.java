package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.domain.LanguageEnabled;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.persistence.LanguageEnabledMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/21.
 * 语言启用Service
 */
@Service
public class LanguageEnabledService extends BaseService<LanguageEnabledMapper, LanguageEnabled> {

    private final LanguageEnabledMapper languageEnabledMapper;

    public LanguageEnabledService(LanguageEnabledMapper roleMapper) {
        this.languageEnabledMapper = roleMapper;
    }

    /**
     * 启用语言
     * @param languageEnabled
     * @return
     */
    @Transactional
    public LanguageEnabled createLanguageEnabled(LanguageEnabled languageEnabled) {
        //校验
        if (languageEnabled == null || languageEnabled.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (languageEnabled.getLanguage()== null || "".equals(languageEnabled.getLanguage())) {
            throw new BizException(RespCode.LANGUAGE_CODE_NULL);
        }
        //检查是否已经存在启用
        Integer count = getLanguageCountByCode(languageEnabled.getLanguage());
        if (count != null && count > 0) {
            //如果已经启用，则直接返回
            return languageEnabled;
        }
        languageEnabledMapper.insert(languageEnabled);
        return languageEnabled;
    }
    /**
     * 检查是否已经启用
     * @param language
     * @return
     */
    public Integer getLanguageCountByCode(String language) {
        return languageEnabledMapper.selectCount(new EntityWrapper<LanguageEnabled>()
                .eq("language", language));
    }

    /**
     * 当所有启用的语言 分页
     * @param page
     * @return
     */
    public List<LanguageEnabled> getAllLanguageEnabled(Page page) {
        return languageEnabledMapper.selectPage(page, new EntityWrapper<LanguageEnabled>().orderBy("id"));
    }
    /**
     * 根据ID，获取对应的启用语言信息
     * @param id
     * @return
     */
    public LanguageEnabled getLanguageEnabledById(Long id) {
        return languageEnabledMapper.selectById(id);
    }
}
