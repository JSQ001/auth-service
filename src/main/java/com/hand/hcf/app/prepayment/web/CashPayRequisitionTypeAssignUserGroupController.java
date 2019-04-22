package com.hand.hcf.app.prepayment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignUserGroup;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeAssignUserGroupService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 韩雪 on 2017/12/29.
 */
@RestController
@RequestMapping("/api/cash/pay/requisition/type/assign/userGroups")
public class CashPayRequisitionTypeAssignUserGroupController {
    private final CashPayRequisitionTypeAssignUserGroupService cashPayRequisitionTypeAssignUserGroupService;

    public CashPayRequisitionTypeAssignUserGroupController(CashPayRequisitionTypeAssignUserGroupService cashPayRequisitionTypeAssignUserGroupService){
        this.cashPayRequisitionTypeAssignUserGroupService = cashPayRequisitionTypeAssignUserGroupService;
    }

    /**
     * 批量新增 预付款单类型关联人员组
     *
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/cash/pay/requisition/type/assign/userGroups/batch 【类型关联员工组】 增人员组
     * @apiDescription 批量新增 预付款单类型关联人员组
     * @apiGroup PrepaymentService
     * @apiParam {Long} payRequisitionTypeId 预付款单类型ID
     * @apiParam {Long} userGroupId 人员组ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} payRequisitionTypeId 预付款单类型ID
     * @apiSuccess {Long} userGroupId 部门ID
     * @apiParamExample {json} 请求参数:
    [
    {
    "payRequisitionTypeId":"979262492650864642",
    "userGroupId":"442"
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1003842229847322626",
    "payRequisitionTypeId": "979262492650864642",
    "userGroupId": "442"
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "同一预付款单类型下的人员组不允许重复!",
    "errorCode": "10603"
    }
     */
    @PostMapping("/batch")
    public ResponseEntity<List<CashPayRequisitionTypeAssignUserGroup>> createCashPayRequisitionTypeAssignUserGroupBatch(@RequestBody List<CashPayRequisitionTypeAssignUserGroup> list){
        return ResponseEntity.ok(cashPayRequisitionTypeAssignUserGroupService.createCashPayRequisitionTypeAssignUserGroupBatch(list));
    }

    /**
     * 批量修改 预付款单类型关联人员组
     *
     * @param list
     * @return
     */
    /**
     * @api {PUT} /api/cash/pay/requisition/type/assign/userGroups/batch 【类型关联员工组】 改人员组
     * @apiDescription 批量修改 预付款单类型关联人员组
     * @apiGroup PrepaymentService
     * @apiParam {Long} id  主键id
     * @apiParam {Long} payRequisitionTypeId 预付款单类型ID
     * @apiParam {Long} userGroupId 人员组ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} payRequisitionTypeId 预付款单类型ID
     * @apiSuccess {Long} userGroupId 部门ID
     * @apiParamExample {json} 请求参数:
    [
    {
    "id": "1003842229847322626",
    "payRequisitionTypeId":"979262492650864643",
    "userGroupId":"442"
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1003842229847322626",
    "payRequisitionTypeId": "979262492650864643",
    "userGroupId": "442"
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "同一预付款单类型下的人员组不允许重复!",
    "errorCode": "10603"
    }
     */
    @PutMapping("/batch")
    public ResponseEntity<List<CashPayRequisitionTypeAssignUserGroup>> updateCashPayRequisitionTypeAssignUserGroupBatch(@RequestBody List<CashPayRequisitionTypeAssignUserGroup> list){
        return ResponseEntity.ok(cashPayRequisitionTypeAssignUserGroupService.updateCashPayRequisitionTypeAssignUserGroupBatch(list));
    }

    /**
     * 批量删除 预付款单类型关联人员组(物理删除)
     *
     * @param list
     * @return
     */
    /**
     * @api {DELETE} /api/cash/pay/requisition/type/assign/userGroups/batch 【类型关联员工组】 删人员组
     * @apiDescription 批量删除 预付款单类型关联人员组
     * @apiGroup PrepaymentService
     * @apiParam {List(Long)} id  主键id
     * @apiParamExample {json} 请求参数:
    [
     "1003842229847322626"
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "该预付款单类型关联人员组数据不存在!",
    "errorCode": "10602"
    }
     */
    @DeleteMapping("/batch")
    public ResponseEntity deleteCashPayRequisitionTypeAssignUserGroupBatch(@RequestBody List<Long> list){
        cashPayRequisitionTypeAssignUserGroupService.deleteCashPayRequisitionTypeAssignUserGroupBatch(list);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据预付款单类型id查询所有已关联的人员组(分页)
     *
     * @param payRequisitionTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/pay/requisition/type/assign/userGroups/query?payRequisitionTypeId=979262492650864642&page=0&size=10 【类型关联员工组】 查人员组
     * @apiDescription 根据预付款单类型id查询所有已关联的人员组，分页
     * @apiGroup PrepaymentService
     * @apiParam {Long} payRequisitionTypeId  预付款单类型ID
     * @apiParam {int} page
     * @apiParam {int} size
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} payRequisitionTypeId 预付款单类型ID
     * @apiSuccess {Long} userGroupId 部门ID
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "979289063295315970",
    "payRequisitionTypeId": "979262492650864642",
    "userGroupId": "441"
    },
    {
    "id": "1003842229847322626",
    "payRequisitionTypeId": "979262492650864642",
    "userGroupId": "442"
    }
    ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<CashPayRequisitionTypeAssignUserGroup>> getCashPayRequisitionTypeAssignUserGroupByCond(
            @RequestParam(value = "payRequisitionTypeId") Long payRequisitionTypeId,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionTypeAssignUserGroup> list = cashPayRequisitionTypeAssignUserGroupService.getCashPayRequisitionTypeAssignUserGroupByCond(payRequisitionTypeId,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/type/assign/userGroups/query");
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }
}
