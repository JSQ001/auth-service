package com.hand.hcf.app.expense.invoice.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.invoice.dto.InvoiceCertificationDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceCertificationService;
import com.hand.hcf.app.expense.report.service.ExpenseReportHeaderService;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description 发票认证
 * @date 2019/4/19 10:30
 * @version: 1.0.0
 */
@Api(tags = "发票认证")
@RestController
@RequestMapping("/api/invoice/certification")
public class InvoiceCertificationController {

    @Autowired
    private InvoiceCertificationService invoiceCertificationService;


    @GetMapping
    @ApiOperation(value = "分页查询所有未提交认证", notes = "分页查询所有未提交认证信息 开发:郑少锋")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "invoiceTypeId", value = "发票类型ID",
                    required = false, dataType = "Long"),
            @ApiImplicitParam(paramType="query", name = "invoiceNo", value = "发票号码",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "invoiceCode", value = "发票代码",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "invoiceDateFrom", value = "开票日期从",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "invoiceDateTo", value = "开票日期至",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "invoiceAmountFrom", value = "合计金额从",
                    required = false, dataType = "BigDecimal"),
            @ApiImplicitParam(paramType="query", name = "invoiceAmountTo", value = "合计金额至",
                    required = false, dataType = "BigDecimal"),
            @ApiImplicitParam(paramType="query", name = "createdMethod", value = "录入方式",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "certificationStatus", value = "认证状态",
                    required = false, dataType = "Long"),
            @ApiImplicitParam(paramType="query", name = "isSubmit", value = "查看未提交与提交的标志",
                    required = false, dataType = "Boolean"),
            @ApiImplicitParam(paramType="query", name = "page", value = "第几页", required = true, dataType = "int"),
            @ApiImplicitParam(paramType="query", name = "size", value = "页数", required = true, dataType = "int")
    })
    public ResponseEntity<List<InvoiceCertificationDTO>> pageInvoiceCertifiedByCond(
            @RequestParam(value = "invoiceTypeId",required = false) Long invoiceTypeId,
            @RequestParam(value = "invoiceNo",required = false) String invoiceNo,
            @RequestParam(value = "invoiceCode",required = false) String invoiceCode,
            @RequestParam(value = "invoiceDateFrom",required = false) String invoiceDateFrom,
            @RequestParam(value = "invoiceDateTo",required = false) String invoiceDateTo,
            @RequestParam(value = "invoiceAmountFrom",required = false) BigDecimal invoiceAmountFrom,
            @RequestParam(value = "invoiceAmountTo",required = false) BigDecimal invoiceAmountTo,
            @RequestParam(value = "createdMethod",required = false) String createdMethod,
            @RequestParam(value = "certificationStatus",required = false) Long certificationStatus,
            @RequestParam(value = "isSubmit",required = false,defaultValue = "true") Boolean isSubmit,
            @ApiIgnore Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<InvoiceCertificationDTO> invoiceCertificationDTOList = invoiceCertificationService.pageInvoiceCertifiedByCond(
                invoiceTypeId,
                invoiceNo,
                invoiceCode,
                invoiceDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(invoiceDateFrom),
                invoiceDateTo  == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(invoiceDateTo),
                invoiceAmountFrom,
                invoiceAmountTo,
                createdMethod,
                certificationStatus,
                isSubmit,
                page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(invoiceCertificationDTOList, totalHeader, HttpStatus.OK);
    }


    @ApiOperation(value = "导出所有未提交认证", notes = "导出所有未提交认证信息 开发:郑少锋")
    @PostMapping(value = "/export",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "invoiceTypeId", value = "发票类型ID",
                    required = false, dataType = "Long"),
            @ApiImplicitParam(paramType="query", name = "invoiceNo", value = "发票号码",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "invoiceCode", value = "发票代码",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "invoiceDateFrom", value = "开票日期从",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "invoiceDateTo", value = "开票日期至",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "invoiceAmountFrom", value = "合计金额从",
                    required = false, dataType = "BigDecimal"),
            @ApiImplicitParam(paramType="query", name = "invoiceAmountTo", value = "合计金额至",
                    required = false, dataType = "BigDecimal"),
            @ApiImplicitParam(paramType="query", name = "createdMethod", value = "录入方式",
                    required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "certificationStatus", value = "认证状态",
                    required = false, dataType = "Long"),
            @ApiImplicitParam(paramType="query", name = "isSubmit", value = "查看未提交与提交的标志",
                    required = false, dataType = "Boolean"),
    })
    public void exportInvoiceUncertified(
            @ApiIgnore @RequestBody ExportConfig exportConfig,
            @ApiIgnore HttpServletRequest request,
            @ApiIgnore HttpServletResponse response,
            @RequestParam(value = "invoiceTypeId",required = false) Long invoiceTypeId,
            @RequestParam(value = "invoiceNo",required = false) String invoiceNo,
            @RequestParam(value = "invoiceCode",required = false) String invoiceCode,
            @RequestParam(value = "invoiceDateFrom",required = false) String invoiceDateFrom,
            @RequestParam(value = "invoiceDateTo",required = false) String invoiceDateTo,
            @RequestParam(value = "invoiceAmountFrom",required = false) BigDecimal invoiceAmountFrom,
            @RequestParam(value = "invoiceAmountTo",required = false) BigDecimal invoiceAmountTo,
            @RequestParam(value = "createdMethod",required = false) String createdMethod,
            @RequestParam(value = "certificationStatus",required = false) Long certificationStatus,
            @RequestParam(value = "isSubmit",required = false) Boolean isSubmit) throws IOException {
        invoiceCertificationService.exportInvoiceCertified(request, response, exportConfig, invoiceTypeId, invoiceNo,
                invoiceCode,
                invoiceDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(invoiceDateFrom),
                invoiceDateTo  == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(invoiceDateTo),
                invoiceAmountFrom,
                invoiceAmountTo,
                createdMethod,
                certificationStatus,
                isSubmit);
    }



    @ApiOperation(value = "更新发票认证状态", notes = "更新发票认证状态 开发:郑少锋")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "headerId", value = "发票头Id",
                    required = false, dataType = "List<Long>"),
            @ApiImplicitParam(paramType="query", name = "status", value = "认证状态",
                    required = false, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "approvalText", value = "认证失败原因",
                    required = false, dataType = "String")
    })
    @PutMapping("/update/certified/status")
    public void updateInvoiceCertifiedStatus(
            @ApiIgnore @RequestBody List<Long> headerId,
            @RequestParam("status") Integer status,
            @RequestParam(value = "approvalText",required = false) String approvalText) {
        invoiceCertificationService.updateInvoiceCertifiedStatus(headerId,status, approvalText);
    }

    @ApiOperation(value = "提交发票认证", notes = "提交发票认证 开发:郑少锋")
    @PostMapping("/submit")
    public void submitInvoiceCertified(
            @ApiIgnore @RequestBody List<Long> headerIds,
            @ApiParam("申请认证税款所属期") @RequestParam("taxDate") String taxDate){
        invoiceCertificationService.submitInvoiceCertified(headerIds,taxDate);
    }
}
