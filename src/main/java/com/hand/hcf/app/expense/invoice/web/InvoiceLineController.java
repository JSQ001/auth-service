package com.hand.hcf.app.expense.invoice.web;

import com.hand.hcf.app.expense.invoice.domain.InvoiceLine;
import com.hand.hcf.app.expense.invoice.service.InvoiceLineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/20
 */
@Api(tags = "发票行")
@RestController
@RequestMapping("/api/invoice/line")
public class InvoiceLineController {
    private final InvoiceLineService invoiceLineService;

    public InvoiceLineController(InvoiceLineService invoiceLineService){
        this.invoiceLineService = invoiceLineService;
    }

    /**
     * 更新 发票行
     * @param invoiceLine
     * @return
     */
    @PutMapping
    @ApiOperation(value = "更新 发票行", notes = "更新 发票行 开发:xue.han")
    public ResponseEntity<InvoiceLine> updateInvoiceLine(@ApiParam(value = "发票行") @RequestBody InvoiceLine invoiceLine){
        return ResponseEntity.ok(invoiceLineService.updateInvoiceLine(invoiceLine));
    }

    /**
     * 根据id 删除 发票行
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id 删除 发票行", notes = "根据id 删除 发票行 开发:xue.han")
    public ResponseEntity deleteInvoiceLine(@PathVariable Long id){
        invoiceLineService.deleteInvoiceLine(id);
        return ResponseEntity.ok().build();
    }
}
