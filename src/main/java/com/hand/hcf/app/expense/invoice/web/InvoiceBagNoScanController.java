package com.hand.hcf.app.expense.invoice.web;

import com.hand.hcf.app.expense.invoice.domain.InvoiceBagNoScan;
import com.hand.hcf.app.expense.invoice.service.InvoiceBagNoScanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuo.zhang
 * @description 发票袋号码扫描(InvoiceBagNoScan)表控制层
 * @date 2019-04-29 16:08:06
 */
@Api(tags = "发票袋号码扫描记录")
@RestController
@RequestMapping("/api/invoice/bag/no/scan")
public class InvoiceBagNoScanController {

    @Autowired
    private InvoiceBagNoScanService invoiceBagNoScanService;

    @PostMapping("/signReports/confirm")
    @ApiOperation(value = "发票袋号码扫描记录保存", notes = "发票袋号码扫描记录保存 开发:张卓")
    public ResponseEntity saveInvoiceBagNoScan(@ApiParam(value = "发票袋号码扫描记录") @RequestBody InvoiceBagNoScan invoiceBagNoScan){
        return ResponseEntity.ok(invoiceBagNoScanService.saveInvoiceBagNoScan(invoiceBagNoScan));
    }


}