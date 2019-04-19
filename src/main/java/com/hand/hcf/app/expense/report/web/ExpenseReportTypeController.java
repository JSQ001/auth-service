package com.hand.hcf.app.expense.report.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CashTransactionClassCO;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.expense.report.domain.ExpenseReportType;
import com.hand.hcf.app.expense.report.dto.ExpenseReportTypeDTO;
import com.hand.hcf.app.expense.report.dto.ExpenseReportTypeRequestDTO;
import com.hand.hcf.app.expense.report.service.ExpenseReportTypeService;
import com.hand.hcf.app.expense.type.domain.ExpenseDimension;
import com.hand.hcf.app.expense.type.domain.ExpenseType;
import com.hand.hcf.app.expense.type.web.dto.ExpenseTypeWebDTO;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/2/22
 */
/**
 * @apiDefine ReportTypeService 报账单类型
 */
@RestController
@RequestMapping("/api/expense/report/type")
public class ExpenseReportTypeController {
    private final ExpenseReportTypeService expenseReportTypeService;

    public ExpenseReportTypeController(ExpenseReportTypeService expenseReportTypeService){
        this.expenseReportTypeService = expenseReportTypeService;
    }

    /**
     * 新增 报账单类型
     *
     * @param expenseReportTypeRequestDTO
     * @return
     */
    /**
     * @api {POST} /api/expense/report/type 【报账单类型】 新增
     * @apiDescription 新增 报账单类型
     * @apiGroup ReportTypeService
     * @apiParam (请求参数) {ExpenseReportTypeRequestDTO} expenseReportTypeRequestDTO 报账单类型DTO
     * @apiParam (ExpenseReportTypeRequestDTO) {ExpenseReportType} expenseReportType 报账单类型
     * @apiParam (ExpenseReportTypeRequestDTO) {List(Long)} expenseTypeIdList 关联费用类型id集合
     * @apiParam (ExpenseReportTypeRequestDTO) {List(Long)} cashTransactionClassIdList 关联付款用途id集合
     * @apiParam (ExpenseReportTypeRequestDTO) {List(Long)} departmentOrUserGroupIdList 关联部门或人员组id集合
     * @apiParam (ExpenseReportTypeRequestDTO) {List(DepartmentOrUserGroupDTO)} departmentOrUserGroupList 关联部门或人员组的对象集合
     * @apiParam (ExpenseReportType) {Long} tenantId  租户ID
     * @apiParam (ExpenseReportType) {Long} setOfBooksId  账套ID
     * @apiParam (ExpenseReportType) {String} reportTypeCode 报账单类型代码
     * @apiParam (ExpenseReportType) {String} reportTypeName 报账单类型名称
     * @apiParam (ExpenseReportType) {Long} formId  关联表单ID
     * @apiParam (ExpenseReportType) {Long} formType  关联表单类型
     * @apiParam (ExpenseReportType) {Boolean} enabled 是否启用
     * @apiParam (ExpenseReportType) {Boolean} allExpenseFlag 是否关联全部费用类型标志(全部类型:1;部分类型:0)
     * @apiParam (ExpenseReportType) {Boolean} budgetFlag 预算管控标志(启用:Y;不启用:N)
     * @apiParam (ExpenseReportType) {String} applicationFormBasis 关联申请单依据
     * @apiParam (ExpenseReportType) {Boolean} associateContract 关联合同标志(可关联:Y;不可关联:N)
     * @apiParam (ExpenseReportType) {Boolean} [contractRequired] 合同必输标志(必输:Y;非必输:N)
     * @apiParam (ExpenseReportType) {Boolean} multiPayee 多收款方标志(多收款方:Y;单一收款方:N)
     * @apiParam (ExpenseReportType) {String} payeeType 收款方属性
     * @apiParam (ExpenseReportType) {Boolean} allCashTransactionClass 是否全部付款用途标志(全部类型:1;部分类型:0)
     * @apiParam (ExpenseReportType) {String} paymentMethod 付款方式类型
     * @apiParam (ExpenseReportType) {Boolean} writeOffApplication 核销依据:是否关联相同申请单(是:Y;否:N)
     * @apiParam (ExpenseReportType) {Boolean} writeOffContract 核销依据:是否关联相同合同(是:Y;否:N)
     * @apiParam (ExpenseReportType) {String} applyEmployee 适用人员("1001":全部;"1002";部门;"1003":人员组)
     * @apiParam (DepartmentOrUserGroupDTO) {Long} id 部门或人员组id
     * @apiParam (DepartmentOrUserGroupDTO) {String} name 部门path 或 人员组name
     * @apiSuccess (返回参数) {ExpenseReportType} expenseReportType 报账单类型
     * @apiParamExample {json} 请求参数:
    {
    "expenseReportType":{
    "tenantId":1085713586410717186,
    "setOfBooksId":1085717261577322498,
    "reportTypeCode":"hxtest1",
    "reportTypeName":"hanxuetest1",
    "i18n":{
    "reportTypeName":[
    {
    "language":"zh_cn",
    "value":"hanxuetest1"
    },
    {
    "language":"en_us",
    "value":"hxtest1"
    }
    ]
    },
    "formId":111,
    "formType":888,
    "enabled":true,
    "allExpenseFlag":true,
    "budgetFlag":false,
    "applicationFormBasis":"HEADER_COM",
    "associateContract":true,
    "contractRequired":false,
    "multiPayee":false,
    "payeeType":"EMPLOYEE",
    "allCashTransactionClass":true,
    "paymentMethod":"ONLINE_PAYMENT",
    "writeOffApplication":false,
    "writeOffContract":false,
    "applyEmployee":"1001"
    },
    "expenseTypeIdList":[],
    "cashTransactionClassIdList":[],
    "departmentOrUserGroupIdList":[]
    }
     * @apiSuccessExample {json} 成功返回值:
    {
    "i18n": {
    "reportTypeName": [
    {
    "language": "zh_cn",
    "value": "hanxuetest1"
    },
    {
    "language": "en_us",
    "value": "hxtest1"
    }
    ]
    },
    "id": "1100287347849232386",
    "deleted": false,
    "createdDate": "2019-02-26T14:52:00.74+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-02-26T14:52:00.741+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "enabled": true,
    "tenantId": "1085713586410717186",
    "setOfBooksId": "1085717261577322498",
    "reportTypeCode": "hxtest1",
    "reportTypeName": "hanxuetest1",
    "formId": "111",
    "formType": 888,
    "allExpenseFlag": true,
    "budgetFlag": false,
    "applicationFormBasis": "HEADER_COM",
    "associateContract": true,
    "contractRequired": false,
    "multiPayee": false,
    "payeeType": "EMPLOYEE",
    "allCashTransactionClass": true,
    "paymentMethod": "ONLINE_PAYMENT",
    "writeOffApplication": false,
    "writeOffContract": false,
    "applyEmployee": "1001",
    "setOfBooksCode": null,
    "setOfBooksName": null,
    "paymentMethodName": null
    }
     */
    @PostMapping
    public ResponseEntity<ExpenseReportType> createExpenseReportType(@RequestBody @NotNull ExpenseReportTypeRequestDTO expenseReportTypeRequestDTO){
        return ResponseEntity.ok(expenseReportTypeService.createExpenseReportType(expenseReportTypeRequestDTO));
    }

    /**
     * 修改 报账单类型
     *
     * @param expenseReportTypeRequestDTO
     * @return
     */
    /**
     * @api {PUT} /api/expense/report/type 【报账单类型】 修改
     * @apiDescription 修改 报账单类型
     * @apiGroup ReportTypeService
     * @apiParam (请求参数) {ExpenseReportTypeRequestDTO} expenseReportTypeRequestDTO 报账单类型
     * @apiParam (ExpenseReportTypeRequestDTO) {ExpenseReportType} expenseReportType 报账单类型
     * @apiParam (ExpenseReportTypeRequestDTO) {List(Long)} expenseTypeIdList 关联费用类型id集合
     * @apiParam (ExpenseReportTypeRequestDTO) {List(Long)} cashTransactionClassIdList 关联付款用途id集合
     * @apiParam (ExpenseReportTypeRequestDTO) {List(Long)} departmentOrUserGroupIdList 关联部门或人员组id集合
     * @apiParam (ExpenseReportTypeRequestDTO) {List(DepartmentOrUserGroupDTO)} departmentOrUserGroupList 关联部门或人员组的对象集合
     * @apiSuccess (返回参数) {ExpenseReportType} expenseReportType 报账单类型
     * @apiParamExample {json} 请求参数:
    "expenseReportType":{
    "id":1100287347849232386,
    "tenantId":1085713586410717186,
    "setOfBooksId":1085717261577322498,
    "reportTypeCode":"hxtest1",
    "reportTypeName":"hanxuetest1update",
    "i18n":{
    "reportTypeName":[
    {
    "language":"zh_cn",
    "value":"hanxuetest1update"
    },
    {
    "language":"en_us",
    "value":"hxtest1update"
    }
    ]
    },
    "formId":111,
    "formType":888,
    "enabled":true,
    "allExpenseFlag":true,
    "budgetFlag":false,
    "applicationFormBasis":"HEADER_COM",
    "associateContract":true,
    "contractRequired":false,
    "multiPayee":false,
    "payeeType":"EMPLOYEE",
    "allCashTransactionClass":true,
    "paymentMethod":"ONLINE_PAYMENT",
    "writeOffApplication":false,
    "writeOffContract":false,
    "applyEmployee":"1001",
    "versionNumber":1
    },
    "expenseTypeIdList":[],
    "cashTransactionClassIdList":[],
    "departmentOrUserGroupIdList":[]
    }
     * @apiSuccessExample {json} 成功返回值:
    {
    "i18n": {
    "reportTypeName": [
    {
    "language": "zh_cn",
    "value": "hanxuetest1update"
    },
    {
    "language": "en_us",
    "value": "hxtest1update"
    }
    ]
    },
    "id": "1100287347849232386",
    "deleted": null,
    "createdDate": null,
    "createdBy": null,
    "lastUpdatedDate": null,
    "lastUpdatedBy": null,
    "versionNumber": 2,
    "enabled": true,
    "tenantId": "1085713586410717186",
    "setOfBooksId": "1085717261577322498",
    "reportTypeCode": "hxtest1",
    "reportTypeName": "hanxuetest1update",
    "formId": "111",
    "formType": 888,
    "allExpenseFlag": true,
    "budgetFlag": false,
    "applicationFormBasis": "HEADER_COM",
    "associateContract": true,
    "contractRequired": false,
    "multiPayee": false,
    "payeeType": "EMPLOYEE",
    "allCashTransactionClass": true,
    "paymentMethod": "ONLINE_PAYMENT",
    "writeOffApplication": false,
    "writeOffContract": false,
    "applyEmployee": "1001",
    "setOfBooksCode": null,
    "setOfBooksName": null,
    "paymentMethodName": null
    }
     */
    @PutMapping
    public ResponseEntity<ExpenseReportType> updateExpenseReportType(@RequestBody ExpenseReportTypeRequestDTO expenseReportTypeRequestDTO){
        return ResponseEntity.ok(expenseReportTypeService.updateExpenseReportType(expenseReportTypeRequestDTO));
    }

    /**
     * 根据ID查询 报账单类型
     *
     * @param id
     * @return
     */
    /**
     * @api {GET} /api/expense/report/type/1100287347849232386 【报账单类型】 根据ID查询
     * @apiDescription 根据ID查询 报账单类型
     * @apiGroup ReportTypeService
     * @apiParam (请求参数) {Long} id 报账单类型ID
     * @apiSuccess (返回参数) {ExpenseReportTypeRequestDTO} expenseReportTypeRequestDTO 报账单类型
     * @apiSuccessExample {json} 成功返回值:
    {
    "expenseReportType": {
    "i18n": {
    "reportTypeName": [
    {
    "language": "en_us",
    "value": "hxtest1update"
    },
    {
    "language": "zh_cn",
    "value": "hanxuetest1update"
    }
    ]
    },
    "id": "1100287347849232386",
    "deleted": false,
    "createdDate": "2019-02-26T14:52:00.74+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-02-26T15:48:43.808+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 2,
    "enabled": true,
    "tenantId": "1085713586410717186",
    "setOfBooksId": "1085717261577322498",
    "reportTypeCode": "hxtest1",
    "reportTypeName": "hanxuetest1update",
    "formId": "111",
    "formType": 888,
    "allExpenseFlag": true,
    "budgetFlag": false,
    "applicationFormBasis": "HEADER_COM",
    "associateContract": true,
    "contractRequired": false,
    "multiPayee": false,
    "payeeType": "EMPLOYEE",
    "allCashTransactionClass": true,
    "paymentMethod": "ONLINE_PAYMENT",
    "writeOffApplication": false,
    "writeOffContract": false,
    "applyEmployee": "1001",
    "setOfBooksCode": "HAND_SOB001",
    "setOfBooksName": "融晶账套001",
    "paymentMethodName": "线上"
    },
    "expenseTypeIdList": null,
    "cashTransactionClassIdList": null,
    "departmentOrUserGroupIdList": null,
    "departmentOrUserGroupList": null
    }
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseReportTypeRequestDTO> getExpenseReportType(@PathVariable Long id){
        return ResponseEntity.ok(expenseReportTypeService.getExpenseReportType(id));
    }

    /**
     * 自定义条件查询 报账单类型(分页)
     *
     * @param setOfBooksId
     * @param reportTypeCode
     * @param reportTypeName
     * @param enabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/expense/report/type/query 【报账单类型】 根据条件查询
     * @apiDescription 自定义条件分页查询 报账单类型
     * @apiGroup ReportTypeService
     * @apiParam {Long} [setOfBooksId] 账套ID
     * @apiParam {String} [reportTypeCode] 报账单类型代码
     * @apiParam {String} [reportTypeName] 报账单类型名称
     * @apiParam {Boolean} [enabled] 是否启用
     * @apiParam {int} page
     * @apiParam {int} size
     * @apiSuccess {ExpenseReportType} expenseReportType 报账单类型
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "i18n": {
    "reportTypeName": [
    {
    "language": "en_us",
    "value": "hxtest1update"
    },
    {
    "language": "zh_cn",
    "value": "hanxuetest1update"
    }
    ]
    },
    "id": "1100287347849232386",
    "deleted": false,
    "createdDate": "2019-02-26T14:52:00.74+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-02-26T15:48:43.808+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 2,
    "enabled": true,
    "tenantId": "1085713586410717186",
    "setOfBooksId": "1085717261577322498",
    "reportTypeCode": "hxtest1",
    "reportTypeName": "hanxuetest1update",
    "formId": "111",
    "formType": 888,
    "allExpenseFlag": true,
    "budgetFlag": false,
    "applicationFormBasis": "HEADER_COM",
    "associateContract": true,
    "contractRequired": false,
    "multiPayee": false,
    "payeeType": "EMPLOYEE",
    "allCashTransactionClass": true,
    "paymentMethod": "ONLINE_PAYMENT",
    "writeOffApplication": false,
    "writeOffContract": false,
    "applyEmployee": "1001",
    "setOfBooksCode": "HAND_SOB001",
    "setOfBooksName": "融晶账套001",
    "paymentMethodName": "线上"
    }
    ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<ExpenseReportType>> getExpenseReportTypeByCond(
            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @RequestParam(value = "reportTypeCode", required = false) String reportTypeCode,
            @RequestParam(value = "reportTypeName", required = false) String reportTypeName,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<ExpenseReportType> list = expenseReportTypeService.getExpenseReportTypeByCond(setOfBooksId,reportTypeCode,reportTypeName,enabled,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list,httpHeaders, HttpStatus.OK);
    }

    /**
     * 获取某个报账单类型下，当前账套下、启用的、PAYMENT类型的 已分配的、未分配的、全部的 付款用途(现金事物分类)
     * @param setOfBooksId 账套ID
     * @param range 查询范围(全部：all；已选：selected；未选：notChoose)
     * @param reportTypeId 报账单类型ID
     * @param code 付款用途代码
     * @param name 付款用途名称
     * @param pageable 分页信息
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/expense/report/type/query/transaction/class?setOfBooksId=1083762150064451585&range=all&reportTypeId&code&name&page=0&size=10 【报账单类型关联付款用途】查询
     * @apiDescription 获取某个报账单类型下，当前账套下、启用的、PAYMENT类型的 已分配的、未分配的、全部的 付款用途(现金事物分类)
     * @apiGroup ReportTypeService
     * @apiParam {Long} setOfBooksId 账套ID
     * @apiParam {String} range 查询范围(全部：all；已选：selected；未选：notChoose)
     * @apiParam {Long} [reportTypeId] 报账单类型ID(新建时不传，更新时传)
     * @apiParam {String} [code] 付款用途代码
     * @apiParam {String} [name] 付款用途名称
     * @apiParam {int} page 当前页
     * @apiParam {int} size 每页大小
     * @apiSuccess {Long} id  付款用途ID
     * @apiSuccess {String} classCode 付款用途代码
     * @apiSuccess {String} description 付款用途名称
     * @apiSuccess {Boolean} assigned 是否被分配(true表示已被分配，false表示未被分配)
     * @apiParamExample {json} 请求参数:
     * /api/expense/report/type/query/transaction/class?setOfBooksId=1083762150064451585&range=all&reportTypeId&code&name&page=0&size=10
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1085101891113521153",
    "enabled": true,
    "deleted": false,
    "createdDate": "2019-01-15T17:10:25.719+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-01-31T15:35:30.22+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 2,
    "setOfBookId": "1083762150064451585",
    "typeCode": "PAYMENT",
    "typeName": null,
    "classCode": "01",
    "description": "22",
    "setOfBookCode": null,
    "setOfBookName": null,
    "assigned": false
    }
    ]
     */
    @GetMapping("/query/transaction/class")
    public ResponseEntity<List<CashTransactionClassCO>> getTransactionClassForExpenseReportType(
            @RequestParam(value = "setOfBooksId") Long setOfBooksId,
            @RequestParam(value = "range") String range,
            @RequestParam(value = "reportTypeId",required = false) Long reportTypeId,
            @RequestParam(value = "code",required = false) String code,
            @RequestParam(value = "name",required = false) String name,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CashTransactionClassCO> result = expenseReportTypeService.getTransactionClassForExpenseReportType(setOfBooksId,range,reportTypeId,code,name,page);
        HttpHeaders httpHeaders = PageUtil.getTotalHeader(page);
        return new ResponseEntity<>(result.getRecords(),httpHeaders,HttpStatus.OK);
    }

    /**
     * 获取用户有权限创建的报账单类型
     * @return
     */
    /**
     * @api {GET} /api/expense/report/type/owner/all 【报账单】用户有权限创建的单据类型
     * @apiDescription 获取用户有权限创建的单据类型
     * @apiGroup ExpenseReport
     * @apiSuccess (返回参数) {Long} id  ID
     * @apiSuccess (返回参数) {Long} tenantId  租户ID
     * @apiSuccess (返回参数) {Long} setOfBooksId  账套ID
     * @apiSuccess (返回参数) {String} reportTypeCode 报账单类型代码
     * @apiSuccess (返回参数) {String} reportTypeName 报账单类型名称
     * @apiSuccess (返回参数) {Long} formId  关联表单ID
     * @apiSuccess (返回参数) {Long} formType  关联表单类型
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {Boolean} allExpenseFlag 是否关联全部费用类型标志(全部类型:1;部分类型:0)
     * @apiSuccess (返回参数) {Boolean} budgetFlag 预算管控标志(启用:Y;不启用:N)
     * @apiSuccess (返回参数) {String} applicationFormBasis 关联申请单依据
     * @apiSuccess (返回参数) {Boolean} associateContract 关联合同标志(可关联:Y;不可关联:N)
     * @apiSuccess (返回参数) {Boolean} [contractRequired] 合同必输标志(必输:Y;非必输:N)
     * @apiSuccess (返回参数) {Boolean} multiPayee 多收款方标志(多收款方:Y;单一收款方:N)
     * @apiSuccess (返回参数) {String} payeeType 收款方属性
     * @apiSuccess (返回参数) {Boolean} allCashTransactionClass 是否全部付款用途标志(全部类型:1;部分类型:0)
     * @apiSuccess (返回参数) {String} paymentMethod 付款方式类型
     * @apiSuccess (返回参数) {Boolean} writeOffApplication 核销依据:是否关联相同申请单(是:Y;否:N)
     * @apiSuccess (返回参数) {Boolean} writeOffContract 核销依据:是否关联相同合同(是:Y;否:N)
     * @apiSuccess (返回参数) {String} applyEmployee 适用人员("1001":全部;"1002";部门;"1003":人员组)
     * @apiParamExample {json} 请求参数:
        /api/expense/report/type/owner/all
     * @apiSuccessExample {json} 成功返回值:
    [
        {
            "i18n": null,
            "id": "1105854700853731329",
            "deleted": false,
            "createdDate": "2019-03-13T23:34:41.128+08:00",
            "createdBy": "1083751705402064897",
            "lastUpdatedDate": "2019-03-13T23:34:41.128+08:00",
            "lastUpdatedBy": "1083751705402064897",
            "versionNumber": 1,
            "enabled": true,
            "tenantId": "1083751703623680001",
            "setOfBooksId": "1083762150064451585",
            "reportTypeCode": "PKK_EXPENSE",
            "reportTypeName": "PKK报账单类型",
            "formId": "1105649912412237826",
            "formType": 2,
            "allExpenseFlag": false,
            "budgetFlag": true,
            "applicationFormBasis": "HEADER_DEPARTMENT",
            "associateContract": false,
            "contractRequired": null,
            "multiPayee": true,
            "payeeType": "BOTH",
            "allCashTransactionClass": false,
            "paymentMethod": "ONLINE_PAYMENT",
            "writeOffApplication": false,
            "writeOffContract": false,
            "applyEmployee": "1001",
            "setOfBooksCode": null,
            "setOfBooksName": null,
            "paymentMethodName": null,
            "formName": null
        }
    ]
     */
    @GetMapping("/owner/all")
    public ResponseEntity<List<ExpenseReportType>> getCurrentUserExpenseReportType(){
        List<ExpenseReportType> currentUserExpenseReportType = expenseReportTypeService.getCurrentUserExpenseReportType();
        return ResponseEntity.ok(currentUserExpenseReportType);
    }

    /**
     * 根据报账类型获取明细配置
     * @param expenseReportTypeId
     * @param headerId
     * @return
     */
    /**
     * @api {GET} /api/expense/report/type/properties/detail 【报账单】根据报账单类型获取明细配置
     * @apiDescription 根据报账单类型获取明细配置
     * @apiGroup ExpenseReport
     * @apiParam {Long} expenseReportTypeId 报账单类型ID
     * @apiParam {Long} [headerId] 单据头ID
     * @apiSuccess (返回参数) {Long} id  ID
     * @apiSuccess (返回参数) {Long} tenantId  租户ID
     * @apiSuccess (返回参数) {Long} setOfBooksId  账套ID
     * @apiSuccess (返回参数) {String} reportTypeCode 报账单类型代码
     * @apiSuccess (返回参数) {String} reportTypeName 报账单类型名称
     * @apiSuccess (返回参数) {Long} formId  关联表单ID
     * @apiSuccess (返回参数) {Long} formType  关联表单类型
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {Boolean} allExpenseFlag 是否关联全部费用类型标志(全部类型:1;部分类型:0)
     * @apiSuccess (返回参数) {Boolean} budgetFlag 预算管控标志(启用:Y;不启用:N)
     * @apiSuccess (返回参数) {String} applicationFormBasis 关联申请单依据
     * @apiSuccess (返回参数) {Boolean} associateContract 关联合同标志(可关联:Y;不可关联:N)
     * @apiSuccess (返回参数) {Boolean} [contractRequired] 合同必输标志(必输:Y;非必输:N)
     * @apiSuccess (返回参数) {Boolean} multiPayee 多收款方标志(多收款方:Y;单一收款方:N)
     * @apiSuccess (返回参数) {String} payeeType 收款方属性
     * @apiSuccess (返回参数) {Boolean} allCashTransactionClass 是否全部付款用途标志(全部类型:1;部分类型:0)
     * @apiSuccess (返回参数) {String} paymentMethod 付款方式类型
     * @apiSuccess (返回参数) {Boolean} writeOffApplication 核销依据:是否关联相同申请单(是:Y;否:N)
     * @apiSuccess (返回参数) {Boolean} writeOffContract 核销依据:是否关联相同合同(是:Y;否:N)
     * @apiSuccess (返回参数) {String} applyEmployee 适用人员("1001":全部;"1002";部门;"1003":人员组)
     * @apiSuccess (返回参数) {List} expenseDimensions 维度配置
     * @apiSuccess (返回参数) {ExpenseReportTypeDistSetting} expenseReportTypeDistSetting 分摊配置
     * @apiSuccess (expenseDimensions) {Long} value 默认的维值Id
     * @apiSuccess (expenseDimensions) {Long} dimensionId 维度Id
     * @apiSuccess (expenseDimensions) {String} dimensionField 维度字段名
     * @apiSuccess (expenseDimensions) {String} name 维度名称
     * @apiSuccess (expenseDimensions) {String} valueName 维值名称
     * @apiSuccess (expenseReportTypeDistSetting) {Long} reportTypeId 报账单类型ID
     * @apiSuccess (expenseReportTypeDistSetting) {Boolean} companyDistFlag 公司参与分摊标志
     * @apiSuccess (expenseReportTypeDistSetting) {String} companyDistRange 公司分摊范围 (账套下所有公司:ALL_COM_IN_SOB;本公司及下属公司:'CURRENT_COM_&_SUB_COM';下属公司:SUB_COM;自定义范围:CUSTOM_RANGE)
     * @apiSuccess (expenseReportTypeDistSetting) {Long} companyDefaultId 默认分摊公司ID
     * @apiSuccess (expenseReportTypeDistSetting) {String} companyCode 默认分摊公司代码
     * @apiSuccess (expenseReportTypeDistSetting) {String} companyName 默认分摊公司名称
     * @apiSuccess (expenseReportTypeDistSetting) {String} companyVisible 公司可见设置 (只读:READ_ONLY;可编辑:EDITABLE;隐藏:HIDDEN)
     * @apiSuccess (expenseReportTypeDistSetting) {Boolean} departmentDistFlag 部门参与分摊标志
     * @apiSuccess (expenseReportTypeDistSetting) {String} departmentDistRange 部门分摊范围 (租户下所有部门:ALL_DEP_IN_TENANT;账套下所有部门:ALL_DEP_IN_SOB;公司下所有部门:ALL_DEP_IN_COM;自定义范围:CUSTOM_RANGE)
     * @apiSuccess (expenseReportTypeDistSetting) {Long} departmentDefaultId 默认分摊部门ID
     * @apiSuccess (expenseReportTypeDistSetting) {String} departmentCode 默认分摊部门代码
     * @apiSuccess (expenseReportTypeDistSetting) {String} departmentName 默认分摊部门名称
     * @apiSuccess (expenseReportTypeDistSetting) {String} departmentVisible 部门可见设置 (只读:READ_ONLY;可编辑:EDITABLE;隐藏:HIDDEN)
     * @apiSuccess (expenseReportTypeDistSetting) {Boolean} resCenterDistFlag 责任中心参与分摊标志
     * @apiSuccess (expenseReportTypeDistSetting) {String} resDistRange 责任中心分摊范围 (部门对应责任中心:DEP_RES_CENTER;账套下所有责任中心:ALL_RES_CENTER_IN_SOB;自定义范围:CUSTOM_RANGE)
     * @apiSuccess (expenseReportTypeDistSetting) {Long} resDefaultId 默认分摊责任中心ID
     * @apiSuccess (expenseReportTypeDistSetting) {String} resCode 默认分摊责任中心代码
     * @apiSuccess (expenseReportTypeDistSetting) {String} resName 默认分摊责任中心名称
     * @apiSuccess (expenseReportTypeDistSetting) {String} resVisible 责任中心可见设置 (只读:READ_ONLY;可编辑:EDITABLE;隐藏:HIDDEN)
     * @apiParamExample {json} 请求参数:
    /api/expense/report/type/owner/all
     * @apiSuccessExample {json} 成功返回值:
    {
    "i18n": null,
    "id": "1105854700853731329",
    "deleted": false,
    "createdDate": "2019-03-13T23:34:41.128+08:00",
    "createdBy": "1083751705402064897",
    "lastUpdatedDate": "2019-03-13T23:34:41.128+08:00",
    "lastUpdatedBy": "1083751705402064897",
    "versionNumber": 1,
    "enabled": true,
    "tenantId": "1083751703623680001",
    "setOfBooksId": "1083762150064451585",
    "reportTypeCode": "PKK_EXPENSE",
    "reportTypeName": "PKK报账单类型",
    "formId": "1105649912412237826",
    "formType": 2,
    "allExpenseFlag": false,
    "budgetFlag": true,
    "applicationFormBasis": "HEADER_DEPARTMENT",
    "associateContract": false,
    "contractRequired": null,
    "multiPayee": true,
    "payeeType": "BOTH",
    "allCashTransactionClass": false,
    "paymentMethod": "ONLINE_PAYMENT",
    "writeOffApplication": false,
    "writeOffContract": false,
    "applyEmployee": "1001",
    "setOfBooksCode": null,
    "setOfBooksName": null,
    "paymentMethodName": null,
    "formName": null,
    "expenseDimensions": [
        {
        "id": null,
        "createdDate": null,
        "createdBy": null,
        "lastUpdatedDate": null,
        "lastUpdatedBy": null,
        "versionNumber": null,
        "documentType": null,
        "value": "1084698307856949249",
        "valueName": "dv2131",
        "name": "维度2",
        "dimensionId": "1084698172754223106",
        "dimensionField": "dimension2Id",
        "headerFlag": true,
        "headerId": null,
        "sequence": 20,
        "requiredFlag": true,
        "options": null
        }
    ],
    "expenseReportTypeDistSetting": {
        "id": "1105857901938155521",
        "createdDate": "2019-03-13T23:47:24.326+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-03-13T23:47:24.326+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "reportTypeId": "1105854700853731329",
        "companyDistFlag": true,
        "companyDistRange": "ALL_COM_IN_SOB",
        "companyDefaultId": "1105639563476512770",
        "companyVisible": "EDITABLE",
        "departmentDistFlag": true,
        "departmentDistRange": "ALL_DEP_IN_COM",
        "departmentDefaultId": "1103178526945767425",
        "departmentVisible": "EDITABLE",
        "resCenterDistFlag": false,
        "resDistRange": null,
        "resDefaultId": null,
        "resVisible": "READ_ONLY",
        "companyCode": "0313",
        "companyName": "春暖花开公司",
        "departmentCode": "0013",
        "departmentName": "吃货二部",
        "resCode": null,
        "resName": null
    }
    }
     */
    @GetMapping("/properties/detail")
    public ResponseEntity<ExpenseReportTypeDTO> getExpenseReportHeaderDimensions(@RequestParam("expenseReportTypeId") Long expenseReportTypeId,
                                                                                   @RequestParam(value = "headerId",required = false) Long headerId){
        ExpenseReportTypeDTO expenseReportTypeDTO = expenseReportTypeService.getExpenseReportType(expenseReportTypeId, headerId);
        return ResponseEntity.ok(expenseReportTypeDTO);
    }

    /**
     * 获取报账单费用类型 (部分类型)
     * @param expenseReportTypeId
     * @param pageable
     * @return
     */
    /**
     *
     * @api {GET} /api/expense/report/type/section/expense/type 【报账单】获取部分费用类型
     * @apiDescription 根据报账单类型ID获取配置的部分费用类型
     * @apiGroup ExpenseReport
     * @apiParam (请求参数) {Long} expenseReportTypeId  报账单类型ID
     * @apiParam (请求参数) {Long} employeeId  申请人ID
     * @apiParam (请求参数) {Long} companyId   公司ID
     * @apiParam (请求参数) {Long} departmentId  部门ID
     * @apiParam (请求参数) {Long} typeCategoryId    单据大类ID
     * @apiParam (请求参数) {Long} expenseTypeName  费用类型名称
     * @apiParam (请求参数) {int} [page]  页数
     * @apiParam (请求参数) {int} [size]  每页大小
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {Object} i18n 多语言信息
     * @apiSuccess (返回参数) {Integer} sequence 序号
     * @apiSuccess (返回参数) {Long} setOfBooksId 账套ID
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {Boolean} enabled 是否启用
     * @apiSuccess (返回参数) {String} name 名称
     * @apiSuccess (返回参数) {String} code 代码
     * @apiSuccess (返回参数) {String} iconName 图标名称
     * @apiSuccess (返回参数) {String} iconUrl 图标地址
     * @apiSuccess (返回参数) {Long} typeCategoryId 所属大类
     * @apiSuccess (返回参数) {Boolean} entryMode 是否使用单价
     * @apiSuccess (返回参数) {Integer} priceUnit 单价模式
     * @apiSuccess (返回参数) {Integer} typeFlag 类别类型 0-申请 1- 费用
     * @apiSuccess (返回参数) {Integer} attachmentFlag 附件模式
     * @apiSuccess (返回参数) {Long} sourceTypeId 申请类型
     * @apiSuccess (返回参数) {Boolean} deleted    删除标志
     * @apiSuccess (返回参数) {Integer} versionNumber    版本号
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate  创建时间
     * @apiSuccess (返回参数) {Long} createdBy    创建人ID
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate    最后更新时间
     * @apiSuccess (返回参数) {Long} lastUpdatedBy    更新人ID
     *
     * @apiSuccessExample {json} 返回报文:
     * [
     *     {
     *         "i18n": null,
     *         "id": "1060011184605777922",
     *         "deleted": false,
     *         "createdDate": "2018-11-07T11:28:55.13+08:00",
     *         "createdBy": "1031",
     *         "lastUpdatedDate": "2018-11-07T11:28:55.131+08:00",
     *         "lastUpdatedBy": "1031",
     *         "versionNumber": 1,
     *         "enabled": true,
     *         "name": "测试",
     *         "iconName": "meetings",
     *         "code": "test",
     *         "iconUrl": "https://huilianyi-uat-static.oss-cn-shanghai.aliyuncs.com//2ec774f5-7aba-486c-bd48-cf2ae74c9d9f/expenseIcon/8a950184-2032-436f-9431-db91dad1287c-meetings.png",
     *         "tenantId": "1022057230117146625",
     *         "setOfBooksId": "1037906263432859649",
     *         "sequence": 0,
     *         "typeCategoryId": "1059677501352337410",
     *         "typeFlag": 0,
     *         "entryMode": false,
     *         "attachmentFlag": null,
     *         "sourceTypeId": null,
     *         "priceUnit": null,
     *         "typeCategoryName": "aaa"
     *     }]
     */
    @GetMapping("/section/expense/type")
    public ResponseEntity<List<ExpenseType>> getExpenseTypesById(@RequestParam("expenseReportTypeId") Long expenseReportTypeId,
                                                                 @RequestParam("employeeId") Long employeeId,
                                                                 @RequestParam("companyId") Long companyId,
                                                                 @RequestParam("departmentId") Long departmentId,
                                                                 @RequestParam(value = "typeCategoryId",required = false) Long typeCategoryId,
                                                                 @RequestParam(value = "expenseTypeName", required = false) String expenseTypeName,
                                                                 Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        List<ExpenseTypeWebDTO> expenseReportTypeExpenseType =
                expenseReportTypeService.getExpenseReportTypeExpenseType(expenseReportTypeId,employeeId,companyId, departmentId, typeCategoryId, expenseTypeName, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(expenseReportTypeExpenseType,totalHeader,HttpStatus.OK);
    }

    /**
     * 获取部分现金事务分类信息
     * @param expenseReportTypeId
     * @param code
     * @param name
     * @param pageable
     * @return
     */
    /**
     *
     * @api {GET} /api/expense/report/type/section/cash/transaction/class 【报账单】获取部分现金事务
     * @apiDescription 根据报账单类型ID获取配置的获取部分现金事务
     * @apiGroup ExpenseReport
     * @apiParam (请求参数) {Long} expenseReportTypeId  报账单类型ID
     * @apiParam (请求参数) {int} [code]  现金事务代码
     * @apiParam (请求参数) {int} [name]  现金事务名称
     * @apiParam (请求参数) {int} [page]  页数
     * @apiParam (请求参数) {int} [size]  每页大小
     * @apiSuccess (返回参数) {Long} id  主键id
     * @apiSuccess (返回参数) {String} classCode 现金事务代码
     * @apiSuccess (返回参数) {String} description 现金事务名称
     *
     * @apiSuccessExample {json} 返回报文:
    [
        {
        "id": "1103935497253076994",
        "enabled": true,
        "deleted": false,
        "createdDate": "2019-03-08T16:28:27.332+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-03-08T16:28:27.332+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "setOfBookId": "1083762150064451585",
        "typeCode": "PAYMENT",
        "typeName": null,
        "classCode": "111",
        "description": "2222",
        "setOfBookCode": null,
        "setOfBookName": null,
        "assigned": null
        }
    ]
     */
    @GetMapping("/section/cash/transaction/class")
    public ResponseEntity<List<CashTransactionClassCO>> getExpenseReportTypeCashTransactionClasses(@RequestParam("expenseReportTypeId") Long expenseReportTypeId,
                                                           @RequestParam(value = "code",required = false) String code,
                                                           @RequestParam(value = "name",required = false) String name,
                                                           Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionClassCO> cashTransactionClassCOPage =
                expenseReportTypeService.getExpenseReportTypeCashTransactionClasses(expenseReportTypeId,code,name, page);
        HttpHeaders totalHeader = PageUtil.getTotalHeader(page);
        return new ResponseEntity(cashTransactionClassCOPage,totalHeader,HttpStatus.OK);
    }

    /**
     * {GET} /api/expense/report/type/by/companyId
     * 根据公司id查询报账单类型
     * @param companyId
     * @return
     */
    @GetMapping("/by/companyId")
    public ResponseEntity getExpenseReprotTypeByCompanyId(@RequestParam(value = "companyId") Long companyId){
        List<ExpenseReportType> reportTypes = expenseReportTypeService.getExpenseReprotTypeByCompanyId(companyId);
        return ResponseEntity.ok(reportTypes);
    }

    @GetMapping("/queryByformTypes")
    public ResponseEntity<List<ExpenseReportType>> getExpenseReportTypeByFormTypes(
            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
            @RequestParam(value = "formTypes",required = false)List<Long> formTypes
            ) throws URISyntaxException {
        List<ExpenseReportType> list = expenseReportTypeService.getExpenseReportTypeByFormTypes(setOfBooksId,formTypes);

        return ResponseEntity.ok(list);
    }

    /**
     *
     * @api {GET} /api/expense/report/type/users 【报账单】根据单据类型id查询有该单据权限的用户
     * @apiDescription 根据单据类型id查询有该单据权限的用户
     * @apiGroup ExpenseReport
     * @apiParam (请求参数) {Long} expenseReportTypeId  报账单类型ID
     */
    @GetMapping("/users")
    public ResponseEntity listUsersByApplicationType(@RequestParam(value = "expenseReportTypeId") Long expenseReportTypeId,
                                                     @RequestParam(value = "userCode", required = false) String userCode,
                                                     @RequestParam(value = "userName", required = false) String userName,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        Page queryPage = PageUtil.getPage(page, size);
        List<ContactCO> result = expenseReportTypeService.listUsersByExpenseReportType(expenseReportTypeId, userCode, userName, queryPage);

        HttpHeaders headers = PageUtil.getTotalHeader(queryPage);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

}
