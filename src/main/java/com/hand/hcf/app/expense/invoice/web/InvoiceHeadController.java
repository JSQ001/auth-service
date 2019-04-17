package com.hand.hcf.app.expense.invoice.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.invoice.domain.InvoiceHead;
import com.hand.hcf.app.expense.invoice.domain.InvoiceLineExpence;
import com.hand.hcf.app.expense.invoice.dto.InvoiceDTO;
import com.hand.hcf.app.expense.invoice.dto.InvoiceLineDistDTO;
import com.hand.hcf.app.expense.invoice.dto.InvoiceLineExpenceWebQueryDTO;
import com.hand.hcf.app.expense.invoice.service.InvoiceHeadService;
import com.hand.hcf.app.core.domain.ExportConfig;
import com.hand.hcf.app.core.handler.ExcelExportHandler;
import com.hand.hcf.app.core.service.ExcelExportService;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    /**
     * @api {POST} /api/invoice/head/insert/invoice 【新建发票头、行】
     * @apiDescription 新建发票头、行
     * @apiGroup InvoiceService
     * @apiParam (请求参数) {InvoiceDTO} invoiceDTO 发票DTO
     * @apiParam (InvoiceDTO的属性) {InvoiceHead} invoiceHead 发票头
     * @apiParam (InvoiceDTO的属性) {List} invoiceLineList 发票行集合
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
     * @apiSuccess (返回参数) {InvoiceDTO} invoiceDTO 发票DTO
     * @apiParamExample {json} 请求参数
     {
    "invoiceHead":{
    "invoiceTypeId":1087374219022475265,
    "tenantId":1083751703623680001,
    "setOfBooksId":1083762150064451585,
    "invoiceDate":"2019-01-23T08:04:00.727Z",
    "invoiceNo":"1234567890123457",
    "invoiceCode":"002",
    "totalAmount":2,
    "invoiceAmount":1,
    "taxTotalAmount":1,
    "currencyCode":"CNY",
    "remark":"hxtest2",
    "createdMethod":"BY_HAND"
    },
    "invoiceLineList":[
    {
    "tenantId":1083751703623680001,
    "setOfBooksId":1083762150064451585,
    "invoiceLineNum":1,
    "detailAmount":1,
    "taxRate":"3%",
    "taxAmount":0.01
    }
    ]
    }
     * @apiSuccessExample {json} 成功返回值
    {
    "invoiceHead": {
    "id": "1088249100002455553",
    "createdDate": "2019-01-24T09:36:18.79+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-01-24T09:36:18.79+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "invoiceTypeId": "1087374219022475265",
    "tenantId": "1083751703623680001",
    "setOfBooksId": "1083762150064451585",
    "invoiceDate": "2019-01-23T08:04:00.727Z",
    "invoiceNo": "1234567890123457",
    "invoiceCode": "002",
    "machineNo": null,
    "checkCode": null,
    "totalAmount": 2,
    "invoiceAmount": 1,
    "taxTotalAmount": 1,
    "currencyCode": "CNY",
    "exchangeRate": 1,
    "remark": "hxtest2",
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
    "createdMethod": "BY_HAND",
    "checkResult": null
    },
    "invoiceLineList": [
    {
    "id": "1088249101327855618",
    "createdDate": "2019-01-24T09:36:19.104+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-01-24T09:36:19.104+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "tenantId": "1083751703623680001",
    "setOfBooksId": "1083762150064451585",
    "invoiceHeadId": "1088249100002455553",
    "invoiceLineNum": 1,
    "goodsName": null,
    "specificationModel": null,
    "unit": null,
    "num": null,
    "unitPrice": null,
    "detailAmount": 1,
    "taxRate": "3%",
    "taxAmount": 0.01,
    "currencyCode": "CNY",
    "exchangeRate": 1
    }
    ]
    }
     */
    @PostMapping("/insert/invoice")
    public ResponseEntity<InvoiceDTO> insertInvoice(@RequestBody InvoiceDTO invoiceDTO){
        return ResponseEntity.ok(invoiceHeadService.insertInvoice(invoiceDTO));
    }

    /**
     * 校验录入的发票代码和号码是否已经在发票头表存在
     * @param invoiceCode 发票代码
     * @param invoiceNo 发票号码
     * @return
     */
    /**
     * @api {GET} /api/invoice/head/check/invoiceCode/invoiceNo 【校验录入的发票代码和号码是否已经在发票头表存在】
     * @apiDescription 校验录入的发票代码和号码是否已经在发票头表存在
     * @apiGroup InvoiceService
     * @apiParam (请求参数) {String} invoiceCode 发票代码
     * @apiParam (请求参数) {String} invoiceNo 发票号码
     * @apiSuccess (返回参数) {String} str 返回success表示通过校验
     * @apiParamExample {json} 请求参数
     * localhost:9095/api/invoice/head/check/invoiceCode/invoiceNo?invoiceCode=demo00001&invoiceNo=12345888
     * @apiSuccessExample {json} 成功返回值
     * success
     */
    @GetMapping("/check/invoiceCode/invoiceNo")
    public ResponseEntity<String> checkInvoiceCodeInvoiceNo(@RequestParam(value = "invoiceCode") String invoiceCode,
                                                      @RequestParam(value = "invoiceNo") String invoiceNo)throws URISyntaxException {
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
    /**
     * /**
     * @api {GET} /api/invoice/head/{id} 【查询一条发票头行信息】
     * @apiDescription 根据发票头id查询发票头、行信息
     * @apiGroup InvoiceService
     * @apiParam (请求参数) {Long} id 发票头id
     * @apiSuccess (返回参数) {InvoiceDTO} invoiceDTO 发票DTO
     * @apiParamExample {json} 请求参数
     * /api/invoice/head/1088276385069854722
     * @apiSuccessExample {json} 成功返回值
     {
    "invoiceHead": {
    "id": "1088276385069854722",
    "createdDate": "2019-01-24T11:24:44.055+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-01-24T11:24:44.055+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "invoiceTypeId": "1087374219022475265",
    "tenantId": "1083751703623680001",
    "setOfBooksId": "1083762150064451585",
    "invoiceDate": "2019-01-23T16:04:00.727+08:00",
    "invoiceNo": "1234567890123457",
    "invoiceCode": "002",
    "machineNo": null,
    "checkCode": null,
    "totalAmount": 2,
    "invoiceAmount": 1,
    "taxTotalAmount": 1,
    "currencyCode": "CNY",
    "exchangeRate": 1,
    "remark": "hxtest2",
    "buyerName": null,
    "buyerTaxNo": null,
    "buyerAddPh": null,
    "buyerAccount": null,
    "salerName": null,
    "salerTaxNo": null,
    "salerAddPh": null,
    "salerAccount": null,
    "cancelFlag": false,
    "redInvoiceFlag": false,
    "createdMethod": "BY_HAND",
    "checkResult": false
    },
    "invoiceLineList": [
    {
    "id": "1088276385296347137",
    "createdDate": "2019-01-24T11:24:44.109+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-01-24T11:24:44.109+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "tenantId": "1083751703623680001",
    "setOfBooksId": "1083762150064451585",
    "invoiceHeadId": "1088276385069854722",
    "invoiceLineNum": 1,
    "goodsName": null,
    "specificationModel": null,
    "unit": null,
    "num": null,
    "unitPrice": null,
    "detailAmount": 1,
    "taxRate": "3%",
    "taxAmount": 0.01,
    "currencyCode": "CNY",
    "exchangeRate": 1
    }
    ]
    }
     */
    @GetMapping("/{id}")
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
    /**
     * @api {GET} /api/invoice/head/query/by/cond 【条件分页查询我的票夹】
     * @apiDescription 根据条件分页查询我的票夹
     * @apiGroup InvoiceService
     * @apiParam (请求参数) {Long} createdBy 用户id
     * @apiParam (请求参数) {Long} invoiceTypeId 发票类型id
     * @apiParam (请求参数) {String} invoiceNo 发票号码
     * @apiParam (请求参数) {String} invoiceCode 发票代码
     * @apiParam (请求参数) {String} invoiceDateFrom 开票日期从
     * @apiParam (请求参数) {String} invoiceDateTo 开票日期至
     * @apiParam (请求参数) {BigDecimal} invoiceAmountFrom 金额合计从
     * @apiParam (请求参数) {BigDecimal} invoiceAmountTo 金额合计至
     * @apiParam (请求参数) {BigDecimal} createdMethod 创建方式
     * @apiParam (请求参数) {Boolean} checkResult 验真状态
     * @apiParam (请求参数) {BigDecimal} reportProgress 报账进度
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiSuccess (返回参数) {List} InvoiceHead 发票头集合
     * @apiParamExample {json} 请求参数
     * /api/invoice/head/query/by/cond?createdBy=1083751705402064897&page=0&size=10
     * @apiSuccessExample {json} 成功返回值
    [
    {
    "id": "1088006000747339777",
    "createdDate": "2019-01-23T17:30:19.412+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-01-23T17:30:19.412+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "invoiceTypeId": "1087374219022475265",
    "tenantId": "1083751703623680001",
    "setOfBooksId": "1083762150064451585",
    "invoiceDate": "2019-01-23T16:04:00.727+08:00",
    "invoiceNo": "1234567890123456",
    "invoiceCode": "001",
    "machineNo": null,
    "checkCode": null,
    "totalAmount": 2,
    "invoiceAmount": 1,
    "taxTotalAmount": 1,
    "currencyCode": "CNY",
    "exchangeRate": 1,
    "remark": "hxtest1",
    "buyerName": null,
    "buyerTaxNo": null,
    "buyerAddPh": null,
    "buyerAccount": null,
    "salerName": null,
    "salerTaxNo": null,
    "salerAddPh": null,
    "salerAccount": null,
    "cancelFlag": false,
    "redInvoiceFlag": false,
    "createdMethod": "BY_HAND",
    "checkResult": false
    },
    {
    "id": "1088249100002455553",
    "createdDate": "2019-01-24T09:36:18.79+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-01-24T09:36:18.79+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "invoiceTypeId": "1087374219022475265",
    "tenantId": "1083751703623680001",
    "setOfBooksId": "1083762150064451585",
    "invoiceDate": "2019-01-23T16:04:00.727+08:00",
    "invoiceNo": "1234567890123457",
    "invoiceCode": "002",
    "machineNo": null,
    "checkCode": null,
    "totalAmount": 2,
    "invoiceAmount": 1,
    "taxTotalAmount": 1,
    "currencyCode": "CNY",
    "exchangeRate": 1,
    "remark": "hxtest2",
    "buyerName": null,
    "buyerTaxNo": null,
    "buyerAddPh": null,
    "buyerAccount": null,
    "salerName": null,
    "salerTaxNo": null,
    "salerAddPh": null,
    "salerAccount": null,
    "cancelFlag": false,
    "redInvoiceFlag": false,
    "createdMethod": "BY_HAND",
    "checkResult": false
    }
    ]
     */
    @GetMapping("/query/by/cond")
    public ResponseEntity<List<InvoiceHead>> getInvoiceHeadByCond(
            @RequestParam(value = "createdBy") Long createdBy,
            @RequestParam(value = "invoiceTypeId",required = false)Long invoiceTypeId,
            @RequestParam(value = "invoiceNo",required = false)String invoiceNo,
            @RequestParam(value = "invoiceCode",required = false)String invoiceCode,
            @RequestParam(value = "invoiceDateFrom",required = false)String invoiceDateFrom,
            @RequestParam(value = "invoiceDateTo",required = false)String invoiceDateTo,
            @RequestParam(value = "invoiceAmountFrom",required = false)BigDecimal invoiceAmountFrom,
            @RequestParam(value = "invoiceAmountTo",required = false)BigDecimal invoiceAmountTo,
            @RequestParam(value = "createdMethod",required = false)String createdMethod,
            @RequestParam(value = "checkResult",required = false)Boolean checkResult,
            @RequestParam(value = "reportProgress",required = false)String reportProgress,
            Pageable pageable)throws URISyntaxException {
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
    /**
     * @api {DELETE} /api/invoice/head/delete/invoice/by/headIds 【批量删除我的发票】
     * @apiDescription 根据发票头id集合批量删除发票(如果返回的集合为空，则说明成功删除；反之有发票已经关联了报账单)
     * @apiGroup InvoiceService
     * @apiParam (请求参数) {List} headIds 发票头id集合
     * @apiSuccess (返回参数) {List} InvoiceHead 发票头集合
     * @apiParamExample {json} 请求参数
     * [1088249100002455553]
     * @apiSuccessExample {json} 成功返回值
     * []
     */
    @DeleteMapping("/delete/invoice/by/headIds")
    public ResponseEntity<List<InvoiceHead>> deleteInvoiceByIds(@RequestBody List<Long> headIds){
        return ResponseEntity.ok(invoiceHeadService.deleteInvoiceByIds(headIds));
    }

    /**
     * 根据发票头id 批量验真发票
     * @param headIds
     * @return
     */
    /**
     * @api {POST} /api/invoice/head/check/invoice/by/headIds 【批量验真我的发票】
     * @apiDescription 根据发票头id集合批量验真发票
     * @apiGroup InvoiceService
     * @apiParam (请求参数) {List} headIds 发票头id集合
     * @apiSuccess (返回参数) {List} InvoiceHead 发票头集合
     * @apiParamExample {json} 请求参数
     * [1088006000747339777]
     */
    @PostMapping("/check/invoice/by/headIds")
    public ResponseEntity checkInvoice(@RequestBody List<Long> headIds){
        invoiceHeadService.checkInvoice(headIds);
        return ResponseEntity.ok().build();
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
    /**
     * @api {GET} /api/invoice/head/query/invoice/line/expense/by/headId 【关联报账单详情分页查询】
     * @apiDescription 关联报账单详情分页查询
     * @apiGroup InvoiceService
     * @apiParam (请求参数) {Long} headId 发票头id
     * @apiParam (请求参数) {String} expenseNum 报账单单号
     * @apiParam (请求参数) {Long} expenseTypeId 报账单类型id
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiSuccess (返回参数) {List} InvoiceLineExpence 发票行报销记录表
     * @apiSuccess (InvoiceLineExpence的属性) {Long} tenantId 租户ID
     * @apiSuccess (InvoiceLineExpence的属性) {Long} setOfBooksId 账套ID
     * @apiSuccess (InvoiceLineExpence的属性) {Long} invoiceLineId 关联发票行ID
     * @apiSuccess (InvoiceLineExpence的属性) {String} invoiceNo 发票号码
     * @apiSuccess (InvoiceLineExpence的属性) {String} invoiceCode 发票代码
     * @apiSuccess (InvoiceLineExpence的属性) {String} goodsName 货物或应税劳务、服务名称
     * @apiSuccess (InvoiceLineExpence的属性) {String} specificationModel 规格型号
     * @apiSuccess (InvoiceLineExpence的属性) {String} unit 单位
     * @apiSuccess (InvoiceLineExpence的属性) {Long} num 数量
     * @apiSuccess (InvoiceLineExpence的属性) {BigDecimal} unitPrice 单价
     * @apiSuccess (InvoiceLineExpence的属性) {BigDecimal} detailAmount 金额
     * @apiSuccess (InvoiceLineExpence的属性) {String} taxRate 税率
     * @apiSuccess (InvoiceLineExpence的属性) {BigDecimal} taxAmount 税额
     * @apiSuccess (InvoiceLineExpence的属性) {String} currencyCode 币种
     * @apiSuccess (InvoiceLineExpence的属性) {BigDecimal} exchangeRate 汇率
     * @apiParamExample {json} 请求参数
     * /api/invoice/head/query/invoice/line/expense/by/headId?headId=1088689956484284417
     */
    @GetMapping("/query/invoice/line/expense/by/headId")
    public ResponseEntity<List<InvoiceLineExpenceWebQueryDTO>> getInvoiceLineExpenceByHeadId(
            @RequestParam("headId")Long headId,
            @RequestParam(value = "expenseNum",required = false)String expenseNum,
            @RequestParam(value = "expenseTypeId",required = false)Long expenseTypeId,
            Pageable pageable)throws URISyntaxException{
        Page page = PageUtil.getPage(pageable);
        Page<InvoiceLineExpenceWebQueryDTO> result = invoiceHeadService.getInvoiceLineExpenceByHeadId(headId, expenseNum, expenseTypeId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + result.getTotal());
        headers.add("Link", "/api/invoice/head/query/invoice/line/expense/by/headId");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }

    /**
     * 发票报账明细分页查询
     * @param createdBy
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
     * @param createdMethod
     * @param checkResult
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/invoice/head/query/invoice/line/dist/by/cond 【发票报账明细分页查询】
     * @apiDescription 发票报账明细分页查询
     * @apiGroup InvoiceService
     * @apiParam (请求参数) {Long} createdBy 当前员工id
     * @apiParam (请求参数) {Long} invoiceTypeId 发票类型id
     * @apiParam (请求参数) {String} invoiceCode 发票代码
     * @apiParam (请求参数) {String} invoiceNo 发票号码
     * @apiParam (请求参数) {String} expenseNum 报账单单号
     * @apiParam (请求参数) {String} invoiceDateFrom 开票日期从
     * @apiParam (请求参数) {String} invoiceDateTo 开票日期至
     * @apiParam (请求参数) {BigDecimal} invoiceAmountFrom 金额合计从
     * @apiParam (请求参数) {BigDecimal} invoiceAmountTo 金额合计至
     * @apiParam (请求参数) {Integer} invoiceLineNumFrom 发票行序号从
     * @apiParam (请求参数) {Integer} invoiceLineNumTo 发票行序号至
     * @apiParam (请求参数) {String} taxRate 税率
     * @apiParam (请求参数) {BigDecimal} taxAmountFrom 税额从
     * @apiParam (请求参数) {BigDecimal} taxAmountTo 税额至
     * @apiParam (请求参数) {String} createdMethod 创建方式
     * @apiParam (请求参数) {Boolean} checkResult 验真状态
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiSuccess (返回参数) {List} InvoiceLineDistDTO 发票分配行表DTO
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} id 发票分配行id
     * @apiSuccess (InvoiceLineDistDTO的属性) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} createdBy 创建人
     * @apiSuccess (InvoiceLineDistDTO的属性) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (InvoiceLineDistDTO的属性) {Integer} versionNumber 版本号
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} tenantId 租户ID
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} setOfBooksId 账套ID
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} invoiceLineId 关联发票行ID
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} invoiceNo 发票号码
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} invoiceCode 发票代码
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} goodsName 货物或应税劳务、服务名称
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} specificationModel 规格型号
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} unit 单位
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} num 数量
     * @apiSuccess (InvoiceLineDistDTO的属性) {BigDecimal} unitPrice 单价
     * @apiSuccess (InvoiceLineDistDTO的属性) {BigDecimal} detailAmount 金额
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} taxRate 税率
     * @apiSuccess (InvoiceLineDistDTO的属性) {BigDecimal} taxAmount 税额
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} currencyCode 币种
     * @apiSuccess (InvoiceLineDistDTO的属性) {BigDecimal} exchangeRate 汇率
     *
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} invoiceTypeId 发票类型id
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} invoiceTypeName 发票类型名称
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} invoiceHeadId 发票头id
     * @apiSuccess (InvoiceLineDistDTO的属性) {ZonedDateTime} invoiceDate 开票日期
     * @apiSuccess (InvoiceLineDistDTO的属性) {BigDecimal} invoiceAmount 金额合计
     * @apiSuccess (InvoiceLineDistDTO的属性) {Integer} invoiceLineNum 发票行号
     * @apiSuccess (InvoiceLineDistDTO的属性) {BigDecimal} lineDetailAmount 发票行金额
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} expenseNum 报账单号
     * @apiSuccess (InvoiceLineDistDTO的属性) {ZonedDateTime} applicationDate 申请日期
     * @apiSuccess (InvoiceLineDistDTO的属性) {Long} applicant 申请人
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} documentState 单据状态
     * @apiSuccess (InvoiceLineDistDTO的属性) {Integer} costLineNumber 费用行号
     * @apiSuccess (InvoiceLineDistDTO的属性) {String} costType 费用类型
     * @apiSuccess (InvoiceLineDistDTO的属性) {BigDecimal} costAmount 费用金额
     * @apiSuccess (InvoiceLineDistDTO的属性) {Boolean} installmentDeduction 分期抵扣
     * @apiParamExample {json} 请求参数
     * /api/invoice/head/query/invoice/line/dist/by/cond?createdBy=1083751705402064897
     * @apiSuccessExample {json} 成功返回值
     [
    {
    "id": "1088665798866968577",
    "tenantId": "1083751703623680001",
    "setOfBooksId": "1083762150064451585",
    "invoiceLineId": "1088665798866968577",
    "invoiceNo": "123456",
    "invoiceCode": "demo0001",
    "goodsName": "demo",
    "specificationModel": "abc-aa",
    "unit": "个",
    "num": "1",
    "unitPrice": 10,
    "detailAmount": 10,
    "taxRate": "5%",
    "taxAmount": 0.5,
    "currencyCode": "CNY",
    "exchangeRate": 1,
    "createdDate": "2019-01-25T13:12:07.54+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-01-25T13:12:07.54+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "invoiceTypeId": "1087516935796224002",
    "invoiceTypeName": "增值税专用发票",
    "invoiceDate": "2019-01-25T13:10:37+08:00",
    "invoiceAmount": 1111,
    "invoiceLineNum": 1,
    "lineDetailAmount": null,
    "expenseNum": null,
    "applicationDate": null,
    "applicant": null,
    "documentState": null,
    "costLineNumber": null,
    "costType": null,
    "costAmount": null,
    "installmentDeduction": null
    }
    ]
     */
    @GetMapping("/query/invoice/line/dist/by/cond")
    public ResponseEntity<List<InvoiceLineDistDTO>> getInvoiceLineDistByCond(
            @RequestParam(value = "invoiceTypeId",required = false)Long invoiceTypeId,
            @RequestParam(value = "invoiceCode",required = false)String invoiceCode,
            @RequestParam(value = "invoiceNo",required = false)String invoiceNo,
            @RequestParam(value = "expenseNum",required = false)String expenseNum,
            @RequestParam(value = "invoiceDateFrom",required = false)String invoiceDateFrom,
            @RequestParam(value = "invoiceDateTo",required = false)String invoiceDateTo,
            @RequestParam(value = "invoiceAmountFrom",required = false)BigDecimal invoiceAmountFrom,
            @RequestParam(value = "invoiceAmountTo",required = false)BigDecimal invoiceAmountTo,
            @RequestParam(value = "invoiceLineNumFrom",required = false)Integer invoiceLineNumFrom,
            @RequestParam(value = "invoiceLineNumTo",required = false)Integer invoiceLineNumTo,
            @RequestParam(value = "taxRate",required = false)String taxRate,
            @RequestParam(value = "taxAmountFrom",required = false)BigDecimal taxAmountFrom,
            @RequestParam(value = "taxAmountTo",required = false)BigDecimal taxAmountTo,
            @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
            @RequestParam(value = "applyDateTo",required = false)String applyDateTo,
            @RequestParam(value = "applicant",required = false)Long applicant,
            @RequestParam(value = "documentStatus",required = false)String documentStatus,
            @RequestParam(value = "costLineNumberFrom",required = false)Long costLineNumberFrom,
            @RequestParam(value = "costLineNumberTo",required = false)Long costLineNumberTo,
            @RequestParam(value = "costType",required = false)String costType,
            @RequestParam(value = "costAmountFrom",required = false)BigDecimal costAmountFrom,
            @RequestParam(value = "costAmountTo",required = false)BigDecimal costAmountTo,
            @RequestParam(value = "installmentDeduction",required = false)Boolean installmentDeduction,
            Pageable pageable)throws URISyntaxException {
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
    public void exportInvoiceHeadInfo(HttpServletRequest request,
                                      @RequestBody ExportConfig exportConfig,
                                      HttpServletResponse response,
                                      @RequestParam(value = "createdBy")Long createdBy,
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
    public void exportInvoiceLineDistInfo(HttpServletRequest request,
                                      @RequestBody ExportConfig exportConfig,
                                      HttpServletResponse response,
                                          @RequestParam(value = "invoiceTypeId",required = false)Long invoiceTypeId,
                                          @RequestParam(value = "invoiceCode",required = false)String invoiceCode,
                                          @RequestParam(value = "invoiceNo",required = false)String invoiceNo,
                                          @RequestParam(value = "expenseNum",required = false)String expenseNum,
                                          @RequestParam(value = "invoiceDateFrom",required = false)String invoiceDateFrom,
                                          @RequestParam(value = "invoiceDateTo",required = false)String invoiceDateTo,
                                          @RequestParam(value = "invoiceAmountFrom",required = false)BigDecimal invoiceAmountFrom,
                                          @RequestParam(value = "invoiceAmountTo",required = false)BigDecimal invoiceAmountTo,
                                          @RequestParam(value = "invoiceLineNumFrom",required = false)Integer invoiceLineNumFrom,
                                          @RequestParam(value = "invoiceLineNumTo",required = false)Integer invoiceLineNumTo,
                                          @RequestParam(value = "taxRate",required = false)String taxRate,
                                          @RequestParam(value = "taxAmountFrom",required = false)BigDecimal taxAmountFrom,
                                          @RequestParam(value = "taxAmountTo",required = false)BigDecimal taxAmountTo,
                                          @RequestParam(value = "applyDateFrom",required = false)String applyDateFrom,
                                          @RequestParam(value = "applyDateTo",required = false)String applyDateTo,
                                          @RequestParam(value = "applicant",required = false)Long applicant,
                                          @RequestParam(value = "documentStatus",required = false)String documentStatus,
                                          @RequestParam(value = "costLineNumberFrom",required = false)Long costLineNumberFrom,
                                          @RequestParam(value = "costLineNumberTo",required = false)Long costLineNumberTo,
                                          @RequestParam(value = "costType",required = false)String costType,
                                          @RequestParam(value = "costAmountFrom",required = false)BigDecimal costAmountFrom,
                                          @RequestParam(value = "costAmountTo",required = false)BigDecimal costAmountTo,
                                          @RequestParam(value = "installmentDeduction",required = false)Boolean installmentDeduction,
                                      Pageable pageable) throws IOException {
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
    /**
     * @api {GET} api/invoice/head/query/invoice/all/by/cond
     * @apiDescription  根据条件查询所有发票头行
     * @apiGroup InvoiceService
     * @apiParam {Long} createdBy 创建人
     * @apiParam {String} invoiceCode 发票代码
     * @apiParam {String} invoiceNo 发票号码
     * @apiParam {String} invoiceDateFrom 开票日期从
     * @apiParam {String} invoiceDateTo 开票日期至
     * @apiParam {String} salerName 销方名称
     * @apiParam {String} currencyCode 币种
     * @apiParamExample {json} Request-Param:
     *  http://127.0.0.1:9095/expense/api/invoice/head/query/invoice/all/by/cond
     * @apiSuccessExample {json} Success-Response:
     * [
        {
        "invoiceHead": {
        "id": "1088665798825025537",
        "createdDate": "2019-01-25T13:12:07.53+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-25T14:17:56.713+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 2,
        "invoiceTypeId": "1087516935796224002",
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "invoiceDate": "2019-01-25T13:10:37+08:00",
        "invoiceNo": "123456",
        "invoiceCode": "demo0001",
        "machineNo": "1234",
        "checkCode": "123456",
        "totalAmount": 1234,
        "invoiceAmount": 1111,
        "taxTotalAmount": 123,
        "currencyCode": "CNY",
        "exchangeRate": 1,
        "remark": "1234",
        "buyerName": "张三",
        "buyerTaxNo": "12666",
        "buyerAddPh": "10000",
        "buyerAccount": "123456",
        "salerName": "销售",
        "salerTaxNo": "1234",
        "salerAddPh": "10086",
        "salerAccount": "123666",
        "cancelFlag": false,
        "redInvoiceFlag": false,
        "createdMethod": "BY_HAND",
        "checkResult": true,
        "stringInvoiceDate": null,
        "stringCheckResult": null,
        "invoiceTypeName": "增值税专用发票",
        "createdMethodName": null,
        "reportProgress": null,
        "reportProgressName": null
        },
        "invoiceLineList": [
        {
        "id": "1088665798866968577",
        "createdDate": "2019-01-25T13:12:07.54+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-25T13:12:07.54+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "invoiceHeadId": "1088665798825025537",
        "invoiceLineNum": 1,
        "goodsName": "demo",
        "specificationModel": "abc-aa",
        "unit": "个",
        "num": "1",
        "unitPrice": 10,
        "detailAmount": 10,
        "taxRate": "5%",
        "taxAmount": 0.5,
        "currencyCode": "CNY",
        "exchangeRate": 1
        }
        ]
        }
     ]
     */
    @GetMapping("/query/invoice/all/by/cond")
    public ResponseEntity pageInvoiceByCond(@RequestParam(value = "createdBy",required = false) Long createdBy,
                                        @RequestParam(value = "invoiceCode",required = false) String invoiceCode,
                                        @RequestParam(value = "invoiceNo",required = false) String invoiceNo,
                                        @RequestParam(value = "invoiceDateFrom",required = false)String invoiceDateFrom,
                                        @RequestParam(value = "invoiceDateTo",required = false)String invoiceDateTo,
                                        @RequestParam(value = "",required = false) String salerName,
                                        @RequestParam(value = "currencyCode",required = false) String currencyCode,
                                        @RequestParam(value = "page",defaultValue = "0") int page,
                                        @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<InvoiceHead> invoiceDTOS = invoiceHeadService.pageInvoiceByCond(createdBy,invoiceCode,invoiceNo,invoiceDateFrom,invoiceDateTo,currencyCode,salerName,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(invoiceDTOS,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {POST}  /api/invoice/head/check/invoice
     * @apiDescription  手工录入发票只校验不保存接口 （我的账本录入发票-手工录入-保存）
     * @apiGroup InvoiceService
     * @apiParam (请求参数) {InvoiceDTO} invoiceDTO 发票DTO
     * @apiParam (InvoiceDTO的属性) {InvoiceHead} invoiceHead 发票头
     * @apiParam (InvoiceDTO的属性) {List} invoiceLineList 发票行集合
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
     * @apiParam (InvoiceHead的属性) {Boolean} fromBook 是否来源于账本
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
     * @apiSuccess (返回参数) {InvoiceDTO} invoiceDTO 发票DTO
     * @apiParamExample {json} 请求参数
    {
        "invoiceHead":{
        "invoiceTypeId":1087374219022475265,
        "tenantId":1083751703623680001,
        "setOfBooksId":1083762150064451585,
        "invoiceDate":"2019-01-23T08:04:00.727Z",
        "invoiceNo":"1234567890123457",
        "invoiceCode":"002",
        "totalAmount":2,
        "invoiceAmount":1,
        "taxTotalAmount":1,
        "currencyCode":"CNY",
        "remark":"hxtest2",
        "createdMethod":"BY_HAND"
        },
        "invoiceLineList":[
        {
        "tenantId":1083751703623680001,
        "setOfBooksId":1083762150064451585,
        "invoiceLineNum":1,
        "detailAmount":1,
        "taxRate":"3%",
        "taxAmount":0.01
        }
        ]
    }
     * @apiSuccessExample {json} 成功返回值
    {
        "invoiceHead": {
        "id": "1088249100002455553",
        "createdDate": "2019-01-24T09:36:18.79+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-24T09:36:18.79+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "invoiceTypeId": "1087374219022475265",
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "invoiceDate": "2019-01-23T08:04:00.727Z",
        "invoiceNo": "1234567890123457",
        "invoiceCode": "002",
        "machineNo": null,
        "checkCode": null,
        "totalAmount": 2,
        "invoiceAmount": 1,
        "taxTotalAmount": 1,
        "currencyCode": "CNY",
        "exchangeRate": 1,
        "remark": "hxtest2",
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
        "createdMethod": "BY_HAND",
        "checkResult": null
        },
        "invoiceLineList": [
        {
        "id": "1088249101327855618",
        "createdDate": "2019-01-24T09:36:19.104+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-24T09:36:19.104+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "invoiceHeadId": "1088249100002455553",
        "invoiceLineNum": 1,
        "goodsName": null,
        "specificationModel": null,
        "unit": null,
        "num": null,
        "unitPrice": null,
        "detailAmount": 1,
        "taxRate": "3%",
        "taxAmount": 0.01,
        "currencyCode": "CNY",
        "exchangeRate": 1
        }
        ]
     }
     */
    @PostMapping("/check/invoice")
    public ResponseEntity<InvoiceDTO> checkInvoice(@RequestBody InvoiceDTO invoiceDTO){
        return ResponseEntity.ok(invoiceHeadService.checkInvoice(invoiceDTO));
    }
}
