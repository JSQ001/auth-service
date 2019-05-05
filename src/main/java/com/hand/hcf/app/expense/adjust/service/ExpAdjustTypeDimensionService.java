package com.hand.hcf.app.expense.adjust.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.common.co.DimensionItemCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.adjust.domain.ExpAdjustTypeDimension;
import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustType;
import com.hand.hcf.app.expense.adjust.persistence.ExpAdjustTypeDimensionMapper;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 费用调整单关联维度(ExpAdjustTypeDimension)表服务接口
 *
 * @author zhanhua.cheng
 * @since 2019-04-09 17:37:44
 */
@Service
public class ExpAdjustTypeDimensionService extends BaseService<ExpAdjustTypeDimensionMapper, ExpAdjustTypeDimension> {

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ExpenseAdjustTypeService expenseAdjustTypeService;

    /**
     * 分配维度
     * @param expAdjustTypeId
     * @param dimensions
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ExpAdjustTypeDimension> assignDimensions(Long expAdjustTypeId, List<ExpAdjustTypeDimension> dimensions) {

        if (CollectionUtils.isEmpty(dimensions)){
            throw new BizException(RespCode.EXPENSE_APPLICATION_DIMENSION_IS_NULL);
        }
        // 查询类型是否存在
        ExpenseAdjustType expenseAdjustType = expenseAdjustTypeService.selectById(expAdjustTypeId);
        if (null == expenseAdjustType) {
            throw new BizException(RespCode.SYS_OBJECT_IS_EMPTY);
        }
        dimensions.forEach(e -> {
            e.setExpAdjustTypeId(expAdjustTypeId);
            if (e.getHeaderFlag() == null){
                e.setHeaderFlag(false);
            }
        });
        try{
            this.insertOrUpdateBatch(dimensions);
        }catch (DataAccessException e){
            throw new BizException(RespCode.EXPENSE_APPLICATION_DIMENSION_DUPLICATE);
        }
        return dimensions;
    }

    /**
     * 查询维度
     * @param expAdjustTypeId
     * @param page
     * @return
     */
    public List<ExpAdjustTypeDimension> queryDimension(Long expAdjustTypeId, Page<ExpAdjustTypeDimension> page) {

        List<ExpAdjustTypeDimension> expAdjustTypeDimensions = this.selectPage(page,
                new EntityWrapper<ExpAdjustTypeDimension>().eq("exp_adjust_type_id", expAdjustTypeId)
                        .orderBy("sequence"))
                .getRecords();
        if (!CollectionUtils.isEmpty(expAdjustTypeDimensions)) {
            // 设置默认值的名称
            List<Long> valueIds = expAdjustTypeDimensions
                    .stream()
                    .map(ExpAdjustTypeDimension::getDefaultValue)
                    .collect(Collectors.toList());
            List<DimensionItemCO> dimensionItemCOS = organizationService.listDimensionItemsByIds(valueIds);

            Map<Long, String> valueMap = dimensionItemCOS
                    .stream()
                    .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1, k2) -> k1));

            // 设置维度的名称
            List<Long> dimensionIds = expAdjustTypeDimensions
                    .stream()
                    .map(ExpAdjustTypeDimension::getDimensionId)
                    .collect(Collectors.toList());
            List<DimensionCO> dimensionCOS = organizationService.listDimensionsByIds(dimensionIds);
            Map<Long, String> dimensionNameMap = dimensionCOS.
                    stream()
                    .collect(Collectors.toMap(DimensionCO::getId, DimensionCO::getDimensionName, (k1, k2) -> k1));

            expAdjustTypeDimensions.forEach(e -> {
                if (valueMap.containsKey(e.getDefaultValue())) {
                    e.setValueName(valueMap.get(e.getDefaultValue()));
                }
                if (dimensionNameMap.containsKey(e.getDimensionId())) {
                    e.setDimensionName(dimensionNameMap.get(e.getDimensionId()));
                }
            });
        }
        return expAdjustTypeDimensions;
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
     * @param expAdjustTypeId
     * @param setOfBooksId
     * @param dimensionCode
     * @param dimensionName
     * @param enabled
     * @return
     */
    public List<DimensionCO> listDimensionByConditionFilter(Long expAdjustTypeId,
                                                            Long setOfBooksId,
                                                            String dimensionCode,
                                                            String dimensionName,
                                                            Boolean enabled){
        List<Long> ids = this.selectList(new EntityWrapper<ExpAdjustTypeDimension>()
                .eq("exp_adjust_type_id", expAdjustTypeId))
                .stream()
                .map(ExpAdjustTypeDimension::getDimensionId)
                .collect(Collectors.toList());
        List<DimensionCO> result = organizationService.listDimensionsBySetOfBooksIdConditionByIgnoreIds(setOfBooksId, dimensionCode, dimensionName, enabled, ids);
        return result;
    }
}