package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.DimensionCO;
import com.hand.hcf.app.common.co.DimensionItemCO;
import com.hand.hcf.app.base.org.SysCodeValueCO;
import com.hand.hcf.app.expense.common.externalApi.OrganizationService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeDimension;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeDimensionMapper;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import jdk.internal.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/1
 */
@Service
@Transactional
public class ExpenseReportTypeDimensionService extends BaseService<ExpenseReportTypeDimensionMapper,ExpenseReportTypeDimension>{

    @Autowired
    private ExpenseReportTypeDimensionMapper expenseReportTypeDimensionMapper;

    @Autowired
    private ExpenseReportTypeMapper expenseReportTypeMapper;

    @Autowired
    private OrganizationService organizationService;
    

    /**
     * 单个新增 报账单类型关联维度
     * @param expenseReportTypeDimension
     * @return
     */
    public ExpenseReportTypeDimension createExpenseReportTypeDimension(ExpenseReportTypeDimension expenseReportTypeDimension){
        if (expenseReportTypeDimension.getId() != null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DIMENSION_ALREADY_EXISTS);
        }
        //报账单类型分配维度不能重复
        if (expenseReportTypeDimensionMapper.selectList(
                new EntityWrapper<ExpenseReportTypeDimension>()
                        .eq("report_type_id",expenseReportTypeDimension.getReportTypeId())
                        .eq("dimension_id",expenseReportTypeDimension.getDimensionId())
        ).size() > 0 ){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DIMENSION_NOT_ALLOWED_TO_REPEAT);
        }
        //同样布局下的优先级不可重复
        if (expenseReportTypeDimensionMapper.selectList(
                new EntityWrapper<ExpenseReportTypeDimension>()
                        .eq("report_type_id",expenseReportTypeDimension.getReportTypeId())
                        .eq("position",expenseReportTypeDimension.getPosition())
                        .eq("sequence_number",expenseReportTypeDimension.getSequenceNumber())
        ).size() > 0 ){
            String position = expenseReportTypeDimension.getPosition();
            SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("2003",position);
            if(sysCodeValueCO != null){
                position = sysCodeValueCO.getName();
            }
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DIMENSION_SEQUENCE_NUMBER_NOT_ALLOWED_TO_REPEAT_AT_THE_SAME_POSITION,new Object[]{position,expenseReportTypeDimension.getSequenceNumber()});
        }
        expenseReportTypeDimensionMapper.insert(expenseReportTypeDimension);
        return expenseReportTypeDimension;
    }

    /**
     * 单个修改 报账单类型关联维度
     * @param expenseReportTypeDimension
     * @return
     */
    public ExpenseReportTypeDimension updateExpenseReportTypeDimension(ExpenseReportTypeDimension expenseReportTypeDimension){
        ExpenseReportTypeDimension oldExpenseReportTypeDimension = expenseReportTypeDimensionMapper.selectById(expenseReportTypeDimension.getId());
        if (oldExpenseReportTypeDimension == null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DIMENSION_NOT_EXIST);
        }
        //报账单类型分配维度不能重复
        if (!oldExpenseReportTypeDimension.getDimensionId().equals(expenseReportTypeDimension.getDimensionId())){
            if (expenseReportTypeDimensionMapper.selectList(
                    new EntityWrapper<ExpenseReportTypeDimension>()
                            .eq("report_type_id",expenseReportTypeDimension.getReportTypeId())
                            .eq("dimension_id",expenseReportTypeDimension.getDimensionId())
            ).size() > 0 ){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DIMENSION_NOT_ALLOWED_TO_REPEAT);
            }
        }
        //同样布局下的优先级不可重复
        if ( (!oldExpenseReportTypeDimension.getPosition().equals(expenseReportTypeDimension.getPosition())) ||
                (!oldExpenseReportTypeDimension.getSequenceNumber().equals(expenseReportTypeDimension.getSequenceNumber())) ){
            if (expenseReportTypeDimensionMapper.selectList(
                    new EntityWrapper<ExpenseReportTypeDimension>()
                            .eq("report_type_id",expenseReportTypeDimension.getReportTypeId())
                            .eq("position",expenseReportTypeDimension.getPosition())
                            .eq("sequence_number",expenseReportTypeDimension.getSequenceNumber())
            ).size() > 0 ){
                String position = expenseReportTypeDimension.getPosition();
                SysCodeValueCO sysCodeValueCO = organizationService.getSysCodeValueByCodeAndValue("2003",position);
                if(sysCodeValueCO != null){
                    position = sysCodeValueCO.getName();
                }
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DIMENSION_SEQUENCE_NUMBER_NOT_ALLOWED_TO_REPEAT_AT_THE_SAME_POSITION,new Object[]{position,expenseReportTypeDimension.getSequenceNumber()});
            }
        }
        expenseReportTypeDimension.setVersionNumber(oldExpenseReportTypeDimension.getVersionNumber());
        expenseReportTypeDimensionMapper.updateAllColumnById(expenseReportTypeDimension);
        return expenseReportTypeDimensionMapper.selectById(expenseReportTypeDimension);
    }

    /**
     * 根据id删除 报账单类型关联维度
     * @param id
     */
    public void deleteExpenseReportTypeDimension(Long id){
        if (expenseReportTypeDimensionMapper.selectById(id) == null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DIMENSION_NOT_EXIST);
        }
        expenseReportTypeDimensionMapper.deleteById(id);
    }

    /**
     * 分页查询 某个报账单类型下分配的维度
     * @param reportTypeId
     * @param page
     * @return
     */
    public Page<ExpenseReportTypeDimension> getExpenseReportTypeDimensionByCond(Long reportTypeId, Page page){
        Page<ExpenseReportTypeDimension> result = this.selectPage(page,
                new EntityWrapper<ExpenseReportTypeDimension>()
                        .eq("report_type_id",reportTypeId)
                        .orderBy("sequence_number")
        );

        if (!CollectionUtils.isEmpty(result.getRecords())){
            // 设置默认值的名称
            List<Long> defaultValueIdList = result.getRecords()
                    .stream()
                    .map(ExpenseReportTypeDimension::getDefaultValueId)
                    .collect(Collectors.toList());
            List<DimensionItemCO> dimensionItemCOList = organizationService.listDimensionItemsByIds(defaultValueIdList);

            Map<Long, String> defaultValueMap = dimensionItemCOList
                    .stream()
                    .collect(Collectors.toMap(DimensionItemCO::getId, DimensionItemCO::getDimensionItemName, (k1, k2) -> k1));

            // 设置维度的名称
            List<Long> dimensionIds = result.getRecords()
                    .stream()
                    .map(ExpenseReportTypeDimension::getDimensionId)
                    .collect(Collectors.toList());
            List<DimensionCO> dimensionCOList = organizationService.listDimensionsByIds(dimensionIds);
            Map<Long, String> dimensionNameMap = dimensionCOList.
                    stream()
                    .collect(Collectors.toMap(DimensionCO::getId, DimensionCO::getDimensionName, (k1, k2) -> k1));

            result.getRecords().forEach(e -> {
                if (defaultValueMap.containsKey(e.getDefaultValueId())){
                    e.setDefaultValueName(defaultValueMap.get(e.getDefaultValueId()));
                }

                if (dimensionNameMap.containsKey(e.getDimensionId())){
                    e.setDimensionName(dimensionNameMap.get(e.getDimensionId()));
                }
            });
        }

        return result;
    }

    /**
     * 根据报账单类型id 不分页查询 其尚未分配的维度
     *
     * @param reportTypeId 报账单类型id
     * @return
     */
    public List<DimensionCO> getNotAssignDimensionForExpenseReportType(Long reportTypeId){
        ExpenseReportType expenseReportType = expenseReportTypeMapper.selectById(reportTypeId);
        if (expenseReportType == null){
            throw new BizException(RespCode.EXPENSE_REPORT_TYPE_NOT_EXIST);
        }

        List<Long> dimensionIds = expenseReportTypeDimensionMapper.selectList(
                new EntityWrapper<ExpenseReportTypeDimension>()
                        .eq("report_type_id",reportTypeId)
        ).stream().map(ExpenseReportTypeDimension::getDimensionId).collect(Collectors.toList());

        List<DimensionCO> dimensionCOs = organizationService.listDimensionsBySetOfBooksIdConditionByIgnoreIds(expenseReportType.getSetOfBooksId(), null, null, true, dimensionIds);

        return dimensionCOs;
    }
}
