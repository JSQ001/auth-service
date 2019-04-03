package com.hand.hcf.app.mdata.location.persistence;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.common.dto.LocalizationDTO;
import com.hand.hcf.app.common.dto.LocationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by silence on 2018/5/13.
 */
@Mapper
public interface LocalizationDTOMapper{
    /**
     * 分页查询 根据条件查询国家信息
     * @param countryCode 国家代码
     * @param countryName 国家名称
     * @param language 语言类型
     * @param includeList 筛选条件
     * @param page 分页对象
     * @return
     */
    public List<LocalizationDTO> getLocalizationCountryByCode(@Param("countryCode") String countryCode,
                                                              @Param("countryName") String countryName,
                                                              @Param("language") String language,
                                                              @Param("includeList") String includeList,
                                                              Pagination page);

    public List<LocalizationDTO> getLocalizationStateByCode(@Param("code") String code, @Param("vendorType") String vendorType, @Param("language") String language, @Param("includeList") String includeList, Pagination page);

    public List<LocalizationDTO> getLocalizationCityByCode(@Param("code") String code, @Param("vendorType") String vendorType, @Param("language") String language, @Param("includeList") String includeList, Pagination page);

    public List<LocalizationDTO> getLocalizationDistrictByCode(@Param("code") String code, @Param("vendorType") String vendorType, @Param("language") String language, @Param("includeList") String includeList, Pagination page);
    // 新城市查询SQL
    List<LocalizationDTO> getLocalizationCityByCountry(@Param("code") String code,
                                                       @Param("city") String city,
                                                       @Param("vendorType") String vendorType,
                                                       @Param("language") String language);
    List<LocalizationDTO> selectLocalizationDistrictByCountry(@Param("code") String code, @Param("vendorType") String vendorType, @Param("language") String language);

    LocalizationDTO getOneLocalizationCountryByCode(@Param("language") String language, @Param("code") String code);

    List<LocationDTO> listCityByIds(@Param("code") String code,
                                    @Param("vendorType") String vendorType,
                                    @Param("language") String language,
                                    @Param("cityIds") List<Long> cityIds);
}
