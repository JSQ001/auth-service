package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.payment.service.CashTransactionDataRelationService;
import com.hand.hcf.app.payment.service.CashTransactionDataService;
import com.hand.hcf.app.payment.web.dto.AmountAndDocumentNumberDTO;
import com.hand.hcf.app.payment.web.dto.CashDataPublicReportHeaderDTO;
import com.hand.hcf.app.payment.web.dto.CashTransactionDataWebDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by cbc on 2017/9/29.
 */
@RestController
@RequestMapping("/api/cash/transactionData")
@AllArgsConstructor
public class CashTransactionDataController {

    private final CashTransactionDataService transactionDataService;
    private final CashTransactionDataRelationService cashTransactionDataRelationService;

    /**
     * 根据条件查询待付提交数据
     *
     * @param documentNumber
     * @param documentCategory
     * @param employeeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param paymentMethodCategory
     * @param partnerCategory
     * @param partnerId
     * @param pageable
     * @return
     * @throws URISyntaxException
     */
    /**
     * @api {GET} /api/cash/transactionData/query 【根据条件查询待付提交数据】根据条件查询待付提交数据
     * @apiDescription 该接口用于根据条件查询待付提交数据
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Boolean} isEnabled 是否启用
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentTypeId 单据类型
     * @apiParam (请求参数) {Long} employeeId 员工
     * @apiParam (请求参数) {Date} requisitionDateFrom 申请日期从
     * @apiParam (请求参数) {Date} requisitionDateTo 申请日期至
     * @apiParam (请求参数) {String} paymentMethodCategory 支付类型
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {Long} partnerId 收款方
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {List} paymentCompanyId 付款公司
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiSuccess (返回参数) {Boolean} isEnabled 是否启用
     * @apiSuccess (返回参数) {Boolean} isDeleted 是否删除
     * @apiSuccess (返回参数) {ZonedDateTime} createdDate 创建日期
     * @apiSuccess (返回参数) {Long} createdBy 创建人
     * @apiSuccess (返回参数) {ZonedDateTime} lastUpdatedDate 最后更新日期
     * @apiSuccess (返回参数) {Long} lastUpdatedBy 最后更新人
     * @apiSuccess (返回参数) {Integer} versionNumber 版本号
     * @apiSuccess (返回参数) {Long} setOfBookId 账套ID
     * @apiSuccess (返回参数) {String} typeCode 现金事务类型代码(现金交易事务类型代码) PAYMENT 付款,PREPAYMENT 预付款,PREPAYMENT_RECEIPT 预收款,RECEIPT 收款
     * @apiSuccess (返回参数) {String} typeName 现金事务类型名称(现金交易事务类型名称)
     * @apiSuccess (返回参数) {String} classCode 现金事务分类代码
     * @apiSuccess (返回参数) {String} description 现金事务分类名称
     * @apiSuccess (返回参数) {String} setOfBookCode 账套代码
     * @apiSuccess (返回参数) {String} setOfBookName 账套名称


     * @apiParamExample {json} 请求参数
     *  /api/cash/transactionData/query?page=1&size=1
     * @apiSuccessExample {json}  result 成功返回值
     * [
     *
     *             {
     *                 "documentNumber": "PAY1000201710122118",
     *                 "documentCategory": "PAYMENT_REQUISITION",
     *                 "employeeName": "来者可追",
     *                 "requisitionDate": "2017-11-29T15:10:28+08:00",
     *                 "amount": 2017,
     *                 "payableAmount": 2017,
     *                 "currentPayAmount": 2017,
     *                 "paymentMethodCategory": "ONLINE_PAYMENT",
     *                 "partnerCategory": "EMPLOYEE",
     *                 "partnerName": "来者可追",
     *                 "accountNumber": "6124666677778888",
     *                 "versionNumber": 3,
     *                 "billcode": null,
     *                 "customerBatchNo": null,
     *                 "payDate": null,
     *                 "detailId": null,
     *                 "employeeId": 1,
     *                 "curreny": null,
     *                 "paymentStatus": "N",
     *                 "paymentStatusName": "未支付",
     *                 "documentCategoryName": "借款单",
     *                 "paymentMethodCategoryName": "线上",
     *                 "partnerCategoryName": "员工",
     *                 "id": "5",
     *                 "isEnabled": true,
     *                 "isDeleted": false,
     *                 "createdDate": "2017-10-12T21:19:16+08:00",
     *                 "createdBy": 1,
     *                 "lastUpdatedDate": "2017-11-28T19:53:41+08:00",
     *                 "lastUpdatedBy": 1
     *             }
     *         ]
     */
    @GetMapping(value = "/query")
    public ResponseEntity getTransactionDataByCond(
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) String documentCategory,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String requisitionDateFrom,
            @RequestParam(required = false) String requisitionDateTo,
            @RequestParam(required = false) String paymentMethodCategory,
            @RequestParam(required = false) String partnerCategory,
            @RequestParam(required = false) Long partnerId,
            @RequestParam(required = false) BigDecimal amountFrom,
            @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) String documentTypeName,
            @RequestParam(required = false) List<Long> paymentCompanyId,
            Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashTransactionDataWebDTO> result = transactionDataService.getTransactionDataByCond(
                paymentCompanyId,
                documentNumber,
                documentCategory,
                employeeId,
                requisitionDateFrom,
                requisitionDateTo,
                paymentMethodCategory,
                partnerCategory,
                partnerId,
                amountFrom,
                amountTo,
                documentTypeName,
                page);
        HttpHeaders headers = PageUtil.generateHttpHeaders(page, "/api/cash/transactionData/query");
        return new ResponseEntity(result, headers, HttpStatus.OK);

    }

    /**
     * 修改待付提交数据界面
     *
     * @param list
     * @return
     */

    /**
     * @api {PUT} /api/cash/transactionData 【修改待付提交数据界面】修改待付提交数据界面
     * @apiDescription 该接口用于修改待付提交数据界面
     * @apiGroup PaymentService
     * @apiParam (请求参数) {List} list 支付数据DTO集合
     * @apiSuccess (返回参数) {List} result
     * @apiParamExample {json} 请求参数
     *         [
     *             {
     *                 "id":4,
     *                 "accountNumber":6124666677778888,
     *                 "versionNumber": 11
     *             }
     *         ]
     */
    @PutMapping
    public ResponseEntity updateTransactionDataBatch(@RequestBody List<CashTransactionDataWebDTO> list) {
        List<CashTransactionDataWebDTO> result = transactionDataService.updateTransactionDataBatch(list);
        return ResponseEntity.ok(result);
    }

    /**
     * 统一支付结果查询：按照单据头信息查询
     *
     * @param documentCategory 业务大类
     * @param documentHeaderId 所属单据头id
     * @return 结果
     */
    /**
     * @api {GET} /api/cash/transactionData/paymentResult/header 【统一支付结果查询：按照单据头信息查询】统一支付结果查询：按照单据头信息查询
     * @apiDescription 该接口用于统一支付结果查询：按照单据头信息查询
     * @apiGroup PaymentService
     * @apiParam (请求参数) {Boolean} isEnabled 是否启用
     * @apiParam (请求参数) {String} billcode 流水号
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentTypeId 单据类型
     * @apiParam (请求参数) {Long} employeeId 员工
     * @apiParam (请求参数) {Date} requisitionDateFrom 申请日期从
     * @apiParam (请求参数) {Date} requisitionDateTo 申请日期至
     * @apiParam (请求参数) {String} paymentMethodCategory 支付类型
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {Long} partnerId 收款方
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {List} paymentCompanyId 付款公司
     * @apiSuccess (返回参数) {BigDecimal} aDouble 具体的已支付金额



     * @apiParamExample {json} 请求参数
     *              /api/cash/transactionData/paymentResult/header?documentCategory=EXP_REPORT&documentHeaderId=911065727674056702
     * @apiSuccessExample {json}  aDouble 成功返回值
     *         * 返回具体的已支付金额
     *         * 688.24
     *
     */
    @GetMapping("/paymentResult/header")
    public ResponseEntity<BigDecimal> queryByDocumentHeaderId(@RequestParam(value = "documentCategory") String documentCategory,
                                                              @RequestParam(value = "documentHeaderId") Long documentHeaderId) {
        BigDecimal aDouble = transactionDataService.queryByDocumentHeaderId(documentCategory, documentHeaderId, null);
        return ResponseEntity.ok(aDouble);
    }

    /**
     * 统一支付结果查询：按照单据行信息查询
     *
     * @param documentCategory 业务大类
     * @param documentHeaderId 所属单据头id
     * @param documentLineId   待付行id
     * @return 结果
     */

    /**
     * @api {GET} /api/cash/transactionData/paymentResult/line 【统一支付结果查询：按照单据行信息查询】统一支付结果查询：按照单据行信息查询
     * @apiDescription 该接口用于统一支付结果查询：按照单据行信息查询
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {Long} documentHeaderId 单据头id
     * @apiParam (请求参数) {Long} documentLineId 单据行id
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiParamExample {json} 请求参数
     *       /api/cash/transactionData/paymentResult/line?documentCategory=EXP_REPORT&documentHeaderId=911065727674056702&documentLineId=911065727674056701
     * @apiSuccessExample {json} aDouble 成功返回值
     *         * 返回具体的已支付金额
     *         * 688.24
     *
     */
    @GetMapping("/paymentResult/line")
    public ResponseEntity<BigDecimal> queryByDocumentLineId(@RequestParam(value = "documentCategory") String documentCategory,
                                                            @RequestParam(value = "documentHeaderId") Long documentHeaderId,
                                                            @RequestParam(value = "documentLineId") Long documentLineId) {
//        PaymentResultDTO result = cashTransactionDetailService.queryByDocumentHeaderId(documentCategory, documentHeaderId, documentLineId);
        BigDecimal aDouble = transactionDataService.queryByDocumentHeaderId(documentCategory, documentHeaderId, documentLineId);
        return ResponseEntity.ok(aDouble);
    }


    /**
     *  查询对应币种的总金额和 总单据数
     * @return
     */

    /**
     * @api {GET} /api/cash/transactionData/select/totalAmountAndDocumentNum 【查询对应币种的总金额和 总单据数】查询对应币种的总金额和 总单据数
     * @apiDescription 该接口用于查询对应币种的总金额和 总单据数
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} documentNumber 单据编号
     * @apiParam (请求参数) {String} documentCategory 单据类型
     * @apiParam (请求参数) {Long} employeeId 员工id
     * @apiParam (请求参数) {Date} requisitionDateFrom 申请日期从
     * @apiParam (请求参数) {Date} requisitionDateTo 申请日期至
     * @apiParam (请求参数) {String} paymentMethodCategory 支付类型
     * @apiParam (请求参数) {String} partnerCategory 收款类型
     * @apiParam (请求参数) {Long} partnerId 收款方
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {List} paymentCompanyId 付款公司
     * @apiParam (请求参数) {String} documentTypeName 单据类型名称
     * @apiSuccess (返回参数) {Long} id 主键ID
     * @apiParamExample {json} 请求参数
     *       /api/cash/transactionData/paymentResult/line?documentCategory=EXP_REPORT&documentHeaderId=911065727674056702&documentLineId=911065727674056701
     * @apiSuccessExample {json} 成功返回值
     *         [
     *             {
     *                 "currency": "CNY",
     *                 "totalAmount": 302697,
     *                 "documentNumber": 5
     *             }
     *         ]
     *
     */
    @GetMapping("/select/totalAmountAndDocumentNum")
    public ResponseEntity getTotalAmountAndDocumentNum(@RequestParam(required = false) String documentNumber,
                                                       @RequestParam(required = false) String documentCategory,
                                                       @RequestParam(required = false) Long employeeId,
                                                       @RequestParam(required = false) String requisitionDateFrom,
                                                       @RequestParam(required = false) String requisitionDateTo,
                                                       @RequestParam(required = false) String paymentMethodCategory,
                                                       @RequestParam(required = false) String partnerCategory,
                                                       @RequestParam(required = false) Long partnerId,
                                                       @RequestParam(required = false) BigDecimal amountFrom,
                                                       @RequestParam(required = false) BigDecimal amountTo,
                                                       @RequestParam(required = false) List<Long> paymentCompanyId,
                                                       @RequestParam(required = false) String documentTypeName){
        List<AmountAndDocumentNumberDTO> result = transactionDataService.getTotalAmountAndDocumentNum(
                                                        paymentCompanyId,
                                                        documentNumber,
                                                        documentCategory,
                                                        employeeId,
                                                        requisitionDateFrom == null ? null : TypeConversionUtils.getStartTimeForDayYYMMDD(requisitionDateFrom) ,
                                                        requisitionDateTo == null? null :  TypeConversionUtils.getEndTimeForDayYYMMDD(requisitionDateTo),
                                                        paymentMethodCategory,
                                                        partnerCategory,
                                                        partnerId,
                                                        amountFrom,
                                                        amountTo,
                                                        documentTypeName
        );
        return ResponseEntity.ok(result);
    }
    /**
     * @Author: bin.xie
     * @Description: 
     * @param: reportNumber 报账单编号
     * @param: applicationId 申请人ID
     * @param: allType 是否所有类型
     * @param: formTypes 关联单据类型 [1,2,3]
     * @param: pageable  
     * @return: org.springframework.http.ResponseEntity<java.util.List<com.hand.hcf.app.payment.CashDataPublicReportHeaderDTO>>
     * @Date: Created in 2018/4/25 17:09
     * @Modified by
     */

    /**
     * @api {POST} /api/cash/transactionData/relation/query 【关联关系】关联关系
     * @apiDescription 该接口用于查询关联关系
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} reportNumber 报账单编号
     * @apiParam (请求参数) {Long} applicationId 申请人ID
     * @apiParam (请求参数) {Boolean} allType 是否所有类型
     * @apiParam (请求参数) {Long} formTypes 关联单据类型 [1,2,3]
     * @apiParam (请求参数) {Date} requisitionDateTo 申请日期至
     * @apiParam (请求参数) {pageable} pageable 分页
     * @apiSuccess (返回参数) {List} resultList
     * @apiParamExample {json} 请求参数
     *             {
     *                 "reportNumber": "CNY",
     *                 "applicationId": 302697,
     *                 "allType": true,
     *                 "formTypes": [1]
     *             }
     * @apiSuccessExample {json}List 成功返回值
     *         [
     *             {
     *                 "reportHeadId": "212121",
     *                 "reportNumber": 222222,
     *                 "reportTypeName": qqq
     *             }
     *         ]
     *
     */
    @PostMapping("/relation/query")
    public ResponseEntity<List<CashDataPublicReportHeaderDTO>> getAcpAssociated(@RequestParam(value = "reportNumber", required = false) String reportNumber,
                                                                                @RequestParam(value = "applicationId") Long applicationId,
                                                                                @RequestParam(value = "allType") Boolean allType,
                                                                                @RequestBody(required = false) List<Long> formTypes,
                                                                                @RequestParam(value = "documentTypeId", required = false) Long documentTypeId, Pageable pageable) throws URISyntaxException {

        Page page = PageUtil.getPage(pageable);
        List<CashDataPublicReportHeaderDTO> resultList = cashTransactionDataRelationService.queryReportAssociatedAcp(
                reportNumber, applicationId, allType, formTypes, page, documentTypeId);
        HttpHeaders headers = PageUtil.generateHttpHeaders(page, "/api/cash/transactionData/relation/query");
        return new ResponseEntity<>(resultList, headers, HttpStatus.OK);
    }


    /**
     * @Author: bin.xie
     * @Description: 根据报账单ID查询通用支付数据
     * @param: headerId
     * @param: pageable
     * @return
     * @Date: Created in 2018/5/9 9:31
     * @Modified by
     */

    /**
     * @api {GET} /api/cash/transactionData/public/query 【根据报账单ID查询通用支付数据】根据报账单ID查询通用支付数据
     * @apiDescription 根据报账单ID查询通用支付数据
     * @apiGroup PaymentService
     * @apiParam (请求参数) {String} headerId 报账单id
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {Long} partnerId 收款方
     * @apiParam (请求参数) {BigDecimal} amountFrom 金额从
     * @apiParam (请求参数) {BigDecimal} amountTo 金额至
     * @apiParam (请求参数) {pageable} pageable 分页
     * @apiSuccess (返回参数) {List} resultList
     * @apiParamExample {json} 请求参数
     *             {
     *                 "headerId": 32323,
     *                 "partnerCategory": "",
     *                 "partnerId": 323232,
     *                 "amountFrom": 20.24,
     *                 "amountTo": 20.77,
     *             }
     * @apiSuccessExample {json} 成功返回值
     *         [
     *             {
     *                 "reportHeadId": "212121",
     *                 "reportNumber": 222222,
     *                 "reportTypeName": qqq
     *             }
     *         ]
     *
     */
    @PostMapping("/public/query")
    public ResponseEntity<List<CashTransactionDataWebDTO>> queryPublicPage(@RequestParam(value = "headerId") Long headerId,
                                                                           @RequestBody List<Long> dataIds,
                                                                           @RequestParam(value = "partnerCategory", required = false) String partnerCategory,
                                                                           @RequestParam(value = "partnerId", required = false) Long partnerId,
                                                                           @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                                                           @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                                                           Pageable pageable)throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        Page<CashTransactionDataWebDTO> result = transactionDataService.selectPublicPage(headerId,dataIds, page, partnerCategory, partnerId, amountFrom, amountTo);
        HttpHeaders headers = PageUtil.generateHttpHeaders(result, "/api/cash/transactionData/public/query");
        return new ResponseEntity<>(result.getRecords(), headers, HttpStatus.OK);
    }
}
