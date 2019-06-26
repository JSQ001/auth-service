package com.hand.hcf.app.expense.invoice.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.invoice.domain.InvoiceType;
import com.hand.hcf.app.expense.invoice.dto.InvoiceTypeDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/16 16:35
 * @version: 1.0.0
 */
@Api(tags = "发票类型")
@RestController
@RequestMapping("/api/invoice/type")
public class InvoiceTypeController {
    @Autowired
    private InvoiceTypeService invoiceTypeService;


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "新增发票类型定义", notes = "新增发票类型定义 开发:shaofeng.zheng")
    public ResponseEntity<InvoiceType> insertInvoiceType(@ApiParam(value = "发票类型") @RequestBody InvoiceType invoiceType){
        return ResponseEntity.ok(invoiceTypeService.insertInvoiceType(invoiceType));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "编辑发票类型定义", notes = "编辑发票类型定义 开发:shaofeng.zheng")
    public ResponseEntity<InvoiceType> updateInvoiceType(@ApiParam(value = "发票类型") @RequestBody InvoiceType invoiceType){
        return ResponseEntity.ok(invoiceTypeService.updateInvoiceType(invoiceType));
    }


    @GetMapping(value = "query",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "发票类型定义-查询", notes = "发票类型定义-查询 开发:shaofeng.zheng")
    public ResponseEntity<List<InvoiceType>> pageInvoiceTypeByCond(@ApiParam(value = "发票类型代码") @RequestParam(value = "invoiceTypeCode",required = false) String invoiceTypeCode,
                                                                   @ApiParam(value = "发票类型名称") @RequestParam(value = "invoiceTypeName",required = false) String invoiceTypeName,
                                                                   @ApiParam(value = "抵扣标志") @RequestParam(value = "deductionFlag",required = false) String deductionFlag,
                                                                   @ApiParam(value = "启用/禁用") @RequestParam(value = "enabled",required = false) Boolean enabled,
                                                                   @ApiParam(value = "账套ID") @RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
                                                                   @ApiParam(value = "接口映射值") @RequestParam(value = "interfaceMapping",required = false) String interfaceMapping,
                                                                   @ApiParam(value = "当前页") @RequestParam(value = "page",defaultValue = "0") int page,
                                                                   @ApiParam(value = "每页多少条") @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<InvoiceType> invoiceTypeList =  invoiceTypeService.pageInvoiceTypeByCond(invoiceTypeCode, invoiceTypeName, deductionFlag, enabled,setOfBooksId,interfaceMapping,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(invoiceTypeList,httpHeaders, HttpStatus.OK);
    }


    @GetMapping("/sob/tenant/query")
    @ApiOperation(value = "发票类型定义-查询", notes = "发票类型定义-查询 开发:shaofeng.zheng")
    public ResponseEntity<List<InvoiceTypeDTO>> listInvoiceTypeBySobAndTenant(){
        return ResponseEntity.ok(invoiceTypeService.listInvoiceTypeBySobAndTenant());
    }

    /**
     * 给 我的票夹页面 的提供的 发票类型查询接口
     * @param tenantId
     * @return
     */
    @GetMapping("/query/for/invoice")
    @ApiOperation(value = "给 我的票夹页面 的提供的 发票类型查询接口", notes = "给 我的票夹页面 的提供的 发票类型查询接口开发:shaofeng.zheng")
    public ResponseEntity<List<InvoiceType>> queryInvoiceTypeForInvoice(
            @ApiParam(value = "租户ID") @RequestParam("tenantId") Long tenantId,
            @ApiParam(value = "账套ID") @RequestParam("setOfBooksId") Long setOfBooksId){
        return ResponseEntity.ok(invoiceTypeService.queryInvoiceTypeForInvoice(tenantId,setOfBooksId));
    }

}
