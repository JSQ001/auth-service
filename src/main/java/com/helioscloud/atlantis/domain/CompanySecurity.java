package com.helioscloud.atlantis.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.helioscloud.atlantis.persistence.mybatis.BaseModel;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Transy on 2017/5/17.
 */
@Data
@TableName(value = "art_company_security")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanySecurity extends BaseModel<CompanySecurity> {

    @Id
    protected Long id;

    @TableField(value = "company_oid")
    protected UUID companyOID;

    @TableField(value = "account_code")
    protected String accountCode;

    @TableField(value = "notice_type")
    protected int noticeType;

    @TableField(value = "dimission_delay_days")
    protected int dimissionDelayDays;

    @TableField(value = "password_expire_days")
    protected int passwordExpireDays;

    @TableField(value = "password_rule")
    protected String passwordRule;

    @TableField(value = "password_length_min")
    protected int passwordLengthMin;

    @TableField(value = "password_length_max")
    protected int passwordLengthMax;

    @TableField(value = "password_repeat_times")
    protected int passwordRepeatTimes;

    @TableField(value = "create_data_type")
    private int createDataType;

    @TableField(value = "tenant_id")
    private Long tenantId;
    //解锁时间分钟
    @TableField(value = "auto_unlock_duration")
    private int autoUnlockDuration;

    @TableField(value = "password_attempt_times")
    private int passwordAttemptTimes;

    @TableField(value = "enable_mobile_modify")
    private Boolean enableMobileModify;

    @TableField(value = "enable_email_modify")
    private Boolean enableEmailModify;

    @Override
    protected Serializable pkVal() {
        return getId();
    }
}
