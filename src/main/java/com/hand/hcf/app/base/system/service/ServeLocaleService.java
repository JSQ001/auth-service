package com.hand.hcf.app.base.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.base.system.domain.ServeLocale;
import com.hand.hcf.app.base.system.dto.LocaleDTO;
import com.hand.hcf.app.base.system.persistence.ServeLocaleMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;
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
 * @date: 2019/3/11
 */
@Service
@AllArgsConstructor
@Transactional
public class ServeLocaleService extends BaseService<ServeLocaleMapper,ServeLocale>{
    private final ServeLocaleMapper serveLocaleMapper;

    private final SysCodeService sysCodeService;

    /**
     * 单个新增 服务端多语言
     * @param serveLocale
     * @return
     */
    @Transactional
    public ServeLocale createServeLocale(ServeLocale serveLocale){
        if (serveLocale.getId() != null){
            throw new BizException(RespCode.SERVE_LOCALE_EXIST);
        }
        if ( serveLocaleMapper.selectList(
                new EntityWrapper<ServeLocale>()
                        .eq("key_code",serveLocale.getKeyCode())
                        .eq("language",serveLocale.getLanguage())
        ).size() > 0 ){
            throw new BizException(RespCode.SERVE_LOCALE_KEY_CODE_NOT_ALLOWED_TO_REPEAT);
        }
        serveLocaleMapper.insert(serveLocale);
        return serveLocaleMapper.selectById(serveLocale);
    }

    /**
     * 批量新增 服务端多语言
     * @param list
     * @return
     */
    @Transactional
    public List<ServeLocale> createServeLocaleBatch(List<ServeLocale> list){
        List<ServeLocale> result = new ArrayList<>();
        list.stream().forEach(serveLocale -> {
            ServeLocale serveLocaleTemp = createServeLocale(serveLocale);
            result.add(serveLocaleTemp);
        });
        return result;
    }

    /**
     * 单个编辑 服务端多语言
     * @param serveLocale
     * @return
     */
    @Transactional
    public ServeLocale updateServeLocale(ServeLocale serveLocale){
        if (serveLocale.getId() == null){
            throw new BizException(RespCode.SERVE_LOCALE_NOT_EXIST);
        }
        serveLocaleMapper.updateAllColumnById(serveLocale);
        return serveLocaleMapper.selectById(serveLocale);
    }

    /**
     * 批量编辑 服务端多语言
     * @param list
     * @return
     */
    @Transactional
    public List<ServeLocale> updateServeLocaleBatch(List<ServeLocale> list){
        List<ServeLocale> result = new ArrayList<>();
        list.stream().forEach(serveLocale -> {
            ServeLocale serveLocaleTemp = updateServeLocale(serveLocale);
            result.add(serveLocaleTemp);
        });
        return result;
    }

    /**
     * 单个删除 服务端多语言
     * @param id
     */
    public void deleteServeLocaleById(Long id){
        ServeLocale serveLocale = serveLocaleMapper.selectById(id);
        if (serveLocale == null){
            throw new BizException(RespCode.SERVE_LOCALE_NOT_EXIST);
        }
        deleteById(id);
    }

    /**
     * 单个查询 服务端语言
     * @param id
     * @return
     */
    public ServeLocale getServeLocaleById(Long id){
        ServeLocale serveLocale = serveLocaleMapper.selectById(id);
        if (serveLocale == null){
            throw new BizException(RespCode.SERVE_LOCALE_NOT_EXIST);
        }
        SysCodeValue sysCodeValue = sysCodeService.getValueBySysCodeAndValue("CATEGORY", serveLocale.getCategory());
        if (sysCodeValue != null){
            serveLocale.setCategoryName(sysCodeValue.getName());
        }
        return serveLocale;
    }

    /**
     * 分页查询 服务端多语言
     * @param language
     * @param applicationId
     * @param keyCode
     * @param keyDescription
     * @param page
     * @return
     */
    public List<ServeLocale> getServeLocaleByCond(String language, Long applicationId, String keyCode, String keyDescription,String category,Page page) {
        if (category == ""){
            category = null;
        }
        List<ServeLocale> result = serveLocaleMapper.selectPage(page,
                new EntityWrapper<ServeLocale>()
                        .eq(language != null,"language",language)
                        .eq(applicationId != null,"application_id",applicationId)
                        .like(keyCode != null,"key_code",keyCode)
                        .like(keyDescription != null,"key_description",keyDescription)
                        .eq(category != null ,"category",category)
                        .orderBy("key_code")
        );
        if (!CollectionUtils.isEmpty(result)){
            result.stream().forEach(serveLocale -> {
                SysCodeValue sysCodeValue = sysCodeService.getValueBySysCodeAndValue("CATEGORY", serveLocale.getCategory());
                if (sysCodeValue != null){
                    serveLocale.setCategoryName(sysCodeValue.getName());
                }
            });
        }
        return result;
    }

    /**
     * 不分页查询 map形式的服务端多语言
     * @param language
     * @param applicationId
     * @return
     */
    public Map<String,String> mapServeLocaleByCond(String language, Long applicationId){
        Map<String,String> mapServeLocale = new HashMap<>();

        List<ServeLocale> serveLocaleList = serveLocaleMapper.selectList(
                new EntityWrapper<ServeLocale>()
                        .eq(language != null,"language",language)
                        .eq(applicationId != null,"application_id",applicationId)
                        .orderBy("key_code")
        );
        if (!CollectionUtils.isEmpty(serveLocaleList)){
            serveLocaleList.stream().forEach(serveLocale -> {
                mapServeLocale.put(serveLocale.getKeyCode(),serveLocale.getKeyDescription());
            });
        }

        return mapServeLocale;
    }

    /**
     * 分页查询 服务端多语言(返回外文描述信息)
     * @param applicationId
     * @param sourceLanguage
     * @param targetLanguage
     * @param keyCode
     * @param page
     * @return
     */
    public List<LocaleDTO> getOtherServeLocaleByCond(Long applicationId, String sourceLanguage, String targetLanguage, String keyCode, Page page) {
        List<LocaleDTO> localeDTOList = new ArrayList<>();

        List<ServeLocale> serveLocaleList = serveLocaleMapper.selectPage(page,
                new EntityWrapper<ServeLocale>()
                        .eq(applicationId != null, "application_id", applicationId)
                        .eq(sourceLanguage != null, "language", sourceLanguage)
                        .like(keyCode != null, "key_code", keyCode)
        );

        if (!CollectionUtils.isEmpty(serveLocaleList)){
            serveLocaleList.stream().forEach(sourceServeLocale -> {
                ServeLocale targetServeLocale = this.selectOne(
                        new EntityWrapper<ServeLocale>()
                                .eq("application_id", sourceServeLocale.getApplicationId())
                                .eq("application_code", sourceServeLocale.getApplicationCode())
                                .eq("key_code", sourceServeLocale.getKeyCode())
                                .eq("language", targetLanguage)
                );

                LocaleDTO localeDTO = LocaleDTO.builder()
                        .keyCode(sourceServeLocale.getKeyCode())
                        .sourceId(sourceServeLocale.getId())
                        .sourceKeyDescription(sourceServeLocale.getKeyDescription())
                        .sourceCategory(sourceServeLocale.getCategory())
                        .build();

                if (targetServeLocale != null){
                    localeDTO.setTargetId(targetServeLocale.getId());
                    localeDTO.setTargetKeyDescription(targetServeLocale.getKeyDescription());
                    localeDTO.setTargetVersionNumber(targetServeLocale.getVersionNumber());
                }else {
                    localeDTO.setTargetId(null);
                    localeDTO.setTargetKeyDescription(null);
                }
                localeDTOList.add(localeDTO);
            });
        }
        return localeDTOList;
    }

    /**
     * 根据keyCode、语言查询多语言信息
     * 由于该接口主要供第三方接口使用，在base中使用索引库，所以在此就不使用es了
     * @param keyCode
     * @param language
     * @return
     */
    public ServeLocale getServeLocaleByKeyAndLanguage(String keyCode,
                                                      String language){
        ServeLocale serve = ServeLocale.builder()
                .keyCode(keyCode)
                .language(language)
                .build();
        ServeLocale serveLocale = serveLocaleMapper.selectOne(serve);
        if(serveLocale == null){
            serve.setLanguage(LanguageEnum.ZH_CN.getKey());
            serveLocale = serveLocaleMapper.selectOne(serve);
        }
        return serveLocale;
    }

    /**
     *  根据keyCode 查询多语言信息
     *  由于该接口主要供第三方接口使用，在base中使用索引库，所以在此就不使用es了
     * @param keyCode
     * @return
     */
    public List<ServeLocale> listServeLocaleByKey(String keyCode){
        return selectList(new EntityWrapper<ServeLocale>()
                .eq("key_code",keyCode));
    }
}
