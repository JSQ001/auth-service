package com.hand.hcf.app.expense.travel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.common.co.DepartmentCO;
import com.hand.hcf.app.common.co.DimensionItemCO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.DimensionUtils;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationLineDetail;
import com.hand.hcf.app.expense.travel.persistence.TravelApplicationLineDetailMapper;
import com.hand.hcf.app.expense.travel.web.dto.TravelApplicationLineDetailWebDTO;
import com.hand.hcf.app.core.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TravelApplicationLineDetailService extends BaseService<TravelApplicationLineDetailMapper, TravelApplicationLineDetail> {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private MapperFacade mapper;

    /**
     * 设置公司部门名称
     *
     * @param dtos
     */
    public void setOtherInfo(List<TravelApplicationLineDetailWebDTO> dtos) {
        if (!CollectionUtils.isEmpty(dtos)) {
            Set<Long> companyIds = new HashSet<>();
            Set<Long> departmentIds = new HashSet<>();
            Set<Long> employeeIds = new HashSet<>();
            dtos.stream().forEach(e -> {
                companyIds.add(e.getCompanyId());
                departmentIds.add(e.getUnitId());
                employeeIds.add(e.getBookerId());
                employeeIds.add(e.getComPeopleId());
            });

            // 查询公司
            List<CompanyCO> companies = organizationService.listCompaniesByIds(new ArrayList<>(companyIds));
            Map<Long, String> companyMap = companies
                    .stream()
                    .collect(Collectors.toMap(CompanyCO::getId, CompanyCO::getName, (k1, k2) -> k1));
            // 查询部门
            List<DepartmentCO> departments = organizationService.listDepartmentsByIds(new ArrayList<>(departmentIds));

            Map<Long, String> departmentMap = departments
                    .stream()
                    .collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getName, (k1, k2) -> k1));
            // 查询员工
            Map<Long, ContactCO> usersMap = organizationService.getUserMapByUserIds(new ArrayList<>(employeeIds));

            dtos
                    .stream()
                    .forEach(e -> {
                        if (companyMap.containsKey(e.getCompanyId())) {
                            e.setCompanyName(companyMap.get(e.getCompanyId()));
                        }
                        if (departmentMap.containsKey(e.getUnitId())) {
                            e.setDepartmentName(departmentMap.get(e.getUnitId()));
                        }
                        if (usersMap.containsKey(e.getBookerId())) {
                            e.setBookerName(usersMap.get(e.getBookerId()).getFullName());
                        }
                        if (usersMap.containsKey(e.getComPeopleId())) {
                            e.setTravelPeopleStr(usersMap.get(e.getComPeopleId()).getFullName());
                        }
                    });
        }
    }

    public List<TravelApplicationLineDetailWebDTO> getDetailsByLineId(Long lineId){
        List<TravelApplicationLineDetailWebDTO> dtoList = new ArrayList<>();
        List<TravelApplicationLineDetail> travelApplicationLineDetailList = baseMapper.selectList(new EntityWrapper<TravelApplicationLineDetail>().eq("requisition_line_id",lineId));

        if (!CollectionUtils.isEmpty(travelApplicationLineDetailList)) {
            dtoList = mapper.mapAsList(travelApplicationLineDetailList,TravelApplicationLineDetailWebDTO.class);

            Set<Long> dimensionValueList = new HashSet<>();
            dtoList.forEach(e -> {
                List<Long> valueIdList = DimensionUtils.getDimensionId(e, TravelApplicationLineDetailWebDTO.class);
                if (!CollectionUtils.isEmpty(valueIdList)){
                    dimensionValueList.addAll(valueIdList);
                }
            });
            List<DimensionItemCO> valueDTOs = organizationService.listDimensionItemsByIds(new ArrayList<>(dimensionValueList));

            Map<Long, String> valueMap = valueDTOs
                    .stream()
                    .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1, k2) -> k1));
            for (int i = 1; i <= dtoList.size(); i++) {
                TravelApplicationLineDetailWebDTO dto = dtoList.get(i - 1);
                DimensionUtils.setDimensionCodeOrName("Name", dto, TravelApplicationLineDetailWebDTO.class, valueMap);
            }
            setOtherInfo(dtoList);
        }
        return dtoList;
    }
}

