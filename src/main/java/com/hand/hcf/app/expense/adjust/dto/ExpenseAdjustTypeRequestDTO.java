package com.hand.hcf.app.expense.adjust.dto;

import com.hand.hcf.app.expense.adjust.domain.ExpenseAdjustType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@ApiModel(description = "费用调整单类型请求")
@Data
public class ExpenseAdjustTypeRequestDTO {
    /*费用调整单类型定义*/
    @NotNull
    @ApiModelProperty(value = "费用调整单类型定义")
    private ExpenseAdjustType expenseAdjustType;

    /*关联费用类型id集合*/
    @ApiModelProperty(value = "关联费用类型id集合")
    private List<Long> expenseIdList;

    /*关联维度id集合*/
    @ApiModelProperty(value = "关联维度id集合")
    private List<Long> dimensionIdList;

    /*关联部门或人员组id集合*/
    @ApiModelProperty(value = "关联部门或人员组id集合")
    private List<Long> departmentOrUserGroupIdList;

    /*关联部门或人员组的对象集合*/
    @ApiModelProperty(value = "关联部门或人员组的对象集合")
    private List<DepartmentOrUserGroupReturnDTO> departmentOrUserGroupList;


    /*根据id查询时，返回关联费用类型id集合*/
    @ApiModelProperty(value = "根据id查询时，返回关联费用类型id集合")
    private List<String> returnExpenseIdList;

    /*根据id查询时，返回关联维度id集合*/
    @ApiModelProperty(value = "根据id查询时，返回关联维度id集合")
    private List<String> returnDimensionIdList;
}
