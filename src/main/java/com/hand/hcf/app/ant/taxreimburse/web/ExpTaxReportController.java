package com.hand.hcf.app.ant.taxreimburse.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReport;
import com.hand.hcf.app.ant.taxreimburse.service.ExpBankFlowService;
import com.hand.hcf.app.ant.taxreimburse.service.ExpTaxReportService;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * @description: 税金申报controller·
 * @date 2019/5/29 14:56
 */
@RestController
@RequestMapping("/api/exp/tax/report")
public class ExpTaxReportController {

    @Autowired
    private ExpTaxReportService expTaxReportService;

    @Autowired
    private SysCodeService sysCodeService;

    /**
     * 获取税金申报List的数据-分页查询
     * url-/api/exp/tax/report/list/query
     *
     * @param companyId
     * @param currencyCode
     * @param blendStatusCode
     * @param requestDateFrom
     * @param requestDateTo
     * @param requestAmountFrom
     * @param requestAmountTo
     * @param businessSubcategoryName
     * @param taxCategoryId
     * @param status
     * @param pageable
     * @return
     */

    @GetMapping("/list/query")
    public ResponseEntity<List<ExpTaxReport>> getExpTaxReportByCon(
            @RequestParam(required = false) String companyId,/*公司*/
            @RequestParam(required = false) String currencyCode,/*币种*/
            @RequestParam(required = false) String blendStatusCode,/*勾兑状态*/
            @RequestParam(required = false) String requestPeriodFrom,/*申报期间从*/
            @RequestParam(required = false) String requestPeriodTo,/*申报期间至*/
            @RequestParam(required = false) BigDecimal requestAmountFrom,/*申报金额从*/
            @RequestParam(required = false) BigDecimal requestAmountTo,/*申报金额至*/
            @RequestParam(required = false) String businessSubcategoryName,/*业务小类名称*/
            @RequestParam(required = false) String taxCategoryId,/*值列表值Id映射税种代码*/
            @RequestParam(required = false) Integer status,/*报账状态*/
            @ApiIgnore Pageable pageable) {


        Page page = PageUtil.getPage(pageable);
        List<ExpTaxReport> expTaxReportList = expTaxReportService.getExpTaxReportByCon(
                companyId,
                currencyCode,
                blendStatusCode,
                requestPeriodFrom,
                requestPeriodTo,
                requestAmountFrom,
                requestAmountTo,
                businessSubcategoryName,
                taxCategoryId,
                status,
                page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(expTaxReportList, httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/taxCategory/list/{taxCode}")
    public ResponseEntity<List<SysCodeValue>> listItemsByType(@PathVariable(value = "taxCode") String code) {
        return ResponseEntity.ok(sysCodeService.listEnabledSysCodeValueBySysCodeAnd(code));
    }

}
