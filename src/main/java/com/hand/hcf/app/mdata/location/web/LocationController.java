package com.hand.hcf.app.mdata.location.web;

import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.location.domain.LocationDetail;
import com.hand.hcf.app.mdata.location.dto.LocationInfoDTO;
import com.hand.hcf.app.mdata.location.dto.SolrLocationDTO;
import com.hand.hcf.app.mdata.location.service.DtoService;
import com.hand.hcf.app.mdata.location.service.LocationDetailService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
 * @Date: 2018/9/17
 */
@RestController
@RequestMapping("/api")
public class LocationController {

    @Autowired
    DtoService dtoService;
    @Autowired
    private LocationDetailService locationDetailService;
    /**
     *标准城市搜索（带权重分词）
     */
    @GetMapping(value = "/location/search")
    public ResponseEntity<List<LocationDTO>> searchBySolr(
            @RequestParam(value = "country", required = false, defaultValue = "all") String country,
            @RequestParam(value = "vendorType", required = false,defaultValue = "standard") String vendorType,
            @RequestParam(value = "keyWord") String keyWord,
            @RequestParam(value = "page", required = false,defaultValue = "0") int page,
            @RequestParam(value = "size", required = false,defaultValue = "50") int size,
            @RequestParam(value = "type", required = false) String type) {
        List<LocationDTO> locationDTOs = new ArrayList<>();
        Page<SolrLocationDTO> dtoPage = null;
        Pageable pageable = PageRequest.of(page,size);
        String language= OrgInformationUtil.getCurrentLanguage();
        if (dtoPage == null || dtoPage.getContent().size() == 0){
            com.baomidou.mybatisplus.plugins.Page pg = PageUtil.getPage(page,size);
            locationDTOs = dtoService.getLocationDTOByKey(keyWord, language, vendorType, country, pg,type);
        }else{
            for (SolrLocationDTO solrLocationDTO : dtoPage.getContent()) {
                LocationDTO locationDTO = toLocationDTO(solrLocationDTO);
                locationDTOs.add(locationDTO);
            }
        }
        return ResponseEntity.ok(locationDTOs == null ? new ArrayList<>() :locationDTOs);
    }

    private static LocationDTO toLocationDTO(SolrLocationDTO dto){
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

    /**
     * 模糊查询地点
     */
    @GetMapping("/location/search/cities")
    public ResponseEntity<List<LocationInfoDTO>> queryCities(@RequestParam(required = false) String description,
                                                             @RequestParam(required = false) Long id,
                                                             @RequestParam(required = false) String code,
                                                             Pageable pageable){
        com.baomidou.mybatisplus.plugins.Page page = PageUtil.getPage(pageable);
        List<LocationInfoDTO> result = locationDetailService.listCityByDescription(description, id, code, page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return  new ResponseEntity(result,httpHeaders, HttpStatus.OK);
    }

    @ApiOperation(value = "查询城市", notes = "根据id集合查询城市")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "ids", value = "城市ID集合", required = true, dataType = "List")
    })
    @PostMapping("/location/city/ids")
    public List<LocationDetail> listLocationCityByIds(@RequestBody List<Long> ids){

        List<LocationDetail> cityList = locationDetailService.selectBatchIds(ids);
        return cityList;
    }
}
