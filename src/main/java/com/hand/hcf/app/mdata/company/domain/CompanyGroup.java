package com.hand.hcf.app.mdata.company.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by fanfuqiang 2018/11/21
 */
@TableName("sys_company_group")
@Data
public class CompanyGroup extends DomainI18nEnable implements Serializable {

    @TableField(value = "company_group_code")
    private String companyGroupCode;

    @TableField(value = "company_group_name")
    @I18nField
    private String companyGroupName;

    @TableField(value = "description")
    private String description;


    @TableField(value = "set_of_books_id")
    private Long setOfBooksId;


    @TableField(value = "tenant_id")
    private Long tenantId;
}
