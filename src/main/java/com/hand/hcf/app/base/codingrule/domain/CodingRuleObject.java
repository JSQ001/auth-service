package com.hand.hcf.app.base.codingrule.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author dong.liu on 2017-08-23
 */
@Data
@TableName("sys_coding_rule_object")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodingRuleObject extends DomainI18nEnable {
	@TableField(value = "tenant_id")

	private Long tenantId;  //租户id
	@TableField("document_type_code")
	private String documentTypeCode; //单据类型代码
	@TableField(exist = false)
	private String documentTypeName; //单据类型名称
	@TableField("company_code")
	private String companyCode; //公司代码
	@TableField(exist = false)
	private String companyName; //公司代码



}
