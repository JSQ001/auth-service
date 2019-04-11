package com.hand.hcf.app.expense.application.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.common.domain.DimensionDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * <p>
 * 费用申请单分摊行表
 * </p>
 *
 * @author bin.xie
 * @since 2018-11-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exp_application_line_dist")
public class ApplicationLineDist extends DimensionDomain {

    /**
     * 申请类型ID
     */
    private Long expenseTypeId;

    private Long lineId;
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
    /**
     * 申请单行关闭标志（Y/N）默认N
     */
    private ClosedTypeEnum closedFlag;


    /**
     * 关联责任中心ID
     */
    @TableField("responsibility_center_id")
    private Long responsibilityCenterId;
}
