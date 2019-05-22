package com.hand.hcf.app.expense.accrual.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 费用预提单类型关联公司
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/5/15
 */
@ApiModel(description = "费用预提单类型关联公司")
@Data
@TableName("exp_accrual_company")
public class ExpenseAccrualCompany extends Domain {

    //费用预提单类型ID
    @ApiModelProperty(value = "费用预提单类型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("exp_accrual_type_id")
    private Long expAccrualTypeId;

    //公司ID
    @ApiModelProperty(value = "公司ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("company_id")
    private Long companyId;

    //启用标志
    @ApiModelProperty(value = "启用标志")
    @TableField("enable_flag")
    private Boolean enableFlag;

    //公司代码
    @ApiModelProperty(value = "公司代码")
    @TableField(exist = false)
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
