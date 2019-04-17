package com.hand.hcf.app.expense.policy.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.core.annotation.UniqueField;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @description: 费用政策定义表
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/29 10:32
 */
@Data
@TableName(value = "exp_expense_policy")
public class ExpensePolicy extends DomainLogicEnable {
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 账套ID
     */
    private Long setOfBooksId;
    /**
     * 优先级
     */
    @UniqueField
    private Long priority;
    /**
     * 公司级别ID
     */
    @TableField(strategy = FieldStrategy.IGNORED)
    private Long companyLevelId;
    /**
     * 费用类型ID
     */
    private Long expenseTypeId;
    /**
     * 费用类型标识
     */
    private Integer expenseTypeFlag;
    /**
     * 申请人职务
     */
    @TableField(strategy = FieldStrategy.IGNORED)
    private String dutyType;
    /**
     * 申请人员工级别
     */
    @TableField(strategy = FieldStrategy.IGNORED)
    private String staffLevel;
    /**
     * 申请人所属部门ID
     */
    @TableField(strategy = FieldStrategy.IGNORED)
    private Long departmentId;
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
    private ZonedDateTime startDate;
    /**
     * 有效日期至
     */
    private ZonedDateTime endDate;
    /**
     * 全部/部分公司标识
     */
    private Boolean allCompanyFlag;
}
