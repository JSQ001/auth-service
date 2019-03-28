package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.mdata.location.domain.Location;
import com.hand.hcf.app.mdata.location.domain.LocationDetail;
import com.hand.hcf.app.mdata.location.domain.LocationDetailCode;
import com.hand.hcf.app.mdata.location.domain.VendorAlias;
import com.hand.hcf.app.mdata.location.dto.SolrLocationDTO;
import com.hand.hcf.app.mdata.location.persistence.LocationDetailCodeMapper;
import com.hand.hcf.app.mdata.location.persistence.LocationDetailMapper;
import com.hand.hcf.app.mdata.location.persistence.LocationMapper;
import com.hand.hcf.app.mdata.location.persistence.VendorAliasMapper;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.core.util.PageUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by markfredchen on 2016/12/6.
 */
@Service
public class LocationService {

    @Autowired
    LocationMapper locationMapper;
    @Autowired
    LocationDetailMapper locationDetailMapper;
    @Autowired
    VendorAliasMapper vendorAliasMapper;
    @Autowired
    LocationDetailCodeMapper locationDetailCodeMapper;
    @Autowired
    DtoService dtoService;

    @Autowired
    private MapperFacade mapperFacade;

    /**
     * Get Location By Code
     **/
    public List<Location> getLocationByCode(String code, String language) {
        List<Location> locationList = locationMapper.selectDeatailByCode(code, language);
        return locationList;
    }

    /**
     * Get location By Vendor Alias
     **/
    public List<Location> getLocationByVendorAlias(String vendorType, String alias, String language) {

        List<VendorAlias> vList = vendorAliasMapper.selectList(new EntityWrapper<VendorAlias>()
                .eq("vendor_type", vendorType)
                .eq("alias", alias)
                .eq("language", language)
                .orderBy("code", true));
        if (vList == null || vList.size() == 0) {
            return null;
        } else {
            List<Location> locationList = new ArrayList<>();
            vList.stream().forEach(u -> {
                Location location = locationMapper.selectList(new EntityWrapper<Location>().eq("code", u.getCode())).get(0);
                LocationDetail locationDetail = locationDetailMapper.selectList(new EntityWrapper<LocationDetail>()
                        .eq("code", u.getCode())
                        .eq("language", u.getLanguage())).get(0);
                location.setLocationDetailList(Arrays.asList(locationDetail));
                locationList.add(location);
            });
            return locationList;
        }
    }


    public List<Location> getLocationByType(String language, String locationType, String code) {
        List<Location> locationList = locationMapper.selectList(new EntityWrapper<Location>().eq("code", code).eq("type", locationType));
        if (locationList == null || locationList.size() == 0) {
            return null;
        } else {
            locationList.stream().forEach(u -> {
                List<LocationDetail> locationDetail = locationDetailMapper.selectList(new EntityWrapper<LocationDetail>()
                        .eq("code", code)
                        .eq("language", language));
                u.setLocationDetailList(locationDetail);
            });
            return locationList;
        }
    }

    public Location findOneByCode(String code) {
        List<Location> locations = locationMapper.selectList(new EntityWrapper<Location>().eq("code", code));
        if (CollectionUtils.isNotEmpty(locations)) {
            Location location = locations.get(0);
            List<VendorAlias> vendorAliases = vendorAliasMapper.selectList(
                    new EntityWrapper<VendorAlias>().eq("code", code))
                    .stream()
                    .map(u -> {
                        u.setLocation(location);
                        return u;
                    })
                    .collect(Collectors.toList());

            List<LocationDetailCode> locationDetailCodes = locationDetailCodeMapper.selectList(new EntityWrapper<LocationDetailCode>().eq("code", code));

            LocationDetailCode locationDetailCode = new LocationDetailCode();

            if (CollectionUtils.isNotEmpty(locationDetailCodes)) {
                locationDetailCode = locationDetailCodes.get(0);
            }
            LocationDetailCode finalLocationDetailCode = locationDetailCode;
            List<LocationDetail> locationDetailList = locationDetailMapper.selectList(
                    new EntityWrapper<LocationDetail>()
                            .eq("code", location.getCode()))
                    .stream()
                    .map(u -> {
                        u.setLocation(location);
                        u.setVendorAliasList(vendorAliases);
                        u.setLocationDetailCode(finalLocationDetailCode);
                        return u;
                    })
                    .collect(Collectors.toList());
            location.setLocationDetailList(locationDetailList);
            location.setVendorAliasList(vendorAliases);
            return location;
        }
        return null;
    }

    public Location findOneByCodeForResource(String code) {
        List<Location> locations = locationMapper.selectList(new EntityWrapper<Location>().eq("code", code));
        if (CollectionUtils.isNotEmpty(locations)) {
            Location location = locations.get(0);
            List<LocationDetail> locationDetailList = locationDetailMapper.selectList(
                    new EntityWrapper<LocationDetail>()
                            .eq("code", location.getCode()))
                    .stream()
                    .map(u -> {
                        u.setLocation(null);
                        u.setVendorAliasList(null);
                        return u;
                    })
                    .collect(Collectors.toList());
            location.setLocationDetailList(locationDetailList);
            location.setVendorAliasList(null);
            return location;
        }
        return null;
    }

    public List<Location> listLocation(String code) {
        List<Location> locations = locationMapper.selectList(
                new EntityWrapper<Location>()
                        .eq("code", code))
                .stream()
                .map(u -> {
                    List<VendorAlias> vendorAliases = vendorAliasMapper.selectList(
                            new EntityWrapper<VendorAlias>().eq("code", code));
                    u.setVendorAliasList(vendorAliases);
                    List<LocationDetail> locationDetailList = locationDetailMapper.selectList(
                            new EntityWrapper<LocationDetail>()
                                    .eq("code", u.getCode()));
                    u.setLocationDetailList(locationDetailList);
                    return u;
                })
                .collect(Collectors.toList());
        return locations;
    }

    public List<Location> listAll(Page page) {
        List<Location> locations = locationMapper.selectPage(page, null)
                .stream()
                .map(u -> {
                    List<VendorAlias> vendorAliases = vendorAliasMapper.selectList(
                            new EntityWrapper<VendorAlias>().eq("code", u.getCode()));
                    u.setVendorAliasList(vendorAliases);
                    List<LocationDetail> locationDetailList = locationDetailMapper.selectList(
                            new EntityWrapper<LocationDetail>()
                                    .eq("code", u.getCode()));
                    u.setLocationDetailList(locationDetailList);
                    return u;
                })
                .collect(Collectors.toList());
        return locations;
    }

    public List<Location> getAll(Page page) {
        return locationMapper.selectAll(page);
    }


    public LocationDTO toLocationDTO(SolrLocationDTO dto) {
        LocationDTO locationDTO = LocationDTO.builder()
                .language(dto.getLanguage())
                .code(dto.getCode())
                .type(dto.getType())
                .countryCode(dto.getCountryCode())
                .stateCode(dto.getStateCode())
                .cityCode(dto.getCityCode())
                .districtCode(dto.getDistrictCode())
                .country(dto.getCountry())
                .state(dto.getState())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .description(dto.getDescription())
                .vendorAlias(dto.getVendorAlias())
                .vendorCode(dto.getVendorCode())
                .vendorCountryCode(dto.getVendorCountryCode())//添加供应商国内国外识别码
                .build();
        return locationDTO;
    }

    public LocationDTO getLocationByCode(String language, String vendorType, String code) {
        SolrLocationDTO dto = new SolrLocationDTO();
        LocationDTO locationDTO = null;
        org.springframework.data.domain.Page<SolrLocationDTO> dtoPage = null;
        dto.setCode(code);
        dto.setLanguage(language);
        dto.setVendorType(vendorType);
        if (dtoPage == null || dtoPage.getContent().size() == 0) {
            locationDTO = dtoService.getLocationDTO(dto);
        } else {
            locationDTO = toLocationDTO(dtoPage.getContent().get(0));
        }
        return locationDTO;
    }


    public List<LocationDTO> getLocationsByCodes(List<String> codes, String language, String vendorType) {
        List<LocationDTO> result = new ArrayList<>();
        codes.stream().forEach(code -> {
            if (code != null) {
                SolrLocationDTO dto = new SolrLocationDTO();
                LocationDTO locationDTO = null;
                org.springframework.data.domain.Page<SolrLocationDTO> dtoPage = null;
                dto.setCode(code);
                dto.setLanguage(language);
                dto.setVendorType(vendorType);
                if (dtoPage == null || dtoPage.getContent().size() == 0) {
                    locationDTO = dtoService.getLocationDTO(dto);
                } else {
                    locationDTO = toLocationDTO(dtoPage.getContent().get(0));
                }
                result.add(locationDTO);
            }
        });
        return result;

    }

    public List<LocationDTO> searchLocation(String language, String country, String vendorType
            , String keyWord, int page, int size, String type) {
        List<LocationDTO> locationDTOs = new ArrayList<>();
        org.springframework.data.domain.Page<SolrLocationDTO> dtoPage = null;
        Pageable pageable = PageRequest.of(page, size);
        if (dtoPage == null || dtoPage.getContent().size() == 0) {
            Page pg = PageUtil.getPage(page, size);
            locationDTOs = dtoService.getLocationDTOByKey(keyWord, language, vendorType, country, pg, type);
        } else {
            for (SolrLocationDTO solrLocationDTO : dtoPage.getContent()) {
                LocationDTO locationDTO = toLocationDTO(solrLocationDTO);
                locationDTOs.add(locationDTO);
            }
        }
        return locationDTOs;
    }


    public List<LocationDTO> getCity(String vendor, String keyWord) {

        return searchLocation(
                LanguageEnum.ZH_CN.getKey(), null, vendor, keyWord, 0, 50, null);

    }

    public List<String> listCountryCode (){
        List<String> CountryCodeList = locationMapper.listCountryCode();
        return CountryCodeList;
    }

}
