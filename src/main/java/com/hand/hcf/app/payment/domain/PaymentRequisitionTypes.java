package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: bin.xie
 * @Description: 付款申请单类型实体类
 * @Date: Created in 10:50 2018/1/22
 * @Modified by
 */
@Data
@TableName("csh_req_types")
public class PaymentRequisitionTypes extends DomainLogicEnable {
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; //主键ID

    @TableField(value = "acp_req_type_code")
    @NotNull
    private String acpReqTypeCode; //付款申请单代码

    @TableField(value = "description")
    @NotNull
    private String description; //付款申请单名称

    @TableField(value = "tenant_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;//租户ID

    @TableField(value = "set_of_books_id")
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId; //账套ID

    @TableField(value = "is_related")
    private Boolean related; //是否关联报账单

    @TableField(value = "according_as_related")
    private String accordingAsRelated;//关联报账单的依据  BASIS_01 -- 申请人=报账单申请人

    @TableField(value = "related_type")
    private String relatedType; // BASIS_01 -- 全部类型 BASIS_02 --部分类型

    @TableField(value = "form_oid")
    private String formOid;//关联表单OID

    @TableField(value = "form_name")
    private String formName;//关联表单名称

    @TableField(value = "form_type")
    private String formType;//关联表单类型

    @TableField(value = "apply_employee")
    private String applyEmployee; //适用人员  BASIS_01 -- 全部 BASIS_02 --部门 BASIS_03 --人员组

}
