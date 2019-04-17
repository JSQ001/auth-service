package com.hand.hcf.app.mdata.location.service;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.location.domain.LocationDetail;
import com.hand.hcf.app.mdata.location.dto.LocationInfoDTO;
import com.hand.hcf.app.mdata.location.persistence.LocationDetailMapper;
import com.hand.hcf.app.mdata.location.persistence.LocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vance on 2017/3/5.
 */
@Service
public class LocationDetailService {



    @Autowired
    LocationDetailMapper locationDetailMapper;

    @Autowired
    LocationMapper locationMapper;

    public String getNextLocaitonType() {
        return nextLocaitonType;
    }

    public void setNextLocaitonType(String nextLocaitonType) {
        this.nextLocaitonType = nextLocaitonType;
    }

    private String nextLocaitonType = "";

    public LocationDetail findLocatinoDetail(String locationCode, String language) {
        LocationDetail locationDetail = locationDetailMapper.findByCode(locationCode,language);
        if (locationDetail == null) {
            throw new ObjectNotFoundException(LocationDetail.class, locationCode);
        }
        return locationDetail;
    }

    public List<LocationDetail> findCascade(LocationDetail locationDetail, String vendorType, String language, String baseCountry, String locationCode) {
        List<LocationDetail> locationDetails = new ArrayList<>();

        String type = locationDetail.getLocation().getType();
        if (type.equals("COUNTRY")) {
            locationDetails = locationDetailMapper.findByLocationTypeAndLanguage(
                    language,
                    locationDetail.getLocation().getCountry_code(),
                    type,
                    vendorType,
                    "",
                    "");
        } else if (type.equals("STATE")) {
            locationDetails = locationDetailMapper.findByLocationTypeAndLanguage(
                    language,
                    locationDetail.getLocation().getCountry_code(),
                    type,
                    vendorType,
                    "",
                    "");
        } else if (type.equals("CITY")) {
            locationDetails = locationDetailMapper.findByLocationTypeAndLanguage(
                    language,
                    locationDetail.getLocation().getCountry_code(),
                    type,
                    vendorType,
                    locationDetail.getLocation().getState_code(),
                    "");
        } else if (type.equals("REGION")) {
            locationDetails = locationDetailMapper.findByLocationTypeAndLanguage(
                    language,
                    locationDetail.getLocation().getCountry_code(),
                    type,
                    vendorType,
                    locationDetail.getLocation().getState_code(),
                    locationDetail.getLocation().getCity_code());
        }
        return locationDetails;
    }

    public List<LocationInfoDTO> listCityByDescription(String description,
                                                       Long id,
                                                       String code,
                                                       Page page) {
        String currentLanguage = OrgInformationUtil.getCurrentLanguage();
        if (!StringUtils.hasText(currentLanguage)){
            currentLanguage = LanguageEnum.ZH_CN.getKey();
        }
        return locationDetailMapper.listCityByDescription(description, id, code, currentLanguage, page);
    }
}
