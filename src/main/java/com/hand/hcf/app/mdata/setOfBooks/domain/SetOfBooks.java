package com.hand.hcf.app.mdata.setOfBooks.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by fanfuqiang 2018/11/20
 */
@TableName("sys_set_of_books")
@Data
public class SetOfBooks extends DomainI18nEnable implements Serializable {

    @TableField(value = "set_of_books_code")
    private String setOfBooksCode;

    @TableField(value = "set_of_books_name")
    @I18nField
    private String setOfBooksName;

    @TableField(value = "period_set_code")
    private String periodSetCode;

    @TableField(value = "functional_currency_code")
    private String functionalCurrencyCode;

    @TableField(value = "account_set_id")

    private Long accountSetId;

    @TableField(value = "tenant_id")
    @JsonIgnore

    private Long tenantId;

}
