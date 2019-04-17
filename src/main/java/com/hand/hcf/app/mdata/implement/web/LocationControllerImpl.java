package com.hand.hcf.app.mdata.implement.web;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.hand.hcf.app.common.co.LocationLevelCO;
import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.common.dto.VendorAliasDTO;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.location.adapter.Adapter;
import com.hand.hcf.app.mdata.location.adapter.LocationAdapter;
import com.hand.hcf.app.mdata.location.domain.*;
import com.hand.hcf.app.mdata.location.dto.*;
import com.hand.hcf.app.mdata.location.enums.LocationType;
import com.hand.hcf.app.mdata.location.enums.VendorTypeEnum;
import com.hand.hcf.app.mdata.location.persistence.AirportCodeMapper;
import com.hand.hcf.app.mdata.location.persistence.LocationDetailMapper;
import com.hand.hcf.app.mdata.location.service.*;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vance.zhao on 16/10/28.
 */

@RestController
public class LocationControllerImpl {

    @Autowired
    LocationService locationService;


    @Autowired
    LocationDetailService locationDetailService;

    @Autowired
    VendorAliasService vendorAliasService;

    @Autowired
    CustomLocationDetailCodeService customLocationDetailCodeService;

    @Autowired
    LocationDBService locationDBService;

    @Autowired
    DtoService dtoService;

    @Autowired
    LocationDetailMapper locationDetailMapper;

    @Autowired
    AirportCodeMapper airportCodeMapper;
    @Autowired
    LocalizationDTOService localizationDTOService;

    @Autowired
    private LocationLevelService locationLevelService;
    @Autowired
    private MapperFacade mapperFacade;

    @RequestMapping(value = "/one/location", method = RequestMethod.GET)
    public ResponseEntity<Location> getOneLocation(@RequestParam("code") String code) {
        Location location = locationService.findOneByCodeForResource(code);
        return ResponseEntity.ok(location);
    }

    @RequestMapping(value = "/locations/transfer", method = RequestMethod.GET)
    public ResponseEntity<List<VendorAlias>> getVendorAliasByCode(
            @RequestParam(value = "from") String from,
            @RequestParam(value = "to") String to,
            @RequestParam(value = "city") String city) {

        List<VendorAlias> res = vendorAliasService.getVendorAliasByAliasAndVendorType(OrgInformationUtil.getCurrentLanguage(), from, to, city);
        return new ResponseEntity(res, HttpStatus.OK);
    }

    /**
     *
     */
    //@RequestMapping(value = "/locations/{code}", method = RequestMethod.GET)
    public LocationDTO getLocationByCode(
                                         @RequestParam(value = "vendorType", required = false, defaultValue = "standard") String vendorType,
                                         @PathVariable("code") String code) throws Exception {
        return locationService.getLocationByCode(OrgInformationUtil.getCurrentLanguage(), vendorType, code);

    }

    //@PostMapping(value = "/locations/codes")
    public ResponseEntity<List<LocationDTO>> getLocationByCodes(
            @RequestBody List<String> codes,
            @RequestParam(value = "vendorType", required = false, defaultValue = "standard") String vendorType) throws Exception {

        List<LocationDTO> result = new ArrayList<>();
        result = locationService.getLocationsByCodes(codes, OrgInformationUtil.getCurrentLanguage(), vendorType);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    private List<LocationDTO> convertLocationDto(List<Location> locationList, String language) {
        List<LocationDTO> locationDTOList = new ArrayList<>();
        for (Location location : locationList) {
            Adapter locationAdapter = new LocationAdapter(location, language);
            LocationDTO locationDTO = (LocationDTO) locationAdapter.convertToDTO();
            locationDTOList.add(locationDTO);
        }
        return locationDTOList;
    }


    //@RequestMapping(value = "/location/aliases/{vendorType}/{alias}", method = RequestMethod.GET)

    public LocationDTO getLocationByVendorAlias(@PathVariable("vendorType") String vendorType, @PathVariable("alias") String alias) {
        String language= OrgInformationUtil.getCurrentLanguage();
        List<Location> locationList = locationService.getLocationByVendorAlias(vendorType, alias, language);
        String message;

        if (locationList.size() == 0) {
            message = "Vendor Type = " + vendorType + " and Vendor Alias = " + alias + " and Language = " + language + " not found";
            throw new ObjectNotFoundException(LocationControllerImpl.class, message);
        }
        locationList = locationList.stream().distinct().collect(Collectors.toList());
        List<LocationDTO> locationDTOList = convertLocationDto(locationList, language);
        if (locationDTOList.size() == 0) {
            message = "Vendor Type = " + vendorType + " and Vendor Alias = " + alias + " and Language = " + language + " not found";
            throw new ObjectNotFoundException(LocationControllerImpl.class, message);
        }
        return locationDTOList.get(0);
    }


    //RequestBody ExpenseReportDTO expenseReport

    /**
     *
     */
    @RequestMapping(value = "/location/alias", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public ResponseEntity<CreateVendorAliasResponseDTO> createLocationAlias(@RequestBody @Valid VendorAliasDTO vendorAliasDTO) {
        String vendorType = vendorAliasDTO.getVendorType();
        String alias = vendorAliasDTO.getAlias();
        String language = vendorAliasDTO.getLanguage();
        String code = vendorAliasDTO.getCode();

        List<Location> locationList = locationService.getLocationByCode(code, language);
        if (locationList.size() == 0) {
            throw new ObjectNotFoundException(LocationControllerImpl.class, code, language);
        }

        vendorAliasService.createVendorAlias(code, vendorType, alias);
        CreateVendorAliasResponseDTO createVendorAliasReponseDTO = new CreateVendorAliasResponseDTO();
        createVendorAliasReponseDTO.setCode(code);

        return new ResponseEntity(createVendorAliasReponseDTO, HttpStatus.OK);
    }

    /**
     *
     */
    @RequestMapping(value = "/location/details", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ResponseEntity<HashSet<LocationDetail>> getLocationDetailByLocationType(
            @RequestParam(value = "baseCountry") String baseCountry,
            @RequestParam("vendorType") String vendorType,
            @RequestParam(value = "locationType") String locationType,
            @PageableDefault(value = 10, sort = {"id"}, direction = Sort.Direction.DESC)
                    Pageable pageable) {
        String language= OrgInformationUtil.getCurrentLanguage();
        List<LocationDetail> locationDetails = null;
        if (baseCountry.equals("domestic")) {
            if (locationType.equals("STATE") && vendorType.equals(VendorTypeEnum.Ctrip_Air.getVendorTypeName())) {
                locationDetails = locationDetailMapper.findByDomesticByVendorTypeAndLocationType("CHN", "CITY", vendorType);

                HashSet<String> stateNames = new HashSet<>();
                for (LocationDetail locationDetail : locationDetails) {
                    stateNames.add(locationDetail.getState());
                }
                locationDetails = locationDetailMapper.findByStateName("CHN", locationType, stateNames);
            } else {

                locationDetails = locationDetailMapper.findByStateName("CHN", locationType, null);
            }
        } else if (baseCountry.equals("foreign")) {
            locationDetails = locationDetailMapper.findByForeignByVendorType("CNH", locationType, vendorType);
        }

        if (null == locationDetails || locationDetails.size() == 0) {
            throw new ObjectNotFoundException(LocationDetail.class, locationType);
        }
        List<LocationDetailDTO> locationDetailDTOS = new ArrayList<>();
        for (LocationDetail locationDetail : locationDetails) {
            LocationDetailDTO locationDetailDTO = new LocationDetailDTO();
            BeanUtils.copyProperties(locationDetail, locationDetailDTO);
            try {
                String pinyin = PinyinHelper.getShortPinyin(baseCountry.equals("domestic") ? locationDetail.getState() : locationDetail.getCountry());
                locationDetailDTO.setShortPinyin(pinyin);
                locationDetailDTOS.add(locationDetailDTO);
            } catch (PinyinException e) {
                e.printStackTrace();
            }
        }
        Comparator<LocationDetailDTO> comparator = Comparator.comparing(LocationDetailDTO::getShortPinyin);
        Collections.sort(locationDetailDTOS, comparator);


        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = locationDetailDTOS.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element)) {
                newList.add(element);
            }
        }
        locationDetailDTOS.clear();
        locationDetailDTOS.addAll(newList);

        return new ResponseEntity(locationDetailDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/location/children/{locationCode}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ResponseEntity<List<LocationDetail>> getCascadedLocationDetail(
            @RequestParam(value = "baseCountry") String baseCountry,
            @RequestHeader("vendorType") String vendorType,
            @PathVariable("locationCode") String locationCode,
            @PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC)
                    Pageable pageable) {
        if (locationCode == null) {
            throw new ObjectNotFoundException(LocationDetail.class, locationCode);
        }
        String language= OrgInformationUtil.getCurrentLanguage();
        List<LocationDetailDTO> locationDetailDTOS = new ArrayList<>();
        // find current code detail
        LocationDetail locationDetail = locationDetailService.findLocatinoDetail(locationCode, language);

        String nextLocaitonTypeLevel = LocationType.getNextLocationType(locationDetail.getLocation().getType());
        locationDetail.getLocation().setType(nextLocaitonTypeLevel);
        List<LocationDetail> locationDetails = new ArrayList<>();

        // check the state
        if (nextLocaitonTypeLevel.equals("STATE")) {
            locationDetails = locationDetailMapper.findByLocationTypeAndLanguage(language, locationDetail.getLocation().getCountry_code(), "CITY", vendorType, "", "");
            HashSet<String> stateNames = new HashSet<>();
            for (LocationDetail ld : locationDetails) {
                stateNames.add(ld.getState());
            }
            locationDetails = locationDetailMapper.findByStateNameLanguage(locationDetail.getLocation().getCountry_code(), nextLocaitonTypeLevel, stateNames, language);
        } else {
            locationDetails = locationDetailService.findCascade(locationDetail, vendorType, language, baseCountry, locationCode);
        }

        //if locationDetails is null then find next level
        while (nextLocaitonTypeLevel != LocationType.locationStack.getLast() && locationDetails.size() == 0) {
            nextLocaitonTypeLevel = LocationType.getNextLocationType(nextLocaitonTypeLevel);
            locationDetail.getLocation().setType(nextLocaitonTypeLevel);
            locationDetails = locationDetailService.findCascade(locationDetail, vendorType, language, baseCountry, locationCode);
        }
        if (null != locationDetails && locationDetails.size() > 0) {
            for (LocationDetail ld : locationDetails) {
                LocationDetailDTO locationDetailDTO = new LocationDetailDTO();
                BeanUtils.copyProperties(ld, locationDetailDTO);
                locationDetailDTOS.add(locationDetailDTO);
            }
        }
        return new ResponseEntity(locationDetailDTOS, HttpStatus.OK);
    }

    /**
     *
     */
    /*  @RequestMapping(value = "/location/aliases/{vendorType}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")*/
    public VendorAliasDTO getVendorAliasByCode(
            @PathVariable("vendorType") String vendorType,
            @RequestParam(value = "code") String code,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        VendorAliasDTO vendorAliasDTO = new VendorAliasDTO();
        String language= OrgInformationUtil.getCurrentLanguage();
        VendorAlias vendorAlias = vendorAliasService.getSingleVendorAliasBy(code, vendorType, language);
        BeanUtils.copyProperties(vendorAlias, vendorAliasDTO);
        return vendorAliasDTO;
    }

    /**
     *
     */
    @RequestMapping(value = "/location/alphabet/{locationType}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public ResponseEntity<List<CustomLocationDetailCodeDTO>> getLocationDetailByAlphabet(
            @RequestParam(value = "baseCountry") String baseCountry,
            @PathVariable(value = "locationType") String locationType,
            @RequestHeader("vendorType") String vendorType,
            @PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC)
                    Pageable pageable) {

        if (vendorType.equals("ctrip_train")) {
            vendorType = "standard";
        }
        String language= OrgInformationUtil.getCurrentLanguage();
        List<CustomLocationDetailCodeDTO> customLocationDetailCodeDTOS
                = customLocationDetailCodeService.findEntireLocationDetailCode(baseCountry, locationType, vendorType, language);
        return new ResponseEntity<>(customLocationDetailCodeDTOS, HttpStatus.ACCEPTED);

    }


    /**
     * 标准城市搜索（带权重分词）
     */
    //@GetMapping(value = "/location/search")
    public List<LocationDTO> searchBySolr(
            @RequestParam(value = "country", required = false, defaultValue = "all") String country,
            @RequestParam(value = "vendorType", required = false, defaultValue = "standard") String vendorType,
            @RequestParam(value = "keyWord") String keyWord,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "50") int size,
            @RequestParam(value = "type", required = false) String type) {
        String language= OrgInformationUtil.getCurrentLanguage();
        return locationService.searchLocation(language,
                country,
                vendorType,
                keyWord,
                page,
                size,
                type);
    }

    /**
     * 标准城市搜索（根据code排序）
     *
     * @return
     */
    //@GetMapping(value = "/city/search")
    public ResponseEntity<List<LocationDTO>> searchBySolrNoAnaliaze(
            @RequestParam(value = "vendorType", required = false, defaultValue = "standard") String vendorType,
            @RequestParam(value = "keyWord", required = false) String keyWord,
            @RequestParam(value = "orderType", defaultValue = "asc") String orderType,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "50") int size
    ) {
        List<LocationDTO> locationDTOs = new ArrayList<>();
        Page<SolrLocationDTO> dtoPage = null;
        Pageable pageable = PageRequest.of(page, size);
        Page<JSONObject> result = null;
        List<SolrLocationDTO> results = null;
        HttpHeaders headers = new HttpHeaders();
        String language= OrgInformationUtil.getCurrentLanguage();

        if (results == null || results.size() == 0) {
            com.baomidou.mybatisplus.plugins.Page pg = PageUtil.getPage(page, size);
            locationDTOs = dtoService.getLocationDTOByKey(keyWord, language, vendorType, "all", pg, null);
            headers.add("X-Total-Count", "" + pg.getTotal());
        } else {
            for (SolrLocationDTO solrLocationDTO : results) {
                LocationDTO locationDTO = locationService.toLocationDTO(solrLocationDTO);
                locationDTOs.add(locationDTO);
            }
        }
        headers.add("Link", "/api/city/search");
        return new ResponseEntity(locationDTOs, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/location/type", method = RequestMethod.GET)
    public ResponseEntity<LocationDTO> getLocationByType(
            @RequestParam(value = "locationType") String locationType,
            @RequestParam(value = "code") String code) {
        String language= OrgInformationUtil.getCurrentLanguage();
        List<Location> locationList = locationService.getLocationByType(language, locationType, code);
        List<LocationDTO> locationDTOList = convertLocationDto(locationList, language);
        if (locationDTOList.size() == 0) {
            String message;
            message = "Location Type = " + locationType + " and Code = " + code + " and Language = " + language + " not found";
            throw new ObjectNotFoundException(LocationControllerImpl.class, message);
        }
        return new ResponseEntity(locationDTOList.get(0), HttpStatus.OK);
    }

    /**
     * 根据hlycode查询城市三字码
     *
     * @param code
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/airport/code", method = RequestMethod.GET)
    public ResponseEntity getLocationByCode(@RequestParam(value = "code") String code) {
        List<AirportCode> airportCodeList = airportCodeMapper.selectList(new EntityWrapper<AirportCode>().eq("code", code));
        return new ResponseEntity(airportCodeList, HttpStatus.OK);
    }

    /**
     * 根据hlycode查询城市三字码
     *
     * @param code
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/airport/codes", method = RequestMethod.GET)
    public ResponseEntity getLocationByCodes(@RequestParam(value = "code") String code) throws Exception {
        List<AirportCode> airportCodeList = airportCodeMapper.selectList(new EntityWrapper<AirportCode>().eq("code", code));
        StringBuffer sb = new StringBuffer();
        if (!CollectionUtils.isEmpty(airportCodeList)) {
            airportCodeList.stream().forEach(a -> {
                sb.append(a.getAirportCode()).append(",");
            });
            sb.delete(sb.length() - 1, sb.length());
        }
        return new ResponseEntity(new AirCode(sb.toString()), HttpStatus.OK);
    }

    /**
     * 根据code vendorType language查询某国家下的所有地址信息
     *
     * @param code
     * @param vendorType
     * @return
     */
    @RequestMapping(value = "/localization/query/all/address", method = RequestMethod.GET)
    public ResponseEntity<List<AddressDTO>> getAddressDTOByCountry(@RequestParam(value = "code") String code,
                                                                   @RequestParam(value = "vendorType", defaultValue = "standard") String vendorType
                                                                   ) {
        String language= OrgInformationUtil.getCurrentLanguage();
        return ResponseEntity.ok(localizationDTOService.getAddressDTOByCountry(code, vendorType, language));
    }


    /**
     * 别名查询地点V2
     *
     * @param vendorType
     * @param alias
     * @return
     */
    //@RequestMapping(value = "/location/aliases/v2", method = RequestMethod.GET)
    public LocationDTO getLocationByVendorAliasV2(
                                                  @RequestParam("vendorType") String vendorType,
                                                  @RequestParam("alias") String alias) {
        String language= OrgInformationUtil.getCurrentLanguage();
        List<Location> locationList = locationService.getLocationByVendorAlias(vendorType, alias, language);
        String message;

        if (locationList.size() == 0) {
            message = "Vendor Type = " + vendorType + " and Vendor Alias = " + alias + " and Language = " + language + " not found";
            throw new ObjectNotFoundException(LocationControllerImpl.class, message);
        }
        locationList = locationList.stream().distinct().collect(Collectors.toList());
        List<LocationDTO> locationDTOList = convertLocationDto(locationList, language);
        return locationDTOList.get(0);
    }


    //@PostMapping(value = "/location/search/keyword",produces = "application/json;charset=utf-8")
    public ResponseEntity<List<JSONObject>> findLocationByParentCondition(@RequestParam(value = "code") String code,
                                                                          @RequestParam(value = "keyword", required = false) String keyword,
                                                                          @RequestBody List<String> reverseAreaCodes,
                                                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                          @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        String language= OrgInformationUtil.getCurrentLanguage();
        //Page<JSONObject> pages = elasticsearchService.selectLocationByParentCodeAndkeyword(code, keyword, reverseAreaCodes, language, pageable);
        Page<JSONObject> pages = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + pages.getTotalElements());
        headers.add("Link", "/api/localization/query/state");
        return new ResponseEntity(pages.getContent(), headers, HttpStatus.OK);
    }

    public List<LocationDTO> listCityByIds(@RequestBody List<Long> cityIds,
                                           @RequestParam(value = "vendorType", defaultValue = "standard") String vendorType) {
        return localizationDTOService.listCityByIds(cityIds,vendorType);
    }

    /**
     * 根据地点id或级别ID或级别代码获取地点级别
     *
     * @param locationId
     * @param levelId
     * @param levelCode
     * @return
     */
    //@Override
    public LocationLevelCO getLocationLevelByLocationIdOrLevelIdOrLevelCode(
            @RequestParam(value = "locationId", required = false) Long locationId,
            @RequestParam(value = "levelId", required = false) Long levelId,
            @RequestParam(value = "levelCode", required = false) String levelCode) {
        if (locationId == null && levelId == null && levelCode == null) {
            return null;
        }
        LocationLevel locationLevel = locationLevelService.getLocationLevelByLocationIdOrLevelIdOrLevelCode(locationId, levelId, levelCode);
        return mapperFacade.map(locationLevel, LocationLevelCO.class);
    }
}
