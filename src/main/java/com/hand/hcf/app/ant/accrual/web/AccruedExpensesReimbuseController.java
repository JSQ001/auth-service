package com.hand.hcf.app.ant.accrual.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.accrual.domain.AccruedExpensesHeader;
import com.hand.hcf.app.ant.accrual.domain.AccruedReimburse;
import com.hand.hcf.app.ant.accrual.service.AccruedExpensesHeaderService;
import com.hand.hcf.app.ant.accrual.service.AccruedExpensesReimbuseService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description: 预提报销单
 * @version: 1.0
 * @author: dazhuang.xie@hand-china.com
 * @date: 2019/6/18
 */

@RestController
@RequestMapping("/api/accrual")
public class AccruedExpensesReimbuseController {

    @Autowired
    private AccruedExpensesReimbuseService accruedExpensesReimbuseService;

    @GetMapping("/reimbuse/by/param")
    @ApiOperation(value = "预提报销查询", notes = "预提报销查询 开发:谢大壮")
    public ResponseEntity<List<AccruedReimburse>> getAccruedReimbuseByParam(
            @ApiParam(value = "单号") @RequestParam(value = "requisitionNumber", required = false) String requisitionNumber,
            @ApiParam(value = "创建日期从") @RequestParam(value = "accrualDateFrom", required = false) String accrualDateFrom,
            @ApiParam(value = "创建日期至") @RequestParam(value = "accrualDateTo", required = false) String accrualDateTo,
            @ApiParam(value = "预提单类型ID") @RequestParam(value = "expAccrualTypeId", required = false) Long expAccrualTypeId,
            @ApiParam(value = "状态") @RequestParam(value = "status", required = false) Integer status,
            @ApiParam(value = "责任人ID") @RequestParam(value = "demanderId", required = false) Long demanderId,
            @ApiParam(value = "金额从") @RequestParam(value = "accrualAmountFrom", required = false) BigDecimal accrualAmountFrom,
            @ApiParam(value = "金额至") @RequestParam(value = "accrualAmountTo", required = false) BigDecimal accrualAmountTo
    ){
        AccruedReimburse accruedReimburse = new AccruedReimburse();
        accruedReimburse.setRequisitionNumber(requisitionNumber);
        accruedReimburse.setAccrualDateFrom(accrualDateFrom);
        accruedReimburse.setToDate(accrualDateTo);
        accruedReimburse.setExpAccrualTypeId(expAccrualTypeId);
        accruedReimburse.setStatus(status);
        accruedReimburse.setDemanderId(demanderId);
        accruedReimburse.setAccrualAmountFrom(accrualAmountFrom);
        accruedReimburse.setAccrualAmountTo(accrualAmountTo);
        //return ResponseEntity.ok(accruedExpensesHeaderService.getAccruedReportById(expenseReportId));
        return ResponseEntity.ok(accruedExpensesReimbuseService.getAccruedReimbuse(accruedReimburse));
    }



}
