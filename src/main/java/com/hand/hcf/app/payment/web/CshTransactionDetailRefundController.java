package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import com.hand.hcf.app.payment.service.CashTransactionDetailService;
import com.hand.hcf.app.payment.web.dto.CashTransactionDetailRefundDTO;
import com.hand.hcf.app.payment.web.dto.PartnerSelectDTO;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author: bin.xie
 * @Description: 付款退款controller
 * @Date: Created in 15:40 2018/4/3
 * @Modified by
 */
@Api(tags="付款退款API")
@RestController
@AllArgsConstructor
@RequestMapping("/api/cash/refund")
public class CshTransactionDetailRefundController {

    private CashTransactionDetailService service;
    /**
     * @api {GET} {{payment-service_url}}/api/cash/refund/query 【退票】查询支付明细
     * @apiGroup PaymentService
     * @apiDescription 查询支付明细
     *
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {Date} payDateFrom 支付时间从
     * @apiParam (请求参数) {Date} payDateTo 支付时间至
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {String} documentNumber 单据号
     * @apiParam (请求参数) {Long} employeeId 员工
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/document/approve?documentType=PUBLIC_REPORT&documentHeaderId=1&tenantId=1&operatorId=1&operationType=1
     */
    @ApiOperation(value = "【退票】查询支付明细", notes = "【退票】查询支付明细 开发：bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query")
    public ResponseEntity<List<CashTransactionDetail>> queryByCondition(@ApiIgnore Pageable pageable,
                                                                        String billcode,
                                                                        String payDateFrom,
                                                                        String payDateTo,
                                                                        BigDecimal amountFrom,
                                                                        BigDecimal amountTo,
                                                                        String documentNumber,
                                                                        Long employeeId,
                                                                        String documentCategory,
                                                                        String partnerCategory,
                                                                        Long partnerId)throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);

        List<CashTransactionDetail> list = service.getRefundDetailsByCondition(page,
                billcode,
                payDateFrom,
                payDateTo,
                amountFrom,
                amountTo,
                documentNumber,
                employeeId,
                documentCategory,
                partnerCategory,
                partnerId);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/cash/refund/query");
        return new ResponseEntity(list,httpHeaders, HttpStatus.OK);
    }


    /**
     * @api {POST} {{payment-service_url}}/api/cash/refund/save 【核销】保存退票数据
     * @apiGroup PaymentService
     * @apiDescription 保存退票数据
     *
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {Date} payDateFrom 支付时间从
     * @apiParam (请求参数) {Date} payDateTo 支付时间至
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {String} documentNumber 单据号
     * @apiParam (请求参数) {Long} employeeId 员工
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     *
     * @apiParamExample {json}请求样例：
     *      {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     */
    @ApiOperation(value = "【核销】保存退票数据", notes = "【核销】保存退票数据 开发：bin.xie")
    @PostMapping("/save")
    public ResponseEntity<CashTransactionDetail> saveRefundData(@ApiParam(value = "支付详情") @RequestBody CashTransactionDetail dto){
        return ResponseEntity.ok(service.saveRefundData(dto));
    }
    /**
     * @api {PUT} {{payment-service_url}}/api/cash/refund/save 【退票】修改退票数据
     * @apiGroup PaymentService
     * @apiDescription 修改退票数据
     *
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {Date} payDateFrom 支付时间从
     * @apiParam (请求参数) {Date} payDateTo 支付时间至
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {String} documentNumber 单据号
     * @apiParam (请求参数) {Long} employeeId 员工
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     *
     * @apiParamExample {json}请求样例：
     *      {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     */
    @ApiOperation(value = "【退票】修改退票数据", notes = "【退票】修改退票数据 开发：bin.xie")
    @PutMapping("/save")
    public ResponseEntity<CashTransactionDetail> updateData(@ApiParam(value = "支付详情") @RequestBody CashTransactionDetail dto){
        return ResponseEntity.ok(service.updateData(dto));
    }

    /**
     * @api {PUT} {{payment-service_url}}/api/cash/refund/query/{id} 【核销】通过退票id查询退票数据
     * @apiGroup PaymentService
     * @apiDescription 通过退票id查询退票数据
     *
     * @apiParam (请求参数) {Long} id 退票id
     *
     * @apiSuccessExample {json} 成功返回样例:
     *      {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     */
    @ApiOperation(value = "【核销】通过退票id查询退票数据", notes = "【核销】通过退票id查询退票数据 开发：bin.xie")
    @GetMapping("/query/{id}")
    public ResponseEntity<CashTransactionDetail> queryById(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(service.selectRefundById(id));
    }


    /**
     * @api {GET} {{payment-service_url}}/api/cash/refund/query/myRefund 【退票】查询当前用户的退票数据
     * @apiGroup PaymentService
     * @apiDescription 查询当前用户的退票数据
     *
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {Date} returnDateFrom 退票日期从
     * @apiParam (请求参数) {Date} returnDateTo 退票日期至
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {String} refBillCode 原支付流水
     * @apiParam (请求参数) {String} draweeAccountNumber 收款方账号
     * @apiParam (请求参数) {String} payeeAccountNumber 退款放账号
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {String} backFlashStatus 回显状态
     *
     * @apiSuccessExample {json} 成功返回样例:
     *      [{
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     *                 ]
     */
    @ApiOperation(value = "【退票】查询当前用户的退票数据", notes = "【退票】查询当前用户的退票数据 开发：bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })    @GetMapping("/query/myRefund")
    public ResponseEntity<List<CashTransactionDetail>> queryMyRefundByCondition(@ApiIgnore Pageable pageable,
                                                                                String  billcode,//退款流水号
                                                                                String    returnDateFrom,//退款日期从
                                                                                String    returnDateTo, //退款日期至
                                                                                BigDecimal  amountFrom,//金额从
                                                                                BigDecimal  amountTo, //金额至
                                                                                String  refBillCode,//原支付流水
                                                                                String  draweeAccountNumber,//收款方账号
                                                                                String  payeeAccountNumber,//退款方账号
                                                                                String  partnerCategory,
                                                                                String  backFlashStatus,
                                                                                Long partnerId)throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionDetail> list = service.getMyRefundByCondition(page,
                billcode,
                returnDateFrom,
                returnDateTo,
                amountFrom,
                amountTo,
                refBillCode,
                draweeAccountNumber,
                payeeAccountNumber,
                partnerCategory,
                partnerId,
                backFlashStatus);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/cash/refund/query/myRefund");
        return new ResponseEntity(list,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} {{payment-service_url}}/api/cash/refund/query/myRefund/{id} 【退票】查询当前用户的退票数据(指定id)
     * @apiGroup PaymentService
     * @apiDescription 查询当前用户的退票数据(指定id)
     *
     * @apiParam (请求参数) {Long} id 当前用户退票数据的id
     *
     * @apiSuccessExample {json} 成功返回样例:
     *      [{
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     *                 ]
     */
    @ApiOperation(value = "【退票】查询当前用户的退票数据(指定id)", notes = "【退票】查询当前用户的退票数据(指定id) 开发:bin.xie")
    @GetMapping("/query/myRefund/{id}")
    public ResponseEntity<CashTransactionDetailRefundDTO> queryMyRefundById(@PathVariable(value = "id") Long id){
        return ResponseEntity.ok(service.queryMyRefundById(id));
    }

    /**
     * @api {delete} {{payment-service_url}}/api/cash/refund/delete/{id}【退票】删除退票数据(指定id)
     * @apiGroup PaymentService
     * @apiDescription 删除退票数据(指定id)
     *
     * @apiParam (请求参数) {Long} id 退票数据的id
     *
     */

    @ApiOperation(value = "【退票】删除退票数据(指定id)", notes = "【退票】删除退票数据(指定id) 开发:bin.xie")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteRefundById(@PathVariable(value = "id") Long id){
        service.deleteRefundById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} {{payment-service_url}}/api/cash/refund//operate【退票】提交退票
     * @apiGroup PaymentService
     * @apiDescription 提交退票
     *
     * @apiParam (请求参数) {CashTransactionDetail} dto 支付明细dto
     *
     * @apiParamExample {json}请求样例：
     *      {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     * @apiSuccessExample {json} 成功返回样例:
     *   {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     */

    @ApiOperation(value = "【退票】提交退票", notes = "【退票】提交退票 开发:bin.xie")
    @PostMapping("/operate")
    public ResponseEntity<CashTransactionDetail> submitRefund(@ApiParam(value = "支付详情") @RequestBody CashTransactionDetail dto){

        return ResponseEntity.ok(service.submitRefund(dto));
    }

    /**
     * @api {GET} {{payment-service_url}}/api/cash/refund/uncheck/query【退票】不检查查询退票
     * @apiGroup PaymentService
     * @apiDescription 不检查查询退票
     *
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {Date} returnDateFrom 退票日期从
     * @apiParam (请求参数) {Date} returnDateTo 退票日期至
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {String} refBillCode 原支付流水
     * @apiParam (请求参数) {String} draweeAccountNumber 收款方账号
     * @apiParam (请求参数) {String} payeeAccountNumber 退款放账号
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {String} backFlashStatus 回显状态
     * @apiSuccessExample {json} 成功返回样例:
     *   [{
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     *                 ]
     */

    @ApiOperation(value = "【退票】不检查查询退票", notes = "【退票】不检查查询退票 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/uncheck/query")
    public ResponseEntity<List<CashTransactionDetail>> uncheckQuery(@ApiIgnore Pageable pageable,
                                                                    String  billcode,//退款流水号
                                                                    String    returnDateFrom,//退款日期从
                                                                    String    returnDateTo, //退款日期至
                                                                    BigDecimal  amountFrom,//金额从
                                                                    BigDecimal  amountTo, //金额至
                                                                    String  refBillCode,//原支付流水
                                                                    String  draweeAccountNumber,//收款方账号
                                                                    String  payeeAccountNumber,//退款方账号
                                                                    String  partnerCategory,
                                                                    Long partnerId,
                                                                    String  backFlashStatus)throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionDetail> list = service.selectUncheckData(page,
                billcode,
                returnDateFrom ,
                returnDateTo,
                amountFrom,
                amountTo,
                refBillCode,
                draweeAccountNumber,
                payeeAccountNumber,
                partnerCategory,
                partnerId,
                backFlashStatus);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/cash/refund/uncheck/query");
        return new ResponseEntity(list,httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {POST} {{payment-service_url}}/api/cash/refund/approved【退票】退票审批
     * @apiGroup PaymentService
     * @apiDescription 退票审批
     *
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {Date} returnDateFrom 退票日期从
     * @apiParam (请求参数) {Date} returnDateTo 退票日期至
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {String} refBillCode 原支付流水
     * @apiParam (请求参数) {String} draweeAccountNumber 收款方账号
     * @apiParam (请求参数) {String} payeeAccountNumber 退款放账号
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {String} backFlashStatus 回显状态
     * @apiParamExample {json}请求样例：
     *  {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     * @apiSuccessExample {json} 成功返回样例:
     *   {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     *
     */
    @ApiOperation(value = "【退票】退票审批", notes = "【退票】退票审批 开发:bin.xie")
    @PostMapping("/approved")
    public ResponseEntity<CashTransactionDetail> approved(@ApiParam(value = "支付详情") @RequestBody CashTransactionDetail dto){

        return ResponseEntity.ok(service.operate(dto));
    }

    /**
     * @api {POST} {{payment-service_url}}/api/cash/refund/rejected【退票】退票被拒绝
     * @apiGroup PaymentService
     * @apiDescription 退票被拒绝
     *
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {Date} returnDateFrom 退票日期从
     * @apiParam (请求参数) {Date} returnDateTo 退票日期至
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {String} refBillCode 原支付流水
     * @apiParam (请求参数) {String} draweeAccountNumber 收款方账号
     * @apiParam (请求参数) {String} payeeAccountNumber 退款放账号
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {String} backFlashStatus 回显状态
     * @apiParamExample {json}请求样例：
     *  {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     * @apiSuccessExample {json} 成功返回样例:
     *   {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     *
     */
    @ApiOperation(value = "【退票】退票被拒绝", notes = "【退票】退票被拒绝 开发:bin.xie")
    @PostMapping("/rejected")
    public ResponseEntity<CashTransactionDetail> rejected(@ApiParam(value = "支付详情") @RequestBody CashTransactionDetail dto){

        return ResponseEntity.ok(service.operate(dto));
    }


    /**
     * @api {POST} {{payment-service_url}}/api/cash/refund/checked/query【退票】检查查询
     * @apiGroup PaymentService
     * @apiDescription 检查查询
     *
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {Date} returnDateFrom 退票日期从
     * @apiParam (请求参数) {Date} returnDateTo 退票日期至
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {String} refBillCode 原支付流水
     * @apiParam (请求参数) {String} draweeAccountNumber 收款方账号
     * @apiParam (请求参数) {String} payeeAccountNumber 退款放账号
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {String} backFlashStatus 回显状态
     * @apiParamExample {json}请求样例：
     *  {
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }
     * @apiSuccessExample {json} 成功返回样例:
     *   [{
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }]
     *
     */
    @ApiOperation(value = "【退票】检查查询", notes = "【退票】检查查询 开发:bin.xie")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/checked/query")
    public ResponseEntity<List<CashTransactionDetail>> checkedQuery(@ApiIgnore Pageable pageable,
                                                                    String  billcode,//退款流水号
                                                                    String    returnDateFrom,//退款日期从
                                                                    String    returnDateTo, //退款日期至
                                                                    BigDecimal  amountFrom,//金额从
                                                                    BigDecimal  amountTo, //金额至
                                                                    String  refBillCode,//原支付流水
                                                                    String  draweeAccountNumber,//收款方账号
                                                                    String  payeeAccountNumber,//退款方账号
                                                                    String  partnerCategory,
                                                                    Long partnerId,
                                                                    String  backFlashStatus)throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);

        List<CashTransactionDetail> list = service.selectCheckedData(page,
                billcode,
                returnDateFrom,
                returnDateTo,
                amountFrom,
                amountTo,
                refBillCode,
                draweeAccountNumber,
                payeeAccountNumber,
                partnerCategory,
                partnerId,
                backFlashStatus);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/cash/refund/checked/query");
        return new ResponseEntity(list,httpHeaders, HttpStatus.OK);
    }


    /**
     * @api {POST} {{payment-service_url}}/api/cash/refund/partner/query/{type}/{flag}【退票】收款查询
     * @apiGroup PaymentService
     * @apiDescription 收款查询
     *
     * @apiParam (请求参数) {int} type 类型
     * @apiParam (请求参数) {int} flag 标志
     *
     * @apiSuccessExample {json} 成功返回样例:
     *   [{
     *                 "id": "917992066456748034",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-11T13:55:39+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-10-11T13:55:39+08:00",
     *                 "lastUpdatedBy": 1,
     *                 "versionNumber": 1,
     *                 "tenantId": "1",
     *                 "paymentFileName": null,
     *                 "customerBatchNo": "2",
     *                 "billcode": "4",
     *                 "documentCompanyId": "1",
     *                 "paymentCompanyId": "1",
     *                 "draweeCompanyId": "1",
     *                 "documentCategory": "1",
     *                 "documentTypeId": "1",
     *                 "documentId": "1",
     *                 "documentNumber": "111",
     *                 "requisitionDate": "2017-10-11T13:58:39+08:00",
     *                 "employeeId": "1",
     *                 "employeeName": "1",
     *                 "documentLineId": "1",
     *                 "remark": "1",
     *                 "paymentTypeId": "1",
     *                 "paymentTypeCode": "待对接后查",
     *                 "paymentTypeName": "付款方式名称：待对接后查",
     *                 "ebankingFlag": false,
     *                 "payDate": "2017-10-11T13:55:39+08:00",
     *                 "requestTime": "2017-10-11T13:55:39+08:00",
     *                 "paymentStatus": "F",
     *                 "refundStatus": "N",
     *                 "paymentReturnStatus": "1",
     *                 "partnerCategory": "1",
     *                 "partnerId": "1",
     *                 "partnerCode": "1",
     *                 "partnerName": "1",
     *                 "currency": "RMB",
     *                 "exchangeRate": 2.9,
     *                 "amount": 4,
     *                 "writeOffAmount": 1,
     *                 "cshTransactionTypeCode": "1",
     *                 "cshTransactionClassId": "1",
     *                 "cashFlowItemId": "1",
     *                 "scheduleDate": "2017-10-11T14:56:09+08:00",
     *                 "draweeId": "1",
     *                 "draweeAccountNumber": "公司银行付款账户账号",
     *                 "draweeAccountName": "公司银行付款账户名称",
     *                 "draweeBankNumber": "待解决：根据付款的companyBankId查",
     *                 "draweeBankProvinceCode": "待解决：根据付款的companyBankId查",
     *                 "draweeBankCityCode": "待解决：根据付款的companyBankId查",
     *                 "payeeAccountNumber": "1222222222222222222",
     *                 "payeeAccountName": "1",
     *                 "payeeBankNumber": "待解决，根据收款的银行账号查",
     *                 "payeeBankName": "待解决，根据收款账号查询收款账号所属银行名称",
     *                 "payeeBankProvinceCode": "待解决：根据收款账号查询",
     *                 "payeeBankCityCode": "待解决：根据收款账号查询",
     *                 "responseCode": "接口响应码",
     *                 "responseMessage": "接口响应信息",
     *                 "readFlag": null,
     *                 "returnState": null,
     *                 "resultCode": null,
     *                 "resultMessage": null,
     *                 "accCheckCode": null,
     *                 "accCheckDate": null,
     *                 "accountStatus": false,
     *                 "cshTransactionDataId": "1",
     *                 "companyId": "1"
     *                 }]
     *
     */

    @ApiOperation(value = "【退票】收款查询", notes = "【退票】收款查询 开发:bin.xie")
    @GetMapping("/partner/query/{type}/{flag}")
    public ResponseEntity<List<PartnerSelectDTO>> listPartnerSelect(@PathVariable(value = "type") int type,
                                                                    @PathVariable(value = "flag") int flag){

        return ResponseEntity.ok(service.listPartner(type,flag));
    }
}
