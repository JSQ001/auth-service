package com.hand.hcf.app.expense.invoice.web;

import com.hand.hcf.app.expense.invoice.service.InvoiceLineDistService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/23
 */
@RestController
@RequestMapping("/api/invoice/line/dist")
public class InvoiceLineDistController {
    private final InvoiceLineDistService invoiceLineDistService;

    public InvoiceLineDistController(InvoiceLineDistService invoiceLineDistService){
        this.invoiceLineDistService = invoiceLineDistService;
    }
}
