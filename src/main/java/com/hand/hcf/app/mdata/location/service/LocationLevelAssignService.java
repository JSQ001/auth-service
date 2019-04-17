package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.plugins.Page;

import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.location.domain.LocationLevelAssign;
import com.hand.hcf.app.mdata.location.dto.LocationLevelAssignDTO;
import com.hand.hcf.app.mdata.location.persistence.LocationLevelAssignMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 地点级别分配地点Service
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/3/27 14:46
 */
@Service
public class LocationLevelAssignService extends BaseService<LocationLevelAssignMapper, LocationLevelAssign> {

    @Autowired
    private LocationLevelAssignMapper locationLevelAssignMapper;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;

    /**
     * 根据地点级别id删除地点级别分配信息
     * @param levelId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteLocationLevelAssignByLevelId(Long levelId){
        locationLevelAssignMapper.deleteLocationLevelAssignByLevelId(levelId);
    }

    /**
     * 批量删除地点级别分配信息
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteLocationLevelAssignByIds(List<Long> ids){
        for (Long id : ids){
            locationLevelAssignMapper.deleteLocationLevelAssignById(id);
        }
        return true;
    }

    /**
     * 分配地点
     * @param locationLevelAssignDTO
     */
    public void distributeLocation(LocationLevelAssignDTO locationLevelAssignDTO){
        List<Long> locationIds = locationLevelAssignDTO.getLocationIds();
        locationIds.stream().forEach(
                id -> {
                    LocationLevelAssign locationLevelAssign = LocationLevelAssign
                            .builder()
                            .levelId(locationLevelAssignDTO.getLevelId())
                            .locationId(id)
                            .build();
                        locationLevelAssignMapper.insert(locationLevelAssign);
                    } );
    }

    /**
     * 查询地点级别已分配的地点信息
     * @param levelId
     * @param countryCode
     * @param stateCode
     * @param cityCode
     * @param type
     * @param code
     * @param description
     * @param language
     * @param page
     * @return
     */
    public List<LocationLevelAssign> queryLocationLevelAssign(Long levelId,
                                                              String countryCode,
                                                              String stateCode,
                                                              String cityCode,
                                                              String type,
                                                              String code,
                                                              String description,
                                                              String language,
                                                              Page<LocationLevelAssign> page){
        List<LocationLevelAssign> locationDTOS = locationLevelAssignMapper.queryLocationLevelAssign(levelId,countryCode, stateCode, cityCode, type, code, description, language, page);
        List<LocationLevelAssign> locationLevelAssigns = locationDTOS.stream().map(locationDTO -> {
            LocationLevelAssign domain = new LocationLevelAssign();
            BeanUtils.copyProperties(locationDTO,domain);
            if (!StringUtils.isEmpty(locationDTO.getType())) {
                SysCodeValueCO sysCodeValueCO = hcfOrganizationInterface.getValueBySysCodeAndValue("LOCATION_TYPE",locationDTO.getType());
                domain.setTypeDesc(sysCodeValueCO.getName());
            }
            return domain;
        }).collect(Collectors.toList());

        return locationLevelAssigns;
    }
}
