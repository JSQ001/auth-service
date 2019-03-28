package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.hand.hcf.app.mdata.location.domain.LocationDetail;
import com.hand.hcf.app.mdata.location.domain.LocationDetailCode;
import com.hand.hcf.app.mdata.location.dto.CustomLocationDetailCodeDTO;
import com.hand.hcf.app.mdata.location.dto.CustomLocationDetailDTO;
import com.hand.hcf.app.mdata.location.dto.LocationDetailCodeDTO;
import com.hand.hcf.app.mdata.location.persistence.LocationDetailCodeMapper;
import com.hand.hcf.app.mdata.location.persistence.LocationDetailMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vance on 2017/3/20.
 */
@Service
public class CustomLocationDetailCodeService {

    private ConcurrentHashMap<String, List<LocationDetailCodeDTO>> resultMap = new ConcurrentHashMap<>();

    private static String[] municipalities = new String[]{"上海", "北京", "澳门", "天津", "香港", "重庆"};


    @Autowired
    LocationDetailMapper locationDetailMapper;
    @Autowired
    LocationDetailCodeMapper locationDetailCodeMapper;

    public List<CustomLocationDetailCodeDTO> findEntireLocationDetailCode(String baseCountry, String locationType, String vendorType, String language) {
        List<LocationDetailCode> locationDetailCodeList = new ArrayList<>();
        List<LocationDetail> locationDetails = new ArrayList<>();

        locationDetails = locationDetailMapper.findMunicipalities("STATE", vendorType, Arrays.asList(municipalities), language);

        locationDetails.addAll(locationDetailMapper.findNameWithoutMunicipalitiesAndVendorType(locationType, vendorType, Arrays.asList(municipalities), language));

        if (vendorType.equals("standard")) {
            locationDetails.addAll(locationDetailMapper.findNameWithoutMunicipalitiesAndVendorType("REGION", vendorType, Arrays.asList(municipalities), language));
        }
        locationDetails.stream().forEach((c) -> {
            List<LocationDetailCode> list = locationDetailCodeMapper.selectList(new EntityWrapper<LocationDetailCode>().eq("code", c.getCode()));
            LocationDetailCode locationDetailCode = null;
            if (list != null && list.size() > 0) {
                locationDetailCode = list.get(0);
                locationDetailCode.setLocationDetail(c);
            }

            locationDetailCodeList.add(locationDetailCode);
        });

        List<CustomLocationDetailCodeDTO> customLocationDetailCodeDTOS = new ArrayList<>();
        for (LocationDetailCode locationDetailCode : locationDetailCodeList) {
            try {
                LocationDetailCodeDTO locationDetailCodeDTO = new LocationDetailCodeDTO();
                BeanUtils.copyProperties(locationDetailCode, locationDetailCodeDTO);
                String firstLetter = PinyinHelper.getShortPinyin(locationDetailCode.getLocationDetail().getVendorAliasList().get(0).getAlias()).substring(0, 1).toLowerCase();

//                String firstLetter = (locationDetailCode.getCity_pinyin() == null || locationDetailCode.getCity_pinyin().isEmpty())
//                        ? locationDetailCode.getState_pinyin().substring(0, 1)
//                        : locationDetailCode.getCity_pinyin().substring(0, 1);

                if (!resultMap.containsKey(firstLetter)) {
                    List<LocationDetailCodeDTO> _locationDetailCodeDTOList = new ArrayList<>();
                    _locationDetailCodeDTOList.add(locationDetailCodeDTO);
                    resultMap.put(firstLetter, _locationDetailCodeDTOList);
                } else {
                    resultMap.get(firstLetter).add(locationDetailCodeDTO);
                }

            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (PinyinException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        resultMap.forEach((key, result) ->
        {
            CustomLocationDetailCodeDTO customLocationDetailCodeDTO = new CustomLocationDetailCodeDTO();
            customLocationDetailCodeDTO.setAlphabet(key);
            List<CustomLocationDetailDTO> customLocationDetailDTOList = new ArrayList<>();

            result.stream().forEach((c) -> {
                CustomLocationDetailDTO customLocationDetailDTO = new CustomLocationDetailDTO();
                final boolean[] ignore = {false};

                customLocationDetailDTOList.forEach((a) -> {
                    if (a.getCode().equals(c.getCode())) {
                        ignore[0] = true;
                    }
                });

                try {
                    customLocationDetailDTO.setCode(c.getCode());
                    customLocationDetailDTO.setLanguage(c.getLocationDetail().getLanguage());
                    customLocationDetailDTO.setCountry(c.getLocationDetail().getCountry());
                    customLocationDetailDTO.setState(c.getLocationDetail().getState());
                    customLocationDetailDTO.setDistrict(c.getLocationDetail().getDistrict());
                    customLocationDetailDTO.setDescription(c.getLocationDetail().getDescription());
                    customLocationDetailDTO.setAbbreviation(c.getLocationDetail().getAbbreviation());
                    customLocationDetailDTO.setCountry_pinyin(c.getCountry_pinyin());
                    customLocationDetailDTO.setCountry_code(c.getCountry_code());
                    customLocationDetailDTO.setState_pinyin(c.getState_pinyin());
                    customLocationDetailDTO.setCity_pinyin(c.getCity_pinyin());
                    customLocationDetailDTO.setCity(c.getLocationDetail().getCity());
                    customLocationDetailDTO.setAliasName(c.getLocationDetail().getVendorAliasList().get(0).getAlias());

                    if (!ignore[0]) {
                        customLocationDetailDTOList.add(customLocationDetailDTO);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            });

            customLocationDetailCodeDTO.setCustomLocationDetailDTOS(customLocationDetailDTOList);
            customLocationDetailCodeDTOS.add(customLocationDetailCodeDTO);
        });

        for (CustomLocationDetailCodeDTO c : customLocationDetailCodeDTOS) {
            List<CustomLocationDetailDTO> tempList = c.getCustomLocationDetailDTOS();
            Collections.sort(tempList, new Comparator<CustomLocationDetailDTO>() {
                @Override
                public int compare(CustomLocationDetailDTO c1, CustomLocationDetailDTO c2) {
                    return Double.compare(c1.getAliasName().length(), c2.getAliasName().length());
                }
            });
            c.setCustomLocationDetailDTOS(tempList);
        }
        return customLocationDetailCodeDTOS;
    }
}
