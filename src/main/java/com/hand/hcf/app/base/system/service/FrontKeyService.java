package com.hand.hcf.app.base.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.FrontKey;
import com.hand.hcf.app.base.system.domain.Module;
import com.hand.hcf.app.base.system.dto.FrontKeyDTO;
import com.hand.hcf.app.base.system.persistence.FrontKeyMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author houyin.zhang@hand-china.com
 * @date 2018/8/20
 * 多前端TitleService
 */
@Service
public class FrontKeyService extends BaseService<FrontKeyMapper, FrontKey> {

    private final FrontKeyMapper frontKeyMapper;

    private final ModuleService moduleService;



    public FrontKeyService(FrontKeyMapper frontKeyMapper, ModuleService moduleService) {
        this.frontKeyMapper = frontKeyMapper;
        this.moduleService = moduleService;
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
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (frontKey.getKeyCode() == null || "".equals(frontKey.getKeyCode())) {
            throw new BizException(RespCode.AUTH_FRONT_KEY_NULL);
        }
        if (frontKey.getModuleId() == null ) {
            throw new BizException(RespCode.AUTH_MODULE_ID_NULL);
        }
        //检查key是否唯一
        Integer count = getFrontKeyByKeyAndLang(frontKey.getKeyCode(), frontKey.getLang());
        if (count != null && count > 0) {
            throw new BizException(RespCode.AUTH_FRONT_KEY_NOT_UNION);
        }
        if(StringUtils.isEmpty(frontKey.getModuleCode())){
            Module module = moduleService.selectById(frontKey.getModuleId());
            frontKey.setModuleCode(module.getModuleCode());
        }
        frontKeyMapper.insert(frontKey);
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
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        if (frontKey.getModuleId() == null ) {
            throw new BizException(RespCode.AUTH_MODULE_ID_NULL);
        }
        //校验ID是否在数据库中存在
        FrontKey rr = frontKeyMapper.selectById(frontKey.getId());
        if (rr == null) {
            throw new BizException(RespCode.SYS_DATASOURCE_CANNOT_FIND_OBJECT);
        }
        if (frontKey.getEnabled() == null ) {
            frontKey.setEnabled(rr.getEnabled());
        }
        if (frontKey.getDeleted() == null ) {
            frontKey.setDeleted(rr.getDeleted());
        }
        if (frontKey.getModuleId() == null ) {
            frontKey.setModuleId(rr.getModuleId());
        }
        if (frontKey.getModuleCode() == null || "".equals(frontKey.getModuleCode())) {
            frontKey.setModuleCode(rr.getModuleCode());
        }
        frontKey.setCreatedBy(rr.getCreatedBy());
        frontKey.setCreatedDate(rr.getCreatedDate());
        frontKey.setKeyCode(rr.getKeyCode());
        this.updateById(frontKey);
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

            return frontKeyMapper.selectCount(new EntityWrapper<FrontKey>()
                    .eq("lang", lang)
                    .eq("key_code", key));
    }

    /**
     * @param id 删除前端Title（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteFrontKey(Long id) {
        if (id != null) {
            FrontKey frontKey = selectById(id);
            frontKey.setDeleted(true);
            frontKey.setKeyCode(frontKey.getKeyCode() + "_DELETED_" + RandomStringUtils.random(6));
            updateById(frontKey);
        }
    }

    /**
     * @param ids 批量删除前端Title（逻辑删除）
     * @return
     */
    @Transactional
    public void deleteBatchFrontKey(List<Long> ids) {
        if (ids != null && CollectionUtils.isNotEmpty(ids)) {
            ids.forEach(d -> {
                try {
                    deleteFrontKey(d);
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
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<FrontKey> getFrontKeysByModuleId(Long moduleId, Boolean enabled, Page page) {
        //检查是否启用了ES

        return frontKeyMapper.selectPage(page, new EntityWrapper<FrontKey>()
                .eq(enabled != null, "enabled", enabled)
                .eq("module_id", moduleId)
                .orderBy("key_code"));
    }

    /**
     * 取所有前端Title 分页
     *
     * @param page
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @return
     */
    public List<FrontKey> getFrontKeys(Boolean enabled, Page page) {

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

            return frontKeyMapper.selectById(id);
    }

    /**
     * 根据模块和Lang，取所有前端Title 分页
     *
     * @param moduleId  模块Id
     * @param lang      语言类型
     * @param enabled 如果不传，则不控制，如果传了，则根据传的值控制
     * @param key
     * @param descriptions
     * @param page
     * @return
     */
    public List<FrontKey> getFrontKeysByModuleIdAndLang(Long moduleId, String lang, Boolean enabled, String key, String descriptions, Page page) {
        //判断 是否启用了ES

            return frontKeyMapper.selectPage(page, new EntityWrapper<FrontKey>()
                    .eq(enabled != null, "enabled", enabled)
                    .eq("lang", lang)
                    .eq(moduleId != null && moduleId > 0, "module_id", moduleId)
                    .like(key != null,"key_code", key)
                    .like(descriptions != null, "descriptions", descriptions )
                    .orderBy("key_code"));
    }

    /**
     * 根据模块和Lang，取所有前端Title 不分页
     *
     * @param lang 语言类型
     * @return
     */
    public List<FrontKey> getFrontKeysByLang(String lang) {
        //判断 是否启用了ES

            return frontKeyMapper.selectList(new EntityWrapper<FrontKey>()
                    .eq("enabled", true)
                    .eq("lang", lang)
                    .orderBy("key_code"));
    }

    /**
     * 提示语言同步界面Title
     *
     * @param language
     */
    public void syncFrontKeyByLanguage(String language) {
        //获取所有中文的界面Title(除了已经在 language中的)
        List<FrontKey> list = null;

        if (list != null) {
            List<FrontKey> newList = list.stream().map(e -> {
                e.setLang(language);
                e.setId(null);
                return e;
            }).collect(Collectors.toList());
            //批量保存 100 提交一次
            this.insertBatch(newList, 100);

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

                    dbFrontKey = frontKeyMapper.selectBatchIds(keyIdList);
            }
            if (dbFrontKey != null && dbFrontKey.size() > 0) {
                //批量设置描述字段
                dbFrontKey.stream().forEach(key -> {
                    key.setDescriptions(map.get(key.getId()).getDescriptions());
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
    @Transactional
    public void batchCreateFrontKey(List<FrontKey> frontKey) {
        if (frontKey != null && frontKey.size() > 0) {
            //批量保存，里面需要校验keyCode不允许重复
            frontKey.forEach(front -> {
                this.createFrontKey(front);
            });
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

            return frontKeyMapper.selectList(new EntityWrapper<FrontKey>()
                    .eq("key_code", keyCode)
                    .eq(StringUtils.isNotEmpty(lang), "lang", lang)
                    .eq(enabled != null, "enabled", enabled));
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
                                             Page page) {
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

            return frontKeyMapper.getFrontKeysByCond(keyCode, descriptions, moduleId, lang, keyword, page);
    }

    /**
     * 根据模块代码、keyCode、语言查询多语言信息
     * 由于该接口主要供第三方接口使用，在base中使用索引库，所以在此就不使用es了
     * @param moduleCode
     * @param keyCode
     * @param lang
     * @return
     */
    public FrontKey getFrontKeyByModuleAndKeyAndLang(String moduleCode,
                                       String keyCode,
                                       String lang){
        FrontKey frontKey = new FrontKey();
        frontKey.setModuleCode(moduleCode);
        frontKey.setKeyCode(keyCode);
        frontKey.setLang(lang);
        frontKey.setDeleted(false);
        FrontKey frontKeyValue = frontKeyMapper.selectOne(frontKey);
        if(frontKeyValue == null){
            frontKey.setLang(LanguageEnum.ZH_CN.getKey());
            frontKeyValue = frontKeyMapper.selectOne(frontKey);
        }
        return frontKeyValue;
    }

    /**
     *  根据模块代码、keyCode 查询多语言信息
     *  由于该接口主要供第三方接口使用，在base中使用索引库，所以在此就不使用es了
     * @param moduleCode
     * @param keyCode
     * @return
     */
    public List<FrontKey> getFrontKeysByModuleAndKey(String moduleCode,
                                             String keyCode){
        return selectList(new EntityWrapper<FrontKey>()
                .eq("module_code",moduleCode)
                .eq("key_code",keyCode)
                .eq("deleted",false));
    }
}
