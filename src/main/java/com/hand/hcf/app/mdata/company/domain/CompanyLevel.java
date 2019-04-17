package com.hand.hcf.app.mdata.company.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by fanfuqiang 2018/11/21
 */
@TableName("sys_company_levels")
@Data
public class CompanyLevel extends DomainI18nEnable implements Serializable {


    private static final long serialVersionUID = -695102950057668329L;

    @NotNull
    @TableField("company_level_code")
    private String companyLevelCode;

    @TableField("tenant_id")

    @JsonIgnore
    private Long tenantId;

    @Length(max = 100)
    @I18nField
    @TableField("description")
    private String description;

}
