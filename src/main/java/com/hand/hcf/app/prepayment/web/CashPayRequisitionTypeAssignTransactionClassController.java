package com.hand.hcf.app.prepayment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.CashTransactionClassCO;
import com.hand.hcf.app.common.co.CashTransactionClassForOtherCO;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignTransactionClass;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeAssignTransactionClassService;
import com.hand.hcf.app.core.util.PaginationUtil;
import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 韩雪 on 2017/10/25.
 */
@Api(tags = "预付款单类型关联的现金事务分类表")
@RestController
@RequestMapping("/api/cash/pay/requisition/type/assign/transaction/classes")
public class CashPayRequisitionTypeAssignTransactionClassController {
    private final CashPayRequisitionTypeAssignTransactionClassService cashSobPayReqTypeAssignTransactionClassService;

    public CashPayRequisitionTypeAssignTransactionClassController(CashPayRequisitionTypeAssignTransactionClassService cashSobPayReqTypeAssignTransactionClassService){
        this.cashSobPayReqTypeAssignTransactionClassService = cashSobPayReqTypeAssignTransactionClassService;
    }

    /**
     * 批量新增 预付款单类型关联的现金事务分类表
     *
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/cash/pay/requisition/type/assign/transaction/classes/batch 【类型关联现金事务】 增现金事务
     * @apiDescription 批量新增 预付款单类型关联的现金事务分类表
     * @apiGroup PrepaymentService
     * @apiParam {Long} sobPayReqTypeId 预付款单类型ID
     * @apiParam {Long} transactionClassId 现金事务分类ID
     * @apiParam {String} [transactionClassCode] 现金事务分类代码
     * @apiParam {String} [transactionClassName] 现金事务分类名称
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} sobPayReqTypeId 预付款单类型ID
     * @apiSuccess {Long} transactionClassId 现金事务分类ID
     * @apiSuccess {String} [transactionClassCode] 现金事务分类代码
     * @apiSuccess {String} [transactionClassName] 现金事务分类名称
     * @apiParamExample {json} 请求参数:
    [
    {
    "sobPayReqTypeId":"970961962148040712",
    "transactionClassId":"975984917265620994",
    "transactionClassCode":"Test",
    "transactionClassName":"新增测试"
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1003829890784854018",
    "sobPayReqTypeId": "970961962148040712",
    "transactionClassId": "975984917265620994",
    "transactionClassCode": "Test",
    "transactionClassName": "新增测试"
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "同一预付款单类型下的现金事务分类不允许重复!",
    "errorCode": "10203"
    }
     */
    @PostMapping("/batch")
    @ApiOperation(value = "批量新增 预付款单类型关联的现金事务分类表", notes = "批量新增 预付款单类型关联的现金事务分类表 开发:韩雪")
    public ResponseEntity<List<CashPayRequisitionTypeAssignTransactionClass>> createCashPayRequisitionTypeAssignTransactionClassBatch(@ApiParam(value = "预付款单类型关联的现金事务分类表") @RequestBody List<CashPayRequisitionTypeAssignTransactionClass> list){
        return ResponseEntity.ok(cashSobPayReqTypeAssignTransactionClassService.createCashPayRequisitionTypeAssignTransactionClassBatch(list));
    }

    /**
     * 批量修改 预付款单类型关联的现金事务分类表
     *
     * @param list
     * @return
     */
    /**
     * @api {PUT} /api/cash/pay/requisition/type/assign/transaction/classes/batch 【类型关联现金事务】 改现金事务
     * @apiDescription 批量修改 预付款单类型关联的现金事务分类表
     * @apiGroup PrepaymentService
     * @apiParam {Long} id  主键id
     * @apiParam {Long} sobPayReqTypeId 预付款单类型ID
     * @apiParam {Long} transactionClassId 现金事务分类ID
     * @apiParam {String} [transactionClassCode] 现金事务分类代码
     * @apiParam {String} [transactionClassName] 现金事务分类名称
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} sobPayReqTypeId 预付款单类型ID
     * @apiSuccess {Long} transactionClassId 现金事务分类ID
     * @apiSuccess {String} [transactionClassCode] 现金事务分类代码
     * @apiSuccess {String} [transactionClassName] 现金事务分类名称
     * @apiParamExample {json} 请求参数:
    [
    {
    "sobPayReqTypeId":"970961962148040712",
    "transactionClassId":"975984917265620995",
    "transactionClassCode":"Test1",
    "transactionClassName":"新增测试1"
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "sobPayReqTypeId":"970961962148040712",
    "transactionClassId":"975984917265620995",
    "transactionClassCode":"Test1",
    "transactionClassName":"新增测试1"
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "同一预付款单类型下的现金事务分类不允许重复!",
    "errorCode": "10203"
    }
     */
    @PutMapping("/batch")
    @ApiOperation(value = "批量修改 预付款单类型关联的现金事务分类表", notes = "批量修改 预付款单类型关联的现金事务分类表 开发:韩雪")
    public ResponseEntity<List<CashPayRequisitionTypeAssignTransactionClass>> updateCashPayRequisitionTypeAssignTransactionClassBatch(@ApiParam(value = "预付款单类型关联的现金事务分类表") @RequestBody List<CashPayRequisitionTypeAssignTransactionClass> list){
        return ResponseEntity.ok(cashSobPayReqTypeAssignTransactionClassService.updateCashPayRequisitionTypeAssignTransactionClassBatch(list));
    }

    /**
     * 批量删除 预付款单类型关联的现金事务分类表(物理删除)
     *
     * @param list
     * @return
     */
    /**
     * @api {DELETE} /api/cash/pay/requisition/type/assign/transaction/classes/batch 【类型关联现金事务】 删现金事务
     * @apiDescription 批量删除 预付款单类型关联的现金事务分类表(物理删除)
     * @apiGroup PrepaymentService
     * @apiParam {List(Long)} id  主键id
     * @apiParamExample {json} 请求参数:
    {
    "970961962148040712"
    }
     */
    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除 预付款单类型关联的现金事务分类表(物理删除)", notes = "批量删除 预付款单类型关联的现金事务分类表(物理删除) 开发:韩雪")
    public ResponseEntity deleteCashPayRequisitionTypeAssignTransactionClassBatch(@ApiParam(value = "主键id") @RequestBody List<Long> list){
        cashSobPayReqTypeAssignTransactionClassService.deleteCashPayRequisitionTypeAssignTransactionClassBatch(list);
        return ResponseEntity.ok().build();
    }

    /**
     *根据预付款单类型ID->sobPayReqTypeId 查询出与之对应的现金事务分类表中的数据，前台显示现金事务分类表代码以及现金事务分类表名称(分页)
     *
     * @param sobPayReqTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/pay/requisition/type/assign/transaction/classes/query?sobPayReqTypeId=970961962148040712&page=0&size=10 【类型关联现金事务】 查现金事务
     * @apiDescription 根据预付款单类型ID查询现金事务分类表，分页
     * @apiGroup PrepaymentService
     * @apiParam {Long} sobPayReqTypeId 预付款单类型ID
     * @apiParam {int} page
     * @apiParam {int} size
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} sobPayReqTypeId 预付款单类型ID
     * @apiSuccess {Long} transactionClassId 现金事务分类ID
     * @apiSuccess {String} transactionClassCode 现金事务分类代码
     * @apiSuccess {String} transactionClassName 现金事务分类名称
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1003829890784854018",
    "sobPayReqTypeId": "970961962148040712",
    "transactionClassId": "975984917265620994",
    "transactionClassCode": "CSH0320003",
    "transactionClassName": "HS003"
    },
    {
    "id": "1003831807594364929",
    "sobPayReqTypeId": "970961962148040712",
    "transactionClassId": "975984917265620995",
    "transactionClassCode": null,
    "transactionClassName": null
    }
    ]
     */
    @GetMapping("/query")
    @ApiOperation(value = "根据预付款单类型ID查询现金事务分类表，分页", notes = "根据预付款单类型ID查询现金事务分类表，分页 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<CashPayRequisitionTypeAssignTransactionClass>> getCashPayRequisitionTypeAssignTransactionClassByCond(
            @ApiParam(value = "预付款单类型ID") @RequestParam(value = "sobPayReqTypeId") Long sobPayReqTypeId,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionTypeAssignTransactionClass> list = cashSobPayReqTypeAssignTransactionClassService.getCashPayRequisitionTypeAssignTransactionClassByCond(sobPayReqTypeId,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/type/assign/transaction/classes/query");
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }

    /**
     * 根据预付款单类型ID->sobPayReqTypeId 查询出与之对应的现金事务分类表中的数据，前台显示现金事务分类表代码以及现金事务分类表名称(不分页)
     *
     * @param sobPayReqTypeId
     * @return
     */
    /**
     * @api {GET} /api/cash/pay/requisition/type/assign/transaction/classes/queryAll?sobPayReqTypeId=970961962148040712 【类型关联现金事务】 查现金事务
     * @apiDescription 根据预付款单类型ID查询现金事务分类表，不分页
     * @apiGroup PrepaymentService
     * @apiParam {Long} sobPayReqTypeId 预付款单类型ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} sobPayReqTypeId 预付款单类型ID
     * @apiSuccess {Long} transactionClassId 现金事务分类ID
     * @apiSuccess {String} transactionClassCode 现金事务分类代码
     * @apiSuccess {String} transactionClassName 现金事务分类名称
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1003829890784854018",
    "sobPayReqTypeId": "970961962148040712",
    "transactionClassId": "975984917265620994",
    "transactionClassCode": "CSH0320003",
    "transactionClassName": "HS003"
    },
    {
    "id": "1003831807594364929",
    "sobPayReqTypeId": "970961962148040712",
    "transactionClassId": "975984917265620995",
    "transactionClassCode": null,
    "transactionClassName": null
    }
    ]
     */
    @GetMapping("/queryAll")
    @ApiOperation(value = "根据预付款单类型ID查询现金事务分类表，不分页", notes = "根据预付款单类型ID查询现金事务分类表，不分页 开发:韩雪")
    public ResponseEntity<List<CashPayRequisitionTypeAssignTransactionClass>> getCashPayRequisitionTypeAssignTransactionClassAllByCond(
            @ApiParam(value = "预付款单类型ID") @RequestParam(value = "sobPayReqTypeId") Long sobPayReqTypeId){
        List<CashPayRequisitionTypeAssignTransactionClass> list = cashSobPayReqTypeAssignTransactionClassService.getCashPayRequisitionTypeAssignTransactionClassAllByCond(sobPayReqTypeId);
        return ResponseEntity.ok(list);
    }

    /**
     * 获取当前账套下，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类
     *
     * @param setOfBookId
     * @return
     */
    /**
     * @api {GET} /api/cash/pay/requisition/type/assign/transaction/classes/queryCashTransactionClass/937515627984846850 【类型关联现金事务】 查现金事务
     * @apiDescription 获取当前账套下，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类
     * @apiGroup PrepaymentService
     * @apiParam {Long} setOfBookId 账套ID
     * @apiSuccess {Long} id  现金事务分类ID
     * @apiSuccess {String} typeCode 现金事务类型code
     * @apiSuccess {String} classCode 现金事务分类代码
     * @apiSuccess {String} description 现金事务分类名称
     * @apiSuccess {String} setOfBookCode 账套代码
     * @apiSuccess {String} setOfBookName 账套名称
     * @apiSuccess {Boolean} assigned 是否被分配
     * @apiSuccess {Long} versionNumber 版本号
     * @apiSuccess {Boolean} isEnabled    启用标志
     * @apiSuccess {Boolean} isDeleted    删除标志
     * @apiSuccess {ZonedDateTime} createdDate 创建时间
     * @apiSuccess {Long} createdBy 创建人id
     * @apiSuccess {ZonedDateTime} lastUpdatedDate 最后更新时间
     * @apiSuccess {Long} lastUpdatedBy 最后更新人id
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "958670974595686401",
    "isEnabled": true,
    "isDeleted": false,
    "createdDate": "2018-01-31T19:59:06+08:00",
    "createdBy": 177601,
    "lastUpdatedDate": "2018-04-20T13:52:52+08:00",
    "lastUpdatedBy": 177601,
    "versionNumber": 3,
    "setOfBookId": "937515627984846850",
    "typeCode": "PREPAYMENT",
    "typeName": null,
    "classCode": "hx001",
    "description": "hx001",
    "setOfBookCode": null,
    "setOfBookName": null,
    "assigned": null
    }
    ]
     */
    @GetMapping("/queryCashTransactionClass/{setOfBookId}")
    @ApiOperation(value = "获取当前账套下，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类", notes = "获取当前账套下，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类 开发:韩雪")
    public ResponseEntity<List<CashTransactionClassCO>> getCashTransactionClassBySetOfBookId(@PathVariable long setOfBookId){
        return ResponseEntity.ok(cashSobPayReqTypeAssignTransactionClassService.getCashTransactionClassBySetOfBookId(setOfBookId));
    }

    /**
     * 根据所选范围 查询现金事务分类(分页)
     *
     * @param forOtherDTO
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/pay/requisition/type/assign/transaction/classes/queryByRange?page=0&size=10 【类型关联现金事务】 查现金事务
     * @apiDescription 根据所选范围 查询现金事务分类，分页)
     * @apiGroup PrepaymentService
     * @apiParam {Long} setOfBookId 账套ID
     * @apiParam {String} range 查询范围
     * @apiParam {String} classCode 现金事务分类代码
     * @apiParam {String} description 现金事务分类名称
     * @apiParam {List(Long)} transactionClassIdList 已选现金事务分类id集合
     * @apiSuccess {Long} id  现金事务分类ID
     * @apiSuccess {String} typeCode 现金事务类型code
     * @apiSuccess {String} classCode 现金事务分类代码
     * @apiSuccess {String} description 现金事务分类名称
     * @apiSuccess {Boolean} assigned 是否被分配
     * @apiSuccessExample {json} 成功返回值:
    {
    "offset": 0,
    "limit": 10,
    "total": 9,
    "size": 10,
    "pages": 1,
    "current": 1,
    "searchCount": true,
    "openSort": true,
    "orderByField": null,
    "records": [
    {
    "id": "961560746796359682",
    "typeCode": null,
    "classCode": "11",
    "description": "测试",
    "assigned": true
    },
    {
    "id": "975984582782459905",
    "typeCode": null,
    "classCode": "CSH0320001",
    "description": "HS001",
    "assigned": true
    }
    ],
    "condition": null,
    "asc": true,
    "offsetCurrent": 0
    }
     */
    @PostMapping(value = "/queryByRange")
    @ApiOperation(value = "获取当前账套下，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类", notes = "获取当前账套下，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<CashTransactionClassCO>> getCashPayRequisitionTypeAssignTransactionClassByCond (
            @ApiParam(value = "其他DTO") @RequestBody @Valid CashTransactionClassForOtherCO forOtherDTO,
            @ApiIgnore Pageable pageable) throws Exception {
        Page page = PageUtil.getPage(pageable);
        Page<CashTransactionClassCO> list = cashSobPayReqTypeAssignTransactionClassService.getCashPayRequisitionTypeAssignTransactionClassByCond(forOtherDTO, page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/type/assign/transaction/classes/queryByRange");
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }
}
