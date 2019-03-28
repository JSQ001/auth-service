package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeExpenseType;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeExpenseTypeMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/24
 */
@Service
@Transactional
public class ExpenseReportTypeExpenseTypeService extends BaseService<ExpenseReportTypeExpenseTypeMapper,ExpenseReportTypeExpenseType>{

    @Autowired
    private ExpenseReportTypeExpenseTypeMapper expenseReportTypeExpenseTypeMapper;

    /**
     * 批量新增 报账单类型关联费用类型表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseReportTypeExpenseType> createExpenseReportTypeExpenseTypeBatch(List<ExpenseReportTypeExpenseType> list){
        list.stream().forEach(expenseReportTypeExpenseType -> {
            if(expenseReportTypeExpenseType.getId() != null){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_EXPENSE_TYPE_ALREADY_EXISTS);
            }
            //设置条件
            if ( expenseReportTypeExpenseTypeMapper.selectList(
                    new EntityWrapper<ExpenseReportTypeExpenseType>()
                            .eq("report_type_id",expenseReportTypeExpenseType.getReportTypeId())
                            .eq("expense_type_id",expenseReportTypeExpenseType.getExpenseTypeId())
            ).size() > 0 ){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_EXPENSE_TYPE_NOT_ALLOWED_TO_REPEAT);
            }

            this.insert(expenseReportTypeExpenseType);
        });
        return list;
    }

    /**
     * 根据报账单类型id批量删除：报账单类型关联费用类型表
     * @param reportTypeId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpenseReportTypeExpenseTypeByReportTypeIdBatch(Long reportTypeId){
        baseMapper.delete(new EntityWrapper<ExpenseReportTypeExpenseType>().eq("report_type_id", reportTypeId));
    }
}
