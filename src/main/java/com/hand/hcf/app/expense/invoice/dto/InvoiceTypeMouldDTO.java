package com.hand.hcf.app.expense.invoice.dto;

import com.hand.hcf.app.expense.invoice.domain.InvoiceTypeMouldHeadColumn;
import com.hand.hcf.app.expense.invoice.domain.InvoiceTypeMouldLineColumn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/17 9:46
 * @version: 1.0.0
 */
@ApiModel(description = "发票类型模板 domain 类")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceTypeMouldDTO {
    @NotNull
    @ApiModelProperty(value = "发票类型模板头")
    private InvoiceTypeMouldHeadColumn invoiceTypeMouldHeadColumn;
    @ApiModelProperty(value = "发票类型模板行")
    private InvoiceTypeMouldLineColumn invoiceTypeMouldLineColumn;

}