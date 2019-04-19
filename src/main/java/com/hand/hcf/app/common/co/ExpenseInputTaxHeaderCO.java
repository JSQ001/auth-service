package com.hand.hcf.app.common.co;


import com.hand.hcf.app.common.annotation.InterfaceFieldAttribute;
import com.hand.hcf.app.common.enums.SceneElementFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @Auther: zhu.zhao
 * @Date: 2019/04/14 09:08
 * 进项税单头
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseInputTaxHeaderCO implements Serializable {

    /**
     * 主键id
     */
    @InterfaceFieldAttribute(sequence = 0, display = false, elementFiled = SceneElementFieldType.TRANSACTION_HEADER_ID)
    @NotNull
    private Long id;

    /**
     * 租户id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 10, display = false, elementFiled = SceneElementFieldType.TENANT_ID)
    private Long tenantId;

    /**
     * 账套id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 20, display = false, elementFiled = SceneElementFieldType.SET_OF_BOOKS_ID)
    private Long setOfBooksId;

    /**
     * 币种
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 30, elementFiled = SceneElementFieldType.CURRENCY_CODE)
    private String currencyCode;

    /**
     * 汇率
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 40)
    private Double rate;

    /**
     * 单据编号
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 50, elementFiled = SceneElementFieldType.DOCUMENT_NUMBER)
    private String documentNumber;

    /**
     * 公司id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 60)
    private Long companyId;

    /**
     * 部门id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 70)
    private Long departmentId;

    /**
     * 申请人id
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 80)
    private Long applicationId;

    /**
     * 业务日期
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 90)
    private ZonedDateTime transferDate;

    /**
     * 业务大类
     */
    @InterfaceFieldAttribute(sequence = 100)
    private String transferType;

    /**
     * 计算比例
     */
    @InterfaceFieldAttribute(sequence = 110)
    private Long transferProportion;

    /**
     * 用途类型
     */
    @InterfaceFieldAttribute(sequence = 120)
    private String useType;

    /**
     * 基数金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 130)
    private BigDecimal baseAmount;

    /**
     * 基数本币金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 140)
    private BigDecimal baseFunctionAmount;

    /**
     * 金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 150)
    private BigDecimal amount;

    /**
     * 本币金额
     */
    @NotNull
    @InterfaceFieldAttribute(sequence = 160)
    private BigDecimal functionAmount;

    /**
     * 备注
     */
    @InterfaceFieldAttribute(sequence = 170)
    private String description;

    /**
     * 状态
     */
    @InterfaceFieldAttribute(sequence = 180)
    private String status;

    /**
     * 账务日期 单据审核的界面给的参数，不需要审核的则默认提供系统日期
     */
    @InterfaceFieldAttribute(sequence = 190)
    private ZonedDateTime accountDate;

    /**
     * 账务期间  单据审核的界面给的参数，不需要审核的则默认提供系统日期对应期间
     */
    @InterfaceFieldAttribute(sequence = 200)
    private String accountPeriod;

}
