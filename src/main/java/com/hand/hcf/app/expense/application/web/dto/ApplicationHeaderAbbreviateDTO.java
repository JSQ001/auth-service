package com.hand.hcf.app.expense.application.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.annotation.ExcelDomainField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/25 00:42
 * @remark 费用申请简易信息
 */
@Data
public class ApplicationHeaderAbbreviateDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 单据类型ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long typeId;
    /**
     * 单据类型Name
     */
    private String typeName;
    /**
     * 单据编号
     */
    private String documentNumber;
    /**
     * 提交日期
     */
    @ExcelDomainField(dataFormat = "yyyy-mm-dd")
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
     * 备注
     */
    private String remarks;
    /**
     * 申请人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;
    /**
     * 申请人代码
     */
    private String employeeCode;
    /**
     * 申请人名称
     */
    private String employeeName;
    /**
     * 分摊行信息
     */
    private List<ApplicationLineAbbreviateDTO> lines;


    // 以下属性均作为查询条件
    /**
     * 报账单头公司ID
     */
    @JsonIgnore
    Long headerCompanyId;

    /**
     * 报账单头部门ID
     */
    @JsonIgnore
    Long headerDepartmentId;

    /**
     * 申请单费用类型
     */
    @JsonIgnore
    Long expenseTypeId;

    /**
     * 关联单据类型
     */
    @JsonIgnore
    String relatedDocumentCategory;

    /**
     * 报账单头ID
     */
    @JsonIgnore
    Long expReportHeaderId;

    /**
     * 来源单据类型
     */
    @JsonIgnore
    String sourceDocumentCategory;
    /**
     * 单据状态
     */
    @JsonIgnore
    Integer status;
}
