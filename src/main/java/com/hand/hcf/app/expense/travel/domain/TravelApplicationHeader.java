package com.hand.hcf.app.expense.travel.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.common.domain.DimensionDomain;
import com.hand.hcf.app.expense.common.utils.SqlConditionExpanse;
import com.hand.hcf.app.core.annotation.ExcelDomainField;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 ** 差旅申请单头
 * @author .
 * @date 2019/3/11
 */

@Data
@TableName("exp_travel_app_header")
public class TravelApplicationHeader extends DimensionDomain {
    /**
     * 租户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 账套ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    /**
     * 单据编号
     */
    @TableField(value = "requisition_number", condition = SqlConditionExpanse.LIKE)
    private String requisitionNumber;
    /**
     * 公司ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long unitId;
    /**
     * 申请人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;
    /**
     * 开始日期
     */
    @ExcelDomainField(dataFormat = "yyyy-mm-dd")
    private ZonedDateTime startDate;
    /**
     * 结束日期
     */
    @ExcelDomainField(dataFormat = "yyyy-mm-dd")
    private ZonedDateTime endDate;
    /**
     * 订票模式(1代表统一订，2代表分开订)
     */
    @TableField("order_mode")
    private String orderMode;
    /**
     * 订票人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderer;
    /**
     * 事由
     */
    @TableField(value = "description",strategy = FieldStrategy.IGNORED)
    private String description;
    /**
     * 单据类型id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentTypeId;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 汇率
     */
    private BigDecimal exchangeRate;
    /**
     * 原币金额
     */
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal totalAmount;
    /**
     * 本位币金额
     */
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal functionalAmount;
    /**
     * 状态
     */
    private Integer status;

    /**
     * 申请日期
     */
    @ExcelDomainField(dataFormat = "yyyy-mm-dd")
    private ZonedDateTime requisitionDate;
    /**
     * 申请单关闭标志 true 关闭，false 不关闭 默认false
     */
    private ClosedTypeEnum closedFlag;

    /**
     * 关联附件的OID 用 , 分割
     */
    @TableField(value = "attachment_oid", strategy = FieldStrategy.IGNORED)
    private String attachmentOid;

    /**
     * 单据类型
     */
    private Integer documentType;

    /**
     * 单据OID
     */
    private String documentOid;
}
