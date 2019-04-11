package com.hand.hcf.app.workflow.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.workflow.domain.WorkflowTransfer;
import com.hand.hcf.app.workflow.dto.WorkflowTransferDTO;
import com.hand.hcf.app.workflow.service.WorkflowTransferService;
import com.hand.hcf.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/21 9:30
 * @version: 1.0.0
 */
@RestController
@RequestMapping("/api/workflow/transfer")
public class WorkflowTransferController {

    @Autowired
    private WorkflowTransferService workflowTransferService;

    /**
     * @api {GET} /api/workflow/transfer/query 【审批流转交-查询】
     * @apiDescription  查询审批流转交与代理
     * @apiGroup WorkflowTransferController
     * @apiParam {String} documentCategory 单据大类 801001 对公报账单，801002 预算日记账 801003 预付款单 801004 合同  801005 付款申请单 801006 费用调整单
     * @apiParam {Long} workflowId 审批流Id
     * @apiParam {Long} authorizeId 授权人Id
     * @apiParam {Long} agentId 代理人Id
     * @apiParam {ZonedDateTime} startDate 有效日期从
     * @apiParam {ZonedDateTime} endDate 有效日期至
     * @apiParam {String} authorizationNotes 备注
     * @apiParam {String} tab agent：代理  authorizer：转交 [必填]
     * @apiParamExample {json} Request-Param:
     *  http://127.0.0.1:9096/workflow/api/workflow/transfer/query?tab=authorizer
     * @apiSuccessExample {json} Success-Response:
     * [
            {
        "id": "1087278958413668353",
        "createdDate": "2019-01-21T17:21:19.018+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-21T17:21:19.019+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "authorizerId": "1083751705402064897",
        "documentCategory": "801001",
        "workflowId": "1086076030665027586",
        "agentId": "1087188765930799105",
        "startDate": "2019-01-21T10:42:03.904+08:00",
        "endDate": "2019-03-21T10:42:03.904+08:00",
        "authorizationNotes": "测试数据01"
        }
     ]
     */
    @GetMapping(value = "/query")
    public ResponseEntity<List<WorkflowTransferDTO>> pageWorkflowTransferByCond(@RequestParam(value = "documentCategory",required = false) String documentCategory,
                                                                                @RequestParam(value = "workflowId",required = false) Long workflowId,
                                                                                @RequestParam(value = "authorizeId",required = false) Long authorizeId,
                                                                                @RequestParam(value = "agentId",required = false) Long agentId,
                                                                                @RequestParam(value = "startDate",required = false) String startDate,
                                                                                @RequestParam(value = "endDate",required = false) String endDate,
                                                                                @RequestParam(value = "authorizationNotes",required = false) String authorizationNotes,
                                                                                @RequestParam(value = "tab") String tab,
                                                                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                                @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        Page mybaitsPage = PageUtil.getPage(page,size);
        List<WorkflowTransferDTO> workflowTransferList = workflowTransferService.pageWorkflowTransferByCond(documentCategory,workflowId,authorizeId,agentId,startDate,endDate,authorizationNotes,tab,mybaitsPage);
        HttpHeaders headers = PageUtil.getTotalHeader(mybaitsPage);
        return new ResponseEntity<>(workflowTransferList,headers, HttpStatus.OK);
    }

    /**
     * @api {POST} /api/workflow/transfer 【审批流转交-新建转交】
     * @apiDescription  新建转交
     * @apiGroup WorkflowTransferController
     * @apiParam {Object} WorkflowTransfer domain类
     * @apiParam {Long} documentCategory 单据大类 801001 对公报账单，801002 预算日记账 801003 预付款单 801004 合同  801005 付款申请单 801006 费用调整单
     * @apiParam {Long} workflowId 审批流
     * @apiParam {Long} agentId 代理人Id
     * @apiParam {ZonedDateTime} startDate 有效日期从
     * @apiParam {ZonedDateTime} endDate 有效日期至
     * @apiParam {String} authorizationNotes 备注
     * @apiParamExample {json} Request-Param:
     *{
        "documentCategory": "801001",
        "workflowId": "1086076030665027586",
        "agentId": "1087188765930799105",
        "startDate": "2019-01-21T10:42:03.904+08:00",
        "endDate":"2019-03-21T10:42:03.904+08:00",
        "authorizationNotes":"测试数据01"
        }
     * @apiSuccessExample {json} Success-Response:
     * {
        "id": "1087278958413668353",
        "createdDate": "2019-01-21T17:21:19.018+08:00",
        "createdBy": "1083751705402064897",
        "lastUpdatedDate": "2019-01-21T17:21:19.019+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": 1,
        "tenantId": "1083751703623680001",
        "setOfBooksId": "1083762150064451585",
        "authorizerId": "1083751705402064897",
        "documentCategory": "801001",
        "workflowId": "1086076030665027586",
        "agentId": "1087188765930799105",
        "startDate": "2019-01-21T10:42:03.904+08:00",
        "endDate": "2019-03-21T10:42:03.904+08:00",
        "authorizationNotes": "测试数据01"
        }
     */
    @PostMapping
    public ResponseEntity insertWorkflowTransfer(@RequestBody WorkflowTransfer workflowTransfer){
        return ResponseEntity.ok(workflowTransferService.insertWorkflowTransfer(workflowTransfer));
    }

    /**
     * @api {PUT}  /api/workflow/transfer 【审批流转交-编辑转交】
     * @apiDescription 编辑转交
     * @apiGroup WorkflowTransferController
     * @apiParam {Long} id 审批流转交id
     * @apiParam {Long} documentCategory 单据大类
     * @apiParam {Long} workflowId 审批流
     * @apiParam {Long} agentId 代理人Id
     * @apiParam {ZonedDateTime} startDate 有效日期从
     * @apiParam {ZonedDateTime} endDate 有效日期至
     * @apiParam {String} authorizationNotes 备注
     * @apiParamExample {json} Request-Param:
     * {
        "id": "1087278958413668353",
        "documentCategory": "801001",
        "workflowId": "1086076030665027586",
        "agentId": "1087188765930799105",
        "startDate": "2019-01-21T10:42:03.904+08:00",
        "endDate":"2019-05-30T10:42:03.904+08:00",
        "authorizationNotes":"测试数据02"
        }
     * @apiSuccessExample {json} Success-Response:
     * {
        "id": "1087278958413668353",
        "createdDate": null,
        "createdBy": null,
        "lastUpdatedDate": "2019-01-21T17:25:16.306+08:00",
        "lastUpdatedBy": "1083751705402064897",
        "versionNumber": null,
        "tenantId": null,
        "setOfBooksId": null,
        "authorizerId": null,
        "documentCategory": "801001",
        "workflowId": "1086076030665027586",
        "agentId": "1087188765930799105",
        "startDate": "2019-01-21T10:42:03.904+08:00",
        "endDate": "2019-05-30T10:42:03.904+08:00",
        "authorizationNotes": "测试数据02"
        }
     */
    @PutMapping
    public ResponseEntity updateWorkflowTransfer(@RequestBody WorkflowTransfer workflowTransfer){
        return ResponseEntity.ok(workflowTransferService.updateWorkflowTransfer(workflowTransfer));
    }


}
