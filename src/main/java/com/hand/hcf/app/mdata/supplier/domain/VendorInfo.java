package com.hand.hcf.app.mdata.supplier.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.common.enums.SourceEnum;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/4 15:04
 */
@TableName("ven_vendor_info")
@Data
public class VendorInfo extends Domain {

    private Long tenantId;

    private String companyOid;

    private String vendorId;

    private String vendorName;

    private String vendorCode;

    private Integer status;

    @TableField(exist = false)
    private String lastUpdatedByEmployeeId;

    @TableField(exist = false)
    private String lastUpdatedByName;

    private Long vendorTypeId;

    private String importCode;

    private Long industryId;

    private Long vendorLevelId;

    private String legalRepresentative;

    private String taxId;

    private String contact;

    private String contactPhone;

    private String contactMail;

    private String fax;

    private String country;

    private String address;

    private String remark;

    @TableField(value = "effective_date", strategy = FieldStrategy.IGNORED)
    private ZonedDateTime effectiveDate;

    private SourceEnum source;
    //   add by cjx 审批状态
    private String vendorStatus;
}
