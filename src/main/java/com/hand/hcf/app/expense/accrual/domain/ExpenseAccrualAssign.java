package com.hand.hcf.app.expense.accrual.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainEnable;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import com.hand.hcf.app.expense.common.utils.SqlConditionExpanse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exp_report_accrual")
public class ExpenseAccrualAssign extends DomainEnable {

// @TableField(value = "id")
// private Integer id;
//
 @TableField(exist = false)
private Integer rn;
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

 @TableField(value = "form_id")
 private Long formId;

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

// @TableField(value = "enabled")
// private Integer enabled;


//
// created_date AS createdDate,
// created_by AS createdBy,
// last_updated_date AS lastUpdatedDate,
// last_updated_by AS lastUpdatedBy,
// version_number AS versionNumber

// public ExpenseAccrualAssign(String typeCode, Long setOfBooksId){
//  this.setOfBooksId = setOfBooksId;
//  this.typeCode = typeCode;
//  this.requireInput = null;
//  this.associateContract = null;
//  this.budgetFlag = null;
// }
}
