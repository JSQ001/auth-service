package com.hand.hcf.app.expense.input.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.input.dto.ExpInputForReportLineDTO;
import com.hand.hcf.app.expense.input.service.ExpInputTaxLineService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/2/28 15:40
 */
@RestController
@RequestMapping("/api/input/line")
public class ExpInputTaxLineController {

    @Autowired
    private ExpInputTaxLineService expInputTaxLineService;

    @GetMapping("/queryByHeaderId")
    public ResponseEntity getHeaderByUserId(@RequestParam(value = "headerId") Long headerId, Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page result = expInputTaxLineService.queryByHeaderId(headerId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/input/line/queryByHeaderId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    @GetMapping("/getReportData")
    public ResponseEntity getReportData(@RequestParam(value = "documentNumber", required = false) String documentNumber,
                                        @RequestParam(value = "applicantId", required = false) Long applicantId,
                                        @RequestParam(value = "expenseTypeId", required = false) Long expenseTypeId,
                                        @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                        @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                        @RequestParam(value = "companyId", required = false) Long companyId,
                                        @RequestParam(value = "departmentId", required = false) Long departmentId,
                                        @RequestParam(value = "transferDateFrom", required = false) String transferDateFrom,
                                        @RequestParam(value = "transferDateTo", required = false) String transferDateTo,
                                        @RequestParam(value = "description", required = false) String description,
                                        @RequestParam(value = "headerId") Long headerId,
                                        Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page result = expInputTaxLineService.getReportData(documentNumber, applicantId, expenseTypeId, amountFrom, amountTo,companyId,departmentId,transferDateFrom,transferDateTo,description,headerId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/input/line/getReportData");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @PostMapping("/insertOrUpdate")
    public ResponseEntity insertOrUpdateHeader(@RequestBody List<ExpInputForReportLineDTO> expInputForReportLineDTOs) {
        return ResponseEntity.ok(expInputTaxLineService.insertOrUpdateLine(expInputForReportLineDTOs));
    }
    @GetMapping("/delete")
    public ResponseEntity deleteById(@RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(expInputTaxLineService.delete(id));
    }
}
