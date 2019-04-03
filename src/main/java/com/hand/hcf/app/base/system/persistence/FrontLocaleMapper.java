package com.hand.hcf.app.base.system.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.system.domain.FrontLocale;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/12
 */
public interface FrontLocaleMapper extends BaseMapper<FrontLocale>{
    List<FrontLocale> getFrontLocaleByCond(@Param("applicationId") Long applicationId,
                                  @Param("targetLanguage") String targetLanguage,
                                  @Param("keyCode") String keyCode,
                                  Page page);

}
