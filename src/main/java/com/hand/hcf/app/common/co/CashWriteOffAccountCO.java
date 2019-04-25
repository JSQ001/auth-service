package com.hand.hcf.app.common.co;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/4/12 17:41
 * @remark 核销数据生成凭证依据数据
 */
@Data
public class CashWriteOffAccountCO implements Serializable {
    /**
     * 单据类型
     */
    @NotNull
    private String documentType;
    /**
     * 单据头ID
     */
    @NotNull
    private Long documentHeaderId;
    /**
     * 单据行ID(若不传值则与单据相关核销记录全部生成凭证)
     */
    private List<Long> documentLineIds;
    /**
     * 租户ID
     */
    @NotNull
    private Long tenantId;
    /**
     * 操作人ID
     */
    @NotNull
    private Long operatorId;
    /**
     * 账务日期
     */
    @NotNull
    private ZonedDateTime accountDate;
    /**
     * 账务期间
     */
    private String accountPeriod;
}
