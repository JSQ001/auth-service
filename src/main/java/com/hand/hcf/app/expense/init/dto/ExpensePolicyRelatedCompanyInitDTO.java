package com.hand.hcf.app.expense.init.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhu.zhao
 * @Date: 2019/04/26
 * 申请政策分配公司初始化DTO
 */
@ApiModel("请政策分配公司初始化DTO")
@Data
public class ExpensePolicyRelatedCompanyInitDTO {

    @ApiModelProperty(value = "校验信息")
    private Map<String, List<String>> resultMap;

    @ApiModelProperty(value = "账套id")
    private Long tenantId;

    @ApiModelProperty(value = "账套id")
    private Long setOfBooksId;

    @ApiModelProperty(value = "账套code")
    private String setOfBooksCode;

    @ApiModelProperty(value = "优先级")
    private Long priority;

    @ApiModelProperty(value = "类型代码id")
    private Long expenseTypeId;

    @ApiModelProperty(value = "类型 0-申请类型 1-费用类型")
    private Integer typeFlag;

    @ApiModelProperty(value = "类型代码code")
    private String code;

    @ApiModelProperty(value = "政策定义表id")
    private Long expExpensePolicyId;

    @ApiModelProperty(value = "公司id")
    private Long companyId;

    @ApiModelProperty(value = "公司code")
    private String companyCode;
}
