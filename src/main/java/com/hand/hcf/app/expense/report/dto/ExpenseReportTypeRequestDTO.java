package com.hand.hcf.app.expense.report.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hcf.app.core.serializer.CollectionToStringSerializer;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/22
 */
@Data
public class ExpenseReportTypeRequestDTO {
    /**
     * 报账单类型
     */
    @NotNull
    private ExpenseReportType expenseReportType;

    /**
     * 部分费用类型id集合
     */
    @JsonSerialize(using = CollectionToStringSerializer.class)
    private List<Long> expenseTypeIdList;

    /**
     * 部分付款用途id集合
     */
    @JsonSerialize(using = CollectionToStringSerializer.class)
    private List<Long> cashTransactionClassIdList;

    /**
     * 部门或人员组id集合
     */
    @JsonSerialize(using = CollectionToStringSerializer.class)
    private List<Long> departmentOrUserGroupIdList;

    /**
     * 部门或人员组集合
     */
    private List<DepartmentOrUserGroupDTO> departmentOrUserGroupList;
}
