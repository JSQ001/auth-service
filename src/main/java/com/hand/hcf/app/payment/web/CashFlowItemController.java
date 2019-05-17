package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CashFlowItemCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.payment.domain.CashFlowItem;
import com.hand.hcf.app.payment.service.CashFlowItemService;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 韩雪 on 2017/9/6.
 */
@Api(tags = "现金流量项Controller")
@RestController
@RequestMapping("/api/cash/flow/items")
public class CashFlowItemController {
    private final CashFlowItemService cashFlowItemService;

    public CashFlowItemController(CashFlowItemService cashFlowItemService){
        this.cashFlowItemService = cashFlowItemService;
    }

    /**
     * 新增 现金流量项表
     *
     * @param cashFlowItem
     * @return
     */
    /**
     * @api {POST} /api/cash/flow/items 【现金流量项】单个新增
     * @apiDescription 新增单个现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} flowCode 现金流量项代码
     * @apiParam (请求参数) {String} description 现金流量项描述
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} setOfBookId 账套ID
     * @apiSuccess (返回参数) {String} flowCode 现金流量项代码
     * @apiSuccess (返回参数) {String} description 现金流量项描述
     * @apiParamExample {json} 请求参数
     * {
     *   "setOfBookId": 1,
     *   "flowCode": "cbc",
     *   "description": "现金等价物转换"
     * }
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "905388917401387009",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-06T19:15:13.684+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-06T19:15:13.684+08:00",
     *   "lastUpdatedBy": 11,
     *   "setOfBookId": 1,
     *   "flowCode": "cbc",
     *   "description": "现金等价物转换",
     *   "versionNumber": 1
     * }
     */

    @ApiOperation(value = "新增单个现金流量项", notes = "新增单个现金流量项 开发:")
    @PostMapping
    public ResponseEntity<CashFlowItem> createCashFlowItem(@ApiParam(value = "现金流量项") @RequestBody CashFlowItem cashFlowItem){
        return ResponseEntity.ok(cashFlowItemService.createCashFlowItem(cashFlowItem));
    }

    /**
     * 修改 现金流量项表
     *
     * @param cashFlowItem
     * @return
     */
    /**
     * @api {PUT} /api/cash/flow/items 【现金流量项】单个修改
     * @apiDescription 修改单个现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} id 主键ID
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} flowCode 现金流量项代码
     * @apiParam (请求参数) {String} description 现金流量项描述
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
     * @apiSuccess (返回参数) {String} flowCode 现金流量项代码
     * @apiSuccess (返回参数) {String} description 现金流量项描述
     * @apiParamExample {json} 请求参数
     * {
     *   "id": 905388917401387000,
     *   "description": "hx现金等价物转换",
     *   "versionNumber": 1
     * }
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "905388917401387009",
     *   "isEnabled": null,
     *   "isDeleted": null,
     *   "createdDate": null,
     *   "createdBy": null,
     *   "lastUpdatedDate": null,
     *   "lastUpdatedBy": null,
     *   "setOfBookId": null,
     *   "flowCode": null,
     *   "description": "hx现金等价物转换",
     *   "versionNumber": 1
     * }
     */

    @ApiOperation(value = "修改现金流量项表", notes = "修改现金流量项表 开发:")
    @PutMapping
    public ResponseEntity<CashFlowItem> updateCashFlowItem(@ApiParam(value = "现金流量项") @RequestBody CashFlowItem cashFlowItem){
        return ResponseEntity.ok(cashFlowItemService.updateCashFlowItem(cashFlowItem));
    }

    /**
     * 删除 现金流量项表(逻辑删除)
     *
     * @param id
     * @return
     */
    /**
     * @apiDefine myID
     * @apiParam (请求参数) {Long} id 现金流量项待删除的ID
     */
    /**
     * @apiDefine MyError
     * @apiError UserNotFound The <code>id</code> of the User was not found.
     */
    /**
     * @api {DELETE} /api/cash/flow/items/{id} 【现金流量项】单个删除
     * @apiDescription 根据id删除单个现金流量项
     * @apiGroup PaymentService
     * @apiUse myID
     * @apiUse MyError
     */

    @ApiOperation(value = "逻辑删除现金流量项表", notes = "逻辑删除现金流量项表 开发:")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCashFlowItem(@PathVariable Long id){
        cashFlowItemService.deleteCashFlowItem(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据ID查询 现金流量项表
     *
     * @param id
     * @return
     */
    /**
     * @api {GET} /api/cash/flow/items/{id} 【现金流量项】单个查询
     * @apiDescription 根据id查询单个现金流量项
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * /api/cash/flow/items/905376955439280129
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "905376955439280129",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-06T18:27:42+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-06T18:27:42+08:00",
     *   "lastUpdatedBy": 11,
     *   "setOfBookId": 1,
     *   "flowCode": "hx",
     *   "description": "定期存款",
     *   "versionNumber": 1
     * }
     */

    @ApiOperation(value = "根据id查询单个现金流量项", notes = "根据id查询单个现金流量项 开发:")
    @GetMapping("/{id}")
    public ResponseEntity<CashFlowItem> getCashFlowItem(@PathVariable Long id){
        return ResponseEntity.ok(cashFlowItemService.selectById(id));
    }

    /**
     * 自定义条件查询 现金流量项表(分页)
     *
     * @param setOfBookId
     * @param flowCode
     * @param description
     * @param isEnabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/flow/items/query 【现金流量项】分页查询
     * @apiDescription 根据条件分页查询现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} [flowCode] 现金流量项代码
     * @apiParam (请求参数) {String} [description] 现金流量项名称
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiParamExample {json} 请求参数
     * /api/cash/flow/items/query?flowCode=hx
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "905376955439280129",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-06T18:27:42+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-06T18:27:42+08:00",
     *   "lastUpdatedBy": 11,
     *   "setOfBookId": 1,
     *   "flowCode": "hx",
     *   "description": "定期存款",
     *   "versionNumber": 1
     * }
     * ]
     */

    @ApiOperation(value = "根据条件分页查询现金流量项", notes = "根据条件分页查询现金流量项 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query")
    public ResponseEntity<List<CashFlowItem>> getCashFlowItemByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId") Long setOfBookId,
            @ApiParam(value = "现金流量项代码") @RequestParam(value = "flowCode", required = false) String flowCode,
            @ApiParam(value = "现金流量项名称") @RequestParam(value = "description", required = false) String description,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashFlowItem> list = cashFlowItemService.getCashFlowItemByCond(setOfBookId,flowCode, description,isEnabled, page, false);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/flow/items/query");
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 现金流量项表(分页)
     *
     * @param setOfBookId
     * @param flowCode
     * @param description
     * @param isEnabled
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/flow/items/query/enable/dataAuth 【现金流量项】分页查询
     * @apiDescription 根据条件分页查询现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} [flowCode] 现金流量项代码
     * @apiParam (请求参数) {String} [description] 现金流量项名称
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiParamExample {json} 请求参数
     * /api/cash/flow/items/query?flowCode=hx
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "905376955439280129",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-06T18:27:42+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-06T18:27:42+08:00",
     *   "lastUpdatedBy": 11,
     *   "setOfBookId": 1,
     *   "flowCode": "hx",
     *   "description": "定期存款",
     *   "versionNumber": 1
     * }
     * ]
     */

    @ApiOperation(value = "根据条件是否授权分页查询现金流量项", notes = "根据条件是否授权分页查询现金流量项 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query/enable/dataAuth")
    public ResponseEntity<List<CashFlowItem>> getCashFlowItemByCondEnableDataAuth(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId") Long setOfBookId,
            @ApiParam(value = "现金流量项代码") @RequestParam(value = "flowCode", required = false) String flowCode,
            @ApiParam(value = "现金流量项名称") @RequestParam(value = "description", required = false) String description,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashFlowItem> list = cashFlowItemService.getCashFlowItemByCond(setOfBookId,flowCode, description,isEnabled, page, true);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/flow/items/query/enable/dataAuth");
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 现金流量项表(不分页)
     *
     * @param setOfBookId
     * @param flowCode
     * @param description
     * @param isEnabled
     * @return
     */
    /**
     * @api {GET} /api/cash/flow/items/queryAll 【现金流量项】不分页查询
     * @apiDescription 根据条件不分页查询现金流量项
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} setOfBookId 账套ID
     * @apiParam (请求参数) {String} [flowCode] 现金流量项代码
     * @apiParam (请求参数) {String} [description] 现金流量项名称
     * @apiParam (请求参数) {Boolean} [isEnabled] 是否启用
     * @apiParamExample {json} 请求参数
     * /api/cash/flow/items/query?flowCode=hx
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "905376955439280129",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-06T18:27:42+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-06T18:27:42+08:00",
     *   "lastUpdatedBy": 11,
     *   "setOfBookId": 1,
     *   "flowCode": "hx",
     *   "description": "定期存款",
     *   "versionNumber": 1
     * }
     * ]
     */

    @ApiOperation(value = "根据条件不分页查询现金流量项", notes = "根据条件不分页查询现金流量项 开发:")
    @GetMapping("/queryAll")
    public ResponseEntity<List<CashFlowItem>> getCashFlowItemAllByCond(
            @ApiParam(value = "账套ID") @RequestParam(value = "setOfBookId") Long setOfBookId,
            @ApiParam(value = "现金流量项代码") @RequestParam(value = "flowCode", required = false) String flowCode,
            @ApiParam(value = "现金流量项名称") @RequestParam(value = "description", required = false) String description,
            @ApiParam(value = "是否启用") @RequestParam(value = "enabled", required = false) Boolean isEnabled){
        List<CashFlowItem> list = cashFlowItemService.getCashFlowItemAllByCond(setOfBookId,flowCode, description,isEnabled);
        return ResponseEntity.ok(list);
    }

    /**
     * 批量新增 现金流量项表
     *
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/cash/flow/items/batch 【现金流量项】批量新增
     * @apiDescription 批量新增现金流量项
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * [
     * {
     *   "setOfBookId": 2,
     *   "flowCode": "ld",
     *   "description": "收到原保险合同保费取得的现金",
     *   "versionNumber": 1
     * },
     * {
     *   "setOfBookId": 2,
     *   "flowCode": "pxt",
     *   "description": "收到的税费返还",
     *   "versionNumber": 1
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
     *   "flowCode": "ld",
     *    "description": "收到原保险合同保费取得的现金",
     *   "versionNumber": 1
     * },
     * {
     *   "id": "905390919682424833",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-06T19:23:11.256+08:00",
     *   "createdBy": 11,
     *   "lastUpdatedDate": "2017-09-06T19:23:11.256+08:00",
     *   "lastUpdatedBy": 11,
     *   "setOfBookId": 2,
     *   "flowCode": "pxt",
     *   "description": "收到的税费返还",
     *   "versionNumber": 1
     * }
     * ]
     */

    @ApiOperation(value = "批量新增现金流量项", notes = "批量新增现金流量项 开发:")
    @PostMapping("/batch")
    public ResponseEntity<List<CashFlowItem>> createCashFlowItemBatch(@ApiParam(value = "现金流量项") @RequestBody List<CashFlowItem> list){
        return ResponseEntity.ok(cashFlowItemService.createCashFlowItemBatch(list));
    }

    /**
     * 批量修改 现金流量项表
     *
     * @param list
     * @return
     */
    /**
     * @api {PUT} /api/cash/flow/items/batch 【现金流量项】批量修改
     * @apiDescription 批量修改现金流量项
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * [
     * {
     *   "id": 905390919606927400,
     *   "description": "hx收到原保险合同保费取得的现金",
     *   "versionNumber": 1
     * },
     * {
     *   "id": 905390919682424800,
     *   "description": "hx收到的税费返还",
     *   "versionNumber": 1
     * }
     * ]
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "905390919606927362",
     *   "isEnabled": null,
     *   "isDeleted": null,
     *   "createdDate": null,
     *   "createdBy": null,
     *   "lastUpdatedDate": null,
     *   "lastUpdatedBy": null,
     *   "setOfBookId": null,
     *   "flowCode": null,
     *   "description": "hx收到原保险合同保费取得的现金",
     *   "versionNumber": 1
     * },
     * {
     *   "id": "905390919682424833",
     *   "isEnabled": null,
     *   "isDeleted": null,
     *   "createdDate": null,
     *   "createdBy": null,
     *   "lastUpdatedDate": null,
     *   "lastUpdatedBy": null,
     *   "setOfBookId": null,
     *   "flowCode": null,
     *   "description": "hx收到的税费返还",
     *   "versionNumber": 1
     * }
     * ]
     */

    @ApiOperation(value = "批量修改现金流量项表", notes = "批量修改现金流量项表 开发:")
    @PutMapping("/batch")
    public ResponseEntity<List<CashFlowItem>> updateCashFlowItemBatch(@ApiParam(value = "现金流量项") @RequestBody List<CashFlowItem> list){
        return ResponseEntity.ok(cashFlowItemService.updateCashFlowItemBatch(list));
    }

    /**
     * 批量删除 现金流量项表
     *
     * @param list
     * @return
     */
    /**
     * @api {DELETE} /api/cash/flow/items/batch 【现金流量项】批量删除
     * @apiDescription 批量删除现金流量项
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

    @ApiOperation(value = "批量删除现金流量项表", notes = "批量删除现金流量项表 开发:")
    @DeleteMapping("/batch")
    public ResponseEntity deleteCashFlowItemBatch(@ApiParam(value = "现金流量项列表") @RequestBody List<Long> list){
        cashFlowItemService.deleteCashFlowItemBatch(list);
        return ResponseEntity.ok().build();
    }


    /**
     * 根据代码、名称分页查询某个账套下，启用的不在id范围内的现金流量项
     *
     * @param setOfBookId
     * @param flowCode
     * @param description
     * @param enabled
     * @param existIdList
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation(value = "分页查询现金流量项", notes = "根据代码、名称分页查询某个账套下，启用的不在id范围内的现金流量项 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "setOfBookId", value = "账套ID", dataType = "Long"),
            @ApiImplicitParam(name = "flowCode", value = "现金流量项代码", dataType = "String"),
            @ApiImplicitParam(name = "description", value = "现金流量项名称", dataType = "String"),
            @ApiImplicitParam(name = "existIdList", value = "现金流量项id集合", dataType = "List"),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @PostMapping("/query/undistributed")
    public ResponseEntity<Page<CashFlowItemCO>> getUndistributedCashFlowItemByCond(
            @RequestParam(value = "setOfBookId") Long setOfBookId,
            @RequestParam(value = "flowCode", required = false) String flowCode,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @RequestBody List<Long> existIdList,
            Pageable pageable){
        Page page = PageUtil.getPage(pageable);
        Page<CashFlowItemCO> list = cashFlowItemService.getUndistributedCashFlowItemByCond(setOfBookId,flowCode, description,enabled, existIdList, page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }
}
