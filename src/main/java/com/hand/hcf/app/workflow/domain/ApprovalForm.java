package com.hand.hcf.app.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18n;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;
import java.util.UUID;

/**
 */
@Data
@TableName("sys_approval_form")
public class ApprovalForm extends DomainI18n {
    private UUID formOid;
    @I18nField
    private String formName;
    private String iconName;
    private Integer formTypeId;
    private String messageKey;
    private Boolean asSystem;
    private Boolean valid;
    private Long parentId;
    @I18nField
    private String remark;
    //申请关联报销单 表单
    private UUID referenceOid;
    private Long tenantId;
    private Long companyId;
    //用户可见范围，1001，全部可见；1002，自定义用户组可见
    private Integer visibleUserScope;
    //可选费用类型范围，1001，全部，1002，自定义可选费用类型
    private Integer visibleExpenseTypeScope;
    private String formCode;
    private Integer expenseTypeCheckStatus; //默认1000：不做处理，1001：按总费用校验申请，1002：按费用类型校验
    private Boolean submitFlag = false;//申请单控制报销单的提交，报销单的金额超申请是否可以继续提交,默认不能继续提交
    //账号ID
    private Long setOfBooksId;
    private Integer fromType;
    private Integer visibleCompanyScope;
    private Integer approvalMode;
    @Transient
    @TableField(exist = false)
    private List<UUID> companyOIDList;
    @Transient
    @TableField(exist = false)
    private String iconUrl;

}
