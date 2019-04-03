package com.hand.hcf.app.expense.input.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.input.domain.ExpInputTaxHeader;
import com.hand.hcf.app.expense.input.service.ExpInputTaxHeaderService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ResponseEntity getHeaders(@RequestParam(value = "applicantId", required = false) Long applicantId,
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
        HttpHeaders headers = new HttpHeaders();
        Page result = expInputTaxHeaderService.queryHeader(applicantId, transferType, useType, transferDateFrom, transferDateTo, status, amountFrom, amountTo, description,documentNumber,companyId,departmentId, page);
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/input/header/query");
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
    public ResponseEntity updateStatus(@RequestParam(value = "id") Long id,@RequestParam(value = "status")int status,@RequestBody Map<String, String> approvalRemark) {
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
}
