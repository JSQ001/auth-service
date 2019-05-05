package com.hand.hcf.app.expense.invoice.web;

import com.hand.hcf.app.expense.invoice.dto.InvoiceLineExpenceWebQueryDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineExpenceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/22
 */
@Api(tags = "发票行报销记录")
@RestController
@RequestMapping("/api/invoice/line/expence")
public class InvoiceLineExpenceController {
    private final InvoiceLineExpenceService invoiceLineExpenceService;

    public InvoiceLineExpenceController(InvoiceLineExpenceService invoiceLineExpenceService) {
        this.invoiceLineExpenceService = invoiceLineExpenceService;
    }

    @GetMapping("/query/{reportHeaderId}")
    @ApiOperation(value = "根据报账单头查询报账单下所有发票行记录", notes = "根据报账单头查询报账单下所有发票行记录 开发:张卓")
    public ResponseEntity<List<InvoiceLineExpenceWebQueryDTO>> getInvoiceLineExpencesByReportHeaderId(@ApiParam(value = "报账单头ID")
                                                                                                      @PathVariable Long reportHeaderId) {
        return ResponseEntity.ok(invoiceLineExpenceService.getInvoiceLineExpenseByReportHeaderId(reportHeaderId, null));
    }


}
