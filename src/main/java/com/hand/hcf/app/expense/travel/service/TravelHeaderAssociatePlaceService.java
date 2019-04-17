package com.hand.hcf.app.expense.travel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.dto.LocationDTO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.travel.domain.TravelHeaderAssociatePlace;
import com.hand.hcf.app.expense.travel.persistence.TravelHeaderAssociatePlaceMapper;
import com.hand.hcf.app.expense.travel.web.dto.TravelPeopleDTO;
import com.hand.hcf.app.expense.travel.web.dto.TravelPlaceDTO;
import com.hand.hcf.app.core.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TravelHeaderAssociatePlaceService extends BaseService<TravelHeaderAssociatePlaceMapper, TravelHeaderAssociatePlace> {
    @Autowired
    private OrganizationService organizationService;

    public List<TravelPlaceDTO> listTravelFromPlaceByTypeAndId(Long requisitionHeaderId, String Type) {
        List<TravelPlaceDTO> travelPeopleDTOList = new ArrayList<>();
        List<Long> placeList = baseMapper.selectList(
                new EntityWrapper<TravelHeaderAssociatePlace>()
                        .eq("place_type",Type)
                        .eq(requisitionHeaderId != null,"requisition_header_id",requisitionHeaderId)
        ).stream().map(TravelHeaderAssociatePlace::getPlaceId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
        List<LocationDTO> LocationCOS = organizationService.listCityByIds(placeList);
        if(CollectionUtils.isNotEmpty((LocationCOS))) {
            LocationCOS.stream().forEach(c ->
                    travelPeopleDTOList.add(TravelPlaceDTO.builder()
                            .placeId(c.getId())
                            .placeName(c.getCity())
                            .build())
            );
        }
        return travelPeopleDTOList;
    }
}
