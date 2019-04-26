package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.payment.domain.CompanyBank;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by 刘亮 on 2017/9/29.
 */
@Data
public class CompanyBankAuthDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bankAccountId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bankAccountCompanyId;
    private String bankAccountCompanyCode;
    private String bankAccountCompanyName;
    private String companyCode;
    private String companyName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private UUID authorizeEmployeeId;
    private String employee;
    private String employeeCode;
    private String employeeName;
    private String employeeJob;
    private String employeeJobCode;
    private ZonedDateTime authorizeDateFrom;
    private ZonedDateTime authorizeDateTo;
    private Boolean enabled;
    private Boolean deleted;
    private Integer versionNumber;
    private Long createdBy;
    private ZonedDateTime createdDate;
    private Integer authFlag;
    private CompanyBank companyBank;
}
