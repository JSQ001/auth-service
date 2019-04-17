package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeTransactionClass;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeTransactionClassMapper;
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
public class ExpenseReportTypeTransactionClassService extends BaseService<ExpenseReportTypeTransactionClassMapper,ExpenseReportTypeTransactionClass> {

    @Autowired
    private ExpenseReportTypeTransactionClassMapper expenseReportTypeTransactionClassMapper;

    /**
     * 批量新增 报账单类型关联付款用途表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseReportTypeTransactionClass> createExpenseReportTypeTransactionClassBatch(List<ExpenseReportTypeTransactionClass> list){
        list.stream().forEach(expenseReportTypeTransactionClass -> {
            if(expenseReportTypeTransactionClass.getId() != null){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_TRANSACTION_CLASS_ALREADY_EXISTS);
            }
            //设置条件
            if ( expenseReportTypeTransactionClassMapper.selectList(
                    new EntityWrapper<ExpenseReportTypeTransactionClass>()
                            .eq("report_type_id",expenseReportTypeTransactionClass.getReportTypeId())
                            .eq("transaction_class_id",expenseReportTypeTransactionClass.getTransactionClassId())
            ).size() > 0 ){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_TRANSACTION_CLASS_NOT_ALLOWED_TO_REPEAT);
            }

            this.insert(expenseReportTypeTransactionClass);
        });
        return list;
    }

    /**
     * 根据报账单类型id批量删除：报账单类型关联付款用途表
     * @param reportTypeId
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpenseReportTypeTransactionClassByReportTypeIdBatch(Long reportTypeId){
        baseMapper.delete(new EntityWrapper<ExpenseReportTypeTransactionClass>().eq("report_type_id", reportTypeId));
    }
}
