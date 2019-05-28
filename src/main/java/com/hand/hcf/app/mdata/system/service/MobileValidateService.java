package com.hand.hcf.app.mdata.system.service;

import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import com.hand.hcf.app.mdata.system.domain.MobileValidate;
import com.hand.hcf.app.mdata.system.persistence.MobileValidateMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Created by Transy on 2017-10-31.
*/
@Service
@CacheConfig(cacheNames = {CacheConstants.MOBILE_VALIDATE})
public class MobileValidateService {

    public static final String DEFAULT_COUNTRY_CODE = "CN";
    public static final Pattern number = Pattern.compile("[0-9]*");
    private final Logger log = LoggerFactory.getLogger(MobileValidateService.class);

    @Autowired
    private MobileValidateMapper mobileValidateMapper;
    @Autowired
    BaseI18nService baseI18nService;

    public boolean validateMobile(String countryCode,String mobile){
        if(StringUtils.isBlank(mobile)){
            return false;
        }
        if(!isNumeric(mobile)){
            return false;
        }
        if(StringUtils.isBlank(countryCode)){
            countryCode = DEFAULT_COUNTRY_CODE;
        }
        Map<String,Object> param = new HashedMap();
        param.put("short_name",countryCode);
        List<MobileValidate> mobileValidateList = mobileValidateMapper.selectByMap(param);
        if(CollectionUtils.isNotEmpty(mobileValidateList)){
           long count = mobileValidateList.stream().filter(mobileValidate -> mobileValidate.getMobileLength()==0 || mobileValidate.getMobileLength() == mobile.length()).count();
           return count > 0 ? true : false;
        }else{
            log.info("国家编码验证规则不存在,国家编码:{}",countryCode);
            throw new RuntimeException("国家编码验证规则不存在,国家编码:"+countryCode);
        }
    }

    @CacheEvict(value = CacheConstants.MOBILE_VALIDATE,allEntries = true)
    public MobileValidate save(MobileValidate mobileValidate){
        Map<String,Object> param = new HashedMap();
        param.put("country_code",mobileValidate.getCountryCode());
        List<MobileValidate> result = mobileValidateMapper.selectByMap(param);
        if(CollectionUtils.isNotEmpty(result)){
            throw new BizException(RespCode.MOBILE_VALIDATE_DUPLICATED_CODE);
        }
        mobileValidateMapper.insert(mobileValidate);
        baseI18nService.insertOrUpdateI18n(mobileValidate.getI18n(),MobileValidate.class,mobileValidate.getId());
        return mobileValidate;
    }

    @CacheEvict(value = CacheConstants.MOBILE_VALIDATE,allEntries = true)
    public MobileValidate update(MobileValidate mobileValidate){
        Map<String,Object> param = new HashedMap();
        param.put("country_code",mobileValidate.getCountryCode());
        List<MobileValidate> result = mobileValidateMapper.selectByMap(param);
        if(CollectionUtils.isNotEmpty(result)){
            if(!result.get(0).getId().equals(mobileValidate.getId())){
                throw new BizException(RespCode.MOBILE_VALIDATE_DUPLICATED_CODE);
            }
        }
        mobileValidateMapper.updateById(mobileValidate);
        baseI18nService.insertOrUpdateI18n(mobileValidate.getI18n(),MobileValidate.class,mobileValidate.getId());
        return mobileValidate;
    }

    @CacheEvict(value = CacheConstants.MOBILE_VALIDATE,allEntries = true)
    public void delete(long id){
        MobileValidate mobileValidate = new MobileValidate();
        mobileValidate.setId(id);
        mobileValidate.setDeleted(true);
        mobileValidateMapper.updateById(mobileValidate);
    }

    public MobileValidate findOne(Long id) {
//       return mobileValidateMapper.selectById(id);
        return baseI18nService.selectOneBaseTableInfoWithI18n(id,MobileValidate.class);
    }
    //@Cacheable(keyGenerator = "wiselyKeyGenerator")
    public List<MobileValidate> findAll(boolean isEnabled,String language) {
        //参数language 做多语言缓存作用
        List<MobileValidate> all = mobileValidateMapper.findAll(isEnabled);
        return baseI18nService.convertListByLocale(all);
    }

    public MobileValidate getMobileValidate(String shortName){
        if(StringUtils.isBlank(shortName)){
            shortName = DEFAULT_COUNTRY_CODE;
        }
        Map<String,Object> param = new HashedMap();
        param.put("short_name",shortName);
        param.put("enabled",true);
        param.put("deleted",false);
        List<MobileValidate> mobileValidateList = mobileValidateMapper.selectByMap(param);
        if(CollectionUtils.isNotEmpty(mobileValidateList)){
            MobileValidate mobileValidate = mobileValidateList.get(0);
            return baseI18nService.convertOneByLocale(mobileValidate);
        }else {
            return null;
        }
    }

    public boolean isNumeric(String str){
        Matcher isNum = number.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public void insetMobileValidateI18n(Long id ,String language,String countryName){
        mobileValidateMapper.insetMobileValidateI18n(id,language,countryName);
    }
}
