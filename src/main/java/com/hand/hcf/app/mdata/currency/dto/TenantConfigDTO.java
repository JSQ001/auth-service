package com.hand.hcf.app.mdata.currency.dto;

import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 汇率容差DTO
 *
 * @author shuai.wang02@hand-china.com
 * @version 1.0
 * @date 2019/4/29 18:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantConfigDTO extends DomainLogicEnable {

    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 账套ID
     */
    private Long setOfBooksId;
    /**
     * 告警汇率容差
     */
    private Double warnExchangeRateTol;
    /**
     * 禁止汇率容差
     */
    private Double prohibitExchangeRateTol;
}
