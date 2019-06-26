package com.hand.hcf.app.mdata.location.web;

import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.LocationControllerImpl;
import com.hand.hcf.app.mdata.location.adapter.SolrLocationAdapter;
import com.hand.hcf.app.mdata.location.domain.Location;
import com.hand.hcf.app.mdata.location.domain.VendorAlias;
import com.hand.hcf.app.mdata.location.domain.VendorAliasDetail;
import com.hand.hcf.app.mdata.location.dto.CityAndCountryDTO;
import com.hand.hcf.app.mdata.location.dto.GetAliasByVendorCodeResponseDTO;
import com.hand.hcf.app.mdata.location.dto.SolrLocationDTO;
import com.hand.hcf.app.mdata.location.service.LocationService;
import com.hand.hcf.app.mdata.location.service.VendorAliasDetailService;
import com.hand.hcf.app.mdata.location.service.VendorAliasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vance.zhao on 16/10/28.
 */
@RestController
@RequestMapping("/api")
public class VendorAliasController {

    private static final Logger log = LoggerFactory.getLogger(VendorAliasController.class);


    @Autowired
    private LocationService locationService;

    @Autowired
    private VendorAliasService vendorAliasService;

    @Autowired
    private VendorAliasDetailService vendorAliasDetailService;

    /**
     * Request:
     * *VendorType*
     * *Code*
     * <p>
     * Header:
     * *Language*
     * <p>
     * Response:
     * *GetAliasByVendorCodeResponseDTO*
     */
    @RequestMapping(value = "/vendor/aliases/{vendorType}/{code}", method = RequestMethod.GET)
    public ResponseEntity<GetAliasByVendorCodeResponseDTO>

    getAliasByVendorCodeResponseDTO(@RequestHeader(value = "language") @Valid String language,
                                    @PathVariable("vendorType") @Valid String vendorType,
                                    @PathVariable("code") @Valid String code) {
        List<VendorAlias> vendorAliasList = vendorAliasService.getForCityName(code, vendorType, language);

        if (vendorAliasList.size() == 0) {
            String message = new String("VendorType  = " + vendorType + " and code = " + code + " and Language = " + language + " not found");
            throw new ObjectNotFoundException(LocationControllerImpl.class, message);
        }

        List<VendorAliasDetail> vendorCodeAliases = vendorAliasDetailService.findByCodeAndVendorType(code, vendorType);
        VendorAliasDetail vendorCodeAlias = null;
        if (CollectionUtils.isNotEmpty(vendorCodeAliases)) {
            vendorCodeAlias = vendorCodeAliases.get(0);
        }
        List<GetAliasByVendorCodeResponseDTO> responseDTOList = new ArrayList<>();
        for (VendorAlias v : vendorAliasList) {
            GetAliasByVendorCodeResponseDTO getAliasByVendorCodeResponseDTO = new GetAliasByVendorCodeResponseDTO();
            getAliasByVendorCodeResponseDTO.setAlias(v.getAlias());
            if (vendorCodeAlias != null) {
                getAliasByVendorCodeResponseDTO.setCityAlias(vendorCodeAlias.getCityAliasCode());
                getAliasByVendorCodeResponseDTO.setCountryAlias(vendorCodeAlias.getCountryAliasCode());
            }
            responseDTOList.add(getAliasByVendorCodeResponseDTO);
        }
        return new ResponseEntity(responseDTOList.get(0), HttpStatus.OK);
    }

    /**
     * 供应商code单量查询
     * @param language
     * @param vendorType
     * @param code
     * @return
     */
    @RequestMapping(value = "/vendor/aliases/{vendorType}", method = RequestMethod.GET)
    public ResponseEntity<GetAliasByVendorCodeResponseDTO> getAliasByVendorCodeResponse(
            @RequestHeader(value = "language") @Valid String language,
            @PathVariable("vendorType") @Valid String vendorType,
            @RequestParam("code") @Valid String code) {
        GetAliasByVendorCodeResponseDTO vendorAliasDetailsByCode = vendorAliasService.findVendorAliasDetailsByCode(code, vendorType, language);
        return new ResponseEntity(vendorAliasDetailsByCode, HttpStatus.OK);
    }

    /**
     * 供应商code批量查询
     * @param language
     * @param vendorType
     * @param codes
     * @return
     */
    @RequestMapping(value = "/vendor/aliases/{vendorType}", method = RequestMethod.POST)
    public ResponseEntity<List<GetAliasByVendorCodeResponseDTO>> getAliasByVendorCodesListResponse(
                                                            @RequestHeader(value = "language") @Valid String language,
                                                            @PathVariable("vendorType") @Valid String vendorType,
                                                            @RequestBody List<String> codes) {
        List<GetAliasByVendorCodeResponseDTO> getAliasByVendorCodeResponseDTOS = vendorAliasService.vendorAliasDetailsByCodes(codes, vendorType, language);
        return new ResponseEntity(getAliasByVendorCodeResponseDTOS, HttpStatus.OK);
    }

    @RequestMapping(value = "/vendor/three/{codeType}/{code}/{vendorType}")
    public ResponseEntity<CityAndCountryDTO>
    getAliasByThreeCode(@RequestHeader(value = "language") @Valid String language,
                        @PathVariable(value = "codeType") String codeType,
                        @PathVariable(value = "vendorType") String vendorType,
                        @PathVariable(value = "code") String code) {
        List<VendorAliasDetail> vendorCodeAliases = new ArrayList<>();

        if ("city".equals(codeType)) {
            vendorCodeAliases = vendorAliasDetailService.findByCityAliasCodeAndVendorType(code, vendorType);
        } else if ("country".equals(codeType)) {
            vendorCodeAliases = vendorAliasDetailService.findByCountryAliasCodeAndVendorType(code, vendorType);
        }
        if (CollectionUtils.isNotEmpty(vendorCodeAliases)) {
            String cityCode = vendorCodeAliases.get(0).getCode();
            List<Location> locationList = locationService.listLocation(cityCode);
            List<SolrLocationDTO> solrLocationDTOs = new ArrayList<>();
            locationList.stream().forEach(l -> {
                l.getVendorAliasList().stream().filter(v -> language.equals(v.getLanguage())).forEach(v -> {
                    SolrLocationAdapter adapter = new SolrLocationAdapter(l, v.getLanguage(), v.getVendorType());
                    solrLocationDTOs.add(adapter.convertToDTO());
                });
            });
            CityAndCountryDTO dto = new CityAndCountryDTO();
            if (CollectionUtils.isEmpty(solrLocationDTOs) || solrLocationDTOs.get(0) == null) {
                throw new ObjectNotFoundException(VendorAliasController.class, "cityCode: " + cityCode + "not found");
            }
            dto.setCityName(solrLocationDTOs.get(0).getVendorAlias());
            dto.setCountryName(solrLocationDTOs.get(0).getCountry());
            return ResponseEntity.ok(dto);
        }
        throw new ObjectNotFoundException(VendorAliasController.class, "VendorType  = " + vendorType + " and code = " + code + " and codeType = " + codeType + " not found");
    }

    @GetMapping("/hly/code")
    public String getHlyCodeByVendorCode(@RequestParam(value = "vendorCode") String vendorCode,
                                         @RequestParam(value = "vendorType") String vendorType) {
        String language= OrgInformationUtil.getCurrentLanguage();
        List<VendorAlias> vendorAliasList = vendorAliasService.getForCode(vendorCode, vendorType, language);
        if (CollectionUtils.isNotEmpty(vendorAliasList)) {
            return vendorAliasList.get(0).getCode();
        } else {
            return null;
        }
    }

    @GetMapping("/transfer/code")
    public String getHlyCodeByVendorName(@RequestParam(value = "vendorCode") String vendorCode,
                                         @RequestParam(value = "vendorType") String vendorType) {
        String targetVendorType = null;
        if (vendorType.equals("titancloud_code")) {
            targetVendorType = "amap_code";
        } else {
            targetVendorType = "titancloud_code";
        }
        String language= OrgInformationUtil.getCurrentLanguage();
        List<VendorAlias> vendorAliasList = vendorAliasService.getForCode(vendorCode, vendorType, language);
        if (CollectionUtils.isNotEmpty(vendorAliasList) && targetVendorType != null) {
            String code = vendorAliasList.get(0).getCode();
            List<VendorAlias> vendorAliasList1 = vendorAliasService.getForCityName(code, targetVendorType, language);
            if (CollectionUtils.isEmpty(vendorAliasList1)) {
                throw new ObjectNotFoundException(VendorAliasController.class, "VendorType  = " + vendorType + " and vendorCode = " + vendorCode + " not found transfer vendorCode");
            }
            return vendorAliasList1.get(0).getAlias();
        } else {
            throw new ObjectNotFoundException(VendorAliasController.class, "VendorType  = " + vendorType + " and vendorCode = " + vendorCode + " not found transfer vendorCode");
        }
    }
}
