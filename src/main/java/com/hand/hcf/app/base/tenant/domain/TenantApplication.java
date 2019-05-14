package com.hand.hcf.app.base.tenant.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  租户分配应用表
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/5/7
 */
@Data
@TableName("sys_tenant_application")
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantApplication extends Domain {
    private Long tenantId;
    private Long applicationId;
    /**
     * 应用代码
     */
    @TableField(exist = false)
    private String applicationCode;
    /**
     * 应用名称
     */
    @TableField(exist = false)
    private String applicationName;
}
