package com.hand.hcf.app.base.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.base.system.domain.ServeLocale;
import com.hand.hcf.app.base.system.persistence.ServeLocaleMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class ServeLocaleService extends BaseService<ServeLocaleMapper,ServeLocale>{
    private final ServeLocaleMapper serveLocaleMapper;

    private final SysCodeService sysCodeService;

    /**
     * 单个新增 服务端多语言
     * @param serveLocale
     * @return
     */
    public ServeLocale createServeLocale(ServeLocale serveLocale){
        if (serveLocale.getId() != null){
            throw new BizException(RespCode.SERVE_LOCALE_EXIST);
        }
        if ( serveLocaleMapper.selectList(
                new EntityWrapper<ServeLocale>()
                        .eq("deleted",false)
                        .eq("key_code",serveLocale.getKeyCode())
                        .eq("language",serveLocale.getLanguage())
        ).size() > 0 ){
            throw new BizException(RespCode.SERVE_LOCALE_KEY_CODE_NOT_ALLOWED_TO_REPEAT);
        }
        serveLocaleMapper.insert(serveLocale);
        return serveLocaleMapper.selectById(serveLocale);
    }

    /**
     * 单个编辑 服务端多语言
     * @param serveLocale
     * @return
     */
    public ServeLocale updateServeLocale(ServeLocale serveLocale){
        if (serveLocale.getId() == null){
            throw new BizException(RespCode.SERVE_LOCALE_NOT_EXIST);
        }
        serveLocaleMapper.updateAllColumnById(serveLocale);
        return serveLocaleMapper.selectById(serveLocale);
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
                        .eq("deleted",false)
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
                        .eq("deleted",false)
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
}
