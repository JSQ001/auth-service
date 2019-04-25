package com.hand.hcf.app.expense.init.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: zhu.zhao
 * @Date: 2019/04/17
 * 申请类型/费用类型适用初始化DTO
 */
@Data
public class ExpenseTypeAssignUserInitDTO {
    /**
     * 校验信息
     */
    private Map<String, List<String>> resultMap;
    /**
     * 账套id
     */
    private Long setOfBooksId;

    /**
     * 账套code
     */
    private String setOfBooksCode;
    /**
     * 费用类型id
     */
    private Long expenseTypeId;
    /**
     * 类型 0-申请类型 1-费用类型
     */
    private Integer typeFlag;

    /**
     * 类型代码code
     */
    private String code;

    /**
     * 适用类型
     */
    private Integer applyType;

    /**
     * 适用ID
     */
    private Long userTypeId;

    /**
     * 适用code
     */
    private String userTypeCode;
}