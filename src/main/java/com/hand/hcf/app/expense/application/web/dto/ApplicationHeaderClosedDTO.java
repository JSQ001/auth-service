package com.hand.hcf.app.expense.application.web.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.core.annotation.ExcelDomainField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * <p>
 *  申请单导出对象
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2019/3/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationHeaderClosedDTO {

    /**
     * 单据编号
     */
    private String documentNumber;
    private String companyName;
    private String departmentName;
    private String employeeName;
    private String typeName;

    @ExcelDomainField(dataFormat = "yyyy-mm-dd")
    private ZonedDateTime requisitionDate;

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
     * 申请单关闭标志 true 关闭，false 不关闭 默认false
     */
    private String closedFlag;
    /**
     * 可关闭金额
     */
    @ExcelDomainField(align = "right",dataFormat = "#,##0.00")
    private BigDecimal canCloseAmount;

}
