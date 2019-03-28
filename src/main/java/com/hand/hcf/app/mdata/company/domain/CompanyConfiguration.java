package com.hand.hcf.app.mdata.company.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@TableName(value = "sys_company_configuration")
public class CompanyConfiguration {

    @TableField(value = "company_oid")
    private UUID companyOid;

    @TableField(value = "currency_code")
    private String currencyCode;

    private ConfigurationDetail configuration = new ConfigurationDetail();

    @TableField(value="last_updated_date",fill= FieldFill.INSERT_UPDATE)
    protected ZonedDateTime lastUpdatedDate;
}
