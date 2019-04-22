package com.hand.hcf.app.expense.input.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description 从报账单取其行数据的DTO
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/3/4 11:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpInputForReportLineDTO {

    private Long id;
    /**
     * 单据头id
     */
    private Long inputTaxHeaderId;
    /**
     * 报账单单号
     */
    private String documentNumber;

    /**
     * 原报账单头id
     */
    private Long expReportHeaderId;
    /**
     * 原报账单行id
     */
    private Long expReportLineId;

    /**
     * 报账单申请人
     */
    private Long applicantId;

    private String fullName;

    private Long tenantId;

    protected Long setOfBooksId;

    private Long companyId;

    private Long departmentId;

    /**
     * 费用类型id
     */
    private Long expenseTypeId;

    /**
     * 费用类型名称
     */
    private String expenseTypeName;

    /**
     * 报账单发生日期
     */
    private ZonedDateTime transferDate;


    private String  currencyCode;

    private Long rate;
    /**
     * 报账单金额
     */
    private BigDecimal reportAmount;

    /**
     * 可转出税额，可视同销售金额。
     */
    private BigDecimal ableAmount;

    /**
     * 转出税额or视同销售税额。实际等于行的基数金额
     */
    private BigDecimal baseAmount;

    /**
     * 备注
     */
    private String description;

    /**
     * 选择标志 Y 全部 P部分 N没有选择
     */
    private String selectFlag;

    /**
     * 分摊行信息
     */
    List<ExpInputForReportDistDTO> expInputForReportDistDTOS;

    /**
     * 用途类类型
     */
    private String useType;
}
