package com.hand.hcf.app.mdata.location.persistence;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.mdata.location.dto.LocationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/3/29 10:47
 */
@Mapper
public interface LocationDTOMapper {

    /**
     * 按条件查询地点信息
     *
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
    List<LocationDTO> queryLocationByCondition(@Param("countryCode") String countryCode,
                                               @Param("stateCode") String stateCode,
                                               @Param("cityCode") String cityCode,
                                               @Param("type") String type,
                                               @Param("code") String code,
                                               @Param("description") String description,
                                               @Param("language") String language,
                                               Page page);

    /**
     * 获得国家列表信息
     *
     * @param language
     * @return
     */
    List<LocationDTO> getCountryList(String language);

    /**
     * 根据国家代码获得省信息
     *
     * @param countryCode
     * @param language
     * @return
     */
    List<LocationDTO> getStateListByCountryCode(@Param("countryCode") String countryCode, @Param("language") String language);

    /**
     * 根据省代码获得市
     *
     * @param stateCode
     * @param language
     * @return
     */
    List<LocationDTO> getCityListByStateCode(@Param("stateCode") String stateCode,@Param("language") String language);
}
