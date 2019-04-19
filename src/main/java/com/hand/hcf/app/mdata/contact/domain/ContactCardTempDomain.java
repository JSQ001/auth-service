package com.hand.hcf.app.mdata.contact.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@TableName("sys_contact_card_import")
public class ContactCardTempDomain extends Domain {

    @TableField("row_number")
    private String rowNumber;

    @TableField("employee_id")
    private String employeeId;

    @TableField("user_oid")
    private UUID userOid;

    @TableField("contact_card_oid")
    private UUID contactCardOid;

    @TableField("first_name")
    private String firstName;

    @TableField("last_name")
    private String lastName;

    @TableField("nationality_code")
    private String nationalityCode;

    @TableField("nationality")
    private String nationality;

    @TableField("card_type_code")
    private String cardTypeCode;

    @TableField("card_type")
    private Integer cardType;

    @TableField("card_no")
    private String cardNo;

    @TableField("card_expired_time_str")
    private String cardExpiredTimeStr;

    @TableField("card_expired_time")
    private ZonedDateTime cardExpiredTime;

    @TableField("enabled_str")
    private String enabledStr;

    @TableField("enabled")
    private boolean enabled;

    @TableField("primary_str")
    private String primaryStr;

    @TableField("primary_flag")
    @JsonProperty(value = "primary")
    private boolean primaryFlag;

    @TableField("has_primary")
    private boolean hasPrimary;

    @TableField("batch_number")
    private String batchNumber ;

    @TableField("error_detail")
    private String errorDetail;

    @TableField("error_flag")
    private Boolean errorFlag;

}
