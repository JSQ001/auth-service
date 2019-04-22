package com.hand.hcf.app.expense.init.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhu.zhao
 * @Date: 2019/04/17
 * 费用政策初始化DTO
 */
@Data
public class ExpensePolicyInitDTO {
    /**
     * 校验信息
     */
    private Map<String, List<String>> resultMap;
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 账套ID
     */
    private Long setOfBooksId;
    /**
     * 账套code
     */
    private String setOfBooksCode;
    /**
     * 优先级
     */
    private Long priority;
    /**
     * 公司级别ID
     */
    private Long companyLevelId;

    /**
     * 公司级别code
     */
    private String companyLevelCode;
    /**
     * 费用类型ID
     */
    private Long expenseTypeId;
    /**
     * 费用类型code
     */
    private String expenseTypeCode;
    /**
     * 费用类型标识
     */
    private Integer expenseTypeFlag;
    /**
     * 申请人职务
     */
    private String dutyType;
    /**
     * 申请人员工级别
     */
    private String staffLevel;
    /**
     * 申请人所属部门ID
     */
    private Long departmentId;
    /**
     * 申请人所属部门code
     */
    private String departmentCode;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 控制策略值CODE
     */
    private String controlStrategyCode;
    /**
     * 消息值CODE
     */
    private String messageCode;
    /**
     * 判断符号
     */
    private String judgementSymbol;
    /**
     * 控制维度类型
     */
    private String controlDimensionType;
    /**
     * 有效日期从
     */
    private String startDateStr;
    /**
     * 有效日期从
     */
    private ZonedDateTime startDate;
    /**
     * 有效日期从
     */
    private String endDateStr;
    /**
     * 有效日期至
     */
    private ZonedDateTime endDate;
    /**
     * 全部/部分公司标识
     */
    private String allCompanyFlagStr;
    /**
     * 全部/部分公司标识
     */
    private Boolean allCompanyFlag;

    /**
     * 启用标识
     */
    private String enabledStr;

    /**
     * 启用标识
     */
    private Boolean enabled;

    /**
     * 公司code
     */
    private Long companyId;

    /**
     * 公司code
     */
    private String companyCode;

    private String value;
}
