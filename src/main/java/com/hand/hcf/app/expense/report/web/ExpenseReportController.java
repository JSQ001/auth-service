package com.hand.hcf.app.expense.report.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.expense.common.dto.BudgetCheckResultDTO;
import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.report.domain.ExpenseReportLine;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.dto.*;
import com.hand.hcf.app.expense.report.service.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @apiDefine ExpenseReport 报账单
 */
/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/5 14:45
 * @remark
 */
@Api(tags = "报账单")
@RestController
@RequestMapping("/api/expense/report")
public class ExpenseReportController {

    @Autowired
    private ExpenseReportHeaderService expenseReportHeaderService;

    @Autowired
    private ExpenseReportLineService expenseReportLineService;

    @Autowired
    private ExpenseReportDistService expenseReportDistService;

    @Autowired
    private ExpenseReportTaxDistService expenseReportTaxDistService;

    @Autowired
    private ExpenseReportPaymentScheduleService expenseReportPaymentScheduleService;





    /**
     * queryExpenseReportPaymentScheduleByIds : 查询报账单 付款信息表 -根据ID集合查询---付款申请单财务查询使用
     */
    @GetMapping("/payment/schedule/query/ids")
    @ApiOperation(value = "分页查询报账单", notes = "根据报账单ID集合分页查询报账单信息 开发:赵旭东")
    public ResponseEntity<List<ExpenseReportPaymentScheduleDTO>> queryExpenseReportPaymentScheduleByIds(@ApiParam(value = "报账单ids") @RequestParam(required = false) String ids,
                                                                                                        @ApiParam(value = "amount") @RequestParam(required = false) Long amount,
                                                                                                        @ApiParam(value = "页") @RequestParam(defaultValue = PageUtil.DEFAULT_PAGE) int page,
                                                                                                        @ApiParam(value = "页大小") @RequestParam(defaultValue = PageUtil.DEFAULT_SIZE) int size){


        Page mybatisPage = PageUtil.getPage(page, size);

        HttpHeaders totalHeader = PageUtil.getTotalHeader(mybatisPage);
        List<ExpenseReportPaymentScheduleDTO> list  = expenseReportPaymentScheduleService.queryExpenseReportPaymentScheduleByIds(ids, amount, mybatisPage);

        return new ResponseEntity(list,totalHeader,HttpStatus.OK);
    }


    /**
     * queryExpenseReportLineByids : 查询报账单行表信息，根据id集合查询
     *
     */
    @ApiOperation(value = "分页查询报账单行表信息", notes = "根据报账单ID集合分页查询报账单行表信息 开发:赵旭东")
    @GetMapping("/line/query/ids")
    public ResponseEntity<List<ExpenseReportLine>> queryExpenseReportLineByids(@ApiParam(value = "报账单ids") @RequestParam(required = false) String ids,
                                                                               @ApiParam(value = "费用类型") @RequestParam(required = false) Long expenseTypeId,
                                                                               @ApiParam(value = "时间从") @RequestParam(required = false) String reportLineFrom,
                                                                               @ApiParam(value = "时间至") @RequestParam(required = false) String reportLineTo,
                                                                               @ApiParam(value = "页") @RequestParam(defaultValue = PageUtil.DEFAULT_PAGE) int page,
                                                                               @ApiParam(value = "页大小") @RequestParam(defaultValue = PageUtil.DEFAULT_SIZE) int size){

        Page mybatisPage = PageUtil.getPage(page, size);

        List<ExpenseReportLine> list = expenseReportLineService.queryExpenseReportLineByids(ids, expenseTypeId, reportLineFrom, reportLineTo, mybatisPage);

        HttpHeaders totalHeader = PageUtil.getTotalHeader(mybatisPage);

        return new ResponseEntity(list,totalHeader,HttpStatus.OK);
    }



    @PostMapping("/header/save")
    @ApiOperation(value = "分页查询报账单行表信息", notes = "根据报账单ID集合分页查询报账单行表信息 开发:赵旭东")
    public ResponseEntity<ExpenseReportHeader> saveExpenseReportHeader(@ApiParam(value = "报账单头") @RequestBody @Valid ExpenseReportHeaderDTO expenseReportHeaderDTO){
        return ResponseEntity.ok(expenseReportHeaderService.saveExpenseReportHeader(expenseReportHeaderDTO,null,false));
    }

    /**
     * 我的报账单
     * @param documentTypeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param applicantId
     * @param status
     * @param currencyCode
     * @param amountFrom
     * @param amountTo
     * @param remark
     * @param editor 默认为false，true时可以查询编辑中的数据
     * @param pageable
     * @return
     */

    @ApiOperation(value = "报账单头查询接口-分页查询", notes = "报账单头查询接口-分页查询", tags = {"query"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/header/my")
    public ResponseEntity<List<ExpenseReportHeader>> getMyExpenseReports(@ApiParam(value = "单据头类型id") @RequestParam(required = false) Long documentTypeId,
                                                                         @ApiParam(value = "申请日期从") @RequestParam(required = false) String requisitionDateFrom,
                                                                         @ApiParam(value = "申请日期至") @RequestParam(required = false) String requisitionDateTo,
                                                                         @ApiParam(value = "申请人id") @RequestParam(required = false) Long applicantId,
                                                                         @ApiParam(value = "需求方id") @RequestParam(required = false) Long demanderId,
                                                                         @ApiParam(value = "状态") @RequestParam(required = false) Integer status,
                                                                         @ApiParam(value = "OU") @RequestParam(required = false) Long companyId,
                                                                         @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal amountFrom,
                                                                         @ApiParam(value = "金额至") @RequestParam(required = false) BigDecimal amountTo,
                                                                         @ApiParam(value = "摘要") @RequestParam(required = false) String remark,
                                                                         @ApiParam(value = "单据号") @RequestParam(required = false) String requisitionNumber,
                                                                         @ApiParam(value = "编辑中标识，为true时查询1001，1003，1005,2001的单据") @RequestParam(required = false,defaultValue = "false") Boolean editor,
                                                                         @ApiParam(value = "通过标识，为true时查询1002和1004的单据")  @RequestParam(required = false,defaultValue = "false") Boolean passed,
                                                                         @ApiIgnore Pageable pageable){
        Page page =PageUtil.getPage(pageable);
        ZonedDateTime reqDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom);
        ZonedDateTime reqDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo);
        List<ExpenseReportHeader> myExpenseReports = expenseReportHeaderService.getMyExpenseReports(documentTypeId,
                reqDateFrom,
                reqDateTo,
                applicantId,
                demanderId,
                status,
                companyId,
                amountFrom,
                amountTo,
                remark,
                requisitionNumber,
                editor,
                passed,
                page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(myExpenseReports,totalHeader,HttpStatus.OK);
    }

    /**
     * 获取报账单头明细信息
     * @param expenseReportId
     * @return
     */

    @GetMapping("/header/by/id")
    @ApiOperation(value = "根据报账单头ID获取报账单头信息", notes = "根据报账单头ID获取报账单头信息 开发:赵旭东")
    public ResponseEntity<ExpenseReportHeaderDTO> getExpenseReportById(@ApiParam(value = "报账单头ID") @RequestParam Long expenseReportId){
        return ResponseEntity.ok(expenseReportHeaderService.getExpenseReportById(expenseReportId));
    }

    /**
     * 删除报账单
     * @param id
     */

    @DeleteMapping("/header/delete/{id}")
    @ApiOperation(value = "删除报账单", notes = "删除报账单 开发:张开")
    public void deleteExpenseReportHeader(@PathVariable Long id){
        expenseReportHeaderService.deleteExpenseReportHeaderById(id);
    }

    /**
     * 保存费用行信息
     */

    @PostMapping("/line/save")
    @ApiOperation(value = "保存费用行", notes = "保存费用行 开发:张开")
    public ResponseEntity<ExpenseReportLineDTO> saveExpenseReportLine(@ApiParam(value = "费用行") @RequestBody @Valid ExpenseReportLineDTO dto){
        return ResponseEntity.ok(expenseReportLineService.saveExpenseReportLine(dto,true));
    }

    /**
     * 保存报账单行信息
     */

    @PostMapping("/line/report/save")
    @ApiOperation(value = "保存报账单行", notes = "保存报账单行")
    public ResponseEntity<ExpenseReportLineDTO> saveExpenseReportLineNew(@ApiParam(value = "报账单行") @RequestBody @Valid ExpenseReportLineDTO dto){
        return ResponseEntity.ok(expenseReportLineService.saveExpenseReportLineNew(dto,true));
    }

    /**
     * 根据账本自动生成费用行
     * @param headerId
     * @param expenseBookIds
     */
    /**
     * @api {POST} /api/expense/report/line/create/from/book 【报账单】导入费用
     * @apiDescription 根据账本信息自动生成费用相关信息
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} headerId 报账单ID
     * @apiParam (请求body){List(Long)} expenseBookIds 账本ID集合
     */
    @PostMapping("/line/create/from/book")
    @ApiOperation(value = "根据账本自动生成费用行", notes = "根据账本自动生成费用行 开发:张开")
    public void saveExpenseReportLineFromBook(@ApiParam(value = "头ID") @RequestParam Long headerId,
                                              @ApiParam(value = "账本ID") @RequestBody List<Long> expenseBookIds){
        expenseReportLineService.saveExpenseReportLineFromBook(headerId,expenseBookIds);
    }


    @GetMapping("/line/query/by/headerId")
    @ApiOperation(value = "查询报账单下的费用行信息", notes = "查询报账单下的费用行信息 开发:张开")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseReportLine>> getExpenseReportLinesByHeaderId(@ApiParam(value = "报账单头ID") @RequestParam Long reportHeaderId,
                                                                                   @ApiIgnore Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportLine> expenseReportLinesByHeaderId = expenseReportLineService.getExpenseReportLinesByHeaderId(reportHeaderId, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(expenseReportLinesByHeaderId,totalHeader,HttpStatus.OK);
    }


    @GetMapping("/line/query/by/id")
    @ApiOperation(value = "费用行明细查询费用行明细，由于分摊行需要分页，所以需要单独调用链接", notes = "费用行明细查询费用行明细，由于分摊行需要分页，所以需要单独调用链接 开发:张开")
    public ResponseEntity<ExpenseReportLine> getExpenseReportLineById(@ApiParam(value = "ID") @RequestParam(value = "id") Long id){
        return ResponseEntity.ok(expenseReportLineService.getExpenseReportLineById(id));
    }

    /**
     * @api {GET} /api/expense/report/line/delete/invoice 【报账单】删除费用行发票行
     * @apiDescription 删除费用行发票行
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} lineId 费用行ID
     * @apiParam (请求参数){Long} invoiceLineId 发票行ID
     */
    @DeleteMapping("/line/delete/invoice")
    @ApiOperation(value = "【报账单】删除费用行发票行", notes = "【报账单】删除费用行发票行 开发:张开")
    public void deleteInvoiceByInvoiceLineId(@ApiParam(value = "费用行ID") @RequestParam(value = "lineId") Long lineId,
                                             @ApiParam(value = "发票行ID") @RequestParam(value = "invoiceLineId") Long invoiceLineId){
        expenseReportLineService.deleteInvoiceByInvoiceLineId(invoiceLineId,lineId);
    }


    @GetMapping("/dist/query/by/lineId")
    @ApiOperation(value = "根据费用行ID查询分摊行", notes = "根据费用行ID查询分摊行 开发:张开")
    public ResponseEntity<List<ExpenseReportDistDTO>> getExpenseReportDistByLineId(@ApiParam(value = "费用行ID") @RequestParam(value = "lineId") Long lineId){
        List<ExpenseReportDistDTO> expenseReportDistByLineId = expenseReportDistService.getExpenseReportDistDTOByLineId(lineId);
        return ResponseEntity.ok(expenseReportDistByLineId);
    }

    /**
     * 删除费用行
     * @param id
     */

    @DeleteMapping("/line/delete/{id}")
    @ApiOperation(value = "【报账单】删除费用行信息", notes = "【报账单】删除费用行信息 开发:张开")
    public void deleteExpenseReportLine(@PathVariable Long id){
        expenseReportLineService.deleteExpenseReportLineById(id);
    }

    /**
     * 创建修改计划付款行
     *
     * @param expensePaymentScheduleDTO
     * @return
     */

    @PostMapping("/payment/schedule/save")
    @ApiOperation(value = "创建修改计划付款行", notes = "创建修改计划付款行 开发:张开")
    public ResponseEntity<ExpenseReportPaymentScheduleDTO> createOrUpdatePaymentSchedule(@ApiParam(value = "费用报账支付计划") @Valid @RequestBody ExpenseReportPaymentScheduleDTO expensePaymentScheduleDTO) {
        return ResponseEntity.ok(expenseReportPaymentScheduleService.createOrUpdatePaymentSchedule(expensePaymentScheduleDTO));
    }

    /**
     * 根据ID查询报账单付款计划行
     *
     * @param id 计划付款行ID
     * @return
     */

    @GetMapping("/payment/schedule/{id}")
    @ApiOperation(value = "计划付款行ID", notes = "计划付款行ID 开发:张开")
    public ResponseEntity<ExpenseReportPaymentScheduleDTO> getExpensePaymentScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseReportPaymentScheduleService.getExpensePaymentScheduleById(id));
    }


    @GetMapping("/payment/schedule/query")
    @ApiOperation(value = "分页查询一个报帐单头下的付款计划行", notes = "分页查询一个报帐单头下的付款计划行 开发:张开")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseReportPaymentScheduleDTO>> getExpensePaymentSchedule(@ApiParam(value = "报账单头ID") @RequestParam(value = "reportHeaderId") Long reportHeaderId,
                                                                                           @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportPaymentScheduleDTO> expensePaymentScheduleByCond = expenseReportPaymentScheduleService.getExpensePaymentScheduleByCond(reportHeaderId, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(expensePaymentScheduleByCond,totalHeader,HttpStatus.OK);
    }


    @DeleteMapping("/payment/schedule/delete/{id}")
    @ApiOperation(value = "删除计划付款行", notes = "删除计划付款行 开发:张开")
    public void deleteExpenseReportPaymentSchedule(@PathVariable Long id){
        expenseReportPaymentScheduleService.deleteExpenseReportPaymentSchedule(id);
    }

    /**
     * 根据费用申请单编号查找报账单分摊行信息
     */
    @GetMapping("/getDistfromApplication")
    @ApiOperation(value = "根据费用申请单编号查找报账单分摊行信息", notes = "根据费用申请单编号查找报账单分摊行信息 开发:张开")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity getExpenseReportDistFromApplication(@ApiParam(value = "文档编号") @RequestParam(required = true,value = "documentNumber")String documentNumber,
                                                              @ApiParam(value = "报账编号") @RequestParam(required = false,value ="reportNumber")String reportDocumentNumber,
                                                              @ApiParam(value = "公司ID") @RequestParam(required = false,value = "companyId")Long companyId,
                                                              @ApiParam(value = "部门ID") @RequestParam(required = false,value = "unitId")Long unitId,
                                                              @ApiIgnore Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportDistDTO> list = expenseReportDistService.queryExpenseReportDistFromApplication(page, documentNumber, reportDocumentNumber, companyId, unitId);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);

        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }

    @ApiOperation(value = "提交报账单时，校验费用政策", notes = "提交报账单时，根据报账单头ID校验费用政策 开发:韩雪")
    @GetMapping("/submit/check/policy")
    public ResponseEntity checkPolicy(@ApiParam(value = "报账单头ID") @RequestParam("id") Long id) {

        return ResponseEntity.ok(expenseReportHeaderService.checkPolicy(id));
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ApiOperation(value = "【报账单】提交工作流", notes = "【报账单】提交工作流息 开发:张开")
    public ResponseEntity<BudgetCheckResultDTO> submit(@ApiParam(value = "工作流单据参数") @RequestBody WorkFlowDocumentRefCO workFlowDocumentRef,
                                                       @ApiParam(value = "是否忽略警告") @RequestParam(value = "ignoreWarningFlag", required = false) Boolean ignoreWarningFlag) {
        return ResponseEntity.ok(expenseReportHeaderService.submit(workFlowDocumentRef,ignoreWarningFlag));
    }


    @PostMapping(value = "/create/accounting")
    @ApiOperation(value = "创建凭证", notes = "创建凭证 开发:张开")
    public ResponseEntity saveInitializeExpReportGeneralLedgerJournalLine(@ApiParam(value = "报账单头ID") @RequestParam("reportHeaderId") Long reportHeaderId,
                                                                          @ApiParam(value = "财务日期") @RequestParam("accountingDate") String accountingDate){
        String reuslt = expenseReportHeaderService.saveInitializeExpReportGeneralLedgerJournalLine(reportHeaderId,accountingDate);
        return ResponseEntity.ok(reuslt);
    }

    @GetMapping("/get/expenseReport/by/query")
    @ApiOperation(value = "报账单财务查询", notes = "报账单财务查询 开发:hao.yi")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseReportHeaderDTO>> MyExpenseReportsFinanceQuery(
                                                                                    @ApiParam(value = "公司ID") @RequestParam(required = false)Long companyId,
                                                                                    @ApiParam(value = "账套ID")@RequestParam(required = false)Long setOfBooksId,
                                                                                    @ApiParam(value = "报账单类型ID") @RequestParam(required = false) Long documentTypeId,
                                                                                    @ApiParam(value = "申请人ID") @RequestParam(required = false,value = "applyId") Long applicantId,
                                                                                    @ApiParam(value = "状态") @RequestParam(required = false) Integer status,
                                                                                    @ApiParam(value = "部门ID") @RequestParam(required = false)Long unitId,
                                                                                    @ApiParam(value = "申请日期从") @RequestParam(required = false,value = "applyDateFrom") String requisitionDateFrom,
                                                                                    @ApiParam(value = "申请日期至") @RequestParam(required = false,value = "applyDateTo") String requisitionDateTo,
                                                                                    @ApiParam(value = "币种") @RequestParam(required = false,value = "currency") String currencyCode,
                                                                                    @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal amountFrom,
                                                                                    @ApiParam(value = "金额至") @RequestParam(required = false) BigDecimal amountTo,
                                                                                    @ApiParam(value = "已付金额从") @RequestParam(required = false) BigDecimal paidAmountFrom,
                                                                                    @ApiParam(value = "已付金额至") @RequestParam(required = false) BigDecimal paidAmountTo,
                                                                                    @ApiParam(value = "反冲标志") @RequestParam(required = false) String backlashFlag,
                                                                                    @ApiParam(value = "检查日期从") @RequestParam(required = false,value = "checkDateFrom") String checkDateFrom,
                                                                                    @ApiParam(value = "检查日期至") @RequestParam(required = false,value = "checkDateTo") String checkDateTo,
                                                                                    @ApiParam(value = "备注") @RequestParam(required = false) String remark,
                                                                                    @ApiParam(value = "申请号码") @RequestParam(required = false,value = "documentCode") String requisitionNumber,
                                                                                    @ApiParam(value = "租户ID") @RequestParam(required = false)Long tenantId,
                                                                                    @ApiIgnore Pageable pageable){
        Page page =PageUtil.getPage(pageable);
        ZonedDateTime reqDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom);
        ZonedDateTime reqDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo);
        ZonedDateTime cDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(checkDateFrom);
        ZonedDateTime cDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(checkDateTo);

        //首先在费用模块根据条件全部查询出来， 然后再将符合条件的单据编号单据id输出至支付模块， 再信息返回回来。然后在进行比对筛选
        List<ExpenseReportHeaderDTO> list =expenseReportHeaderService.queryExpenseReportsFinance(
                companyId,
                null,
                documentTypeId,
                reqDateFrom,
                reqDateTo,
                applicantId,
                status,
                currencyCode,
                amountFrom,
                amountTo,
                remark,
                requisitionNumber,
                unitId,
                paidAmountFrom,
                paidAmountTo,
                cDateFrom,
                cDateTo,
                backlashFlag,
                tenantId,
                false,
                page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(list,totalHeader,HttpStatus.OK);
    }

    @GetMapping("/get/expenseReport/by/query/enable/dataAuth")
    @ApiOperation(value = "报账单财务查询 (数据权限控制)", notes = "报账单财务查询 (数据权限控制) 开发:hao.yi")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseReportHeaderDTO>> MyExpenseReportsFinanceQueryDataAuth(
            @ApiParam(value = "公司ID") @RequestParam(required = false)Long companyId,
            @ApiParam(value = "账套ID")@RequestParam(required = false)Long setOfBooksId,
            @ApiParam(value = "报账单类型ID")@RequestParam(required = false) Long documentTypeId,
            @ApiParam(value = "申请人ID") @RequestParam(required = false,value = "applyId") Long applicantId,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status,
            @ApiParam(value = "部门ID") @RequestParam(required = false)Long unitId,
            @ApiParam(value = "申请日期从") @RequestParam(required = false,value = "applyDateFrom") String requisitionDateFrom,
            @ApiParam(value = "申请日期至") @RequestParam(required = false,value = "applyDateTo") String requisitionDateTo,
            @ApiParam(value = "币种") @RequestParam(required = false,value = "currency") String currencyCode,
            @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal amountFrom,
            @ApiParam(value = "金额至") @RequestParam(required = false) BigDecimal amountTo,
            @ApiParam(value = "已付金额从") @RequestParam(required = false) BigDecimal paidAmountFrom,
            @ApiParam(value = "已付金额至") @RequestParam(required = false) BigDecimal paidAmountTo,
            @ApiParam(value = "反冲标志") @RequestParam(required = false) String backlashFlag,
            @ApiParam(value = "检查日期从") @RequestParam(required = false,value = "checkDateFrom") String checkDateFrom,
            @ApiParam(value = "检查日期至") @RequestParam(required = false,value = "checkDateTo") String checkDateTo,
            @ApiParam(value = "备注") @RequestParam(required = false) String remark,
            @ApiParam(value = "申请号码") @RequestParam(required = false,value = "documentCode") String requisitionNumber,
            @ApiParam(value = "租户ID") @RequestParam(required = false)Long tenantId,
            @ApiIgnore Pageable pageable){
        Page page =PageUtil.getPage(pageable);
        ZonedDateTime reqDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom);
        ZonedDateTime reqDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo);
        ZonedDateTime cDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(checkDateFrom);
        ZonedDateTime cDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(checkDateTo);

        //首先在费用模块根据条件全部查询出来， 然后再将符合条件的单据编号单据id输出至支付模块， 再信息返回回来。然后在进行比对筛选
        List<ExpenseReportHeaderDTO> list =expenseReportHeaderService.queryExpenseReportsFinance(
                companyId,
                setOfBooksId,
                documentTypeId,
                reqDateFrom,
                reqDateTo,
                applicantId,
                status,
                currencyCode,
                amountFrom,
                amountTo,
                remark,
                requisitionNumber,
                unitId,
                paidAmountFrom,
                paidAmountTo,
                cDateFrom,
                cDateTo,
                backlashFlag,
                tenantId,
                true,
                page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(list,totalHeader,HttpStatus.OK);
    }
    /**
     * 报账单财务查询导出功能
     */
   @RequestMapping("/export")
   @ApiOperation(value = "报账单财务查询导出功能", notes = "报账单财务查询导出功能 开发:hao.yi")
   @ApiImplicitParams({
           @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
           @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
   })
    public  void MyExpenseReportsFinanceExport(@ApiParam(value = "公司ID") @RequestParam(required = false)Long companyId,
                                               @ApiParam(value = "报账单类型ID")@RequestParam(required = false) Long documentTypeId,
                                               @ApiParam(value = "申请人ID") @RequestParam(required = false,value = "applyId") Long applicantId,
                                               @ApiParam(value = "状态") @RequestParam(required = false) Integer status,
                                               @ApiParam(value = "部门ID") @RequestParam(required = false)Long unitId,
                                               @ApiParam(value = "申请日期从") @RequestParam(required = false,value = "applyDateFrom") String requisitionDateFrom,
                                               @ApiParam(value = "申请日期至") @RequestParam(required = false,value = "applyDateTo") String requisitionDateTo,
                                               @ApiParam(value = "币种") @RequestParam(required = false,value = "currency") String currencyCode,
                                               @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal amountFrom,
                                               @ApiParam(value = "金额至") @RequestParam(required = false) BigDecimal amountTo,
                                               @ApiParam(value = "已付金额从") @RequestParam(required = false) BigDecimal paidAmountFrom,
                                               @ApiParam(value = "已付金额至") @RequestParam(required = false) BigDecimal paidAmountTo,
                                               @ApiParam(value = "反冲标志") @RequestParam(required = false) String backlashFlag,
                                               @ApiParam(value = "检查日期从") @RequestParam(required = false,value = "checkDateFrom") String checkDateFrom,
                                               @ApiParam(value = "检查日期至") @RequestParam(required = false,value = "checkDateTo") String checkDateTo,
                                               @ApiParam(value = "备注") @RequestParam(required = false) String remark,
                                               @ApiParam(value = "申请号码") @RequestParam(required = false,value = "documentCode") String requisitionNumber,
                                               @ApiParam(value = "租户ID") @RequestParam(required = false)Long tenantId,
                                               @ApiIgnore Pageable pageable,
                                               @ApiParam(value = "导出配置")  @RequestBody ExportConfig exportConfig,
                                               HttpServletResponse response,
                                               HttpServletRequest request) throws IOException {

        ZonedDateTime reqDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom);
        ZonedDateTime reqDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo);
        ZonedDateTime cDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(checkDateFrom);
        ZonedDateTime cDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(checkDateTo);
        expenseReportHeaderService.exportFormExcel(companyId,
                                                    documentTypeId,
                                                    reqDateFrom,
                                                    reqDateTo,
                                                    applicantId,
                                                    status,
                                                    currencyCode,
                                                    amountFrom,
                                                    amountTo,
                                                    remark,
                                                    requisitionNumber,
                                                    unitId,
                                                    paidAmountFrom,
                                                    paidAmountTo,
                                                    cDateFrom,
                                                    cDateTo,
                                                    backlashFlag,
                                                    tenantId,
                                                    response,
                                                    request,
                                                    exportConfig);
    }

    @GetMapping("/header/emailReports")
    @ApiOperation(value = "分页查询可邮寄报账单", notes = "分页查询审核通过未比对通过的报账单 开发:张卓")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseReportHeader>> getEmailExpenseReports(@ApiParam(value = "单据类型ID") @RequestParam(required = false) Long documentTypeId,
                                                                            @ApiParam(value = "审核日期从") @RequestParam(required = false) String auditDateFrom,
                                                                            @ApiParam(value = "审核日期到") @RequestParam(required = false) String auditDateTo,
                                                                            @ApiParam(value = "单据提交人ID") @RequestParam(required = false) Long applicantId,
                                                                            @ApiParam(value = "单据状态") @RequestParam(required = false) Integer status,
                                                                            @ApiParam(value = "币种") @RequestParam(required = false) String currencyCode,
                                                                            @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal amountFrom,
                                                                            @ApiParam(value = "金额到") @RequestParam(required = false) BigDecimal amountTo,
                                                                            @ApiParam(value = "备注") @RequestParam(required = false) String remark,
                                                                            @ApiParam(value = "单据号") @RequestParam(required = false) String requisitionNumber,
                                                                            @ApiIgnore Pageable pageable){
        Page page =PageUtil.getPage(pageable);
        ZonedDateTime reqAuditDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(auditDateFrom);
        ZonedDateTime reqAuditDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(auditDateTo);
        List<ExpenseReportHeader> myExpenseReports = expenseReportHeaderService.getEmailExpenseReports(documentTypeId,
                reqAuditDateFrom,
                reqAuditDateTo,
                applicantId,
                status,
                currencyCode,
                amountFrom,
                amountTo,
                remark,
                requisitionNumber,
                page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(myExpenseReports,totalHeader,HttpStatus.OK);
    }

    @PostMapping("/header/emailReports/save/invoiceBagNo")
    @ApiOperation(value = "保存发票费用关联关系的发票袋号码", notes = "保存发票费用关联关系的发票袋号码 开发:张卓")
    public void saveInvoiceLineExpenceInvoiceBagNo(@ApiParam(value = "报销单信息") @RequestBody List<Long> expenseReportHeaderIdList,
                                                   @ApiParam(value = "发票袋号码") @RequestParam String invoiceBagNo){
        expenseReportHeaderService.saveInvoiceLineExpenceInvoiceBagNo(expenseReportHeaderIdList,invoiceBagNo);
    }

    @PostMapping("/header/emailReports/confirm")
    @ApiOperation(value = "根据报销单头确认报销单邮寄", notes = "根据报销单头确认报销单邮寄 开发:张卓")
    public void confirmInvoiceLineExpenceEmail(@ApiParam(value = "报销单信息") @RequestBody List<Long> expenseReportHeaderIdList){
        expenseReportHeaderService.confirmInvoiceLineExpenceEmail(expenseReportHeaderIdList);
    }

    /**
     * 根据账本取当前用户有权限创建，且有相应费用类型的报账单类型
     * @param expenseBookIds
     * @return
     */
    @ApiOperation(value = "根据账本获取报账单类型", notes = "根据账本获取当前用户有权限创建，且有相应费用类型的报账单类型 开发:kai.zhang")
    @PostMapping("/own/condition/expenseBook")
    public ResponseEntity<List<ExpenseReportType>> getExpenseReportTypeByExpenseBooks(@ApiParam("账本ID集合") @RequestBody List<Long> expenseBookIds){
        return ResponseEntity.ok(expenseReportHeaderService.getExpenseReportTypeByExpenseBooks(expenseBookIds));
    }

    /**
     * 自动生成报账单
     * @param expenseReportAutoCreateDTO
     * @return
     */
    @PostMapping("/header/auto/create")
    @ApiOperation(value = "自动生成报账单", notes = "根据费用类型以及账本信息或者发票信息，自动生成报账单 开发:kai.zhang")
    public ResponseEntity<Long> autoCreateExpenseReport(@ApiParam(value = "自动生成报账单所需信息") @RequestBody @Valid ExpenseReportAutoCreateDTO expenseReportAutoCreateDTO){
        return ResponseEntity.ok(expenseReportHeaderService.autoCreateExpenseReport(expenseReportAutoCreateDTO));
    }

    /**
     * 自动生成报账本
     * @param invoiceDTOS
     * @return
     */
    @PostMapping("/books/auto/create")
    @ApiOperation(value = "自动生成报账本", notes = "根据发票信息，自动生成账本 开发:kai.zhang")
    public ResponseEntity<ExpenseReportInvoiceMatchResultDTO> createExpenseBookByInvoice(@ApiParam(value = "自动生成账本所需信息") @RequestBody List<InvoiceDTO> invoiceDTOS){
        return ResponseEntity.ok(expenseReportHeaderService.createExpenseBookByInvoice(invoiceDTOS,false));
    }

    /**
     * 发票自动匹配费用类型
     * @param invoiceDTOS
     * @return
     */
    @PostMapping("/invoice/auto/match")
    @ApiOperation(value = "发票自动匹配费用类型", notes = "根据发票信息，自动匹配费用类型 开发:kai.zhang")
    public ResponseEntity<ExpenseReportInvoiceMatchResultDTO> invoiceAutoMatchExpense(@ApiParam(value = "发票信息")@RequestBody List<InvoiceDTO> invoiceDTOS){
        return ResponseEntity.ok(expenseReportHeaderService.invoiceAutoMatchExpense(invoiceDTOS));
    }

    /**
     * 根据发票对应的费用类型，获取相应的报账单类型
     * @param invoiceDTOS
     * @return
     */
    @PostMapping("/own/condition/invoice")
    @ApiOperation(value = "根据发票获取报账单类型", notes = "根据发票获取当前用户有权限创建，且有相应费用类型的报账单类型 开发:kai.zhang")
    public ResponseEntity<List<ExpenseReportType>> getExpenseReportTypeByInvoices(@ApiParam("发票信息") @RequestBody List<InvoiceDTO> invoiceDTOS){
        return ResponseEntity.ok(expenseReportHeaderService.getExpenseReportTypeByInvoices(invoiceDTOS));
    }



    @GetMapping("/header/signReports")
    @ApiOperation(value = "分页查询已扫描过发票袋号码的报账单", notes = "分页查询已扫描过发票袋号码的报账单 开发:张卓")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<ExpenseReportHeader>> getSignExpenseReports(@ApiParam(value = "单据类型ID") @RequestParam(required = false) Long documentTypeId,
                                                                            @ApiParam(value = "单据提交人ID") @RequestParam(required = false) Long applicantId,
                                                                            @ApiParam(value = "单据状态") @RequestParam(required = false) Integer status,
                                                                            @ApiParam(value = "币种") @RequestParam(required = false) String currencyCode,
                                                                            @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal amountFrom,
                                                                            @ApiParam(value = "金额到") @RequestParam(required = false) BigDecimal amountTo,
                                                                            @ApiParam(value = "备注") @RequestParam(required = false) String remark,
                                                                            @ApiParam(value = "单据号") @RequestParam(required = false) String requisitionNumber,
                                                                            @ApiParam(value = "发票袋号码") @RequestParam(required = false) String invoiceBagNo,
                                                                            @ApiParam(value = "签收状态") @RequestParam(required = false) String receiptDocumentsFlag,
                                                                            @ApiParam(value = "匹配成功状态") @RequestParam(required = false) String sheerMateFlag,
                                                                            @ApiParam(value = "签收人ID") @RequestParam(required = false) Long dealUserId,
                                                                           @ApiIgnore Pageable pageable){
        Page page =PageUtil.getPage(pageable);
//        ZonedDateTime reqAuditDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(auditDateFrom);
//        ZonedDateTime reqAuditDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(auditDateTo);
        List<ExpenseReportHeader> myExpenseReports = expenseReportHeaderService.getSignExpenseReports(documentTypeId,
                applicantId,
                status,
                currencyCode,
                amountFrom,
                amountTo,
                remark,
                requisitionNumber,
                invoiceBagNo,
                receiptDocumentsFlag,
                sheerMateFlag,
                dealUserId,
                page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(myExpenseReports,totalHeader,HttpStatus.OK);
    }

    @PostMapping("/header/signReports/confirm")
    @ApiOperation(value = "报账单单据签收", notes = "报账单单据签收 开发:张卓")
    public void confirmSignReports(@ApiParam(value = "报销单头id集合") @RequestBody List<Long> expenseReportHeaderIdList){
        expenseReportHeaderService.expenseReportHeaderSign(expenseReportHeaderIdList);
    }

    @PostMapping("/header/comparison/confirm")
    @ApiOperation(value = "更新报账单比对结果", notes = "更新报账单比对结果 开发:张卓")
    public void confirmSignReportsComparisonFlag(@ApiParam(value = "报销单头id集合") @RequestBody List<Long> expenseReportHeaderIdList){
        expenseReportHeaderService.updateExpenseReportHeaderComparisonFlag(expenseReportHeaderIdList);
    }

    @PostMapping("/header/signReports/reject")
    @ApiOperation(value = "单据签收-单据退回", notes = "单据签收-单据退回 开发:赵立国")
    public void rejectSignReports(@ApiParam(value = "单据退回方式") @RequestBody rejectSignReportsDTO dto){
        expenseReportHeaderService
                .rejectSignReports(dto.getRejectType(),dto.getRejectReason(),dto.getExpenseReportHeaderIdList());
    }
}
