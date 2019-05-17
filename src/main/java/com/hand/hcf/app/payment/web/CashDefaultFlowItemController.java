package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.payment.domain.CashDefaultFlowItem;
import com.hand.hcf.app.payment.domain.CashFlowItem;
import com.hand.hcf.app.payment.service.CashDefaultFlowItemService;
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

@Api(tags = "现金事务分类关联现金流量表API")
@RestController
@RequestMapping("/api/cash/default/flowitems")
public class CashDefaultFlowItemController {
    private final CashDefaultFlowItemService cashDefaultFlowItemService;

    public CashDefaultFlowItemController(CashDefaultFlowItemService cashDefaultFlowItemService){
        this.cashDefaultFlowItemService = cashDefaultFlowItemService;
    }

    /**
     * 新增 现金事务分类关联现金流量表
     *
     * @param cashDefaultFlowItem
     * @return
     */
    /**
     * @api {POST} /api/cash/default/flowitems 【现金事务分类分配现金流量项】单个新增
     * @apiDescription 新增单个现金事务分类分配现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} transactionClassId 现金事务分类ID
     * @apiParam (请求参数) {Long} cashFlowItemId 现金流量项ID
     * @apiParam (请求参数) {Boolean} defaultFlag 是否为默认现金流量项
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} transactionClassId 现金事务分类ID
     * @apiSuccess (返回参数) {Long} cashFlowItemId 现金流量项ID
     * @apiSuccess (返回参数) {Boolean} defaultFlag 是否为默认现金流量项
     * @apiParamExample {json} 请求参数
     * {
     *   "transactionClassId": 905823156752146400,
     *   "cashFlowItemId": 905376955439280100,
     *   "defaultFlag": false
     * }
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "905825469436907522",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-08T00:09:55.993+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-08T00:09:55.993+08:00",
     *   "lastUpdatedBy": 11,
     *   "transactionClassId": 905823156752146400,
     *   "cashFlowItemId": 905376955439280100,
     *   "defaultFlag": false,
     *   "versionNumber": 1
     * }
     */

    @ApiOperation(value = "新增单个现金事务分类分配现金流量项", notes = "新增单个现金事务分类分配现金流量项 开发:")
    @PostMapping
    public ResponseEntity<CashDefaultFlowItem> createCashDefaultFlowItem(@ApiParam(value = "现金事务分类关联现金流量") @RequestBody CashDefaultFlowItem cashDefaultFlowItem){
        return ResponseEntity.ok(cashDefaultFlowItemService.createCashDefaultFlowItem(cashDefaultFlowItem));
    }

    /**
     * 修改 现金事务分类关联现金流量表
     *
     * @param cashDefaultFlowItem
     * @return
     */
    /**
     * @api {PUT} /api/cash/default/flowitems 【现金事务分类分配现金流量项】单个修改
     * @apiDescription 修改单个现金事务分类分配现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} id 主键ID
     * @apiParam (请求参数) {Boolean} defaultFlag 是否为默认现金流量项
     * @apiParam (请求参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} transactionClassId 现金事务分类ID
     * @apiSuccess (返回参数) {Long} cashFlowItemId 现金流量项ID
     * @apiSuccess (返回参数) {Boolean} defaultFlag 是否为默认现金流量项
     * @apiParamExample {json} 请求参数
     * {
     *   "id": 905825076292210690,
     *   "defaultFlag": true,
     *   "versionNumber": 1
     * }
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "905825076292210690",
     *   "isEnabled": null,
     *   "isDeleted": null,
     *   "createdDate": null,
     *   "createdBy": null,
     *   "lastUpdatedDate": null,
     *   "lastUpdatedBy": null,
     *   "transactionClassId": null,
     *   "cashFlowItemId": null,
     *   "defaultFlag": true,
     *   "versionNumber": 1
     * }
     */

    @ApiOperation(value = "修改单个现金事务分类分配现金流量项", notes = "修改单个现金事务分类分配现金流量项 开发:")
    @PutMapping
    public ResponseEntity<CashDefaultFlowItem> updateCashDefaultFlowItem(@ApiParam(value = "现金事务分类关联现金流量") @RequestBody CashDefaultFlowItem cashDefaultFlowItem){
        return ResponseEntity.ok(cashDefaultFlowItemService.updateCashDefaultFlowItem(cashDefaultFlowItem));
    }

    /**
     * 删除 现金事务分类关联现金流量表(逻辑删除)
     *
     * @param id
     * @return
     */
    /**
     * @apiDefine myID
     * @apiParam (请求参数) {Long} id 现金事务分类分配现金流量项待删除的ID
     */
    /**
     * @apiDefine MyError
     * @apiError UserNotFound The <code>id</code> of the User was not found.
     */
    /**
     * @api {DELETE} /api/cash/default/flowitems/{id} 【现金事务分类分配现金流量项】单个删除
     * @apiDescription 根据id删除单个现金事务分类分配现金流量项
     * @apiGroup PaymentService
     * @apiUse myID
     * @apiUse MyError
     */

    @ApiOperation(value = "根据id删除单个现金事务分类分配现金流量项", notes = "根据id删除单个现金事务分类分配现金流量项 开发:")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCashDefaultFlowItem(@PathVariable Long id){
        cashDefaultFlowItemService.deleteCashDefaultFlowItem(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据ID查询 现金事务分类关联现金流量表
     *
     * @param id
     * @return
     */
    /**
     * @api {GET} /api/cash/default/flowitems/{id} 【现金事务分类分配现金流量项】单个查询
     * @apiDescription 根据id查询单个现金事务分类分配现金流量项
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * /api/cash/default/flowitems/905825469436907522
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "905825469436907522",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-08T00:09:55.993+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-08T00:09:55.993+08:00",
     *   "lastUpdatedBy": 11,
     *   "transactionClassId": 905823156752146400,
     *   "cashFlowItemId": 905376955439280100,
     *   "defaultFlag": false,
     *   "versionNumber": 1
     * }
     */

    @ApiOperation(value = "根据id查询单个现金事务分类分配现金流量项", notes = "根据id查询单个现金事务分类分配现金流量项 开发:")
    @GetMapping("/{id}")
    public ResponseEntity<CashDefaultFlowItem> getCashDefaultFlowItem(@PathVariable Long id){
        return ResponseEntity.ok(cashDefaultFlowItemService.getCashDefaultFlowItem(id));
    }

    /**
     * 自定义条件查询 现金事务分类关联现金流量表(分页)
     *
     * @param transactionClassId
     * @param defaultFlag
     * @param isEnabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/default/flowitems/query 【现金事务分类分配现金流量项】分页查询
     * @apiDescription 根据条件分页查询现金事务分类分配现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} transactionClassId 现金事务分类ID
     * @apiParam (请求参数) {Boolean} [defaultFlag] 是否为默认现金流量项
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} transactionClassId 现金事务分类ID
     * @apiSuccess (返回参数) {Long} cashFlowItemId 现金流量项ID
     * @apiSuccess (返回参数) {Boolean} defaultFlag 是否为默认现金流量项
     * @apiSuccess (返回参数) {String} cashFlowItemCode 现金流量项代码
     * @apiSuccess (返回参数) {String} cashFlowItemName 现金流量项名称
     * @apiParamExample {json} 请求参数
     * api/cash/default/flowitems/query?page=0&size=10&transactionClassId=987208008816254977
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *    "cashFlowItemCode":"14984",
     *    "cashFlowItemId":"987171989671510017",
     *    "cashFlowItemName":"1498测试现金流量项",
     *    "createdBy":177601,
     *    "createdDate":"2018-04-20T13:55:10+08:00",
     *    "defaultFlag":false,
     *    "id":"987208029045383170",
     *    "isDeleted":false,
     *    "isEnabled":true,
     *    "lastUpdatedBy":177601,
     *    "lastUpdatedDate":"2018-04-20T13:55:10+08:00",
     *    "transactionClassCode":null,
     *    "transactionClassId":"987208008816254977",
     *    "transactionClassName":null,
     *    "versionNumber":1
     * }
     *]
     */

    @ApiOperation(value = "根据条件分页查询现金事务分类分配现金流量项", notes = "根据条件分页查询现金事务分类分配现金流量项 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query")
    public ResponseEntity<List<CashDefaultFlowItem>> getCashDefaultFlowItemByCond(
            @ApiParam(value = "现金事务分类ID") @RequestParam(value = "transactionClassId", required = false) Long transactionClassId,
            @ApiParam(value = "是否为默认现金流量项") @RequestParam(value = "defaultFlag", required = false) Boolean defaultFlag,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashDefaultFlowItem> list = cashDefaultFlowItemService.getCashDefaultFlowItemByCond(transactionClassId,defaultFlag,isEnabled,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/default/flowitems/query");
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 现金事务分类关联现金流量表(不分页)
     *
     * @param transactionClassId
     * @param defaultFlag
     * @param isEnabled
     * @return
     */
    /**
     * @api {GET} /api/cash/default/flowitems/queryAll 【现金事务分类分配现金流量项】不分页查询
     * @apiDescription 根据条件不分页查询现金事务分类分配现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} transactionClassId 现金事务分类ID
     * @apiParam (请求参数) {Boolean} [defaultFlag] 是否为默认现金流量项
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} transactionClassId 现金事务分类ID
     * @apiSuccess (返回参数) {Long} cashFlowItemId 现金流量项ID
     * @apiSuccess (返回参数) {Boolean} defaultFlag 是否为默认现金流量项
     * @apiSuccess (返回参数) {String} cashFlowItemCode 现金流量项代码
     * @apiSuccess (返回参数) {String} cashFlowItemName 现金流量项名称
     * @apiParamExample {json}
     * api/cash/default/flowitems/query?transactionClassId=987208008816254977
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *    "cashFlowItemCode":"14984",
     *    "cashFlowItemId":"987171989671510017",
     *    "cashFlowItemName":"1498测试现金流量项",
     *    "createdBy":177601,
     *    "createdDate":"2018-04-20T13:55:10+08:00",
     *    "defaultFlag":false,
     *    "id":"987208029045383170",
     *    "isDeleted":false,
     *    "isEnabled":true,
     *    "lastUpdatedBy":177601,
     *    "lastUpdatedDate":"2018-04-20T13:55:10+08:00",
     *    "transactionClassCode":null,
     *    "transactionClassId":"987208008816254977",
     *    "transactionClassName":null,
     *    "versionNumber":1
     * }
     *]
     */

    @ApiOperation(value = "根据条件不分页查询现金事务分类分配现金流量项", notes = "根据条件不分页查询现金事务分类分配现金流量项 开发:")
    @GetMapping("/queryAll")
    public ResponseEntity<List<CashDefaultFlowItem>> getCashDefaultFlowItemAllByCond(
            @ApiParam(value = "现金事务分类ID") @RequestParam(value = "transactionClassId", required = false) Long transactionClassId,
            @ApiParam(value = "是否为默认现金流量项") @RequestParam(value = "defaultFlag", required = false) Boolean defaultFlag,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled){
        List<CashDefaultFlowItem> list = cashDefaultFlowItemService.getCashDefaultFlowItemAllByCond(transactionClassId,defaultFlag,isEnabled);
        return ResponseEntity.ok(list);
    }

    /**
     * 查询尚未分配的现金流量项
     *
     * @param setOfBookId
     * @param transactionClassId
     * @param flowCode
     * @param description
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/default/flowitems/queryNotSaveFlowItem 【现金事务分类分配现金流量项】查询尚未分配的现金流量项
     * @apiDescription 根据条件查询现金事务分类尚未分配的现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {Long} transactionClassId 现金事务分类ID
     * @apiParam (请求参数) {String} [flowCode] 现金流量项代码
     * @apiParam (请求参数) {String} [description] 现金流量项名称
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiSuccess (返回参数) {List} list 返回泛型为CashFlowItem的List集合
     * @apiSuccess (CashFlowItem的属性) {Long} id 主键ID
     * @apiSuccess (CashFlowItem的属性) {Boolean} isEnabled 是否启用
     * @apiSuccess (CashFlowItem的属性) {Boolean} isDeleted 是否删除
     * @apiSuccess (CashFlowItem的属性) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (CashFlowItem的属性) {Long} createdBy 创建人
     * @apiSuccess (CashFlowItem的属性) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (CashFlowItem的属性) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (CashFlowItem的属性) {Integer} versionNumber 版本号
     * @apiSuccess (CashFlowItem的属性) {Long} setOfBookId 账套ID
     * @apiSuccess (CashFlowItem的属性) {String} flowCode 现金流量项代码
     * @apiSuccess (CashFlowItem的属性) {String} description 现金流量项描述
     * @apiParamExample {json}
     * api/cash/default/flowitems/queryNotSaveFlowItem?&page=0&size=10&flowCode=&description=&setOfBookId=937515627984846850&isEnabled=true&transactionClassId=987208008816254977
     * @apiSuccessExample {json} 成功返回值
     * [
     *  {
     *      "createdBy":177691,
     *      "createdDate":"2018-04-24T17:51:54+08:00",
     *      "description":"111",
     *      "flowCode":"123",
     *      "id":"988717155328057345",
     *      "isDeleted":false,
     *      "isEnabled":true,
     *      "lastUpdatedBy":177691,
     *      "lastUpdatedDate":"2018-04-25T10:20:30+08:00",
     *      "setOfBookId":"937515627984846850",
     *      "versionNumber":6
     *  }
     * ]
     */

    @ApiOperation(value = "根据条件查询现金事务分类尚未分配的现金流量项", notes = "根据条件查询现金事务分类尚未分配的现金流量项 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/queryNotSaveFlowItem")
    public ResponseEntity<List<CashFlowItem>> getNotSaveFlowItem(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId") Long setOfBookId,
            @ApiParam(value = "现金事务分类ID") @RequestParam(value = "transactionClassId") Long transactionClassId,
            @ApiParam(value = "现金流量项代码") @RequestParam(value = "flowCode", required = false) String flowCode,
            @ApiParam(value = "现金流量项名称") @RequestParam(value = "description", required = false) String description,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashFlowItem> list = cashDefaultFlowItemService.getNotSaveFlowItem(setOfBookId,transactionClassId,flowCode,description,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/default/flowitems//queryNotSaveFlowItem");
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * 批量新增 现金事务分类关联现金流量表
     *
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/cash/default/flowitems/batch 【现金事务分类分配现金流量项】批量新增
     * @apiDescription 批量新增现金事务分类分配现金流量项
     * @apiGroup PaymentService
     * @apiParamExample {json}
     * [
     * {
     *   "transactionClassId": 1,
     *   "cashFlowItemId": 1,
     *   "defaultFlag": false
     * },
     * {
     *   "transactionClassId": 1,
     *   "cashFlowItemId": 2,
     *   "defaultFlag": false
     * }
     * ]
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "905829745399816194",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-08T00:26:55.462+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-08T00:26:55.462+08:00",
     *   "lastUpdatedBy": 11,
     *   "transactionClassId": 1,
     *   "cashFlowItemId": 1,
     *   "defaultFlag": false,
     *   "versionNumber": 1
     * },
     * {
     *   "id": "905829745424982017",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-08T00:26:55.47+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-08T00:26:55.47+08:00",
     *   "lastUpdatedBy": 11,
     *   "transactionClassId": 1,
     *   "cashFlowItemId": 2,
     *   "defaultFlag": false,
     *   "versionNumber": 1
     * }
     * ]
     */

    @ApiOperation(value = "批量新增现金事务分类分配现金流量项", notes = "批量新增现金事务分类分配现金流量项 开发:")
    @PostMapping("/batch")
    public ResponseEntity<List<CashDefaultFlowItem>> createCashDefaultFlowItemBatch(@ApiParam(value = "现金事务分类关联现金流量列表") @RequestBody List<CashDefaultFlowItem> list){
        return ResponseEntity.ok(cashDefaultFlowItemService.createCashDefaultFlowItemBatch(list));
    }

    /**
     * 批量修改 现金事务分类关联现金流量表
     *
     * @param list
     * @return
     */
    /**
     * @api {PUT} /api/cash/default/flowitems/batch 【现金事务分类分配现金流量项】批量修改
     * @apiDescription 批量修改现金事务分类分配现金流量项
     * @apiGroup PaymentService
     * @apiParamExample {json}
     * [
     * {
     *   "id": 905825076292210690,
     *   "defaultFlag": true,
     *   "versionNumber": 1
     * }
     * ]
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "905825076292210690",
     *   "isEnabled": null,
     *   "isDeleted": null,
     *   "createdDate": null,
     *   "createdBy": null,
     *   "lastUpdatedDate": null,
     *   "lastUpdatedBy": null,
     *   "transactionClassId": null,
     *   "cashFlowItemId": null,
     *   "defaultFlag": true,
     *   "versionNumber": 1
     * }
     * ]
     */

    @ApiOperation(value = "批量修改现金事务分类分配现金流量项", notes = "批量修改现金事务分类分配现金流量项 开发:")
    @PutMapping("/batch")
    public ResponseEntity<List<CashDefaultFlowItem>> updateCashDefaultFlowItemBatch(@ApiParam(value = "现金事务分类关联现金流量列表") @RequestBody List<CashDefaultFlowItem> list){
        return ResponseEntity.ok(cashDefaultFlowItemService.updateCashDefaultFlowItemBatch(list));
    }

    /**
     * 批量删除 现金事务分类关联现金流量表
     *
     * @param list
     * @return
     */
    /**
     * @api {DELETE} /api/cash/default/flowitems/batch 【现金事务分类分配现金流量项】批量删除
     * @apiDescription 批量删除现金事务分类分配现金流量项
     * @apiGroup PaymentService
     * @apiParamExample {json}
     * [
     *   905829745399816194,
     *   905829745424982017
     * ]
     * @apiSuccessExample {json} 成功返回值
     * {
     *
     * }
     */

    @ApiOperation(value = "批量删除现金事务分类分配现金流量项", notes = "批量删除现金事务分类分配现金流量项 开发:")
    @DeleteMapping("/batch")
    public ResponseEntity deleteCashDefaultFlowItemBatch(@ApiParam(value = "现金事务分类关联现金流量列表") @RequestBody List<Long> list){
        cashDefaultFlowItemService.deleteCashDefaultFlowItemBatch(list);
        return ResponseEntity.ok().build();
    }

    /**
     * 给artemis、prepayment 提供
     * 根据现金事务分类ID->transactionClassId，返回现金事务分类code、现金事务分类name，
     * 以及该现金事务分类下的默认现金流量项的code和name
     *
     * @param transactionClassId
     * @return
     */

    @ApiOperation(value = "根据现金事务分类ID返回现金流量项信息", notes = "根据现金事务分类ID返回现金流量项信息 开发:")
    @GetMapping("/queryByTransactionClassId/{transactionClassId}")
    public ResponseEntity<CashDefaultFlowItem> getCashDefaultFlowItemByTransactionClassId(@PathVariable Long transactionClassId){
        return ResponseEntity.ok(cashDefaultFlowItemService.getCashDefaultFlowItemByTransactionClassId(transactionClassId));
    }
}
