package com.hand.hcf.app.expense.application.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/11/15
 */
@ApiModel(description = "费用申请单类型关联机构")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exp_application_type_company")
public class ApplicationTypeAssignCompany extends DomainEnable {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "公司ID")
    private Long companyId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "申请类型ID")
    private Long applicationTypeId;
    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 公司类型
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司类型")
    private String companyTypeName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

}
