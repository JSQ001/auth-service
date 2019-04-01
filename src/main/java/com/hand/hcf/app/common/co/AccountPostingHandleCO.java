package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/6/21 11:53
 * @remark 凭证过账处理DTO
 */
@Data
public class AccountPostingHandleCO {

    /**
     * 租户
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 来源事务类型
     */
    @NotNull
    private String sourceTransactionType;
    /**
     * 事务头ID
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long transactionHeaderId;
    /**
     * 事务行ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long transactionLineId;
    /**
     * 事务分配行ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long transactionDistId;
    /**
     * 操作人ID
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastUpdatedBy;
}
