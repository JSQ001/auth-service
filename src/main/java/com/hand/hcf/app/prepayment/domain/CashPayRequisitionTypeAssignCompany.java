package com.hand.hcf.app.prepayment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by 韩雪 on 2017/10/25.
 */
@ApiModel(description = "预付款单类型关联的公司表")
@Data
@TableName("csh_sob_pay_req_t_ass_company")
public class CashPayRequisitionTypeAssignCompany extends DomainEnable {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "sob_pay_req_type_id")
    @ApiModelProperty(value = "预付款单类型ID")
    private Long sobPayReqTypeId;//预付款单类型ID

    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "company_id")
    @ApiModelProperty(value = "公司ID")
    private Long companyId;//公司ID

    //公司代码
    @TableField("company_code")
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    //公司名称
    @TableField(exist = false)
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    //公司类型
    @TableField(exist = false)
    @ApiModelProperty(value = "公司类型")
    private String companyType;
}
