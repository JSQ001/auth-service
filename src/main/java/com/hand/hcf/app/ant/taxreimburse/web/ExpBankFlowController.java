package com.hand.hcf.app.ant.taxreimburse.web;

import com.alipay.fc.fcbuservice.open.util.commons.StringUtil;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpBankFlow;
import com.hand.hcf.app.ant.taxreimburse.service.ExpBankFlowService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
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
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 银行流水controller·
 * @date 2019/5/29 14:56
 */
@RestController
@RequestMapping("/api/exp/bank/flow")
public class ExpBankFlowController {

    @Autowired
    private ExpBankFlowService expBankFlowService;


    /**
     * 获取银行流水List的数据-分页查询
     * url-/api/exp/bank/flow/list/query
     *
     * @param companyId
     * @param fundFlowNumber
     * @param bankAccountName
     * @param currencyCode
     * @param payDateFrom
     * @param payDateTo
     * @param flowAmountFrom
     * @param flowAmountTo
     * @param blendStatusCode
     * @param status
     * @param pageable
     * @return
     */

    @GetMapping("/list/query")
    public ResponseEntity<List<ExpBankFlow>> getExpTaxReportByCon(
            @RequestParam(required = false) String companyId,/*公司*/
            @RequestParam(required = false) String fundFlowNumber,/*资金流水号*/
            @RequestParam(required = false) String bankAccountName,/*对方名称*/
            @RequestParam(required = false) String currencyCode,/*币种*/
            @RequestParam(required = false) String payDateFrom,/*支付日期从*/
            @RequestParam(required = false) String payDateTo,/*支付日期至*/
            @RequestParam(required = false) BigDecimal flowAmountFrom,/*流水金额从*/
            @RequestParam(required = false) BigDecimal flowAmountTo,/*流水金额至*/
            @RequestParam(required = false) String blendStatusCode,/*勾兑状态*/
            @RequestParam(required = false) Integer status,/*报账状态*/
            @ApiIgnore Pageable pageable) {


        Page page = PageUtil.getPage(pageable);
        ZonedDateTime payDateFroms = TypeConversionUtils.getStartTimeForDayYYMMDD(payDateFrom);
        ZonedDateTime payDateTos = TypeConversionUtils.getEndTimeForDayYYMMDD(payDateTo);
        List<ExpBankFlow> expBankFlowList = expBankFlowService.getExpBankFlowByCon(
                companyId,
                fundFlowNumber,
                bankAccountName,
                currencyCode,
                payDateFroms,
                payDateTos,
                flowAmountFrom,
                flowAmountTo,
                blendStatusCode,
                status,
                page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(expBankFlowList, httpHeaders, HttpStatus.OK);
    }
}
