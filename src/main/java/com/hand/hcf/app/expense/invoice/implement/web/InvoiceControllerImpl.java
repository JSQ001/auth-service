package com.hand.hcf.app.expense.invoice.implement.web;

import com.hand.hcf.app.expense.invoice.service.InvoiceCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/4/23 23:50
 * @version: 1.0.0
 */
@RestController
public class InvoiceControllerImpl {

    @Autowired
    private InvoiceCertificationService invoiceCertificationService;

    public void updateInvoiceCertifiedStatus(
            @RequestBody List<Long> headerId,
            @RequestParam("status") Integer status,
            @RequestParam(value = "certificationReason",required = false) String certificationReason) {
        invoiceCertificationService.updateInvoiceCertifiedStatus(headerId,status,certificationReason);
    }
}
