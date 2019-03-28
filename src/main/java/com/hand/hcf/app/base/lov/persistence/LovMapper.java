package com.hand.hcf.app.base.lov.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.lov.domain.Lov;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LovMapper extends BaseMapper<Lov> {

    /**
     * Lov  分页查询
     *
     * @param lovCode
     * @param lovName
     * @param appId
     * @return
     */
    List<Lov> pageAll(@Param("lovCode") String lovCode,
                      @Param("lovName") String lovName,
                      @Param("appId") Long appId,
                      Page page);
}
