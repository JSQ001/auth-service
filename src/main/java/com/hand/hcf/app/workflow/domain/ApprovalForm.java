package com.hand.hcf.app.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18n;
import lombok.Data;

import javax.persistence.Transient;
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
    private String formCode;
    private Boolean submitFlag = false;//申请单控制报销单的提交，报销单的金额超申请是否可以继续提交,默认不能继续提交
    //账号ID
    private Integer fromType;
    private Integer approvalMode;
    @Transient
    @TableField(exist = false)
    private String iconUrl;

}
