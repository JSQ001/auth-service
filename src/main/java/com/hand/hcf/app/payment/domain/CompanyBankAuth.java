package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by 刘亮 on 2017/9/28.
 */
@TableName("csh_company_bank_auth")
@Data
public class CompanyBankAuth extends DomainLogicEnable {
    private static final long serialVersionUID = -678566998320573466L;
    @TableField("bank_account_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bankAccountId;
    @TableField("authorize_company_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long authorizeCompanyId;
    @TableField("authorize_department_id")
    private Long authorizeDepartmentId;
    @TableField("authorize_employee_id")
    private UUID authorizeEmployeeId;
    @TableField("authorize_date_from")
    private ZonedDateTime authorizeDateFrom;
    @TableField("authorize_date_to")
    private ZonedDateTime authorizeDateTo;

    @TableField("company_code")
    private String companyCode;
    @TableField("department_code")
    private String departmentCode;
    @TableField("employee_code")
    private String employeeCode;

    /**
     *     1001：授权到公司
     *     1002：授权到公司和部门交集
     *     1003：授权到员工
     */
    @NotNull
    @TableField("auth_flag")
    private Integer authFlag;
}
