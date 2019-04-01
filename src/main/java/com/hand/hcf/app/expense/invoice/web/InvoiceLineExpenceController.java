package com.hand.hcf.app.expense.invoice.web;

import com.hand.hcf.app.expense.invoice.service.InvoiceLineExpenceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/22
 */
@RestController
@RequestMapping("/api/invoice/line/expence")
public class InvoiceLineExpenceController {
    private final InvoiceLineExpenceService invoiceLineExpenceService;

    public InvoiceLineExpenceController(InvoiceLineExpenceService invoiceLineExpenceService){
        this.invoiceLineExpenceService = invoiceLineExpenceService;
    }


}
