package com.hand.hcf.app.expense.input.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "从报账单取其行数据的DTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpInputForReportLineDTO {
    @ApiModelProperty(value = "id")
    private Long id;
    /**
     * 单据头id
     */
    @ApiModelProperty(value = "单据头id")
    private Long inputTaxHeaderId;
    /**
     * 报账单单号
     */
    @ApiModelProperty(value = "报账单单号")
    private String documentNumber;

    /**
     * 原报账单头id
     */
    @ApiModelProperty(value = "原报账单头id")
    private Long expReportHeaderId;
    /**
     * 原报账单行id
     */
    @ApiModelProperty(value = "原报账单行id")
    private Long expReportLineId;

    /**
     * 报账单申请人
     */
    @ApiModelProperty(value = "报账单申请人id")
    private Long applicantId;
    @ApiModelProperty(value = "全称")
    private String fullName;
    @ApiModelProperty(value = "租户id")
    private Long tenantId;
    @ApiModelProperty(value = "账套id")
    protected Long setOfBooksId;
    @ApiModelProperty(value = "公司ID")
    private Long companyId;
    @ApiModelProperty(value = "部门ID")
    private Long departmentId;

    /**
     * 费用类型id
     */
    @ApiModelProperty(value = "费用类型id")
    private Long expenseTypeId;

    /**
     * 费用类型名称
     */
    @ApiModelProperty(value = "费用类型名称")
    private String expenseTypeName;

    /**
     * 报账单发生日期
     */
    @ApiModelProperty(value = "报账单发生日期")
    private ZonedDateTime transferDate;

    @ApiModelProperty(value = "币种")
    private String  currencyCode;
    @ApiModelProperty(value = "率")
    private Long rate;
    /**
     * 报账单金额
     */
    @ApiModelProperty(value = "报账单金额")
    private BigDecimal reportAmount;

    /**
     * 可转出税额，可视同销售金额。
     */
    @ApiModelProperty(value = "可转出税额，可视同销售金额。")
    private BigDecimal ableAmount;

    /**
     * 转出税额or视同销售税额。实际等于行的基数金额
     */
    @ApiModelProperty(value = "转出税额or视同销售税额。实际等于行的基数金额")
    private BigDecimal baseAmount;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String description;

    /**
     * 选择标志 Y 全部 P部分 N没有选择
     */
    @ApiModelProperty(value = "选择标志 Y 全部 P部分 N没有选择")
    private String selectFlag;

    /**
     * 分摊行信息
     */
    @ApiModelProperty(value = "分摊行信息")
    List<ExpInputForReportDistDTO> expInputForReportDistDTOS;

    /**
     * 用途类类型
     */
    @ApiModelProperty(value = "用途类类型")
    private String useType;
}
