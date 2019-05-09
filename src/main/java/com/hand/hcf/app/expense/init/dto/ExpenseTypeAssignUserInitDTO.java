package com.hand.hcf.app.expense.init.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhu.zhao
 * @Date: 2019/04/17
 * 申请类型/费用类型适用初始化DTO
 */
@Data
@ApiModel(description = "申请类型/费用类型适用初始化DTO")
public class ExpenseTypeAssignUserInitDTO {
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
     * 费用类型id
     */
    @ApiModelProperty(value = "费用类型id")
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
     * 适用类型
     */
    @ApiModelProperty(value = "适用类型")
    private Integer applyType;

    /**
     * 适用ID
     */
    @ApiModelProperty(value = "适用ID")
    private Long userTypeId;

    /**
     * 适用code
     */
    @ApiModelProperty(value = "适用code")
    private String userTypeCode;
}
