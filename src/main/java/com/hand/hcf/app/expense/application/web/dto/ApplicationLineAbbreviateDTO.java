package com.hand.hcf.app.expense.application.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/25 00:53
 * @remark 申请单分摊简易信息
 */
@Data
public class ApplicationLineAbbreviateDTO {
    /**
     * 费用申请分摊行ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 申请类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationTypeId;
    /**
     * 申请类型名称
     */
    private String applicationTypeName;
    /**
     * 费用类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expenseTypeId;
    /**
     * 费用类型名称
     */
    private String expenseTypeName;
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
    private BigDecimal amount;
    /**
     * 本位币金额
     */
    private BigDecimal functionalAmount;
    /**
     *可报账金额
     */
    private BigDecimal usableAmount;

    /**
     *已报账金额
     */
    private BigDecimal usedAmount;
    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 账套
     */
    private Long setOfBooksId;
    /**
     * 费用发生日期
     */
    private ZonedDateTime expenseDate;
    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long departmentId;
    /**
     * 部门代码
     */
    private String departmentCode;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 公司ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    /**
     * 公司代码
     */
    private String companyCode;
    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 责任中心ID
     */
    private Long responsibilityCenterId;
    /**
     * 责任中心代码
     */
    private String responsibilityCenterCode;
    /**
     * 责任中心名称
     */
    private String responsibilityCenterName;

    /**
     * 维度1
     */
    private Long dimension1Id;
    /**
     * 维度2
     */
    private Long dimension2Id;
    /**
     * 维度3
     */
    private Long dimension3Id;
    /**
     * 维度4
     */
    private Long dimension4Id;
    /**
     * 维度5
     */
    private Long dimension5Id;
    /**
     * 维度6
     */
    private Long dimension6Id;
    /**
     * 维度7
     */
    private Long dimension7Id;
    /**
     * 维度8
     */
    private Long dimension8Id;
    /**
     * 维度9
     */
    private Long dimension9Id;
    /**
     * 维度10
     */
    private Long dimension10Id;
    /**
     * 维度11
     */
    private Long dimension11Id;
    /**
     * 维度12
     */
    private Long dimension12Id;
    /**
     * 维度13
     */
    private Long dimension13Id;
    /**
     * 维度14
     */
    private Long dimension14Id;
    /**
     * 维度15
     */
    private Long dimension15Id;
    /**
     * 维度16
     */
    private Long dimension16Id;
    /**
     * 维度17
     */
    private Long dimension17Id;
    /**
     * 维度18
     */
    private Long dimension18Id;
    /**
     * 维度19
     */
    private Long dimension19Id;
    /**
     * 维度20
     */
    private Long dimension20Id;

    private String dimension1Name;

    private String dimension2Name;

    private String dimension3Name;

    private String dimension4Name;

    private String dimension5Name;

    private String dimension6Name;

    private String dimension7Name;

    private String dimension8Name;

    private String dimension9Name;

    private String dimension10Name;

    private String dimension11Name;

    private String dimension12Name;

    private String dimension13Name;

    private String dimension14Name;

    private String dimension15Name;

    private String dimension16Name;

    private String dimension17Name;

    private String dimension18Name;

    private String dimension19Name;

    private String dimension20Name;
}
