package com.hand.hcf.app.ant.accrualExpense.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.accrualExpense.domain.AccruedExpensesHeader;
import com.hand.hcf.app.ant.accrualExpense.service.AccruedExpensesHeaderService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/5/20
 */

@RestController
@RequestMapping("/api/accrual/report")
public class AccruedExpensesController {

    @Autowired
    private AccruedExpensesHeaderService accruedExpensesHeaderService;

    /**
     * 我的预提单
     *
     * @param requisitionNumber
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param companyId
     * @param documentTypeId
     * @param demanderId
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param editor
     * @param passed
     * @param pageable
     * @return
     */
    @GetMapping("/header/my")
    public ResponseEntity<List<AccruedExpensesHeader>> getAccruedExpenseReports(@ApiParam(value = "单据号") @RequestParam(required = false) String requisitionNumber,
                                                                                @ApiParam(value = "创建日期从") @RequestParam(required = false) String requisitionDateFrom,
                                                                                @ApiParam(value = "创建日期至") @RequestParam(required = false) String requisitionDateTo,
                                                                                @ApiParam(value = "公司id") @RequestParam(required = false) Long companyId,
                                                                                @ApiParam(value = "预提事项id") @RequestParam(required = false) Long documentTypeId,
                                                                                @ApiParam(value = "责任人id") @RequestParam(required = false) Long demanderId,
                                                                                @ApiParam(value = "币种") @RequestParam(required = false) String currencyCode,
                                                                                @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal amountFrom,
                                                                                @ApiParam(value = "金额至") @RequestParam(required = false) BigDecimal amountTo,
                                                                                @ApiParam(value = "编辑中标识，为true时查询1001，1003，1005,2001的单据") @RequestParam(required = false, defaultValue = "false") Boolean editor,
                                                                                @ApiParam(value = "通过标识，为true时查询1002和1004的单据") @RequestParam(required = false, defaultValue = "false") Boolean passed,
                                                                                @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        ZonedDateTime reqDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom);
        ZonedDateTime reqDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo);
        List<AccruedExpensesHeader> accruedExpenseReports = accruedExpensesHeaderService.getExpenseReports(requisitionNumber,
                reqDateFrom,
                reqDateTo,
                companyId,
                documentTypeId,
                demanderId,
                currencyCode,
                amountFrom,
                amountTo,
                editor,
                passed,
                page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(accruedExpenseReports, totalHeader, HttpStatus.OK);
    }

}
