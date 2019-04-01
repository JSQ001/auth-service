package com.hand.hcf.app.expense.book.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.expense.book.domain.ExpenseBook;
import com.hand.hcf.app.expense.book.service.ExpenseBookService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description  我的账本
 * @date 2019/2/21 14:17
 * @version: 1.0.0
 */
@RestController
@RequestMapping("/api/expense/book")
public class ExpenseBookController {

    @Autowired
    private ExpenseBookService expenseBookService;
    /**
     * @api {POST}  /api/expense/book 新增我的账本
     * @apiDescription 新增我的账本
     * @apiGroup ExpenseBook
     * @apiParam {Long}  expenseTypeId 费用类型ID
     * @apiParam {Long} invoiceLineIdList 发票行id
     * @apiParam {Long} tenantId 租户id
     * @apiParam {Long} setOfBooksId 账套id
     * @apiParam {String} expenseDate 发生时间
     * @apiParam {String} currencyCode 币种
     * @apiParam {BigDecimal} exchangeRate 汇率
     * @apiParam {BigDecimal} amount 金额
     * @apiParam {BigDecimal} functionalAmount 本位币
     * @apiParam {String} quantity 数量
     * @apiParam {BigDecimal} price 单价
     * @apiParam {String} priceUnit 单位
     * @apiParam {String} remarks 备注
     * @apiParam {String} attachmentOid 附件Oid
     * @apiParamExample {json} Request-Param:
     * {
        "expenseDate": "2019-03-11T11:11:48.692Z",
        "currencyCode": "CNY",
        "amount": "88.00",
        "remarks": "测试",
        "attachmentOid": "",
        "expenseTypeId": "1105038436836556801",
        "invoiceLineIdList": [
        "1100361956605018113","1088665798866968577"
        ],
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "exchangeRate": 1,
        "functionalAmount": 1,
        "fields": [ ],
        "price": "88.00",
        "quantity": 1
    }
     * @apiSuccessExample {json} Success-Response:
     * {
        "id": "1105063819392106497",
        "createdDate": null,
        "createdBy": null,
        "lastUpdatedDate": "2019-03-11T19:45:17.823+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": null,
        "expenseTypeId": "1105038436836556801",
        "expenseTypeName": null,
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "expenseDate": "2019-03-11T11:11:48.692Z",
        "currencyCode": "CNY",
        "exchangeRate": 1,
        "amount": 88,
        "functionalAmount": 88,
        "quantity": "1",
        "price": 88,
        "priceUnit": null,
        "remarks": "测试",
        "attachmentOid": "",
        "invoiceLineIdList": [
        "1100361956605018113",
        "1088665798866968577"
        ],
        "invoiceMethod": null,
        "invoiceHead": null,
        "fields": [],
        "attachments": null
        }
     */
    @PostMapping
    public ResponseEntity insertExpenseBook(@RequestBody ExpenseBook expenseBook){
        return ResponseEntity.ok(expenseBookService.insertExpenseBook(expenseBook));
    }
    
    /**
     * @api {PUT}   /api/expense/book 修改我的账本
     * @apiDescription 修改我的账本
     * @apiGroup ExpenseBook
     * @apiParam {Long} id 账本id
     * @apiParam {Long}  expenseTypeId 费用类型ID
     * @apiParam {Long} invoiceLineIdList 发票行id
     * @apiParam {Long} tenantId 租户id
     * @apiParam {Long} setOfBooksId 账套id
     * @apiParam {String} expenseDate 发生时间
     * @apiParam {String} currencyCode 币种
     * @apiParam {BigDecimal} exchangeRate 汇率
     * @apiParam {BigDecimal} amount 金额
     * @apiParam {BigDecimal} functionalAmount 本位币
     * @apiParam {String} quantity 数量
     * @apiParam {BigDecimal} price 单价
     * @apiParam {String} priceUnit 单位
     * @apiParam {String} remarks 备注
     * @apiParam {String} attachmentOid 附件Oid
     * @apiParamExample {json} Request-Param:
     * {   "id":"1105063819392106497",
        "expenseDate": "2019-03-11T11:11:48.692Z",
        "currencyCode": "CNY",
        "amount": "88.00",
        "remarks": "测试",
        "attachmentOid": "",
        "expenseTypeId": "1105038436836556801",
        "invoiceLineIdList": [
        "1100361956605018113","1088665798866968577"
        ],
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "exchangeRate": 1,
        "functionalAmount": 1,
        "fields": [ ],
        "price": "88.00",
        "quantity": 1
        }
         * @apiSuccessExample {json} Success-Response:
         * {
        "id": "1105063819392106497",
        "createdDate": null,
        "createdBy": null,
        "lastUpdatedDate": "2019-03-11T19:45:17.823+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": null,
        "expenseTypeId": "1105038436836556801",
        "expenseTypeName": null,
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "expenseDate": "2019-03-11T11:11:48.692Z",
        "currencyCode": "CNY",
        "exchangeRate": 1,
        "amount": 88,
        "functionalAmount": 88,
        "quantity": "1",
        "price": 88,
        "priceUnit": null,
        "remarks": "测试",
        "attachmentOid": "",
        "invoiceLineIdList": [
        "1100361956605018113",
        "1088665798866968577"
        ],
        "invoiceMethod": null,
        "invoiceHead": null,
        "fields": [],
        "attachments": null
    }
     */
    @PutMapping
    public ResponseEntity updateExpenseBook(@RequestBody ExpenseBook expenseBook){
        return ResponseEntity.ok(expenseBookService.updateExpenseBook(expenseBook));
    }
    /**
     * @api {GET}  /api/expense/book/query 查询我的账本
     * @apiDescription 分页查询我的账本
     * @apiGroup ExpenseBook
     * @apiParam {Long} expenseTypeId 费用类型ID
     * @apiParam {String} dateFrom 发生日期从
     * @apiParam {String} dateTo 发生日期至于
     * @apiParam {BigDecimal} amountFrom 金额从
     * @apiParam {BigDecimal} amountTo 金额至
     * @apiParam {String} currencyCode 币种
     * @apiParamExample {json} Request-Param:
     *   http://127.0.0.1:9095/expense/api/expense/book/query
     * @apiSuccessExample {json} Success-Response:
     * [
        {
        "id": "1098773613995212802",
        "createdDate": "2019-02-22T10:36:58.465+08:00",
        "createdBy": "1085713587144720385",
        "lastUpdatedDate": "2019-02-22T10:36:58.466+08:00",
        "lastUpdatedBy": "1085713587144720385",
        "versionNumber": 1,
        "expenseTypeId": "1082451581907279874",
        "expenseTypeName": "测试申请",
        "invoiceId": "1088665798825025537",
        "tenantId": "1085713586410717186",
        "setOfBooksId": "1085717261577322498",
        "expenseDate": "2019-03-21T10:42:03.904+08:00",
        "currencyCode": "CNY",
        "exchangeRate": 1,
        "amount": 1,
        "functionalAmount": 1,
        "quantity": "1",
        "price": 1,
        "priceUnit": "1",
        "remarks": "测试",
        "attachmentOid": null
        }
        ]
     */
    @GetMapping("/query")
    public ResponseEntity pageExpenseBookByCond(@RequestParam(value = "expenseTypeId",required = false) Long expenseTypeId,
                                                @RequestParam(value = "dateFrom",required = false) String dateFrom,
                                                @RequestParam(value = "dateTo", required = false) String dateTo,
                                                @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                                @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                                @RequestParam(value = "currencyCode",required = false) String currencyCode,
                                                @RequestParam(value = "page",defaultValue = "0") int page,
                                                @RequestParam(value = "size",defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ExpenseBook> expenseBookList = expenseBookService.pageExpenseBookByCond(expenseTypeId, dateFrom, dateTo, amountFrom,amountTo,currencyCode,null,null,queryPage);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(expenseBookList,httpHeaders, HttpStatus.OK);
    }
    /**
     * @api {GET}  /api/expense/book/{id}  获取我的账本
     * @apiDescription  获取我的账本
     * @apiGroup ExpenseBook
     * @apiParam {Long} id 账本Id
     * @apiParamExample {json} Request-Param:
     *  http://127.0.0.1:9095/expense/api/expense/book/1098773613995212802
     * @apiSuccessExample {json} Success-Response:
     * {
        "id": "1098773613995212802",
        "createdDate": "2019-02-22T10:36:58.465+08:00",
        "createdBy": "1085713587144720385",
        "lastUpdatedDate": "2019-02-22T10:36:58.466+08:00",
        "lastUpdatedBy": "1085713587144720385",
        "versionNumber": 1,
        "expenseTypeId": "1082451581907279874",
        "expenseTypeName": null,
        "invoiceId": null,
        "tenantId": "1085713586410717186",
        "setOfBooksId": "1085717261577322498",
        "expenseDate": "2019-03-21T10:42:03.904+08:00",
        "currencyCode": "CNY",
        "exchangeRate": 1,
        "amount": 1,
        "functionalAmount": 1,
        "quantity": "1",
        "price": 1,
        "priceUnit": "1",
        "remarks": "测试",
        "attachmentOid": null
        }
     */
    @GetMapping("/{id}")
    public ResponseEntity getExpenseBookById(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(expenseBookService.selectById(id));
    }


    /**
     * @api {DELETE}   /api/expense/book 删除账本关联
     * @apiDescription 删除账本关联
     * @apiGroup ExpenseBook
     * @apiParam {Long} expenseBookId 账本Id
     * @apiParam {Long} invoiceLineId 发票行Id
     * @apiParam {Long} invoiceHeadId 发票头Id
     * @apiParamExample {json} Request-Param:
     *    http://127.0.0.1:9095/api/expense/book?expenseBookId =121313424&invoiceHeadId=213134&invoiceLineId=2131313
     * @apiSuccess {Boolean} success 是否成功
     */
    @DeleteMapping
    public ResponseEntity deleteExpenseBook(@RequestParam(value = "expenseBookId",required = false) Long expenseBookId,
                                            @RequestParam("invoiceHeadId") Long invoiceHeadId,
                                            @RequestParam("invoiceLineId") Long invoiceLineId){
        return ResponseEntity.ok(expenseBookService.deleteExpenseBook(expenseBookId, invoiceHeadId, invoiceLineId ));
    }

    /**
     * @api {GET}  /api/expense/book/release 从账本导入费用
     * @apiDescription 从账本导入费用
     * @apiGroup ExpenseReport
     * @apiParam {Long} expenseReportTypeId 报账单类型ID
     * @apiParam {String} currencyCode 币种
     * @apiParam {Long} [expenseTypeId] 费用类型ID
     * @apiParam {String} [requisitionDateFrom] 发生日期从
     * @apiParam {String} [requisitionDateTo] 发生日期至于
     * @apiParam {String} [remarks] 备注
     * @apiParam {BigDecimal} [amountFrom] 金额从
     * @apiParam {BigDecimal} [amountTo] 金额至
     * @a/api/expense/book/release
     * @apiSuccessExample {json} Success-Response:
     * [
    {
    "id": "1098773613995212802",
    "createdDate": "2019-02-22T10:36:58.465+08:00",
    "createdBy": "1085713587144720385",
    "lastUpdatedDate": "2019-02-22T10:36:58.466+08:00",
    "lastUpdatedBy": "1085713587144720385",
    "versionNumber": 1,
    "expenseTypeId": "1082451581907279874",
    "expenseTypeName": "测试申请",
    "invoiceId": "1088665798825025537",
    "tenantId": "1085713586410717186",
    "setOfBooksId": "1085717261577322498",
    "expenseDate": "2019-03-21T10:42:03.904+08:00",
    "currencyCode": "CNY",
    "exchangeRate": 1,
    "amount": 1,
    "functionalAmount": 1,
    "quantity": "1",
    "price": 1,
    "priceUnit": "1",
    "remarks": "测试",
    "attachmentOid": null
    }
    ]
     */
    @GetMapping("/release")
    public ResponseEntity getOwnExpenseBooks(@RequestParam("expenseReportTypeId") Long expenseReportTypeId,
                                             @RequestParam("currencyCode") String currencyCode,
                                             @RequestParam(required = false) Long expenseTypeId,
                                             @RequestParam(required = false) String requisitionDateFrom,
                                             @RequestParam(required = false) String requisitionDateTo,
                                             @RequestParam(required = false) String remarks,
                                             @RequestParam(required = false) BigDecimal amountFrom,
                                             @RequestParam(required = false) BigDecimal amountTo,
                                             Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseBook> expenseBookList = expenseBookService.pageExpenseBookByCond(expenseTypeId, requisitionDateFrom, requisitionDateTo, amountFrom,amountTo,currencyCode,remarks,expenseReportTypeId,page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(expenseBookList,totalHeader,HttpStatus.OK);
    }
}
