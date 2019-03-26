package com.hand.hcf.app.mdata.legalEntity.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * 法人实体类
 * Created by Strive on 17/9/4.
 */
@TableName(value = "sys_legal_entity")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class LegalEntity extends DomainI18nEnable implements Serializable {
    /**
     * 法人实体oid
     */
    @TableField(value = "legal_entity_oid")
    private UUID legalEntityOid;
    /**
     * 租户id
     */
    @TableField(value = "tenant_id")
    private Long tenantId;
    /**
     * 公司id
     */
    @TableField(value = "company_id")
    private Long companyId;
    /**
     * 上级法人实体id
     */
    @TableField(value = "parent_legal_entity_id")
    private Long parentLegalEntityId;
    /**
     * 法人实体名称
     */
    @I18nField
    @TableField(value = "entity_name")
    private String entityName;
    /**
     * 地址
     */
    @I18nField
    @TableField(value = "address")
    private String address;
    /**
     * 纳税人识别号
     */
    @TableField(value = "taxpayer_number")
    private String taxpayerNumber;
    /**
     * 开户银行
     */
    @I18nField
    @TableField(value = "account_bank")
    private String accountBank;
    /**
     * 电话
     */
    @TableField(value = "telephone")
    private String telePhone;
    /**
     * 银行账户
     */
    @TableField(value = "account_number")
    private String accountNumber;
    /**
     * 账套id
     */
    @TableField(value = "set_of_books_id")
    private Long setOfBooksId;
    /**
     * 附件id
     */
    @TableField(value = "attachment_id")
    private Long attachmentId;

    @TableField(value = "path")
    private String path;

    @TableField(value = "depth")
    private Integer depth;
    /**
     * 主语言
     */
    @TableField(value = "main_language")
    private String mainLanguage;
}
