package com.hand.hcf.app.expense.report.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 报账单类型关联公司表
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/26
 */
@ApiModel(description = "报账单类型关联公司表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("exp_report_type_company")
public class ExpenseReportTypeCompany extends DomainEnable{
    //报账单类型ID
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "report_type_id")
    @ApiModelProperty(value = "报账单类型ID")
    private Long reportTypeId;

    //公司ID
    @ApiModelProperty(value = "公司ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "company_id")
    private Long companyId;

    //公司代码
    @ApiModelProperty(value = "公司代码")
    @TableField("company_code")
    private String companyCode;

    //公司名称
    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String companyName;

    //公司类型
    @ApiModelProperty(value = "公司类型")
    @TableField(exist = false)
    private String companyType;
}
