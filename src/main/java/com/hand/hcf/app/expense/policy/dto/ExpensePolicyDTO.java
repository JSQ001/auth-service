package com.hand.hcf.app.expense.policy.dto;

import com.hand.hcf.app.expense.policy.domain.ExpensePolicy;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicyControlDimension;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicyDynamicField;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicyRelatedCompany;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/29 10:32
 */
@Data
public class ExpensePolicyDTO extends ExpensePolicy {
    /**
     * 账套名称
     */
    private String setOfBooksName;
    /**
     * 公司级别名称
     */
    private String companyLevelName;
    /**
     * 费用类型名称
     */
    private String expenseTypeName;
    /**
     * 申请人职务名称
     */
    private String dutyTypeName;
    /**
     * 申请人员工级别名称
     */
    private String staffLevelName;
    /**
     * 申请人所属部门名称
     */
    private String departmentName;
    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 控制策略值名称
     */
    private String controlStrategyName;
    /**
     * 费用政策动态字段
     */
    List<ExpensePolicyDynamicField> dynamicFields;
    /**
     * 费用政策关联公司
     */
    List<ExpensePolicyRelatedCompany> relatedCompanies;
    /**
     * 控制维度
     */
    List<ExpensePolicyControlDimension> controlDimensions;
    /**
     * 费用类型信息
     */
    ExpenseTypeWebDTO expenseTypeInfo;
}
