package com.hand.hcf.app.expense.travel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationHeader;
import com.hand.hcf.app.expense.travel.domain.TravelAssociatePeople;
import com.hand.hcf.app.expense.travel.persistence.TravelAssociatePeopleMapper;
import com.hand.hcf.app.expense.travel.web.dto.TravelPeopleDTO;
import com.hand.hcf.app.core.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TravelAssociatePeopleService extends BaseService<TravelAssociatePeopleMapper, TravelAssociatePeople> {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private TravelApplicationHeaderService headerService;


    /**
     *
     * @param headerId
     * @return
     */
    public Page<ContactCO> listUsersByHeaderId(Long headerId,String employeeCode,String fullName,String keyWord,Page page) {
        List<Long> userList = baseMapper.selectList(
                new EntityWrapper<TravelAssociatePeople>()
                        .eq("asso_type","H")
                        .eq(headerId != null,"asso_pk_id",headerId)
        ).stream().map(TravelAssociatePeople::getComPeopleId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
        TravelApplicationHeader header = headerService.selectById(headerId);
        Long employeeId =  header.getEmployeeId();
        if(!userList.contains(employeeId)){
            userList.add(employeeId);
        }
        return organizationService.pageConditionNameAndIds(employeeCode,fullName, keyWord,userList, page);
    }

    public List<TravelPeopleDTO> listTravelPeopleByAssoPkIdAndPosition(Long assoPkId, String position) {
        List<Long> userList = baseMapper.selectList(
                new EntityWrapper<TravelAssociatePeople>()
                        .eq("asso_type",position)
                        .eq(assoPkId != null,"asso_pk_id",assoPkId)
        ).stream().map(TravelAssociatePeople::getComPeopleId).distinct().filter(Objects::nonNull).collect(Collectors.toList());
        List<ContactCO> contactCOS = organizationService.listUsersByIds(userList);
        List<TravelPeopleDTO> travelPeopleDTOList = new ArrayList<>();
        contactCOS.stream().forEach(c ->
            travelPeopleDTOList.add(TravelPeopleDTO.builder().employeeId(c.getId()).employeeName(c.getFullName()).build())
        );
        return travelPeopleDTOList;
    }
}
