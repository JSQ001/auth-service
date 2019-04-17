package com.hand.hcf.app.expense.application.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainEnable;
import com.hand.hcf.app.expense.common.utils.SqlConditionExpanse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/8
 */
@Data
@TableName("exp_application_type")
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationType extends DomainEnable {

    /**
     * 代码
     */
    @NotNull
    @TableField(value = "type_code", condition = SqlConditionExpanse.LIKE)
    private String typeCode;
    /**
     * 名称
     */
    @TableField(value = "type_name", condition = SqlConditionExpanse.LIKE)
    private String typeName;
    /**
     * 账套ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long setOfBooksId;

    @TableField(exist = false)
    private String setOfBooksName;
    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 表单OID
     */
    private String formOid;
    /**
     * 表单名称
     */
    private String formName;
    /**
     * 表单类型
     */
    private Integer formType;
    /**
     * 是否预算管控
     */
    private Boolean budgetFlag = false;
    /**
     * 是否关联全部申请类型
     */
    private Boolean allFlag;
    /**
     * 是否可以关联合同
     */
    private Boolean associateContract = false;
    /**
     * 合同是否必输
     */
    private Boolean requireInput = false;

    private String applyEmployee;

    //是否可同时发起预付款标志（true：可发起，false：不可发起）
    @TableField("pre_payment_flag")
    private Boolean prePaymentFlag;



    public ApplicationType(String typeCode, Long setOfBooksId){
        this.setOfBooksId = setOfBooksId;
        this.typeCode = typeCode;
        this.requireInput = null;
        this.associateContract = null;
        this.budgetFlag = null;
    }
}
