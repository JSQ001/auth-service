package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.enums.SourceEnum;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @Author: 魏胜
 * @Description:
 * @Date: 2018/4/4 15:15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorInfoCO extends DomainObjectDTO {

    private String venNickname;
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;

    private String companyOid;

    private String venNickOid;

    private String venderCode;

    /**
     * 自动编码标记
     */
    private Boolean autoCodeMark;

    private Integer venType;

    private String venOperatorNumber;

    private String venOperatorName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long venderTypeId;

    private String venderTypeName;

    private String importCode;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long industryId;

    private String industryName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long venderLevelId;

    private String venderLevelName;

    private String artificialPerson;

    private String contact;

    private String contactPhone;

    private String contactMail;

    private String taxIdNumber;

    private String fax;

    private String country;

    private String address;

    private String notes;

    private ZonedDateTime effectiveDate;

    private List<VendorTypeCO> venTypes;

    private List<VendorBankAccountCO> venBankAccountBeans;

    /**
     * 前端展示更新日期
     */
    private ZonedDateTime webUpdateDate;

    private ZonedDateTime createTime;

    private ZonedDateTime updateTime;

    private SourceEnum source;
}
