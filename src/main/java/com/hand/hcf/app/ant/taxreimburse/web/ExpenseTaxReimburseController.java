package com.hand.hcf.app.ant.taxreimburse.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpBankFlow;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReport;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpenseTaxReimburseHead;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpenseTaxReimburseVoucher;
import com.hand.hcf.app.ant.taxreimburse.service.ExpBankFlowService;
import com.hand.hcf.app.ant.taxreimburse.service.ExpTaxReportService;
import com.hand.hcf.app.ant.taxreimburse.service.ExpenseTaxReimburseHeadService;
import com.hand.hcf.app.ant.taxreimburse.service.ExpenseTaxReimburseVoucherService;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 国内税金缴纳报账单controller
 * @date 2019/6/10 10:26
 */
@RestController
@RequestMapping("/api/exp/tax/reimburse")
public class ExpenseTaxReimburseController {

    @Autowired
    private ExpenseTaxReimburseHeadService expenseTaxReimburseHeadService;

    @Autowired
    private ExpTaxReportService expTaxReportService;

    @Autowired
    private ExpBankFlowService expBankFlowService;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private ExpenseTaxReimburseVoucherService taxReimburseVoucherService;


    /**
     * 查询所有报账单头信息--url-/api/exp/tax/reimburse/head/list
     *
     * @param documentTypeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param applicantId
     * @param status
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param remark
     * @param benefited_id
     * @param benefitedDepartName
     * @param requisitionNumber
     * @param pageable
     * @return
     */
    @GetMapping("/head/list")
    public ResponseEntity<List<ExpenseTaxReimburseHead>> getTaxReimburseHeadList(
            @RequestParam(required = false) Long documentTypeId,
            @RequestParam(required = false) String requisitionDateFrom,
            @RequestParam(required = false) String requisitionDateTo,
            @RequestParam(required = false) Long applicantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) Long benefited_id,
            @RequestParam(required = false) String benefitedDepartName,
            @RequestParam(required = false) String requisitionNumber,
            Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        ZonedDateTime reqDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom);
        ZonedDateTime reqDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo);
        List<ExpenseTaxReimburseHead> expenseTaxReimburseHeadList = expenseTaxReimburseHeadService.getTaxReimburseList(
                documentTypeId,
                reqDateFrom,
                reqDateTo,
                applicantId,
                status,
                currencyCode,
                amountFrom,
                amountTo,
                remark,
                benefited_id,
                benefitedDepartName,
                requisitionNumber,
                page
        );
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(expenseTaxReimburseHeadList, totalHeader, HttpStatus.OK);
    }

    /**
     * 获取报账单头明细信息--/api/exp/tax/reimburse/head/by/id
     *
     * @param expenseReportId
     * @return
     */
    @GetMapping("/head/by/id")
    public ResponseEntity<ExpenseTaxReimburseHead> getExpenseReportById(@RequestParam Long expenseReportId) {
        ExpenseTaxReimburseHead expenseTaxReimburseHead = new ExpenseTaxReimburseHead();
        if (null != expenseReportId) {
            expenseTaxReimburseHead = expenseTaxReimburseHeadService.getExpenseReportById(expenseReportId);
        }
        return ResponseEntity.ok(expenseTaxReimburseHead);
    }


    /**
     * 导出国内税金缴纳报账单头信息
     *
     * @param request
     * @param response
     * @param exportConfig
     * @param documentTypeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param applicantId
     * @param status
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param remark
     * @param benefitedId
     * @param benefitedDepartName
     * @param requisitionNumber
     * @throws IOException
     */
    @RequestMapping("/head/export")
    public void export(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody ExportConfig exportConfig,
            @RequestParam(required = false) Long documentTypeId,
            @RequestParam(required = false) String requisitionDateFrom,
            @RequestParam(required = false) String requisitionDateTo,
            @RequestParam(required = false) Long applicantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) Long benefitedId,
            @RequestParam(required = false) String benefitedDepartName,
            @RequestParam(required = false) String requisitionNumber)
            throws IOException {
        ZonedDateTime reqDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom);
        ZonedDateTime reqDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo);
        Page<ExpenseTaxReimburseHead> taxReimburseHeadByPage = expenseTaxReimburseHeadService.getTaxReimburseHeadByPage(
                documentTypeId,
                reqDateFrom,
                reqDateTo,
                applicantId,
                status,
                currencyCode,
                amountFrom,
                amountTo,
                remark,
                benefitedId,
                benefitedDepartName,
                requisitionNumber,
                new Page<ExpenseTaxReimburseHead>(1, 0));
        int total = TypeConversionUtils.parseInt(taxReimburseHeadByPage.getTotal());
        String name = exportConfig.getFileName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = "_" + sdf.format(new Date());
        String fileName = name + date;
        exportConfig.setFileName(fileName);
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ExpenseTaxReimburseHead, ExpenseTaxReimburseHead>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ExpenseTaxReimburseHead> queryDataByPage(Page page) {
                List<ExpenseTaxReimburseHead> list = expenseTaxReimburseHeadService.exportTaxReimburseHead(
                        documentTypeId,
                        reqDateFrom,
                        reqDateTo,
                        applicantId,
                        status,
                        currencyCode,
                        amountFrom,
                        amountTo,
                        remark,
                        benefitedId,
                        benefitedDepartName,
                        requisitionNumber);
                return list;
            }

            @Override
            public ExpenseTaxReimburseHead toDTO(ExpenseTaxReimburseHead t) {
                return t;
            }

            @Override
            public Class<ExpenseTaxReimburseHead> getEntityClass() {
                return ExpenseTaxReimburseHead.class;
            }
        }, threadNumber, request, response);
    }

    /**
     * 发起报账后，新建报账单 url-/api/exp/tax/reimburse/head/save
     *
     * @param ids
     * @param expenseTaxReimburseHead
     * @return
     */
    @PostMapping("/head/save/{ids}")
    public ResponseEntity<ExpenseTaxReimburseHead> saveExpenseReportHeader(@PathVariable(value = "ids") String ids, @RequestBody ExpenseTaxReimburseHead expenseTaxReimburseHead) {
        return ResponseEntity.ok(expenseTaxReimburseHeadService.saveTaxReimburseHead(ids, expenseTaxReimburseHead));
    }

    /**
     * 导出税金明细信息-行页面
     *
     * @param request
     * @param response
     * @param exportConfig
     * @param expReimburseHeaderId
     * @throws IOException
     */
    @RequestMapping("/line/tax/export")
    public void exportTaxDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody ExportConfig exportConfig,
            @RequestParam String expReimburseHeaderId
    ) throws IOException {
        Page<ExpTaxReport> taxReimburseHeadByPage = expTaxReportService.getTaxReportDetail(
                expReimburseHeaderId,
                new Page<ExpTaxReport>(1, 0));
        int total = TypeConversionUtils.parseInt(taxReimburseHeadByPage.getTotal());
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
                List<ExpTaxReport> list = expTaxReportService.exportTaxDetail(expReimburseHeaderId);
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
     * 导出支付明细信息-行页面
     *
     * @param request
     * @param response
     * @param exportConfig
     * @param expReimburseHeaderId
     * @throws IOException
     */
    @RequestMapping("/line/payment/export")
    public void exportPaymentDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody ExportConfig exportConfig,
            @RequestParam String expReimburseHeaderId
    ) throws IOException {
        Page<ExpBankFlow> taxReimburseHeadByPage = expBankFlowService.getPaymentDetail(
                expReimburseHeaderId,
                new Page<ExpTaxReport>(1, 0));
        int total = TypeConversionUtils.parseInt(taxReimburseHeadByPage.getTotal());
        String name = exportConfig.getFileName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = "_" + sdf.format(new Date());
        String fileName = name + date;
        exportConfig.setFileName(fileName);
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ExpBankFlow, ExpBankFlow>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ExpBankFlow> queryDataByPage(Page page) {
                List<ExpBankFlow> list = expBankFlowService.exportPaymentDeatil(expReimburseHeaderId);
                return list;
            }

            @Override
            public ExpBankFlow toDTO(ExpBankFlow t) {
                return t;
            }

            @Override
            public Class<ExpBankFlow> getEntityClass() {
                return ExpBankFlow.class;
            }
        }, threadNumber, request, response);
    }

    /**
     * 导出凭证数据信息-行页面
     *
     * @param request
     * @param response
     * @param exportConfig
     * @param expReimburseHeaderId
     * @throws IOException
     */
    @RequestMapping("/line/voucher/export")
    public void exportVoucherDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody ExportConfig exportConfig,
            @RequestParam String expReimburseHeaderId
    ) throws IOException {
        Page<ExpenseTaxReimburseVoucher> taxReimburseVoucherPage = taxReimburseVoucherService.getVoucherDetail(
                expReimburseHeaderId,
                new Page<ExpTaxReport>(1, 0));
        int total = TypeConversionUtils.parseInt(taxReimburseVoucherPage.getTotal());
        String name = exportConfig.getFileName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = "_" + sdf.format(new Date());
        String fileName = name + date;
        exportConfig.setFileName(fileName);
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<ExpenseTaxReimburseVoucher, ExpenseTaxReimburseVoucher>() {
            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<ExpenseTaxReimburseVoucher> queryDataByPage(Page page) {
                List<ExpenseTaxReimburseVoucher> list = taxReimburseVoucherService.exportVoucherDeatil(expReimburseHeaderId);
                return list;
            }

            @Override
            public ExpenseTaxReimburseVoucher toDTO(ExpenseTaxReimburseVoucher t) {
                return t;
            }

            @Override
            public Class<ExpenseTaxReimburseVoucher> getEntityClass() {
                return ExpenseTaxReimburseVoucher.class;
            }
        }, threadNumber, request, response);
    }


    /**
     * 报账单提交
     *
     * @param documentId
     * @return
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public boolean submit(@RequestParam String documentId) {
        return expenseTaxReimburseHeadService.submit(documentId);
    }

    /**
     * 报账单撤回-只有审批中的才可撤回
     *
     * @param documentId
     * @return
     */
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    public boolean withdraw(@RequestParam String documentId) {
        return expenseTaxReimburseHeadService.withdraw(documentId);
    }

    /**
     * 报账单删除-只有编辑中的才可删除，并且修改税金/银行数据状态
     *
     * @param documentId
     * @return
     */
    @RequestMapping(value = "/detele/by/headId", method = RequestMethod.POST)
    public boolean deleteById(@RequestParam String documentId) {
        return expenseTaxReimburseHeadService.deleteById(documentId);
    }

    /**
     * 批量-报账单删除-只有编辑中的才可删除，并且修改税金/银行数据状态
     * url:/api/exp/tax/reimburse/head/batch/delete
     *
     * @param ids
     */
    @DeleteMapping("/head/batch/delete")
    public boolean deleteBatch(@RequestParam String ids) {
        return expenseTaxReimburseHeadService.deleteReimburseBatchs(ids);
    }

}
