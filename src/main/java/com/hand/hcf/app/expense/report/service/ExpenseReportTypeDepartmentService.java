package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeDepartment;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeDepartmentMapper;
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
public class ExpenseReportTypeDepartmentService extends BaseService<ExpenseReportTypeDepartmentMapper,ExpenseReportTypeDepartment>{

    @Autowired
    private ExpenseReportTypeDepartmentMapper expenseReportTypeDepartmentMapper;

    /**
     * 批量新增 报账单类型关联部门表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseReportTypeDepartment> createExpenseReportTypeDepartmentBatch(List<ExpenseReportTypeDepartment> list){
        list.stream().forEach(expenseReportTypeDepartment -> {
            if(expenseReportTypeDepartment.getId() != null){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DEPARTMENT_ALREADY_EXISTS);
            }
            //设置条件
            if ( expenseReportTypeDepartmentMapper.selectList(
                    new EntityWrapper<ExpenseReportTypeDepartment>()
                            .eq("report_type_id",expenseReportTypeDepartment.getReportTypeId())
                            .eq("department_id",expenseReportTypeDepartment.getDepartmentId())
            ).size() > 0 ){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_DEPARTMENT_NOT_ALLOWED_TO_REPEAT);
            }

            this.insert(expenseReportTypeDepartment);
        });
        return list;
    }
}
