package com.hand.hcf.app.expense.invoice.dto;

import com.hand.hcf.app.expense.invoice.domain.InvoiceTypeMouldHeadColumn;
import com.hand.hcf.app.expense.invoice.domain.InvoiceTypeMouldLineColumn;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceTypeMouldDTO {
    @NotNull
    private InvoiceTypeMouldHeadColumn invoiceTypeMouldHeadColumn;

    private InvoiceTypeMouldLineColumn invoiceTypeMouldLineColumn;

}
