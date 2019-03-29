package com.hand.hcf.app.expense.application.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.expense.application.enums.ClosedTypeEnum;
import com.hand.hcf.app.expense.common.utils.RespCode;
import com.hand.hcf.app.expense.common.utils.SqlConditionExpanse;
import com.hand.hcf.core.annotation.ExcelDomainField;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * <p>
 * 费用申请单头表
 * </p>
 *
 * @author bin.xie
 * @since 2018-11-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exp_application_header")
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationHeader extends Domain {
    /**
     * 单据编号
     */
    @TableField(value = "document_number", condition = SqlConditionExpanse.LIKE)
    private String documentNumber;
    /**
     * 单据类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = RespCode.EXPENSE_APPLICATION_TYPE_IS_NUTT)
    private Long typeId;
    /**
     * 提交日期
     */
    @ExcelDomainField(dataFormat = "yyyy-mm-dd")
    private ZonedDateTime requisitionDate;
    /**
     * 申请人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;
    /**
     * 币种
     */
    private String currencyCode;
    /**
     * 原币金额
     */
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal amount;
    /**
     * 本位币金额
     */
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal functionalAmount;
    /**
     * 备注
     */
    @TableField(value = "remarks",strategy = FieldStrategy.IGNORED)
    private String remarks;
    /**
     * 单据OID
     */
    private String documentOid;
    /**
     * 表单OID
     */
    private String formOid;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 单据类型
     */
    private Integer documentType;
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
    /**
     * 关联合同头ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "contract_header_id", strategy = FieldStrategy.IGNORED)
    private Long contractHeaderId;
    /**
     * 是否预算管控 来源于单据类型 创建后不可以修改 方便动态生成列
     */
    private Boolean budgetFlag;
    /**
     * 是否可以关联合同 来源于单据类型 创建后不可以修改 方便动态生成列
     */
    private Boolean associateContract;
    /**
     * 合同是否必输 来源于单据类型 创建后不可以修改 方便动态生成列
     */
    private Boolean requireInput;
    /**
     * 是否超预算
     */
    private Boolean budgetStatus;
    /**
     * 超预算描述
     */
    private String budgetErrorMessage;
    /**
     * 申请单关闭标志 true 关闭，false 不关闭 默认false
     */
    private ClosedTypeEnum closedFlag;

    /**
     * 关联附件的OID 用 , 分割
     */
    @TableField(value = "attachment_oid", strategy = FieldStrategy.IGNORED)
    private String attachmentOid;

    private String applicationOid;

    /**
     * 部门OID
     */
    private String departmentOid;

    /**
     * 已关闭金额(原币)
     */
    private BigDecimal closedAmount;
    /**
     * 已关闭金额(本位币)
     */
    private BigDecimal closedFunctionalAmount;
}
