package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;
import io.swagger.annotations.*;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by 刘亮 on 2017/9/28.
 */
@ApiModel(description = "银行账户授权实体类")
@TableName("csh_company_bank_auth")
@Data
public class CompanyBankAuth extends DomainLogicEnable {
    private static final long serialVersionUID = -678566998320573466L;
    @ApiModelProperty(value = "银行账户id")
    @TableField("bank_account_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bankAccountId;

    @ApiModelProperty(value = "公司授权id")
    @TableField("authorize_company_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long authorizeCompanyId;

    @ApiModelProperty(value = "授权部门Id")
    @TableField("authorize_department_id")
    private Long authorizeDepartmentId;

    @ApiModelProperty(value = "授权雇员Id")
    @TableField("authorize_employee_id")
    private UUID authorizeEmployeeId;

    @ApiModelProperty(value = "授权日期从")
    @TableField("authorize_date_from")
    private ZonedDateTime authorizeDateFrom;

    @ApiModelProperty(value = "授权日期至")
    @TableField("authorize_date_to")
    private ZonedDateTime authorizeDateTo;

    @ApiModelProperty(value = "公司代码")
    @TableField("company_code")
    private String companyCode;

    @ApiModelProperty(value = "部门代码")
    @TableField("department_code")
    private String departmentCode;

    @ApiModelProperty(value = "员工代码")
    @TableField("employee_code")
    private String employeeCode;

    /**
     *     1001：授权到公司
     *     1002：授权到公司和部门交集
     *     1003：授权到员工
     */
    @ApiModelProperty(value = "授权范围")
    @NotNull
    @TableField("auth_flag")
    private Integer authFlag;
}
