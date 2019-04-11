package com.hand.hcf.app.mdata.location.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.location.domain.LocationLevelAssign;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: 地点级别分配地点Mapper
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/3/27 14:43
 */
public interface LocationLevelAssignMapper extends BaseMapper<LocationLevelAssign> {
    /**
     * 根据地点级别id删除地点级别分配信息
     *
     * @param levelId
     */
    public void deleteLocationLevelAssignByLevelId(Long levelId);

    /**
     * 根据id删除地点级别分配信息
     *
     * @param id
     */
    public void deleteLocationLevelAssignById(Long id);

    /**
     * 查询地点级别已分配的地点信息
     * @param levelId
     * @param countryCode
     * @param stateCode
     * @param cityCode
     * @param type
     * @param code
     * @param description
     * @param language
     * @param page
     * @return
     */
    public List<LocationLevelAssign> queryLocationLevelAssign(@Param("levelId") Long levelId,
                                                              @Param("countryCode") String countryCode,
                                                              @Param("stateCode") String stateCode,
                                                              @Param("cityCode") String cityCode,
                                                              @Param("type") String type,
                                                              @Param("code") String code,
                                                              @Param("description") String description,
                                                              @Param("language") String language,
                                                              Page page);
}
