package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.mdata.location.domain.Location;
import com.hand.hcf.app.mdata.location.domain.LocationDetail;
import com.hand.hcf.app.mdata.location.domain.VendorAlias;
import com.hand.hcf.app.mdata.location.dto.SolrLocationDTO;
import com.hand.hcf.app.mdata.location.persistence.LocationDetailMapper;
import com.hand.hcf.app.mdata.location.persistence.LocationMapper;
import com.hand.hcf.app.mdata.location.persistence.VendorAliasMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 16:21 2018/5/18
 * @Modified by
 */
@Service
public class DtoService {
    @Autowired
    VendorAliasMapper aliasMapper;

    @Autowired
    LocationMapper locationMapper;

    @Autowired
    LocationDetailMapper locationDetailMapper;

    public LocationDTO getLocationDTO(SolrLocationDTO dto){
        LocationDTO locationDTO = new LocationDTO();
        List<VendorAlias> vList = aliasMapper.selectList(new EntityWrapper<VendorAlias>()
                .eq("code",dto.getCode())
                .eq(dto.getVendorType() != null,"vendor_type",dto.getVendorType())
                .eq("language",dto.getLanguage())
                .orderBy("code",true));
        if (vList == null || vList.size() == 0){
            return null;
        }else{
            VendorAlias vendorAlias = vList.get(0);
            Location location = locationMapper.selectList(new EntityWrapper<Location>().eq("code",dto.getCode())).get(0);
            LocationDetail locationDetail = locationDetailMapper.selectList(new EntityWrapper<LocationDetail>()
                    .eq("code",dto.getCode())
                    .eq("language",dto.getLanguage())).get(0);
            return toLocationDTO(location,vendorAlias,locationDetail);
        }
    };

    private static LocationDTO toLocationDTO(Location location, VendorAlias vendorAlias, LocationDetail locationDetail){
        LocationDTO locationDTO = LocationDTO.builder()
                .language(vendorAlias.getLanguage())
                .code(location.getCode())
                .type(location.getType())
                .countryCode(location.getCountry_code())
                .stateCode(location.getState_code())
                .cityCode(location.getCity_code())
                .districtCode(location.getDistrict_code())
                .country(locationDetail == null ? null : locationDetail.getCountry())
                .state(locationDetail == null ? null : locationDetail.getState())
                .city(locationDetail == null ? null : locationDetail.getCity())
                .district(locationDetail == null ? null : locationDetail.getDistrict())
                .description(locationDetail == null ? null : locationDetail.getDescription())
                .vendorAlias(vendorAlias == null ? null : vendorAlias.getAlias().replace(" ", "").replace(" ", "").replace("，", ","))
                .vendorCode(vendorAlias == null ? null : vendorAlias.getVendorCode())
                .vendorCountryCode(vendorAlias == null ? null : vendorAlias.getVendorCountryCode())//添加供应商国内国外识别码
                .build();
        return locationDTO;
    }

    public List<LocationDTO> getLocationDTOByKey(String key, String language, String vendorType, String country, Page page, String type){
        List<LocationDTO> locationDTOS = new ArrayList<>();
        List<VendorAlias> vList = aliasMapper.selectEsByKey(page,key,new EntityWrapper<VendorAlias>()
                .eq("va.language",language)
                .eq("va.vendor_type",vendorType)
                .eq(!country.equals("all"),"ld.country",country)
                .eq(type != null,"l.type",type)
                .orderBy("va.code",true));
        if (vList == null || vList.size() == 0){
            return null;
        }else{
            vList.stream().forEach(vendorAlias -> {
                LocationDTO locationDTO = new LocationDTO();
                Location location = locationMapper.selectList(new EntityWrapper<Location>().eq("code",vendorAlias.getCode())).get(0);
                LocationDetail locationDetail = locationDetailMapper.selectList(new EntityWrapper<LocationDetail>()
                        .eq("code",vendorAlias.getCode())
                        .eq("language",vendorAlias.getLanguage())).get(0);
                locationDTOS.add(toLocationDTO(location,vendorAlias,locationDetail));
            });

            return locationDTOS;
        }
    };
}
