package com.hand.hcf.app.payment.implement.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.*;
import com.hand.hcf.app.payment.service.*;
import com.hand.hcf.app.payment.utils.SpecificationUtil;
import com.hand.hcf.app.payment.web.dto.CashTransactionDataWebDTO;
import com.hand.hcf.app.payment.web.dto.CashWriteOffRequestWebDto;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionHeaderWebDTO;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @description: 第三方接口-其他模块访问此支付模块的api
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/12/18 11:44
 */

@RestController
@AllArgsConstructor
//@PreAuthorize("hasRole('" + AuthoritiesConstants.INTEGRATION_CLIENTS + "')")
public class PaymentImplementController /*implements PaymentInterface, ApplyPaymentInterface*/ {
    private static final Logger log = LoggerFactory.getLogger(PaymentImplementController.class);
    private final CashTransactionDataService transactionDataService;
    private final CashTransactionDetailService cashTransactionDetailService;
    private final CashWriteOffService cashWriteOffService;
    private final PaymentRequisitionHeaderService paymentRequisitionHeaderService;
    private final PaymentRequisitionTypesService paymentRequisitionTypesService;
    private final MapperFacade mapper;
    private final CashTransactionClassService cashTransactionClassService;
    private final CashDefaultFlowItemService cashDefaultFlowItemService;

    private final CashPaymentMethodService cashPaymentMethodService;

    private final CashFlowItemService cashFlowItemService;

    /**
     * 给artemis、prepayment 提供
     * 根据现金事务分类ID->transactionClassId，返回现金事务分类code、现金事务分类name，
     * 以及该现金事务分类下的默认现金流量项的code和name
     *
     * @param transactionClassId 现金事务分类id
     * @return
     */

    //@GetMapping("/cash/default/flowitems/queryByTransactionClassId/{transactionClassId}")
    public CashDefaultFlowItemCO getCashDefaultFlowItemByTransactionClassId(@PathVariable("transactionClassId") Long transactionClassId) {
        CashDefaultFlowItem item = cashDefaultFlowItemService.getCashDefaultFlowItemByTransactionClassId(transactionClassId);
        CashDefaultFlowItemCO co = mapper.map(item, CashDefaultFlowItemCO.class);
        return co;
    }

    /**
     * 根据现金事务分类ID获取现金事务分类code、现金事务分类name
     *
     * @param id 现金事务分类id
     * @return
     */

    //@GetMapping("/cash/transaction/classes/forArtemisById/{id}")
    public CashTransactionClassCO getCashTransactionClassById(@PathVariable("id") Long id) {
        CashTransactionClass cashTransactionClass = cashTransactionClassService.getCashTransactionClassById(id);
        CashTransactionClassCO co = mapper.map(cashTransactionClass, CashTransactionClassCO.class);
        return co;
    }

    /**
     * 获取当前账套下的，启用的、现金事务类型为PREPAYMENT(预付款) 的 现金事务分类
     * @param setOfBookId 账套id
     * @return
     */

    public List<CashTransactionClassCO> listCashTransactionClassBySetOfBookId(@PathVariable("setOfBookId") Long setOfBookId) {
        List<CashTransactionClass> cashTransactionClassList = cashTransactionClassService.listCashTransactionClassBySetOfBookId(setOfBookId);
        List<CashTransactionClassCO> cashTransactionClassCOs = mapper.mapAsList(cashTransactionClassList, CashTransactionClassCO.class);
        return cashTransactionClassCOs;
    }

    /**
     * 根据未核销金额范围获取单据核销信息
     * @param unWriteOffAmountFrom 未核销金额从
     * @param unWriteOffAmountTo 未核销金额至
     * @param setOfBooksId 账套id
     * @return
     */

    public List<CashWriteOffDocumentAmountCO> listDocumentByWriteOffAmount(@RequestParam(required = false, value = "unWriteOffAmountFrom") BigDecimal unWriteOffAmountFrom,
                                                                           @RequestParam(required = false, value = "unWriteOffAmountTo") BigDecimal unWriteOffAmountTo,
                                                                           @RequestParam(required = false, value = "setOfBooksId") Long setOfBooksId){
        List<CashWriteOffDocumentAmountCO> list = mapper.mapAsList(
                cashWriteOffService.listDocumentByWriteOffAmount(unWriteOffAmountFrom, unWriteOffAmountTo, setOfBooksId, OrgInformationUtil.getCurrentTenantId()), CashWriteOffDocumentAmountCO.class);
        return list;
    }

    /**
     * 根据未核销金额范围获取不满足条件的单据信息
     * @param unWriteOffAmountFrom 未核销金额从
     * @param unWriteOffAmountTo 未核销金额至
     * @param setOfBooksId 账套id
     * @return
     */

    public List<Long> listExcludeDocumentByWriteOffAmount(@RequestParam(required = false) BigDecimal unWriteOffAmountFrom,
                                                            @RequestParam(required = false) BigDecimal unWriteOffAmountTo,
                                                            @RequestParam(required = false) Long setOfBooksId){
        return cashWriteOffService.listExcludeDocumentByWriteOffAmount(unWriteOffAmountFrom,unWriteOffAmountTo,setOfBooksId, OrgInformationUtil.getCurrentTenantId());
    }

    /**
     * 批量导入支付数据
     * @param COs 通用支付信息
     * @return
     */

    public void saveTransactionDataBatch(@RequestBody @Valid List<CashTransactionDataCreateCO> COs) {
        List<CashTransactionData> cashTransactionDatas = mapper.mapAsList(COs, CashTransactionData.class);
        transactionDataService.saveTransactionDataBatch(cashTransactionDatas);
    }

    /**
     *  根据预付款单据头ID查询已付款金额和已退款金额
     * @param ids 预付款单据头id集合
     * @param employeeId 申请人id
     * @param companyId 单据公司id
     * @param documentTypeId 单据类型id
     * @return
     */

    public List<PaymentDocumentAmountCO> listAmountByPrepaymentIds(@RequestBody(required = false) List<Long> ids,
                                                                   @RequestParam(value = "employeeId", required = false) Long employeeId,
                                                                   @RequestParam(value = "companyId", required = false)Long companyId,
                                                                   @RequestParam(value = "documentTypeId", required = false)Long documentTypeId){

        return transactionDataService.listAmountByDocumentIds(ids, SpecificationUtil.PREPAYMENT_REQUISITION, employeeId,
                companyId, documentTypeId);
    }

    /**
     * 根据现金事务分类ID集合查询详情
     * @param list 现金事务分类id集合
     * @return
     */

    public List<CashTransactionClassCO> listCashTransactionClassByIdList(@RequestBody List<Long> list) {
        List<CashTransactionClass> cashTransactionClassList = cashTransactionClassService.listCashTransactionClassByIdList(list);

        List<CashTransactionClassCO> cos = mapper.mapAsList(cashTransactionClassList,CashTransactionClassCO.class);
        return cos;
    }

    /**
     * 获取某个预付款单类型下，当前账套下、启用的、PREPAYMENT类型的 已分配的、未分配的、全部的 现金事物分类
     * @param forArtemisCO
     * @param page
     * @param size
     * @return
     * @throws URISyntaxException
     */

    public Page<CashTransactionClassCO> listCashTransactionClassForPerPayByRange(@RequestBody @Valid CashTransactionClassForOtherCO forArtemisCO,
                                                                                 @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                                                 @RequestParam(value = "size",required = false,defaultValue = "10") int size) throws URISyntaxException {
        Pageable pageable = PageRequest.of(page, size);
        Page mybatisPage = PageUtil.getPage(pageable);
        Page<CashTransactionClass> list = cashTransactionClassService.listCashTransactionClassForPerPayByRange(forArtemisCO,mybatisPage);
        return mapAsPage(list, CashTransactionClassCO.class);
    }

    /**
     * 获取某个表单下，当前账套下 已分配的、未分配的 现金事物分类
     * @param forArtemisCO
     * @param page
     * @param size
     * @return
     * @throws URISyntaxException
     */

    public Page<CashTransactionClassCO> listCashTransactionClassByRange(@RequestBody @Valid CashTransactionClassForOtherCO forArtemisCO,
                                                                        @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                                        @RequestParam(value = "size",required = false,defaultValue = "10") int size) throws URISyntaxException {

        Pageable pageable = PageRequest.of(page, size);
        Page mybatisPage = PageUtil.getPage(pageable);
        Page<CashTransactionClass> list = cashTransactionClassService.listCashTransactionClassByRange(forArtemisCO, mybatisPage);
        Page<CashTransactionClassCO> cos = mapAsPage(list, CashTransactionClassCO.class);
        return cos;
    }

    /**
     * 单据提交，核销记录生效，并且传入支付平台
     * @param documentType 单据类型
     * @param documentHeaderId 单据头id
     * @param documentLineIds 单据行id集合
     * @param lastUpdatedBy 最后更新人
     * @return
     */
    //@PostMapping("/write/off/enforce")

    public String saveWriteOffTakeEffect(@RequestParam String documentType,
                                     @RequestParam Long documentHeaderId,
                                     @RequestBody(required = false) List<Long> documentLineIds,
                                     @RequestParam Long lastUpdatedBy) {
        try {
            cashWriteOffService.writeOffTakeEffect(documentType,
                    documentHeaderId,
                    documentLineIds,
                    OrgInformationUtil.getCurrentTenantId(),
                    lastUpdatedBy);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(RespCode.SYS_FAILED);
        }
        return null;
    }

    /**
     * 根据单据信息，回滚核销记录至未生效
     * @param documentType 单据类型
     * @param documentHeaderId 单据头id
     * @param documentLineIds 单据行id集合
     * @param lastUpdatedBy 最后跟新人
     * @return
     */

    public String updateRollback(@RequestParam String documentType,
                           @RequestParam Long documentHeaderId,
                           @RequestBody(required = false) List<Long> documentLineIds,
                           @RequestParam Long lastUpdatedBy) {
        try {
            cashWriteOffService.writeOffRollback(documentType,
                    documentHeaderId,
                    documentLineIds,
                    OrgInformationUtil.getCurrentTenantId(),
                    lastUpdatedBy);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(RespCode.SYS_FAILED);
        }
        return null;
    }

    /**
     * 核销 - 确认核销后立即生效
     * @param requestCO
     * @return
     */

    public List<CashWriteOffCO> saveWriteOffAtOnce(@RequestBody @Valid CashWriteOffRequestCO requestCO) {
        CashWriteOffRequestWebDto webRequestDto = mapper.map(requestCO, CashWriteOffRequestWebDto.class);
        cashWriteOffService.writeOffAtOnce(webRequestDto);
        List<CashWriteOffCO> collect = mapper.mapAsList(webRequestDto.getCashWriteOffMsg(), CashWriteOffCO.class);
        return collect;
    }

    /**
     * 查询单据是否有行信息
     * @param oid 单据oid
     * @return
     */
    //@GetMapping("/get/has/line/by/oid")

    public Boolean getHasLineByOid(@RequestParam String oid){
        return paymentRequisitionHeaderService.getHasLineByOid(oid);
    }

    /**
     * 根据付款申请单oid获取付款申请单详情
     * @param oid 付款申请单oid
     * @return
     */
    //@GetMapping("/get/acp/head/by/oid")

    public PaymentRequisitionHeaderCO getAcpHeadByOid(@RequestParam String oid){
        PaymentRequisitionHeaderCO paymentRequisitionHeaderCO = new PaymentRequisitionHeaderCO();
        mapper.map(paymentRequisitionHeaderService.getHeadByOID(oid), paymentRequisitionHeaderCO);
        return paymentRequisitionHeaderCO;
    }

    /**
     * 根据oid更新付款申请单状态
     * @param oid 单据oid
     * @param userId 用户id
     * @param status 状态
     * @param versionNumber 版本号
     * @param rejectType 驳回类型: 1000-正常, 1001-撤回, 1002-审批驳回 1003-审核驳回 1004-开票驳回
     * @param approvalComment 驳回原因
     * @return
     */
    //@PutMapping("/update/acp/status/by/oid")

    public void updateAcpStatusByOid(@RequestParam String oid,
                                     @RequestParam Long userId,
                                     @RequestParam Integer status,
                                     @RequestParam Integer versionNumber,
                                     @RequestParam( value = "rejectType",required = false) String rejectType, @RequestParam(value = "approvalComment",required = false) String approvalComment
    ){
        /*ExceptionDetail messageDTO = new ExceptionDetail();
        try{
            messageDTO = paymentRequisitionHeaderService.updateStatusByOid(oid,userId,status,versionNumber,rejectType,approvalComment);
        } catch (BizException e) {
            String errorMsg = exceptionService.getMessageDetailByCode(e.getCode());
            messageDTO.setErrorCode(e.getCode());
            messageDTO.setMessage(StringUtils.hasText(errorMsg) ? errorMsg : e.getMessage());
            log.error("根据就oid更新付款申请单状态失败,原因代码：{},原因为：{}", e.getCode(), errorMsg);
        } catch (Exception e) {
            messageDTO.setErrorCode("-1");
            messageDTO.setMessage("SYSTEM ERROR!");
            log.error("根据就oid更新付款申请单状态失败,原因：{}", e.getMessage());
        }
        return messageDTO;*/

        paymentRequisitionHeaderService.updateStatusByOid(oid,userId,status,versionNumber,rejectType,approvalComment);

        return ;
    }

    /**
     * 条件查询付款申请单头信息
     * @param queryCO
     * @return
     */
    //@PostMapping("/get/acp/head/by/input")

    public List<PaymentRequisitionHeaderCO> listAcpHeadByInput(@RequestBody PaymentRequisitionQueryCO queryCO){
        List<PaymentRequisitionHeaderCO> list = mapper.mapAsList(
                paymentRequisitionHeaderService.getHeadByInput(queryCO), PaymentRequisitionHeaderCO.class);
        return list;
    }

    /**
     * 查询单据的核销金额
     * @param documentType 单据类型
     * @param documentHeaderId 单据头id
     * @param documentLineId 单据行id
     * @return
     */
    //@GetMapping("/write/off/get/amount")

    public Map<Long,BigDecimal> listDocumentWriteOffAmount(@RequestParam String documentType,
                                                          @RequestParam Long documentHeaderId,
                                                          @RequestParam(required = false) Long documentLineId){
        return cashWriteOffService.listDocumentWriteOffAmount(documentType,documentHeaderId,documentLineId, OrgInformationUtil.getCurrentTenantId());
    }

    /**
     * 根据合同ID查询支付明细数据
     * @param headerId 合同头id
     * @return
     */
    //@GetMapping("/get/cash/detail/by/headerId/{headerId}")

    public List<CashTransactionDetailCO> listCashDetailByHeaderId(@PathVariable("headerId") Long headerId){
        List<CashTransactionDetail> cashTransactionDetails = cashTransactionDetailService.listCashDetailByHeaderId(headerId);
        List<CashTransactionDetailCO> list = mapper.mapAsList(cashTransactionDetails, CashTransactionDetailCO.class);
        return list;
    }

    /**
     * 根据付款申请单类型id获取付款申请单信息
     * @param id 付款申请单类型id
     * @return
     */
    //@GetMapping("/get/acp/type/by/id")

    public PaymentRequisitionTypesCO getAcpTypeById(@RequestParam Long id){
        PaymentRequisitionTypes types = paymentRequisitionTypesService.getTypesById(id);
        PaymentRequisitionTypesCO paymentRequisitionTypesCO = new PaymentRequisitionTypesCO();
        mapper.map(types, paymentRequisitionTypesCO);
        return paymentRequisitionTypesCO;
    }

    /**
     * 查询已支付单据的核销金额
     * @param documentCategory 单据大类
     * @param documentId 单据头ID
     * @param documentLineId 单据行ID
     * @return
     */
    //@GetMapping("/get/prepayment/cash/write/off/amount")

    public Map<Long,BigDecimal> listPaidDocumentWriteOffAmount(@RequestParam String documentCategory,
                                                             @RequestParam Long documentId,
                                                             @RequestParam(required = false) Long documentLineId){
        return cashWriteOffService.listPaidDocumentWriteOffAmount(documentCategory,documentId,documentLineId);
    }

    /**
     * 根据报账单ID条件查询相关的支付数据
     * @param headerId 报账单id
     * @param partnerCategory 收款方类型
     * @param partnerId 收款方
     * @param amountFrom 金额从
     * @param amountTo 金额至
     * @param dataIds 待付明细id集合
     * @param page
     * @param size
     * @return
     */
    //@PostMapping("/public/query")

    public Page<CashTransactionDataCO> listPublicPage(@RequestParam(value = "headerId") Long headerId,
                                                        @RequestParam(value = "partnerCategory", required = false) String partnerCategory,
                                                        @RequestParam(value = "partnerId", required = false) Long partnerId,
                                                        @RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                                        @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                                        @RequestBody List<Long> dataIds,
                                                        @RequestParam(value = "page",required = false,defaultValue = "0") int page,
                                                        @RequestParam(value = "size",required = false,defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page mybatisPage = PageUtil.getPage(pageable);
        Page<CashTransactionDataWebDTO> cashTransactionDataWebDTOPage = transactionDataService.selectPublicPage(headerId, dataIds, mybatisPage, partnerCategory, partnerId, amountFrom, amountTo);
        Page<CashTransactionDataCO> transactionDataCOPage = mapAsPage(cashTransactionDataWebDTOPage,CashTransactionDataCO.class);
        return transactionDataCOPage;
    }

    /**
     * 根据待付数据ID更新待付数据的金额（报账单类型)
     * @param cos
     * @return
     */
    //@PostMapping("/cash/data/updateByDocument")

    public void updateByDocument(@RequestBody List<PaymentExpenseReportCO> cos){
        /*ExceptionDetail messageDTO = new ExceptionDetail();
        try{
            messageDTO = transactionDataService.updateByDocument(cos);
        }catch (BizException e) {
            String errorMsg = exceptionService.getMessageDetailByCode(e.getCode());
            messageDTO.setErrorCode(e.getCode());
            messageDTO.setMessage(StringUtils.hasText(errorMsg) ? errorMsg : e.getMessage());
            log.error("根据待付数据ID更新待付数据的金额失败,原因代码：{},原因为：{}", e.getCode(), errorMsg);
        } catch (Exception e) {
            messageDTO.setErrorCode("-1");
            messageDTO.setMessage("SYSTEM ERROR!");
            log.error("根据待付数据ID更新待付数据的金额失败,原因：{}", e.getMessage());
        }
        return messageDTO;*/


        transactionDataService.updateByDocument(cos);

        return ;
    }

    /**
     * 根据待付数据ID查询待付数据
     * @param id 待付数据id
     * @return
     */
    //@GetMapping("/cash/data/getPublicById")

    public CashTransactionDataCO getPublicById(@RequestParam("id") Long id){
        return mapper.map(transactionDataService.queryPublicById(id), CashTransactionDataCO.class);
    }

    /**
     * 根据报账单头ID查询相关的金额
     * @param headerId 报账单头id
     * @return
     */
    //@GetMapping("/cash/data/getPublic/amount")

    public List<PublicReportLineAmountCO> listPublicAmount(@RequestParam("headerId") Long headerId){
        return transactionDataService.listPublicAmount(headerId);
    }

    /**
     * 报账单获取核销记录以及支付金额
     * @param documentType 单据类型
     * @param documentHeaderId 单据头id
     * @param documentLineId 单据行id
     * @return
     */
    //@GetMapping("/write/off/history/all")

    public CashWriteOffHistoryAndPaymentAmountCO listCashWriteOffHistoryAll(@RequestParam String documentType,
                                                                            @RequestParam Long documentHeaderId,
                                                                            @RequestParam(required = false) Long documentLineId){
        CashWriteOffHistoryAndPaymentAmountCO co = new CashWriteOffHistoryAndPaymentAmountCO();
        List<PublicReportLineAmountCO> publicAmount = transactionDataService.listPublicAmount(documentHeaderId);
        List<CashWriteOffCO> cashWriteOffHistory = mapper.mapAsList(
                cashWriteOffService.getCashWriteOffHistory(documentType, documentHeaderId, documentLineId, null), CashWriteOffCO.class);
        co.setCashWriteOffHistories(cashWriteOffHistory);
        co.setPublicAmounts(publicAmount);
        return co;
    }

    /**
     * 根据报账单头ID删除相关的待付数据
     * @param headerId 报账单头id
     * @return
     */
    //@DeleteMapping("/cash/transactionData/deleteByHeaderId")

    public Boolean deleteTransactionDataByHeaderId(@RequestParam Long headerId){
        return transactionDataService.deleteTransactionDataByHeaderId(headerId);
    }

    /**
     * 查询报账单的反冲金额或者已付金额的ID
     * @param amountFrom 金额从
     * @param amountTo 金额至
     * @param flag true--查询已付金额 false--查询反冲金额
     * @return
     */
    //@GetMapping("/public/query/all/amount")

    public List<PublicReportAmountCO> listIdAndAmount(@RequestParam(value = "amountFrom", required = false) BigDecimal amountFrom,
                                                        @RequestParam(value = "amountTo", required = false) BigDecimal amountTo,
                                                        @RequestParam(value = "flag",defaultValue = "false") Boolean flag){
        return transactionDataService.listIdAndAmount(amountFrom, amountTo,flag);
    }

    /**
     * 根据预付款头、行ID查询报账单的核销记录
     * @param co
     * @return
     */
    //@PostMapping("/prepayment/query/publicByIds")

    public Map<Long, List<PublicReportWriteOffCO>> listReportWriteOffCO(@RequestBody PrepaymentDocumentIdsCO co){
        Long headerId = co.getHeaderId();
        List<Long> lineIds = co.getLineIds();
        return cashTransactionDetailService.selectIdByPrePayment(headerId, lineIds);
    }

    /**
     * 批量查询单据的已核销金额
     * @param documentCategory 单据类型
     * @param documentIds 单据id集合
     * @return
     */
    //@PostMapping("/get/prepayment/cash/write/off/amount/batch")

    public Map<Long,BigDecimal> listDocumentWriteOffAmount(@RequestParam String documentCategory,
                                                             @RequestBody List<Long> documentIds){
        return cashWriteOffService.selectDocumentWriteOffAmountBatch(documentCategory,documentIds);
    }

    /**
     * 根据预付款单据行ID查询已付款金额和已退款金额
     * @param lineIds 预付款单据行id集合
     * @return
     */
    //@PostMapping("/get/prepayment/pay/return/amount")

    public List<PaymentDocumentAmountCO> listAmountByPrepaymentLineIds(@RequestBody List<Long> lineIds){
        return transactionDataService.listAmountByPrepaymentLineIds(lineIds, SpecificationUtil.PREPAYMENT_REQUISITION);
    }

    /**
     * 根据id或者限定条件查询现金事务分类
     * @param selectId 现金事务分类id
     * @param setOfBooksId 账套id
     * @param code 现金事务分类代码
     * @param name 现金事务分类名称
     * @param page 第几页
     * @param size 多少条
     * @return
     * @throws URISyntaxException
     */
//    @Override
//    @GetMapping("/cash/transaction/classes/queryByIdOrCond")
//    public Page<AccountingMatchGroupValueDTO> a(
//            @RequestParam(value = "selectId", required = false) Long selectId,
//            @RequestParam(value = "setOfBooksId", required = false) Long setOfBooksId,
//            @RequestParam(value = "code", required = false) String code,
//            @RequestParam(value = "name", required = false) String name,
//            @RequestParam(value = "page",required = false,defaultValue = "0") int page,
//            @RequestParam(value = "size",required = false,defaultValue = "10") int size) throws URISyntaxException {
//        Pageable pageable = PageRequest.of(page, size);
//        Page mybatisPage = PageUtil.getPage(pageable);
//        Page<AccountingMatchGroupValueDTO> list = cashTransactionClassService.listCashTransactionClassByIdOrCond(
//                selectId, setOfBooksId, code, name, mybatisPage);
//        return list;
//    }

    /**
     * 根据id或者条件查询现金流量项
     * @param selectId 现金流量项id
     * @param setOfBooksId 账套id
     * @param code 现金流量项代码
     * @param name 现金流量项名称
     * @param page 第几页
     * @param size 多少条
     * @return
     * @throws URISyntaxException
     */
//    @Override
//    @GetMapping("/cash/flow/items/queryByIdOrCond")
//    public Page<AccountingMatchGroupValueDTO> listCashFlowItemByIdOrCond(
//            @RequestParam(value = "selectId",required = false) Long selectId,
//            @RequestParam(value = "setOfBooksId",required = false) Long setOfBooksId,
//            @RequestParam(value = "code", required = false) String code,
//            @RequestParam(value = "name", required = false) String name,
//            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
//            @RequestParam(value = "size", required = false, defaultValue = "10") int size)throws URISyntaxException {
//        Pageable pageable = PageRequest.of(page, size);
//        Page mybatisPage = PageUtil.getPage(pageable);
//        Page<AccountingMatchGroupValueDTO> list = cashFlowItemService.listCashFlowItemByIdOrCond(selectId,setOfBooksId,code, name, mybatisPage);
//        return list;
//    }

    /**
     * 给对公报账单提供，根据账套id，查询该账套下启用的、付款类型的现金事务分类集合
     * @param setOfBooksId 账套id
     * @return
     */

    //@GetMapping("/cash/transaction/classes/queryBySetOfBooksId/{setOfBooksId}")
    public List<CashTransactionClassCO> listCashTransactionClassBySetOfBooksId(
            @PathVariable("setOfBooksId") Long setOfBooksId) {
        List<CashTransactionClass> cashTransactionClasses = cashTransactionClassService
                .listCashTransactionClassBySetOfBooksId(setOfBooksId);
        List<CashTransactionClassCO> list = mapper.mapAsList(cashTransactionClasses, CashTransactionClassCO.class);
        return list;
    }

    /**
     * 根据ID查询付款方式
     * @param id
     * @param paymentMethod
     * @return
     */

    public CashPaymentMethodCO getPaymentMethodById(@PathVariable("id") Long id,
                                                        @RequestParam(value = "paymentMethod",required = false) String paymentMethod) {
        return mapper.map(cashPaymentMethodService.selectPaymentMethodById(id,paymentMethod), CashPaymentMethodCO.class);
    }

    /**
     * 核销核算
     * @param co
     * @return
     */

    public String saveWriteOffJournalLines(@RequestBody @Valid CashWriteOffAccountCO co) {
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
     * 核销单据审核
     * @param documentType 单据类型
     * @param documentHeaderId 单据id
     * @param operatorId 操作人id
     * @param operationType 操作类型 1:通过 -1:拒绝
     * @return
     */

    public String updateAuditChangeWriteOffStatus(@RequestParam String documentType,
                                            @RequestParam Long documentHeaderId,
                                            @RequestParam Long operatorId,
                                            @RequestParam Integer operationType){
        try {
            cashWriteOffService.auditChangeWriteOffStatus(documentType,
                    documentHeaderId,
                    OrgInformationUtil.getCurrentTenantId(),
                    operatorId,
                    operationType);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(RespCode.SYS_FAILED);
        }
        return "SUCCESS";
    }

    /**
     * 提交时，更新filterFlag字段
     * @param oid oid 单据oid
     * @param filterFlag 是否跳过:true表示跳过,false表示不跳(根据单据驳回重新提交,金额或成本中心等是否变更 确认审批时候需要过滤)
     * @return
     */

    public Boolean updateFilterFlagByOid(@RequestParam("oid") String oid, @RequestParam("filterFlag") Boolean filterFlag) {
        PaymentRequisitionHeaderWebDTO dto =  paymentRequisitionHeaderService.getHeadByOID(oid);
        PaymentRequisitionHeader header = new PaymentRequisitionHeader();
        BeanUtils.copyProperties(dto,header);
        paymentRequisitionHeaderService.updateById(header);
        return true;
    }

    /**
     * 根据单据信息删除核销记录
     * @param documentType
     * @param documentHeaderId
     * @param documentLineId
     * @return
     */

    public Boolean deleteWriteOffForDocumentMessage(@RequestParam("documentType") String documentType,
                                                    @RequestParam("documentHeaderId") Long documentHeaderId,
                                                    @RequestParam(required = false, value = "documentLineId") Long documentLineId) {
        return cashWriteOffService.deleteCashWriteOffByDocumentMessage(documentType, documentHeaderId, documentLineId);
    }


    public String getFormTypeNameByFormTypeId(@RequestParam("id") Long id) {
        PaymentRequisitionTypes requisitionType = paymentRequisitionTypesService.selectById(id);
        return requisitionType != null ? requisitionType.getDescription() : null;
    }

    /**
     * 通用支付信息数据接入
     * @param cashTransactionDataCreateCO 通用支付信息
     * @return
     */

    public void saveTransactionData(@RequestBody @Valid CashTransactionDataCreateCO cashTransactionDataCreateCO) {
        CashTransactionData cashTransactionData = new CashTransactionData();
        mapper.map(cashTransactionDataCreateCO,cashTransactionData);
        transactionDataService.createTransactionData(cashTransactionData);
    }

    /**
     * 资金回写支付明细表
     * @param cos
     * @return
     */

    public Boolean updateCashTransactionDetailByFund(@RequestBody List<CashTransactionDetailCO> cos) {
        return cashTransactionDetailService.updateCashTransactionDetailByFund(cos);
    }

    /**
     * 根据code查询现金流项
     *
     * @param code
     * @return
     */

    public CashFlowItemCO getCashFlowItemByCode(String code) {
        return cashFlowItemService.getCashFlowItemByCode(code);
    }

    /**
     * 根据 根据已付金额来确定报销单数据
     */

    public   List<CashTransactionDetailCO> queryCashTransactionDetailByReport(BigDecimal paidAmountFrom,
                                                                              BigDecimal paidAmountTo,
                                                                              String backlashFlag) {

        return cashTransactionDetailService.queryCashTransactionDetailByReport(paidAmountFrom,paidAmountTo,backlashFlag);
    }

    private <S, D> Page<D> mapAsPage(Page<S> source, Class<D> destinationClass){
        Page<D> result = new Page<>();
        List<S> sourceRecords = source.getRecords();
        source.setRecords(new ArrayList<>());
        mapper.map(source, result);
        List<D> resultRecords = mapper.mapAsList(sourceRecords, destinationClass);
        result.setRecords(resultRecords);
        return result;
    }


    /**
     * 根据代码、名称分页查询某个账套下，启用的不在id范围内的现金流量项
     *
     * @param setOfBookId
     * @param flowCode
     * @param description
     * @param enabled
     * @param existIdList
     * @param page
     * @param size
     * @return
     */

    public Page<CashFlowItemCO> getUndistributedCashFlowItemByCond(
            @RequestParam(value = "setOfBookId") Long setOfBookId,
            @RequestParam(value = "flowCode", required = false) String flowCode,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @RequestBody List<Long> existIdList,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ){
        Page pageTemp = PageUtil.getPage(page, size);
        return cashFlowItemService.getUndistributedCashFlowItemByCond(setOfBookId,flowCode,description,enabled,existIdList,pageTemp);
    }
}
