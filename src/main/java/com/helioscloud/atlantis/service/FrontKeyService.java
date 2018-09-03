package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.domain.FrontKey;
import com.helioscloud.atlantis.dto.FrontKeyDTO;
import com.helioscloud.atlantis.persistence.FrontKeyMapper;
import com.helioscloud.atlantis.service.es.EsFrontKeyInfoSerivce;
import com.helioscloud.atlantis.util.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by houyin.zhang@hand-china.com on 2018/8/20.
 * 多前端TitleService
 */
@Service
public class FrontKeyService extends BaseService<FrontKeyMapper, FrontKey> {

    private final FrontKeyMapper frontKeyMapper;

    private final EsFrontKeyInfoSerivce esFrontKeyInfoSerivce;

    private final boolean esEnabledFlag;

    public FrontKeyService(FrontKeyMapper moduleMapper, EsFrontKeyInfoSerivce esFrontKeyInfoSerivce) {
        this.frontKeyMapper = moduleMapper;
        this.esFrontKeyInfoSerivce = esFrontKeyInfoSerivce;
        this.esEnabledFlag = esFrontKeyInfoSerivce.isElasticSearchEnable();
    }

    /**
     * 前端Title创建
     *
     * @param frontKey
     * @return
     */
    @Transactional
    public FrontKey createFrontKey(FrontKey frontKey) {
        //校验
        if (frontKey == null || frontKey.getId() != null) {
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        if (frontKey.getKeyCode() == null || "".equals(frontKey.getKeyCode())) {
            throw new BizException(RespCode.FRONT_KEY_NULL);
        }
        if (frontKey.getModuleId() == null || "".equals(frontKey.getModuleId())) {
            throw new BizException(RespCode.MODULE_ID_NULL);
        }
        //检查key是否唯一
        Integer count = getFrontKeyByKeyAndLang(frontKey.getKeyCode(), frontKey.getLang());
        if (count != null && count > 0) {
            throw new BizException(RespCode.FRONT_KEY_NOT_UNION);
        }
        frontKeyMapper.insert(frontKey);
        esFrontKeyInfoSerivce.saveEsFrontKeyIndex(frontKey);
        return frontKey;
    }

    /**
     * 更新前端Title（代码不允许修改）
     *
     * @param frontKey
     * @return
     */
    @Transactional
    public FrontKey updateFrontKey(FrontKey frontKey) {
        //校验
        if (frontKey == null || frontKey.getId() == null) {
            throw new BizException(RespCode.ID_NULL);
        }
        if (frontKey.getModuleId() == null || "".equals(frontKey.getModuleId())) {
            throw new BizException(RespCode.MODULE_ID_NULL);
        }
        //校验ID是否在数据库中存在
        FrontKey rr = frontKeyMapper.selectById(frontKey.getId());
        if (rr == null) {
            throw new BizException(RespCode.DB_NOT_EXISTS);
        }
        if (frontKey.getEnabled() == null || "".equals(frontKey.getEnabled())) {
            frontKey.setEnabled(rr.getEnabled());
        }
        if (frontKey.getDeleted() == null || "".equals(frontKey.getDeleted())) {
            frontKey.setDeleted(rr.getDeleted());
        }
        if (frontKey.getModuleId() == null || "".equals(frontKey.getModuleId())) {
            frontKey.setModuleId(rr.getModuleId());
        }
        frontKey.setCreatedBy(rr.getCreatedBy());
        frontKey.setCreatedDate(rr.getCreatedDate());
        frontKey.setKeyCode(rr.getKeyCode());
        this.updateById(frontKey);
        esFrontKeyInfoSerivce.saveEsFrontKeyIndex(frontKey);
        return frontKey;
    }

    /**
     * 检查是否存在相同的前端Title
     *
     * @param key
     * @return
     */
    public Integer getFrontKeyByKeyAndLang(String key, String lang) {
        //判断 是否启用了ES
        if (esEnabledFlag) {
            Long count = esFrontKeyInfoSerivce.getFrontKeyByKeyAndLangFrontES(key, lang);
            if (count != null) {
                return count.intValue();
            }
            return 0;
        } else {
            return frontKeyMapper.selectCount(new EntityWrapper<FrontKey>()
                    .eq("lang", lang)
                    .eq("key_code", key));
        }
    }

    /**
     * @param id 删除前端Title（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteFrontKey(Long id) throws Exception {
        if (id != null) {
            this.deleteById(id);
            esFrontKeyInfoSerivce.deleteEsFrontKeyIndex(id);
        }
    }

    /**
     * @param ids 批量删除前端Title（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchFrontKey(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            this.deleteBatchIds(ids);
            ids.forEach(d -> {
                try {
                    esFrontKeyInfoSerivce.deleteEsFrontKeyIndex(d);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }


    /**
     * 根据模块，取所有前端Title 分页
     *
     * @param moduleId  模块Id
     * @param pageable
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<FrontKey> getFrontKeysByModuleId(Long moduleId, Boolean enabled, Pageable pageable) {
        //检查是否启用了ES
        if (esEnabledFlag) {
            return esFrontKeyInfoSerivce.getFrontKeysByModuleIdFromES(moduleId, enabled, pageable);
        }
        Page page = PageUtil.getPage(pageable);
        return frontKeyMapper.selectPage(page, new EntityWrapper<FrontKey>()
                .eq(enabled != null, "enabled", enabled)
                .eq("module_id", moduleId)
                .orderBy("key_code"));
    }

    /**
     * 取所有前端Title 分页
     *
     * @param pageable
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<FrontKey> getFrontKeys(Boolean enabled, Pageable pageable) {
        if (esEnabledFlag) {
            return esFrontKeyInfoSerivce.getFrontKeysFromES(enabled, pageable);
        }
        Page page = PageUtil.getPage(pageable);
        return frontKeyMapper.selectPage(page, new EntityWrapper<FrontKey>()
                .eq(enabled != null, "enabled", enabled)
                .orderBy("key_code"));
    }

    /**
     * 根据ID，获取对应的前端Title信息
     *
     * @param id
     * @return
     */
    public FrontKey getFrontKeyById(Long id) {
        //判断 是否启用了ES
        if (esEnabledFlag) {
            return esFrontKeyInfoSerivce.getFrontKeyByIdFromES(id);
        } else {
            return frontKeyMapper.selectById(id);
        }
    }

    /**
     * 根据模块和Lang，取所有前端Title 分页
     *
     * @param moduleId  模块Id
     * @param lang      语言类型
     * @param pageable
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<FrontKey> getFrontKeysByModuleIdAndLang(Long moduleId, String lang, Boolean enabled, Pageable pageable) {
        //判断 是否启用了ES
        if (esEnabledFlag) {
            return esFrontKeyInfoSerivce.getFrontKeysByModuleIdAndLangFromES(moduleId, lang, enabled, pageable);
        } else {
            Page page = PageUtil.getPage(pageable);
            return frontKeyMapper.selectPage(page, new EntityWrapper<FrontKey>()
                    .eq(enabled != null, "enabled", enabled)
                    .eq("lang", lang)
                    .eq(moduleId != null && moduleId > 0, "module_id", moduleId)
                    .orderBy("key_code"));
        }
    }

    /**
     * 根据模块和Lang，取所有前端Title 不分页
     *
     * @param lang 语言类型
     * @return
     */
    public List<FrontKey> getFrontKeysByLang(String lang) {
        //判断 是否启用了ES
        if (esEnabledFlag) {
            return esFrontKeyInfoSerivce.getFrontKeysByLangFromES(lang);
        } else {
            return frontKeyMapper.selectList(new EntityWrapper<FrontKey>()
                    .eq("enabled", true)
                    .eq("lang", lang)
                    .orderBy("key_code"));
        }
    }

    /**
     * 提示语言同步界面Title
     *
     * @param language
     */
    public void syncFrontKeyByLanguage(String language) {
        //获取所有中文的界面Title(除了已经在 language中的)
        List<FrontKey> list = null;
        if (esEnabledFlag) {
            //获取所有中文的界面Title(除了已经在 language中的)
            list = frontKeyMapper.getListFrontKeysNotInLanguage(language);
        }
        if (list != null) {
            List<FrontKey> newList = list.stream().map(e -> {
                e.setLang(language);
                e.setId(null);
                return e;
            }).collect(Collectors.toList());
            //批量保存 200 提交一次
            this.insertBatch(newList, 200);
            newList.forEach(e->{
                //更新ES缓存
                esFrontKeyInfoSerivce.saveEsFrontKeyIndex(e);
            });
        }
    }

    /**
     * 批量更新 界面Title的描述
     *
     * @param fontKeyDTOS
     */
    public void batchUpdateFrontKey(List<FrontKeyDTO> fontKeyDTOS) {
        if (fontKeyDTOS != null && fontKeyDTOS.size() > 0) {
            List<Long> keyIdList = new ArrayList<>();
            Map<Long, FrontKeyDTO> map = new HashMap<>();
            fontKeyDTOS.stream().forEach(key -> {
                keyIdList.add(key.getId());
                map.put(key.getId(), key);
            });
            List<FrontKey> dbFrontKey = new ArrayList<>();
            if (keyIdList.size() > 0) {
                //批量查询
                if (esEnabledFlag) {
                    dbFrontKey = esFrontKeyInfoSerivce.getFrontKeyByIdsFromES(keyIdList);
                } else {
                    dbFrontKey = frontKeyMapper.selectBatchIds(keyIdList);
                }
            }
            if (dbFrontKey != null && dbFrontKey.size() > 0) {
                //批量设置描述字段
                dbFrontKey.stream().forEach(key -> {
                    key.setDescriptions(map.get(key.getId()).getDescriptions());
                    esFrontKeyInfoSerivce.saveEsFrontKeyIndex(key);
                });
                this.updateBatchById(dbFrontKey);
            }
        }
    }

    /**
     * 批量保存 界面Title
     *
     * @param frontKey
     */
    public void batchCreateFrontKey(List<FrontKey> frontKey) {
        if (frontKey != null && frontKey.size() > 0) {
            //批量保存，里面需要校验keyCode不允许重复
            frontKey.forEach(front -> {
                this.createFrontKey(front);
            });
            //this.insertBatch(frontKey);
        }
    }

    /**
     * 根据KeyCode，查询界面Title，
     *
     * @param keyCode   界面Title的代码
     * @param lang      语言，不传则不控制，传了则按传入的值进行控制
     * @param enabled 启用标识，不传则不控制，传了则按传入的值进行控制
     * @return 界面Title对象
     */
    public List<FrontKey> getFrontKeyByKeyCodeAndLang(String keyCode, String lang, Boolean enabled) {
        if (esEnabledFlag) {
            return esFrontKeyInfoSerivce.getFrontKeyByKeyCodeAndLangFromES(keyCode, lang, enabled);
        } else {
            return frontKeyMapper.selectList(new EntityWrapper<FrontKey>()
                    .eq("key_code", keyCode)
                    .eq(StringUtils.isNotEmpty(lang), "lang", lang)
                    .eq(enabled != null, "enabled", enabled));
        }
    }

    /**
     * 界面Title 模糊查询
     * 查询启用且未删除的界面Title
     *
     * @param keyCode
     * @param descriptions
     * @param moduleId
     * @param lang
     * @param keyword      模糊匹配 keyCode或descriptions
     * @return
     */
    public List<FrontKey> getFrontKeysByCond(String keyCode,
                                             String descriptions,
                                             String moduleId,
                                             String lang,
                                             String keyword,
                                             Pageable pageable) {
        if (StringUtils.isEmpty(keyCode)) {
            keyCode = null;
        }
        if (StringUtils.isEmpty(descriptions)) {
            descriptions = null;
        }
        if (StringUtils.isEmpty(moduleId)) {
            moduleId = null;
        }
        if (StringUtils.isEmpty(lang)) {
            lang = null;
        }
        if (StringUtils.isEmpty(keyword)) {
            keyword = null;
        }
        if(esEnabledFlag){
            return esFrontKeyInfoSerivce.getFrontKeysByCondFromES(keyCode,descriptions,moduleId,lang,keyword,pageable);
        }else{
            Page page = PageUtil.getPage(pageable);
            return frontKeyMapper.getFrontKeysByCond(keyCode, descriptions, moduleId, lang, keyword, page);
        }
    }
}
