package com.hand.hcf.app.base.tenant.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

import java.io.Serializable;


@TableName("sys_tenant_protocol")
@Data
public class TenantProtocol extends DomainI18nEnable implements Serializable{
    private static final long serialVersionUID = 8847628557206145823L;

    private Long tenantId;
    @I18nField
    private String title;
    @I18nField
    private String content;
    //是否只显示自定义
    private Boolean tenantOnly;

}
