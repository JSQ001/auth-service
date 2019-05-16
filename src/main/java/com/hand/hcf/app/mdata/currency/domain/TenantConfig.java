package com.hand.hcf.app.mdata.currency.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 汇率容差实体类
 *
 * @author shuai.wang02@hand-china.com
 * @version 1.0
 * @date 2019/4/29 18:15
 */
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant_config")
@Data
@ApiModel("汇率容差")
public class TenantConfig extends DomainLogicEnable {

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;
    /**
     * 账套ID
     */
    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套ID", required = true)
    private Long setOfBooksId;
    /**
     * 告警汇率容差
     */
    @TableField("warn_exchange_rate_tol")
    @ApiModelProperty(value = "告警汇率容差", required = true)
    private Double warnExchangeRateTol;
    /**
     * 禁止汇率容差
     */
    @TableField("prohibit_exchange_rate_tol")
    @ApiModelProperty(value = "禁止汇率容差", required = true)
    private Double prohibitExchangeRateTol;
}
