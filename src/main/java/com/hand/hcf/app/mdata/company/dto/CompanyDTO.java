package com.hand.hcf.app.mdata.company.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * A DTO for the Company entity.
 */
@Data
public class CompanyDTO implements Serializable {

    private static final long serialVersionUID = -4346328410488755526L;

    private Long id;
    private UUID groupCompanyOid;
    private UUID companyOid;

    @NotNull
    @Size(max = 255)
    private String name;

    private String logoURL;

    @JsonIgnore
    private String createdBy;

    private ZonedDateTime createdDate;

    private Boolean doneRegisterLead;

    private String taxId;
    private Boolean enabled;
    private int noticeType;
    private int dimissionDelayDays;
    private int passwordExpireDays;
    private String passwordRule;
    private int passwordLengthMin;
    private int passwordLengthMax;
    private int passwordRepeatTimes;
    private int createDataType;
    private int passwordAttemptTimes;
    private int autoUnlockDuration;

    private String companyCode;
    private String address;
   // @JsonIgnore

    private Long companyLevelId;
    /**
     * 公司级别名称
     */
    private String companyLevelName;
    //@JsonIgnore

    private Long parentCompanyId;
    /**
     * 上级公司名称
     */
    private String parentCompanyName;

    private ZonedDateTime startDateActive;

    private ZonedDateTime endDateActive;
    //@JsonIgnore
    /**
     * 公司类型CODE
     */

    private String companyTypeCode;
    /**
     * 公司类型名称
     */
    private String companyTypeName;
    //@JsonIgnore
    /**
     * 账套id
     */

    private Long setOfBooksId;
    /**
     * 账套名称
     */
    private String setOfBooksName;
    //@JsonIgnore
    /**
     * 法人实体id
     */

    private Long legalEntityId;
    /**
     * 法人实体名称
     */
    private String legalEntityName;
    /**
     * 本位币
     */
    private String baseCurrency = "CNY";
    /**
     * 本位币名称
     */
    private String baseCurrencyName = "人民币";
    /**
     * 租户id
     */

    private Long tenantId;
    /**
     * 多语言
     */
    private Map<String, List<Map<String, String>>> i18n;
    private String path;
    private Integer depth;
    private Boolean showCustomLogo;

    private Boolean enableMobileModify;
    private Boolean enableEmailModify;
    /**
     * 是否允许通过接口修改员工密码
     */
    private Boolean enablePasswordModify;

    private Boolean companyUnitFlag;

    /**
     * 子公司列表
     */
    private List<CompanyDTO> children;

}
