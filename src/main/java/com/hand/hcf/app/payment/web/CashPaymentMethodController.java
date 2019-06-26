package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CashPaymentMethod;
import com.hand.hcf.app.payment.service.CashPaymentMethodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by 刘亮 on 2017/9/6.
 */
@Api(tags = "付款方式API")
@RestController
@RequestMapping("/api/cash/payment/method")
public class CashPaymentMethodController {

    private final CashPaymentMethodService cashPaymentMethodService;

    public CashPaymentMethodController(CashPaymentMethodService cashPaymentMethodService) {
        this.cashPaymentMethodService = cashPaymentMethodService;
    }

    /**
     * 新增或修改付款方式
     * @param cashPaymentMethod
     * @return
     */
    /**
     * @api {POST} /api/cash/payment/method 【付款方式】单个新增或修改
     * @apiDescription 新增或修改单个付款方式
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} [id] 主键ID
     * @apiParam (请求参数) {String} paymentMethodCode 付款方式代码
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {String} paymentMethodCategory 付款方式类型 ONLINE_PAYMENT 线上,OFFLINE_PAYMENT 线下,EBANK_PAYMENT 落地文件
     * @apiParam (请求参数) {String} description 付款方式名称
     * @apiParam (请求参数) {String} createType 创建类型
     * @apiParam (请求参数) {Integer} [versionNumber] 版本号
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {String} paymentMethodCode 付款方式代码
     * @apiSuccess (返回参数) {Long} tenantId 租户ID
     * @apiSuccess (返回参数) {String} paymentMethodCategory 付款方式类型 ONLINE_PAYMENT 线上,OFFLINE_PAYMENT 线下,EBANK_PAYMENT 落地文件
     * @apiSuccess (返回参数) {String} description 付款方式名称
     * @apiSuccess (返回参数) {String} createType 创建类型
     * @apiSuccess (返回参数) {String} paymentMethodCategoryName 付款方式类型名称
     * @apiParamExample {json} 请求参数
     * {
     *   "id": "",
     *   "isEnabled": "true",
     *   "paymentMethodCode": "zhifu13111",
     *   "paymentMethodCategory": "ONLINE_PAYMENT",
     *   "description": "线上支付",
     *   "versionNumber": "1"
     * }
     * @apiSuccessExample {json} 成功返回值
     *{
     *   "id": "904992174175711233",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-05T16:58:42.922+08:00",
     *   "createdBy": 100,
     *   "lastUpdatedDate": "2017-09-05T16:58:42.922+08:00",
     *   "lastUpdatedBy": 100,
     *   "bankCode": "1001",
     *   "bankName": "某某银行",
     *   "bankShortName": "某银行",
     *   "bankType": "现金银行",
     *   "versionNumber": 1
     * }
     */

    @ApiOperation(value = "新增或修改单个付款方式", notes = "新增或修改单个付款方式 开发:")
    @PostMapping()
    public ResponseEntity<CashPaymentMethod> insertOrUpdateCashPaymentMethod(@ApiParam(value = "付款方式") @RequestBody CashPaymentMethod cashPaymentMethod){
        return ResponseEntity.ok(cashPaymentMethodService.insertOrUpdateCashPaymentMethod(cashPaymentMethod));
    }


    /**
     * 单个删除付款方式
     * @param id
     * @return
     */
    /**
     * @apiDefine myID
     * @apiParam (请求参数) {Long} id 付款方式待删除的ID
     */
    /**
     * @apiDefine MyError
     * @apiError UserNotFound The <code>id</code> of the User was not found.
     */
    /**
     * @api {DELETE} /api/cash/payment/method/deleteById/{id} 【付款方式】单个删除
     * @apiDescription 根据id删除单个付款方式
     * @apiGroup PaymentService
     * @apiUse myID
     * @apiUse MyError
     */

    @ApiOperation(value = "单个删除付款方式", notes = "单个删除付款方式 开发:")
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Boolean> deleteCashPaymentMethodById(@PathVariable Long id){
        boolean flag = cashPaymentMethodService.deleteById(id);
        return ResponseEntity.ok(flag);
    }

    /**
     * 条件查询
     * @param paymentMethodCode
     * @param description
     * @param current
     * @param size
     * @return
     * @throws URISyntaxException
     */

    @ApiOperation(value = "条件查询", notes = "条件查询 开发:")
    @GetMapping("/query")
    public ResponseEntity<List<CashPaymentMethod>> selectByInput(
            @ApiParam(value = "付款方式code") @RequestParam(value = "paymentMethodCode", required = false ) String paymentMethodCode,
            @ApiParam(value = "付款方式名称") @RequestParam(value = "description", required = false ) String description,
            @ApiParam(value = "目前") @RequestParam(defaultValue = PageUtil.DEFAULT_PAGE) int current,
            @ApiParam(value = "尺寸") @RequestParam(defaultValue = PageUtil.DEFAULT_SIZE) int size
            ) throws URISyntaxException {
        Page page = new Page(current,size);
        List<CashPaymentMethod> list =  cashPaymentMethodService.selectByInput(paymentMethodCode, description, page);

        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, "/api/Cash/PaymentMethod/query");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * 条件查询
     * @param paymentMethodCode
     * @param description
     * @param page
     * @param size
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/payment/method/query/lov 【付款方式】分页查询
     * @apiDescription 根据条件分页查询付款方式
     * @apiGroup PaymentService
     * @apiParam (请求参数){String} [paymentMethodCode] 付款方式代码
     * @apiParam (请求参数){String} [description] 付款方式名称
     * @apiParam (请求参数) {Integer} page 页码
     * @apiParam (请求参数) {Integer} size 每页条数
     * @apiParamExample {json} 请求参数
     * api/Cash/PaymentMethod/query/lov?description=支付&paymentMethodCode=zhifu
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "919820290140672002",
     *   "isEnabled": true,
     *   "isDeleted": true,
     *   "createdDate": "2017-10-16T15:00:21+08:00",
     *   "createdBy": 1,
     *   "lastUpdatedDate": "2017-10-16T15:00:21+08:00",
     *   "lastUpdatedBy": 1,
     *   "versionNumber": 1,
     *   "paymentMethodCode": "zhifu13111_DELETE_304306",
     *   "tenantId": 1,
     *   "paymentMethodCategory": "ONLINE_PAYMENT",
     *   "description": "线上支付"
     *  }
     * ]
     */

    @ApiOperation(value = "根据条件分页查询付款方式", notes = "根据条件分页查询付款方式 开发:")
    @GetMapping("/query/lov")
    public ResponseEntity<List<CashPaymentMethod>> selectByInputLOV(
            @ApiParam(value = "付款方式code") @RequestParam(value = "paymentMethodCode", required = false ) String paymentMethodCode,
            @ApiParam(value = "付款方式名称") @RequestParam(value = "description", required = false ) String description,
            @ApiParam(value = "页面") @RequestParam(defaultValue = PageUtil.DEFAULT_PAGE) int page,
            @ApiParam(value = "尺寸") @RequestParam(defaultValue = PageUtil.DEFAULT_SIZE) int size
    ) throws URISyntaxException {
        Page pages = new Page(page + 1,size);
        List<CashPaymentMethod> list =  cashPaymentMethodService.selectByInputLOV(paymentMethodCode, description, pages);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(pages, "/api/Cash/PaymentMethod/query/lov");
        return new ResponseEntity(list, httpHeaders, HttpStatus.OK);
    }

    /**
     * 条件查询
     * @param paymentMethodCode
     * @param description
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/payment/method/query/all 【付款方式】不分页查询
     * @apiDescription 根据条件不分页查询付款方式
     * @apiGroup PaymentService
     * @apiParam (请求参数){String} [paymentMethodCode] 付款方式代码
     * @apiParam (请求参数){String} [description] 付款方式名称
     * @apiParamExample {json} 请求参数
     * api/Cash/PaymentMethod/query/all?description=支付&paymentMethodCode=zhifu
     * @apiSuccessExample {json} 成功返回值
     * [
     * {
     *   "id": "919820290140672002",
     *   "isEnabled": true,
     *   "isDeleted": true,
     *   "createdDate": "2017-10-16T15:00:21+08:00",
     *   "createdBy": 1,
     *   "lastUpdatedDate": "2017-10-16T15:00:21+08:00",
     *   "lastUpdatedBy": 1,
     *   "versionNumber": 1,
     *   "paymentMethodCode": "zhifu13111_DELETE_304306",
     *   "tenantId": 1,
     *   "paymentMethodCategory": "ONLINE_PAYMENT",
     *   "description": "线上支付"
     *  }
     * ]
     */

    @ApiOperation(value = "根据条件不分页查询付款方式", notes = "根据条件不分页查询付款方式 开发:")
    @GetMapping("/query/all")
    public ResponseEntity<List<CashPaymentMethod>> selectAll(
            @ApiParam(value = "付款方式code") @RequestParam(value = "paymentMethodCode", required = false ) String paymentMethodCode,
            @ApiParam(value = "付款方式名称") @RequestParam(value = "description", required = false ) String description){

        List<CashPaymentMethod> list =  cashPaymentMethodService.selectAll(paymentMethodCode, description);
        return ResponseEntity.ok(list);
    }
    /**
     * 批量删除
     * @param list
     * @return
     */
    /**
     * @api {DELETE} /api/cash/payment/method/delete/batch 【付款方式】批量删除
     * @apiDescription 批量删除付款方式
     * @apiGroup PaymentService
     */

    @ApiOperation(value = "批量删除付款方式", notes = "批量删除付款方式 开发:")
    @DeleteMapping("/delete/batch")
    public ResponseEntity deleteCashPaymentMethodBatch(@ApiParam(value = "付款方式列表") @RequestBody List<CashPaymentMethod> list){
        cashPaymentMethodService.deleteCashPaymentMethodBatch(list);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量新增或修改
     * @param list
     * @return
     */
    /**
     * @api {POST} /api/cash/payment/method/insertOrUpdate/batch 【付款方式】批量新增或修改
     * @apiDescription 批量新增或修改付款方式
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * [
     * {
     *   "id": "",
     *   "isEnabled": "true",
     *   "paymentMethodCode": "zhifu13111",
     *   "paymentMethodCategory": "ONLINE_PAYMENT",
     *   "description": "线上支付",
     *   "versionNumber": "1"
     * }
     * ]
     * @apiSuccessExample {json} 成功返回值
     * [
     *  {
     *   "id": "904992174175711233",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-09-05T16:58:42.922+08:00",
     *   "createdBy": 100,
     *   "lastUpdatedDate": "2017-09-05T16:58:42.922+08:00",
     *   "lastUpdatedBy": 100,
     *   "bankCode": "1001",
     *   "bankName": "某某银行",
     *   "bankShortName": "某银行",
     *   "bankType": "现金银行",
     *   "versionNumber": 1
     *  }
     * ]
     */

    @ApiOperation(value = "批量新增或修改付款方式", notes = "批量新增或修改付款方式 开发:")
    @PostMapping("/insertOrUpdate/batch")
    public ResponseEntity<List<CashPaymentMethod>> insertOrUpdateCashPaymentMethodBatch(@ApiParam(value = "付款方式列表") @RequestBody List<CashPaymentMethod> list){
        List<CashPaymentMethod> list1 = cashPaymentMethodService.insertOrUpdateCashPaymentMethodBatch(list);
        return ResponseEntity.ok(list1);
    }


    /**
     * 根据当前付款方式类型查看付款方式----通用支付平台用
     * @param paymentType
     * @return
     */
    /**
     * @api {GET} /api/cash/payment/method/selectByPaymentType 【付款方式】根据当前付款方式类型查看付款方式
     * @apiDescription 根据当前付款方式类型查看付款方式----通用支付平台用
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} paymentType 付款方式类型
     */

    @ApiOperation(value = "根据当前付款方式类型查看付款方式--通用支付平台", notes = "根据当前付款方式类型查看付款方式--通用支付平台 开发:")
    @GetMapping("/selectByPaymentType")
    public ResponseEntity<List<CashPaymentMethod>> selectByPaymentType(@ApiParam(value = "付款方式") @RequestParam String paymentType){
        return ResponseEntity.ok(cashPaymentMethodService.selectByPaymentType(paymentType));
    }



    /*查看当前租户下的所有付款方式（不分页）---公司银行账户详情下页签使用
    * */

    /**
     * @return
     */
    /**
     * @api {GET} /api/cash/payment/method/selectByTenantId 【付款方式】不分页查询当前租户下所有付款方式
     * @apiDescription 不分页查询当前租户下所有付款方式(公司银行账户详情下页签使用)
     * @apiGroup PaymentService
     */

    @ApiOperation(value = "不分页查询当前租户下所有付款方式", notes = "不分页查询当前租户下所有付款方式 开发:")
    @GetMapping("/selectByTenantId")
    public ResponseEntity<List<CashPaymentMethod>> selectByTenantId(){
        return ResponseEntity.ok(cashPaymentMethodService.selectPaymentMethodByTenantId(OrgInformationUtil.getCurrentTenantId()));
    }


    /**
     * 根据付款方式id查看付款方式详情
     */
    /**
     * @api {GET} /api/cash/payment/method/selectById/{id} 【付款方式】单个查询
     * @apiDescription 根据付款方式id查看付款方式详情
     * @apiGroup PaymentService
     * @apiParamExample {json} 请求参数
     * /api/Cash/PaymentMethod/selectById/918365270157312002
     * @apiSuccessExample {json} 成功返回值
     * {
     *   "id": "918365270157312002",
     *   "isEnabled": true,
     *   "isDeleted": false,
     *   "createdDate": "2017-10-12T14:38:37+08:00",
     *   "createdBy": 1,
     *   "lastUpdatedDate": "2017-10-12T14:38:37+08:00",
     *   "lastUpdatedBy": 1,
     *   "versionNumber": 1,
     *   "paymentMethodCode": "zhifu1336",
     *   "tenantId": 1,
     *   "paymentMethodCategory": "ONLINE_PAYMENT",
     *   "description": "线上支付"
     * }
     */

    @ApiOperation(value = "根据付款方式id查看付款方式详情", notes = "根据付款方式id查看付款方式详情 开发:")
    @GetMapping("/selectById/{id}")
    public ResponseEntity<CashPaymentMethod> selectById(@PathVariable Long id,@ApiParam(value = "付款方式") @RequestParam(value = "paymentMethod",required = false) String paymentMethod){

        return  ResponseEntity.ok(cashPaymentMethodService.selectPaymentMethodById(id,paymentMethod));
    }


    /*
    * 新增时，过滤已选的小类付款方式
    * */

    /**
     * @api {GET} /api/cash/payment/method/get/payment/by/bankId/and/code 【付款方式】新增时过滤已选的小类付款方式
     * @apiDescription 新增时过滤已选的小类付款方式
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Long} companyBankId 公司银行账户ID
     * @apiParam (请求参数) {String} type 付款方式类型 ONLINE_PAYMENT 线上,OFFLINE_PAYMENT 线下,EBANK_PAYMENT 落地文件
     * @apiParam (请求参数) {Long} [companyPaymentId] 付款公司ID
     * @apiParam (请求参数) {Long} [paymentMethodId] 付款方式ID
     */

    @ApiOperation(value = "新增时过滤已选的小类付款方式", notes = "新增时过滤已选的小类付款方式 开发:")
    @GetMapping("/get/payment/by/bankId/and/code")
    public ResponseEntity<List<CashPaymentMethod>> selectByInput(
            @ApiParam(value = "公司银行账户ID") @RequestParam Long companyBankId,
            @ApiParam(value = "付款方式类型") @RequestParam String type,
            @ApiParam(value = "付款公司ID") @RequestParam(value = "companyPaymentId",required = false) Long companyPaymentId,
            @ApiParam(value = "付款方式ID") @RequestParam(value = "paymentMethodId",required = false) Long paymentMethodId
            ){
        return ResponseEntity.ok(cashPaymentMethodService.selectByTypeAndCompanyBankId(type,companyBankId,companyPaymentId,paymentMethodId));
    }

}
