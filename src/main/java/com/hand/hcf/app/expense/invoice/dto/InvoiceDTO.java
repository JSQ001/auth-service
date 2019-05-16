package com.hand.hcf.app.expense.invoice.dto;

import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLine;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "发票信息")
public class InvoiceDTO {
    @ApiModelProperty(value = "发票头信息",dataType = "InvoiceHead")
    private InvoiceHead invoiceHead;
    @ApiModelProperty(value = "发票行信息",dataType = "List<InvoiceLine>")
    private List<InvoiceLine> invoiceLineList;
}
