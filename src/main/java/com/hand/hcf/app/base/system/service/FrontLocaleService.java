package com.hand.hcf.app.base.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.FrontLocale;
import com.hand.hcf.app.base.system.dto.LocaleDTO;
import com.hand.hcf.app.base.system.persistence.FrontLocaleMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/12
 */
@Service
@AllArgsConstructor
@Transactional
public class FrontLocaleService extends BaseService<FrontLocaleMapper,FrontLocale>{
    private final FrontLocaleMapper frontLocaleMapper;

    /**
     * 单个新增 中控多语言
     * @param frontLocale
     * @return
     */
    @Transactional
    public FrontLocale createFrontLocale(FrontLocale frontLocale){
        if (frontLocale.getId() != null){
            throw new BizException(RespCode.FRONT_LOCALE_EXIST);
        }
        if ( frontLocaleMapper.selectList(
                new EntityWrapper<FrontLocale>()
                        .eq("key_code",frontLocale.getKeyCode())
                        .eq("language",frontLocale.getLanguage())
        ).size() > 0 ){
            throw new BizException(RespCode.FRONT_LOCALE_KEY_CODE_NOT_ALLOWED_TO_REPEAT);
        }
        frontLocaleMapper.insert(frontLocale);
        return frontLocaleMapper.selectById(frontLocale);
    }

    /**
     * 批量新增 中控多语言
     * @param list
     * @return
     */
    @Transactional
    public List<FrontLocale> createFrontLocaleBatch(List<FrontLocale> list){
        List<FrontLocale> result = new ArrayList<>();
        list.stream().forEach(frontLocale -> {
            FrontLocale frontLocaleTemp = createFrontLocale(frontLocale);
            result.add(frontLocaleTemp);
        });
        return result;
    }

    /**
     * 单个编辑 中控多语言
     * @param frontLocale
     * @return
     */
    @Transactional
    public FrontLocale updateFrontLocale(FrontLocale frontLocale){
        if (frontLocale.getId() == null){
            throw new BizException(RespCode.FRONT_LOCALE_NOT_EXIST);
        }
        frontLocaleMapper.updateAllColumnById(frontLocale);
        return frontLocaleMapper.selectById(frontLocale);
    }

    /**
     * 批量编辑 中控多语言
     * @param list
     * @return
     */
    @Transactional
    public List<FrontLocale> updateFrontLocaleBatch(List<FrontLocale> list){
        List<FrontLocale> result = new ArrayList<>();
        list.stream().forEach(frontLocale -> {
            FrontLocale frontLocaleTemp = updateFrontLocale(frontLocale);
            result.add(frontLocaleTemp);
        });
        return result;
    }

    /**
     * 单个删除 中控多语言
     * @param id
     */
    public void deleteFrontLocaleById(Long id){
        FrontLocale frontLocale = frontLocaleMapper.selectById(id);
        if (frontLocale == null){
            throw new BizException(RespCode.FRONT_LOCALE_NOT_EXIST);
        }
        deleteById(id);
    }

    /**
     * 单个查询 中控多语言
     * @param id
     * @return
     */
    public FrontLocale getFrontLocaleById(Long id){
        FrontLocale frontLocale = frontLocaleMapper.selectById(id);
        if (frontLocale == null){
            throw new BizException(RespCode.FRONT_LOCALE_NOT_EXIST);
        }
        return frontLocale;
    }

    /**
     * 分页查询 中控多语言
     * @param language 语言
     * @param applicationId 应用ID
     * @param keyCode 界面key值
     * @param keyDescription key描述
     * @param page
     * @return
     */
    public List<FrontLocale> getFrontLocaleByCond(String language, Long applicationId, String keyCode, String keyDescription, Page page) {
        List<FrontLocale> result = frontLocaleMapper.selectPage(page,
                new EntityWrapper<FrontLocale>()
                        .eq(language != null,"language",language)
                        .eq(applicationId != null,"application_id",applicationId)
                        .like(keyCode != null,"key_code",keyCode)
                        .like(keyDescription != null,"key_description",keyDescription)
                        .orderBy("key_code")
        );
        return result;
    }

    /**
     * 不分页查询 map形式的中控多语言
     * @param language
     * @param applicationId
     * @return
     */
    public Map<String,String> mapFrontLocaleByCond(String language,Long applicationId){
        Map<String,String> mapFrontLocale = new HashMap<>();

        List<FrontLocale> frontLocaleList = frontLocaleMapper.selectList(
                new EntityWrapper<FrontLocale>()
                        .eq(language != null,"language",language)
                        .eq(applicationId != null,"application_id",applicationId)
                        .orderBy("key_code")
        );
        if (!CollectionUtils.isEmpty(frontLocaleList)){
            frontLocaleList.stream().forEach(frontLocale -> {
                mapFrontLocale.put(frontLocale.getKeyCode(),frontLocale.getKeyDescription());
            });
        }

        return mapFrontLocale;
    }

    /**
     * 分页查询 中控多语言(返回外文描述信息)
     * @param applicationId
     * @param sourceLanguage
     * @param targetLanguage
     * @param keyCode
     * @param page
     * @return
     */
    public List<LocaleDTO> getOtherFrontLocaleByCond(Long applicationId, String sourceLanguage, String targetLanguage, String keyCode, Page page) {
        List<LocaleDTO> localeDTOList = new ArrayList<>();

        List<FrontLocale> frontLocaleList = frontLocaleMapper.selectPage(page,
                new EntityWrapper<FrontLocale>()
                        .eq(applicationId != null, "application_id", applicationId)
                        .eq(sourceLanguage != null, "language", sourceLanguage)
                        .like(keyCode != null, "key_code", keyCode)
        );

        if (!CollectionUtils.isEmpty(frontLocaleList)){
            frontLocaleList.stream().forEach(sourceFrontLocale -> {
                FrontLocale targetFrontLocale = this.selectOne(
                        new EntityWrapper<FrontLocale>()
                                .eq("application_id", sourceFrontLocale.getApplicationId())
                                .eq("application_code", sourceFrontLocale.getApplicationCode())
                                .eq("key_code", sourceFrontLocale.getKeyCode())
                                .eq("language", targetLanguage)
                );

                LocaleDTO localeDTO = LocaleDTO.builder()
                        .keyCode(sourceFrontLocale.getKeyCode())
                        .sourceId(sourceFrontLocale.getId())
                        .sourceKeyDescription(sourceFrontLocale.getKeyDescription())
                        .build();

                if (targetFrontLocale != null){
                    localeDTO.setTargetId(targetFrontLocale.getId());
                    localeDTO.setTargetKeyDescription(targetFrontLocale.getKeyDescription());
                    localeDTO.setTargetVersionNumber(targetFrontLocale.getVersionNumber());
                }else {
                    localeDTO.setTargetId(null);
                    localeDTO.setTargetKeyDescription(null);
                }
                localeDTOList.add(localeDTO);
            });
        }
        return localeDTOList;
    }
}
