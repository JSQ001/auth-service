package com.hand.hcf.app.prepayment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignDepartment;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeAssignDepartmentService;
import com.hand.hcf.app.core.util.PaginationUtil;
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
 * Created by 韩雪 on 2017/12/29.
 */
@Api(tags = "预付款单类型关联部门")
@RestController
@RequestMapping("/api/cash/pay/requisition/type/assign/departments")
public class CashPayRequisitionTypeAssignDepartmentController {
    private final CashPayRequisitionTypeAssignDepartmentService cashPayRequisitionTypeAssignDepartmentService;

    public CashPayRequisitionTypeAssignDepartmentController(CashPayRequisitionTypeAssignDepartmentService cashPayRequisitionTypeAssignDepartmentService){
        this.cashPayRequisitionTypeAssignDepartmentService = cashPayRequisitionTypeAssignDepartmentService;
    }

    /**
     * 批量新增 预付款单类型关联部门
     *
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/cash/pay/requisition/type/assign/departments/batch 【类型关联部门】 增部门
     * @apiDescription 批量新增部门
     * @apiGroup PrepaymentService
     * @apiParam {Long} payRequisitionTypeId 预付款单类型ID
     * @apiParam {Long} departmentId 部门ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} payRequisitionTypeId 预付款单类型ID
     * @apiSuccess {Long} departmentId 部门ID
     * @apiParamExample {json} 请求参数:
     [
    {
    "payRequisitionTypeId":"969401155975135237",
    "departmentId":"625575"
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1003544194085978114",
    "payRequisitionTypeId": "969401155975135237",
    "departmentId": "625575"
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "同一预付款单类型下的部门不允许重复!",
    "errorCode": "10503"
    }
     */
    @PostMapping("/batch")
    @ApiOperation(value = "批量新增 预付款单类型关联部门", notes = "批量新增 预付款单类型关联部门 开发:韩雪")
    public ResponseEntity<List<CashPayRequisitionTypeAssignDepartment>> createCashPayRequisitionTypeAssignDepartmentBatch(@ApiParam(value = "预付款单类型关联部门") @RequestBody List<CashPayRequisitionTypeAssignDepartment> list){
        return ResponseEntity.ok(cashPayRequisitionTypeAssignDepartmentService.createCashPayRequisitionTypeAssignDepartmentBatch(list));
    }

    /**
     * 批量修改 预付款单类型关联部门
     *
     * @param list
     * @return
     */
    /**
     * @api {PUT} /api/cash/pay/requisition/type/assign/departments/batch 【类型关联部门】 改部门
     * @apiDescription 批量修改部门
     * @apiGroup PrepaymentService
     * @apiParam {Long} id  主键id
     * @apiParam {Long} payRequisitionTypeId 预付款单类型ID
     * @apiParam {Long} departmentId 部门ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} payRequisitionTypeId 预付款单类型ID
     * @apiSuccess {Long} departmentId 部门ID
     * @apiParamExample {json} 请求参数:
    [
    {
    "id": "987235829514702849",
    "payRequisitionTypeId":"969401155975135234",
    "departmentId":"625576"
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "987235829514702849",
    "payRequisitionTypeId": "969401155975135234",
    "departmentId": "625576"
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "同一预付款单类型下的部门不允许重复!",
    "errorCode": "10503"
    }
     */
    @PutMapping("/batch")
    @ApiOperation(value = "批量修改 预付款单类型关联部门", notes = "批量修改 预付款单类型关联部门 开发:韩雪")
    public ResponseEntity<List<CashPayRequisitionTypeAssignDepartment>> updateCashPayRequisitionTypeAssignDepartmentBatch(@ApiParam(value = "预付款单类型关联部门") @RequestBody List<CashPayRequisitionTypeAssignDepartment> list){
        return ResponseEntity.ok(cashPayRequisitionTypeAssignDepartmentService.updateCashPayRequisitionTypeAssignDepartmentBatch(list));
    }


    /**
     * 批量删除 预付款单类型关联部门(物理删除)
     *
     * @param list
     * @return
     */
    /**
     * @api {DELETE} /api/cash/pay/requisition/type/assign/departments/batch 【类型关联部门】 删部门
     * @apiDescription 批量删除部门
     * @apiGroup PrepaymentService
     * @apiParam {Long} id  主键id
     * @apiParamExample {json} 请求参数:
    [
    "987235829514702849"
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "该预付款单类型关联部门数据不存在!",
    "errorCode": "10502"
    }
     */
    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除 预付款单类型关联部门(物理删除)", notes = "批量删除 预付款单类型关联部门(物理删除) 开发:韩雪")
    public ResponseEntity deleteCashPayRequisitionTypeAssignDepartmentBatch(@ApiParam(value = "主键id") @RequestBody List<Long> list){
        cashPayRequisitionTypeAssignDepartmentService.deleteCashPayRequisitionTypeAssignDepartmentBatch(list);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据预付款单类型id查询所有已关联的部门(分页)
     *
     * @param payRequisitionTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/pay/requisition/type/assign/departments/query?payRequisitionTypeId=987211794839965698&page=0&size=10 【类型关联部门】 查部门
     * @apiDescription 根据预付款单类型id查询所有已关联的部门，分页
     * @apiGroup PrepaymentService
     * @apiParam {Long} payRequisitionTypeId  预付款单类型ID
     * @apiParam {int} [page] page
     * @apiParam {int} [size] size
     * @apiSuccess {Long} id 主键ID
     * @apiSuccess {Long} payRequisitionTypeId 预付款单类型ID
     * @apiSuccess {Long} departmentId 部门ID
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "987218806063697921",
    "payRequisitionTypeId": "987211794839965698",
    "departmentId": "625575"
    },
    {
    "id": "987218806097252354",
    "payRequisitionTypeId": "987211794839965698",
    "departmentId": "625675"
    }
    ]
     */
    @GetMapping("/query")
    @ApiOperation(value = "根据预付款单类型id查询所有已关联的部门(分页)", notes = "根据预付款单类型id查询所有已关联的部门(分页) 开发:韩雪")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    public ResponseEntity<List<CashPayRequisitionTypeAssignDepartment>> getCashPayRequisitionTypeAssignDepartmentByCond(
            @ApiParam(value = "预付款单类型ID") @RequestParam(value = "payRequisitionTypeId") Long payRequisitionTypeId,
            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionTypeAssignDepartment> list = cashPayRequisitionTypeAssignDepartmentService.getCashPayRequisitionTypeAssignDepartmentByCond(payRequisitionTypeId,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/type/assign/departments/query");
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }
}
