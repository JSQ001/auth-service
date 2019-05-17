package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.payment.domain.CashTransactionLog;
import com.hand.hcf.app.payment.service.CashTransactionLogService;
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
 * Created by 韩雪 on 2017/9/30.
 */
@Api(tags = "通用支付平台日志API")
@RestController
@RequestMapping("/api/cash/transaction/logs")
public class CashTransactionLogController {
    private final CashTransactionLogService cashTransactionLogService;

    public CashTransactionLogController(CashTransactionLogService cashTransactionLogService){
        this.cashTransactionLogService = cashTransactionLogService;
    }

    /**
     * 新增 通用支付平台日志表
     *
     * @param cashTransactionLog
     * @return
     */

    @ApiOperation(value = "新增通用支付平台日志表", notes = "新增通用支付平台日志表 开发:")
    @PostMapping()
    public ResponseEntity<CashTransactionLog> createCashTransactionLog(@ApiParam(value = "通用支付平台日志") @RequestBody CashTransactionLog cashTransactionLog){
        return ResponseEntity.ok(cashTransactionLogService.createCashTransactionLog(cashTransactionLog));
    }

    /**
     * 自定义条件查询 通用支付平台日志表(分页)
     *
     * @param paymentDetailId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/transaction/logs/query 【通用支付平台日志】分页查询
     * @apiDescription 根据条件分页查询通用支付平台日志
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} paymentDetailId 支付明细表ID
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiSuccess (返回参数) {List} list 返回泛型为CashTransactionLog的List集合
     * @apiSuccess (CashTransactionLog的属性) {Long} id 通用支付平台日志表ID
     * @apiSuccess (CashTransactionLog的属性) {Long} paymentDetailId 支付明细表ID
     * @apiSuccess (CashTransactionLog的属性) {Long} userId 操作用户ID
     * @apiSuccess (CashTransactionLog的属性) {String} operationType 操作类型 NEW 新增,PEND_MODIFY 待付修改,PEND_PAY 待付支付,ENSURE_SUCCESS 确认支付,ENSURE_FAIL 确认失败,PAY_MODIFY 支付修改,REPAY 重新支付
     * @apiSuccess (CashTransactionLog的属性) {ZonedDateTime} operationTime 操作时间
     * @apiSuccess (CashTransactionLog的属性) {String} remark 备注
     * @apiSuccess (CashTransactionLog的属性) {String} userName 操作用户姓名
     * @apiSuccess (CashTransactionLog的属性) {String} operationTypeName 操作类型名称
     * @apiSuccess (CashTransactionLog的属性) {byte[]} bankMessage 银行报文
     */

    @ApiOperation(value = "通用支付平台日志分页查询", notes = "通用支付平台日志分页查询 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query")
    public ResponseEntity<List<CashTransactionLog>> getCashTransactionLogByCond(
            @ApiParam(value = "支付明细表ID") @RequestParam(value = "paymentDetailId") Long paymentDetailId,@ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionLog> list = cashTransactionLogService.getCashTransactionLogByCond(paymentDetailId,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/logs/query");
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }

    /**
     * 自定义条件查询 通用支付平台日志表(不分页)
     *
     * @param paymentDetailId
     * @return
     */
    /**
     * @api {GET} /api/cash/transaction/logs/queryAll 【通用支付平台日志】不分页查询
     * @apiDescription 根据条件不分页查询通用支付平台日志
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} paymentDetailId 支付明细表ID
     * @apiSuccess (返回参数) list {List} 返回泛型为CashTransactionLog的List集合
     * @apiSuccess (CashTransactionLog的属性) {Long} id 通用支付平台日志表ID
     * @apiSuccess (CashTransactionLog的属性) {Long} paymentDetailId 支付明细表ID
     * @apiSuccess (CashTransactionLog的属性) {Long} userId 操作用户ID
     * @apiSuccess (CashTransactionLog的属性) {String} operationType 操作类型 NEW 新增,PEND_MODIFY 待付修改,PEND_PAY 待付支付,ENSURE_SUCCESS 确认支付,ENSURE_FAIL 确认失败,PAY_MODIFY 支付修改,REPAY 重新支付
     * @apiSuccess (CashTransactionLog的属性) {ZonedDateTime} operationTime 操作时间
     * @apiSuccess (CashTransactionLog的属性) {String} remark 备注
     * @apiSuccess (CashTransactionLog的属性) {String} userName 操作用户姓名
     * @apiSuccess (CashTransactionLog的属性) {String} operationTypeName 操作类型名称
     * @apiSuccess (CashTransactionLog的属性) {byte[]} bankMessage 银行报文
     */

    @ApiOperation(value = "通用支付平台日志不分页查询", notes = "通用支付平台日志不分页查询 开发:")
    @GetMapping("/queryAll")
    public ResponseEntity<List<CashTransactionLog>> getCashTransactionLogAllByCond(
            @ApiParam(value = "支付明细表ID") @RequestParam(value = "paymentDetailId") Long paymentDetailId){
        List<CashTransactionLog> list = cashTransactionLogService.getCashTransactionLogAllByCond(paymentDetailId);
        return ResponseEntity.ok(list);
    }

    /**
     * 根据支付表id查询对应的支付明细id的日志
     *
     * @param dateId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/transaction/logs/queryByDataId 【通用支付平台日志】根据支付表id分页查询对应的支付明细id日志
     * @apiDescription 根据支付表id分页查询对应的支付明细id的日志
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} dateId 支付表id
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (Pageable的属性) {Integer} page 页码
     * @apiParam (Pageable的属性) {Integer} size 每页条数
     * @apiSuccess (返回参数) {List} list 返回泛型为CashTransactionLog的List集合
     * @apiSuccess (CashTransactionLog的属性) {Long} id 通用支付平台日志表ID
     * @apiSuccess (CashTransactionLog的属性) {Long} paymentDetailId 支付明细表ID
     * @apiSuccess (CashTransactionLog的属性) {Long} userId 操作用户ID
     * @apiSuccess (CashTransactionLog的属性) {String} operationType 操作类型 NEW 新增,PEND_MODIFY 待付修改,PEND_PAY 待付支付,ENSURE_SUCCESS 确认支付,ENSURE_FAIL 确认失败,PAY_MODIFY 支付修改,REPAY 重新支付
     * @apiSuccess (CashTransactionLog的属性) {ZonedDateTime} operationTime 操作时间
     * @apiSuccess (CashTransactionLog的属性) {String} remark 备注
     * @apiSuccess (CashTransactionLog的属性) {String} userName 操作用户姓名
     * @apiSuccess (CashTransactionLog的属性) {String} operationTypeName 操作类型名称
     * @apiSuccess (CashTransactionLog的属性) {byte[]} bankMessage 银行报文
     */

    @ApiOperation(value = "根据支付表id分页查询对应的支付明细id日志", notes = "根据支付表id分页查询对应的支付明细id日志 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/queryByDataId")
    public ResponseEntity<List<CashTransactionLog>> getCashTransactionLogByDataId(
            @ApiParam(value = "支付表id") @RequestParam Long dateId,@ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionLog> list = cashTransactionLogService.getCashTransactionLogByDataId(dateId,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cash/transaction/logs/queryByDataId");
        return new ResponseEntity(list, headers, HttpStatus.OK);
    }
}
