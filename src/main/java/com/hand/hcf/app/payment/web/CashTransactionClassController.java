package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.payment.domain.CashTransactionClass;
import com.hand.hcf.app.payment.service.CashTransactionClassService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 韩雪 on 2017/9/7.
 */
@Api(tags = "现金事务分类API")
@RestController
@RequestMapping("/api/cash/transaction/classes")
public class CashTransactionClassController {
    private final CashTransactionClassService cashTransactionClassService;

    public CashTransactionClassController(CashTransactionClassService cashTransactionClassService){
        this.cashTransactionClassService = cashTransactionClassService;
    }

    /**
     * 新增 现金事务分类表
     *
     * @param cashTransactionClass
     * @return
     */
    /**
     * @api {POST} /api/cash/transaction/classes 【现金事务分类】单个新增
     * @apiDescription 该接口用于新增单个现金事务分类
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} typeCode 现金事务类型代码(现金交易事务类型) PAYMENT 付款,PREPAYMENT 预付款,PREPAYMENT_RECEIPT 预收款,RECEIPT 收款
     * @apiParam (请求参数) {String} classCode 现金事务分类代码
     * @apiParam (请求参数) {String} description 现金事务分类名称
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} setOfBookId 账套ID
     * @apiSuccess (返回参数) {String} typeCode 现金事务类型代码(现金交易事务类型代码) PAYMENT 付款,PREPAYMENT 预付款,PREPAYMENT_RECEIPT 预收款,RECEIPT 收款
     * @apiSuccess (返回参数) {String} typeName 现金事务类型名称(现金交易事务类型名称)
     * @apiSuccess (返回参数) {String} classCode 现金事务分类代码
     * @apiSuccess (返回参数) {String} description 现金事务分类名称
     * @apiSuccess (返回参数) {String} setOfBookCode 账套代码
     * @apiSuccess (返回参数) {String} setOfBookName 账套名称
     * @apiParamExample {json} 请求参数
     * {
     *   "setOfBookId": 1,
     *   "typeCode": "PAYMENT",
     *   "classCode": "130",
     *   "description": "名称3",
     * }
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "905693428099588098",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-07T15:25:14.889+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-07T15:25:14.889+08:00",
     *   "lastUpdatedBy": 11,
     *   "setOfBookId": 1,
     *   "typeCode": "PAYMENT",
     *   "classCode": "130",
     *   "description": "名称3",
     *   "versionNumber": 1
     * }
     */

    @ApiOperation(value = "新增单个现金事务分类", notes = "新增单个现金事务分类 开发：")
    @PostMapping
    public ResponseEntity<CashTransactionClass> createCashTransactionClass(@ApiParam(value = "现金事务分类") @RequestBody CashTransactionClass cashTransactionClass){
        return ResponseEntity.ok(cashTransactionClassService.createCashTransactionClass(cashTransactionClass));
    }

    /**
     * 修改 现金事务分类表
     *
     * @param cashTransactionClass
     * @return
     */
    /**
     * @api {PUT} /api/cash/transaction/classes 【现金事务分类】单个修改
     * @apiDescription 该接口用于修改单个现金事务分类
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} id 主键ID
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} typeCode 现金事务类型代码(现金交易事务类型代码) PAYMENT 付款,PREPAYMENT 预付款,PREPAYMENT_RECEIPT 预收款,RECEIPT 收款
     * @apiParam (请求参数) {String} classCode 现金事务分类代码
     * @apiParam (请求参数) {String} description 现金事务分类名称
     * @apiParam (请求参数) {Boolean} isEnabled 是否启用
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} setOfBookId 账套ID
     * @apiSuccess (返回参数) {String} typeCode 现金事务类型代码(现金交易事务类型代码) PAYMENT 付款,PREPAYMENT 预付款,PREPAYMENT_RECEIPT 预收款,RECEIPT 收款
     * @apiSuccess (返回参数) {String} typeName 现金事务类型名称(现金交易事务类型名称)
     * @apiSuccess (返回参数) {String} classCode 现金事务分类代码
     * @apiSuccess (返回参数) {String} description 现金事务分类名称
     * @apiSuccess (返回参数) {String} setOfBookCode 账套代码
     * @apiSuccess (返回参数) {String} setOfBookName 账套名称
     * @apiParamExample {json} 请求参数
     * {
     *   "id": 905691062625431554,
     *   "description": "hx名称3",
     *   "versionNumber": 1
     * }
     * @apiSuccessExample {json}  成功返回值
     * {
     *   "id": "905691062625431554",
     *   "isEnabled": null,
     *   "isDeleted": null,
     *   "createdDate": null,
     *   "createdBy": null,
     *   "lastUpdatedDate": null,
     *   "lastUpdatedBy": null,
     *   "setOfBookId": null,
     *   "typeCode": null,
     *   "classCode": null,
     *   "description": "hx名称3",
     *   "versionNumber": 1
     * }
     */
    @ApiOperation(value = "修改单个现金事务分类", notes = "修改单个现金事务分类 开发：")
    @PutMapping
    public ResponseEntity<CashTransactionClass> updateCashTransactionClass(@ApiParam(value = "现金事务分类") @RequestBody CashTransactionClass cashTransactionClass){
        return ResponseEntity.ok(cashTransactionClassService.updateCashTransactionClass(cashTransactionClass));
    }

    /**
     * 删除 现金事务分类表(逻辑删除)
     *
     * @param id
     * @return
     */
    /**
     * @apiDefine myID
     * @apiParam (请求参数) {Long} id 现金事务分类待删除的ID
     */
    /**
     * @apiDefine MyError
     * @apiError UserNotFound The <code>id</code> of the User was not found.
     */
    /**
     * @api {DELETE} /api/cash/transaction/classes/{id} 【现金事务分类】单个删除
     * @apiDescription 根据id删除单个现金事务分类
     * @apiGroup PaymentService
     * @apiUse myID
     * @apiUse MyError
     */

    @ApiOperation(value = "逻辑删除单个现金事务分类", notes = "逻辑删除单个现金事务分类 开发：")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCashTransactionClass(@ApiParam(value = "id") @PathVariable Long id){
        cashTransactionClassService.deleteCashTransactionClass(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据ID查询 现金事务分类表
     *
     * @param id
     * @return
     */
    /**
     * @api {GET} /api/cash/transaction/classes/{id} 【现金事务分类】单个查询
     * @apiDescription 根据id查询单个现金事务分类
     * @apiGroup PaymentService
     * @apiParamExample {josn} 请求参数
     * /api/cash/flow/items/1
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "1",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-11-28T07:05:10+08:00",
     *   "createdBy": 1,
     *   "lastUpdatedDate": "2017-11-28T07:05:10+08:00",
     *   "lastUpdatedBy": 1,
     *   "versionNumber": 1,
     *   "setOfBookId": "913384531111178242",
     *   "typeCode": "PAYMENT",
     *   "typeName": "付款",
     *   "classCode": "hx",
     *   "description": "hanxue",
     *   "setOfBookCode": "DEFAULT_SOB",
     *   "setOfBookName": "默认账套"
     * }
     */

    @ApiOperation(value = "根据id查询单个现金事务分类", notes = "根据id查询单个现金事务分类 开发：")
    @GetMapping("/{id}")
    public ResponseEntity<CashTransactionClass> getCashTransactionClass(@PathVariable Long id){
        return ResponseEntity.ok(cashTransactionClassService.getCashTransactionClass(id));
    }

    /**
     * 自定义条件查询 现金事务分类表(分页)
     *
     * @param setOfBookId
     * @param classCode
     * @param description
     * @param typeCode
     * @param isEnabled
     * @return
     */
    /**
     * @api {GET} /api/cash/transaction/classes/query 【现金事务分类】分页查询
     * @apiDescription 根据条件分页查询现金事务分类
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} [classCode] 现金事务分类代码
     * @apiParam (请求参数) {String} [description] 现金事务分类名称
     * @apiParam (请求参数) {String} [typeCode] 现金事务类型代码(现金交易事务类型代码) PAYMENT 付款,PREPAYMENT 预付款,PREPAYMENT_RECEIPT 预收款,RECEIPT 收款
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiParamExample {json} 请求参数
     * /api/cash/transaction/classes/query?setOfBookId=913384531111178242
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "1",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-11-28T07:05:10+08:00",
     *   "createdBy": 1,
     *   "lastUpdatedDate": "2017-11-28T07:05:10+08:00",
     *   "lastUpdatedBy": 1,
     *   "versionNumber": 1,
     *   "setOfBookId": "913384531111178242",
     *   "typeCode": "PAYMENT",
     *   "typeName": "付款",
     *   "classCode": "hx",
     *   "description": "hanxue",
     *   "setOfBookCode": "DEFAULT_SOB",
     *   "setOfBookName": "默认账套"
     *  }
     * ]
     */

    @ApiOperation(value = "根据条件分页查询现金事务分类", notes = "根据条件分页查询现金事务分类 开发：")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query")
    public ResponseEntity<List<CashTransactionClass>> getCashTransactionClassByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId") Long setOfBookId,
            @ApiParam(value = "现金事务分类代码") @RequestParam(value = "classCode", required = false) String classCode,
            @ApiParam(value = "现金事务分类名称") @RequestParam(value = "description", required = false) String description,
            @ApiParam(value = "现金事务类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionClass> list = cashTransactionClassService.getCashTransactionClassByCond(setOfBookId,classCode, description,typeCode,isEnabled, page, false);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/classes/query");
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 现金事务分类表(分页)
     *
     * @param setOfBookId
     * @param classCode
     * @param description
     * @param typeCode
     * @param isEnabled
     * @return
     */
    /**
     * @api {GET} /api/cash/transaction/classes/query/enable/dataAuth 【现金事务分类】分页查询
     * @apiDescription 根据条件分页查询现金事务分类
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} [classCode] 现金事务分类代码
     * @apiParam (请求参数) {String} [description] 现金事务分类名称
     * @apiParam (请求参数) {String} [typeCode] 现金事务类型代码(现金交易事务类型代码) PAYMENT 付款,PREPAYMENT 预付款,PREPAYMENT_RECEIPT 预收款,RECEIPT 收款
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiParamExample {json} 请求参数
     * /api/cash/transaction/classes/query?setOfBookId=913384531111178242
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "1",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-11-28T07:05:10+08:00",
     *   "createdBy": 1,
     *   "lastUpdatedDate": "2017-11-28T07:05:10+08:00",
     *   "lastUpdatedBy": 1,
     *   "versionNumber": 1,
     *   "setOfBookId": "913384531111178242",
     *   "typeCode": "PAYMENT",
     *   "typeName": "付款",
     *   "classCode": "hx",
     *   "description": "hanxue",
     *   "setOfBookCode": "DEFAULT_SOB",
     *   "setOfBookName": "默认账套"
     *  }
     * ]
     */

    @ApiOperation(value = "根据条件是否授权分页查询现金事务分类", notes = "根据条件是否授权分页查询现金事务分类 开发：")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query/enable/dataAuth")
    public ResponseEntity<List<CashTransactionClass>> getCashTransactionClassByCondEnableDataAuth(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId") Long setOfBookId,
            @ApiParam(value = "现金事务分类") @RequestParam(value = "classCode", required = false) String classCode,
            @ApiParam(value = "现金事务分类名称") @RequestParam(value = "description", required = false) String description,
            @ApiParam(value = "现金事务类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionClass> list = cashTransactionClassService.getCashTransactionClassByCond(setOfBookId,classCode, description,typeCode,isEnabled, page, true);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/classes/query/enable/dataAuth");
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 现金事务分类表(不分页)
     *
     * @param setOfBookId
     * @param classCode
     * @param description
     * @param typeCode
     * @param isEnabled
     * @return
     */
    /**
     * @api {GET} /api/cash/transaction/classes/queryAll 【现金事务分类】不分页查询
     * @apiDescription 根据条件不分页查询现金事务分类
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} [classCode] 现金事务分类代码
     * @apiParam (请求参数) {String} [description] 现金事务分类名称
     * @apiParam (请求参数) {String} [typeCode] 现金事务类型代码(现金交易事务类型代码) PAYMENT 付款,PREPAYMENT 预付款,PREPAYMENT_RECEIPT 预收款,RECEIPT 收款
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiParamExample {json} 请求参数
     * /api/cash/transaction/classes/queryAll?setOfBookId=913384531111178242
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "1",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-11-28T07:05:10+08:00",
     *   "createdBy": 1,
     *   "lastUpdatedDate": "2017-11-28T07:05:10+08:00",
     *   "lastUpdatedBy": 1,
     *   "versionNumber": 1,
     *   "setOfBookId": "913384531111178242",
     *   "typeCode": "PAYMENT",
     *   "typeName": "付款",
     *   "classCode": "hx",
     *   "description": "hanxue",
     *   "setOfBookCode": "DEFAULT_SOB",
     *   "setOfBookName": "默认账套"
     *  }
     * ]
     */

    @ApiOperation(value = "根据条件不分页查询现金事务分类", notes = "根据条件不分页查询现金事务分类 开发：")
    @GetMapping("/queryAll")
    public ResponseEntity<List<CashTransactionClass>> getCashTransactionClassAllByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId") Long setOfBookId,
            @ApiParam(value = "现金事务分类代码") @RequestParam(value = "classCode", required = false) String classCode,
            @ApiParam(value = "现金事务分类名称") @RequestParam(value = "description", required = false) String description,
            @ApiParam(value = "现金事务类型代码") @RequestParam(value = "typeCode", required = false) String typeCode,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled){
        List<CashTransactionClass> list = cashTransactionClassService.getCashTransactionClassAllByCond(setOfBookId,classCode, description,typeCode,isEnabled);
        return ResponseEntity.ok(list);
    }

    /**
     * 批量新增 现金事务分类表
     *
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/cash/transaction/classes/batch 【现金事务分类】批量新增
     * @apiDescription 批量新增现金事务分类
     * @apiGroup PaymentService
     * @apiParamExample {josn} 请求参数
     * [
     * {
     *   "setOfBookId": 2,
     *   "typeCode": "PREPAYMENT",
     *   "classCode": "110",
     *   "description": "收到原保险合同保费取得的现金",
     * }
     * ]
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "905390919606927362",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-06T19:23:11.242+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-06T19:23:11.242+08:00",
     *   "lastUpdatedBy": 11,
     *   "setOfBookId": 2,
     *   "classCode": "110",
     *   "description": "收到原保险合同保费取得的现金",
     *   "versionNumber": 1
     * }
     * ]
     */

    @ApiOperation(value = "批量新增现金事务分类", notes = "批量新增现金事务分类 开发：")
    @PostMapping("/batch")
    public ResponseEntity<List<CashTransactionClass>> createCashTransactionClassBatch(@ApiParam(value = "现金事务分类") @RequestBody List<CashTransactionClass> list){
        return ResponseEntity.ok(cashTransactionClassService.createCashTransactionClassBatch(list));
    }

    /**
     * 批量修改 现金事务分类表
     *
     * @param list
     * @return
     */
    /**
     * @api {PUT} /api/cash/transaction/classes/batch 【现金事务分类】批量修改
     * @apiDescription 批量修改现金事务分类
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * [
     * {
     *   "id": 905694152116150273,
     *   "description": "hx名称1"
     * }
     * ]
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "905694152116150273",
     *   "isEnabled": null,
     *   "isDeleted": null,
     *   "createdDate": null,
     *   "createdBy": null,
     *   "lastUpdatedDate": null,
     *   "lastUpdatedBy": null,
     *   "setOfBookId": null,
     *   "typeCode": null,
     *   "classCode": null,
     *   "description": "hx名称1",
     *   "versionNumber": null
     * }
     * ]
     */

    @ApiOperation(value = "批量修改现金事务分类", notes = "批量修改现金事务分类 开发：")
    @PutMapping("/batch")
    public ResponseEntity<List<CashTransactionClass>> updateCashTransactionClassBatch(@ApiParam(value = "现金事务分类") @RequestBody List<CashTransactionClass> list){
        return ResponseEntity.ok(cashTransactionClassService.updateCashTransactionClassBatch(list));
    }

    /**
     * 批量删除 现金事务分类表(逻辑删除)
     *
     * @param list
     * @return
     */
    /**
     * @api {DELETE} /api/cash/transaction/classes/batch 【现金事务分类】批量删除
     * @apiDescription 批量删除现金事务分类
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * [
     *   905390919606927400,
     *   905390919682424800
     * ]
     * @apiSuccessExample {json} 成功返回值
     * {
     *
     * }
     */

    @ApiOperation(value = "批量删除现金事务分类", notes = "批量删除现金事务分类 开发：")
    @DeleteMapping("/batch")
    public ResponseEntity deleteCashTransactionClassBatch(@ApiParam(value = "现金事务列表") @RequestBody List<Long> list){
        cashTransactionClassService.deleteCashTransactionClassBatch(list);
        return ResponseEntity.ok().build();
    }

/*--------------------------------------------------对外提供-------------------------------------------------------------------------*/


    /**
     * 获取当前账套下的，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类
     *
     * @param setOfBookId
     * @return
     */
//    @GetMapping("/queryBySetOfBookId/{setOfBookId}")
//    public ResponseEntity<List<CashTransactionClass>> getCashTransactionClassBySetOfBookId(@PathVariable Long setOfBookId){
//        return ResponseEntity.ok(cashTransactionClassService.getCashTransactionClassBySetOfBookId(setOfBookId));
//    }

    /**
     * 根据现金事务分类ID集合查询详情
     *
     * @param list
     * @return
     */
//    @PostMapping("/queryByIdList")
//    public ResponseEntity<List<CashTransactionClass>> getCashTransactionClassByIdList(@RequestBody List<Long> list){
//        return ResponseEntity.ok(cashTransactionClassService.getCashTransactionClassByIdList(list));
//    }

    /**
     * 给artemis模块提供的，根据现金事务分类ID获取现金事务分类code、现金事务分类name
     *
     * @param id
     * @return
     */
//    @GetMapping("/forArtemisById/{id}")
//    public ResponseEntity<CashTransactionClass> getCashTransactionClassForArtemisById(@PathVariable Long id){
//        return ResponseEntity.ok(cashTransactionClassService.getCashTransactionClassById(id));
//    }


    /**
     * 给artemis提供，对公报销单部分
     * 获取某个表单下，当前账套下 已分配的、未分配的 现金事物分类
     *
     * @param forArtemisDTO
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
//    @PostMapping(value = "/queryCashTransactionClassByRange")
//    public ResponseEntity<Page<CashTransactionClass>> getCashTransactionClassByRange(
//            @RequestBody @Valid CashTransactionClassForOtherDTO forArtemisDTO,
//            Pageable pageable) throws URISyntaxException {
//        Page page = PageUtil.getPage(pageable);
//        Page<CashTransactionClass> list = cashTransactionClassService.getCashTransactionClassByRange(forArtemisDTO,page);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/classes/queryCashTransactionClassByRange");
//        return new ResponseEntity(list, headers, HttpStatus.OK);
//    }

    /**
     * 给prepayment提供，预付款单类型
     * 获取某个预付款单类型下，当前账套下、启用的、PREPAYMENT类型的 已分配的、未分配的、全部的 现金事物分类
     *
     * @param forArtemisDTO
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
//    @PostMapping(value = "/queryCashTransactionClassForPerPayByRange")
//    public ResponseEntity<Page<CashTransactionClass>> getCashTransactionClassForPerPayByRange(
//            @RequestBody @Valid CashTransactionClassForOtherDTO forArtemisDTO,
//            Pageable pageable) throws URISyntaxException {
//        Page page = PageUtil.getPage(pageable);
//        Page<CashTransactionClass> list = cashTransactionClassService.getCashTransactionClassForPerPayByRange(forArtemisDTO,page);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/classes/queryCashTransactionClassForPerPayByRange");
//        return new ResponseEntity(list, headers, HttpStatus.OK);
//    }


    /**
     * 根据id或者限定条件查询现金事务分类
     *
     * @param selectId
     * @param setOfBooksId
     * @param code
     * @param name
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
//    @GetMapping("/queryByIdOrCond")
//    public ResponseEntity<Page<AccountingMatchGroupValueDTO>> getCashTransactionClassByIdOrCond(
//            @RequestParam(value = "selectId",required = false) Long selectId,
//            @RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
//            @RequestParam(value = "code", required = false) String code,
//            @RequestParam(value = "name", required = false) String name,
//            Pageable pageable) throws URISyntaxException {
//        Page page = PageUtil.getPage(pageable);
//        Page<AccountingMatchGroupValueDTO> list = cashTransactionClassService.getCashTransactionClassByIdOrCond(selectId,setOfBooksId,code, name, page);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/classes/queryByIdOrCond");
//        return new ResponseEntity(list, headers, HttpStatus.OK);
//    }

    /**
     * 根据账套id，查询该账套下启用的现金事务分类集合
     *
     * @param setOfBooksId
     * @return
     */
//    @GetMapping("/queryBySetOfBooksId/{setOfBooksId}")
//    public ResponseEntity<List<CashTransactionClass>> getCashTransactionClassBySetOfBooksId(@PathVariable Long setOfBooksId){
//        return ResponseEntity.ok(cashTransactionClassService.getCashTransactionClassBySetOfBooksId(setOfBooksId));
//    }
}
