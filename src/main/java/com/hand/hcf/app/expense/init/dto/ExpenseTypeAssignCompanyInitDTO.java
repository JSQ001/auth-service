package com.hand.hcf.app.expense.init.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhu.zhao
 * @Date: 2019/04/17
 * 申请类型/费用类型分配公司初始化DTO
 */
@ApiModel(description = "业务规则描述")
@Data
public class ExpenseTypeAssignCompanyInitDTO {
    /**
     * 校验信息
     */
    @ApiModelProperty(value = "校验信息")
    private Map<String, List<String>> resultMap;

    /**
     * 账套id
     */
    @ApiModelProperty(value = "账套id")
    private Long setOfBooksId;

    /**
     * 账套code
     */
    @ApiModelProperty(value = "账套code")
    private String setOfBooksCode;

    /**
     * 类型代码id
     */
    @ApiModelProperty(value = "类型代码id")
    private Long expenseTypeId;

    /**
     * 类型 0-申请类型 1-费用类型
     */
    @ApiModelProperty(value = "类型 0-申请类型 1-费用类型")
    private Integer typeFlag;

    /**
     * 类型代码code
     */
    @ApiModelProperty(value = "类型代码code")
    private String code;

    /**
     * 公司code
     */
    @ApiModelProperty(value = "公司id")
    private Long companyId;

    /**
     * 公司code
     */
    @ApiModelProperty(value = "公司code")
    private String companyCode;



}
