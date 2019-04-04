package com.hand.hcf.app.prepayment.web;

import com.baomidou.mybatisplus.plugins.Page;
//import com.hand.hcf.app.application.dto.CustomFormForOtherRequestDTO;
import com.hand.hcf.app.common.co.ApplicationTypeCO;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionTypeAssignRequisitionType;
import com.hand.hcf.app.prepayment.service.CashPayRequisitionTypeAssignRequisitionTypeService;
import com.hand.hcf.app.prepayment.web.dto.CustomFormForOtherDTO;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.PaginationUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 韩雪 on 2017/12/5.
 */
@RestController
@RequestMapping("/api/cash/pay/requisition/type/assign/requisition/types")
public class CashPayRequisitionTypeAssignRequisitionTypeController {
    private final CashPayRequisitionTypeAssignRequisitionTypeService cashPayRequisitionTypeAssignRequisitionTypeService;

    public CashPayRequisitionTypeAssignRequisitionTypeController(CashPayRequisitionTypeAssignRequisitionTypeService cashPayRequisitionTypeAssignRequisitionTypeService){
        this.cashPayRequisitionTypeAssignRequisitionTypeService = cashPayRequisitionTypeAssignRequisitionTypeService;
    }

    /**
     * 批量新增 预付款单类型关联申请单类型
     *
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/cash/pay/requisition/type/assign/requisition/types/batch 【类型关联申请单】 增申请单
     * @apiDescription 批量新增 预付款单类型关联申请单类型
     * @apiGroup PrepaymentService
     * @apiParam {Long} payRequisitionTypeId 预付款单类型ID
     * @apiParam {Long} requisitionTypeId 申请单类型ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} payRequisitionTypeId 预付款单类型ID
     * @apiSuccess {Long} requisitionTypeId 申请单类型ID
     * @apiParamExample {json} 请求参数:
    [
    {
    "payRequisitionTypeId":"971634663418302466",
    "requisitionTypeId":"9999"
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1003569428637843458",
    "payRequisitionTypeId": "971634663418302466",
    "requisitionTypeId": "9999"
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "同一预付款单类型下的申请单类型不允许重复!",
    "errorCode": "10403"
    }
     */
    @PostMapping("/batch")
    public ResponseEntity<List<CashPayRequisitionTypeAssignRequisitionType>> createCashPayRequisitionTypeAssignRequisitionTypeBatch(@RequestBody List<CashPayRequisitionTypeAssignRequisitionType> list){
        return ResponseEntity.ok(cashPayRequisitionTypeAssignRequisitionTypeService.createCashPayRequisitionTypeAssignRequisitionTypeBatch(list));
    }

    /**
     * 批量修改 预付款单类型关联申请单类型
     *
     * @param list
     * @return
     */
    /**
     * @api {PUT} /api/cash/pay/requisition/type/assign/requisition/types/batch 【类型关联申请单】 改申请单
     * @apiDescription 批量修改 预付款单类型关联申请单类型
     * @apiGroup PrepaymentService
     * @apiParam {Long} id  主键id
     * @apiParam {Long} payRequisitionTypeId 预付款单类型ID
     * @apiParam {Long} requisitionTypeId 申请单类型ID
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {Long} payRequisitionTypeId 预付款单类型ID
     * @apiSuccess {Long} requisitionTypeId 申请单类型ID
     * @apiParamExample {json} 请求参数:
    [
    {
    "id":"1003569428637843458",
    "payRequisitionTypeId":"971634663418302466",
    "requisitionTypeId":"9998"
    }
    ]
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "1003569428637843458",
    "payRequisitionTypeId": "971634663418302466",
    "requisitionTypeId": "9998"
    }
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "同一预付款单类型下的申请单类型不允许重复!",
    "errorCode": "10403"
    }
     */
    @PutMapping("/batch")
    public ResponseEntity<List<CashPayRequisitionTypeAssignRequisitionType>> updateCashPayRequisitionTypeAssignRequisitionTypeBatch(@RequestBody List<CashPayRequisitionTypeAssignRequisitionType> list){
        return ResponseEntity.ok(cashPayRequisitionTypeAssignRequisitionTypeService.updateCashPayRequisitionTypeAssignRequisitionTypeBatch(list));
    }

    /**
     * 批量删除 预付款单类型关联申请单类型(物理删除)
     *
     * @param list
     * @return
     */
    /**
     * @api {DELETE} /api/cash/pay/requisition/type/assign/requisition/types/batch 【类型关联申请单】 删申请单
     * @apiDescription 批量删除 预付款单类型关联申请单类型(物理删除)
     * @apiGroup PrepaymentService
     * @apiParam {Long} id  主键id
     * @apiParamExample {json} 请求参数:
    [
    "1003569428637843458"
    ]
     * @apiErrorExample {json} 错误返回值:
    {
    "message": "该预付款单类型关联申请单类型数据不存在!",
    "errorCode": "10402"
    }
     */
    @DeleteMapping("/batch")
    public ResponseEntity deleteCashPayRequisitionTypeAssignRequisitionTypeBatch(@RequestBody List<Long> list){
        cashPayRequisitionTypeAssignRequisitionTypeService.deleteCashPayRequisitionTypeAssignRequisitionTypeBatch(list);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据预付款单类型id查询所有已关联的申请单类型(分页)
     *
     * @param payRequisitionTypeId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/pay/requisition/type/assign/requisition/types/query?payRequisitionTypeId=971634663418302465&page=0&size=10 【类型关联申请单】 查申请单
     * @apiDescription 根据预付款单类型id查询所有已关联的申请单类型，分页
     * @apiGroup PrepaymentService
     * @apiParam {Long} payRequisitionTypeId  预付款单类型ID
     * @apiParam {int} [page] page
     * @apiParam {int} [size] size
     * @apiSuccess {Long} id 主键ID
     * @apiSuccess {Long} payRequisitionTypeId 预付款单类型ID
     * @apiSuccess {Long} requisitionTypeId 申请单类型ID
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "996672162443177985",
    "payRequisitionTypeId": "971634663418302465",
    "requisitionTypeId": "5783"
    },
    {
    "id": "996672162522869761",
    "payRequisitionTypeId": "971634663418302465",
    "requisitionTypeId": "5791"
    }
    ]
     */
    @GetMapping("/query")
    public ResponseEntity<List<CashPayRequisitionTypeAssignRequisitionType>> getCashPayRequisitionTypeAssignRequisitionTypeByCond(
            @RequestParam(value = "payRequisitionTypeId") Long payRequisitionTypeId,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPayRequisitionTypeAssignRequisitionType> list = cashPayRequisitionTypeAssignRequisitionTypeService.getCashPayRequisitionTypeAssignRequisitionTypeByCond(payRequisitionTypeId,page);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,"/api/cash/pay/requisition/type/assign/requisition/types/query");
        return new ResponseEntity(list,headers, HttpStatus.OK);
    }


    /**
     * 根据所选范围 查询申请单类型(分页)
     * @param setOfBooksId 账套ID
     * @param range 所选范围 (全部：all；已选：)
     * @param payRequisitionTypeId
     * @param code 申请单类型代码
     * @param name 申请单类型名称
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {POST} /api/cash/pay/requisition/type/assign/requisition/types/getCustomFormByRange?page=0&size=3【类型关联申请单】 筛申请单
     * @apiDescription 根据所选范围 查询申请单类型，分页
     * @apiGroup PrepaymentService
     * @apiParam {Long} setOfBookId  账套ID
     * @apiParam {Long} companyId 公司ID
     * @apiParam {String} companyName 公司名字
     * @apiParam {String} range 范围 全部：all、已选：selected、未选：notChoose
     * @apiParam {formCode} formCode 申请单类型代码
     * @apiParam {formName}  formName 申请单类型名称
     * @apiParam {List(Long)} idList 申请单类型ID
     * @apiParam {int} [page] page
     * @apiParam {int} [size] size
     * @apiSuccess {Long} id  主键id
     * @apiSuccess {String} formCode 申请单类型代码
     * @apiSuccess {String} formName 申请单类型名称
     * @apiSuccess {Boolean} assigned 是否被分配
     * @apiSuccess {String} companyName 公司name
     * @apiSuccessExample {json} 成功返回值:
    [
    {
    "id": "5783",
    "formCode": null,
    "formName": "费用申请单",
    "assigned": true
    },
    {
    "id": "6405",
    "formCode": null,
    "formName": "费用申请单",
    "assigned": false
    },
    {
    "id": "6420",
    "formCode": null,
    "formName": "费用申请单",
    "assigned": false
    }
    ]
     */
    @GetMapping("/get/application/type/by/range")
    public ResponseEntity<List<ApplicationTypeCO>> getCustomFormByRange(
            @RequestParam(value = "setOfBooksId") Long setOfBooksId,
            @RequestParam(value = "range") String range,
            @RequestParam(value = "payRequisitionTypeId",required = false) Long payRequisitionTypeId,
            @RequestParam(value = "code",required = false) String code,
            @RequestParam(value = "name",required = false) String name,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<ApplicationTypeCO> list = cashPayRequisitionTypeAssignRequisitionTypeService.getCustomFormByRange(setOfBooksId,range,payRequisitionTypeId,code,name,page);
        HttpHeaders headers = PageUtil.getTotalHeader(page);
        return new ResponseEntity(list.getRecords(),headers, HttpStatus.OK);
    }
}
