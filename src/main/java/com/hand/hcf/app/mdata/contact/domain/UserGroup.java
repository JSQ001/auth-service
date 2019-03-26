package com.hand.hcf.app.mdata.contact.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by markfredchen on 16/10/2.
 */
@Data
@TableName("sys_user_group" )
public class UserGroup extends DomainI18nEnable {

    @NotNull
    @TableField(value = "user_group_oid")
    private UUID userGroupOid;

    @TableField(value = "company_oid")
    private UUID companyOid;

    @NotEmpty
    @I18nField
    private String name;

    @NotNull
    private Boolean enabled = true;

    @Column(name = "comment_")
    @TableField("comment_")
    @I18nField
    private String comments;

    private String code;    // 人员组编码

    private String type;


    private Long tenantId;


}
