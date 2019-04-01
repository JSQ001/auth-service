package com.hand.hcf.app.expense.adjust.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hand.hcf.app.expense.common.domain.DimensionDomain;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/11
 */
@Data
@TableName("exp_adjust_line_temp")
public class ExpenseAdjustLineTemp extends DimensionDomain {
    
    private String amount;
    private Boolean errorFlag;
    private String errorMsg;
    @TableField("row_index")
    private String rowNumber;
    private String batchNumber;
    private String companyCode;
    private String unitCode;
    private Long companyId;
    private Long unitId;
    private String description;
    private Long employeeId;
    private Long expenseTypeId;
    private String expenseTypeCode;
    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    private Long tenantId;
    /**
     * 账套ID
     */
    @TableField(value = "set_of_books_id")
    private Long setOfBooksId;

    /**
     * 费用调整单头ID
     */
    @TableField(value = "exp_adjust_header_id")
    private Long expAdjustHeaderId;

    /**
     * 单据行类型
     */
    private String adjustLineCategory;
    /**
     * 维度1
     */
    @JsonIgnore
    private String dimension1Code;
    /**
     * 维度2
     */
    @JsonIgnore
    private String dimension2Code;
    /**
     * 维度3
     */
    @JsonIgnore
    private String dimension3Code;
    /**
     * 维度4
     */
    @JsonIgnore
    private String dimension4Code;
    /**
     * 维度5
     */
    @JsonIgnore
    private String dimension5Code;
    /**
     * 维度6
     */
    @JsonIgnore
    private String dimension6Code;
    /**
     * 维度7
     */
    @JsonIgnore
    private String dimension7Code;
    /**
     * 维度8
     */
    @JsonIgnore
    private String dimension8Code;
    /**
     * 维度9
     */
    @JsonIgnore
    private String dimension9Code;
    /**
     * 维度10
     */
    @JsonIgnore
    private String dimension10Code;
    /**
     * 维度11
     */
    @JsonIgnore
    private String dimension11Code;
    /**
     * 维度12
     */
    @JsonIgnore
    private String dimension12Code;
    /**
     * 维度13
     */
    @JsonIgnore
    private String dimension13Code;
    /**
     * 维度14
     */
    @JsonIgnore
    private String dimension14Code;
    /**
     * 维度15
     */
    @JsonIgnore
    private String dimension15Code;
    /**
     * 维度16
     */
    @JsonIgnore
    private String dimension16Code;
    /**
     * 维度17
     */
    @JsonIgnore
    private String dimension17Code;
    /**
     * 维度18
     */
    @JsonIgnore
    private String dimension18Code;
    /**
     * 维度19
     */
    @JsonIgnore
    private String dimension19Code;
    /**
     * 维度20
     */
    @JsonIgnore
    private String dimension20Code;

    @TableField(exist = false)
    private String expenseTypeName;
}
