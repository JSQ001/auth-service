package com.hand.hcf.app.common.co;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.web.dto.DomainObjectDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/12/21
 */
@Data
public class BudgetReserveCO extends DomainObjectDTO{
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long companyId;         //预算公司ID
    @NotNull
    private String companyCode;         //预算公司代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long organizationId;         //预算组织ID
    @NotNull
    private String periodName;         //期间
    @NotNull
    private Integer periodYear;        //期间年度
    @NotNull
    private Integer periodQuarter;        //期间季度
    @NotNull
    private Integer periodNumber;        //期间数
    @JsonSerialize(using = ToStringSerializer.class)
    private Long releaseId;         //预算释放ID(报销单、申请单关联关系)
    @NotNull
    private String businessType;         //EXP_REQUISITION:费用申请单,EXP_REPORT:费用报销单
    @NotNull
    private String reserveFlag;         //R:申请单冻结,U:报销单占用
    @NotNull
    private String status;         //状态
    @NotNull
    private String manualFlag;         //手工标志
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentId;         //费用申请单/报销单头ID
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId;         //费用申请单/报销单分配行ID
    private String currency;         //币种
    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId;         //预算项目ID
    private String itemCode;         //预算项目代码
    private Double exchangeRate;         //汇率
    @NotNull
    private BigDecimal amount;         //金额
    @NotNull
    private BigDecimal functionalAmount;         //本位币金额
    @NotNull
    private Integer quantity;         //数量
    private String uom;         //单位
    @JsonSerialize(using = ToStringSerializer.class)
    private Long unitId;         //部门ID
    private String unitCode;         //部门代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;         //员工ID
    private String employeeCode;         //员工代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension1Id;         //维度1
    private String dimension1Code;         //维度1代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension2Id;         //维度2
    private String dimension2Code;         //维度2代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension3Id;         //维度3
    private String dimension3Code;         //维度3代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension4Id;         //维度4
    private String dimension4Code;         //维度4代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension5Id;         //维度5
    private String dimension5Code;         //维度5代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension6Id;         //维度6
    private String dimension6Code;         //维度6代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension7Id;         //维度7
    private String dimension7Code;         //维度7代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension8Id;         //维度8
    private String dimension8Code;         //维度8代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension9Id;         //维度9
    private String dimension9Code;         //维度9代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension10Id;         //维度10
    private String dimension10Code;         //维度10代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension11Id;         //维度11
    private String dimension11Code;         //维度11代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension12Id;         //维度12
    private String dimension12Code;         //维度12代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension13Id;         //维度13
    private String dimension13Code;         //维度13代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension14Id;         //维度14
    private String dimension14Code;         //维度14代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension15Id;         //维度15
    private String dimension15Code;         //维度15代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension16Id;         //维度16
    private String dimension16Code;         //维度16代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension17Id;         //维度17
    private String dimension17Code;         //维度17代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension18Id;         //维度18
    private String dimension18Code;         //维度18代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension19Id;         //维度19
    private String dimension19Code;         //维度19代码
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dimension20Id;         //维度20
    private String dimension20Code;         //维度20代码

    private Integer versionNumber;         //版本号
    @Valid
    private BudgetReportRequisitionReleaseCO releaseMsg;       //预算占用/释放关联关系
    @NotNull
    private String documentItemSourceType;             //预算项目来源类别
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long documentItemSourceId;              //预算项目来源id

}
