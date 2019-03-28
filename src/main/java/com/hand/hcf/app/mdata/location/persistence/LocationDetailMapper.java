package com.hand.hcf.app.mdata.location.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.location.domain.LocationDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.HashSet;
import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 18:15 2018/5/17
 * @Modified by
 */
public interface LocationDetailMapper extends BaseMapper<LocationDetail> {
    List<LocationDetail> findByDomesticByVendorTypeAndLocationType(RowBounds page, @Param("baseCountry") String baseCountry,
                                                                   @Param("locationType") String locationType,
                                                                   @Param("vendorType") String vendorType);
    List<LocationDetail> findByDomesticByVendorTypeAndLocationType(@Param("baseCountry") String baseCountry,
                                                                   @Param("locationType") String locationType,
                                                                   @Param("vendorType") String vendorType);
    List<LocationDetail> findByStateName(@Param("baseCountry") String baseCountry,
                                         @Param("locationType") String locationType,
                                         @Param("stateNames") HashSet<String> stateNames);
    List<LocationDetail> findByForeignByVendorType(RowBounds page, @Param("baseCountry") String baseCountry,
                                                   @Param("locationType") String locationType,
                                                   @Param("vendorType") String vendorType);
    List<LocationDetail> findByForeignByVendorType(@Param("baseCountry") String baseCountry,
                                                   @Param("locationType") String locationType,
                                                   @Param("vendorType") String vendorType);
    List<LocationDetail> findByStateName(RowBounds page, @Param("baseCountry") String baseCountry,
                                         @Param("locationType") String locationType,
                                         @Param("stateNames") HashSet<String> stateNames);
    LocationDetail findByCode(@Param("code") String code, @Param("language") String language);

    List<LocationDetail> findByLocationTypeAndLanguage(@Param("language") String language,
                                                       @Param("baseCountry") String baseCountry,
                                                       @Param("locationType") String locationType,
                                                       @Param("vendorType") String vendorType,
                                                       @Param("stateCode") String stateCode,
                                                       @Param("cityCode") String cityCode);
    List<LocationDetail> findByLocationTypeAndLanguage(@Param("language") String language,
                                                       @Param("baseCountry") String baseCountry,
                                                       @Param("locationType") String locationType,
                                                       @Param("vendorType") String vendorType,
                                                       @Param("stateCode") String stateCode,
                                                       @Param("cityCode") String cityCode,
                                                       RowBounds page);
    List<LocationDetail> findByStateNameLanguage(RowBounds page, @Param("baseCountry") String baseCountry,
                                                 @Param("locationType") String locationType,
                                                 @Param("stateNames") HashSet<String> stateNames,
                                                 @Param("language") String language);
    List<LocationDetail> findByStateNameLanguage(@Param("baseCountry") String baseCountry,
                                                 @Param("locationType") String locationType,
                                                 @Param("stateNames") HashSet<String> stateNames,
                                                 @Param("language") String language);

    List<LocationDetail> findMunicipalities(@Param("locationType") String locationType,
                                            @Param("vendorType") String vendorType,
                                            @Param("stateNames") List<String> stateNames,
                                            @Param("language") String language);
    List<LocationDetail> findNameWithoutMunicipalitiesAndVendorType(@Param("locationType") String locationType,
                                                                    @Param("vendorType") String vendorType,
                                                                    @Param("stateNames") List<String> stateNames,
                                                                    @Param("language") String language);
}
