package com.hand.hcf.app.mdata.area.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.area.domain.Level;
import com.hand.hcf.app.mdata.area.dto.InternationalAreaDTO;
import com.hand.hcf.app.mdata.area.dto.LevelDTO;
import com.hand.hcf.app.mdata.location.service.LocalizationDTOService;
import com.hand.hcf.app.mdata.location.service.LocationService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for province
 */

@Service
@Transactional
public class AreaService {


    @Autowired
    private LevelService levelService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocalizationDTOService localizationDTOService;


    @Autowired
    private MapperFacade mapperFacade;

    public List<InternationalAreaDTO> findAllInternalAreasByParentCode(Long tenantId, String language, String vendorType, String code,
                                                                       String type, UUID levelOid, boolean isTenant, UUID companyOid, Pageable pageable) {
        if (StringUtils.isEmpty(language)) {
            language = "zh_cn";
        }
        if (StringUtils.isEmpty(vendorType)) {
            vendorType = "standard";
        }
        List<String> includeList = null;
        if (levelOid != null) {
            LevelDTO cityLevelV2 = levelService.getCityLevelV2(levelOid, language);
            includeList = cityLevelV2.getInternationalAreaDTOS().stream().map(u -> u.getCode()).collect(Collectors.toList());
        }
        //地区单个值单独查询
        if("only".equals(type)){
                LocationDTO locationDTO = locationService.getLocationByCode(language,null, code);
                InternationalAreaDTO internationalAreaDTO = new InternationalAreaDTO();
                BeanUtils.copyProperties(locationDTO,internationalAreaDTO);
                internationalAreaDTO.setLevel(levelService.findOneByLevelOidAndDeletedFalse(levelOid));
                List<InternationalAreaDTO> content = new ArrayList<>();
                content.add(internationalAreaDTO);
                return content;
            }
        return this.typeConverter(tenantId,type,includeList,pageable,isTenant,companyOid,language,vendorType,code);
    }

    private List<InternationalAreaDTO> generatePage(List<InternationalAreaDTO> internationalAreaDTOS, Long tenantId,
                                                    Boolean isTenant, UUID companyOid){
        if (org.apache.commons.collections.CollectionUtils.isEmpty(internationalAreaDTOS)){
            return null;
        }

        internationalAreaDTOS.stream().map(u -> {
            Level level = new Level();
            if(isTenant){
                 level = levelService.selectOne(new EntityWrapper<Level>().eq("code",u.getCode()).eq("tenant_id",tenantId));
            }else{
                 level = levelService.selectOne(new EntityWrapper<Level>().eq("code",u.getCode()).eq("company_oid",companyOid));
            }
            if (!StringUtils.isEmpty(level.getLevelOid())) {
                level = levelService.findOneByLevelOidAndDeletedFalse(level.getLevelOid());
                u.setLevel(level);
            }
            return u;
        }).collect(Collectors.toList());
        return internationalAreaDTOS;
    }

    public List<InternationalAreaDTO> typeConverter(Long tenantId, String type, List<String> includeList, Pageable pageable,
                                                    Boolean isTenant, UUID companyOid, String language, String vendorType, String code ) {
        Page mybatisPage = PageUtil.getPage(pageable);
        Page localizationDTOList = null;
        switch (type) {
            case "country":
                localizationDTOList = localizationDTOService.getLocalizationCountryByCode(null,null,language,
                         includeList,mybatisPage);
                break;
            case "state":
                localizationDTOList = localizationDTOService.getLocalizationStateByCode(code, vendorType, language,
                        includeList,mybatisPage);
                break;
            case "city":
                localizationDTOList = localizationDTOService.getLocalizationCityByCode(code, vendorType, language,
                         includeList, mybatisPage);
                break;
            case "district":
                localizationDTOList = localizationDTOService.getLocalizationDistrictByCode(code, vendorType, language,
                        includeList,mybatisPage);
                break;
        }
        if (StringUtils.isEmpty(localizationDTOList)){
            return null;
        }
        List<InternationalAreaDTO> internationalAreaDTOS = mapperFacade.mapAsList(localizationDTOList.getRecords(), InternationalAreaDTO.class);
        return generatePage(internationalAreaDTOS, tenantId, isTenant, companyOid);
    }

}
