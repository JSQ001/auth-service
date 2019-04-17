package com.hand.hcf.app.expense.travel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.common.co.DimensionItemCO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignDimension;
import com.hand.hcf.app.expense.travel.persistence.TravelApplicationTypeAssignDimensionMapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Service
public class TravelApplicationTypeAssignDimensionService extends BaseService<TravelApplicationTypeAssignDimensionMapper, TravelApplicationTypeAssignDimension> {

    @Autowired
    private TravelApplicationTypeService travelApplicationTypeService;
    @Autowired
    private OrganizationService organizationService;

    /**
     * 新增
     * @param assignDimension
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TravelApplicationTypeAssignDimension createAssignDimension(TravelApplicationTypeAssignDimension assignDimension) {
        if (assignDimension.getId() != null) {
            throw new BizException(RespCode.SYS_ID_IS_NOT_NULL);
        }
        // 查询类型是否存在
        if (travelApplicationTypeService.selectById(assignDimension.getTypeId()) == null) {
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_NOT_EXISTS);
        }

        try{
            baseMapper.insert(assignDimension);
        }catch (DataAccessException e){
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_ASSIGN_DIMENSION_REPEAT);
        }
        return assignDimension;
    }

    /**
     * 更新
     * @param assignDimension
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TravelApplicationTypeAssignDimension updateAssignDimension(TravelApplicationTypeAssignDimension assignDimension) {
        if (assignDimension.getId() == null) {
            throw new BizException(RespCode.SYS_ID_IS_NULL);
        }
        try{
            baseMapper.updateAllColumnById(assignDimension);
        }catch (DataAccessException e){
            throw new BizException(RespCode.EXPENSE_TRAVEL_APPLICATION_TYPE_ASSIGN_DIMENSION_REPEAT);
        }
        return assignDimension;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAssignDimensionById(Long id) {
        baseMapper.deleteById(id);
        return true;
    }

    /**
     * 分页查询
     * @param travelTypeId
     * @param queryPage
     * @return
     */
    public List<TravelApplicationTypeAssignDimension> pageAssignDimension(Long travelTypeId, Page queryPage) {
        List<TravelApplicationTypeAssignDimension> assignDimensionList = baseMapper.selectPage(queryPage,
                new EntityWrapper<TravelApplicationTypeAssignDimension>().eq("type_id", travelTypeId).orderBy("sequence")
        );
        if (!CollectionUtils.isEmpty(assignDimensionList)){
            // 设置默认值的名称
            List<Long> valueIds = assignDimensionList
                    .stream()
                    .map(TravelApplicationTypeAssignDimension::getDefaultValue)
                    .collect(Collectors.toList());
            List<DimensionItemCO> valueDTOs = organizationService.listDimensionItemsByIds(valueIds);

            Map<Long, String> valueMap = valueDTOs
                    .stream()
                    .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1, k2) -> k1));

            // 设置维度的名称
            List<Long> dimensionIds = assignDimensionList
                    .stream()
                    .map(TravelApplicationTypeAssignDimension::getDimensionId)
                    .collect(Collectors.toList());
            List<DimensionCO> centerDTOS = organizationService.listDimensionsByIds(dimensionIds);
            Map<Long, String> dimensionNameMap = centerDTOS
                    .stream()
                    .collect(Collectors.toMap(DimensionCO::getId, DimensionCO::getDimensionName, (k1, k2) -> k1));

            assignDimensionList.forEach(e -> {
                if (valueMap.containsKey(e.getDefaultValue())){
                    e.setValueName(valueMap.get(e.getDefaultValue()));
                }

                if (dimensionNameMap.containsKey(e.getDimensionId())){
                    e.setDimensionName(dimensionNameMap.get(e.getDimensionId()));
                }
            });
        }
        return assignDimensionList;
    }

    /**
     * 查询未分配的维度
     * @param travelTypeId
     * @param setOfBooksId
     * @param dimensionCode
     * @param dimensionName
     * @param enabled
     * @return
     */
    public List<DimensionCO> listDimensionByConditionFilter(Long travelTypeId, Long setOfBooksId, String dimensionCode, String dimensionName, Boolean enabled) {
        List<Long> ids = baseMapper.selectList(new EntityWrapper<TravelApplicationTypeAssignDimension>()
                .eq("type_id", travelTypeId))
                .stream()
                .map(TravelApplicationTypeAssignDimension::getDimensionId)
                .collect(Collectors.toList());
        List<DimensionCO> result = organizationService.listDimensionsBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, dimensionCode, dimensionName, enabled, ids);
        return result;
    }
}
