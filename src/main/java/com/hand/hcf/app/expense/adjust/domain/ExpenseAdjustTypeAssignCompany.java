package com.hand.hcf.app.expense.adjust.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 韩雪 on 2018/3/15.
 */
@ApiModel(description = "费用调整单类型关联公司表")
@Data
@TableName("exp_adjust_type_assign_com")
public class ExpenseAdjustTypeAssignCompany  extends Domain{
    //id
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId("id")
    @ApiModelProperty(value = "id")
    private Long id;

    //费用调整单类型ID
    @ApiModelProperty(value = "费用调整单类型ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("exp_adjust_type_id")
    private Long expAdjustTypeId;

    //公司ID
    @ApiModelProperty(value = "公司ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("company_id")
    private Long companyId;

    //公司代码
    @ApiModelProperty(value = "公司代码")
    @TableField("company_code")
    private String companyCode;

    //启用标志
    @ApiModelProperty(value = "启用标志")
    @TableField("enabled")
    private Boolean enabled;

    //公司名称
    @ApiModelProperty(value = "公司名称")
    @TableField(exist = false)
    private String companyName;

    //公司类型
    @ApiModelProperty(value = "公司类型")
    @TableField(exist = false)
    private String companyType;
}
