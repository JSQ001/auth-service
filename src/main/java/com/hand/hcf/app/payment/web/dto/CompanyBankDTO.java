package com.hand.hcf.app.payment.web.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.annotation.I18nField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Created by 刘亮 on 2017/12/19.
 */
@Data
public class CompanyBankDTO {
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonIgnore
    private Long tenantId;
    @NotNull
    @Length(max = 50)
    private String bankName;
    private String bankBranchName;
    @NotNull
    @Length(max = 50)
    private String bankCode;
    @I18nField
    private String bankAccountName;
    @NotNull
    @Length(max = 36)
    private String bankAccountNumber;
    @Length(max = 36)
    @NotNull
    private String currencyCode;
    private String currencyName;
    @NotNull
    private String accountCode;
    @Length(max = 50)
    private String swiftCode;
    private String remark;
    @NotNull
    private String country;
    @NotNull
    private String city;
    @Length(max = 50)
    private String province;
    @Length(max = 50)
    private String provinceCode;
    @Length(max = 50)
    private String countryCode;
    @Length(max = 50)
    private String cityCode;
    private String bankAddress;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    private String setOfBooksCode;
    private String setOfBooksName;
    private String companyCode;
    private String companyName;
    /**
     * 创建时间
     */
    protected ZonedDateTime createdDate;
    /**
     * 最后更改时间
     */
    protected ZonedDateTime lastUpdatedDate;
    /**
     * 创建人
     */
    protected String createdBy;
    /**
     * 最后更新人
     */
    protected String lastUpdatedBy;
    protected Boolean enabled;
    protected Boolean deleted;
    private String accountOpeningAddress;




}
