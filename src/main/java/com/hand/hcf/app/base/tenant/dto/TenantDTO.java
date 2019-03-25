package com.hand.hcf.app.base.tenant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by Ray Ma on 2017/9/5.
 */
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantDTO {

    private Long id;
    @NotNull
    private String tenantName;
    private String tenantShortName;
    private Integer licenseLimit;
    private String status;
    private String tenantCode;

    private Long logoId;
    private Boolean showCustomLogo;
    private String logoURL;

    //UTM
    private String utmSource;
    private String utmCampaign;
    private String utmMedium;
    private String countryCode;

    private Boolean enableNewControl;
    private Boolean licensed;


    private Long lastUpdatedBy;

    private ZonedDateTime lastUpdatedDate;

    @NotNull
    private String companyCode;
    //新建法人实体入参
    private String address;
    private String taxpayerNumber;
    private String accountBank;
    private String telephone;
    private String accountNumber;
    private String baseLanguage;


    //租户管理员信息
    private UUID userOid;
    private String login;
    private String password;
    private String fullName;
    private String mobile;
    private String email;
    private String employeeId;
    private String title;

    private String taxId;       //税务证件号


}
