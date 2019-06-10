package com.hand.hcf.app.payment.web;

import com.hand.hcf.app.payment.service.DetailLogService;
import com.hand.hcf.app.payment.web.dto.DetailLogDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.*;

import java.util.List;

/**
 * Created by 刘亮 on 2018/4/8.
 */

@Api(tags = "支付明细日志API")
@RestController
@RequestMapping("/api/detail/log")
public class DetailLogController {
    private final DetailLogService detailLogService;

    public DetailLogController(DetailLogService detailLogService) {
        this.detailLogService = detailLogService;
    }


    /**
     * @api {GET} {{payment-service_url}}/api/detail/log/get/by/detail/id【日志】支付明细日志
     * @apiGroup PaymentService
     * @apiDescription 收款查询
     *
     * @apiParam (请求参数) {long} detailId 类型
     * @apiParam (请求参数) {int} flag 标志
     *
     * @apiSuccessExample {json} 成功返回样例:
     *   [
     *          {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "operation": 1,
     *            }
     *    ]
     *
     *
     */

    @ApiOperation(value = "查询单条日志明细", notes = "根据id查询日志详情 开发：刘亮")
    @GetMapping("/get/by/detail/id")
    public ResponseEntity<List<DetailLogDTO>> getLogsByDetailId(@ApiParam(value = "详情id") @RequestParam Long detailId){
        List<DetailLogDTO> logs = detailLogService.getLogsByDetailId(detailId);
        return ResponseEntity.ok(logs);
    }




}
