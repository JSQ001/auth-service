package com.hand.hcf.app.mdata.location.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.dto.LocalizationDTO;
import com.hand.hcf.app.common.dto.LocalizationStateDTO;
import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.location.service.LocalizationDTOService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/9/11
 */
@RestController
@RequestMapping("/api")
public class LocalizationController {

    @Autowired
    private LocalizationDTOService localizationDTOService;

    /**
     *  根据code查询国家信息
     * @param countryCode 国家代码
     * @param countryName 国家名称
     * @param includeList 筛选条件
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/localization/query/country", method = RequestMethod.GET)
    public ResponseEntity<List<LocalizationDTO>> getLocalizationCountryByCode(@RequestParam(required = false) String countryCode,
                                                                              @RequestParam(required = false) String countryName,
                                                                              @RequestParam(required = false) List<String> includeList,
                                                                              @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                                              @RequestParam(value = "size", required = false,defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        Page mybatisPage = PageUtil.getPage(pageable);
        String language= OrgInformationUtil.getCurrentLanguage();
        Page<LocalizationDTO> result = localizationDTOService.getLocalizationCountryByCode(countryCode, countryName, language,includeList,mybatisPage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/localization/query/country");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据code vendorType language查询/省/直辖市信息
     * @param code
     * @param vendorType
     * @return
     */
    @RequestMapping(value = "/localization/query/state", method = RequestMethod.GET)
    public ResponseEntity<List<LocalizationDTO>> getLocalizationStateByCode(@RequestParam(value = "code")String code,
                                                                            @RequestParam(value = "vendorType",defaultValue = "standard")String vendorType,
                                                                            @RequestParam(required = false) List<String> includeList,
                                                                            @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                                            @RequestParam(value = "size", required = false,defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        Page mybatisPage = PageUtil.getPage(pageable);
        String language= OrgInformationUtil.getCurrentLanguage();
        Page<LocalizationDTO> result = localizationDTOService.getLocalizationStateByCode(code,vendorType,language,includeList,mybatisPage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/localization/query/state");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据code vendorType language查询城市信息
     * @param code
     * @param vendorType
     * @return
     */
    @RequestMapping(value = "/localization/query/city", method = RequestMethod.GET)
    public ResponseEntity<List<LocalizationDTO>> getLocalizationCityByCode(@RequestParam(value = "code")String code,
                                                                           @RequestParam(value = "vendorType",defaultValue = "standard")String vendorType,
                                                                           @RequestParam(required = false) List<String> includeList,
                                                                           @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                                           @RequestParam(value = "size", required = false,defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        Page mybatisPage = PageUtil.getPage(pageable);
        String language= OrgInformationUtil.getCurrentLanguage();
        Page<LocalizationDTO> result = localizationDTOService.getLocalizationCityByCode(code,vendorType,language,includeList,mybatisPage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/localization/query/city");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据code vendorType language查询地区信息
     * @param code
     * @param vendorType
     * @return
     */
    @RequestMapping(value = "/localization/query/district", method = RequestMethod.GET)
    public ResponseEntity<List<LocalizationDTO>> getLocalizationDistrictByCode(@RequestParam(value = "code")String code,
                                                                               @RequestParam(value = "vendorType",defaultValue = "standard")String vendorType,
                                                                               @RequestParam(required = false) List<String> includeList,
                                                                               @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                                               @RequestParam(value = "size", required = false,defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        Page mybatisPage = PageUtil.getPage(pageable);
        String language= OrgInformationUtil.getCurrentLanguage();
        Page<LocalizationDTO> result = localizationDTOService.getLocalizationDistrictByCode(code,vendorType,language,includeList,mybatisPage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/localization/query/district");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据code vendorType language查询所有省和城市信息
     * @param code
     * @param vendorType
     * @return
     */
    @RequestMapping(value = "/localization/query/stateAndCity", method = RequestMethod.GET)
    public ResponseEntity<List<LocalizationStateDTO>> getLocalizationStateAndCityByCode(@RequestParam(value = "code")String code,
                                                                                        @RequestParam(value = "vendorType",defaultValue = "standard")String vendorType,
                                                                                        @RequestParam(required = false) List<String> includeList,
                                                                                        @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                                                        @RequestParam(value = "size", required = false,defaultValue = "1000") int size){
        Pageable pageable = PageRequest.of(page,size);
        Page mybatisPage = PageUtil.getPage(pageable);
        String language= OrgInformationUtil.getCurrentLanguage();
        Page<LocalizationDTO> result = localizationDTOService.getLocalizationStateByCode(code,vendorType,language,includeList,mybatisPage);
        List<LocalizationStateDTO> stateDTOS = new ArrayList<>();
        LocalizationStateDTO stateDTO;
        LocalizationDTO state;
        for(int i = 0; i < result.getRecords().size(); i++){
            state = result.getRecords().get(i);
            stateDTO = new LocalizationStateDTO();
            stateDTO.setCity(state.getCity());
            stateDTO.setCode(state.getCode());
            stateDTO.setCountry(state.getCountry());
            stateDTO.setDistrict(state.getDistrict());
            stateDTO.setState(state.getState());
            stateDTO.setType(state.getType());
            stateDTO.setVendorType(state.getVendorType());
            stateDTOS.add(stateDTO);
        }
        for(int i = 0; i < stateDTOS.size(); i++){
            stateDTO = stateDTOS.get(i);
            if(stateDTO.getCode().equals(code)){
                stateDTO.setChildren(new ArrayList<>());
            } else {
                Page<LocalizationDTO> citys = localizationDTOService.getLocalizationCityByCode(stateDTO.getCode(), vendorType, language, null, mybatisPage);
                stateDTO.setChildren(citys.getRecords());
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link","/api/localization/query/stateAndCity");
        return new ResponseEntity<>(stateDTOS, headers, HttpStatus.OK);
    }
    /**
     * @api {GET} /api/localization/city/query 根据国家代码获取所有城市
     * @apiDescription  根据国家代码获取所有城市
     * @apiGroup Localization
     * @apiParam {String}  code 国家代码
     * @apiParam {String} city 国家名称
     * @apiParamExample {json} Request-Param:
     *      http://localhost:8000/mdata/api/localization/city/query?page=0&size=10&code=CHN000000000&roleType=TENANT
     * @apiSuccessExample {json} Success-Response:
     * [
        {
        "id": "51517",
        "code": "CHN011001000",
        "type": "CITY",
        "country": "中国",
        "state": "北京",
        "city": "东城",
        "district": null,
        "vendorType": "standard"
        }
    ]
     */
    @GetMapping("/localization/city/query")
    public ResponseEntity<List<LocalizationDTO>> pageLocalizationCityByCode(@RequestParam(value = "code")String code,
                                                                            @RequestParam(value = "city",required = false) String city,
                                                                            @RequestParam(value = "vendorType",defaultValue = "standard")String vendorType,
                                                                            @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                                            @RequestParam(value = "size", required = false,defaultValue = "1000") int size){

        String language= OrgInformationUtil.getCurrentLanguage();
        Page mybatisPage = PageUtil.getPage(page,size);
        List<LocalizationDTO> cityList = localizationDTOService.getLocalizationCityByCountry(code,city,vendorType,language);
        mybatisPage.setRecords(cityList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + mybatisPage.getTotal());
        headers.add("Link","/api/localization/query/stateAndCity");
        return new ResponseEntity<>(cityList, headers, HttpStatus.OK);
    }
}
