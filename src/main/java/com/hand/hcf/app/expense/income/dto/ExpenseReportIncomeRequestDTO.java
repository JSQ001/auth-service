package com.hand.hcf.app.expense.income.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hcf.app.core.serializer.CollectionToStringSerializer;
import com.hand.hcf.app.expense.income.domain.ExpenseIncomeAssign;
import com.hand.hcf.app.expense.report.dto.DepartmentOrUserGroupDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: dazhuang.xie@hand-china.com
 * @date: 2019/6/21
 */
@ApiModel(description = "费用报账类型请求DTO")
@Data
public class ExpenseReportIncomeRequestDTO {
    /**
     * 报账单类型
     */
    @NotNull
    @ApiModelProperty(value = "收入报账单类型")
    private ExpenseIncomeAssign expenseIncomeAssign;

    /**
     * 部分费用类型id集合
     */
    @ApiModelProperty(value = "部分费用类型id集合")
    @JsonSerialize(using = CollectionToStringSerializer.class)
    private List<Long> expenseTypeIdList;

    /**
     * 部分付款用途id集合
     */
    @ApiModelProperty(value = "部分付款用途id集合")
    @JsonSerialize(using = CollectionToStringSerializer.class)
    private List<Long> cashTransactionClassIdList;

    /**
     * 部门或人员组id集合
     */
    @ApiModelProperty(value = "部门或人员组id集合")
    @JsonSerialize(using = CollectionToStringSerializer.class)
    private List<Long> departmentOrUserGroupIdList;

    /**
     * 部门或人员组集合
     */
    @ApiModelProperty(value = "部门或人员组集合")
    private List<DepartmentOrUserGroupDTO> departmentOrUserGroupList;
}
