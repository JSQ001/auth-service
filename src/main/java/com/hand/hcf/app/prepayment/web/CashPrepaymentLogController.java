package com.hand.hcf.app.prepayment.web;

import com.hand.hcf.app.prepayment.service.PrepaymentLogService;
import com.hand.hcf.app.prepayment.web.dto.PrePaymentLogDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 刘亮 on 2018/1/26.
 */
@RestController
@RequestMapping("/api/prepayment/log")
public class CashPrepaymentLogController {

    private final PrepaymentLogService prepaymentLogService;

    public CashPrepaymentLogController(PrepaymentLogService prepaymentLogService) {
        this.prepaymentLogService = prepaymentLogService;
    }


    /**
     * @apiDescription 查询日志
     * @api {get} /api/prepayment/log/get/all/by/id?id=984318748081782785 【预付款日志】 查询
     * @apiGroup PrepaymentService
     * @apiParam {Long} id 头ID
     * @apiSuccess {Long} id  日志表ID
     * @apiSuccess {Long} headerId 头ID
     * @apiSuccess {Long} userId  操作用户ID
     * @apiSuccess {String} userName 用户名
     * @apiSuccess {String} userCode 用户code
     * @apiSuccess {String} operationType 操作类型
     * @apiSuccess {String} operationTypeName 操作类型名称
     * @apiSuccess {ZonedDateTime} operationTime 操作时间
     * @apiSuccess {String} operationMessage 操作意见
     * @apiSuccessExample {json} 成功返回值:
    []
     *@apiErrorExample {json} 错误返回值:
    {
    "message": "获取预付款状态值列表异常！",
    "errorCode": "11011"
    }
     */
    @GetMapping("/get/all/by/id")
    public ResponseEntity<List<PrePaymentLogDTO>> getAllById(@RequestParam Long id){
        return ResponseEntity.ok(prepaymentLogService.getAll(id));
    }
}
