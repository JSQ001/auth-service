package com.hand.hcf.app.payment.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.common.co.CashWriteOffAccountCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.service.CashWriteOffService;
import com.hand.hcf.app.payment.web.dto.CashWriteOffHistoryDTO;
import com.hand.hcf.app.payment.web.dto.CashWriteOffRequestWebDto;
import com.hand.hcf.app.payment.web.dto.CashWriteOffReserveDTO;
import com.hand.hcf.app.payment.web.dto.CashWriteOffWebDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;

;

/**
 * Created by kai.zhang on 2017-10-31.
 */
@Api(tags="核销数据API")
@RestController
@RequestMapping("/api/payment/cash/write/off")
public class CashWriteOffController {

    private final CashWriteOffService cashWriteOffService;


    public CashWriteOffController(CashWriteOffService cashWriteOffService){
        this.cashWriteOffService = cashWriteOffService;
    }

//    @PostMapping
//    public CashWriteOff createCashWriteOff(@RequestBody CashWriteOff cashWriteOff){
//        cashWriteOff = cashWriteOffService.createCashWriteOff(cashWriteOff);
//        return cashWriteOff;
//    }
//
//    @PostMapping("/batch")
//    public List<CashWriteOff> createCashWriteOffBatch(@RequestBody List<CashWriteOff> list){
//        list = cashWriteOffService.createCashWriteOffBatch(list);
//        return list;
//    }
//
//    @PutMapping
//    public CashWriteOff updateCashWriteOff(@RequestBody CashWriteOff cashWriteOff){
//        cashWriteOff = cashWriteOffService.updateCashWriteOff(cashWriteOff);
//        return cashWriteOff;
//    }
//
//    @PutMapping("/batch")
//    public List<CashWriteOff> updateCashWriteOffBatch(@RequestBody List<CashWriteOff> list){
//        list = cashWriteOffService.updateCashWriteOffBatch(list);
//        return list;
//    }
//
    /**
     * @api {POST} {{payment-service_url}}/api/payment/cash/write/off/{id} 【核销】删除核销数据
     * @apiGroup PaymentService
     * @apiDescription 删除核销
     * @apiParam (请求参数) {Long} id 核销id
     * @apiSuccess (返回信息) {String} result 返回信息:SUCCESS->成功; NO_WRITE_OFF_DATA->无核销数据; NO_NEED_ACCOUNT->无需核算; 其他为报错信息
     * @apiSuccessExample {json} 成功返回样例:
     * SUCCESS
     */

    @ApiOperation(value = "删除核销数据", notes = "删除核销数据 开发:")
    @DeleteMapping("/{id}")
    public void deleteCashWriteOffById(@PathVariable Long id){
        cashWriteOffService.deleteCashWriteOffById(id);
    }
//
//    @DeleteMapping
//    public void deleteCashWriteOffByIds(@RequestBody List<Long> list){
//        cashWriteOffService.deleteCashWriteOffByIds(list);
//    }
//
//    @GetMapping("/{id}")
//    public CashWriteOff getCashWriteOffById(@PathVariable Long id){
//        return cashWriteOffService.getCashWriteOffById(id);
//    }

    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/query 【核销】查询核销数据
     * @apiGroup PaymentService
     * @apiDescription 查询核销
     * @apiParam (请求参数) {Long} companyId 公司id
     * @apiParam (请求参数) {Long} tenantId 租户id
     * @apiParam (请求参数) {String} partnerCategory 收款方类型
     * @apiParam (请求参数) {Long} partnerId 收款方
     * @apiParam (请求参数) {Long} formId 表单id
     * @apiParam (请求参数) {Long} exportHeaderId 报销头id
     * @apiParam (请求参数) {Long} contractId 合同
     * @apiParam (请求参数) {String} documentType 单据类型
     * @apiParam (请求参数) {Long} documentLineId 单据行id
     * @apiParam (请求参数) {String} currencyCode 币种代码
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiSuccess (返回信息) {List} result
     * @apiSuccessExample {json} 成功返回样例:
     * [
     *      {
     *          "cshTransactionDetailId":122,
     *          "billcode": "1233",
     *          "prepaymentRequisitionAmount": 22.34
     *      }
     * ]
     *
     */

    @ApiOperation(value = "查询核销数据", notes = "查询核销数据 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/query")
    public ResponseEntity<List<CashWriteOffWebDto>> getCashWriteOffDetailByDocumentMsg(@ApiParam(value = "租户id") @RequestParam Long tenantId,
                                                                                       @ApiParam(value = "公司id") @RequestParam Long companyId,
                                                                                       @ApiParam(value = "收款方类型") @RequestParam String partnerCategory,
                                                                                       @ApiParam(value = "收款方") @RequestParam Long partnerId,
                                                                                       @ApiParam(value = "表单id") @RequestParam Long formId,
                                                                                       @ApiParam(value = "报销头id") @RequestParam Long exportHeaderId,
                                                                                       @ApiParam(value = "合同") @RequestParam(required = false) Long contractId,
                                                                                       @ApiParam(value = "单据类型") @RequestParam String documentType,
                                                                                       @ApiParam(value = "单据行id") @RequestParam Long documentLineId,
                                                                                       @ApiParam(value = "币种代码") @RequestParam String currencyCode,
                                                                                       @ApiIgnore Pageable pageable) throws URISyntaxException {
        return cashWriteOffService.getCashWriteOffDetailByDocumentMsg(tenantId,
                companyId,
                partnerCategory,
                partnerId,
                formId,
                exportHeaderId,
                contractId,
                documentType,
                documentLineId,
                currencyCode,
                "/api/payment/cash/write/off/query",
                pageable);
    }

    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/history 【核销】查询核销数据
     * @apiGroup PaymentService
     * @apiDescription 查询核销历史
     * @apiParam (请求参数) {String} documentType 单据类型
     * @apiParam (请求参数) {Long} documentHeaderId 单据头
     * @apiParam (请求参数) {Long} documentLineId 单据行
     * @apiParam (请求参数) {Pageable} pageable 分页
     * @apiSuccess (返回信息) {List} result
     * @apiSuccessExample {json} 成功返回样例:
     * [
     *      {
     *          "cshTransactionDetailId":122,
     *          "billcode": "1233",
     *          "prepaymentRequisitionAmount": 22.34
     *      }
     * ]
     */

    @ApiOperation(value = "查询核销历史数据", notes = "查询核销历史数据 开发:")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/history")
    public ResponseEntity<List<CashWriteOffWebDto>> getCashWriteOffHistory(@ApiParam(value = "单据类型") @RequestParam String documentType,
                                                                           @ApiParam(value = "单据头id") @RequestParam Long documentHeaderId,
                                                                           @ApiParam(value = "单据行id") @RequestParam(required = false) Long documentLineId,
                                                                           @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashWriteOffWebDto> cashWriteOffHistory = cashWriteOffService.getCashWriteOffHistory(documentType, documentHeaderId, documentLineId, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/payment/cash/write/off/history");
        return new ResponseEntity(cashWriteOffHistory, httpHeaders, HttpStatus.OK);
    }

    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/history/all 【核销】查询核销数据
     * @apiGroup PaymentService
     * @apiDescription 查询所有核销历史
     * @apiParam (请求参数) {String} documentType 单据类型
     * @apiParam (请求参数) {Long} documentHeaderId 单据头
     * @apiParam (请求参数) {Long} documentLineId 单据行
     * @apiSuccess (返回信息) {List} result
     * @apiSuccessExample {json}成功返回样例:
     * [
     *      {
     *          "cshTransactionDetailId":122,
     *          "billcode": "1233",
     *          "prepaymentRequisitionAmount": 22.34
     *      }
     * ]
     */

    @ApiOperation(value = "查询所有核销历史", notes = "查询所有核销历史 开发：")
    @GetMapping("/history/all")
    public ResponseEntity<List<CashWriteOffWebDto>> getCashWriteOffHistoryAll(@ApiParam(value = "单据类型") @RequestParam String documentType,
                                                                              @ApiParam(value = "单据头id") @RequestParam Long documentHeaderId,
                                                                              @ApiParam(value = "单据行id") @RequestParam(required = false) Long documentLineId) throws URISyntaxException {
        List<CashWriteOffWebDto> cashWriteOffHistory = cashWriteOffService.getCashWriteOffHistory(documentType, documentHeaderId, documentLineId, null);
        int size = cashWriteOffHistory.size();
        Page page = PageUtil.getPage(0, size);
        page.setTotal(size);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/payment/cash/write/off/history/all");
        return new ResponseEntity(cashWriteOffHistory, httpHeaders, HttpStatus.OK);
    }

    /**
     * 核销 - 确认核销后不更新支付平台，只插入核销记录
     * @param requestDto
     * @return
     */

    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/do 【核销】核销
     * @apiGroup PaymentService
     * @apiDescription 核销
     * @apiParam (请求参数) {CashWriteOffRequestDto} requestDto 核销请求
     * @apiParamExample {json}请求样例：
     *  {
     *      "partnerCategory": "VENDER",
     *      "partnerId": 11,
     *      "companyId": 928
     *  }
     * @apiSuccess (返回信息) {List} result
     * @apiSuccessExample {json} 成功返回样例:
     * [
     *      {
     *          "cshTransactionDetailId":122,
     *          "billcode": "1233",
     *          "prepaymentRequisitionAmount": 22.34
     *      }
     * ]
     */

    @ApiOperation(value = "核销请求", notes = "核销请求 开发：")
    @PostMapping("/do")
    public ResponseEntity<List<CashWriteOffWebDto>> doWriteOff(@ApiParam(value = "核销申请信息") @RequestBody  @Valid CashWriteOffRequestWebDto requestDto){
        cashWriteOffService.writeOff(requestDto);
        return ResponseEntity.ok(requestDto.getCashWriteOffMsg());
    }

    /**
     * 点击确认核销后，核销立即生效，并与支付平台交互
     * @param requestDto
     * @return
     */
    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/do/at/once 【核销】核销立即生效
     * @apiGroup PaymentService
     * @apiDescription 点击确认核销后，核销立即生效，并与支付平台交互
     * @apiParam (请求参数) {CashWriteOffRequestDto} requestDto 核销请求
     * @apiParamExample {json}请求样例：
     *  {
     *      "partnerCategory": "VENDER",
     *      "partnerId": 11,
     *      "companyId": 928
     *  }
     *
     * @apiSuccess (返回信息) {List} result
     * @apiSuccessExample {json} 成功返回样例:
     * [
     *      {
     *          "cshTransactionDetailId":122,
     *          "billcode": "1233",
     *          "prepaymentRequisitionAmount": 22.34
     *      }
     * ]
     */

    @ApiOperation(value = "核销立即生效", notes = "核销立即生效 开发：")
    @PostMapping("/do/at/once")
    public ResponseEntity<List<CashWriteOffWebDto>> writeOffAtOnce(@ApiParam(value = "核销申请信息") @RequestBody  @Valid CashWriteOffRequestWebDto requestDto){
        cashWriteOffService.writeOffAtOnce(requestDto);
        return ResponseEntity.ok(requestDto.getCashWriteOffMsg());
    }

    /**
     * 核销生效，并与支付平台交互
     * @param documentType
     * @param documentHeaderId
     * @param documentLineIds
     * @param tenantId
     * @param lastUpdatedBy
     */

    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/effect【核销】核销生效，并与支付平台交互
     * @apiGroup PaymentService
     * @apiDescription 核销生效，并与支付平台交互
     * @apiParam (请求参数) {String} documentType 单据类型
     * @apiParam (请求参数) {Long} documentHeaderId 单据头
     * @apiParam (请求参数) {List} documentLineIds 单据行
     * @apiParam (请求参数) {Long} tenantId 租户
     * @apiParam (请求参数) {Long} lastUpdatedBy 最后更新人
     */

    @ApiOperation(value = "核销生效并与支付平台交互", notes = "核销生效并与支付平台交互 开发：")
    @PostMapping("/effect")
    public void writeOffTakeEffect(@ApiParam(value = "单据类型") @RequestParam String documentType,
                                   @ApiParam(value = "单据头id") @RequestParam Long documentHeaderId,
                                   @ApiParam(value = "单据行id") @RequestBody(required = false) List<Long> documentLineIds,
                                   @ApiParam(value = "租户id") @RequestParam Long tenantId,
                                   @ApiParam(value = "最后更新者") @RequestParam Long lastUpdatedBy){
        cashWriteOffService.writeOffTakeEffect(documentType,documentHeaderId,documentLineIds,tenantId,lastUpdatedBy);
    }

    /**
     * @api {POST} {{payment-service_url}}/api/payment/cash/write/off/init/journal 【核销】核销核算
     * @apiGroup PaymentService
     * @apiDescription 核销记录生成凭证
     * @apiParam (请求参数) {String} documentType 单据类型
     * @apiParam (请求参数) {Long} documentHeaderId 单据头ID
     * @apiParam (请求参数) {List} [documentLineIds] 单据行ID(若不传值则与单据相关核销记录全部生成凭证)
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {Long} operatorId 操作人ID
     * @apiParam (请求参数) {ZonedDateTime} accountDate 账务日期
     * @apiParam (请求参数) {String} [accountPeriod] 账务期间
     * @apiParamExample {json}请求样例：
     * {
    "documentType":"PUBLIC_REPORT",
    "documentHeaderId":986872553592610817,
    "tenantId":937506219191881730,
    "operatorId":1,
    "accountDate":"2018-04-12T15:17:06+08:00",
    "accountPeriod":"2018-4"
    }
     * @apiSuccess (返回信息) {String} result 返回信息:SUCCESS->成功; NO_WRITE_OFF_DATA->无核销数据; NO_NEED_ACCOUNT->无需核算; 其他为报错信息
     * @apiSuccessExample {json} 成功返回样例:
     * SUCCESS
     */

    @ApiOperation(value = "核销记录生成凭证", notes = "核销记录生成凭证 开发：")
    @PostMapping("/init/journal")
    public String writeOffCreateJournalLines(@ApiParam(value = "核销账户信息") @RequestBody @Valid CashWriteOffAccountCO co){
        return cashWriteOffService.writeOffCreateJournalLines(co.getDocumentType(),
                co.getDocumentHeaderId(),
                co.getDocumentLineIds(),
                co.getTenantId(),
                co.getOperatorId(),
                co.getAccountDate(),
                co.getAccountPeriod(),
                "WRITE_OFF");
    }

    /**
     * @api {POST} {{payment-service_url}}/api/payment/cash/write/off/reverse/init/journal 【核销】核销反冲核算
     * @apiGroup PaymentService
     * @apiDescription 核销反冲记录生成凭证
     * @apiParam (请求参数) {String} documentType 单据类型
     * @apiParam (请求参数) {Long} documentHeaderId 单据头ID
     * @apiParam (请求参数) {List} [documentLineIds] 单据行ID(若不传值则与单据相关核销记录全部生成凭证)
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {Long} operatorId 操作人ID
     * @apiParam (请求参数) {ZonedDateTime} accountDate 账务日期
     * @apiParam (请求参数) {String} [accountPeriod] 账务期间
     *
     * @apiParamExample {json}请求样例：
     * {
        "documentType":"PUBLIC_REPORT",
        "documentHeaderId":986872553592610817,
        "tenantId":937506219191881730,
        "operatorId":1,
        "accountDate":"2018-04-12T15:17:06+08:00",
        "accountPeriod":"2018-4"
        }
     * @apiSuccess (返回信息) {String} result 返回信息:SUCCESS->成功; NO_WRITE_OFF_DATA->无核销数据; NO_NEED_ACCOUNT->无需核算; 其他为报错信息
     * @apiSuccessExample {json} 成功返回样例:
     * SUCCESS
     */

    @ApiOperation(value = "核销反冲记录生成凭证", notes = "核销反冲记录生成凭证 开发：")
    @PostMapping("/reverse/init/journal")
    public String writeOffCreateJournalLinesReverse(@ApiParam(value = "核销账户信息") @RequestBody @Valid CashWriteOffAccountCO co){
        return cashWriteOffService.writeOffCreateJournalLines(co.getDocumentType(),
                co.getDocumentHeaderId(),
                co.getDocumentLineIds(),
                co.getTenantId(),
                co.getOperatorId(),
                co.getAccountDate(),
                co.getAccountPeriod(),
                "WRITE_OFF_RESERVED");
    }

    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/wait/reserve/detail 【核销】待反冲核销记录
     * @apiGroup PaymentService
     * @apiDescription 待反冲核销记录查询
     * @apiParam (请求参数) {String} [documentNumber] 报账单编号
     * @apiParam (请求参数) {Long} [applicantId] 单据申请人ID
     * @apiParam (请求参数) {String} [sourceDocumentNumber] 被核销单据编号
     * @apiParam (请求参数) {String} [billCode] 支付流水号
     * @apiParam (请求参数) {String} [createdDateFrom] 单据创建日期从 YYYY-MM-DD
     * @apiParam (请求参数) {String} [createdDateTo] 单据创建日期至 YYYY-MM-DD
     * @apiParam (请求参数) {BigDecimal} [writeOffAmountFrom] 核销金额从
     * @apiParam (请求参数) {BigDecimal} [writeOffAmountTo] 核销金额至
     * @apiParam (请求参数) {String} [writeOffDateFrom] 核销反冲日期从
     * @apiParam (请求参数) {String} [writeOffDateTo] 核销反冲日期至
     * @apiParam (请求参数) {Integer} page 页数
     * @apiParam (请求参数) {Integer} size 每页大小
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/wait/reserve/detail?page=1&size=10
     * @apiSuccess (核销相关信息) {Long} id 核销ID
     * @apiSuccess (核销相关信息) {Long} cshTransactionDetailId 支付明细ID
     * @apiSuccess (核销相关信息) {BigDecimal} writeOffAmount 核销金额
     * @apiSuccess (核销相关信息) {String} documentType 单据类型
     * @apiSuccess (核销相关信息) {Long} documentHeaderId 核销单据头id
     * @apiSuccess (核销相关信息) {Long} documentLineId 核销单据行id
     * @apiSuccess (核销相关信息) {ZonedDateTime} writeOffDate 核销日期 | 反冲日期
     * @apiSuccess (核销相关信息) {String} periodName 期间
     * @apiSuccess (核销相关信息) {Long} tenantId 租户id
     * @apiSuccess (核销相关信息) {String} status 核销状态:N未生效;P已生效;Y:已核算 | 核销反冲状态:N拒绝;P已提交;Y:已审核
     * @apiSuccess (核销相关信息) {String} statusDescription 核销状态描述
     * @apiSuccess (核销相关信息) {String} operationType 操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
     * @apiSuccess (核销相关信息) {Long} setOfBooksId 账套ID
     * @apiSuccess (核销相关信息) {String} remark 备注
     * @apiSuccess (核销相关信息) {String} approvalOpinions 审核意见
     * @apiSuccess (核销相关信息) {Long} approvalId 审核人ID
     * @apiSuccess (核销相关信息) {String} approvalName 审核人名称
     * @apiSuccess (核销相关信息) {String} documentNumber 核销单据编号
     * @apiSuccess (核销相关信息) {Long} documentApplicantId 核销单据申请人ID
     * @apiSuccess (核销相关信息) {String} documentApplicantName 核销单据申请人名称
     * @apiSuccess (核销相关信息) {ZonedDateTime} documentCreatedDate 单据创建日期
     * @apiSuccess (核销相关信息) {String} attachmentOid 附件OID
     * @apiSuccess (核销相关信息) {String} sourceDocumentNumber 被核销单据编号
     * @apiSuccess (核销相关信息) {Long} sourceDocumentLineId 被核销单据行ID
     * @apiSuccess (核销相关信息) {String} billCode 支付流水号
     * @apiSuccess (核销相关信息) {BigDecimal} reversedAmount 本次反冲金额 - 默认为可反冲金额
     * @apiSuccess (核销相关信息) {Long} sourceWriteOffId 反冲来源ID
     * @apiSuccess (核销相关信息) {String} currency 币种
     * @apiSuccess (核销相关信息) {String} isAccount 是否生成凭证
     * @apiSuccess (核销相关信息) {List} attachments 附件详情
     * @apiSuccess (核销相关信息) {object} cashWriteOffReserveExpReport 对公报账相关信息
     * @apiSuccess (核销相关信息) {object} cashWriteOffReservePrepaymentRequisition 预付款相关信息
     * @apiSuccess (核销相关信息) {List} cashWriteOffReverseHistory 核销反冲历史
     *
     * @apiSuccess (对公报账相关信息) {Long} id ID
     * @apiSuccess (对公报账相关信息) {String} currency 币种
     * @apiSuccess (对公报账相关信息) {BigDecimal} amount 金额
     * @apiSuccess (对公报账相关信息) {BigDecimal} functionalAmount 本币金额
     * @apiSuccess (对公报账相关信息) {String} description 备注
     * @apiSuccess (对公报账相关信息) {Long} payeeId 收款对象ID
     * @apiSuccess (对公报账相关信息) {String} payeeCode 收款对象code
     * @apiSuccess (对公报账相关信息) {String} payeeName 收款对象名称
     * @apiSuccess (对公报账相关信息) {ZonedDateTime} schedulePaymentDate 计划付款日期
     * @apiSuccess (对公报账相关信息) {String} contractHeaderNumber 合同编号
     * @apiSuccess (预付款相关信息) {Long} id ID
     * @apiSuccess (预付款相关信息) {String} currency 币种
     * @apiSuccess (预付款相关信息) {BigDecimal} amount 金额
     * @apiSuccess (预付款相关信息) {BigDecimal} functionalAmount 本币金额
     * @apiSuccess (预付款相关信息) {String} description 备注
     * @apiSuccess (预付款相关信息) {Long} partnerId 收款对象ID
     * @apiSuccess (预付款相关信息) {String} partnerCode 收款对象code
     * @apiSuccess (预付款相关信息) {String} partnerName 收款对象名称
     * @apiSuccess (预付款相关信息) {ZonedDateTime} requisitionPaymentDate 计划付款日期
     * @apiSuccess (预付款相关信息) {String} contractNumber 合同编号
     * @apiSuccessExample {json} 成功返回样例:
     *  [
         {
        "cshTransactionDetailId": "1002124828939739137",
        "writeOffAmount": 25,
        "documentType": "PUBLIC_REPORT",
        "documentHeaderId": "1002183650272083970",
        "documentLineId": "1002184479771197441",
        "writeOffDate": "2018-05-31T21:47:37+08:00",
        "periodName": "2018-5",
        "tenantId": "937506219191881730",
        "status": "Y",
        "statusDescription": null,
        "operationType": "WRITE_OFF",
        "setOfBooksId": "937515627984846850",
        "remark": null,
        "approvalOpinions": null,
        "approvalId": null,
        "approvalName": null,
        "documentNumber": "PR180500108",
        "documentApplicantId": 177601,
        "documentApplicantName": "清浅",
        "documentCreatedDate": null,
        "attachmentOid": null,
        "sourceDocumentNumber": "PREPAYMENT201805310099",
        "sourceDocumentLineId": 1002106933197271042,
        "billCode": "ZF20180531000256",
        "reversedAmount": 25,
        "cashWriteOffReserveExpReport": {
            "id": "1002184479771197441",
            "expReportHeaderId": "1002183650272083970",
            "scheduleLineNumber": 4,
            "companyId": "928",
            "description": "核销、付款",
            "currency": "CNY",
            "exchangeRate": 1,
            "amount": 300,
            "functionalAmount": 300,
            "schedulePaymentDate": "2018-05-30T16:00:00Z",
            "paymentMethod": "OFFLINE_PAYMENT",
            "cshTransactionClassId": "980707942246719490",
            "cashFlowItemId": null,
            "payeeCategory": "EMPLOYEE",
            "payeeId": "177601",
            "payeeCode": "GH0001",
            "contractHeaderId": "975931775027109889",
            "lastModifiedDate": "2018-05-31T13:47:01Z",
            "lastModifiedBy": 177601,
            "createdDate": "2018-05-31T13:46:14Z",
            "createdBy": 177601,
            "payeeName": "清浅",
            "contractHeaderNumber": "CON2018200300009"
        },
        "cashWriteOffReservePrepaymentRequisition": {
            "id": "1002106933197271042",
            "paymentRequisitionHeaderId": "1002106827282706433",
            "refDocumentId": "60719",
            "tenantId": "937506219191881730",
            "companyId": "928",
            "partnerCategory": "EMPLOYEE",
            "partnerId": "177601",
            "partnerCode": "GH0001",
            "requisitionPaymentDate": "2018-05-31T16:42:05+08:00",
            "paymentMethodCategory": "ONLINE_PAYMENT",
            "cshTransactionClassId": "958670974595686401",
            "cashFlowId": null,
            "cashFlowCode": null,
            "amount": 111,
            "currency": "CNY",
            "exchangeRate": 1,
            "functionAmount": 111,
            "description": "合同CON20180400024",
            "contractNumber": "CON2018200300009",
            "contractId": "975931775027109889",
            "partnerName": "清浅",
            "createdDate": "2018-05-31T16:38:06+08:00",
            "createdBy": 177601,
            "lastUpdatedDate": "2018-05-31T16:40:04+08:00",
            "lastUpdatedBy": 177601
        },
        "sourceWriteOffId": null,
        "currency": "CNY",
        "isAccount": "N",
        "cashWriteOffReverseHistory":[],
        "attachments":[]
        }
    ]

     */

    @ApiOperation(value = "待反冲核销记录查询", notes = "待反冲核销记录查询 开发：")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/wait/reserve/detail")
    public ResponseEntity<List<CashWriteOffReserveDTO>> getWaitingReserveWriteOffDetail(@ApiParam(value = "核销单据编号") @RequestParam(required = false) String documentNumber,
                                                                                        @ApiParam(value = "单据申请人ID") @RequestParam(required = false) Long applicantId,
                                                                                        @ApiParam(value = "被核销单据编号") @RequestParam(required = false) String sourceDocumentNumber,
                                                                                        @ApiParam(value = "支付流水号") @RequestParam(required = false) String billCode,
                                                                                        @ApiParam(value = "单据创建日期从") @RequestParam(required = false) String createdDateFrom,
                                                                                        @ApiParam(value = "单据创建日期至") @RequestParam(required = false) String createdDateTo,
                                                                                        @ApiParam(value = "核销金额从") @RequestParam(required = false) BigDecimal writeOffAmountFrom,
                                                                                        @ApiParam(value = "核销金额至") @RequestParam(required = false) BigDecimal writeOffAmountTo,
                                                                                        @ApiParam(value = "核销反冲日期从") @RequestParam(required = false) String writeOffDateFrom,
                                                                                        @ApiParam(value = "核销反冲日期至") @RequestParam(required = false) String writeOffDateTo,
                                                                                        @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashWriteOffReserveDTO> waitingReserveWriteOffDetail = cashWriteOffService.getWaitingReserveWriteOffDetail(documentNumber,
                applicantId,
                sourceDocumentNumber,
                billCode,
                createdDateFrom,
                createdDateTo,
                writeOffAmountFrom,
                writeOffAmountTo,
                writeOffDateFrom,
                writeOffDateTo,
                page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/payment/cash/write/off/wait/reserve/detail");
        return new ResponseEntity<>(waitingReserveWriteOffDetail,httpHeaders, HttpStatus.OK);
    }


    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/user/reserved/detail 【核销】我发起的反冲
     * @apiGroup PaymentService
     * @apiDescription 我发起的反冲
     * @apiParam (请求参数) {String} [documentNumber] 报账单编号
     * @apiParam (请求参数) {Long} [applicantId] 单据申请人ID
     * @apiParam (请求参数) {String} [sourceDocumentNumber] 被核销单据编号
     * @apiParam (请求参数) {String} [billCode] 支付流水号
     * @apiParam (请求参数) {String} [createdDateFrom] 单据创建日期从 YYYY-MM-DD
     * @apiParam (请求参数) {String} [createdDateTo] 单据创建日期至 YYYY-MM-DD
     * @apiParam (请求参数) {BigDecimal} [writeOffAmountFrom] 核销金额从
     * @apiParam (请求参数) {BigDecimal} [writeOffAmountTo] 核销金额至
     * @apiParam (请求参数) {String} [status] 核销状态
     * @apiParam (请求参数) {Long} [approvalId] 复核人ID
     * @apiParam (请求参数) {BigDecimal} [writeOffReverseAmountFrom] 核销反冲金额从
     * @apiParam (请求参数) {BigDecimal} [writeOffReverseAmountTo] 核销反冲金额至
     * @apiParam (请求参数) {String} [writeOffDateFrom] 核销反冲日期从
     * @apiParam (请求参数) {String} [writeOffDateTo] 核销反冲日期至
     * @apiParam (请求参数) {Integer} page 页数
     * @apiParam (请求参数) {Integer} size 每页大小
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/user/reserved/detail?page=1&size=10
     * @apiSuccess (核销相关信息) {Long} id 核销反冲ID
     * @apiSuccess (核销相关信息) {Long} cshTransactionDetailId 支付明细ID
     * @apiSuccess (核销相关信息) {BigDecimal} writeOffAmount 核销金额
     * @apiSuccess (核销相关信息) {String} documentType 单据类型
     * @apiSuccess (核销相关信息) {Long} documentHeaderId 核销单据头id
     * @apiSuccess (核销相关信息) {Long} documentLineId 核销单据行id
     * @apiSuccess (核销相关信息) {ZonedDateTime} writeOffDate 核销日期 | 反冲日期
     * @apiSuccess (核销相关信息) {String} periodName 期间
     * @apiSuccess (核销相关信息) {Long} tenantId 租户id
     * @apiSuccess (核销相关信息) {String} status 核销状态:N未生效;P已生效;Y:已核算 | 核销反冲状态:N拒绝;P已提交;Y:已审核
     * @apiSuccess (核销相关信息) {String} statusDescription 核销状态描述
     * @apiSuccess (核销相关信息) {String} operationType 操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
     * @apiSuccess (核销相关信息) {Long} setOfBooksId 账套ID
     * @apiSuccess (核销相关信息) {String} remark 备注
     * @apiSuccess (核销相关信息) {String} approvalOpinions 审核意见
     * @apiSuccess (核销相关信息) {Long} approvalId 审核人ID
     * @apiSuccess (核销相关信息) {String} approvalName 审核人名称
     * @apiSuccess (核销相关信息) {String} documentNumber 核销单据编号
     * @apiSuccess (核销相关信息) {Long} documentApplicantId 核销单据申请人ID
     * @apiSuccess (核销相关信息) {String} documentApplicantName 核销单据申请人名称
     * @apiSuccess (核销相关信息) {ZonedDateTime} documentCreatedDate 单据创建日期
     * @apiSuccess (核销相关信息) {String} attachmentOid 附件OID
     * @apiSuccess (核销相关信息) {String} sourceDocumentNumber 被核销单据编号
     * @apiSuccess (核销相关信息) {Long} sourceDocumentLineId 被核销单据行ID
     * @apiSuccess (核销相关信息) {String} billCode 支付流水号
     * @apiSuccess (核销相关信息) {BigDecimal} reversedAmount 本次反冲金额 - 默认为可反冲金额
     * @apiSuccess (核销相关信息) {Long} sourceWriteOffId 反冲来源ID
     * @apiSuccess (核销相关信息) {String} currency 币种
     * @apiSuccess (核销相关信息) {String} isAccount 是否生成凭证
     * @apiSuccess (核销相关信息) {Long} createdBy 创建人ID
     * @apiSuccess (核销相关信息) {String} createdCode 创建人代码
     * @apiSuccess (核销相关信息) {String} createdName 创建人名称
     * @apiSuccess (核销相关信息) {List} attachments 附件详情
     * @apiSuccess (核销相关信息) {object} cashWriteOffReserveExpReport 对公报账相关信息
     * @apiSuccess (核销相关信息) {object} cashWriteOffReservePrepaymentRequisition 预付款相关信息
     *
     * @apiSuccess (对公报账相关信息) {Long} id ID
     * @apiSuccess (对公报账相关信息) {String} currency 币种
     * @apiSuccess (对公报账相关信息) {BigDecimal} amount 金额
     * @apiSuccess (对公报账相关信息) {BigDecimal} functionalAmount 本币金额
     * @apiSuccess (对公报账相关信息) {String} description 备注
     * @apiSuccess (对公报账相关信息) {Long} payeeId 收款对象ID
     * @apiSuccess (对公报账相关信息) {String} payeeCode 收款对象code
     * @apiSuccess (对公报账相关信息) {String} payeeName 收款对象名称
     * @apiSuccess (对公报账相关信息) {ZonedDateTime} schedulePaymentDate 计划付款日期
     * @apiSuccess (对公报账相关信息) {String} contractHeaderNumber 合同编号
     *
     * @apiSuccess (预付款相关信息) {Long} id ID
     * @apiSuccess (预付款相关信息) {String} currency 币种
     * @apiSuccess (预付款相关信息) {BigDecimal} amount 金额
     * @apiSuccess (预付款相关信息) {BigDecimal} functionalAmount 本币金额
     * @apiSuccess (预付款相关信息) {String} description 备注
     * @apiSuccess (预付款相关信息) {Long} partnerId 收款对象ID
     * @apiSuccess (预付款相关信息) {String} partnerCode 收款对象code
     * @apiSuccess (预付款相关信息) {String} partnerName 收款对象名称
     * @apiSuccess (预付款相关信息) {ZonedDateTime} requisitionPaymentDate 计划付款日期
     * @apiSuccess (预付款相关信息) {String} contractNumber 合同编号
     * @apiSuccessExample {json} 成功返回样例:
     *  [
    {
    "cshTransactionDetailId": "1002124828939739137",
    "writeOffAmount": 25,
    "documentType": "PUBLIC_REPORT",
    "documentHeaderId": "1002183650272083970",
    "documentLineId": "1002184479771197441",
    "writeOffDate": "2018-05-31T21:47:37+08:00",
    "periodName": "2018-5",
    "tenantId": "937506219191881730",
    "status": "Y",
    "statusDescription": null,
    "operationType": "WRITE_OFF",
    "setOfBooksId": "937515627984846850",
    "remark": null,
    "approvalOpinions": null,
    "approvalId": null,
    "approvalName": null,
    "documentNumber": "PR180500108",
    "documentApplicantId": 177601,
    "documentApplicantName": "清浅",
    "documentCreatedDate": null,
    "attachmentOid": null,
    "sourceDocumentNumber": "PREPAYMENT201805310099",
    "sourceDocumentLineId": 1002106933197271042,
    "billCode": "ZF20180531000256",
    "reversedAmount": 25,
    "cashWriteOffReserveExpReport": {
        "id": "1002184479771197441",
        "expReportHeaderId": "1002183650272083970",
        "scheduleLineNumber": 4,
        "companyId": "928",
        "description": "核销、付款",
        "currency": "CNY",
        "exchangeRate": 1,
        "amount": 300,
        "functionalAmount": 300,
        "schedulePaymentDate": "2018-05-30T16:00:00Z",
        "paymentMethod": "OFFLINE_PAYMENT",
        "cshTransactionClassId": "980707942246719490",
        "cashFlowItemId": null,
        "payeeCategory": "EMPLOYEE",
        "payeeId": "177601",
        "payeeCode": "GH0001",
        "contractHeaderId": "975931775027109889",
        "lastModifiedDate": "2018-05-31T13:47:01Z",
        "lastModifiedBy": 177601,
        "createdDate": "2018-05-31T13:46:14Z",
        "createdBy": 177601,
        "payeeName": "清浅",
        "contractHeaderNumber": "CON2018200300009"
    },
    "cashWriteOffReservePrepaymentRequisition": {
        "id": "1002106933197271042",
        "paymentRequisitionHeaderId": "1002106827282706433",
        "refDocumentId": "60719",
        "tenantId": "937506219191881730",
        "companyId": "928",
        "partnerCategory": "EMPLOYEE",
        "partnerId": "177601",
        "partnerCode": "GH0001",
        "requisitionPaymentDate": "2018-05-31T16:42:05+08:00",
        "paymentMethodCategory": "ONLINE_PAYMENT",
        "cshTransactionClassId": "958670974595686401",
        "cashFlowId": null,
        "cashFlowCode": null,
        "amount": 111,
        "currency": "CNY",
        "exchangeRate": 1,
        "functionAmount": 111,
        "description": "合同CON20180400024",
        "contractNumber": "CON2018200300009",
        "contractId": "975931775027109889",
        "partnerName": "清浅",
        "createdDate": "2018-05-31T16:38:06+08:00",
        "createdBy": 177601,
        "lastUpdatedDate": "2018-05-31T16:40:04+08:00",
        "lastUpdatedBy": 177601
    },
    "sourceWriteOffId": null,
    "currency": "CNY",
    "isAccount": "N",
    "createdBy":1,
    "createdCode":"1",
    "createdName":"1",
    "attachments":[]
    }
    ]

     */

    @ApiOperation(value = "我发起的反冲", notes = "我发起的反冲 开发：")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/user/reserved/detail")
    public ResponseEntity<List<CashWriteOffReserveDTO>> getUserReservedWriteOffDetail(@ApiParam(value = "报账单编号") @RequestParam(required = false) String documentNumber,
                                                                                      @ApiParam(value = "单据申请人ID") @RequestParam(required = false) Long applicantId,
                                                                                      @ApiParam(value = "被核销单据编号") @RequestParam(required = false) String sourceDocumentNumber,
                                                                                      @ApiParam(value = "支付流水号") @RequestParam(required = false) String billCode,
                                                                                      @ApiParam(value = "单据创建日期从") @RequestParam(required = false) String createdDateFrom,
                                                                                      @ApiParam(value = "单据创建日期至") @RequestParam(required = false) String createdDateTo,
                                                                                      @ApiParam(value = "核销金额从") @RequestParam(required = false) BigDecimal writeOffAmountFrom,
                                                                                      @ApiParam(value = "核销金额至") @RequestParam(required = false) BigDecimal writeOffAmountTo,
                                                                                      @ApiParam(value = "核销状态") @RequestParam(required = false) String status,
                                                                                      @ApiParam(value = "审核人ID") @RequestParam(required = false) Long approvalId,
                                                                                      @ApiParam(value = "核销反冲金额从") @RequestParam(required = false) BigDecimal writeOffReverseAmountFrom,
                                                                                      @ApiParam(value = "核销反冲金额至") @RequestParam(required = false) BigDecimal writeOffReverseAmountTo,
                                                                                      @ApiParam(value = "核销日期从") @RequestParam(required = false) String writeOffDateFrom,
                                                                                      @ApiParam(value = "核销日期至") @RequestParam(required = false) String writeOffDateTo,
                                                                                      @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashWriteOffReserveDTO> userReservedWriteOffDetail = cashWriteOffService.getUserReservedWriteOffDetail(documentNumber,
                applicantId,
                sourceDocumentNumber,
                billCode,
                createdDateFrom,
                createdDateTo,
                writeOffAmountFrom,
                writeOffAmountTo,
                status,
                approvalId,
                writeOffReverseAmountFrom,
                writeOffReverseAmountTo,
                writeOffDateFrom,
                writeOffDateTo,
                page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/payment/cash/write/off/user/reserved/detail");
        return new ResponseEntity<>(userReservedWriteOffDetail,httpHeaders, HttpStatus.OK);
    }


    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/rechecking/reserve/detail 【核销】待复核反冲记录
     * @apiGroup PaymentService
     * @apiDescription 待复核反冲记录查询
     * @apiParam (请求参数) {String} [documentNumber] 报账单编号
     * @apiParam (请求参数) {Long} [applicantId] 单据申请人ID
     * @apiParam (请求参数) {String} [sourceDocumentNumber] 被核销单据编号
     * @apiParam (请求参数) {String} [billCode] 支付流水号
     * @apiParam (请求参数) {String} [createdDateFrom] 单据创建日期从 YYYY-MM-DD
     * @apiParam (请求参数) {String} [createdDateTo] 单据创建日期至 YYYY-MM-DD
     * @apiParam (请求参数) {BigDecimal} [writeOffAmountFrom] 核销金额从
     * @apiParam (请求参数) {BigDecimal} [writeOffAmountTo] 核销金额至
     * @apiParam (请求参数) {Long} [approvalId] 复核人ID
     * @apiParam (请求参数) {BigDecimal} [writeOffReverseAmountFrom] 核销反冲金额从
     * @apiParam (请求参数) {BigDecimal} [writeOffReverseAmountTo] 核销反冲金额至
     * @apiParam (请求参数) {String} [writeOffDateFrom] 核销日期从
     * @apiParam (请求参数) {String} [writeOffDateTo] 核销日期至
     * @apiParam (请求参数) {Long} [createdBy] 反冲发起人ID
     * @apiParam (请求参数) {Integer} page 页数
     * @apiParam (请求参数) {Integer} size 每页大小
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/rechecking/reserve/detail?page=1&size=10
     * @apiSuccess (核销相关信息) {Long} id 核销反冲ID
     * @apiSuccess (核销相关信息) {Long} cshTransactionDetailId 支付明细ID
     * @apiSuccess (核销相关信息) {BigDecimal} writeOffAmount 核销金额
     * @apiSuccess (核销相关信息) {String} documentType 单据类型
     * @apiSuccess (核销相关信息) {Long} documentHeaderId 核销单据头id
     * @apiSuccess (核销相关信息) {Long} documentLineId 核销单据行id
     * @apiSuccess (核销相关信息) {ZonedDateTime} writeOffDate 核销日期 | 反冲日期
     * @apiSuccess (核销相关信息) {String} periodName 期间
     * @apiSuccess (核销相关信息) {Long} tenantId 租户id
     * @apiSuccess (核销相关信息) {String} status 核销状态:N未生效;P已生效;Y:已核算 | 核销反冲状态:N拒绝;P已提交;Y:已审核
     * @apiSuccess (核销相关信息) {String} statusDescription 核销状态描述
     * @apiSuccess (核销相关信息) {String} operationType 操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
     * @apiSuccess (核销相关信息) {Long} setOfBooksId 账套ID
     * @apiSuccess (核销相关信息) {String} remark 备注
     * @apiSuccess (核销相关信息) {String} approvalOpinions 审核意见
     * @apiSuccess (核销相关信息) {Long} approvalId 审核人ID
     * @apiSuccess (核销相关信息) {String} approvalName 审核人名称
     * @apiSuccess (核销相关信息) {String} documentNumber 核销单据编号
     * @apiSuccess (核销相关信息) {Long} documentApplicantId 核销单据申请人ID
     * @apiSuccess (核销相关信息) {String} documentApplicantName 核销单据申请人名称
     * @apiSuccess (核销相关信息) {ZonedDateTime} documentCreatedDate 单据创建日期
     * @apiSuccess (核销相关信息) {String} attachmentOid 附件OID
     * @apiSuccess (核销相关信息) {String} sourceDocumentNumber 被核销单据编号
     * @apiSuccess (核销相关信息) {Long} sourceDocumentLineId 被核销单据行ID
     * @apiSuccess (核销相关信息) {String} billCode 支付流水号
     * @apiSuccess (核销相关信息) {BigDecimal} reversedAmount 本次反冲金额 - 默认为可反冲金额
     * @apiSuccess (核销相关信息) {Long} sourceWriteOffId 反冲来源ID
     * @apiSuccess (核销相关信息) {String} currency 币种
     * @apiSuccess (核销相关信息) {String} isAccount 是否生成凭证
     * @apiSuccess (核销相关信息) {Long} createdBy 创建人ID
     * @apiSuccess (核销相关信息) {String} createdCode 创建人代码
     * @apiSuccess (核销相关信息) {String} createdName 创建人名称
     * @apiSuccess (核销相关信息) {List} attachments 附件详情
     * @apiSuccess (核销相关信息) {object} cashWriteOffReserveExpReport 对公报账相关信息
     * @apiSuccess (核销相关信息) {object} cashWriteOffReservePrepaymentRequisition 预付款相关信息
     *
     * @apiSuccess (对公报账相关信息) {Long} id ID
     * @apiSuccess (对公报账相关信息) {String} currency 币种
     * @apiSuccess (对公报账相关信息) {BigDecimal} amount 金额
     * @apiSuccess (对公报账相关信息) {BigDecimal} functionalAmount 本币金额
     * @apiSuccess (对公报账相关信息) {String} description 备注
     * @apiSuccess (对公报账相关信息) {Long} payeeId 收款对象ID
     * @apiSuccess (对公报账相关信息) {String} payeeCode 收款对象code
     * @apiSuccess (对公报账相关信息) {String} payeeName 收款对象名称
     * @apiSuccess (对公报账相关信息) {ZonedDateTime} schedulePaymentDate 计划付款日期
     * @apiSuccess (对公报账相关信息) {String} contractHeaderNumber 合同编号
     *
     * @apiSuccess (预付款相关信息) {Long} id ID
     * @apiSuccess (预付款相关信息) {String} currency 币种
     * @apiSuccess (预付款相关信息) {BigDecimal} amount 金额
     * @apiSuccess (预付款相关信息) {BigDecimal} functionalAmount 本币金额
     * @apiSuccess (预付款相关信息) {String} description 备注
     * @apiSuccess (预付款相关信息) {Long} partnerId 收款对象ID
     * @apiSuccess (预付款相关信息) {String} partnerCode 收款对象code
     * @apiSuccess (预付款相关信息) {String} partnerName 收款对象名称
     * @apiSuccess (预付款相关信息) {ZonedDateTime} requisitionPaymentDate 计划付款日期
     * @apiSuccess (预付款相关信息) {String} contractNumber 合同编号
     * @apiSuccessExample {json} 成功返回样例:
     *  [
    {
    "cshTransactionDetailId": "1002124828939739137",
    "writeOffAmount": 25,
    "documentType": "PUBLIC_REPORT",
    "documentHeaderId": "1002183650272083970",
    "documentLineId": "1002184479771197441",
    "writeOffDate": "2018-05-31T21:47:37+08:00",
    "periodName": "2018-5",
    "tenantId": "937506219191881730",
    "status": "Y",
    "statusDescription": null,
    "operationType": "WRITE_OFF",
    "setOfBooksId": "937515627984846850",
    "remark": null,
    "approvalOpinions": null,
    "approvalId": null,
    "approvalName": null,
    "documentNumber": "PR180500108",
    "documentApplicantId": 177601,
    "documentApplicantName": "清浅",
    "documentCreatedDate": null,
    "attachmentOid": null,
    "sourceDocumentNumber": "PREPAYMENT201805310099",
    "sourceDocumentLineId": 1002106933197271042,
    "billCode": "ZF20180531000256",
    "reversedAmount": 25,
    "cashWriteOffReserveExpReport": {
        "id": "1002184479771197441",
        "expReportHeaderId": "1002183650272083970",
        "scheduleLineNumber": 4,
        "companyId": "928",
        "description": "核销、付款",
        "currency": "CNY",
        "exchangeRate": 1,
        "amount": 300,
        "functionalAmount": 300,
        "schedulePaymentDate": "2018-05-30T16:00:00Z",
        "paymentMethod": "OFFLINE_PAYMENT",
        "cshTransactionClassId": "980707942246719490",
        "cashFlowItemId": null,
        "payeeCategory": "EMPLOYEE",
        "payeeId": "177601",
        "payeeCode": "GH0001",
        "contractHeaderId": "975931775027109889",
        "lastModifiedDate": "2018-05-31T13:47:01Z",
        "lastModifiedBy": 177601,
        "createdDate": "2018-05-31T13:46:14Z",
        "createdBy": 177601,
        "payeeName": "清浅",
        "contractHeaderNumber": "CON2018200300009"
    },
    "cashWriteOffReservePrepaymentRequisition": {
        "id": "1002106933197271042",
        "paymentRequisitionHeaderId": "1002106827282706433",
        "refDocumentId": "60719",
        "tenantId": "937506219191881730",
        "companyId": "928",
        "partnerCategory": "EMPLOYEE",
        "partnerId": "177601",
        "partnerCode": "GH0001",
        "requisitionPaymentDate": "2018-05-31T16:42:05+08:00",
        "paymentMethodCategory": "ONLINE_PAYMENT",
        "cshTransactionClassId": "958670974595686401",
        "cashFlowId": null,
        "cashFlowCode": null,
        "amount": 111,
        "currency": "CNY",
        "exchangeRate": 1,
        "functionAmount": 111,
        "description": "合同CON20180400024",
        "contractNumber": "CON2018200300009",
        "contractId": "975931775027109889",
        "partnerName": "清浅",
        "createdDate": "2018-05-31T16:38:06+08:00",
        "createdBy": 177601,
        "lastUpdatedDate": "2018-05-31T16:40:04+08:00",
        "lastUpdatedBy": 177601
    },
    "sourceWriteOffId": null,
    "currency": "CNY",
    "isAccount": "N",
    "createdBy":1,
    "createdCode":"1",
    "createdName":"1",
    "attachments":[]
    }
    ]

     */

    @ApiOperation(value = "待复核反冲记录查询", notes = "待复核反冲记录查询 开发：")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/rechecking/reserve/detail")
    public ResponseEntity<List<CashWriteOffReserveDTO>> getRecheckingReservedWriteOffDetail(@ApiParam(value = "报账单编号") @RequestParam(required = false) String documentNumber,
                                                                                            @ApiParam(value = "单据申请人ID") @RequestParam(required = false) Long applicantId,
                                                                                            @ApiParam(value = "被核销单据编号") @RequestParam(required = false) String sourceDocumentNumber,
                                                                                            @ApiParam(value = "支付流水号") @RequestParam(required = false) String billCode,
                                                                                            @ApiParam(value = "单据创建日期从") @RequestParam(required = false) String createdDateFrom,
                                                                                            @ApiParam(value = "单据创建日期至") @RequestParam(required = false) String createdDateTo,
                                                                                            @ApiParam(value = "核销金额从") @RequestParam(required = false) BigDecimal writeOffAmountFrom,
                                                                                            @ApiParam(value = "核销金额至") @RequestParam(required = false) BigDecimal writeOffAmountTo,
                                                                                            @ApiParam(value = "审核人ID") @RequestParam(required = false) Long approvalId,
                                                                                            @ApiParam(value = "核销反冲金额从") @RequestParam(required = false) BigDecimal writeOffReverseAmountFrom,
                                                                                            @ApiParam(value = "核销反冲金额至") @RequestParam(required = false) BigDecimal writeOffReverseAmountTo,
                                                                                            @ApiParam(value = "核销日期从") @RequestParam(required = false) String writeOffDateFrom,
                                                                                            @ApiParam(value = "核销日期至") @RequestParam(required = false) String writeOffDateTo,
                                                                                            @ApiParam(value = "创建人") @RequestParam(required = false) Long createdBy,
                                                                                            @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashWriteOffReserveDTO> recheckingReservedWriteOffDetail = cashWriteOffService.getRecheckReservedWriteOffDetail(documentNumber,
                applicantId,
                sourceDocumentNumber,
                billCode,
                createdDateFrom,
                createdDateTo,
                writeOffAmountFrom,
                writeOffAmountTo,
                "P",
                approvalId,
                writeOffReverseAmountFrom,
                writeOffReverseAmountTo,
                writeOffDateFrom,
                writeOffDateTo,
                createdBy,
                page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/payment/cash/write/off/rechecking/reserve/detail");
        return new ResponseEntity<>(recheckingReservedWriteOffDetail,httpHeaders, HttpStatus.OK);
    }


    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/rechecked/reserve/detail 【核销】已复核反冲记录
     * @apiGroup PaymentService
     * @apiDescription 已复核反冲记录查询
     * @apiParam (请求参数) {String} [documentNumber] 报账单编号
     * @apiParam (请求参数) {Long} [applicantId] 单据申请人ID
     * @apiParam (请求参数) {String} [sourceDocumentNumber] 被核销单据编号
     * @apiParam (请求参数) {String} [billCode] 支付流水号
     * @apiParam (请求参数) {String} [createdDateFrom] 单据创建日期从 YYYY-MM-DD
     * @apiParam (请求参数) {String} [createdDateTo] 单据创建日期至 YYYY-MM-DD
     * @apiParam (请求参数) {BigDecimal} [writeOffAmountFrom] 核销金额从
     * @apiParam (请求参数) {BigDecimal} [writeOffAmountTo] 核销金额至
     * @apiParam (请求参数) {Long} [approvalId] 复核人ID
     * @apiParam (请求参数) {BigDecimal} [writeOffReverseAmountFrom] 核销反冲金额从
     * @apiParam (请求参数) {BigDecimal} [writeOffReverseAmountTo] 核销反冲金额至
     * @apiParam (请求参数) {String} [writeOffDateFrom] 核销反冲日期从
     * @apiParam (请求参数) {String} [writeOffDateTo] 核销反冲日期至
     * @apiParam (请求参数) {Long} [createdBy] 反冲发起人ID
     * @apiParam (请求参数) {Integer} page 页数
     * @apiParam (请求参数) {Integer} size 每页大小
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/rechecked/reserve/detail?page=1&size=10
     * @apiSuccess (核销相关信息) {Long} id 核销反冲ID
     * @apiSuccess (核销相关信息) {Long} cshTransactionDetailId 支付明细ID
     * @apiSuccess (核销相关信息) {BigDecimal} writeOffAmount 核销金额
     * @apiSuccess (核销相关信息) {String} documentType 单据类型
     * @apiSuccess (核销相关信息) {Long} documentHeaderId 核销单据头id
     * @apiSuccess (核销相关信息) {Long} documentLineId 核销单据行id
     * @apiSuccess (核销相关信息) {ZonedDateTime} writeOffDate 核销日期 | 反冲日期
     * @apiSuccess (核销相关信息) {String} periodName 期间
     * @apiSuccess (核销相关信息) {Long} tenantId 租户id
     * @apiSuccess (核销相关信息) {String} status 核销状态:N未生效;P已生效;Y:已核算 | 核销反冲状态:N拒绝;P已提交;Y:已审核
     * @apiSuccess (核销相关信息) {String} statusDescription 核销状态描述
     * @apiSuccess (核销相关信息) {String} operationType 操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
     * @apiSuccess (核销相关信息) {Long} setOfBooksId 账套ID
     * @apiSuccess (核销相关信息) {String} remark 备注
     * @apiSuccess (核销相关信息) {String} approvalOpinions 审核意见
     * @apiSuccess (核销相关信息) {Long} approvalId 审核人ID
     * @apiSuccess (核销相关信息) {String} approvalName 审核人名称
     * @apiSuccess (核销相关信息) {String} documentNumber 核销单据编号
     * @apiSuccess (核销相关信息) {Long} documentApplicantId 核销单据申请人ID
     * @apiSuccess (核销相关信息) {String} documentApplicantName 核销单据申请人名称
     * @apiSuccess (核销相关信息) {ZonedDateTime} documentCreatedDate 单据创建日期
     * @apiSuccess (核销相关信息) {String} attachmentOid 附件OID
     * @apiSuccess (核销相关信息) {String} sourceDocumentNumber 被核销单据编号
     * @apiSuccess (核销相关信息) {Long} sourceDocumentLineId 被核销单据行ID
     * @apiSuccess (核销相关信息) {String} billCode 支付流水号
     * @apiSuccess (核销相关信息) {BigDecimal} reversedAmount 本次反冲金额 - 默认为可反冲金额
     * @apiSuccess (核销相关信息) {Long} sourceWriteOffId 反冲来源ID
     * @apiSuccess (核销相关信息) {String} currency 币种
     * @apiSuccess (核销相关信息) {String} isAccount 是否生成凭证
     * @apiSuccess (核销相关信息) {Long} createdBy 创建人ID
     * @apiSuccess (核销相关信息) {String} createdCode 创建人代码
     * @apiSuccess (核销相关信息) {String} createdName 创建人名称
     * @apiSuccess (核销相关信息) {List} attachments 附件详情
     * @apiSuccess (核销相关信息) {object} cashWriteOffReserveExpReport 对公报账相关信息
     * @apiSuccess (核销相关信息) {object} cashWriteOffReservePrepaymentRequisition 预付款相关信息
     *
     * @apiSuccess (对公报账相关信息) {Long} id ID
     * @apiSuccess (对公报账相关信息) {String} currency 币种
     * @apiSuccess (对公报账相关信息) {BigDecimal} amount 金额
     * @apiSuccess (对公报账相关信息) {BigDecimal} functionalAmount 本币金额
     * @apiSuccess (对公报账相关信息) {String} description 备注
     * @apiSuccess (对公报账相关信息) {Long} payeeId 收款对象ID
     * @apiSuccess (对公报账相关信息) {String} payeeCode 收款对象code
     * @apiSuccess (对公报账相关信息) {String} payeeName 收款对象名称
     * @apiSuccess (对公报账相关信息) {ZonedDateTime} schedulePaymentDate 计划付款日期
     * @apiSuccess (对公报账相关信息) {String} contractHeaderNumber 合同编号
     *
     * @apiSuccess (预付款相关信息) {Long} id ID
     * @apiSuccess (预付款相关信息) {String} currency 币种
     * @apiSuccess (预付款相关信息) {Double} amount 金额
     * @apiSuccess (预付款相关信息) {Double} functionalAmount 本币金额
     * @apiSuccess (预付款相关信息) {String} description 备注
     * @apiSuccess (预付款相关信息) {Long} partnerId 收款对象ID
     * @apiSuccess (预付款相关信息) {String} partnerCode 收款对象code
     * @apiSuccess (预付款相关信息) {String} partnerName 收款对象名称
     * @apiSuccess (预付款相关信息) {ZonedDateTime} requisitionPaymentDate 计划付款日期
     * @apiSuccess (预付款相关信息) {String} contractNumber 合同编号
     * @apiSuccessExample {json} 成功返回样例:
     *  [
    {
    "cshTransactionDetailId": "1002124828939739137",
    "writeOffAmount": 25,
    "documentType": "PUBLIC_REPORT",
    "documentHeaderId": "1002183650272083970",
    "documentLineId": "1002184479771197441",
    "writeOffDate": "2018-05-31T21:47:37+08:00",
    "periodName": "2018-5",
    "tenantId": "937506219191881730",
    "status": "Y",
    "statusDescription": null,
    "operationType": "WRITE_OFF",
    "setOfBooksId": "937515627984846850",
    "remark": null,
    "approvalOpinions": null,
    "approvalId": null,
    "approvalName": null,
    "documentNumber": "PR180500108",
    "documentApplicantId": 177601,
    "documentApplicantName": "清浅",
    "documentCreatedDate": null,
    "attachmentOid": null,
    "sourceDocumentNumber": "PREPAYMENT201805310099",
    "sourceDocumentLineId": 1002106933197271042,
    "billCode": "ZF20180531000256",
    "reversedAmount": 25,
    "cashWriteOffReserveExpReport": {
        "id": "1002184479771197441",
        "expReportHeaderId": "1002183650272083970",
        "scheduleLineNumber": 4,
        "companyId": "928",
        "description": "核销、付款",
        "currency": "CNY",
        "exchangeRate": 1,
        "amount": 300,
        "functionalAmount": 300,
        "schedulePaymentDate": "2018-05-30T16:00:00Z",
        "paymentMethod": "OFFLINE_PAYMENT",
        "cshTransactionClassId": "980707942246719490",
        "cashFlowItemId": null,
        "payeeCategory": "EMPLOYEE",
        "payeeId": "177601",
        "payeeCode": "GH0001",
        "contractHeaderId": "975931775027109889",
        "lastModifiedDate": "2018-05-31T13:47:01Z",
        "lastModifiedBy": 177601,
        "createdDate": "2018-05-31T13:46:14Z",
        "createdBy": 177601,
        "payeeName": "清浅",
        "contractHeaderNumber": "CON2018200300009"
    },
    "cashWriteOffReservePrepaymentRequisition": {
        "id": "1002106933197271042",
        "paymentRequisitionHeaderId": "1002106827282706433",
        "refDocumentId": "60719",
        "tenantId": "937506219191881730",
        "companyId": "928",
        "partnerCategory": "EMPLOYEE",
        "partnerId": "177601",
        "partnerCode": "GH0001",
        "requisitionPaymentDate": "2018-05-31T16:42:05+08:00",
        "paymentMethodCategory": "ONLINE_PAYMENT",
        "cshTransactionClassId": "958670974595686401",
        "cashFlowId": null,
        "cashFlowCode": null,
        "amount": 111,
        "currency": "CNY",
        "exchangeRate": 1,
        "functionAmount": 111,
        "description": "合同CON20180400024",
        "contractNumber": "CON2018200300009",
        "contractId": "975931775027109889",
        "partnerName": "清浅",
        "createdDate": "2018-05-31T16:38:06+08:00",
        "createdBy": 177601,
        "lastUpdatedDate": "2018-05-31T16:40:04+08:00",
        "lastUpdatedBy": 177601
    },
    "sourceWriteOffId": null,
    "currency": "CNY",
    "isAccount": "N",
    "createdBy":1,
    "createdCode":"1",
    "createdName":"1",
    "attachments":[]
    }
    ]

     */


    @ApiOperation(value = "已复核反冲记录查询", notes = "已复核反冲记录查询 开发：")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/rechecked/reserve/detail")
    public ResponseEntity<List<CashWriteOffReserveDTO>> getRecheckedReservedWriteOffDetail(@ApiParam(value = "报账单编号") @RequestParam(required = false) String documentNumber,
                                                                                           @ApiParam(value = "单据申请人ID") @RequestParam(required = false) Long applicantId,
                                                                                           @ApiParam(value = "被核销单据编号") @RequestParam(required = false) String sourceDocumentNumber,
                                                                                           @ApiParam(value = "支付流水号") @RequestParam(required = false) String billCode,
                                                                                           @ApiParam(value = "单据创建日期从") @RequestParam(required = false) String createdDateFrom,
                                                                                           @ApiParam(value = "单据创建日期至") @RequestParam(required = false) String createdDateTo,
                                                                                           @ApiParam(value = "核销金额从") @RequestParam(required = false) BigDecimal writeOffAmountFrom,
                                                                                           @ApiParam(value = "核销金额至") @RequestParam(required = false) BigDecimal writeOffAmountTo,
                                                                                           @ApiParam(value = "审核人ID") @RequestParam(required = false) Long approvalId,
                                                                                           @ApiParam(value = "核销反冲金额从") @RequestParam(required = false) BigDecimal writeOffReverseAmountFrom,
                                                                                           @ApiParam(value = "核销反冲金额至") @RequestParam(required = false) BigDecimal writeOffReverseAmountTo,
                                                                                           @ApiParam(value = "核销日期从") @RequestParam(required = false) String writeOffDateFrom,
                                                                                           @ApiParam(value = "核销日期至") @RequestParam(required = false) String writeOffDateTo,
                                                                                           @ApiParam(value = "创建人") @RequestParam(required = false) Long createdBy,
                                                                                           @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashWriteOffReserveDTO> recheckedReservedWriteOffDetail = cashWriteOffService.getRecheckReservedWriteOffDetail(documentNumber,
                applicantId,
                sourceDocumentNumber,
                billCode,
                createdDateFrom,
                createdDateTo,
                writeOffAmountFrom,
                writeOffAmountTo,
                "Y",
                approvalId,
                writeOffReverseAmountFrom,
                writeOffReverseAmountTo,
                writeOffDateFrom,
                writeOffDateTo,
                createdBy,
                page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/payment/cash/write/off/rechecked/reserve/detail");
        return new ResponseEntity<>(recheckedReservedWriteOffDetail,httpHeaders, HttpStatus.OK);
    }


    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/rechecked/reserve/detail 【核销】核销反冲历史
     * @apiGroup PaymentService
     * @apiDescription 核销反冲历史记录查询
     * @apiParam (请求参数) {Long} sourceWriteOffId 核销记录ID
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/reserved/history/detail?sourceWriteOffId=1
     * @apiSuccess (核销相关信息) {Long} id 核销反冲ID
     * @apiSuccess (核销相关信息) {Long} cshTransactionDetailId 支付明细ID
     * @apiSuccess (核销相关信息) {BigDecimal} writeOffAmount 核销金额
     * @apiSuccess (核销相关信息) {String} documentType 单据类型
     * @apiSuccess (核销相关信息) {Long} documentHeaderId 核销单据头id
     * @apiSuccess (核销相关信息) {Long} documentLineId 核销单据行id
     * @apiSuccess (核销相关信息) {ZonedDateTime} writeOffDate 核销日期 | 反冲日期
     * @apiSuccess (核销相关信息) {String} periodName 期间
     * @apiSuccess (核销相关信息) {Long} tenantId 租户id
     * @apiSuccess (核销相关信息) {String} status 核销状态:N未生效;P已生效;Y:已核算 | 核销反冲状态:N拒绝;P已提交;Y:已审核
     * @apiSuccess (核销相关信息) {String} statusDescription 核销状态描述
     * @apiSuccess (核销相关信息) {String} operationType 操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
     * @apiSuccess (核销相关信息) {Long} setOfBooksId 账套ID
     * @apiSuccess (核销相关信息) {String} remark 备注
     * @apiSuccess (核销相关信息) {String} approvalOpinions 审核意见
     * @apiSuccess (核销相关信息) {Long} approvalId 审核人ID
     * @apiSuccess (核销相关信息) {String} approvalName 审核人名称
     * @apiSuccess (核销相关信息) {String} documentNumber 核销单据编号
     * @apiSuccess (核销相关信息) {Long} documentApplicantId 核销单据申请人ID
     * @apiSuccess (核销相关信息) {String} documentApplicantName 核销单据申请人名称
     * @apiSuccess (核销相关信息) {ZonedDateTime} documentCreatedDate 单据创建日期
     * @apiSuccess (核销相关信息) {String} attachmentOid 附件OID
     * @apiSuccess (核销相关信息) {String} sourceDocumentNumber 被核销单据编号
     * @apiSuccess (核销相关信息) {Long} sourceDocumentLineId 被核销单据行ID
     * @apiSuccess (核销相关信息) {String} billCode 支付流水号
     * @apiSuccess (核销相关信息) {BigDecimal} reversedAmount 本次反冲金额 - 默认为可反冲金额
     * @apiSuccess (核销相关信息) {Long} sourceWriteOffId 反冲来源ID
     * @apiSuccess (核销相关信息) {String} currency 币种
     * @apiSuccess (核销相关信息) {String} isAccount 是否生成凭证
     * @apiSuccess (核销相关信息) {Long} createdBy 创建人ID
     * @apiSuccess (核销相关信息) {String} createdCode 创建人代码
     * @apiSuccess (核销相关信息) {String} createdName 创建人名称
     * @apiSuccess (核销相关信息) {List} attachments 附件详情
     * @apiSuccess (核销相关信息) {object} cashWriteOffReserveExpReport 对公报账相关信息
     * @apiSuccess (核销相关信息) {object} cashWriteOffReservePrepaymentRequisition 预付款相关信息
     *
     * @apiSuccess (对公报账相关信息) {Long} id ID
     * @apiSuccess (对公报账相关信息) {String} currency 币种
     * @apiSuccess (对公报账相关信息) {BigDecimal} amount 金额
     * @apiSuccess (对公报账相关信息) {BigDecimal} functionalAmount 本币金额
     * @apiSuccess (对公报账相关信息) {String} description 备注
     * @apiSuccess (对公报账相关信息) {Long} payeeId 收款对象ID
     * @apiSuccess (对公报账相关信息) {String} payeeCode 收款对象code
     * @apiSuccess (对公报账相关信息) {String} payeeName 收款对象名称
     * @apiSuccess (对公报账相关信息) {ZonedDateTime} schedulePaymentDate 计划付款日期
     * @apiSuccess (对公报账相关信息) {String} contractHeaderNumber 合同编号
     *
     * @apiSuccess (预付款相关信息) {Long} id ID
     * @apiSuccess (预付款相关信息) {String} currency 币种
     * @apiSuccess (预付款相关信息) {BigDecimal} amount 金额
     * @apiSuccess (预付款相关信息) {BigDecimal} functionalAmount 本币金额
     * @apiSuccess (预付款相关信息) {String} description 备注
     * @apiSuccess (预付款相关信息) {Long} partnerId 收款对象ID
     * @apiSuccess (预付款相关信息) {String} partnerCode 收款对象code
     * @apiSuccess (预付款相关信息) {String} partnerName 收款对象名称
     * @apiSuccess (预付款相关信息) {ZonedDateTime} requisitionPaymentDate 计划付款日期
     * @apiSuccess (预付款相关信息) {String} contractNumber 合同编号
     * @apiSuccessExample {json} 成功返回样例:
     *  [
    {
    "cshTransactionDetailId": "1002124828939739137",
    "writeOffAmount": 25,
    "documentType": "PUBLIC_REPORT",
    "documentHeaderId": "1002183650272083970",
    "documentLineId": "1002184479771197441",
    "writeOffDate": "2018-05-31T21:47:37+08:00",
    "periodName": "2018-5",
    "tenantId": "937506219191881730",
    "status": "Y",
    "statusDescription": null,
    "operationType": "WRITE_OFF",
    "setOfBooksId": "937515627984846850",
    "remark": null,
    "approvalOpinions": null,
    "approvalId": null,
    "approvalName": null,
    "documentNumber": "PR180500108",
    "documentApplicantId": 177601,
    "documentApplicantName": "清浅",
    "documentCreatedDate": null,
    "attachmentOid": null,
    "sourceDocumentNumber": "PREPAYMENT201805310099",
    "sourceDocumentLineId": 1002106933197271042,
    "billCode": "ZF20180531000256",
    "reversedAmount": 25,
    "cashWriteOffReserveExpReport": {
        "id": "1002184479771197441",
        "expReportHeaderId": "1002183650272083970",
        "scheduleLineNumber": 4,
        "companyId": "928",
        "description": "核销、付款",
        "currency": "CNY",
        "exchangeRate": 1,
        "amount": 300,
        "functionalAmount": 300,
        "schedulePaymentDate": "2018-05-30T16:00:00Z",
        "paymentMethod": "OFFLINE_PAYMENT",
        "cshTransactionClassId": "980707942246719490",
        "cashFlowItemId": null,
        "payeeCategory": "EMPLOYEE",
        "payeeId": "177601",
        "payeeCode": "GH0001",
        "contractHeaderId": "975931775027109889",
        "lastModifiedDate": "2018-05-31T13:47:01Z",
        "lastModifiedBy": 177601,
        "createdDate": "2018-05-31T13:46:14Z",
        "createdBy": 177601,
        "payeeName": "清浅",
        "contractHeaderNumber": "CON2018200300009"
    },
    "cashWriteOffReservePrepaymentRequisition": {
        "id": "1002106933197271042",
        "paymentRequisitionHeaderId": "1002106827282706433",
        "refDocumentId": "60719",
        "tenantId": "937506219191881730",
        "companyId": "928",
        "partnerCategory": "EMPLOYEE",
        "partnerId": "177601",
        "partnerCode": "GH0001",
        "requisitionPaymentDate": "2018-05-31T16:42:05+08:00",
        "paymentMethodCategory": "ONLINE_PAYMENT",
        "cshTransactionClassId": "958670974595686401",
        "cashFlowId": null,
        "cashFlowCode": null,
        "amount": 111,
        "currency": "CNY",
        "exchangeRate": 1,
        "functionAmount": 111,
        "description": "合同CON20180400024",
        "contractNumber": "CON2018200300009",
        "contractId": "975931775027109889",
        "partnerName": "清浅",
        "createdDate": "2018-05-31T16:38:06+08:00",
        "createdBy": 177601,
        "lastUpdatedDate": "2018-05-31T16:40:04+08:00",
        "lastUpdatedBy": 177601
    },
    "sourceWriteOffId": 1,
    "currency": "CNY",
    "isAccount": "N",
    "createdBy":1,
    "createdCode":"1",
    "createdName":"1",
    "attachments":[]
    }
    ]

     */

    @ApiOperation(value = "核销反冲历史记录查询", notes = "核销反冲历史记录查询 开发：")
    @GetMapping("/reserved/history/detail")
    public ResponseEntity<List<CashWriteOffReserveDTO>> getCashWriteOffReverseHistory(@ApiParam(value = "反冲来源ID") @RequestParam Long sourceWriteOffId) throws URISyntaxException {
        List<CashWriteOffReserveDTO> cashWriteOffReverseHistory = cashWriteOffService.getCashWriteOffReverseHistory(sourceWriteOffId);
        int size = cashWriteOffReverseHistory.size();
        Page page = PageUtil.getPage(1, size);
        page.setTotal(size);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/payment/cash/write/off/reserved/history/detail");
        return new ResponseEntity(cashWriteOffReverseHistory,httpHeaders,HttpStatus.OK);
    }

    /**
     * @api {POST} {{payment-service_url}}/api/payment/cash/write/off/do/reserved 【核销】发起核销反冲
     * @apiGroup PaymentService
     * @apiDescription 发起核销反冲
     * @apiParam (请求参数) {Long} id 核销记录ID
     * @apiParam (请求参数) {BigDecimal} reverseAmount 反冲金额
     * @apiParam (请求参数) {String} [remark] 备注
     * @apiParam (请求参数) {String} [attachmentOid] 附件OID(多个附件，用逗号分割)
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/do/reserved?id=1&reverseAmount=20
     */

    @ApiOperation(value = "发起核销反冲", notes = "发起核销反冲 开发：")
    @PostMapping("/do/reserved")
    public ResponseEntity doCashWriteOffReverse(@ApiParam(value = "id") @RequestParam Long id,
                                                @ApiParam(value = "反冲金额") @RequestParam BigDecimal reverseAmount,
                                                @ApiParam(value = "备注") @RequestParam(required = false) String remark,
                                                @ApiParam(value = "附件OID") @RequestParam(required = false) String attachmentOid) {
        cashWriteOffService.doCashWriteOffReverse(id,reverseAmount,remark,attachmentOid);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {DELETE} {{payment-service_url}}/api/payment/cash/write/off/reserved/cancel 【核销】核销反冲作废
     * @apiGroup PaymentService
     * @apiDescription 核销反冲作废
     * @apiParam (请求参数) {Long} id 核销反冲ID
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/reserved/cancel?id=1
     */

    @ApiOperation(value = "核销反冲作废", notes = "核销反冲作废 开发：")
    @DeleteMapping("/reserved/cancel")
    public ResponseEntity cancelCashWriteOffReverse(@ApiParam(value = "id") @RequestParam Long id){
        cashWriteOffService.cancelCashWriteOffReverse(id);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} {{payment-service_url}}/api/payment/cash/write/off/do/reserved/again 【核销】重新发起反冲
     * @apiGroup PaymentService
     * @apiDescription 重新发起核销反冲
     * @apiParam (请求参数) {Long} id 核销记录ID
     * @apiParam (请求参数) {BigDecimal} reverseAmount 反冲金额
     * @apiParam (请求参数) {String} [remark] 备注
     * @apiParam (请求参数) {String} [attachmentOid] 附件OID(多个附件，用逗号分割)
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/do/reserved/again?id=1&reverseAmount=20
     */

    @ApiOperation(value = "重新发起核销反冲", notes = "重新发起核销反冲 开发：")
    @PostMapping("/do/reserved/again")
    public ResponseEntity doCashWriteOffReverseAgain(@ApiParam(value = "id") @RequestParam Long id,
                                                     @ApiParam(value = "反冲金额") @RequestParam BigDecimal reverseAmount,
                                                     @ApiParam(value = "备注") @RequestParam(required = false) String remark,
                                                     @ApiParam(value = "附件OID") @RequestParam(required = false) String attachmentOid){
        cashWriteOffService.doCashWriteOffReverseAgain(id,reverseAmount,remark,attachmentOid);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {POST} {{payment-service_url}}/api/payment/cash/write/off/operation/reserved 【核销】核销反冲复核
     * @apiGroup PaymentService
     * @apiDescription 核销反冲复核
     * @apiParam (请求参数) {Long} id 核销反冲ID
     * @apiParam (请求参数) {Integer} operationType 操作类型 1:通过 -1:拒绝
     * @apiParam (请求参数) {String} approvalOpinions 复核意见
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/operation/reserved?id=1&operationType=1&approvalOpinions=cs
     */

    @ApiOperation(value = "核销反冲复核", notes = "核销反冲复核 开发：")
    @PostMapping("/operation/reserved")
    public ResponseEntity cashWriteOffReverseRecheck(@ApiParam(value = "id") @RequestParam Long id,
                                                     @ApiParam(value = "操作类型") @RequestParam Integer operationType,
                                                     @ApiParam(value = "审核意见") @RequestParam String approvalOpinions){
        cashWriteOffService.cashWriteOffReverseRecheck(id,operationType,approvalOpinions);
        return ResponseEntity.ok().build();
    }

    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/judge/post/accounting/service 【核销】是否核算
     * @apiGroup PaymentService
     * @apiDescription 根据登录人信息判断是否需要生成凭证
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/judge/post/accounting/service
     *
     * @apiSuccessExample {json} 成功返回样例:
     * true
     */

    @ApiOperation(value = "是否核算", notes = "是否核算 开发：")
    @GetMapping("/judge/post/accounting/service")
    public ResponseEntity<Boolean> judgePostAccountingService(){
        return ResponseEntity.ok(cashWriteOffService.judgePostAccountingService(OrgInformationUtil.getCurrentCompanyOid()));
    }

    /**
     * @api {POST} {{payment-service_url}}/api/payment/cash/write/off/document/approve 【核销】单据审核
     * @apiGroup PaymentService
     * @apiDescription 单据审核，对应修改核销记录的状态
     *
     * @apiParam (请求参数) {String} documentType 单据类型
     * @apiParam (请求参数) {Long} documentHeaderId 单据ID
     * @apiParam (请求参数) {Long} tenantId 租户ID
     * @apiParam (请求参数) {Long} operatorId 操作人ID
     * @apiParam (请求参数) {Integer} operationType 操作类型 1:通过 -1:拒绝
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/document/approve?documentType=PUBLIC_REPORT&documentHeaderId=1&tenantId=1&operatorId=1&operationType=1
     */

    @ApiOperation(value = "单据审核修改核销记录状态", notes = "单据审核修改核销记录状态 开发：")
    @PostMapping("/document/approve")
    public ResponseEntity<String> auditChangeWriteOffStatus(@ApiParam(value = "单据类型") @RequestParam String documentType,
                                                            @ApiParam(value = "单据头id") @RequestParam Long documentHeaderId,
                                                            @ApiParam(value = "租户id") @RequestParam Long tenantId,
                                                            @ApiParam(value = "操作者") @RequestParam Long operatorId,
                                                            @ApiParam(value = "操作类型") @RequestParam Integer operationType){
        try {
            cashWriteOffService.auditChangeWriteOffStatus(documentType,
                    documentHeaderId,
                    tenantId,
                    operatorId,
                    operationType);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(RespCode.SYS_FAILED);
        }
        return ResponseEntity.ok("SUCCESS");
    }

    /**
     * @api {GET} {{payment-service_url}}/api/payment/cash/write/off/of/prepayment/detail 【核销】预付款核销历史
     * @apiGroup PaymentService
     * @apiDescription 预付款查询核销明细
     *
     * @apiParam (请求参数) {Long} prepaymentRequisitionId 预付款申请ID
     * @apiParam (请求参数) {String} [documentNumber] 核销单据编号
     * @apiParam (请求参数) {String} [documentFormName] 核销单据类型
     *
     * @apiParamExample {json}请求样例：
     * /api/payment/cash/write/off/of/prepayment/detail?prepaymentRequisitionId=1&page=1&size=10
     *
     * @apiSuccess (预付款相关信息) {Long} id ID
     * @apiSuccess (预付款相关信息) {Long} cshTransactionDetailId 支付明细ID
     * @apiSuccess (预付款相关信息) {BigDecimal} writeOffAmount 核销金额
     * @apiSuccess (预付款相关信息) {String} documentType 核销单据类型
     * @apiSuccess (预付款相关信息) {Long} documentHeaderId 核销单据ID
     * @apiSuccess (预付款相关信息) {Long} documentLineId 核销单据行ID
     * @apiSuccess (预付款相关信息) {ZonedDateTime} writeOffDate 核销时间
     * @apiSuccess (预付款相关信息) {Long} tenantId 租户ID
     * @apiSuccess (预付款相关信息) {String} status 状态
     * @apiSuccess (预付款相关信息) {String} statusDescription 状态描述
     * @apiSuccess (预付款相关信息) {String} operationType 操作类型
     * @apiSuccess (预付款相关信息) {Long} setOfBooksId 账套ID
     * @apiSuccess (预付款相关信息) {String} remark 备注
     * @apiSuccess (预付款相关信息) {String} documentNumber 核销单据编号
     * @apiSuccess (预付款相关信息) {Integer} documentLineNumber 核销单据行序号
     * @apiSuccess (预付款相关信息) {Long} documentApplicantId 单据申请人ID
     * @apiSuccess (预付款相关信息) {String} documentApplicantName 单据申请人名称
     * @apiSuccess (预付款相关信息) {String} currency 币种
     * @apiSuccess (预付款相关信息) {String} documentFormName 核销单据类型
     * @apiSuccessExample {json} 成功返回样例:
     *  [
    {
    "id": "1003876531346198529",
    "cshTransactionDetailId": "1003875709879177217",
    "writeOffAmount": 2000,
    "documentType": "PUBLIC_REPORT",
    "documentHeaderId": "1003875952871358465",
    "documentLineId": "1003876141409517570",
    "writeOffDate": "2018-06-05T13:50:00+08:00",
    "tenantId": "937506219191881730",
    "status": "Y",
    "statusDescription": "已复核",
    "operationType": "WRITE_OFF",
    "setOfBooksId": "937515627984846850",
    "remark": null,
    "documentNumber": "PR180600021",
    "documentLineNumber": 1,
    "documentApplicantId": 177601,
    "documentApplicantName": "清浅",
    "currency": "CNY",
    "documentFormName": "业务宣传费"
    }
    ]
     */

    @ApiOperation(value = "预付款查询核销明细", notes = "预付款查询核销明细 开发：")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页多少条", dataType = "int"),
    })
    @GetMapping("/of/prepayment/detail")
    public ResponseEntity<List<CashWriteOffHistoryDTO>> getPrepaymentWriteOffDetail(@ApiParam(value = "预付款申请ID") @RequestParam(value = "prepaymentRequisitionId") Long prepaymentRequisitionId,
                                                                                    @ApiParam(value = "核销单据编号") @RequestParam(value = "documentNumber",required = false) String documentNumber,
                                                                                    @ApiParam(value = "核销单据类型") @RequestParam(value = "documentFormName",required = false) String documentFormName,
                                                                                    @ApiIgnore Pageable pageable) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashWriteOffHistoryDTO> prepaymentWriteOffDetail = cashWriteOffService.getPrepaymentWriteOffDetail(prepaymentRequisitionId, documentNumber, documentFormName, page);
        HttpHeaders httpHeaders = PageUtil.generateHttpHeaders(page, "/api/payment/cash/write/off/of/prepayment/detail");
        return new ResponseEntity<>(prepaymentWriteOffDetail,httpHeaders,HttpStatus.OK);
    }
}
