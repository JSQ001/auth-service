package com.hand.hcf.app.expense.invoice.web;

import com.hand.hcf.app.expense.invoice.dto.InvoiceTypeMouldDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceTypeMouldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description 发票模板定义
 * @date 2019/1/17 9:37
 * @version: 1.0.0
 */
@Api(tags = "发票模板定义")
@RestController
@RequestMapping("/api/invoice/type/mould")
public class InvoiceTypeMouldController {
    @Autowired
    private InvoiceTypeMouldService invoiceTypeMouldService;


    @PostMapping(value = "/insertOrUpdate",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "据发票类型是否抵扣初始化不同的发票模板", notes = "据发票类型是否抵扣初始化不同的发票模板 开发:shaofeng.zheng")
    public ResponseEntity insertOrUpdateInvoiceTypeMould(@ApiParam(value = "发票类型模板") @RequestBody InvoiceTypeMouldDTO invoiceTypeMouldDTO){
        return ResponseEntity.ok(invoiceTypeMouldService.insertOrUpdateInvoiceTypeMould(invoiceTypeMouldDTO));
    }

    @GetMapping(value = "/query/{invoiceTypeId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "根据发票类型Id获取发票模板", notes = "根据发票类型Id获取发票模板 开发:shaofeng.zheng")
    public ResponseEntity<InvoiceTypeMouldDTO> getInvoiceTypeMouldByTypeId(@PathVariable("invoiceTypeId") Long invoiceTypeId ){
        return ResponseEntity.ok(invoiceTypeMouldService.getInvoiceTypeMouldByTypeId(invoiceTypeId));
    }
}
