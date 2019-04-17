package com.hand.hcf.app.expense.travel.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;

/**
 * 差旅申请单类型定义
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Data
@TableName("exp_travel_app_type")
public class TravelApplicationType extends DomainI18nEnable {
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 账套ID
     */
    private Long setOfBooksId;
    /**
     * 类型代码
     */
    private String code;
    /**
     * 类型名称
     */
    @I18nField
    private String name;
    /**
     * 关联表单ID
     */
    private Long formId;
    /**
     * 预算管控标志（true启用，false不启用）
     */
    private Boolean budgetFlag;
    /**
     * 可用申请类型（true全部，false部分）
     */
    private Boolean allTypeFlag;
    /**
     * 行程填写（true审批前，false审批后）
     */
    private Boolean route;
    /**
     * 可见人员范围（1001全部，1002部门，1003人员组）
     */
    private Integer visibleUserScope;
}
