package com.hand.hcf.app.base.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.domain.FrontKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FrontKeyMapper extends BaseMapper<FrontKey> {
    List<FrontKey> getListFrontKeysNotInLanguage(String lanugage);

    /**
     * 界面Title 模糊查询
     *
     * @param keyCode
     * @param descriptions
     * @param moduleId
     * @param lang
     * @param keyword      模糊匹配 keyCode或descriptions
     * @return
     */
    List<FrontKey> getFrontKeysByCond(@Param("keyCode") String keyCode,
                                      @Param("descriptions") String descriptions,
                                      @Param("moduleId") String moduleId,
                                      @Param("lang") String lang,
                                      @Param("keyword") String keyword,
                                      Page page);
}