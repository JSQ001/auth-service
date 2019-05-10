package com.hand.hcf.app.expense.report.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.WorkFlowDocumentRefCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.common.dto.BudgetCheckResultDTO;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLine;
import com.hand.hcf.app.expense.report.domain.ExpenseReportDist;
import com.hand.hcf.app.expense.report.domain.ExpenseReportHeader;
import com.hand.hcf.app.expense.report.domain.ExpenseReportLine;
import com.hand.hcf.app.expense.report.dto.ExpenseReportDistDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportHeaderDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportLineDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportPaymentScheduleDTO;
import com.hand.hcf.app.expense.report.service.*;
import com.hand.hcf.app.expense.type.web.dto.ExpenseFieldDTO;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import java.util.*;

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


    /**
     * @api {POST} /api/expense/report/header/save 【报账单】创建头信息
     * @apiDescription 创建报账单头信息
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){String} [accountName] 收款方户名
     * @apiParam (请求参数){String} [accountNumber] 收款方账户
     * @apiParam (请求参数){String} [applicantCode] 申请人代码
     * @apiParam (请求参数){Long} applicantId 申请人ID
     * @apiParam (请求参数){String} [applicantName] 申请人名称
     * @apiParam (请求参数){String} [demanderCode] 需求方代码
     * @apiParam (请求参数){Long} demanderId 需求方ID
     * @apiParam (请求参数){String} [demanderName] 需求方名称
     * @apiParam (请求参数){ZonedDateTime} [auditDate] 审核日期
     * @apiParam (请求参数){String} [auditFlag] 审核标志
     * @apiParam (请求参数){String} [budgetCheckResult] 预算校验返回标志
     * @apiParam (请求参数){String} [budgetCheckResultDesc]
     * @apiParam (请求参数){String} [companyCode] 公司代码
     * @apiParam (请求参数){Long} companyId 公司ID
     * @apiParam (请求参数){String} [companyName] 公司名称
     * @apiParam (请求参数){Long} [contractHeaderId] 合同头ID
     * @apiParam (请求参数){String} [contractNumber] 合同编码
     * @apiParam (请求参数){Long} [createdBy] 创建人
     * @apiParam (请求参数){String} [createdCode] 创建人代码
     * @apiParam (请求参数){ZonedDateTime} [createdDate] 创建日期
     * @apiParam (请求参数){String} [createdName] 创建人名称
     * @apiParam (请求参数){String} currencyCode 币种
     * @apiParam (请求参数){String} areaCode 区域
     * @apiParam (请求参数){String} [budgetDepCode] 预算部门代码
     * @apiParam (请求参数){Long} budgetDepId 预算部门ID
     * @apiParam (请求参数){String} [budgetDepName] 预算部门名称
     * @apiParam (请求参数){String} [departmentCode] 受益部门代码
     * @apiParam (请求参数){Long} departmentId 受益部门ID
     * @apiParam (请求参数){String} [departmentName] 受益部门名称
     * @apiParam (请求参数){String} [description] 备注
     * @apiParam (请求参数){String} [attachmentOid] 附件OID
     * @apiParam (请求参数){String} [documentOid] 单据OID
     * @apiParam (请求参数){Long} documentTypeId 报账单类型ID
     * @apiParam (请求参数){String} [documentTypeName] 报账单类型名称
     * @apiParam (请求参数){BigDecimal} exchangeRate 汇率
     * @apiParam (请求参数){BigDecimal} [functionalAmount] 本币金额
     * @apiParam (请求参数){Long} [id] ID
     * @apiParam (请求参数){String} [payeeCategory] 收款方类型（员工：EMPLOYEE；供应商：VENDER）
     * @apiParam (请求参数){Long} [payeeId] 收款方ID
     * @apiParam (请求参数){ZonedDateTime} requisitionDate 申请日期
     * @apiParam (请求参数){String} [requisitionNumber] 报账单编码
     * @apiParam (请求参数){Long} [setOfBooksId] 账套
     * @apiParam (请求参数){Integer} [status] 状态
     * @apiParam (请求参数){Long} [tenantId] 租户
     * @apiParam (请求参数){BigDecimal} [totalAmount] 总金额
     * @apiParam (请求参数){Integer} [versionNumber]  版本号
     * @apiParam (请求参数){List} [expenseDimensions] 报账单维度布局
     *
     * @apiParam (expenseDimensions) {Long} value  所选的值
     * @apiParam (expenseDimensions) {String} valueName  所选的值名称
     * @apiParam (expenseDimensions) {Boolean} name  维度名称
     * @apiParam (expenseDimensions) {String} dimensionId  维度Id
     * @apiParam (expenseDimensions) {String} dimensionFiled  字段代码
     * @apiParam (expenseDimensions) {int} sequence  序号
     * @apiParam (expenseDimensions) {Boolean} headerFlag  是否展示在单据头
     *
     * @apiSuccess (返回参数){String} [accountName] 收款方户名
     * @apiSuccess (返回参数){String} [accountNumber] 收款方账户
     * @apiSuccess (返回参数){String} [applicantCode] 申请人代码
     * @apiSuccess (返回参数){Long} applicantId 申请人ID
     * @apiSuccess (返回参数){String} [applicantName] 申请人名称
     * @apiSuccess (返回参数){String} [demanderCode] 需求方代码
     * @apiSuccess (返回参数){Long} demanderId 需求方ID
     * @apiSuccess (返回参数){String} [demanderName] 需求方名称
     * @apiSuccess (返回参数){ZonedDateTime} [auditDate] 审核日期
     * @apiSuccess (返回参数){String} [auditFlag] 审核标志
     * @apiSuccess (返回参数){String} [budgetCheckResult] 预算校验返回标志
     * @apiSuccess (返回参数){Long} companyId 公司ID
     * @apiSuccess (返回参数){Long} [contractHeaderId] 合同头ID
     * @apiSuccess (返回参数){Long} [createdBy] 创建人
     * @apiSuccess (返回参数){String} [createdCode] 创建人代码
     * @apiSuccess (返回参数){ZonedDateTime} [createdDate] 创建日期
     * @apiSuccess (返回参数){String} [createdName] 创建人名称
     * @apiSuccess (返回参数){String} currencyCode 币种
     * @apiSuccess (返回参数){String} areaCode 区域
     * @apiSuccess (返回参数){Long} budgetDepartmentId 预算部门ID
     * @apiSuccess (返回参数){Long} departmentId 受益部门ID
     * @apiSuccess (返回参数){String} [description] 备注
     * @apiSuccess (返回参数){String} [attachmentOid] 附件OID
     * @apiSuccess (返回参数){String} [documentOid] 单据OID
     * @apiSuccess (返回参数){Long} documentTypeId 报账单类型ID
     * @apiSuccess (返回参数){BigDecimal} exchangeRate 汇率
     * @apiSuccess (返回参数){BigDecimal} [functionalAmount] 本币金额
     * @apiSuccess (返回参数){Long} [id] ID
     * @apiSuccess (返回参数){String} [payeeCategory] 收款方类型（员工：EMPLOYEE；供应商：VENDER）
     * @apiSuccess (返回参数){Long} [payeeId] 收款方ID
     * @apiSuccess (返回参数){ZonedDateTime} requisitionDate 申请日期
     * @apiSuccess (返回参数){String} [requisitionNumber] 报账单编码
     * @apiSuccess (返回参数){Long} [setOfBooksId] 账套
     * @apiSuccess (返回参数){Integer} [status] 状态
     * @apiSuccess (返回参数){Long} [tenantId] 租户
     * @apiSuccess (返回参数){BigDecimal} [totalAmount] 总金额
     * @apiSuccess (返回参数){Integer} [versionNumber]  版本号
     * @apiParamExample {json} 请求参数:
        {
        "companyId": 1083751704185716737,
        "departmentId": 1105676563697147906,
        "applicantId": 1083751704496095233,
        "currencyCode": "CNY",
        "exchangeRate": 1,
        "totalAmount": 100,
        "functionalAmount": null,
        "description": "测试数据",
        "documentTypeId": 1105854700853731329,
        "requisitionDate": "2019-03-13T23:34:41.128+08:00",
        "contractHeaderId": null,
        "status": null,
        "auditFlag": null,
        "auditDate": null,
        "payeeCategory": null,
        "payeeId": null,
        "accountNumber": null,
        "accountName": null,
        "documentOid": null,
        "budgetCheckResult": null,
        "budgetCheckResultDesc": null,
        "applicantName": null,
        "applicantCode": null,
        "expenseDimensions": [
            {
            "value": 1084698307856949249,
            "dimensionId": 1084698172754223106,
            "dimensionField": "dimension2Id",
            "headerFlag": true,
            "sequence": 10,
            "requiredFlag": false
            }
        ],
        "companyName": null,
        "companyCode": null,
        "departmentName": null,
        "departmentCode": null,
        "documentTypeName": null,
        "createdName": null,
        "createdCode": null,
        "contractNumber": null
        }
     *
     * @apiSuccessExample {json} 成功返回值:
        {
        "id": "1106210262543896577",
        "createdDate": "2019-03-14T23:07:33.644+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-03-14T23:07:33.645+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "requisitionNumber": "BXGS00001140320190004",
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "companyId": "1083751704185716737",
        "departmentId": "1105676563697147906",
        "applicantId": "1083751704496095233",
        "currencyCode": "CNY",
        "exchangeRate": 1,
        "totalAmount": 0,
        "functionalAmount": 0,
        "description": "测试数据",
        "documentTypeId": "1105854700853731329",
        "requisitionDate": "2019-03-13T23:34:41.128+08:00",
        "contractHeaderId": null,
        "status": 1001,
        "auditFlag": "N",
        "auditDate": null,
        "payeeCategory": null,
        "payeeId": null,
        "accountNumber": null,
        "accountName": null,
        "documentOid": "7f5e3b6a-18cf-4692-ace5-4ce7feed1cb5",
        "budgetCheckResult": null,
        "budgetCheckResultDesc": null,
        "applicantName": null,
        "applicantCode": null
        }
     */
    @PostMapping("/header/save")
    public ResponseEntity<ExpenseReportHeader> saveExpenseReportHeader(@RequestBody @Valid ExpenseReportHeaderDTO expenseReportHeaderDTO){
        return ResponseEntity.ok(expenseReportHeaderService.saveExpenseReportHeader(expenseReportHeaderDTO));
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
    /**
     * @api {GET} /api/expense/report/header/my 【报账单】我的报账单
     * @apiDescription 我的报账单
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} [documentTypeId] 报账单类型ID
     * @apiParam (请求参数){String} [requisitionDateFrom] 申请日期从(YYYY-MM-DD格式)
     * @apiParam (请求参数){String} [requisitionDateTo] 申请日期至(YYYY-MM-DD格式)
     * @apiParam (请求参数){Long} [applicantId] 申请人ID
     * @apiParam (请求参数){Integer} [status] 状态
     * @apiParam (请求参数){String} [currencyCode] 币种
     * @apiParam (请求参数){BigDecimal} [amountFrom] 金额从
     * @apiParam (请求参数){BigDecimal} [amountTo] 金额至
     * @apiParam (请求参数){String} [remark] 备注
     * @apiParam (请求参数){int} [page] 页数
     * @apiParam (请求参数){int} [size] 每页大小
     *
     * @apiSuccess (返回参数){String} [accountName] 收款方户名
     * @apiSuccess (返回参数){String} [accountNumber] 收款方账户
     * @apiSuccess (返回参数){String} [applicantCode] 申请人代码
     * @apiSuccess (返回参数){Long} applicantId 申请人ID
     * @apiSuccess (返回参数){String} [applicantName] 申请人名称
     * @apiSuccess (返回参数){ZonedDateTime} [auditDate] 审核日期
     * @apiSuccess (返回参数){String} [auditFlag] 审核标志
     * @apiSuccess (返回参数){String} [budgetCheckResult] 预算校验返回标志
     * @apiSuccess (返回参数){String} [budgetCheckResultDesc]
     * @apiSuccess (返回参数){String} [companyCode] 公司代码
     * @apiSuccess (返回参数){Long} companyId 公司ID
     * @apiSuccess (返回参数){String} [companyName] 公司名称
     * @apiSuccess (返回参数){Long} [contractHeaderId] 合同头ID
     * @apiSuccess (返回参数){String} [contractNumber] 合同编码
     * @apiSuccess (返回参数){Long} [createdBy] 创建人
     * @apiSuccess (返回参数){String} [createdCode] 创建人代码
     * @apiSuccess (返回参数){ZonedDateTime} [createdDate] 创建日期
     * @apiSuccess (返回参数){String} [createdName] 创建人名称
     * @apiSuccess (返回参数){String} currencyCode 币种
     * @apiSuccess (返回参数){String} [departmentCode] 部门代码
     * @apiSuccess (返回参数){Long} departmentId 部门ID
     * @apiSuccess (返回参数){String} [departmentName] 部门名称
     * @apiSuccess (返回参数){String} [description] 备注
     * @apiSuccess (返回参数){String} [documentOid] 单据OID
     * @apiSuccess (返回参数){Long} documentTypeId 报账单类型ID
     * @apiSuccess (返回参数){String} [documentTypeName] 报账单类型名称
     * @apiSuccess (返回参数){BigDecimal} exchangeRate 汇率
     * @apiSuccess (返回参数){BigDecimal} [functionalAmount] 本币金额
     * @apiSuccess (返回参数){Long} [id] ID
     * @apiSuccess (返回参数){String} [payeeCategory] 收款方类型（员工：EMPLOYEE；供应商：VENDER）
     * @apiSuccess (返回参数){Long} [payeeId] 收款方ID
     * @apiSuccess (返回参数){ZonedDateTime} requisitionDate 申请日期
     * @apiSuccess (返回参数){String} [requisitionNumber] 报账单编码
     * @apiSuccess (返回参数){Long} [setOfBooksId] 账套
     * @apiSuccess (返回参数){Integer} [status] 状态
     * @apiSuccess (返回参数){Long} [tenantId] 租户
     * @apiSuccess (返回参数){BigDecimal} [totalAmount] 总金额
     * @apiSuccess (返回参数){Integer} [versionNumber]  版本号
     * @apiSuccess (返回参数){List} [expenseDimensions] 报账单维度布局
     *
     * @apiSuccess (expenseDimensions) {Long} value  所选的值
     * @apiSuccess (expenseDimensions) {String} valueName  所选的值名称
     * @apiSuccess (expenseDimensions) {Boolean} name  维度名称
     * @apiSuccess (expenseDimensions) {String} dimensionId  维度Id
     * @apiSuccess (expenseDimensions) {String} dimensionFiled  字段代码
     * @apiSuccess (expenseDimensions) {int} sequence  序号
     * @apiSuccess (expenseDimensions) {Boolean} headerFlag  是否展示在单据头
     * @apiParamExample {json} 请求参数:
    /api/expense/report/header/my
     *
     * @apiSuccessExample {json} 成功返回值:
    [
        {
        "id": "1106210262543896577",
        "createdDate": "2019-03-14T23:07:33.644+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-03-14T23:07:33.645+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "requisitionNumber": "BXGS00001140320190004",
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "companyId": "1083751704185716737",
        "departmentId": "1105676563697147906",
        "applicantId": "1083751705402064897",
        "currencyCode": "CNY",
        "exchangeRate": 1,
        "totalAmount": 0,
        "functionalAmount": 0,
        "description": "测试数据",
        "documentTypeId": "1105854700853731329",
        "requisitionDate": "2019-03-13T23:34:41.128+08:00",
        "contractHeaderId": null,
        "status": 1001,
        "auditFlag": "N",
        "auditDate": null,
        "payeeCategory": null,
        "payeeId": null,
        "accountNumber": null,
        "accountName": null,
        "documentOid": "7f5e3b6a-18cf-4692-ace5-4ce7feed1cb5",
        "budgetCheckResult": null,
        "budgetCheckResultDesc": null,
        "applicantName": "小旋风",
        "applicantCode": "8888888"
        }
    ]
     */
    @ApiOperation(value = "报账单头查询接口-分页查询", notes = "报账单头查询接口-分页查询", tags = {"query"})
    @GetMapping("/header/my")
    public ResponseEntity<List<ExpenseReportHeader>> getMyExpenseReports(@ApiParam(value = "单据头类型id") @RequestParam(required = false) Long documentTypeId,
                                                                         @ApiParam(value = "申请日期从") @RequestParam(required = false) String requisitionDateFrom,
                                                                         @ApiParam(value = "申请日期至") @RequestParam(required = false) String requisitionDateTo,
                                                                         @ApiParam(value = "申请人id") @RequestParam(required = false) Long applicantId,
                                                                         @ApiParam(value = "状态") @RequestParam(required = false) Integer status,
                                                                         @ApiParam(value = "币种") @RequestParam(required = false) String currencyCode,
                                                                         @ApiParam(value = "金额从") @RequestParam(required = false) BigDecimal amountFrom,
                                                                         @ApiParam(value = "金额至") @RequestParam(required = false) BigDecimal amountTo,
                                                                         @ApiParam(value = "备注") @RequestParam(required = false) String remark,
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
                status,
                currencyCode,
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
    /**
     * @api {GET} /api/expense/report/header/by/id 【报账单】根据报账单头ID获取报账单头信息
     * @apiDescription 根据报账单头ID获取报账单头信息
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} expenseReportId 报账单头ID
     *
     * @apiSuccess (返回参数){String} [accountName] 收款方户名
     * @apiSuccess (返回参数){String} [accountNumber] 收款方账户
     * @apiSuccess (返回参数){String} [applicantCode] 申请人代码
     * @apiSuccess (返回参数){Long} applicantId 申请人ID
     * @apiSuccess (返回参数){String} [applicantName] 申请人名称
     * @apiSuccess (返回参数){ZonedDateTime} [auditDate] 审核日期
     * @apiSuccess (返回参数){String} [auditFlag] 审核标志
     * @apiSuccess (返回参数){String} [budgetCheckResult] 预算校验返回标志
     * @apiSuccess (返回参数){Long} companyId 公司ID
     * @apiSuccess (返回参数){Long} [contractHeaderId] 合同头ID
     * @apiSuccess (返回参数){Long} [createdBy] 创建人
     * @apiSuccess (返回参数){String} [createdCode] 创建人代码
     * @apiSuccess (返回参数){ZonedDateTime} [createdDate] 创建日期
     * @apiSuccess (返回参数){String} [createdName] 创建人名称
     * @apiSuccess (返回参数){String} currencyCode 币种
     * @apiSuccess (返回参数){Long} departmentId 部门ID
     * @apiSuccess (返回参数){String} [description] 备注
     * @apiSuccess (返回参数){String} [documentOid] 单据OID
     * @apiSuccess (返回参数){Long} documentTypeId 报账单类型ID
     * @apiSuccess (返回参数){BigDecimal} exchangeRate 汇率
     * @apiSuccess (返回参数){BigDecimal} [functionalAmount] 本币金额
     * @apiSuccess (返回参数){Long} [id] ID
     * @apiSuccess (返回参数){String} [payeeCategory] 收款方类型（员工：EMPLOYEE；供应商：VENDER）
     * @apiSuccess (返回参数){Long} [payeeId] 收款方ID
     * @apiSuccess (返回参数){ZonedDateTime} requisitionDate 申请日期
     * @apiSuccess (返回参数){String} [requisitionNumber] 报账单编码
     * @apiSuccess (返回参数){Long} [setOfBooksId] 账套
     * @apiSuccess (返回参数){Integer} [status] 状态
     * @apiSuccess (返回参数){Long} [tenantId] 租户
     * @apiSuccess (返回参数){BigDecimal} [totalAmount] 总金额
     * @apiSuccess (返回参数){Integer} [versionNumber]  版本号
     * @apiSuccess (返回参数){String} expTaxDist 税金分摊方式(TAX_IN:按含税金额分摊；TAX_OFF:按不含税金额分摊)
     * @apiParamExample {json} 请求参数:
    /api/expense/report/header/by/id?expenseReportId=1106225896027717633
     *
     * @apiSuccessExample {json} 成功返回值:
    {
    "id": "1106225896027717633",
    "createdDate": "2019-03-15T00:09:40.958+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-03-15T00:09:40.958+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "requisitionNumber": "BXGS00001150320190005",
    "tenantId": "1083751703623680001",
    "setOfBooksId": "1083762150064451585",
    "companyId": "1083751704185716737",
    "departmentId": "1105676563697147906",
    "applicantId": "1083751705402064897",
    "currencyCode": "CNY",
    "exchangeRate": 1,
    "totalAmount": 0,
    "functionalAmount": 0,
    "description": "测试数据",
    "documentTypeId": "1105854700853731329",
    "requisitionDate": "2019-03-13T23:34:41.128+08:00",
    "contractHeaderId": null,
    "status": 1001,
    "auditFlag": "N",
    "auditDate": null,
    "payeeCategory": null,
    "payeeId": null,
    "accountNumber": null,
    "accountName": null,
    "documentOid": "4a469d89-3634-416a-b268-f8c43ba5442e",
    "budgetCheckResult": null,
    "budgetCheckResultDesc": null,
    "applicantName": "小旋风",
    "applicantCode": "8888888",
    "expTaxDist": "TAX_IN",
    "expenseDimensions": [
    {
    "id": "1106225896275181570",
    "createdDate": "2019-03-15T00:09:41.013+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-03-15T00:09:41.013+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "documentType": 801001,
    "value": "1084698307856949249",
    "valueName": null,
    "name": null,
    "dimensionId": "1084698172754223106",
    "dimensionField": "dimension2Id",
    "headerFlag": true,
    "headerId": "1106225896027717633",
    "sequence": 10,
    "requiredFlag": false,
    "options": null
    }
    ],
    "companyName": "小嘛呀小二郎公司",
    "companyCode": "GS00001",
    "departmentName": "董事会办公室",
    "departmentCode": "XM-100101",
    "documentTypeName": "PKK报账单类型",
    "createdName": "小旋风",
    "createdCode": "8888888",
    "contractNumber": null
    }
     */
    @GetMapping("/header/by/id")
    public ResponseEntity<ExpenseReportHeaderDTO> getExpenseReportById(@RequestParam Long expenseReportId){
        return ResponseEntity.ok(expenseReportHeaderService.getExpenseReportById(expenseReportId));
    }

    /**
     * 删除报账单
     * @param id
     */
    /**
     * @api {DELETE} /api/expense/report/header/delete/{id} 【报账单】删除报账单
     * @apiDescription 删除报账单
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} id 单据ID

     * @apiParamExample {json} 请求参数:
    /api/expense/report/header/delete/1106225896027717633
     */
    @DeleteMapping("/header/delete/{id}")
    public void deleteExpenseReportHeader(@PathVariable Long id){
        expenseReportHeaderService.deleteExpenseReportHeaderById(id);
    }

    /**
     * 保存费用行信息
     */
    /**
     * @api {POST} /api/expense/report/line/save 【报账单】保存费用行
     * @apiDescription 保存费用行
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} id 报账单行ID
     * @apiParam (请求参数){Long} expReportHeaderId 报账单头ID
     * @apiParam (请求参数){Long} tenantId 租户ID
     * @apiParam (请求参数){Long} setOfBooksId 账套ID
     * @apiParam (请求参数){Long} companyId 公司ID
     * @apiParam (请求参数){Long} expenseTypeId 费用类型ID
     * @apiParam (请求参数){ZonedDateTime} expenseDate 发生日期
     * @apiParam (请求参数){Integer} quantity 数量
     * @apiParam (请求参数){BigDecimal} price 单价
     * @apiParam (请求参数){String} uom 单位
     * @apiParam (请求参数){BigDecimal} exchangeRate 汇率
     * @apiParam (请求参数){String} currencyCode 币种
     * @apiParam (请求参数){BigDecimal} amount 报账金额
     * @apiParam (请求参数){BigDecimal} functionAmount 本币金额
     * @apiParam (请求参数){BigDecimal} expenseAmount 费用金额
     * @apiParam (请求参数){BigDecimal} expenseFunctionAmount 费用本位币金额
     * @apiParam (请求参数){BigDecimal} taxAmount 税额
     * @apiParam (请求参数){BigDecimal} taxFunctionAmount 本币税额
     * @apiParam (请求参数){String} installmentDeductionFlag 分期抵扣标志(是：Y；否：N)
     * @apiParam (请求参数){String} inputTaxFlag 视同销售与进项转出标志（视同销售：FOR_SALE;全额转出：ALL_TRANSFER；部分转出：PART_ TRANSFER）
     * @apiParam (请求参数){String} description 备注
     * @apiParam (请求参数){String} reverseFlag 反冲标志(未反冲：N；反冲提交未审批：P；已反冲：Y)
     * @apiParam (请求参数){String} useType 用途类型
     * @apiParam (请求参数){Integer} versionNumber 版本号
     * @apiParam (请求参数){ZonedDateTime} createdDate 创建日期
     * @apiParam (请求参数){Long} createdBy 创建用户ID
     * @apiParam (请求参数){ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiParam (请求参数){Long} lastUpdatedBy 最后更新用户ID
     * @apiParam (请求参数){String} attachmentOid 附件OID
     * @apiParam (请求参数){Long} expenseBookId 账本ID
     * @apiParam (请求参数){List} fields 费用体系相关属性
     * @apiParam (请求参数){List} invoiceHeads 关联发票信息(发票头ID为空时表示手工录入信息)
     * @apiParam (请求参数){List} expenseReportDistList 分摊行数据
     *
     * @apiParam (InvoiceHead的属性) {Long} tenantId 租户ID
     * @apiParam (InvoiceHead的属性) {Long} setOfBooksId 账套ID
     * @apiParam (InvoiceHead的属性) {ZonedDateTime} invoiceDate 开票日期
     * @apiParam (InvoiceHead的属性) {String} invoiceNo 发票号码
     * @apiParam (InvoiceHead的属性) {String} invoiceCode 发票代码
     * @apiParam (InvoiceHead的属性) {String} machineNo 设备编号
     * @apiParam (InvoiceHead的属性) {String} checkCode 校验码(后6位)
     * @apiParam (InvoiceHead的属性) {BigDecimal} totalAmount 价税合计
     * @apiParam (InvoiceHead的属性) {BigDecimal} invoiceAmount 金额合计
     * @apiParam (InvoiceHead的属性) {BigDecimal} taxTotalAmount 税额合计
     * @apiParam (InvoiceHead的属性) {String} currencyCode 币种
     * @apiParam (InvoiceHead的属性) {BigDecimal} exchangeRate 汇率
     * @apiParam (InvoiceHead的属性) {String} remark 备注
     * @apiParam (InvoiceHead的属性) {String} buyerName 购方名称
     * @apiParam (InvoiceHead的属性) {String} buyerTaxNo 购方纳税人识别号
     * @apiParam (InvoiceHead的属性) {String} buyerAddPh 购方地址/电话
     * @apiParam (InvoiceHead的属性) {String} buyerAccount 购方开户行/账号
     * @apiParam (InvoiceHead的属性) {String} salerName 销方名称
     * @apiParam (InvoiceHead的属性) {String} salerTaxNo 销方纳税人识别号
     * @apiParam (InvoiceHead的属性) {String} salerAddPh 销方地址/电话
     * @apiParam (InvoiceHead的属性) {String} salerAccount 销方开户行/账号
     * @apiParam (InvoiceHead的属性) {Boolean} cancelFlag 作废标志
     * @apiParam (InvoiceHead的属性) {Boolean} redInvoiceFlag 红票标志
     * @apiParam (InvoiceHead的属性) {String} createdMethod 创建方式
     * @apiParam (InvoiceHead的属性) {Boolean} checkResult 验真状态
     * @apiParam (InvoiceHead的属性) {List} invoiceLineList 发票行
     *
     * @apiParam (InvoiceLine的属性) {Long} tenantId 租户ID
     * @apiParam (InvoiceLine的属性) {Long} setOfBooksId 账套ID
     * @apiParam (InvoiceLine的属性) {Long} invoiceHeadId 发票头ID
     * @apiParam (InvoiceLine的属性) {Integer} invoiceLineNum 发票行序号
     * @apiParam (InvoiceLine的属性) {String} goodsName 货物或应税劳务、服务名称
     * @apiParam (InvoiceLine的属性) {String} specificationModel 规格型号
     * @apiParam (InvoiceLine的属性) {String} unit 单位
     * @apiParam (InvoiceLine的属性) {Long} num 数量
     * @apiParam (InvoiceLine的属性) {BigDecimal} unitPrice 单价
     * @apiParam (InvoiceLine的属性) {BigDecimal} detailAmount 金额
     * @apiParam (InvoiceLine的属性) {String} taxRate 税率
     * @apiParam (InvoiceLine的属性) {BigDecimal} taxAmount 税额
     * @apiParam (InvoiceLine的属性) {String} currencyCode 币种
     * @apiParam (InvoiceLine的属性) {BigDecimal} exchangeRate 汇率
     *
     * @apiParam (expenseReportDistList的属性){Long} id 报账单分摊行ID
     * @apiParam (expenseReportDistList的属性){Long} expReportHeaderId 报账单头ID
     * @apiParam (expenseReportDistList的属性){Long} expReportLineId 报账单行ID
     * @apiParam (expenseReportDistList的属性){Long} tenantId 租户ID
     * @apiParam (expenseReportDistList的属性){Long} setOfBooksId 账套ID
     * @apiParam (expenseReportDistList的属性){Long} companyId 公司ID
     * @apiParam (expenseReportDistList的属性){Long} expenseTypeId 费用类型ID
     * @apiParam (expenseReportDistList的属性){Long} departmentId 部门ID
     * @apiParam (expenseReportDistList的属性){Long} responsibilityCenterId 责任中心ID
     * @apiParam (expenseReportDistList的属性){BigDecimal} amount 价税合计总金额
     * @apiParam (expenseReportDistList的属性){BigDecimal} functionAmount 本币金额
     * @apiParam (expenseReportDistList的属性) {BigDecimal} reportDistAmount 分摊金额
     * @apiParam (expenseReportDistList的属性) {BigDecimal} reportDistFunctionAmount 分摊本币金额
     * @apiParam (expenseReportDistList的属性){BigDecimal} exchangeRate 汇率
     * @apiParam (expenseReportDistList的属性){String} currencyCode 币种
     * @apiParam (expenseReportDistList的属性){BigDecimal} taxDistAmount 税分摊额
     * @apiParam (expenseReportDistList的属性){BigDecimal} taxDistFunctionAmount 税本位币分摊金额
     * @apiParam (expenseReportDistList的属性){Long} dimension1Id 维度1
     * @apiParam (expenseReportDistList的属性){Long} dimension2Id 维度2
     * @apiParam (expenseReportDistList的属性){Long} dimension3Id 维度3
     * @apiParam (expenseReportDistList的属性){Long} dimension4Id 维度4
     * @apiParam (expenseReportDistList的属性){Long} dimension5Id 维度5
     * @apiParam (expenseReportDistList的属性){Long} dimension6Id 维度6
     * @apiParam (expenseReportDistList的属性){Long} dimension7Id 维度7
     * @apiParam (expenseReportDistList的属性){Long} dimension8Id 维度8
     * @apiParam (expenseReportDistList的属性){Long} dimension9Id 维度9
     * @apiParam (expenseReportDistList的属性){Long} dimension10Id 维度10
     * @apiParam (expenseReportDistList的属性){Long} dimension11Id 维度11
     * @apiParam (expenseReportDistList的属性){Long} dimension12Id 维度12
     * @apiParam (expenseReportDistList的属性){Long} dimension13Id 维度13
     * @apiParam (expenseReportDistList的属性){Long} dimension14Id 维度14
     * @apiParam (expenseReportDistList的属性){Long} dimension15Id 维度15
     * @apiParam (expenseReportDistList的属性){Long} dimension16Id 维度16
     * @apiParam (expenseReportDistList的属性){Long} dimension17Id 维度17
     * @apiParam (expenseReportDistList的属性){Long} dimension18Id 维度18
     * @apiParam (expenseReportDistList的属性){Long} dimension19Id 维度19
     * @apiParam (expenseReportDistList的属性){Long} dimension20Id 维度20
     * @apiParam (expenseReportDistList的属性){String} reverseFlag 反冲标志(未反冲：N；反冲提交未审批：P；已反冲：Y)
     * @apiParam (expenseReportDistList的属性){Integer} versionNumber 版本号
     * @apiParam (expenseReportDistList的属性){ZonedDateTime} createdDate 创建日期
     * @apiParam (expenseReportDistList的属性){Long} createdBy 创建用户ID
     * @apiParam (expenseReportDistList的属性){ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiParam (expenseReportDistList的属性){Long} lastUpdatedBy 最后更新用户ID
     * @apiParam (expenseReportDistList的属性){Long} sourceDocumentCategory 来源单据类型
     * @apiParam (expenseReportDistList的属性){Long} sourceDocumentId 来源单据id
     * @apiParam (expenseReportDistList的属性){Long} sourceDocumentDistId 来源单据分摊行id
     *
     * @apiParamExample {json} 请求参数:
    {
    "id": null,
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": null,
    "lastUpdatedBy": null,
    "versionNumber": null,
    "expReportHeaderId": null,
    "tenantId": null,
    "setOfBooksId": null,
    "companyId": null,
    "expenseTypeId": null,
    "expenseTypeName": null,
    "expenseDate": null,
    "quantity": null,
    "price": null,
    "uom": null,
    "exchangeRate": null,
    "currencyCode": null,
    "amount": null,
    "functionAmount": null,
    "expenseAmount": null,
    "expenseFunctionAmount": null,
    "taxAmount": null,
    "taxFunctionAmount": null,
    "installmentDeductionFlag": null,
    "inputTaxFlag": null,
    "description": null,
    "reverseFlag": null,
    "useType": null,
    "attachmentOid": null,
    "expenseBookId": null,
    "attachmentOidList": null,
    "attachments": null,
    "fields": [
    {
    "id": null,
    "fieldType": null,
    "fieldDataType": null,
    "name": null,
    "value": null,
    "codeName": null,
    "messageKey": null,
    "sequence": null,
    "customEnumerationOid": null,
    "mappedColumnId": null,
    "printHide": null,
    "required": null,
    "showOnList": null,
    "fieldOid": null,
    "editable": null,
    "defaultValueMode": null,
    "defaultValueKey": null,
    "showValue": null,
    "defaultValueConfigurable": null,
    "commonField": null,
    "reportKey": null,
    "i18n": null,
    "options": null
    }
    ],
    "invoiceHeads": [
    {
    "id": null,
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": null,
    "lastUpdatedBy": null,
    "versionNumber": null,
    "invoiceTypeId": null,
    "tenantId": null,
    "setOfBooksId": null,
    "invoiceDate": null,
    "invoiceNo": null,
    "invoiceCode": null,
    "machineNo": null,
    "checkCode": null,
    "totalAmount": null,
    "invoiceAmount": null,
    "taxTotalAmount": null,
    "currencyCode": null,
    "exchangeRate": null,
    "remark": null,
    "buyerName": null,
    "buyerTaxNo": null,
    "buyerAddPh": null,
    "buyerAccount": null,
    "salerName": null,
    "salerTaxNo": null,
    "salerAddPh": null,
    "salerAccount": null,
    "cancelFlag": null,
    "redInvoiceFlag": null,
    "createdMethod": null,
    "checkResult": null,
    "fromBook": null,
    "stringInvoiceDate": null,
    "stringCheckResult": null,
    "invoiceTypeName": null,
    "createdMethodName": null,
    "reportProgress": null,
    "reportProgressName": null,
    "invoiceLineList": [
    {
    "id": null,
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": null,
    "lastUpdatedBy": null,
    "versionNumber": null,
    "tenantId": null,
    "setOfBooksId": null,
    "invoiceHeadId": null,
    "invoiceLineNum": null,
    "goodsName": null,
    "specificationModel": null,
    "unit": null,
    "num": null,
    "unitPrice": null,
    "detailAmount": null,
    "taxRate": null,
    "taxAmount": null,
    "currencyCode": null,
    "exchangeRate": null
    }
    ]
    }
    ],
    "invoiceLineIds": [],
    "index": null,
    "expenseReportDistList": [
    {
    "id": null,
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": null,
    "lastUpdatedBy": null,
    "versionNumber": null,
    "dimension1Id": null,
    "dimension2Id": null,
    "dimension3Id": null,
    "dimension4Id": null,
    "dimension5Id": null,
    "dimension6Id": null,
    "dimension7Id": null,
    "dimension8Id": null,
    "dimension9Id": null,
    "dimension10Id": null,
    "dimension11Id": null,
    "dimension12Id": null,
    "dimension13Id": null,
    "dimension14Id": null,
    "dimension15Id": null,
    "dimension16Id": null,
    "dimension17Id": null,
    "dimension18Id": null,
    "dimension19Id": null,
    "dimension20Id": null,
    "expReportHeaderId": null,
    "expReportLineId": null,
    "tenantId": null,
    "setOfBooksId": null,
    "companyId": null,
    "expenseTypeId": null,
    "departmentId": null,
    "responsibilityCenterId": null,
    "amount": null,
    "functionAmount": null,
    "exchangeRate": null,
    "currencyCode": null,
    "taxDistAmount": null,
    "taxDistFunctionAmount": null,
    "reverseFlag": null,
    "sourceDocumentCategory": null,
    "sourceDocumentId": null,
    "sourceDocumentDistId": null
    }
    ]
    }
     */
    @PostMapping("/line/save")
    public ResponseEntity<ExpenseReportLineDTO> saveExpenseReportLine(@RequestBody @Valid ExpenseReportLineDTO dto){
        return ResponseEntity.ok(expenseReportLineService.saveExpenseReportLine(dto,true));
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
    public void saveExpenseReportLineFromBook(@RequestParam Long headerId,
                                              @RequestBody List<Long> expenseBookIds){
        expenseReportLineService.saveExpenseReportLineFromBook(headerId,expenseBookIds);
    }

    /**
     * @api {GET} /api/expense/report/line/query/by/headerId 【报账单】查询报账单下的费用行信息
     * @apiDescription 查询报账单下的费用行信息
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} reportHeaderId 报账单ID
     * @apiParam (请求参数){int} [page] 页数
     * @apiParam (请求参数){int} [size] 每页大小
     *
     * @apiSuccess (返回参数){Long} id 报账单行ID
     * @apiSuccess (返回参数){Integer} index 序号
     * @apiSuccess (返回参数){Long} expReportHeaderId 报账单头ID
     * @apiSuccess (返回参数){Long} tenantId 租户ID
     * @apiSuccess (返回参数){Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数){Long} companyId 公司ID
     * @apiSuccess (返回参数){Long} expenseTypeId 费用类型ID
     * @apiSuccess (返回参数){Long} expenseTypeName 费用类型名称
     * @apiSuccess (返回参数){ZonedDateTime} expenseDate 发生日期
     * @apiSuccess (返回参数){Integer} quantity 数量
     * @apiSuccess (返回参数){BigDecimal} price 单价
     * @apiSuccess (返回参数){String} uom 单位
     * @apiSuccess (返回参数){BigDecimal} exchangeRate 汇率
     * @apiSuccess (返回参数){String} currencyCode 币种
     * @apiSuccess (返回参数){BigDecimal} amount 报账金额
     * @apiSuccess (返回参数){BigDecimal} functionAmount 本币金额
     * @apiSuccess (返回参数){BigDecimal} expenseAmount 费用金额
     * @apiSuccess (返回参数){BigDecimal} expenseFunctionAmount 费用本位币金额
     * @apiSuccess (返回参数){BigDecimal} taxAmount 税额
     * @apiSuccess (返回参数){BigDecimal} taxFunctionAmount 本币税额
     * @apiSuccess (返回参数){String} installmentDeductionFlag 分期抵扣标志(是：Y；否：N)
     * @apiSuccess (返回参数){String} inputTaxFlag 视同销售与进项转出标志（视同销售：FOR_SALE;全额转出：ALL_TRANSFER；部分转出：PART_ TRANSFER）
     * @apiSuccess (返回参数){String} description 备注
     * @apiSuccess (返回参数){String} reverseFlag 反冲标志(未反冲：N；反冲提交未审批：P；已反冲：Y)
     * @apiSuccess (返回参数){String} useType 用途类型
     * @apiSuccess (返回参数){Integer} versionNumber 版本号
     * @apiSuccess (返回参数){ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数){Long} createdBy 创建用户ID
     * @apiSuccess (返回参数){ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数){Long} lastUpdatedBy 最后更新用户ID
     * @apiSuccess (返回参数){String} attachmentOid 附件OID
     * @apiSuccess (返回参数){Long} expenseBookId 账本ID
     * @apiSuccess (返回参数){List} fields 费用体系相关属性
     * @apiSuccess (返回参数){List} invoiceHeads 关联的发票信息
     *
     * @apiSuccess (InvoiceHead的属性) {Long} tenantId 租户ID
     * @apiSuccess (InvoiceHead的属性) {Long} setOfBooksId 账套ID
     * @apiSuccess (InvoiceHead的属性) {ZonedDateTime} invoiceDate 开票日期
     * @apiSuccess (InvoiceHead的属性) {String} invoiceNo 发票号码
     * @apiSuccess (InvoiceHead的属性) {String} invoiceCode 发票代码
     * @apiSuccess (InvoiceHead的属性) {String} machineNo 设备编号
     * @apiSuccess (InvoiceHead的属性) {String} checkCode 校验码(后6位)
     * @apiSuccess (InvoiceHead的属性) {BigDecimal} totalAmount 价税合计
     * @apiSuccess (InvoiceHead的属性) {BigDecimal} invoiceAmount 金额合计
     * @apiSuccess (InvoiceHead的属性) {BigDecimal} taxTotalAmount 税额合计
     * @apiSuccess (InvoiceHead的属性) {String} currencyCode 币种
     * @apiSuccess (InvoiceHead的属性) {BigDecimal} exchangeRate 汇率
     * @apiSuccess (InvoiceHead的属性) {String} remark 备注
     * @apiSuccess (InvoiceHead的属性) {String} buyerName 购方名称
     * @apiSuccess (InvoiceHead的属性) {String} buyerTaxNo 购方纳税人识别号
     * @apiSuccess (InvoiceHead的属性) {String} buyerAddPh 购方地址/电话
     * @apiSuccess (InvoiceHead的属性) {String} buyerAccount 购方开户行/账号
     * @apiSuccess (InvoiceHead的属性) {String} salerName 销方名称
     * @apiSuccess (InvoiceHead的属性) {String} salerTaxNo 销方纳税人识别号
     * @apiSuccess (InvoiceHead的属性) {String} salerAddPh 销方地址/电话
     * @apiSuccess (InvoiceHead的属性) {String} salerAccount 销方开户行/账号
     * @apiSuccess (InvoiceHead的属性) {Boolean} cancelFlag 作废标志
     * @apiSuccess (InvoiceHead的属性) {Boolean} redInvoiceFlag 红票标志
     * @apiSuccess (InvoiceHead的属性) {String} createdMethod 创建方式
     * @apiSuccess (InvoiceHead的属性) {Boolean} checkResult 验真状态
     * @apiSuccess (InvoiceHead的属性) {List} invoiceLineList 发票行
     *
     * @apiSuccess (InvoiceLine的属性) {Long} tenantId 租户ID
     * @apiSuccess (InvoiceLine的属性) {Long} setOfBooksId 账套ID
     * @apiSuccess (InvoiceLine的属性) {Long} invoiceHeadId 发票头ID
     * @apiSuccess (InvoiceLine的属性) {Integer} invoiceLineNum 发票行序号
     * @apiSuccess (InvoiceLine的属性) {String} goodsName 货物或应税劳务、服务名称
     * @apiSuccess (InvoiceLine的属性) {String} specificationModel 规格型号
     * @apiSuccess (InvoiceLine的属性) {String} unit 单位
     * @apiSuccess (InvoiceLine的属性) {Long} num 数量
     * @apiSuccess (InvoiceLine的属性) {BigDecimal} unitPrice 单价
     * @apiSuccess (InvoiceLine的属性) {BigDecimal} detailAmount 金额
     * @apiSuccess (InvoiceLine的属性) {String} taxRate 税率
     * @apiSuccess (InvoiceLine的属性) {BigDecimal} taxAmount 税额
     * @apiSuccess (InvoiceLine的属性) {String} currencyCode 币种
     * @apiSuccess (InvoiceLine的属性) {BigDecimal} exchangeRate 汇率
     */
    @GetMapping("/line/query/by/headerId")
    public ResponseEntity<List<ExpenseReportLine>> getExpenseReportLinesByHeaderId(@RequestParam Long reportHeaderId,
                                                            Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportLine> expenseReportLinesByHeaderId = expenseReportLineService.getExpenseReportLinesByHeaderId(reportHeaderId, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(expenseReportLinesByHeaderId,totalHeader,HttpStatus.OK);
    }


    /**
     * @api {GET} /api/expense/report/line/query/by/id 【报账单】费用行明细
     * @apiDescription 费用行明细查询费用行明细，由于分摊行需要分页，所以需要单独调用链接
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} id 费用行ID
     *
     * @apiSuccess (返回参数){Long} id 报账单行ID
     * @apiSuccess (返回参数){Integer} index 序号
     * @apiSuccess (返回参数){Long} expReportHeaderId 报账单头ID
     * @apiSuccess (返回参数){Long} tenantId 租户ID
     * @apiSuccess (返回参数){Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数){Long} companyId 公司ID
     * @apiSuccess (返回参数){Long} expenseTypeId 费用类型ID
     * @apiSuccess (返回参数){Long} expenseTypeName 费用类型名称
     * @apiSuccess (返回参数){ZonedDateTime} expenseDate 发生日期
     * @apiSuccess (返回参数){Integer} quantity 数量
     * @apiSuccess (返回参数){BigDecimal} price 单价
     * @apiSuccess (返回参数){String} uom 单位
     * @apiSuccess (返回参数){BigDecimal} exchangeRate 汇率
     * @apiSuccess (返回参数){String} currencyCode 币种
     * @apiSuccess (返回参数){BigDecimal} amount 报账金额
     * @apiSuccess (返回参数){BigDecimal} functionAmount 本币金额
     * @apiSuccess (返回参数){BigDecimal} expenseAmount 费用金额
     * @apiSuccess (返回参数){BigDecimal} expenseFunctionAmount 费用本位币金额
     * @apiSuccess (返回参数){BigDecimal} taxAmount 税额
     * @apiSuccess (返回参数){BigDecimal} taxFunctionAmount 本币税额
     * @apiSuccess (返回参数){String} installmentDeductionFlag 分期抵扣标志(是：Y；否：N)
     * @apiSuccess (返回参数){String} inputTaxFlag 视同销售与进项转出标志（视同销售：FOR_SALE;全额转出：ALL_TRANSFER；部分转出：PART_ TRANSFER）
     * @apiSuccess (返回参数){String} description 备注
     * @apiSuccess (返回参数){String} reverseFlag 反冲标志(未反冲：N；反冲提交未审批：P；已反冲：Y)
     * @apiSuccess (返回参数){String} useType 用途类型
     * @apiSuccess (返回参数){Integer} versionNumber 版本号
     * @apiSuccess (返回参数){ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数){Long} createdBy 创建用户ID
     * @apiSuccess (返回参数){ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数){Long} lastUpdatedBy 最后更新用户ID
     * @apiSuccess (返回参数){String} attachmentOid 附件OID
     * @apiSuccess (返回参数){Long} expenseBookId 账本ID
     */
    @GetMapping("/line/query/by/id")
    public ResponseEntity<ExpenseReportLine> getExpenseReportLineById(@RequestParam(value = "id") Long id){
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
    public void deleteInvoiceByInvoiceLineId(@RequestParam(value = "lineId") Long lineId,
                                             @RequestParam(value = "invoiceLineId") Long invoiceLineId){
        expenseReportLineService.deleteInvoiceByInvoiceLineId(invoiceLineId,lineId);
    }

    /**
     * @api {GET} /api/expense/report/dist/query/by/lineId 【报账单】根据费用行ID查询分摊行
     * @apiDescription 根据费用行ID查询分摊行
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} lineId 费用行ID
     * @apiParam (请求参数){int} [page] 页数
     * @apiParam (请求参数){int} [size] 每页大小
     *
     * @apiSuccess (返回参数){Long} id 报账单分摊行ID
     * @apiSuccess (返回参数){Long} expReportHeaderId 报账单头ID
     * @apiSuccess (返回参数){Long} expReportLineId 报账单行ID
     * @apiSuccess (返回参数){Long} tenantId 租户ID
     * @apiSuccess (返回参数){Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数){Long} companyId 公司ID
     * @apiSuccess (返回参数){Long} expenseTypeId 费用类型ID
     * @apiSuccess (返回参数){Long} departmentId 部门ID
     * @apiSuccess (返回参数){Long} responsibilityCenterId 责任中心ID
     * @apiSuccess (返回参数){BigDecimal} amount 分摊价税总金额
     * @apiSuccess (返回参数){BigDecimal} functionAmount 本币金额
     * @apiSuccess (返回参数){BigDecimal} reportDistAmount 分摊金额
     * @apiSuccess (返回参数){BigDecimal} reportDistFunctionAmount 分摊本币金额
     * @apiSuccess (返回参数){BigDecimal} exchangeRate 汇率
     * @apiSuccess (返回参数){String} currencyCode 币种
     * @apiSuccess (返回参数){BigDecimal} taxDistAmount 税分摊额
     * @apiSuccess (返回参数){BigDecimal} taxDistFunctionAmount 税本位币分摊金额
     * @apiSuccess (返回参数){Long} dimension1Id 维度1
     * @apiSuccess (返回参数){Long} dimension2Id 维度2
     * @apiSuccess (返回参数){Long} dimension3Id 维度3
     * @apiSuccess (返回参数){Long} dimension4Id 维度4
     * @apiSuccess (返回参数){Long} dimension5Id 维度5
     * @apiSuccess (返回参数){Long} dimension6Id 维度6
     * @apiSuccess (返回参数){Long} dimension7Id 维度7
     * @apiSuccess (返回参数){Long} dimension8Id 维度8
     * @apiSuccess (返回参数){Long} dimension9Id 维度9
     * @apiSuccess (返回参数){Long} dimension10Id 维度10
     * @apiSuccess (返回参数){Long} dimension11Id 维度11
     * @apiSuccess (返回参数){Long} dimension12Id 维度12
     * @apiSuccess (返回参数){Long} dimension13Id 维度13
     * @apiSuccess (返回参数){Long} dimension14Id 维度14
     * @apiSuccess (返回参数){Long} dimension15Id 维度15
     * @apiSuccess (返回参数){Long} dimension16Id 维度16
     * @apiSuccess (返回参数){Long} dimension17Id 维度17
     * @apiSuccess (返回参数){Long} dimension18Id 维度18
     * @apiSuccess (返回参数){Long} dimension19Id 维度19
     * @apiSuccess (返回参数){Long} dimension20Id 维度20
     * @apiSuccess (返回参数){String} reverseFlag 反冲标志(未反冲：N；反冲提交未审批：P；已反冲：Y)
     * @apiSuccess (返回参数){Integer} versionNumber 版本号
     * @apiSuccess (返回参数){ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数){Long} createdBy 创建用户ID
     * @apiSuccess (返回参数){ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数){Long} lastUpdatedBy 最后更新用户ID
     * @apiSuccess (返回参数){Long} sourceDocumentCategory 来源单据类型（EXP_REQUISITION：申请单）
     * @apiSuccess (返回参数){Long} sourceDocumentId 来源单据id
     * @apiSuccess (返回参数){Long} sourceDocumentDistId 来源单据分摊行id
     * @apiSuccess (返回参数){String} dimension1Name...dimension20Name 维值名称
     * @apiSuccess (返回参数){String} companyName 公司名称
     * @apiSuccess (返回参数){String} departmentName 部门名称
     * @apiSuccess (返回参数){String} responsibilityCenterName 责任中心名称
     * @apiSuccess (返回参数){Integer} index 序号
     */
    @GetMapping("/dist/query/by/lineId")
    public ResponseEntity<List<ExpenseReportDistDTO>> getExpenseReportDistByLineId(@RequestParam(value = "lineId") Long lineId){
        List<ExpenseReportDistDTO> expenseReportDistByLineId = expenseReportDistService.getExpenseReportDistDTOByLineId(lineId);
        return ResponseEntity.ok(expenseReportDistByLineId);
    }

    /**
     * 删除费用行
     * @param id
     */
    /**
     * @api {DELETE} /api/expense/report/line/delete/{id} 【报账单】删除费用行信息
     * @apiDescription 删除费用行信息
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} id 费用行ID

     * @apiParamExample {json} 请求参数:
     /api/expense/report/line/delete/1106225896027717633
     */
    @DeleteMapping("/line/delete/{id}")
    public void deleteExpenseReportLine(@PathVariable Long id){
        expenseReportLineService.deleteExpenseReportLineById(id);
    }

    /**
     * 创建修改计划付款行
     *
     * @param expensePaymentScheduleDTO
     * @return
     */
    /**
     * @api {POST} /api/expense/report/payment/schedule/save 【报账单】创建修改计划付款行
     * @apiDescription 创建修改计划付款行
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数) {Long} id    计划付款行ID
     * @apiParam (请求参数) {Long} expReportHeaderId    报账单头ID
     * @apiParam (请求参数) {Integer} index  计划付款行号
     * @apiParam (请求参数) {String} description  计划付款行备注
     * @apiParam (请求参数) {String} currencyCode  币种代码
     * @apiParam (请求参数) {Double} exchangeRate  汇率
     * @apiParam (请求参数) {Double} amount  行金额
     * @apiParam (请求参数) {Double} functionAmount  行本币金额
     * @apiParam (请求参数) {Double} writeOffAmount  已核销金额
     * @apiParam (请求参数) {DateTime} schedulePaymentDate  计划付款日期
     * @apiParam (请求参数) {String} paymentMethod  付款方式大类
     * @apiParam (请求参数) {String} paymentMethodName  付款方式名称
     * @apiParam (请求参数) {Long} cshTransactionClassId  现金事务分类id
     * @apiParam (请求参数) {String} cshTransactionClassName  现金事务分类名称
     * @apiParam (请求参数) {Long} cashFlowItemId  现金流量项id
     * @apiParam (请求参数) {String} payeeCategory  收款对象类别code EMPLOYEE和VENDER
     * @apiParam (请求参数) {String} payeeCategoryName  收款对象类别名称 员工和供应商
     * @apiParam (请求参数) {Long} payeeId  收款对象ID
     * @apiParam (请求参数) {String} payeeCode  收款方代码
     * @apiParam (请求参数) {String} payeeName  收款方名称
     * @apiParam (请求参数) {String} accountNumber  银行账号
     * @apiParam (请求参数) {String} accountName  银行户名
     * @apiParam (请求参数) {String} bankCode  银行代码
     * @apiParam (请求参数) {String} bankName  银行名称
     * @apiParam (请求参数) {String} frozenFlag  是否冻结(Y/N)
     * @apiParam (请求参数) {Long} contractLineId  合同资金计划行id
     * @apiParam (请求参数) {Double} contractLineAmount  合同资金计划行可关联金额
     * @apiParam (请求参数) {ContractHeaderLineCO} contractHeaderLineMessage  合同头行详细信息
     * @apiParam (请求参数) {PublicReportLinePaidInfoDTO} paidInfo    付款信息
     * @apiParam (请求参数) {List(CashWriteOffCO)} cashWriteOffMessage  核销数据集合
     *
     * @apiParam (请求参数contractHeaderLineMessage的属性) {Long} headerId  合同头ID
     * @apiParam (请求参数contractHeaderLineMessage的属性) {String} contractNumber  合同编号
     * @apiParam (请求参数contractHeaderLineMessage的属性) {String} contractName  合同名称
     * @apiParam (请求参数contractHeaderLineMessage的属性) {Double} contractAmount  合同总金额
     * @apiParam (请求参数contractHeaderLineMessage的属性) {Long} lineId  合同行ID
     * @apiParam (请求参数contractHeaderLineMessage的属性) {Long} lineNumber  行号
     * @apiParam (请求参数contractHeaderLineMessage的属性) {String} lineCurrency  合同行币种
     * @apiParam (请求参数contractHeaderLineMessage的属性) {Double} lineAmount  合同行金额
     * @apiParam (请求参数contractHeaderLineMessage的属性) {String} dueDate  签订日期
     *
     * @apiParam (请求参数PublicReportLineAmountCO的属性) {Long} documentLineId  报账单计划付款行ID
     * @apiParam (请求参数PublicReportLineAmountCO的属性) {Double} paidAmount  已支付金额
     * @apiParam (请求参数PublicReportLineAmountCO的属性) {Double} returnAmount  已退款金额
     *

     * @apiParam (请求参数CashWriteOffCO的属性) {Long} cshTransactionDetailId  支付明细id
     * @apiParam (请求参数CashWriteOffCO的属性) {String} billcode  支付流水号
     * @apiParam (请求参数CashWriteOffCO的属性) {String} prepaymentRequisitionTypeDesc  预付款类型描述
     * @apiParam (请求参数CashWriteOffCO的属性) {String} prepaymentRequisitionNumber  预付款单据编号
     * @apiParam (请求参数CashWriteOffCO的属性) {String} payDate  交易日期(支付时间)
     * @apiParam (请求参数CashWriteOffCO的属性) {Double} prepaymentRequisitionAmount  借款金额
     * @apiParam (请求参数CashWriteOffCO的属性) {Double} unWriteOffAmount  借款余额(未核销金额)
     * @apiParam (请求参数CashWriteOffCO的属性) {Double} writeOffAmount  本次核销金额
     * @apiParam (请求参数CashWriteOffCO的属性) {String} writeOffDate  核销日期
     * @apiParam (请求参数CashWriteOffCO的属性) {String} periodName  期间
     * @apiParam (请求参数CashWriteOffCO的属性) {String} status  状态:N未生效;P已生效;Y:已核算
     * @apiParam (请求参数CashWriteOffCO的属性) {Long} tenantId  租户id
     * @apiParam (请求参数CashWriteOffCO的属性) {String} operationType  操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
     * @apiParam (请求参数CashWriteOffCO的属性) {String} currencyCode  币种代码
     * @apiParam (请求参数CashWriteOffCO的属性) {Long} documentHeaderId  单据头ID（报账单头ID）
     * @apiParam (请求参数CashWriteOffCO的属性) {Long} documentLineId  单据行ID (计划付款行ID)
     */
    @PostMapping("/payment/schedule/save")
    public ResponseEntity<ExpenseReportPaymentScheduleDTO> createOrUpdatePaymentSchedule(@Valid @RequestBody ExpenseReportPaymentScheduleDTO expensePaymentScheduleDTO) {
        return ResponseEntity.ok(expenseReportPaymentScheduleService.createOrUpdatePaymentSchedule(expensePaymentScheduleDTO));
    }

    /**
     * 根据ID查询报账单付款计划行
     *
     * @param id 计划付款行ID
     * @return
     */
    /**
     * @api {GET} /api/expense/report/payment/schedule/{id} 【报账单】计划付款行明细
     * @apiDescription 根据ID获取计划付款行明细
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} id 计划付款行ID

     * @apiParamExample {json} 请求参数:
    /api/expense/report/payment/schedule/1234
     */
    @GetMapping("/payment/schedule/{id}")
    public ResponseEntity<ExpenseReportPaymentScheduleDTO> getExpensePaymentScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseReportPaymentScheduleService.getExpensePaymentScheduleById(id));
    }

    /**
     * 分页查询一个报帐单头下的付款计划行
     *
     * @api {GET} /api/expense/report/payment/schedule/query 【报账单】头查计划付款行
     * @apiDescription 分页查询一个报帐单头下的付款计划行
     * @apiGroup ExpenseReport
     * @apiParam (请求参数) {Long} reportHeaderId 报账单头ID
     * @apiParam (请求参数) {Integer} size 每页数量
     * @apiParam (请求参数) {Integer} page 页码，从0开始
     *
     * @apiSuccess (返回参数) {Long} id    计划付款行ID
     * @apiSuccess (返回参数) {Long} expReportHeaderId    报账单头ID
     * @apiSuccess (返回参数) {Integer} index  计划付款行号
     * @apiSuccess (返回参数) {String} description  计划付款行备注
     * @apiSuccess (返回参数) {String} currencyCode  币种代码
     * @apiSuccess (返回参数) {Double} exchangeRate  汇率
     * @apiSuccess (返回参数) {Double} amount  行金额
     * @apiSuccess (返回参数) {Double} functionAmount  行本币金额
     * @apiSuccess (返回参数) {Double} writeOffAmount  已核销金额
     * @apiSuccess (返回参数) {DateTime} schedulePaymentDate  计划付款日期
     * @apiSuccess (返回参数) {String} paymentMethod  付款方式大类
     * @apiSuccess (返回参数) {String} paymentMethodName  付款方式名称
     * @apiSuccess (返回参数) {Long} cshTransactionClassId  现金事务分类id
     * @apiSuccess (返回参数) {String} cshTransactionClassName  现金事务分类名称
     * @apiSuccess (返回参数) {Long} cashFlowItemId  现金流量项id
     * @apiSuccess (返回参数) {String} payeeCategory  收款对象类别code EMPLOYEE和VENDER
     * @apiSuccess (返回参数) {String} payeeCategoryName  收款对象类别名称 员工和供应商
     * @apiSuccess (返回参数) {Long} payeeId  收款对象ID
     * @apiSuccess (返回参数) {String} payeeCode  收款方代码
     * @apiSuccess (返回参数) {String} payeeName  收款方名称
     * @apiSuccess (返回参数) {String} accountNumber  银行账号
     * @apiSuccess (返回参数) {String} accountName  银行户名
     * @apiSuccess (返回参数) {String} bankCode  银行代码
     * @apiSuccess (返回参数) {String} bankName  银行名称
     * @apiSuccess (返回参数) {String} frozenFlag  是否冻结(Y/N)
     * @apiSuccess (返回参数) {Long} contractLineId  合同资金计划行id
     * @apiSuccess (返回参数) {Double} contractLineAmount  合同资金计划行可关联金额
     * @apiSuccess (返回参数) {ContractHeaderLineCO} contractHeaderLineMessage  合同头行详细信息
     * @apiSuccess (返回参数) {PublicReportLinePaidInfoDTO} paidInfo    付款信息
     * @apiSuccess (返回参数) {List(CashWriteOffCO)} cashWriteOffMessage  核销数据集合
     *
     * @apiSuccess (返回参数contractHeaderLineMessage的属性) {Long} headerId  合同头ID
     * @apiSuccess (返回参数contractHeaderLineMessage的属性) {String} contractNumber  合同编号
     * @apiSuccess (返回参数contractHeaderLineMessage的属性) {String} contractName  合同名称
     * @apiSuccess (返回参数contractHeaderLineMessage的属性) {Double} contractAmount  合同总金额
     * @apiSuccess (返回参数contractHeaderLineMessage的属性) {Long} lineId  合同行ID
     * @apiSuccess (返回参数contractHeaderLineMessage的属性) {Long} lineNumber  行号
     * @apiSuccess (返回参数contractHeaderLineMessage的属性) {String} lineCurrency  合同行币种
     * @apiSuccess (返回参数contractHeaderLineMessage的属性) {Double} lineAmount  合同行金额
     * @apiSuccess (返回参数contractHeaderLineMessage的属性) {String} dueDate  签订日期
     *
     * @apiSuccess (返回参数PublicReportLineAmountCO的属性) {Long} documentLineId  报账单计划付款行ID
     * @apiSuccess (返回参数PublicReportLineAmountCO的属性) {Double} paidAmount  已支付金额
     * @apiSuccess (返回参数PublicReportLineAmountCO的属性) {Double} returnAmount  已退款金额
     *

     * @apiSuccess (返回参数CashWriteOffCO的属性) {Long} cshTransactionDetailId  支付明细id
     * @apiSuccess (返回参数CashWriteOffCO的属性) {String} billcode  支付流水号
     * @apiSuccess (返回参数CashWriteOffCO的属性) {String} prepaymentRequisitionTypeDesc  预付款类型描述
     * @apiSuccess (返回参数CashWriteOffCO的属性) {String} prepaymentRequisitionNumber  预付款单据编号
     * @apiSuccess (返回参数CashWriteOffCO的属性) {String} payDate  交易日期(支付时间)
     * @apiSuccess (返回参数CashWriteOffCO的属性) {Double} prepaymentRequisitionAmount  借款金额
     * @apiSuccess (返回参数CashWriteOffCO的属性) {Double} unWriteOffAmount  借款余额(未核销金额)
     * @apiSuccess (返回参数CashWriteOffCO的属性) {Double} writeOffAmount  本次核销金额
     * @apiSuccess (返回参数CashWriteOffCO的属性) {String} writeOffDate  核销日期
     * @apiSuccess (返回参数CashWriteOffCO的属性) {String} periodName  期间
     * @apiSuccess (返回参数CashWriteOffCO的属性) {String} status  状态:N未生效;P已生效;Y:已核算
     * @apiSuccess (返回参数CashWriteOffCO的属性) {Long} tenantId  租户id
     * @apiSuccess (返回参数CashWriteOffCO的属性) {String} operationType  操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
     * @apiSuccess (返回参数CashWriteOffCO的属性) {String} currencyCode  币种代码
     * @apiSuccess (返回参数CashWriteOffCO的属性) {Long} documentHeaderId  单据头ID（报账单头ID）
     * @apiSuccess (返回参数CashWriteOffCO的属性) {Long} documentLineId  单据行ID (计划付款行ID)

     * @apiSuccessExample {json} 返回报文：
     * [
     * {
     * "id": "1002184262879543297",
     * "expReportHeaderId": "1002183650272083970",
     * "scheduleLineNumber": 3,
     * "companyId": "928",
     * "description": "不核销，不付款",
     * "currency": "CNY",
     * "exchangeRate": 1,
     * "amount": 20,
     * "functionalAmount": 20,
     * "writeOffAmount": 0,
     * "schedulePaymentDate": "2018-05-30T16:00:00Z",
     * "paymentMethod": "OFFLINE_PAYMENT",
     * "paymentMethodName": "线下",
     * "cshTransactionClassId": "978869829647757313",
     * "cshTransactionClassName": "普通报销",
     * "cashFlowItemId": null,
     * "cashFlowItemName": null,
     * "payeeCategory": "EMPLOYEE",
     * "payeeCategoryName": "员工",
     * "payeeId": "177606",
     * "payeeCode": null,
     * "payeeName": "Robert",
     * "accountNumber": "620451012384",
     * "accountName": "robert",
     * "bankCode": null,
     * "bankName": null,
     * "frozenFlag": "N",
     * "contractLineId": null,
     * "contractLineAmount": null,
     * "contractHeaderLineMessage": {
     * "headerId": null,
     * "contractNumber": null,
     * "contractName": null,
     * "contractAmount": null,
     * "lineId": null,
     * "lineNumber": null,
     * "lineCurrency": null,
     * "lineAmount": null,
     * "dueDate": null
     * },
     * "paidInfo": {
     * "returnAmount": 0,
     * "paidAmount": 0,
     * "documentLineId": 1002184262879543300
     * },
     * "cashWriteOffMessage": [ ]
     * }
     * ]
     */
    @GetMapping("/payment/schedule/query")
    public ResponseEntity<List<ExpenseReportPaymentScheduleDTO>> getExpensePaymentSchedule(@RequestParam(value = "reportHeaderId") Long reportHeaderId,
                                                                                     Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportPaymentScheduleDTO> expensePaymentScheduleByCond = expenseReportPaymentScheduleService.getExpensePaymentScheduleByCond(reportHeaderId, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(expensePaymentScheduleByCond,totalHeader,HttpStatus.OK);
    }

    /**
     * 删除计划付款行
     * @param id
     */
    /**
     * @api {DELETE} /api/expense/report/payment/schedule/delete/{id} 【报账单】删除计划付款行
     * @apiDescription 删除计划付款行
     * @apiGroup ExpenseReport
     *
     * @apiParam (请求参数){Long} id 计划付款行ID

     * @apiParamExample {json} 请求参数:
    /api/expense/report/payment/schedule/delete/1106225896027717633
     */
    @DeleteMapping("/payment/schedule/delete/{id}")
    public void deleteExpenseReportPaymentSchedule(@PathVariable Long id){
        expenseReportPaymentScheduleService.deleteExpenseReportPaymentSchedule(id);
    }

    /**
     * 根据费用申请单编号查找报账单分摊行信息
     */
    @GetMapping("/getDistfromApplication")
    public ResponseEntity getExpenseReportDistFromApplication(@RequestParam(required = true,value = "documentNumber")String documentNumber,
                                                              @RequestParam(required = false,value ="reportNumber")String reportDocumentNumber,
                                                              @RequestParam(required = false,value = "companyId")Long companyId,
                                                              @RequestParam(required = false,value = "unitId")Long unitId,
                                                              Pageable pageable) {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportDistDTO> list = expenseReportDistService.queryExpenseReportDistFromApplication(page, documentNumber, reportDocumentNumber, companyId, unitId);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);

        return new ResponseEntity<>(list, httpHeaders, HttpStatus.OK);
    }
    /**
     * @api {POST} /api/expense/report/submit 【报账单】提交工作流
     * @apiDescription 提交工作流
     * @apiGroup ExpenseReport
     * @apiParam {List} countersignApproverOIDs 加签审批人OID
     * @apiParam {Long} documentId 单据ID
     * @apiParam {Boolean} ignoreWarningFlag 是否忽略警告
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseEntity<BudgetCheckResultDTO> submit(@RequestBody WorkFlowDocumentRefCO workFlowDocumentRef,
                                                       @RequestParam(value = "ignoreWarningFlag", required = false) Boolean ignoreWarningFlag) {
        return ResponseEntity.ok(expenseReportHeaderService.submit(workFlowDocumentRef,ignoreWarningFlag));
    }

    /**
     * {GET} /api/expense/report/create/accounting
     * @param reportHeaderId
     * @return
     */
    /**
     * @api {POST} /api/expense/report/create/accounting 【报账单】创建凭证
     * @apiDescription 创建凭证
     * @apiGroup ExpenseReport
     * @apiParam {Long} reportHeaderId 报账单头ID
     * @apiParam {String} accountingDate 财务日期
     */
    @PostMapping(value = "/create/accounting")
    public ResponseEntity saveInitializeExpReportGeneralLedgerJournalLine(@RequestParam("reportHeaderId") Long reportHeaderId,
                                                                          @RequestParam("accountingDate") String accountingDate){
        String reuslt = expenseReportHeaderService.saveInitializeExpReportGeneralLedgerJournalLine(reportHeaderId,accountingDate);
        return ResponseEntity.ok(reuslt);
    }
    /**
     * 报账单财务查询
     * * @apiParam (请求参数){Long} [documentTypeId] 报账单类型ID
     *      * @apiParam (请求参数){String} [requisitionDateFrom] 申请日期从(YYYY-MM-DD格式)
     *      * @apiParam (请求参数){String} [requisitionDateTo] 申请日期至(YYYY-MM-DD格式)
     *      * @apiParam (请求参数){Long} [applicantId] 申请人ID
     *      * @apiParam (请求参数){Integer} [status] 状态
     *      * @apiParam (请求参数){String} [currencyCode] 币种
     *      * @apiParam (请求参数){BigDecimal} [amountFrom] 金额从
     *      * @apiParam (请求参数){BigDecimal} [amountTo] 金额至
     *      * @apiParam (请求参数){String} [remark] 备注
     *      * @apiParam (请求参数){int} [page] 页数
     *      * @apiParam (请求参数){int} [size] 每页大小
     */
    @GetMapping("/get/expenseReport/by/query")
    public ResponseEntity<List<ExpenseReportHeaderDTO>> MyExpenseReportsFinanceQuery(
                                                                                    @RequestParam(required = false)Long companyId,
                                                                                    @RequestParam(required = false) Long documentTypeId,
                                                                                    @RequestParam(required = false,value = "applyId") Long applicantId,
                                                                                    @RequestParam(required = false) Integer status,
                                                                                    @RequestParam(required = false)Long unitId,
                                                                                    @RequestParam(required = false,value = "applyDateFrom") String requisitionDateFrom,
                                                                                    @RequestParam(required = false,value = "applyDateTo") String requisitionDateTo,
                                                                                    @RequestParam(required = false,value = "currency") String currencyCode,
                                                                                    @RequestParam(required = false) BigDecimal amountFrom,
                                                                                    @RequestParam(required = false) BigDecimal amountTo,
                                                                                    @RequestParam(required = false) BigDecimal paidAmountFrom,
                                                                                    @RequestParam(required = false) BigDecimal paidAmountTo,
                                                                                    @RequestParam(required = false) String backlashFlag,
                                                                                    @RequestParam(required = false,value = "checkDateFrom") String checkDateFrom,
                                                                                    @RequestParam(required = false,value = "checkDateTo") String checkDateTo,
                                                                                    @RequestParam(required = false) String remark,
                                                                                    @RequestParam(required = false,value = "documentCode") String requisitionNumber,
                                                                                    @RequestParam(required = false)Long tenantId,
                                                                                    Pageable pageable){
        Page page =PageUtil.getPage(pageable);
        ZonedDateTime reqDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom);
        ZonedDateTime reqDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo);
        ZonedDateTime cDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(checkDateFrom);
        ZonedDateTime cDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(checkDateTo);

        //首先在费用模块根据条件全部查询出来， 然后再将符合条件的单据编号单据id输出至支付模块， 再信息返回回来。然后在进行比对筛选
        List<ExpenseReportHeaderDTO> list =expenseReportHeaderService.queryExpenseReportsFinance(
                companyId,
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
    /**
     * 报账单财务查询 (数据权限控制)
     * * @apiParam (请求参数){Long} [documentTypeId] 报账单类型ID
     *      * @apiParam (请求参数){String} [requisitionDateFrom] 申请日期从(YYYY-MM-DD格式)
     *      * @apiParam (请求参数){String} [requisitionDateTo] 申请日期至(YYYY-MM-DD格式)
     *      * @apiParam (请求参数){Long} [applicantId] 申请人ID
     *      * @apiParam (请求参数){Integer} [status] 状态
     *      * @apiParam (请求参数){String} [currencyCode] 币种
     *      * @apiParam (请求参数){BigDecimal} [amountFrom] 金额从
     *      * @apiParam (请求参数){BigDecimal} [amountTo] 金额至
     *      * @apiParam (请求参数){String} [remark] 备注
     *      * @apiParam (请求参数){int} [page] 页数
     *      * @apiParam (请求参数){int} [size] 每页大小
     */
    @GetMapping("/get/expenseReport/by/query/enable/dataAuth")
    public ResponseEntity<List<ExpenseReportHeaderDTO>> MyExpenseReportsFinanceQueryDataAuth(
            @RequestParam(required = false)Long companyId,
            @RequestParam(required = false) Long documentTypeId,
            @RequestParam(required = false,value = "applyId") Long applicantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false)Long unitId,
            @RequestParam(required = false,value = "applyDateFrom") String requisitionDateFrom,
            @RequestParam(required = false,value = "applyDateTo") String requisitionDateTo,
            @RequestParam(required = false,value = "currency") String currencyCode,
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) BigDecimal paidAmountFrom,
            @RequestParam(required = false) BigDecimal paidAmountTo,
            @RequestParam(required = false) String backlashFlag,
            @RequestParam(required = false,value = "checkDateFrom") String checkDateFrom,
            @RequestParam(required = false,value = "checkDateTo") String checkDateTo,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false,value = "documentCode") String requisitionNumber,
            @RequestParam(required = false)Long tenantId,
            Pageable pageable){
        Page page =PageUtil.getPage(pageable);
        ZonedDateTime reqDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom);
        ZonedDateTime reqDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo);
        ZonedDateTime cDateFrom = TypeConversionUtils.getStartTimeForDayYYMMDD(checkDateFrom);
        ZonedDateTime cDateTo = TypeConversionUtils.getEndTimeForDayYYMMDD(checkDateTo);

        //首先在费用模块根据条件全部查询出来， 然后再将符合条件的单据编号单据id输出至支付模块， 再信息返回回来。然后在进行比对筛选
        List<ExpenseReportHeaderDTO> list =expenseReportHeaderService.queryExpenseReportsFinance(
                companyId,
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
    public  void MyExpenseReportsFinanceExport(@RequestParam(required = false)Long companyId,
                                               @RequestParam(required = false) Long documentTypeId,
                                               @RequestParam(required = false,value = "applyId") Long applicantId,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false)Long unitId,
                                               @RequestParam(required = false,value = "applyDateFrom") String requisitionDateFrom,
                                               @RequestParam(required = false,value = "applyDateTo") String requisitionDateTo,
                                               @RequestParam(required = false,value = "currency") String currencyCode,
                                               @RequestParam(required = false) BigDecimal amountFrom,
                                               @RequestParam(required = false) BigDecimal amountTo,
                                               @RequestParam(required = false) BigDecimal paidAmountFrom,
                                               @RequestParam(required = false) BigDecimal paidAmountTo,
                                               @RequestParam(required = false) String backlashFlag,
                                               @RequestParam(required = false,value = "checkDateFrom") String checkDateFrom,
                                               @RequestParam(required = false,value = "checkDateTo") String checkDateTo,
                                               @RequestParam(required = false) String remark,
                                               @RequestParam(required = false,value = "documentCode") String requisitionNumber,
                                               @RequestParam(required = false)Long tenantId,
                                               Pageable pageable,
                                               @RequestBody ExportConfig exportConfig,
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
                                                                            Pageable pageable){
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

    @GetMapping("/header/signReports")
    @ApiOperation(value = "分页查询已扫描过发票袋号码的报账单", notes = "分页查询已扫描过发票袋号码的报账单 开发:张卓")
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
                                                                            Pageable pageable){
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
}
