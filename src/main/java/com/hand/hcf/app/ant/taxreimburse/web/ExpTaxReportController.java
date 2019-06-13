package com.hand.hcf.app.ant.taxreimburse.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReport;
import com.hand.hcf.app.ant.taxreimburse.dto.TaxBlendDataDTO;
import com.hand.hcf.app.ant.taxreimburse.service.ExpBankFlowService;
import com.hand.hcf.app.ant.taxreimburse.service.ExpTaxReportService;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private ExpBankFlowService expBankFlowService;

    @Autowired
    private SysCodeService sysCodeService;

    @Autowired
    private ExcelExportService excelExportService;

    /**
     * 获取税金申报List的数据-分页查询
     * url-/api/exp/tax/report/list/query
     *
     * @param companyId
     * @param currencyCode
     * @param blendStatus
     * @param requestPeriodFrom
     * @param requestPeriodTo
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
            @RequestParam(required = false) Boolean blendStatus,/*勾兑状态*/
            @RequestParam(required = false) String requestPeriodFrom,/*申报期间从*/
            @RequestParam(required = false) String requestPeriodTo,/*申报期间至*/
            @RequestParam(required = false) BigDecimal requestAmountFrom,/*申报金额从*/
            @RequestParam(required = false) BigDecimal requestAmountTo,/*申报金额至*/
            @RequestParam(required = false) String businessSubcategoryName,/*业务小类名称*/
            @RequestParam(required = false) String taxCategoryId,/*值列表值Id映射税种代码*/
            @RequestParam(required = false) Boolean status,/*报账状态*/
            Pageable pageable) {


        Page page = PageUtil.getPage(pageable);
        List<ExpTaxReport> expTaxReportList = expTaxReportService.getExpTaxReportByCon(
                companyId,
                currencyCode,
                blendStatus,
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

    /**
     * 查询值列表中的税种
     *
     * @param code
     * @return
     */
    @GetMapping("/taxCategory/list/{taxCode}")
    public ResponseEntity<List<SysCodeValue>> listItemsByType(@PathVariable(value = "taxCode") String code) {
        return ResponseEntity.ok(sysCodeService.listEnabledSysCodeValueBySysCodeAnd(code));
    }


    /**
     * 导出税金申报数据 url:api/exp/tax/report/export
     *
     * @param request
     * @param response
     * @param exportConfig
     * @param companyId
     * @param currencyCode
     * @param blendStatus
     * @param requestPeriodFrom
     * @param requestPeriodTo
     * @param requestAmountFrom
     * @param requestAmountTo
     * @param businessSubcategoryName
     * @param taxCategoryId
     * @param status
     * @throws IOException
     */
    @RequestMapping("/export")
    public void export(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody ExportConfig exportConfig,
            @RequestParam(required = false) String companyId,/*公司*/
            @RequestParam(required = false) String currencyCode,/*币种*/
            @RequestParam(required = false) Boolean blendStatus,/*勾兑状态*/
            @RequestParam(required = false) String requestPeriodFrom,/*申报期间从*/
            @RequestParam(required = false) String requestPeriodTo,/*申报期间至*/
            @RequestParam(required = false) BigDecimal requestAmountFrom,/*申报金额从*/
            @RequestParam(required = false) BigDecimal requestAmountTo,/*申报金额至*/
            @RequestParam(required = false) String businessSubcategoryName,/*业务小类名称*/
            @RequestParam(required = false) String taxCategoryId,/*值列表值Id映射税种代码*/
            @RequestParam(required = false) Boolean status/*报账状态*/
    ) throws IOException {
        Page<ExpTaxReport> expTaxReportPage = expTaxReportService.getExpTaxReportByPage(
                companyId,
                currencyCode,
                blendStatus,
                requestPeriodFrom,
                requestPeriodTo,
                requestAmountFrom,
                requestAmountTo,
                businessSubcategoryName,
                taxCategoryId,
                status,
                new Page<ExpTaxReport>(1, 0)
        );
        int total = TypeConversionUtils.parseInt(expTaxReportPage.getTotal());

        String name = exportConfig.getFileName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = "_" + sdf.format(new Date());
        String fileName = name + date;
        exportConfig.setFileName(fileName);
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ExpTaxReport, ExpTaxReport>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ExpTaxReport> queryDataByPage(Page page) {
                List<ExpTaxReport> list = expTaxReportService.exportExpTaxReport(
                        companyId,
                        currencyCode,
                        blendStatus,
                        requestPeriodFrom,
                        requestPeriodTo,
                        requestAmountFrom,
                        requestAmountTo,
                        businessSubcategoryName,
                        taxCategoryId,
                        status);
                return list;
            }

            @Override
            public ExpTaxReport toDTO(ExpTaxReport t) {
                return t;
            }

            @Override
            public Class<ExpTaxReport> getEntityClass() {
                return ExpTaxReport.class;
            }
        }, threadNumber, request, response);
    }

    /**
     * 根据Id 批量删除税金申报数据
     * url:api/exp/tax/report/delete
     * @param ids
     */
    @DeleteMapping("/delete")
    public void delete(@RequestParam String ids) {
        expTaxReportService.deleteTaxReport(ids);
    }


    /**
     * 自动勾兑 url:api/exp/tax/report/auto/blend
     */
    @GetMapping("/auto/blend")
    public void autoBlend() {
        //分组查询相同公司、相同币种的税金申报数据，总计申报金额
        List<TaxBlendDataDTO> taxBlendDataDTOList = expTaxReportService.getBlendDataByGroup();
        for (TaxBlendDataDTO taxBlendDataDTO : taxBlendDataDTOList) {
            //分组查询相同公司、相同币种的银行流水数据，总计流水金额
            BigDecimal requestAmoutSum = taxBlendDataDTO.getRequestAmountSum();
            BigDecimal flowAmountSum = taxBlendDataDTO.getFlowAmountSum();
            //比较相同公司、相同币种的申报金额和流水金额是否相等，若相等则更新此公司和币种的数据状态为已勾兑。
            if (null != requestAmoutSum && null != flowAmountSum && requestAmoutSum.equals(flowAmountSum)) {
                Long companyId = taxBlendDataDTO.getCompanyId();
                String currencyCode = taxBlendDataDTO.getCurrencyCode();
                if (null != companyId && StringUtils.isNotEmpty(currencyCode)) {
                    expTaxReportService.updateTaxReport(companyId, currencyCode);
                    expBankFlowService.updateBankFlow(companyId, currencyCode);
                }
            }
        }
    }


    @PostMapping("/saveorupdate")
    public ResponseEntity<List<ExpTaxReport>> saveExpTaxReport(@RequestParam String ids, @RequestBody List<ExpTaxReport> expTaxReportList) {

        expTaxReportService.saveExpTaxReport(ids, expTaxReportList);
        return ResponseEntity.ok(null);
    }

    /**
     * 发起报账 url:/api/exp/bank/flow/make/report
     *
     * @param rowIds
     */
    @PostMapping("/make/report")
    public Boolean makeReimburse(@RequestParam String rowIds) {

        return expTaxReportService.makeReimburse(rowIds);
    }

    /**
     * 详情页面税金明细信息显示
     * @param reimburseHeaderId
     * @param pageable
     * @return
     */
    @GetMapping("list/by/headId")
    public ResponseEntity<List<ExpTaxReport>> getTaxReportDetail(@RequestParam(required = false) String reimburseHeaderId,Pageable pageable ){
        Page page = PageUtil.getPage(pageable);
        List<ExpTaxReport> expTaxReportList = expTaxReportService.getTaxReportDetailList(reimburseHeaderId,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(expTaxReportList, httpHeaders, HttpStatus.OK);
    }

}
