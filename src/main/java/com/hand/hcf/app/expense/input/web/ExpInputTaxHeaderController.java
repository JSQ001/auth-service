package com.hand.hcf.app.expense.input.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxHeader;
import com.hand.hcf.app.expense.input.dto.ExpInputTaxHeaderDTO;
import com.hand.hcf.app.expense.input.service.ExpInputTaxHeaderService;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/2/28 15:40
 */
@RestController
@RequestMapping("/api/input/header")
public class ExpInputTaxHeaderController {

    @Autowired
    private ExpInputTaxHeaderService expInputTaxHeaderService;

    @GetMapping("/query")
    public ResponseEntity getHeaders(
            @RequestParam(value = "applicantId", required = false) Long applicantId,
            @RequestParam(value = "transferType", required = false) String transferType,
            @RequestParam(value = "useType", required = false) String useType,
            @RequestParam(value = "transferDateFrom", required = false) String transferDateFrom,
            @RequestParam(value = "transferDateTo", required = false) String transferDateTo,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
            @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "documentNumber", required = false)String documentNumber,
            @RequestParam(value = "companyId", required = false)Long companyId,
            @RequestParam(value = "departmentId", required = false)Long departmentId,
            Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page result = expInputTaxHeaderService.queryHeader(applicantId, transferType, useType, transferDateFrom,
                transferDateTo, status, amountFrom, amountTo, description,documentNumber,companyId,departmentId, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @GetMapping("/queryById")
    public ResponseEntity getHeaderById(@RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(expInputTaxHeaderService.queryById(id));
    }

    @PostMapping("/insertOrUpdate")
    public ResponseEntity insertOrUpdateHeader(@RequestBody ExpInputTaxHeader expInputTaxHeader) {
        return ResponseEntity.ok(expInputTaxHeaderService.insertOrUpdateHeader(expInputTaxHeader));
    }

    @PostMapping("/updateStatus")
    public ResponseEntity updateStatus(@RequestParam(value = "id") Long id,
                                       @RequestParam(value = "status")int status,
                                       @RequestBody Map<String, String> approvalRemark) {
        String remark = "";
        if(approvalRemark!=null){
            remark =  approvalRemark.get("approvalRemark");
        }
        return ResponseEntity.ok(expInputTaxHeaderService.updateStatus(id,status,remark));
    }

    @GetMapping("/delete")
    public ResponseEntity deleteById(@RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(expInputTaxHeaderService.delete(id));
    }

    /**
     *  进项税业务单财务查询
     * @param companyId
     * @param unitId
     * @param applyId
     * @param status
     * @param transferType
     * @param useType
     * @param applyDateFrom
     * @param applyDateTo
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param reverseFlag
     * @param auditorDateFrom
     * @param auditorDateTo
     * @param remark
     * @return
     */
    @GetMapping("/query/expinput/finance")
    public ResponseEntity queryExpInputFinance(@RequestParam(value = "companyId",required = false)Long companyId,
                                              @RequestParam(value = "unitId",required = false)Long unitId,
                                              @RequestParam(value = "applyId",required = false)Long applyId,
                                              @RequestParam(value = "status",required = false)Long status,
                                              @RequestParam(value = "transferType",required = false)String transferType,
                                              @RequestParam(value = "useType",required = false)String useType,
                                              @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                                              @RequestParam(value = "applyDateTo",required = false)String applyDateTo,
                                              @RequestParam(value = "currencyCode",required = false)String currencyCode,
                                              @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                                              @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                                              @RequestParam(value = "reverseFlag",required = false)String reverseFlag,
                                              @RequestParam(value = "auditorDateFrom",required = false)String auditorDateFrom,
                                              @RequestParam(value = "auditorDateTo",required = false)String auditorDateTo,
                                              @RequestParam(value = "remark",required = false)String remark,
                                               @RequestParam(value = "tenantId",required = false)Long tenantId,
                                               @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                               Pageable pageable){

        ZonedDateTime creatDateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        ZonedDateTime creatDateTo = DateUtil.stringToZonedDateTime(applyDateTo);
        ZonedDateTime auditDateFrom = DateUtil.stringToZonedDateTime(auditorDateFrom);
        ZonedDateTime auditDateTo = DateUtil.stringToZonedDateTime(auditorDateTo);
        if (creatDateTo != null){
            creatDateTo = creatDateTo.plusDays(1);
        }
        if (auditDateTo != null){
            auditDateTo = auditDateTo.plusDays(1);
        }
        Page page = PageUtil.getPage(pageable);
        List<ExpInputTaxHeaderDTO> result =expInputTaxHeaderService.queryExpInputFinance(page,companyId,unitId,applyId,status,
                    transferType,useType,currencyCode,amountFrom,amountTo,
                        reverseFlag,remark,creatDateFrom,creatDateTo,auditDateFrom,auditDateTo,tenantId,documentNumber);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return  new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    /**
     *  进项税财务查询导出
     * @param companyId
     * @param unitId
     * @param applyId
     * @param status
     * @param transferType
     * @param useType
     * @param applyDateFrom
     * @param applyDateTo
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param reverseFlag
     * @param auditorDateFrom
     * @param auditorDateTo
     * @param remark
     * @param tenantId
     * @param documentNumber
     * @param exportConfig
     * @param response
     * @param request
     * @throws IOException
     */
    @PostMapping("/query/expinput/finance/export")
    public void export(@RequestParam(value = "companyId",required = false)Long companyId,
                       @RequestParam(value = "unitId",required = false)Long unitId,
                       @RequestParam(value = "applyId",required = false)Long applyId,
                       @RequestParam(value = "status",required = false)Long status,
                       @RequestParam(value = "transferType",required = false)String transferType,
                       @RequestParam(value = "useType",required = false)String useType,
                       @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                       @RequestParam(value = "applyDateTo",required = false)String applyDateTo,
                       @RequestParam(value = "currencyCode",required = false)String currencyCode,
                       @RequestParam(value = "amountFrom",required = false)BigDecimal amountFrom,
                       @RequestParam(value = "amountTo",required = false)BigDecimal amountTo,
                       @RequestParam(value = "reverseFlag",required = false)String reverseFlag,
                       @RequestParam(value = "auditorDateFrom",required = false)String auditorDateFrom,
                       @RequestParam(value = "auditorDateTo",required = false)String auditorDateTo,
                       @RequestParam(value = "remark",required = false)String remark,
                       @RequestParam(value = "tenantId",required = false)Long tenantId,
                       @RequestParam(value = "documentNumber",required = false) String documentNumber,
                       @RequestBody ExportConfig exportConfig,
                       HttpServletResponse response,
                       HttpServletRequest request) throws IOException {
        ZonedDateTime creatDateFrom = DateUtil.stringToZonedDateTime(applyDateFrom);
        ZonedDateTime creatDateTo = DateUtil.stringToZonedDateTime(applyDateTo);
        ZonedDateTime auditDateFrom = DateUtil.stringToZonedDateTime(auditorDateFrom);
        ZonedDateTime auditDateTo = DateUtil.stringToZonedDateTime(auditorDateTo);
        if (creatDateTo != null){
            creatDateTo = creatDateTo.plusDays(1);
        }
        if (auditDateTo != null){
            auditDateTo = auditDateTo.plusDays(1);
        }
        expInputTaxHeaderService.exportFormExcel(companyId,unitId,applyId,status,transferType,useType,currencyCode,
                amountFrom,amountTo, reverseFlag,remark,creatDateFrom,creatDateTo,auditDateFrom, auditDateTo, tenantId,
                documentNumber,response, request, exportConfig);
    }


}
