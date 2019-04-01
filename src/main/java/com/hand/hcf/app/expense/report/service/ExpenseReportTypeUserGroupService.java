package com.hand.hcf.app.expense.report.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.report.domain.ExpenseReportTypeUserGroup;
import com.hand.hcf.app.expense.report.persistence.ExpenseReportTypeUserGroupMapper;
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
public class ExpenseReportTypeUserGroupService extends BaseService<ExpenseReportTypeUserGroupMapper,ExpenseReportTypeUserGroup>{

    @Autowired
    private ExpenseReportTypeUserGroupMapper expenseReportTypeUserGroupMapper;

    /**
     * 批量新增 报账单类型关联人员组表
     *
     * @param list
     * @return
     */
    @Transactional
    public List<ExpenseReportTypeUserGroup> createExpenseReportTypeUserGroupBatch(List<ExpenseReportTypeUserGroup> list){
        list.stream().forEach(expenseReportTypeUserGroup -> {
            if(expenseReportTypeUserGroup.getId() != null){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_USER_GROUP_ALREADY_EXISTS);
            }
            //设置条件
            if ( expenseReportTypeUserGroupMapper.selectList(
                    new EntityWrapper<ExpenseReportTypeUserGroup>()
                            .eq("report_type_id",expenseReportTypeUserGroup.getReportTypeId())
                            .eq("user_group_id",expenseReportTypeUserGroup.getUserGroupId())
            ).size() > 0 ){
                throw new BizException(RespCode.EXPENSE_REPORT_TYPE_USER_GROUP_NOT_ALLOWED_TO_REPEAT);
            }

            this.insert(expenseReportTypeUserGroup);
        });
        return list;
    }
}
