package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.plugins.Page;

import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.location.dto.LocationDTO;
import com.hand.hcf.app.mdata.location.persistence.LocationDTOMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 地点DTO
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/3/29 13:43
 */
@Service
public class LocationDTOService {
    @Autowired
    private LocationDTOMapper locationDTOMapper;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;
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
    public List<LocationDTO> queryLocationByCondition(String countryCode,
                                                      String stateCode,
                                                      String cityCode,
                                                      String type,
                                                      String code,
                                                      String description,
                                                      String language,
                                                      Page<LocationDTO> page) {
        List<LocationDTO> locationDTOS = locationDTOMapper.queryLocationByCondition(countryCode, stateCode, cityCode, type, code, description, language, page);
        List<LocationDTO> locations = locationDTOS.stream().map(locationDTO -> {
            LocationDTO domain = new LocationDTO();
            BeanUtils.copyProperties(locationDTO,domain);
            if (!StringUtils.isEmpty(locationDTO.getType())) {
                SysCodeValueCO sysCodeValueCO = hcfOrganizationInterface.getValueBySysCodeAndValue("LOCATION_TYPE",locationDTO.getType());
                domain.setTypeDesc(sysCodeValueCO.getName());
            }
            return domain;
        }).collect(Collectors.toList());
        return locations;
    }

    /**
     * 获得国家列表信息
     *
     * @param language
     * @return
     */
    public List<LocationDTO> getCountryList(String language) {
        return locationDTOMapper.getCountryList(language);
    }

    /**
     * 根据国家代码获得省信息
     *
     * @param countryCode
     * @param language
     * @return
     */
    public List<LocationDTO> getStateListByCountryCode(String countryCode, String language) {
        return locationDTOMapper.getStateListByCountryCode(countryCode, language);
    }

    /**
     * 根据省代码获得市
     *
     * @param stateCode
     * @param language
     * @return
     */
    public List<LocationDTO> getCityListByStateCode(String stateCode, String language) {
        return locationDTOMapper.getCityListByStateCode(stateCode, language);
    }
}
