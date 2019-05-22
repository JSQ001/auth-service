package com.hand.hcf.app.expense.invoice.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineExpence;
import com.hand.hcf.app.expense.invoice.dto.InvoiceBatchCheckResultDTO;
import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import com.hand.hcf.app.expense.invoice.dto.InvoiceLineDistDTO;
import com.hand.hcf.app.expense.invoice.dto.InvoiceLineExpenceWebQueryDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceHeadService;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.report.dto.ExpenseReportInvoiceMatchResultDTO;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/16
 */
/**
 * @apiDefine InvoiceService 发票
 */
@Api(tags = "发票头")
@RestController
@RequestMapping("/api/invoice/head")
public class InvoiceHeadController {
    private final InvoiceHeadService invoiceHeadService;

    private ExcelExportService excelExportService;

    public InvoiceHeadController(InvoiceHeadService invoiceHeadService,ExcelExportService excelExportService){
        this.invoiceHeadService = invoiceHeadService;
        this.excelExportService = excelExportService;
    }

    /**
     * 新建 发票头、行
     * @param invoiceDTO
     * @return
     */

    @PostMapping("/insert/invoice")
    @ApiOperation(value = "新建发票头、行", notes = "新建发票头、行 开发:xue.han")
    public ResponseEntity<InvoiceDTO> insertInvoice(@ApiParam(value = "发票信息") @RequestBody InvoiceDTO invoiceDTO){
        return ResponseEntity.ok(invoiceHeadService.insertInvoice(invoiceDTO));
    }

    /**
     * 校验录入的发票代码和号码是否已经在发票头表存在
     * @param invoiceCode 发票代码
     * @param invoiceNo 发票号码
     * @return
     */

    @GetMapping("/check/invoiceCode/invoiceNo")
    @ApiOperation(value = "校验录入的发票代码和号码是否已经在发票头表存在", notes = "校验录入的发票代码和号码是否已经在发票头表存在 开发:xue.han")
    public ResponseEntity<String> checkInvoiceCodeInvoiceNo(@ApiParam(value = "发票代码") @RequestParam(value = "invoiceCode") String invoiceCode,
                                                            @ApiParam(value = "发票号码") @RequestParam(value = "invoiceNo") String invoiceNo)throws URISyntaxException {
        return ResponseEntity.ok(invoiceHeadService.checkInvoiceCodeInvoiceNo(invoiceCode,invoiceNo));
    }

    /**
     * 更新 发票头
     * @param invoiceHead
     * @return
     */
    /*@PutMapping
    public ResponseEntity<InvoiceHead> updateInvoiceHead(@RequestBody InvoiceHead invoiceHead){
        return ResponseEntity.ok(invoiceHeadService.updateInvoiceHead(invoiceHead));
    }*/

    /**
     * 根据发票id 查询发票头行信息
     * @param id
     * @return
     */

    @GetMapping("/{id}")
    @ApiOperation(value = " 根据发票id 查询发票头行信息", notes = " 根据发票id 查询发票头行信息 开发:xue.han")
    public ResponseEntity<InvoiceDTO> getInvoiceByHeadId(@PathVariable Long id){
        return ResponseEntity.ok(invoiceHeadService.getInvoiceByHeadId(id));
    }

    /**
     * 根据条件分页查询 我的票夹
     * @param createdBy 用户id
     * @param invoiceTypeId 发票类型id
     * @param invoiceNo 发票号码
     * @param invoiceCode 发票代码
     * @param invoiceDateFrom 开票日期从
     * @param invoiceDateTo 开票日期至
     * @param invoiceAmountFrom 金额合计从
     * @param invoiceAmountTo 金额合计至
     * @param createdMethod 创建方式
     * @param checkResult 验真状态
     * @param reportProgress 报账进度
     * @param pageable
     * @return
     * @throws URISyntaxException
     */

    @GetMapping("/query/by/cond")
    @ApiOperation(value = "根据条件分页查询我的票夹", notes = "根据条件分页查询我的票夹 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<InvoiceHead>> getInvoiceHeadByCond(
            @ApiParam(value = "用户id") @RequestParam(value = "createdBy") Long createdBy,
            @ApiParam(value = "发票类型id") @RequestParam(value = "invoiceTypeId",required = false)Long invoiceTypeId,
            @ApiParam(value = "发票号码") @RequestParam(value = "invoiceNo",required = false)String invoiceNo,
            @ApiParam(value = "发票代码") @RequestParam(value = "invoiceCode",required = false)String invoiceCode,
            @ApiParam(value = "开票日期从") @RequestParam(value = "invoiceDateFrom",required = false)String invoiceDateFrom,
            @ApiParam(value = "开票日期到") @RequestParam(value = "invoiceDateTo",required = false)String invoiceDateTo,
            @ApiParam(value = "金额合计从") @RequestParam(value = "invoiceAmountFrom",required = false)BigDecimal invoiceAmountFrom,
            @ApiParam(value = "金额合计到") @RequestParam(value = "invoiceAmountTo",required = false)BigDecimal invoiceAmountTo,
            @ApiParam(value = "创建方式") @RequestParam(value = "createdMethod",required = false)String createdMethod,
            @ApiParam(value = "验证状态") @RequestParam(value = "checkResult",required = false)Boolean checkResult,
            @ApiParam(value = "报账进度") @RequestParam(value = "reportProgress",required = false)String reportProgress,
            @ApiIgnore Pageable pageable)throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<InvoiceHead> result = invoiceHeadService.getInvoiceHeadByCond(createdBy,
                invoiceTypeId,
                invoiceNo,
                invoiceCode,
                invoiceDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(invoiceDateFrom),
                invoiceDateTo  == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(invoiceDateTo),
                invoiceAmountFrom,
                invoiceAmountTo,
                createdMethod,
                checkResult,
                reportProgress,
                page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/invoice/head/query/by/cond");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 根据发票头id集合 批量删除 发票
     * @param headIds
     * @return
     */

    @DeleteMapping("/delete/invoice/by/headIds")
    @ApiOperation(value = "根据发票头id集合批量删除发票(如果返回的集合为空，则说明成功删除；反之有发票已经关联了报账单)", notes = "根据发票头id集合批量删除发票(如果返回的集合为空，则说明成功删除；反之有发票已经关联了报账单) 开发:xue.han")
    public ResponseEntity<List<InvoiceHead>> deleteInvoiceByIds(@ApiParam(value = "发票头id集合") @RequestBody List<Long> headIds){
        return ResponseEntity.ok(invoiceHeadService.deleteInvoiceByIds(headIds));
    }

    /**
     * 根据发票头id 批量验真发票
     * @param headIds
     * @return
     */

    @PostMapping("/check/invoice/by/headIds")
    @ApiOperation(value = "根据发票头id集合批量验真发票", notes = "根据发票头id集合批量验真发票 开发:xue.han")
    public ResponseEntity<InvoiceBatchCheckResultDTO> checkInvoice(@ApiParam(value = "发票头id集合") @RequestBody List<Long> headIds){

        return ResponseEntity.ok(invoiceHeadService.checkInvoice(headIds));
    }

    /**
     * 关联报账单详情分页查询
     * @param headId 发票头id
     * @param expenseNum 报账单单号
     * @param expenseTypeId 报账单类型id
     * @param pageable
     * @return
     * @throws URISyntaxException
     */

    @GetMapping("/query/invoice/line/expense/by/headId")
    @ApiOperation(value = "关联报账单详情分页查询", notes = "关联报账单详情分页查询 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<InvoiceLineExpenceWebQueryDTO>> getInvoiceLineExpenceByHeadId(
            @ApiParam(value = "发票头id") @RequestParam("headId")Long headId,
            @ApiParam(value = "报账单单号") @RequestParam(value = "expenseNum",required = false)String expenseNum,
            @ApiParam(value = "报账单类型id") @RequestParam(value = "expenseTypeId",required = false)Long expenseTypeId,
            @ApiIgnore Pageable pageable)throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        Page<InvoiceLineExpenceWebQueryDTO> result = invoiceHeadService.getInvoiceLineExpenceByHeadId(headId, expenseNum, expenseTypeId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/invoice/head/query/invoice/line/expense/by/headId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 发票报账明细分页查询
     * @param invoiceTypeId
     * @param invoiceCode
     * @param invoiceNo
     * @param expenseNum
     * @param invoiceDateFrom
     * @param invoiceDateTo
     * @param invoiceAmountFrom
     * @param invoiceAmountTo
     * @param invoiceLineNumFrom
     * @param invoiceLineNumTo
     * @param taxRate
     * @param taxAmountFrom
     * @param taxAmountTo
     * @param pageable
     * @return
     * @throws URISyntaxException
     */

    @GetMapping("/query/invoice/line/dist/by/cond")
    @ApiOperation(value = "发票报账明细分页查询", notes = "发票报账明细分页查询 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<InvoiceLineDistDTO>> getInvoiceLineDistByCond(
            @ApiParam(value = "发票类型id") @RequestParam(value = "invoiceTypeId",required = false)Long invoiceTypeId,
            @ApiParam(value = "发票代码") @RequestParam(value = "invoiceCode",required = false)String invoiceCode,
            @ApiParam(value = "发票编号") @RequestParam(value = "invoiceNo",required = false)String invoiceNo,
            @ApiParam(value = "报账单单号") @RequestParam(value = "expenseNum",required = false)String expenseNum,
            @ApiParam(value = "开票日期从") @RequestParam(value = "invoiceDateFrom",required = false)String invoiceDateFrom,
            @ApiParam(value = "开票日期至") @RequestParam(value = "invoiceDateTo",required = false)String invoiceDateTo,
            @ApiParam(value = "金额合计从") @RequestParam(value = "invoiceAmountFrom",required = false)BigDecimal invoiceAmountFrom,
            @ApiParam(value = "金额合计至") @RequestParam(value = "invoiceAmountTo",required = false)BigDecimal invoiceAmountTo,
            @ApiParam(value = "发票行序号从") @RequestParam(value = "invoiceLineNumFrom",required = false)Integer invoiceLineNumFrom,
            @ApiParam(value = "发票行序号至") @RequestParam(value = "invoiceLineNumTo",required = false)Integer invoiceLineNumTo,
            @ApiParam(value = "税率") @RequestParam(value = "taxRate",required = false)String taxRate,
            @ApiParam(value = "税额从") @RequestParam(value = "taxAmountFrom",required = false)BigDecimal taxAmountFrom,
            @ApiParam(value = "税额至") @RequestParam(value = "taxAmountTo",required = false)BigDecimal taxAmountTo,
            @ApiParam(value = "申请日期从") @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
            @ApiParam(value = "申请日期至") @RequestParam(value = "applyDateTo",required = false)String applyDateTo,
            @ApiParam(value = "申请人") @RequestParam(value = "applicant",required = false)Long applicant,
            @ApiParam(value = "单据状态") @RequestParam(value = "documentStatus",required = false)String documentStatus,
            @ApiParam(value = "费用行号从") @RequestParam(value = "costLineNumberFrom",required = false)Long costLineNumberFrom,
            @ApiParam(value = "费用行号到") @RequestParam(value = "costLineNumberTo",required = false)Long costLineNumberTo,
            @ApiParam(value = "费用类型") @RequestParam(value = "costType",required = false)String costType,
            @ApiParam(value = "费用金额从") @RequestParam(value = "costAmountFrom",required = false)BigDecimal costAmountFrom,
            @ApiParam(value = "费用金额到") @RequestParam(value = "costAmountTo",required = false)BigDecimal costAmountTo,
            @ApiParam(value = "分期抵扣") @RequestParam(value = "installmentDeduction",required = false)Boolean installmentDeduction,
            @ApiIgnore Pageable pageable)throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<InvoiceLineDistDTO> result = invoiceHeadService.getInvoiceLineDistByCond(
                invoiceTypeId,
                invoiceCode,
                invoiceNo,
                expenseNum,
                invoiceDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(invoiceDateFrom),
                invoiceDateTo  == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(invoiceDateTo),
                invoiceAmountFrom, invoiceAmountTo,
                invoiceLineNumFrom, invoiceLineNumTo,
                taxRate,
                taxAmountFrom,
                taxAmountTo,
                applyDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(applyDateFrom),
                applyDateTo  == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(applyDateTo),
                applicant,
                documentStatus,
                costLineNumberFrom,
                costLineNumberTo,
                costType,
                costAmountFrom,
                costAmountTo,
                installmentDeduction,
                page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/invoice/head/query/invoice/line/dist/by/cond");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }


    /**
     * 导出 我的票夹数据
     * @param request
     * @param exportConfig
     * @param response
     * @param createdBy
     * @param pageable
     * @throws IOException
     */
    @PostMapping("/export/invoice/head/info")
    @ApiOperation(value = "导出 我的票夹数据", notes = "导出 我的票夹数据 开发:xue.han")
    public void exportInvoiceHeadInfo(HttpServletRequest request,
                                      @ApiParam(value = "导出配置") @RequestBody ExportConfig exportConfig,
                                      HttpServletResponse response,
                                      @ApiParam(value = "创建人") @RequestParam(value = "createdBy")Long createdBy,
                                      Pageable pageable) throws IOException {
        Page page = PageUtil.getPage(pageable);
        Page<InvoiceHead> invoiceHeadPage = invoiceHeadService.getInvoiceHeadByCond(createdBy,
                null,null, null,
                null, null,
                null,null,
                null,null,null,page);
        int total = TypeConversionUtils.parseInt(page.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<InvoiceHead, InvoiceHead>() {

            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<InvoiceHead> queryDataByPage(Page page) {
                Page<InvoiceHead> invoiceHeadPage = invoiceHeadService.getInvoiceHeadByCond(createdBy,
                        null,null, null,
                        null, null,
                        null,null,
                        null,null,null,page);
                return invoiceHeadPage.getRecords();
            }

            @Override
            public InvoiceHead toDTO(InvoiceHead invoiceHead) {
                invoiceHead.setStringInvoiceDate(DateUtil.ZonedDateTimeToString(invoiceHead.getInvoiceDate()));
                //给验真状态checkResult，赋值文字(之前是前端赋值的)
                if (invoiceHead.getCheckResult() != null) {
                    if (invoiceHead.getCheckResult() == true) {
                        invoiceHead.setStringCheckResult("已验真");
                    } else {
                        invoiceHead.setStringCheckResult("未验真");
                    }
                }
                return invoiceHead;
            }

            @Override
            public Class<InvoiceHead> getEntityClass() {
                return InvoiceHead.class;
            }
        },threadNumber,request,response);
    }

    /**
     * 导出 发票报账明细数据
     * @param request
     * @param exportConfig
     * @param response
     * @param
     * @param pageable
     * @throws IOException
     */
    @PostMapping("/export/invoice/line/dist/info")
    @ApiOperation(value = "导出 发票报账明细数据", notes = "导出 发票报账明细数据 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public void exportInvoiceLineDistInfo(HttpServletRequest request,
                                          @ApiParam(value = "账套ID") @RequestBody ExportConfig exportConfig,
                                      HttpServletResponse response,
                                          @ApiParam(value = "发票类型id") @RequestParam(value = "invoiceTypeId",required = false)Long invoiceTypeId,
                                          @ApiParam(value = "发票代码") @RequestParam(value = "invoiceCode",required = false)String invoiceCode,
                                          @ApiParam(value = "发票编号") @RequestParam(value = "invoiceNo",required = false)String invoiceNo,
                                          @ApiParam(value = "报账单单号")  @RequestParam(value = "expenseNum",required = false)String expenseNum,
                                          @ApiParam(value = "开票日期从")  @RequestParam(value = "invoiceDateFrom",required = false)String invoiceDateFrom,
                                          @ApiParam(value = "开票日期至")  @RequestParam(value = "invoiceDateTo",required = false)String invoiceDateTo,
                                          @ApiParam(value = "金额合计从")  @RequestParam(value = "invoiceAmountFrom",required = false)BigDecimal invoiceAmountFrom,
                                          @ApiParam(value = "金额合计至")  @RequestParam(value = "invoiceAmountTo",required = false)BigDecimal invoiceAmountTo,
                                          @ApiParam(value = "发票行序号从") @RequestParam(value = "invoiceLineNumFrom",required = false)Integer invoiceLineNumFrom,
                                          @ApiParam(value = "发票行序号至") @RequestParam(value = "invoiceLineNumTo",required = false)Integer invoiceLineNumTo,
                                          @ApiParam(value = "税率")  @RequestParam(value = "taxRate",required = false)String taxRate,
                                          @ApiParam(value = "税额从")  @RequestParam(value = "taxAmountFrom",required = false)BigDecimal taxAmountFrom,
                                          @ApiParam(value = "税额至")  @RequestParam(value = "taxAmountTo",required = false)BigDecimal taxAmountTo,
                                          @ApiParam(value = "申请日期从")  @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                                          @ApiParam(value = "申请日期至")  @RequestParam(value = "applyDateTo",required = false)String applyDateTo,
                                          @ApiParam(value = "申请人")  @RequestParam(value = "applicant",required = false)Long applicant,
                                          @ApiParam(value = "单据状态") @RequestParam(value = "documentStatus",required = false)String documentStatus,
                                          @ApiParam(value = "费用行号从")  @RequestParam(value = "costLineNumberFrom",required = false)Long costLineNumberFrom,
                                          @ApiParam(value = "费用行号到")  @RequestParam(value = "costLineNumberTo",required = false)Long costLineNumberTo,
                                          @ApiParam(value = "费用类型") @RequestParam(value = "costType",required = false)String costType,
                                          @ApiParam(value = "费用金额从")  @RequestParam(value = "costAmountFrom",required = false)BigDecimal costAmountFrom,
                                          @ApiParam(value = "费用金额到")  @RequestParam(value = "costAmountTo",required = false)BigDecimal costAmountTo,
                                          @ApiParam(value = "分期抵扣") @RequestParam(value = "installmentDeduction",required = false)Boolean installmentDeduction,
                                          @ApiIgnore Pageable pageable) throws IOException {
        Page page = PageUtil.getPage(pageable);
        Page<InvoiceLineDistDTO> invoiceLineDistDTOPage = invoiceHeadService.getInvoiceLineDistByCond(
                invoiceTypeId,
                invoiceCode,
                invoiceNo,
                expenseNum,
                invoiceDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(invoiceDateFrom),
                invoiceDateTo  == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(invoiceDateTo),
                invoiceAmountFrom, invoiceAmountTo,
                invoiceLineNumFrom, invoiceLineNumTo,
                taxRate,
                taxAmountFrom,
                taxAmountTo,
                applyDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(applyDateFrom),
                applyDateTo  == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(applyDateTo),
                applicant,
                documentStatus,
                costLineNumberFrom,
                costLineNumberTo,
                costType,
                costAmountFrom,
                costAmountTo,
                installmentDeduction,
                page);
        int total = TypeConversionUtils.parseInt(page.getTotal());
        int threadNumber = total > 100000 ? 8 : 2;
        excelExportService.exportAndDownloadExcel(exportConfig, new ExcelExportHandler<InvoiceLineDistDTO, InvoiceLineDistDTO>() {

            @Override
            public int getTotal() {
                return total;
            }

            @Override
            public List<InvoiceLineDistDTO> queryDataByPage(Page page) {
                Page<InvoiceLineDistDTO> invoiceLineDistDTOPage = invoiceHeadService.getInvoiceLineDistByCond(
                        invoiceTypeId,
                        invoiceCode,
                        invoiceNo,
                        expenseNum,
                        invoiceDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(invoiceDateFrom),
                        invoiceDateTo  == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(invoiceDateTo),
                        invoiceAmountFrom, invoiceAmountTo,
                        invoiceLineNumFrom, invoiceLineNumTo,
                        taxRate,
                        taxAmountFrom,
                        taxAmountTo,
                        applyDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(applyDateFrom),
                        applyDateTo  == null ? null : TypeConversionUtils.getEndTimeForDayYYMMDD(applyDateTo),
                        applicant,
                        documentStatus,
                        costLineNumberFrom,
                        costLineNumberTo,
                        costType,
                        costAmountFrom,
                        costAmountTo,
                        installmentDeduction,
                        page);
                return invoiceLineDistDTOPage.getRecords();
            }

            @Override
            public InvoiceLineDistDTO toDTO(InvoiceLineDistDTO invoiceLineDistDTO) {
                invoiceLineDistDTO.setStringInvoiceDate(DateUtil.ZonedDateTimeToString(invoiceLineDistDTO.getInvoiceDate()));
                invoiceLineDistDTO.setStringApplicationDate(DateUtil.ZonedDateTimeToString(invoiceLineDistDTO.getApplicationDate()));
                return invoiceLineDistDTO;
            }

            @Override
            public Class<InvoiceLineDistDTO> getEntityClass() {
                return InvoiceLineDistDTO.class;
            }
        },threadNumber,request,response);
    }

    /**
     *  根据条件查询所有发票头行
     * @param createdBy 创建人
     * @param invoiceCode 发票代码
     * @param invoiceNo 发票号码
     * @param invoiceDateFrom 开票日期从
     * @param invoiceDateTo 开票日期至
     * @param salerName 销方名称
     * @param currencyCode 币种
     * @param page
     * @param size
     * @return
     */

    @GetMapping("/query/invoice/all/by/cond")
    @ApiOperation(value = "根据条件查询所有发票头行", notes = "根据条件查询所有发票头行 开发:xue.han")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity pageInvoiceByCond(@ApiParam(value = "创建人") @RequestParam(value = "createdBy",required = false) Long createdBy,
                                        @ApiParam(value = "发票代码") @RequestParam(value = "invoiceCode",required = false) String invoiceCode,
                                        @ApiParam(value = "发票号码") @RequestParam(value = "invoiceNo",required = false) String invoiceNo,
                                        @ApiParam(value = "开票日期从") @RequestParam(value = "invoiceDateFrom",required = false)String invoiceDateFrom,
                                        @ApiParam(value = "开票日期至") @RequestParam(value = "invoiceDateTo",required = false)String invoiceDateTo,
                                        @ApiParam(value = "销方名称") @RequestParam(value = "",required = false) String salerName,
                                        @ApiParam(value = "币种") @RequestParam(value = "currencyCode",required = false) String currencyCode,
                                        @ApiParam(value = "当前页") @RequestParam(value = "page",defaultValue = "0") int page,
                                        @ApiParam(value = "每页多少条") @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<InvoiceHead> invoiceDTOS = invoiceHeadService.pageInvoiceByCond(createdBy,invoiceCode,invoiceNo,invoiceDateFrom,invoiceDateTo,currencyCode,salerName,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(invoiceDTOS,httpHeaders, HttpStatus.OK);
    }


    @PostMapping("/check/invoice")
    @ApiOperation(value = "校验发票头、行", notes = "校验发票头、行 开发:xue.han")
    public ResponseEntity<InvoiceDTO> checkInvoice(@ApiParam(value = "发票信息") @RequestBody InvoiceDTO invoiceDTO){
        return ResponseEntity.ok(invoiceHeadService.checkInvoice(invoiceDTO));
    }

    @ApiOperation(value = "发票验真", notes = "发票验真 开发:xue.han")
    @PostMapping("/check/verification")
    public ResponseEntity<ExpenseReportInvoiceMatchResultDTO> invoiceVerification(@ApiParam(value = "发票信息") @RequestBody InvoiceDTO invoiceDTO){
        return ResponseEntity.ok(invoiceHeadService.invoiceVerificationAndCheckCodeAndNumber(invoiceDTO));
    }

    /**
     * invoiceCheck : 发票查验（发票验真+发票保存+返回发票详细信息）
     *
     * @param invoiceDTO 发票信息
     */
    @ApiOperation(value = "发票查验（发票验真+发票保存+返回发票详细信息）", notes = "发票查验（发票验真+发票保存+返回发票详细信息） 开发:xudong.zhao")
    @PostMapping("/invoicecheck/returndetail")
    public ResponseEntity<InvoiceDTO> invoiceCheckReturnDetail(@ApiParam(value = "发票信息") @RequestBody InvoiceDTO invoiceDTO){
        return ResponseEntity.ok(invoiceHeadService.invoiceCheckReturnDetail(invoiceDTO));
    }

    /**
     * 根据报账单行id 查询发票头行信息
     * @param id
     * @return
     */
    @GetMapping("/query/by/reportLineId/{id}")
    @ApiOperation(value = " 根据报账单行id 查询发票头行信息", notes = " 根据报账单行id 查询发票头行信息 开发:张卓")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByReportLineId(@ApiParam(value = "报账单行id") @PathVariable Long id){
        return ResponseEntity.ok(invoiceHeadService.getInvoicesByReportLineId(id));
    }
}
