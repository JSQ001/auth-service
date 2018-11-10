package com.hand.hcf.app.base.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldFill;
import lombok.Data;
import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by markfredchen on 16/3/20.
 */
@Data
@TableName(value = "art_company_configuration")
public class CompanyConfiguration {

    @TableField(value = "company_oid")
    private UUID companyOID;

    @TableField(value = "currency_code")
    private String currencyCode;

    private ConfigurationDetail configuration = new ConfigurationDetail();

    @TableField(value="last_modified_date",fill= FieldFill.INSERT_UPDATE)
    protected DateTime lastModifiedDate;
}
