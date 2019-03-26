package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.dto.LocalizationDTO;
import com.hand.hcf.app.mdata.location.dto.AddressDTO;
import com.hand.hcf.app.mdata.location.persistence.LocalizationDTOMapper;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by silence on 2018/5/13.
 */
@Service
public class LocalizationDTOService {

    @Autowired
    private LocalizationDTOMapper localizationDTOMapper;



    /**
     * 分页查询 根据条件查询国家信息
     * @param countryCode 国家代码
     * @param countryName 国家名称
     * @param language 语言类型
     * @param includeList 筛选条件
     * @param page 分页对象
     * @return
     */
    public Page<LocalizationDTO> getLocalizationCountryByCode( String countryCode,
                                                               String countryName,
                                                               String language,
                                                               List<String> includeList,
                                                               Page<LocalizationDTO> page) {
        //  Code代码拼接
        String includeCode = "";
        //  准备拼接
        if (CollectionUtils.isNotEmpty(includeList)){
            for (int i = 0; i< includeList.size(); i++){
                if ( i == 0){
                    includeCode = includeCode + includeList.get(i);
                }else {
                    includeCode = includeCode + "," + includeList.get(i);
                }
            }
        }
        //  调用查询
        List<LocalizationDTO> localizationDTOList = localizationDTOMapper.getLocalizationCountryByCode(countryCode, countryName, language,includeCode,page);
        if(CollectionUtils.isNotEmpty(localizationDTOList)){page.setRecords(localizationDTOList);}
        return page;
    }

    /**
     * 分页查询 根据code vendorType language查询/省/直辖市信息
     * @param code
     * @param vendorType
     * @param language
     * @param page 分页对象
     * @return
     */
    public Page<LocalizationDTO> getLocalizationStateByCode(String code, String vendorType, String language, List<String> includeList, Page<LocalizationDTO> page) {
        //  Code代码拼接
        String includeCode = "";
        //  准备拼接
        if (CollectionUtils.isNotEmpty(includeList)){
            for (int i = 0; i< includeList.size(); i++){
                if ( i == 0){
                    includeCode = includeCode + includeList.get(i);
                }else {
                    includeCode = includeCode + "," + includeList.get(i);
                }
            }
        }
        //  调用查询
        List<LocalizationDTO> localizationDTOList = localizationDTOMapper.getLocalizationStateByCode(code,vendorType,language,includeCode,page);
        if(CollectionUtils.isNotEmpty(localizationDTOList)){
            page.setRecords(localizationDTOList);
        }
        return page;
    }

    /**
     * 分页查询 根据code vendorType language查询城市信息
     * @param code
     * @param vendorType
     * @param language
     * @param page 分页对象
     * @return
     */
    public Page<LocalizationDTO> getLocalizationCityByCode(String code, String vendorType, String language, List<String> includeList, Page<LocalizationDTO> page) {
        //  Code代码拼接
        String includeCode = "";
        //  准备拼接
        if (CollectionUtils.isNotEmpty(includeList)){
            for (int i = 0; i< includeList.size(); i++){
                if ( i == 0){
                    includeCode = includeCode + includeList.get(i);
                }else {
                    includeCode = includeCode + "," + includeList.get(i);
                }
            }
        }
        //  调用查询
        List<LocalizationDTO> localizationDTOList = localizationDTOMapper.getLocalizationCityByCode(code,vendorType,language,includeCode,page);
        if(CollectionUtils.isNotEmpty(localizationDTOList)){
            page.setRecords(localizationDTOList);
        }
        return page;
    }

    /**
     * 分页查询 根据code vendorType language查询地区信息
     * @param code
     * @param vendorType
     * @param language
     * @param page 分页对象
     * @return
     */
    public Page<LocalizationDTO> getLocalizationDistrictByCode(String code, String vendorType, String language, List<String> includeList, Page<LocalizationDTO> page) {
        //  Code代码拼接
        String includeCode = "";
        //  准备拼接
        if (CollectionUtils.isNotEmpty(includeList)){
            for (int i = 0; i< includeList.size(); i++){
                if ( i == 0){
                    includeCode = includeCode + includeList.get(i);
                }else {
                    includeCode = includeCode + "," + includeList.get(i);
                }
            }
        }
        //  调用查询
        List<LocalizationDTO> localizationDTOList = localizationDTOMapper.getLocalizationDistrictByCode(code,vendorType,language,includeCode,page);
        if(CollectionUtils.isNotEmpty(localizationDTOList)){
            page.setRecords(localizationDTOList);
        }
        return page;
    }

    //  优化后获取某国家下的所有地址信息
    public List<AddressDTO> getAddressDTOByCountry(String code, String vendorType, String language) {
        //  查询州/省/直辖市
        Pageable pageable = PageRequest.of(0, 9999);
        Page pg = PageUtil.getPage(pageable);
        Page<LocalizationDTO> page = this.getLocalizationStateByCode(code,vendorType,language,null,pg);
        List<LocalizationDTO> stateList = page.getRecords();
        //  查询城市
        List<LocalizationDTO> cityList = this.getLocalizationCityByCountry(code,vendorType,language);
        //  查询地区
        List<LocalizationDTO> districtList = this.getLocalizationDistrictByCountry(code,vendorType,language);
        //  遍历省
        List<AddressDTO> resultList = new ArrayList<>();
        stateList.stream().forEach( (LocalizationDTO state) -> {
            //  封装省
            AddressDTO stateDTO = new AddressDTO();
            stateDTO.setLabel(state.getState());
            stateDTO.setValue(state.getCode()+"-"+state.getState());
            // 封装城市
            List<AddressDTO> cityDTOList = new ArrayList<>();
            cityList.stream().forEach( (LocalizationDTO city) -> {
                if (state.getCode().substring(0,7).equals(city.getCode().substring(0,7))){
                    AddressDTO cityDTO = new AddressDTO();
                    cityDTO.setLabel(city.getCity());
                    cityDTO.setValue(city.getCode()+"-"+city.getCity());
                    // 封装地区
                    List<AddressDTO> disDTOList = new ArrayList<>();
                    districtList.stream().forEach( (LocalizationDTO district) -> {
                        if ( city.getCode().substring(0,10).equals(district.getCode().substring(0,10)) ){
                            AddressDTO districtDTO = new AddressDTO();
                            districtDTO.setLabel(district.getDistrict());
                            districtDTO.setValue(district.getCode()+"-"+district.getDistrict());
                            disDTOList.add(districtDTO);
                        }
                    });
                    // setChildren
                    if (disDTOList.size() != 0){
                        cityDTO.setChildren(disDTOList);
                    }
                    cityDTOList.add(cityDTO);
                }
            });
            // setChildren
            stateDTO.setChildren(cityDTOList);
            resultList.add(stateDTO);
        });
        return resultList;
    }

    //  新查询城市
    public List<LocalizationDTO> getLocalizationCityByCountry(String code,String vendorType,String language) {
        List<LocalizationDTO> list = localizationDTOMapper.getLocalizationCityByCountry(code,vendorType,language);


        return list.stream().map(u-> {u.setVendorType(vendorType);return u;}).collect(Collectors.toList());
    }

    //  新查询地区
    public List<LocalizationDTO> getLocalizationDistrictByCountry(String code, String vendorType, String language) {
        List<LocalizationDTO> list = localizationDTOMapper.selectLocalizationDistrictByCountry(code, vendorType, language);


        return  list.stream().map(u-> {u.setVendorType(vendorType);return u;}).collect(Collectors.toList());
    }
    public LocalizationDTO getOneLocalizationCountryByCode(String language,String CountryCode){
        LocalizationDTO localizationDTO =localizationDTOMapper.getOneLocalizationCountryByCode(language,CountryCode);
        return localizationDTO;
    }

}
