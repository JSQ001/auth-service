package com.hand.hcf.app.ant.invoice.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.invoice.dto.InvoiceHeader;
import com.hand.hcf.app.ant.invoice.service.InvoiceHeaderService;
import com.hand.hcf.app.ant.withholdingReimburse.dto.WithholdingReimburse;
import com.hand.hcf.app.ant.withholdingReimburse.service.WithholdingReimburseService;
import com.hand.hcf.app.core.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "蚂蚁发票")
@RestController
@RequestMapping("/api/expense/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceHeaderService invoiceHeaderService;

    @GetMapping("/header/query/page")
    @ApiOperation(value = "发票头分页查询", notes = "发票头分页查询 开发:jsq")
    ResponseEntity<List<InvoiceHeader>> queryHeaderPages(
            @RequestParam(required = false ) Long id,
            Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<InvoiceHeader> list = invoiceHeaderService.queryHeaderPages(id,page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/expense/category/query/page");
        return new ResponseEntity<List<InvoiceHeader>>(list, httpHeaders, HttpStatus.OK);
    }



    @PostMapping("/header/saveOrUpdate")
    @ApiOperation(value = "新建修改发票头", notes = "新建修改发票头 开发:jsq")
    ResponseEntity<InvoiceHeader> newUpdateInvoice(@ApiParam(value = "新建修改发票头") @RequestBody InvoiceHeader invoiceHeader){


        return ResponseEntity.ok(invoiceHeaderService.myInsertOrUpdate(invoiceHeader));
    }




    @DeleteMapping("/header/delete/{id}")
    @ApiOperation(value = "删除发票", notes = "删除发票 开发:jsq")
    ResponseEntity<Boolean> deleteWithholdingReimburse(@PathVariable Long id){
        return ResponseEntity.ok(invoiceHeaderService.deleteInvoiceById(id));
    }
}
