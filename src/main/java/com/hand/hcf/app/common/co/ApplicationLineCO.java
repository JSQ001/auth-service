package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class ApplicationLineCO {

    protected Long id;

    /**
     * 申请类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expenseTypeId;
    /**
     * 申请单头ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long headerId;
    /**
     * 申请日期
     */
    private ZonedDateTime requisitionDate;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 原币金额
     */
    private BigDecimal amount;
    /**
     * 本位币金额
     */
    private BigDecimal functionalAmount;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 单位
     */
    private String priceUnit;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 公司ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    /**
     * 账套ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long departmentId;
    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    private ZonedDateTime exchangeDate;
    /**
     * 关联合同头ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractHeaderId;

    /**关闭状态id
     * NOT_CLOSED(1001, "未关闭"),PARTIAL_CLOSED(1002, "部分关闭"),CLOSED(1003, "已关闭");
     */
    private Integer closedTypeId;
    /**
     * 关闭状态String
     * NOT_CLOSED(1001, "未关闭"),PARTIAL_CLOSED(1002, "部分关闭"),CLOSED(1003, "已关闭");
     */
    private String closedTypeName;
    /**
     * 关联责任中心ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long responsibilityCenterId;

    /**
     * 已关闭金额(原币)
     */
    private BigDecimal closedAmount;
    /**
     * 已关闭金额(本位币)
     */
    private BigDecimal closedFunctionalAmount;
}
