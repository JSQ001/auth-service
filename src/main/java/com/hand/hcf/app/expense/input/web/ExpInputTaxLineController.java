package com.hand.hcf.app.expense.input.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.input.dto.ExpInputForReportLineDTO;
import com.hand.hcf.app.expense.input.service.ExpInputTaxLineService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/2/28 15:40
 */
@Api(tags = "费用进项税单行控制器")
@RestController
@RequestMapping("/api/input/line")
public class ExpInputTaxLineController {

    @Autowired
    private ExpInputTaxLineService expInputTaxLineService;

    @GetMapping("/queryByHeaderId")
    @ApiOperation(value = "根据用户id获取头", notes = "根据用户id获取头 开发:ShilinMao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity getHeaderByUserId(@ApiParam(value = "头ID") @RequestParam(value = "headerId") Long headerId, @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page result = expInputTaxLineService.queryByHeaderId(headerId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/input/line/queryByHeaderId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    @GetMapping("/getReportData")
    @ApiOperation(value = "获取报告日期", notes = "获取报告日期 开发:ShilinMao")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity getReportData(@ApiParam(value = "文档编号") @RequestParam(value = "documentNumber", required = false) String documentNumber,
                                        @ApiParam(value = "申请人ID") @RequestParam(value = "applicantId", required = false) Long applicantId,
                                        @ApiParam(value = "费用类型ID") @RequestParam(value = "expenseTypeId", required = false) Long expenseTypeId,
                                        @ApiParam(value = "金额从") @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                        @ApiParam(value = "金额到") @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                        @ApiParam(value = "公司ID") @RequestParam(value = "companyId", required = false) Long companyId,
                                        @ApiParam(value = "部门ID") @RequestParam(value = "departmentId", required = false) Long departmentId,
                                        @ApiParam(value = "业务日期从") @RequestParam(value = "transferDateFrom", required = false) String transferDateFrom,
                                        @ApiParam(value = "业务日到") @RequestParam(value = "transferDateTo", required = false) String transferDateTo,
                                        @ApiParam(value = "描述") @RequestParam(value = "description", required = false) String description,
                                        @ApiParam(value = "头ID") @RequestParam(value = "headerId") Long headerId,
                                        @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        Page result = expInputTaxLineService.getReportData(documentNumber, applicantId, expenseTypeId, amountFrom, amountTo,companyId,departmentId,transferDateFrom,transferDateTo,description,headerId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/input/line/getReportData");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    @PostMapping("/insertOrUpdate")
    @ApiOperation(value = "插入或更新头", notes = "插入或更新头 开发:ShilinMao")
    public ResponseEntity insertOrUpdateHeader(@ApiParam(value = "从报账单取其行数据的DTO") @RequestBody List<ExpInputForReportLineDTO> expInputForReportLineDTOs) {
        return ResponseEntity.ok(expInputTaxLineService.insertOrUpdateLine(expInputForReportLineDTOs));
    }
    @DeleteMapping("/delete")
    @ApiOperation(value = "根据id删除", notes = "根据id删除 开发:ShilinMao")
    public ResponseEntity deleteById(@ApiParam(value = "id") @RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(expInputTaxLineService.delete(id));
    }
}
