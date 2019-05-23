package com.hand.hcf.app.expense.accrual.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.common.co.DimensionItemCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualDimension;
import com.hand.hcf.app.expense.accrual.domain.ExpenseAccrualType;
import com.hand.hcf.app.expense.accrual.persistence.ExpenseAccrualDimensionMapper;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@Service
public class ExpenseAccrualDimensionService extends
        ServiceImpl<ExpenseAccrualDimensionMapper,ExpenseAccrualDimension> {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExpenseAccrualTypeService expenseAccrualTypeService;
    
    
    /**
     * 分配维度
     * @param expAccrualTypeId
     * @param dimensions
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ExpenseAccrualDimension> assignDimensions(Long expAccrualTypeId,
                                                          List<ExpenseAccrualDimension> dimensions) {

        if (CollectionUtils.isEmpty(dimensions)){
            throw new BizException(RespCode.EXPENSE_APPLICATION_DIMENSION_IS_NULL);
        }
        // 查询类型是否存在
        ExpenseAccrualType expenseAccrualType = expenseAccrualTypeService.selectById(expAccrualTypeId);
        if (null == expenseAccrualType) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        dimensions.forEach(e -> {
            e.setExpAccrualTypeId(expAccrualTypeId);
            if (e.getHeaderFlag() == null){
                e.setHeaderFlag(false);
            }
        });
        try{
            this.insertOrUpdateBatch(dimensions);
        }catch (Exception e){
            throw new BizException(RespCode.EXPENSE_APPLICATION_DIMENSION_DUPLICATE);
        }
        return dimensions;
    }

    /**
     * 查询维度
     * @param expAccrualTypeId
     * @param page
     * @return
     */
    public List<ExpenseAccrualDimension> queryDimension(Long expAccrualTypeId, Page<ExpenseAccrualDimension> page) {

        List<ExpenseAccrualDimension> expAccrualTypeDimensions = this.selectPage(page,
                new EntityWrapper<ExpenseAccrualDimension>().eq("exp_accrual_type_id", expAccrualTypeId)
                        .orderBy("sequence"))
                .getRecords();
        if (!CollectionUtils.isEmpty(expAccrualTypeDimensions)) {
            // 设置默认值的名称
            List<Long> valueIds = expAccrualTypeDimensions
                    .stream()
                    .map(ExpenseAccrualDimension::getDefaultValue)
                    .collect(Collectors.toList());
            List<DimensionItemCO> dimensionItemCOS = organizationService.listDimensionItemsByIds(valueIds);

            Map<Long, String> valueMap = dimensionItemCOS
                    .stream()
                    .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1, k2) -> k1));

            // 设置维度的名称
            List<Long> dimensionIds = expAccrualTypeDimensions
                    .stream()
                    .map(ExpenseAccrualDimension::getDimensionId)
                    .collect(Collectors.toList());
            List<DimensionCO> dimensionCOS = organizationService.listDimensionsByIds(dimensionIds);
            Map<Long, String> dimensionNameMap = dimensionCOS.
                    stream()
                    .collect(Collectors.toMap(DimensionCO::getId, DimensionCO::getDimensionName, (k1, k2) -> k1));

            expAccrualTypeDimensions.forEach(e -> {
                if (valueMap.containsKey(e.getDefaultValue())) {
                    e.setValueName(valueMap.get(e.getDefaultValue()));
                }
                if (dimensionNameMap.containsKey(e.getDimensionId())) {
                    e.setDimensionName(dimensionNameMap.get(e.getDimensionId()));
                }
            });
        }
        return expAccrualTypeDimensions;
    }

    /**
     * 删除维度
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDimension(Long id) {
        this.deleteById(id);
        return true;
    }

    /**
     * 获取当前账套下未分配的维度
     * @param expAccrualTypeId
     * @param setOfBooksId
     * @param dimensionCode
     * @param dimensionName
     * @return
     */
    public List<DimensionCO> listDimensionByConditionFilter(Long expAccrualTypeId,
                                                            Long setOfBooksId,
                                                            String dimensionCode,
                                                            String dimensionName,
                                                            Boolean enabled){
        List<Long> ids = this.selectList(new EntityWrapper<ExpenseAccrualDimension>()
                .eq("exp_accrual_type_id", expAccrualTypeId))
                .stream()
                .map(ExpenseAccrualDimension::getDimensionId)
                .collect(Collectors.toList());
        List<DimensionCO> result = organizationService
                .listDimensionsBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, dimensionCode, dimensionName, enabled, ids);
        return result;
    }
}
