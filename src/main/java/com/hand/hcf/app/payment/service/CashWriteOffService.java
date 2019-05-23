package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.core.util.OperationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.PaginationUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import com.hand.hcf.app.payment.domain.CashWriteOff;
import com.hand.hcf.app.payment.domain.enumeration.CashWriteOffStatus;
import com.hand.hcf.app.payment.externalApi.*;
import com.hand.hcf.app.payment.persistence.CashWriteOffMapper;
import com.hand.hcf.app.payment.utils.ParameterCode;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.adapter.CashWriteOffAdapter;
import com.hand.hcf.app.payment.web.dto.*;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by kai.zhang on 2017-10-30.
 */
@Service
@AllArgsConstructor
public class CashWriteOffService extends BaseService<CashWriteOffMapper, CashWriteOff> {

    private final PaymentPrepaymentService prepaymentService;
    private final SupplierService supplierService;
    private final PaymentAccountingService accountingService;
    private final PaymentOrganizationService organizationService;
//    private final ExpenseReportService expenseReportService;
    private final PaymentContractService contractService;
    private final MapperFacade mapperFacade;


    private final CashTransactionDetailService cashTransactionDetailService;
    private final MessageService messageService;
    private final ExpenseService expenseService;
    /**
     * 新增核销记录
     *
     * @param cash
     * @return
     */
    @Transactional
    public CashWriteOff createCashWriteOff(CashWriteOff cash) {
        // 状态默认为不生效
        if(TypeConversionUtils.isEmpty(cash.getStatus())){
            cash.setStatus(CashWriteOffStatus.N.toString());
        }
        if (cash.getId() != null) {
            throw new BizException(RespCode.SYS_COLUMN_SHOULD_BE_EMPTY, "id");
        }
        baseMapper.insert(cash);
        return cash;
    }

    /**
     * 批量新增核销记录
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashWriteOff> createCashWriteOffBatch(List<CashWriteOff> list) {
        list.stream().forEach(cashWriteOff -> {
            createCashWriteOff(cashWriteOff);
        });
        return list;
    }

    /**
     * 更新核销记录
     *
     * @param cash
     * @return
     */
    @Transactional
    public CashWriteOff updateCashWriteOff(CashWriteOff cash) {
        this.updateAllColumnById(cash);
        return cash;
    }

    /**
     * 批量更新核销记录
     *
     * @param list
     * @return
     */
    @Transactional
    public List<CashWriteOff> updateCashWriteOffBatch(List<CashWriteOff> list) {
        list.stream().forEach(cashWriteOff -> {
            updateCashWriteOff(cashWriteOff);
        });
        return list;
    }

    /**
     * 根据单据信息删除核销记录
     * @param documentType
     * @param documentHeaderId
     * @param documentLineId
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCashWriteOffByDocumentMessage(String documentType,
                                                    Long documentHeaderId,
                                                    Long documentLineId) {
        int i = selectCount(new EntityWrapper<CashWriteOff>()
                .eq("document_type", documentType)
                .eq("document_header_id", documentHeaderId)
                .ne("status", "N")
                .eq(documentLineId != null, "document_line_id", documentLineId));
        if(i > 0){
            throw new BizException(RespCode.PAYMENT_WRITE_OFF_CANNOT_DELETE);
        }
        baseMapper.delete(new EntityWrapper<CashWriteOff>()
                .eq("document_type",documentType)
                .eq("document_header_id",documentHeaderId)
                .eq(documentLineId != null,"document_line_id",documentLineId));
        return true;
    }

    /**
     * 删除核销记录
     *
     * @param id
     * @return
     */
    @Transactional
    public void deleteCashWriteOffById(Long id) {
        baseMapper.deleteById(id);
    }

    /**
     * 批量删除核销记录
     *
     * @param list
     * @return
     */
    @Transactional
    public void deleteCashWriteOffByIds(List<Long> list) {
        baseMapper.deleteBatchIds(list);
    }

    /**
     * 根据支付信息及单据信息，获取核销记录
     * @param domain
     * @return
     */
    @Transactional(readOnly = true)
    public List<CashWriteOff> getCashWriteOffByDocumentMsg(CashWriteOff domain){
        return baseMapper.selectList(new EntityWrapper<CashWriteOff>()
                .eq("csh_transaction_detail_id",domain.getCshTransactionDetailId())
                .eq("document_type",domain.getDocumentType())
                .eq("document_header_id",domain.getDocumentHeaderId())
                .eq("document_line_id",domain.getDocumentLineId())
                .eq("tenant_id",domain.getTenantId())
        );
    }

    /**
     * 根据id获取核销记录
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public CashWriteOff getCashWriteOffById(Long id) {
        CashWriteOff cashWriteOff = baseMapper.selectById(id);
        if (cashWriteOff == null ) {
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        return cashWriteOff;
    }

    /**
     * 单据核销历史
     * @param documentType
     * @param documentHeaderId
     * @param documentLineId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CashWriteOffWebDto> getCashWriteOffHistory(String documentType,
                                                           Long documentHeaderId,
                                                           Long documentLineId,
                                                           Page page){
        Wrapper<CashWriteOff> wrapper = new EntityWrapper<CashWriteOff>().eq("document_type", documentType)
                .eq("document_header_id", documentHeaderId)
                .eq(TypeConversionUtils.isNotEmpty(documentLineId),"document_line_id", documentLineId)
                .eq("operation_type","WRITE_OFF");
                //.eq("tenant_id", OrgInformationUtil.getCurrentTenantID());
        List<CashWriteOff> cashWriteOffs = null;
        if(page != null){
            cashWriteOffs = baseMapper.selectPage(page,wrapper);
        }else{
            cashWriteOffs = baseMapper.selectList(wrapper);
        }

        return cashWriteOffs.stream().map(cashWriteOff -> {
            CashWriteOffWebDto detailDto = CashWriteOffAdapter.toDto(cashWriteOff);
            CashTransactionDetailWebDTO cashTransactionDetailWebDTO = cashTransactionDetailService.getDetailById(detailDto.getCshTransactionDetailId());
            detailDto.setPrepaymentRequisitionTypeDesc(cashTransactionDetailWebDTO.getCshTransactionClassName());
            detailDto.setPrepaymentRequisitionNumber(cashTransactionDetailWebDTO.getDocumentNumber());
            detailDto.setBillcode(cashTransactionDetailWebDTO.getBillCode());
            return detailDto;
        }).collect(Collectors.toList());
    }

    /**
     * 获取待核销明细信息-分页
     * @param tenantId
     * @param companyId
     * @param partnerCategory
     * @param partnerId
     * @param formId               报账单formID
     * @param exportHeaderId       报账单头ID
     * @param contractId           计划付款行关联合同ID
     * @param documentType         单据类型
     * @param documentLineId       计划付款行ID
     * @return
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<CashWriteOffWebDto>> getCashWriteOffDetailByDocumentMsg(Long tenantId,
                                                                                       Long companyId,
                                                                                       String partnerCategory,
                                                                                       Long partnerId,
                                                                                       Long formId,
                                                                                       Long exportHeaderId,
                                                                                       Long contractId,
                                                                                       String documentType,
                                                                                       Long documentLineId,
                                                                                       String currencyCode,
                                                                                       String baseUrl,
                                                                                       Pageable pageable
    ) throws URISyntaxException {
        Page page = PageUtil.getPage(pageable);
        List<CashPrepaymentQueryDTO> cashWriteOffDetail = cashTransactionDetailService.queryPrepaymentResultPage(tenantId,
                companyId,
                partnerCategory,
                partnerId,
                formId,
                exportHeaderId,
                contractId,
                documentType,
                documentLineId,
                currencyCode,
                page);
        if(cashWriteOffDetail == null){
            return null;
        }
        List<CashWriteOffWebDto> cashWriteOffWebDtos = CashWriteOffAdapter.paymentToDto(cashWriteOffDetail);
        HttpHeaders httpHeaders = PaginationUtil.generatePaginationHttpHeaders(page, baseUrl);
        return new ResponseEntity(cashWriteOffWebDtos,httpHeaders, HttpStatus.OK);
    }

    /**
     * 核销 - 确认核销后立即生效
     * @param requestDto
     * @return
     */
    /*@SyncLock(lockPrefix = SyncLockPrefix.CSH_WRITE_OFF,waiting = true)*/
    @Transactional
    public void writeOffAtOnce(/*@LockedObject({"companyId","partnerCategory","partnerId"})*/ CashWriteOffRequestWebDto requestDto) {
        //根据公司信息 + 收款人信息做安全控制
        requestDto.getCashWriteOffMsg().stream().filter(e -> {
            return TypeConversionUtils.isNotEmpty(e.getWriteOffAmount());  //清除核销金额为空的数据
        }).forEach(cashWriteOffDto -> {
            boolean isComplete = false;
            //查询之前的核销记录
            if (requestDto.getDocumentLineAmount().compareTo(cashWriteOffDto.getWriteOffAmount()) == -1) {
                throw new BizException(RespCode.PAYMENT_WRITE_OFF_AMOUNT_GT_DOUCUMENT_AMOUNT);
            }
            if (cashWriteOffDto.getUnWriteOffAmount().compareTo(cashWriteOffDto.getWriteOffAmount()) == -1) {
                throw new BizException(RespCode.PAYMENT_WRITE_OFF_AMOUNT_GT_UN_WRITE_OFF_AMOUNT);
            }
            //未核销金额 = 本次核销金额 标记为完全核销
            if(cashWriteOffDto.getUnWriteOffAmount().compareTo(cashWriteOffDto.getWriteOffAmount()) == 0){
                isComplete = true;
            }
            if (TypeConversionUtils.isEmpty(cashWriteOffDto.getWriteOffDate())) {
                cashWriteOffDto.setWriteOffDate(ZonedDateTime.now());
            }
            Long setOfBooksId = organizationService.getById(requestDto.getCompanyId()).getSetOfBooksId();
            if (TypeConversionUtils.isEmpty(cashWriteOffDto.getPeriodName())) {
                PeriodCO periodInfoBySetOfBooksId = organizationService.getPeriodBysetOfBooksIdAndDateTime(setOfBooksId, cashWriteOffDto.getWriteOffDate());
                if (TypeConversionUtils.isNotEmpty(periodInfoBySetOfBooksId)) {
                    cashWriteOffDto.setPeriodName(periodInfoBySetOfBooksId.getPeriodName());
                }
            }
            CashWriteOff cashWriteOff = CashWriteOffAdapter.toDomain(cashWriteOffDto);
            CashWriteOffAdapter.setDocumentFields(requestDto,cashWriteOff);
            List<CashWriteOff> cashWriteOffByDocumentMsg = getCashWriteOffByDocumentMsg(cashWriteOff);
            cashWriteOff.setStatus(CashWriteOffStatus.P.toString());
            cashWriteOff.setOperationType("WRITE_OFF");
            cashWriteOff.setSetOfBooksId(setOfBooksId);
            //报账单头信息
            /*ExpenseReportHeaderDTO expPublicReportHead = null;
            if("PUBLIC_REPORT".equals(cashWriteOff.getDocumentType())){
                expPublicReportHead = expenseReportService.getExpPublicReportHeadById(cashWriteOff.getDocumentHeaderId());
                if(expPublicReportHead != null){
                    cashWriteOff.setDocumentNumber(TypeConversionUtils.parseString(expPublicReportHead.getBusinessCode()));
                    cashWriteOff.setDocumentApplicantId(TypeConversionUtils.parseLong(expPublicReportHead.getApplicationId()));
                    cashWriteOff.setDocumentCreatedDate(TypeConversionUtils.parseZonedDateTime(expPublicReportHead.getCreatedDate()));
                }
            }*/
            //若单据以前核销过，先清除核销记录(若核销记录有多条，也一并清除)
            if (cashWriteOffByDocumentMsg == null || cashWriteOffByDocumentMsg.size() == 0) {
                cashWriteOffDto.setWriteOffAmountBefore(BigDecimal.ZERO);
                // 只有本次核销金额不为空，且大于零时，才插入核销表
                if(cashWriteOff.getWriteOffAmount() != null && cashWriteOff.getWriteOffAmount().compareTo(BigDecimal.ZERO) == 1){
                    createCashWriteOff(cashWriteOff);
                }
            } else if (cashWriteOffByDocumentMsg.size() >= 1) {
                if(cashWriteOffByDocumentMsg.get(0).getStatus().equals(CashWriteOffStatus.P.toString())){
                    cashWriteOffDto.setWriteOffAmountBefore(cashWriteOffByDocumentMsg.get(0).getWriteOffAmount());
                }else{
                    cashWriteOffDto.setWriteOffAmountBefore(BigDecimal.ZERO);
                }
                cashWriteOffByDocumentMsg.forEach(e -> {
                    deleteCashWriteOffById(e.getId());
                });
                // 只有本次核销金额不为空，且大于零时，才插入核销表
                if(cashWriteOff.getWriteOffAmount() != null && cashWriteOff.getWriteOffAmount().compareTo(BigDecimal.ZERO) == 1){
                    createCashWriteOff(cashWriteOff);
                }
            }
            CashWriteOffAdapter.setDtoBasicsMessage(cashWriteOffDto,cashWriteOff);
            //回写支付平台 - 核销状态
            returnPaymentWriteOffAmount(cashWriteOff.getCshTransactionDetailId(),isComplete);
        });
    }

    /**
     * 核销 - 确认核销后，只保存核销记录
     * @param requestDto
     */
    @Transactional
    public void writeOff(CashWriteOffRequestWebDto requestDto){
        requestDto.getCashWriteOffMsg().stream().filter(e -> {
            return TypeConversionUtils.isNotEmpty(e.getWriteOffAmount());  //清除核销金额为空的数据
        }).forEach(cashWriteOffDto -> {
            if (requestDto.getDocumentLineAmount().compareTo(cashWriteOffDto.getWriteOffAmount()) == -1) {
                throw new BizException(RespCode.PAYMENT_WRITE_OFF_AMOUNT_GT_DOUCUMENT_AMOUNT);
            }
            if (cashWriteOffDto.getUnWriteOffAmount().compareTo(cashWriteOffDto.getWriteOffAmount()) == -1) {
                throw new BizException(RespCode.PAYMENT_WRITE_OFF_AMOUNT_GT_UN_WRITE_OFF_AMOUNT);
            }
            if (TypeConversionUtils.isEmpty(cashWriteOffDto.getWriteOffDate())) {
                cashWriteOffDto.setWriteOffDate(ZonedDateTime.now());
            }
            Long setOfBooksId = organizationService.getById(requestDto.getCompanyId()).getSetOfBooksId();
            if (TypeConversionUtils.isEmpty(cashWriteOffDto.getPeriodName())) {
                PeriodCO periodInfoBySetOfBooksId = organizationService.getPeriodBysetOfBooksIdAndDateTime(setOfBooksId, cashWriteOffDto.getWriteOffDate());
                if (TypeConversionUtils.isNotEmpty(periodInfoBySetOfBooksId)) {
                    cashWriteOffDto.setPeriodName(periodInfoBySetOfBooksId.getPeriodName());
                }else{
                    throw new BizException(RespCode.SYS_PERIOD_NOT_FOUND);
                }
            }
            CashWriteOff cashWriteOff = CashWriteOffAdapter.toDomain(cashWriteOffDto);
            cashWriteOff.setOperationType("WRITE_OFF");
            cashWriteOff.setSetOfBooksId(setOfBooksId);
            CashWriteOffAdapter.setDocumentFields(requestDto,cashWriteOff);
            //报账单头信息
            /*ExpenseReportHeaderDTO expPublicReportHead = null;
            if("PUBLIC_REPORT".equals(cashWriteOff.getDocumentType())){
                expPublicReportHead = expenseReportService.getExpPublicReportHeadById(cashWriteOff.getDocumentHeaderId());
                if(expPublicReportHead != null){
                    cashWriteOff.setDocumentNumber(TypeConversionUtils.parseString(expPublicReportHead.getBusinessCode()));
                    cashWriteOff.setDocumentApplicantId(TypeConversionUtils.parseLong(expPublicReportHead.getApplicationId()));
                    cashWriteOff.setDocumentCreatedDate(TypeConversionUtils.parseZonedDateTime(expPublicReportHead.getCreatedDate()));
                }
            }*/
            List<CashWriteOff> cashWriteOffByDocumentMsg = getCashWriteOffByDocumentMsg(cashWriteOff);
            //若单据以前核销过，先清除核销记录(若核销记录有多条，也一并清除)
            if (cashWriteOffByDocumentMsg == null || cashWriteOffByDocumentMsg.size() == 0) {
                cashWriteOffDto.setWriteOffAmountBefore(BigDecimal.ZERO);
                // 只有本次核销金额不为空，且大于零时，才插入核销表
                if(cashWriteOff.getWriteOffAmount() != null && cashWriteOff.getWriteOffAmount().compareTo(BigDecimal.ZERO) == 1){
                    createCashWriteOff(cashWriteOff);
                }
            } else if (cashWriteOffByDocumentMsg.size() >= 1) {
                cashWriteOffDto.setWriteOffAmountBefore(cashWriteOffByDocumentMsg.get(0).getWriteOffAmount());
                cashWriteOffByDocumentMsg.forEach(e -> {
                    deleteCashWriteOffById(e.getId());
                });
                // 只有本次核销金额不为空，且大于零时，才插入核销表
                if(cashWriteOff.getWriteOffAmount() != null && cashWriteOff.getWriteOffAmount().compareTo(BigDecimal.ZERO) == 1){
                    createCashWriteOff(cashWriteOff);
                }
            }
            CashWriteOffAdapter.setDtoBasicsMessage(cashWriteOffDto,cashWriteOff);
        });
    }

    /**
     * 单据提交，更新核销状态
     * @param documentType
     * @param documentHeaderId
     * @param documentLineIds
     * @param tenantId
     * @param lastUpdatedBy
     */
    /*@SyncLock(lockPrefix = SyncLockPrefix.CSH_WRITE_OFF)*/
    @Transactional
    public void writeOffTakeEffect(/*@LockedObject*/ String documentType,
                                   /*@LockedObject*/ Long documentHeaderId,
                                     List<Long> documentLineIds,
                                     Long tenantId,
                                     Long lastUpdatedBy){
        // 根据单据类型和单据头ID进项并发控制
        List<CashWriteOff> cashWriteOffByDocumentMsg = getCashWriteOffByDocumentMsg(documentType,
                documentHeaderId,
                documentLineIds,
                tenantId);
        cashWriteOffByDocumentMsg.stream().forEach(cashWriteOff -> {
            // 核销日期及核销期间 已提交单据为准
            cashWriteOff.setWriteOffDate(ZonedDateTime.now());
            PeriodCO periodInfoBySetOfBooksId = organizationService.getPeriodBysetOfBooksIdAndDateTime(cashWriteOff.getSetOfBooksId(), cashWriteOff.getWriteOffDate());
            if (TypeConversionUtils.isNotEmpty(periodInfoBySetOfBooksId)) {
                cashWriteOff.setPeriodName(periodInfoBySetOfBooksId.getPeriodName());
            }else{
                throw new BizException(RespCode.SYS_PERIOD_NOT_FOUND);
            }
            // 判断核销状态是否生效
            if(CashWriteOffStatus.N.toString().equals(cashWriteOff.getStatus()) || TypeConversionUtils.isEmpty(cashWriteOff.getStatus())){
                cashWriteOff.setStatus(CashWriteOffStatus.P.toString());
                this.updateAllColumnById(cashWriteOff);
            }else{
                throw new BizException(RespCode.SYS_REQUEST_FREQUENCY_TOO_FAST);
            }
            boolean isComplete = false;
            CashTransactionDetailWebDTO detailById = cashTransactionDetailService.getDetailById(cashWriteOff.getCshTransactionDetailId());
            if(detailById.getWriteOffAmount() != null){
                if (detailById.getWriteOffAmount().compareTo(cashWriteOff.getWriteOffAmount()) == -1) {
                    throw new BizException(RespCode.PAYMENT_WRITE_OFF_AMOUNT_GT_UN_WRITE_OFF_AMOUNT);
                }
                //未核销金额 = 本次核销金额 标记为完全核销
                if(detailById.getWriteOffAmount().compareTo(cashWriteOff.getWriteOffAmount()) == 0){
                    isComplete = true;
                }
            }
            //回写支付平台 - 核销状态
            returnPaymentWriteOffAmount(cashWriteOff.getCshTransactionDetailId(),isComplete);
        });
    }

    /**
     * 根据单据信息，获取核销记录
     * @param documentType
     * @param documentHeaderId
     * @param documentLineIds
     * @param tenantId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CashWriteOff> getCashWriteOffByDocumentMsg(String documentType,
                                                            Long documentHeaderId,
                                                            List<Long> documentLineIds,
                                                            Long tenantId){
        return baseMapper.selectList(new EntityWrapper<CashWriteOff>()
                .eq("document_type",documentType)
                .eq("document_header_id",documentHeaderId)
                .in(TypeConversionUtils.collectionIsNotEmpty(documentLineIds),"document_line_id",documentLineIds)
                .eq("tenant_id",tenantId)
        );
    }

    /**
     * 根据单据信息，回滚核销记录
     * 使本次核销金额为零，前核销记录为反冲金额，也就可以反冲了
     * @param documentType
     * @param documentHeaderId
     * @param documentLineIds
     * @param tenantId
     * @param lastUpdatedBy
     */
    /*@SyncLock(lockPrefix = SyncLockPrefix.CSH_WRITE_OFF)*/
    @Transactional
    public void writeOffRollback(/*@LockedObject*/ String documentType,
                                 /*@LockedObject*/ Long documentHeaderId,
                                 List<Long> documentLineIds,
                                 Long tenantId,
                                 Long lastUpdatedBy){
        List<CashWriteOff> cashWriteOffByDocumentMsg = getCashWriteOffByDocumentMsg(documentType,
                documentHeaderId,
                documentLineIds,
                tenantId);
        cashWriteOffByDocumentMsg.stream().forEach(cashWriteOff -> {
            // 判断核销状态是否生效
            if(CashWriteOffStatus.P.toString().equals(cashWriteOff.getStatus())){
                cashWriteOff.setStatus(CashWriteOffStatus.N.toString());
                updateById(cashWriteOff);
            }else{
                throw new BizException(RespCode.SYS_REQUEST_FREQUENCY_TOO_FAST);
            }
            //回写支付平台 - 核销状态 - 直接标记为未完全核销
            returnPaymentWriteOffAmount(cashWriteOff.getCshTransactionDetailId(),false);
        });
    }

    /**
     * 回写支付平台 - 核销状态
     * @param cshTransactionDetailId 支付明细id
     * @param isComplete 是否完全核销
     */
    public void returnPaymentWriteOffAmount(Long cshTransactionDetailId,Boolean isComplete){

    }

    /**
     * 获取计划付款行核销金额
     * @param documentType
     * @param documentHeaderId
     * @param documentLineId
     * @param tenantId
     * @return
     */
    public Map<Long,BigDecimal> listDocumentWriteOffAmount(String documentType,
                                                      Long documentHeaderId,
                                                      Long documentLineId,
                                                      Long tenantId){
        List<Long> documentLineIds = null;
        if(documentLineId != null){
            documentLineIds = Arrays.asList(documentLineId);
        }
        List<CashWriteOff> cashWriteOffByDocumentMsg = baseMapper.selectList(new EntityWrapper<CashWriteOff>()
                .eq("document_type",documentType)
                .eq("document_header_id",documentHeaderId)
                .in(TypeConversionUtils.collectionIsNotEmpty(documentLineIds),"document_line_id",documentLineIds)
                .eq("tenant_id",tenantId)
                .notExists("select 1 from dual where operation_type = 'WRITE_OFF_RESERVED' and (status in ('N','P') or status is null)")
        );
        Map<Long, Double> collect = cashWriteOffByDocumentMsg.stream().collect(Collectors.groupingBy(
                CashWriteOff::getDocumentLineId,
                Collectors.summingDouble(value -> value.getWriteOffAmount().doubleValue())));
        Map<Long,BigDecimal> result = new HashMap<>();
        collect.entrySet()
                .stream()
                .forEach(longDoubleEntry ->
                        result.put(longDoubleEntry.getKey(), BigDecimal.valueOf(longDoubleEntry.getValue())));
        return result;
    }

    /**
     * 核销记录生成凭证
     * @param documentType            单据类型
     * @param documentHeaderId        单据头ID
     * @param documentLineIds         单据行ID(若不传值则与单据相关核销记录全部生成凭证)
     * @param tenantId                租户ID
     * @param operatorId              操作人ID
     * @return
     */
    /*@SyncLock(lockPrefix = SyncLockPrefix.CSH_WRITE_OFF)*/
    public String writeOffCreateJournalLines(/*@LockedObject*/ String documentType,
                                             /*@LockedObject*/ Long documentHeaderId,
                                             List<Long> documentLineIds,
                                             /*@LockedObject*/ Long tenantId,
                                             Long operatorId,
                                             ZonedDateTime accountDate,
                                             String accountPeriod,
                                             String operationType){
        Boolean flag = false;
        // 判断当前登陆人所在机构是否允许支付往核算模块发送数据
//        FunctionProfile functionProfile = OrganizationInterface.getFunctionProfile(OrgInformationUtil.getCurrentCompanyOID());
//        if (functionProfile != null){
//            if (functionProfile.getProfileDetail() != null && functionProfile.getProfileDetail().size() > 0){
//                flag = TypeConversionUtils.parseBoolean(functionProfile.getProfileDetail().get("payment.accounting.enabled"));
//            }
//        }
        List<CashWriteOff> cashWriteOffByDocumentMsg = baseMapper.selectList(new EntityWrapper<CashWriteOff>()
                .eq("document_type",documentType)
                .eq("document_header_id",documentHeaderId)
                .in(TypeConversionUtils.collectionIsNotEmpty(documentLineIds),"document_line_id",documentLineIds)
                .eq("tenant_id",tenantId)
                .eq("status",CashWriteOffStatus.P.name())
                .eq("operation_type",operationType));
//        if(!flag){
//            cashWriteOffByDocumentMsg.stream().forEach(writeOffDetail -> {
//                changeWriteOffStatus(writeOffDetail.getId(),"Y",operatorId,true);
//            });
//            return "NO_NEED_ACCOUNT";
//        }
        if(CollectionUtils.isNotEmpty(cashWriteOffByDocumentMsg)){
            WriteOffInterfaceCO writeOffInterface = new WriteOffInterfaceCO();
            writeOffInterface.setCreatedBy(operatorId);
            Map<Long, ExpensePaymentScheduleCO> collect = expenseService.getExpPublicReportScheduleByIds(cashWriteOffByDocumentMsg.stream()
                    .map(CashWriteOff::getDocumentLineId).collect(Collectors.toList())).stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
            List<WriteOffDetailCO> writeOffDetails = cashWriteOffByDocumentMsg.stream().map(cashWriteOff -> {
                //支付明细信息
                CashTransactionDetail cashTransactionDetail = cashTransactionDetailService.selectById(cashWriteOff.getCshTransactionDetailId());
                //预付款头信息
                CashPaymentRequisitionHeaderCO prepaymentRequisitionHead = prepaymentService.getCashPaymentRequisitionHeadById(cashTransactionDetail.getDocumentId());
                //预付款行信息
                CashPaymentRequisitionLineCO prepaymentRequisitionLine = prepaymentService.getCashPaymentRequisitionLineById(cashTransactionDetail.getDocumentLineId());
                //报账单头信息
                /*ExpenseReportHeaderCO expPublicReportHead = null;
                if("PUBLIC_REPORT".equals(cashWriteOff.getDocumentType())){
                    expPublicReportHead = expenseReportService.getExpPublicReportHeadById(cashWriteOff.getDocumentHeaderId());
                }*/
                //报账单行信息
                /*ExpensePaymentScheduleDTO expPublicReportSchedule = null;

                if("PUBLIC_REPORT".equals(cashWriteOff.getDocumentType())){
                    expPublicReportSchedule = expenseReportService.getExpPublicReportScheduleById(cashWriteOff.getDocumentLineId());
                }

                if(prepaymentRequisitionHead == null || prepaymentRequisitionLine == null){
                    throw new BizException(RespCode.PAYMENT_WRITE_OFF_PREPAYMENT_REQUISITION_NOT_FOUND);
                }
                if(expPublicReportHead == null || expPublicReportSchedule == null){
                    throw new BizException(RespCode.PAYMENT_WRITE_OFF_PUBLIC_EXPORT_NOT_FOUND);
                }*/
                WriteOffDetailCO writeOffDetail = new WriteOffDetailCO();
                writeOffDetail.setId(cashWriteOff.getId());
                writeOffDetail.setTenantId(cashWriteOff.getTenantId());
                writeOffDetail.setSetOfBooksId(cashWriteOff.getSetOfBooksId());
                writeOffDetail.setOperationType(cashWriteOff.getOperationType());
                writeOffDetail.setCshTransactionDetailId(cashWriteOff.getCshTransactionDetailId());
                writeOffDetail.setCshTransactionDetailNumber(cashTransactionDetail.getBillcode());
                writeOffDetail.setPrepaymentCurrency(cashTransactionDetail.getCurrency());
                writeOffDetail.setPrepaymentCompanyId(TypeConversionUtils.parseLong(prepaymentRequisitionHead.getCompanyId()));
                writeOffDetail.setPrepaymentUnitId(TypeConversionUtils.parseLong(prepaymentRequisitionHead.getUnitId()));
                writeOffDetail.setPrepaymentPaymentMethodCategory(TypeConversionUtils.parseString(prepaymentRequisitionLine.getPaymentMethodCategory()));
                writeOffDetail.setPrepaymentPaymentTypeId(cashTransactionDetail.getPaymentTypeId());
                writeOffDetail.setPrepaymentTransactionClassId(cashTransactionDetail.getCshTransactionClassId());
                writeOffDetail.setPrepaymentExchangeRate(cashTransactionDetail.getExchangeRate());
                writeOffDetail.setPrepaymentPartnerCategory(cashTransactionDetail.getPartnerCategory());
                writeOffDetail.setPrepaymentPartnerId(cashTransactionDetail.getPartnerId());
                writeOffDetail.setPrepaymentPartnerCode(cashTransactionDetail.getPartnerCode());
                writeOffDetail.setDocumentType(cashWriteOff.getDocumentType());
                ExpensePaymentScheduleCO expensePaymentScheduleCO = collect.get(cashWriteOff.getDocumentLineId());
                writeOffDetail.setDocumentCurrency(expensePaymentScheduleCO.getCurrencyCode());
                writeOffDetail.setDocumentNumber(cashWriteOff.getDocumentNumber());
                writeOffDetail.setDocumentHeaderId(cashWriteOff.getDocumentHeaderId());
                writeOffDetail.setDocumentLineId(cashWriteOff.getDocumentLineId());
                writeOffDetail.setDocumentCompanyId(expensePaymentScheduleCO.getCompanyId());
                writeOffDetail.setDocumentUnitId(expensePaymentScheduleCO.getDepartmentId());
                writeOffDetail.setDocumentExchangeRate(TypeConversionUtils.parseDouble(expensePaymentScheduleCO.getExchangeRate()));
                writeOffDetail.setDocumentPaymentMethodCategory(expensePaymentScheduleCO.getPaymentMethod());
                writeOffDetail.setDocumentTransactionClassId(expensePaymentScheduleCO.getCshTransactionClassId());
                writeOffDetail.setDocumentPartnerCategory(expensePaymentScheduleCO.getPayeeCategory());
                writeOffDetail.setDocumentPartnerId(expensePaymentScheduleCO.getPayeeId());
                if("VENDER".equals(expensePaymentScheduleCO.getPayeeCategory())){
                    VendorInfoCO venInfoCO = supplierService.getOneVendorInfoByArtemis(expensePaymentScheduleCO.getPayeeId().toString());
                    writeOffDetail.setDocumentPartnerCode(venInfoCO.getVenderCode());
                }else {
                    ContactCO userById = organizationService.getUserById(expensePaymentScheduleCO.getPayeeId());
                    writeOffDetail.setDocumentPartnerCode(userById.getEmployeeCode());
                }
                writeOffDetail.setAmount(cashWriteOff.getWriteOffAmount());
                writeOffDetail.setWriteOffDate(cashWriteOff.getWriteOffDate());
                writeOffDetail.setWriteOffPeriod(cashWriteOff.getPeriodName());
                writeOffDetail.setAccountDate(accountDate);
                if(TypeConversionUtils.isEmpty(accountPeriod)){
                    PeriodCO periodInfoBySetOfBooksId = organizationService.getPeriodBysetOfBooksIdAndDateTime(cashWriteOff.getSetOfBooksId(), accountDate);
                    if (TypeConversionUtils.isNotEmpty(periodInfoBySetOfBooksId)) {
                        writeOffDetail.setAccountPeriod(periodInfoBySetOfBooksId.getPeriodName());
                    }else{
                        throw new BizException(RespCode.SYS_PERIOD_NOT_FOUND);
                    }
                }else{
                    writeOffDetail.setAccountPeriod(accountPeriod);
                }
                // 核销反冲备注去 核销行上的备注信息
                if("WRITE_OFF".equals(cashWriteOff.getOperationType())){
                    /*writeOffDetail.setRemark(TypeConversionUtils.parseString(expPublicReportSchedule.getDescription()));*/
                }else if("WRITE_OFF_RESERVED".equals(cashWriteOff.getOperationType())){
                    writeOffDetail.setRemark(cashWriteOff.getRemark());
                }
                return writeOffDetail;
            }).collect(Collectors.toList());
            writeOffInterface.setWriteOffDetail(writeOffDetails);
            // bo.liu 核算
            /*String result = accountingService.saveInitializeWriteOffGeneralLedgerJournalLine(writeOffInterface);
            if("SUCCESS".equals(result)){
                writeOffDetails.stream().forEach(writeOffDetail -> {
                    if("WRITE_OFF".equals(writeOffDetail.getOperationType())){
                        changeWriteOffStatus(writeOffDetail.getId(),null,operatorId,true);
                    // 为核销反冲时，不需要
                    }else if("WRITE_OFF_RESERVED".equals(writeOffDetail.getOperationType())){
                        changeWriteOffStatus(writeOffDetail.getId(),null,operatorId,true);
                    }
                });
            }
            return result;*/
        }

        return "NO_WRITE_OFF_DATA";
    }

    /**
     * 修改核销记录状态
     * @param id
     * @param status
     * @param operatorId
     */
    private void changeWriteOffStatus(Long id, String status, Long operatorId,boolean isAccount){
        CashWriteOff cashWriteOff = this.selectById(id);
        if (cashWriteOff == null) {
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        cashWriteOff.setStatus(status);
        if(isAccount){
            cashWriteOff.setIsAccount("Y");
        }
        this.updateById(cashWriteOff);
    }

    /**
     * 根据单据信息获取核销金额
     * @param documentCategory   单据大类
     * @param documentId         单据头ID
     * @param documentLineId     单据行ID
     * @return Map<Long,Double>   key为 documentLineId，value为核销金额
     */
    public Map<Long,BigDecimal> listPaidDocumentWriteOffAmount(String documentCategory,
                                                         Long documentId,
                                                         Long documentLineId){
        Map<Long, Map> longObjectMap = baseMapper.selectDocumentWriteOffAmount(documentCategory, documentId, documentLineId);
        Map<Long,BigDecimal> result = new HashMap();
        longObjectMap.keySet().stream().forEach(key -> {
            result.put(key,BigDecimal.valueOf(TypeConversionUtils.parseDouble(longObjectMap.get(key).get("amount"))));
        });
        return result;
    }

    /**
     * 待反冲核销记录查询
     * @param documentNumber    报账单编号
     * @param applicantId       单据申请人
     * @param sourceDocumentNumber  被核销单据编号(预付款单)
     * @param billCode          支付流水号
     * @param createdDateFrom    单据创建日期从
     * @param createdDateTo      单据创建日期至
     * @param writeOffAmountFrom 核销金额从
     * @param writeOffAmountTo   核销金额至
     * @param writeOffDateFrom   核销反冲日期从
     * @param writeOffDateTo     核销反冲日期至
     * @return
     */
    public List<CashWriteOffReserveDTO> getWaitingReserveWriteOffDetail(String documentNumber,
                                                                        Long applicantId,
                                                                        String sourceDocumentNumber,
                                                                        String billCode,
                                                                        String createdDateFrom,
                                                                        String createdDateTo,
                                                                        BigDecimal writeOffAmountFrom,
                                                                        BigDecimal writeOffAmountTo,
                                                                        String writeOffDateFrom,
                                                                        String writeOffDateTo,
                                                                        Page page){
        List<CashWriteOffReserveDTO> cashWriteOffReserveDTOS = baseMapper.selectWaitingReserveWriteOffDetail(OrgInformationUtil.getCurrentSetOfBookId(),
                documentNumber,
                applicantId,
                sourceDocumentNumber,
                billCode,
                TypeConversionUtils.getStartTimeForDayYYMMDD(createdDateFrom),
                TypeConversionUtils.getEndTimeForDayYYMMDD(createdDateTo),
                writeOffAmountFrom,
                writeOffAmountTo,
                TypeConversionUtils.getStartTimeForDayYYMMDD(writeOffDateFrom),
                TypeConversionUtils.getEndTimeForDayYYMMDD(writeOffDateTo),
                page);
        setDocumentMessage(cashWriteOffReserveDTOS);
        return cashWriteOffReserveDTOS;
    }

    /**
     * 获取用户发起的反冲记录
     * @param documentNumber    报账单编号
     * @param applicantId       申请人
     * @param sourceDocumentNumber  被核销单据编号(预付款单)
     * @param billCode          支付流水号
     * @param createdDateFrom    单据创建日期从
     * @param createdDateTo      单据创建日期至
     * @param writeOffAmountFrom 核销金额从
     * @param writeOffAmountTo   核销金额至
     * @param status             核销状态
     * @param approvalId         复核人ID
     * @param writeOffReverseAmountFrom 核销反冲金额从
     * @param writeOffReverseAmountTo 核销反冲金额至
     * @param writeOffDateFrom 核销反冲日期从
     * @param writeOffDateTo 核销反冲日期至
     * @param page
     * @return
     */
    public List<CashWriteOffReserveDTO> getUserReservedWriteOffDetail(String documentNumber,
                                                                      Long applicantId,
                                                                      String sourceDocumentNumber,
                                                                      String billCode,
                                                                      String createdDateFrom,
                                                                      String createdDateTo,
                                                                      BigDecimal writeOffAmountFrom,
                                                                      BigDecimal writeOffAmountTo,
                                                                      String status,
                                                                      Long approvalId,
                                                                      BigDecimal writeOffReverseAmountFrom,
                                                                      BigDecimal writeOffReverseAmountTo,
                                                                      String writeOffDateFrom,
                                                                      String writeOffDateTo,
                                                                      Page page){
        List<CashWriteOffReserveDTO> cashWriteOffReserveDTOS = baseMapper.selectReservedWriteOffDetail(OrgInformationUtil.getCurrentSetOfBookId(),
                documentNumber,
                applicantId,
                sourceDocumentNumber,
                billCode,
                TypeConversionUtils.getStartTimeForDayYYMMDD(createdDateFrom),
                TypeConversionUtils.getEndTimeForDayYYMMDD(createdDateTo),
                writeOffAmountFrom,
                writeOffAmountTo,
                status,
                approvalId,
                writeOffReverseAmountFrom,
                writeOffReverseAmountTo,
                TypeConversionUtils.getStartTimeForDayYYMMDD(writeOffDateFrom),
                TypeConversionUtils.getEndTimeForDayYYMMDD(writeOffDateTo),
                OrgInformationUtil.getCurrentUserId(),
                null,
                page);
        setDocumentMessage(cashWriteOffReserveDTOS);
        return cashWriteOffReserveDTOS;
    }

    /**
     * 待复核的核销反冲记录
     * @param documentNumber    报账单编号
     * @param applicantId       申请人
     * @param sourceDocumentNumber  被核销单据编号(预付款单)
     * @param billCode          支付流水号
     * @param createdDateFrom    单据创建日期从
     * @param createdDateTo      单据创建日期至
     * @param writeOffAmountFrom 核销金额从
     * @param writeOffAmountTo   核销金额至
     * @param status             单据状态
     * @param approvalId         复核人ID
     * @param writeOffReverseAmountFrom 核销反冲金额从
     * @param writeOffReverseAmountTo 核销反冲金额至
     * @param writeOffDateFrom 核销日期从
     * @param writeOffDateTo 核销日期至
     * @param createdBy          反冲发起人ID
     * @param page
     * @return
     */
    public List<CashWriteOffReserveDTO> getRecheckReservedWriteOffDetail(String documentNumber,
                                                                         Long applicantId,
                                                                         String sourceDocumentNumber,
                                                                         String billCode,
                                                                         String createdDateFrom,
                                                                         String createdDateTo,
                                                                         BigDecimal writeOffAmountFrom,
                                                                         BigDecimal writeOffAmountTo,
                                                                         String status,
                                                                         Long approvalId,
                                                                         BigDecimal writeOffReverseAmountFrom,
                                                                         BigDecimal writeOffReverseAmountTo,
                                                                         String writeOffDateFrom,
                                                                         String writeOffDateTo,
                                                                         Long createdBy,
                                                                         Page page){
        List<CashWriteOffReserveDTO> cashWriteOffReserveDTOS = baseMapper.selectReservedWriteOffDetail(OrgInformationUtil.getCurrentSetOfBookId(),
                documentNumber,
                applicantId,
                sourceDocumentNumber,
                billCode,
                TypeConversionUtils.getStartTimeForDayYYMMDD(createdDateFrom),
                TypeConversionUtils.getEndTimeForDayYYMMDD(createdDateTo),
                writeOffAmountFrom,
                writeOffAmountTo,
                status,
                approvalId,
                writeOffReverseAmountFrom,
                writeOffReverseAmountTo,
                TypeConversionUtils.getStartTimeForDayYYMMDD(writeOffDateFrom),
                TypeConversionUtils.getEndTimeForDayYYMMDD(writeOffDateTo),
                createdBy,
                null,
                page);
        setDocumentMessage(cashWriteOffReserveDTOS);
        return cashWriteOffReserveDTOS;
    }

    /**
     * 设置单据相关信息
     * @param cashWriteOffReserveDTOS
     */
    private void setDocumentMessage(List<CashWriteOffReserveDTO> cashWriteOffReserveDTOS){
        if(CollectionUtils.isNotEmpty(cashWriteOffReserveDTOS)) {
            // 报账单计划付款行信息
            List<Long> documentLineIds = cashWriteOffReserveDTOS.stream().map(cashWriteOffReserveDTO -> {
                return cashWriteOffReserveDTO.getDocumentLineId();
            }).collect(Collectors.toList());
           /*List<ExpensePaymentScheduleDTO> expPublicReportScheduleByIds = expenseReportService.getExpPublicReportScheduleByIds(documentLineIds);*/

            // 被核销信息(预付款)
            List<Long> sourceDocumentLineIds = cashWriteOffReserveDTOS.stream().map(cashWriteOffReserveDTO -> {
                return cashWriteOffReserveDTO.getSourceDocumentLineId();
            }).collect(Collectors.toList());
            List<CashPaymentRequisitionLineCO> prepaymentRequisitionLineByIds = prepaymentService.listCashPaymentRequisitionLineById(sourceDocumentLineIds);
            // 用户信息 + 合同信息
            Set<Long> userIdSet = new HashSet<>();
            Set<Long> contractIdSet = new HashSet<Long>();
            cashWriteOffReserveDTOS.stream().forEach(cashWriteOffReserveDTO -> {
                if (cashWriteOffReserveDTO.getApprovalId() != null) {
                    userIdSet.add(cashWriteOffReserveDTO.getApprovalId());
                }
                if (cashWriteOffReserveDTO.getDocumentApplicantId() != null) {
                    userIdSet.add(cashWriteOffReserveDTO.getDocumentApplicantId());
                }
                userIdSet.add(cashWriteOffReserveDTO.getCreatedBy());
            });
            /*expPublicReportScheduleByIds.stream().forEach(expPublicReportSchedule -> {
                if (expPublicReportSchedule.getPayeeId() != null
                        && "EMPLOYEE".equals(TypeConversionUtils.parseString(expPublicReportSchedule.getPayeeCategory()))) {
                    userIdSet.add(TypeConversionUtils.parseLong(expPublicReportSchedule.getPayeeId()));
                }
                if (expPublicReportSchedule.getContractHeaderId() != null) {
                    contractIdSet.add(TypeConversionUtils.parseLong(expPublicReportSchedule.getContractHeaderId()));
                }
            });*/
            List<ContactCO> users = new ArrayList<>();
            if (userIdSet.size() > 0) {
                users.addAll(organizationService.listByUserIds(userIdSet.stream().collect(Collectors.toList())));
            }
            List<ContractHeaderCO> contractHeaderCOs = new ArrayList<>();
            if (contractIdSet.size() > 0) {
                //bo.liu 合同
                /*contractHeaderCOs.addAll(contractService.listContractHeadersByIds(contractIdSet.stream().collect(Collectors.toList())));*/
            }

            // 获取环境语言信息
            cashWriteOffReserveDTOS.forEach(cashWriteOffReserveDTO -> {
                cashWriteOffReserveDTO.setCashWriteOffReservePrepaymentRequisition(prepaymentRequisitionLineByIds.stream().filter(prepaymentRequisitionLine -> {
                    return cashWriteOffReserveDTO.getSourceDocumentLineId().equals(TypeConversionUtils.parseLong(prepaymentRequisitionLine.getId()));
                }).map(prepaymentRequisitionLine -> {
                    CashWriteOffReservePrepaymentRequisitionDTO dto = new CashWriteOffReservePrepaymentRequisitionDTO();
                    mapperFacade.map(prepaymentRequisitionLine,dto);
                    return dto;
                }).collect(Collectors.toList()).get(0));

                /*cashWriteOffReserveDTO.setCashWriteOffReserveExpReport(expPublicReportScheduleByIds.stream().filter(expPublicReportSchedule -> {
                    return cashWriteOffReserveDTO.getDocumentLineId().equals(TypeConversionUtils.parseLong(expPublicReportSchedule.getId()));
                }).map(expPublicReportSchedule -> {
                    CashWriteOffReserveExpReportDTO dto = new CashWriteOffReserveExpReportDTO();
                    mapperFacade.map(expPublicReportSchedule,dto);
                    if (dto.getPayeeId() != null) {
                        if("VENDER".equals(dto.getPayeeCategory())){
                            VendorInfoCO venInfoCO = supplierService.getOneVendorInfoByArtemis(dto.getPayeeId().toString());
                            dto.setPayeeName(venInfoCO.getVenNickname());
                        }else {
                            dto.setPayeeName(users.stream().filter(user -> {
                                return user.getId().equals(dto.getPayeeId());
                            }).collect(Collectors.toList()).get(0).getFullName());
                        }
                    }
                    if (dto.getContractHeaderId() != null) {
                        dto.setContractHeaderNumber(TypeConversionUtils.parseString(contractHeaderDTOS.stream().filter(contractHeader -> {
                            return dto.getContractHeaderId().equals(TypeConversionUtils.parseLong(contractHeader.getId()));
                        }).collect(Collectors.toList()).get(0).getContractNumber()));
                    }
                    return dto;
                }).collect(Collectors.toList()).get(0));*/

                ContactCO userInfoCO = users.stream().filter(user -> {
                    return user.getId().equals(cashWriteOffReserveDTO.getCreatedBy());
                }).collect(Collectors.toList()).get(0);
                cashWriteOffReserveDTO.setCreatedCode(userInfoCO.getEmployeeCode());
                cashWriteOffReserveDTO.setCreatedName(userInfoCO.getFullName());
                if (cashWriteOffReserveDTO.getApprovalId() != null) {
                    cashWriteOffReserveDTO.setApprovalName(users.stream().filter(user -> {
                        return user.getId().equals(cashWriteOffReserveDTO.getApprovalId());
                    }).collect(Collectors.toList()).get(0).getFullName());
                }
                if (cashWriteOffReserveDTO.getDocumentApplicantId() != null) {
                    cashWriteOffReserveDTO.setDocumentApplicantName(users.stream().filter(user -> {
                        return user.getId().equals(cashWriteOffReserveDTO.getDocumentApplicantId());
                    }).collect(Collectors.toList()).get(0).getFullName());
                }
//                String languageCN = Locale.CHINA.getLanguage() + "_" + Locale.CHINA.getCountry();
                if ("WRITE_OFF_RESERVED".equals(cashWriteOffReserveDTO.getOperationType())) {
                    if ("P".equals(cashWriteOffReserveDTO.getStatus())) {
//                        if (locale.getLanguage().equalsIgnoreCase(languageCN)) {
//                            cashWriteOffReserveDTO.setStatusDescription("复核中");
//                        } else {
//                            cashWriteOffReserveDTO.setStatusDescription("RECHECKING");
//                        }
                        cashWriteOffReserveDTO.setStatusDescription(messageService.getMessageDetailByCode(RespCode.WRITE_OFF_RESERVED_STATUS_P));
                    } else if ("Y".equals(cashWriteOffReserveDTO.getStatus())) {
//                        if (locale.getLanguage().equalsIgnoreCase(languageCN)) {
//                            cashWriteOffReserveDTO.setStatusDescription("已复核");
//                        } else {
//                            cashWriteOffReserveDTO.setStatusDescription("RECHECKED");
//                        }
                        cashWriteOffReserveDTO.setStatusDescription(messageService.getMessageDetailByCode(RespCode.WRITE_OFF_RESERVED_STATUS_Y));
                    } else {
//                        if (locale.getLanguage().equalsIgnoreCase(languageCN)) {
//                            cashWriteOffReserveDTO.setStatusDescription("驳回");
//                        } else {
//                            cashWriteOffReserveDTO.setStatusDescription("REJECTED");
//                        }
                        cashWriteOffReserveDTO.setStatusDescription(messageService.getMessageDetailByCode(RespCode.WRITE_OFF_RESERVED_STATUS_N));
                    }
                }
                if("WRITE_OFF".equals(cashWriteOffReserveDTO.getOperationType())){
                    List<CashWriteOffReserveDTO> cashWriteOffReverseHistory = getCashWriteOffReverseHistory(cashWriteOffReserveDTO.getId());
                    cashWriteOffReserveDTO.setCashWriteOffReverseHistory(cashWriteOffReverseHistory);
                }
                if(TypeConversionUtils.isNotEmpty(cashWriteOffReserveDTO.getAttachmentOid())){
                    List<String> attachmentOIDs = Arrays.asList(cashWriteOffReserveDTO.getAttachmentOid().split(","));
                    List<AttachmentCO> attachmentByOIDS = organizationService.listByOids(attachmentOIDs);
                    cashWriteOffReserveDTO.setAttachments(attachmentByOIDS);
                }else{
                    cashWriteOffReserveDTO.setAttachments(Arrays.asList());
                }
            });
        }
    }

    /**
     * 根据核销记录，获取核销反冲历史记录
     * @param sourceId   核销记录ID
     * @return
     */
    public List<CashWriteOffReserveDTO> getCashWriteOffReverseHistory(Long sourceId){
        List<CashWriteOffReserveDTO> cashWriteOffReserveDTOS = baseMapper.selectReservedWriteOffDetail(OrgInformationUtil.getCurrentSetOfBookId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                sourceId,
                null,
                null);
        setDocumentMessage(cashWriteOffReserveDTOS);
        return cashWriteOffReserveDTOS;
    }

    /**
     * 发起核销反冲
     * @param id    核销记录ID
     * @param reverseAmount   反冲金额
     * @param remark   备注
     * @param attachmentOid  附件OID
     */
    /*@SyncLock(lockPrefix = SyncLockPrefix.CSH_WRITE_OFF_REVERSE, waiting = true)*/
    public void doCashWriteOffReverse(/*@LockedObject*/ Long id,
                                      BigDecimal reverseAmount,
                                      String remark,
                                      String attachmentOid){
        CashWriteOff cashWriteOff = baseMapper.selectById(id);
        List<CashWriteOffReserveDTO> cashWriteOffReverseHistory = baseMapper.selectReservedWriteOffDetail(OrgInformationUtil.getCurrentSetOfBookId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                id,
                null,
                null);
        // 已反冲金额
        BigDecimal reversedAmount = reverseAmount;
        for(CashWriteOffReserveDTO cashWriteOffReserveDTO : cashWriteOffReverseHistory){
            reversedAmount = reversedAmount.add(cashWriteOffReserveDTO.getReversedAmount());
        }
        if(cashWriteOff.getWriteOffAmount().compareTo(reversedAmount) == -1){
            throw new BizException(RespCode.PAYMENT_WRITE_OFF_REVERSE_AMOUNT_TOO_BIG);
        }
        cashWriteOff.setId(null);
        cashWriteOff.setSourceWriteOffId(id);
        cashWriteOff.setStatus(CashWriteOffStatus.P.name());
        cashWriteOff.setWriteOffAmount(OperationUtil.safeMultiply(reverseAmount, BigDecimal.valueOf(-1)));
        cashWriteOff.setRemark(remark);
        cashWriteOff.setOperationType("WRITE_OFF_RESERVED");
        cashWriteOff.setWriteOffDate(ZonedDateTime.now());
        PeriodCO periodInfoBySetOfBooksId = organizationService.getPeriodBysetOfBooksIdAndDateTime(cashWriteOff.getSetOfBooksId(), cashWriteOff.getWriteOffDate());
        if (TypeConversionUtils.isNotEmpty(periodInfoBySetOfBooksId)) {
            cashWriteOff.setPeriodName(periodInfoBySetOfBooksId.getPeriodName());
        }else{
            throw new BizException(RespCode.SYS_PERIOD_NOT_FOUND);
        }
        cashWriteOff.setAttachmentOid(attachmentOid);
        baseMapper.insert(cashWriteOff);
    }

    /**
     * 核销反冲记录作废
     * @param id   核销反冲ID
     */
    public void cancelCashWriteOffReverse(Long id){
        CashWriteOff cashWriteOff = baseMapper.selectById(id);
        if(CashWriteOffStatus.P.name().equals(cashWriteOff.getStatus()) || CashWriteOffStatus.Y.name().equals(cashWriteOff.getStatus())){
            throw new BizException(RespCode.PAYMENT_WRITE_OFF_REVERSE_ERROR);
        }
        baseMapper.deleteById(id);
    }

    /**
     * 重新发起核销反冲
     * @param id       核销反冲ID
     * @param reverseAmount  反冲金额
     * @param remark  备注
     * @param attachmentOid  附件OID
     */
    /*@SyncLock(lockPrefix = SyncLockPrefix.CSH_WRITE_OFF_REVERSE, waiting = true)*/
    public void doCashWriteOffReverseAgain(/*@LockedObject*/ Long id,
                                           BigDecimal reverseAmount,
                                           String remark,
                                           String attachmentOid){
        CashWriteOff cashWriteOff = baseMapper.selectById(id);
        if(CashWriteOffStatus.P.name().equals(cashWriteOff.getStatus()) || CashWriteOffStatus.Y.name().equals(cashWriteOff.getStatus())){
            throw new BizException(RespCode.PAYMENT_WRITE_OFF_REVERSE_REPETITIVE_OPERATION);
        }
        CashWriteOff sourceCashWriteOff = baseMapper.selectById(cashWriteOff.getSourceWriteOffId());
        List<CashWriteOffReserveDTO> cashWriteOffReverseHistory = getCashWriteOffReverseHistory(cashWriteOff.getSourceWriteOffId());
        // 已反冲金额
        BigDecimal reversedAmount = reverseAmount.abs();
        for(CashWriteOffReserveDTO cashWriteOffReserveDTO : cashWriteOffReverseHistory){
            reversedAmount = OperationUtil.sum(reversedAmount,cashWriteOffReserveDTO.getReversedAmount());
        }
        if(OperationUtil.subtract(sourceCashWriteOff.getWriteOffAmount(),reversedAmount).compareTo(BigDecimal.ZERO) == -1){
            throw new BizException(RespCode.PAYMENT_WRITE_OFF_REVERSE_AMOUNT_TOO_BIG);
        }
        cashWriteOff.setStatus(CashWriteOffStatus.P.name());
        cashWriteOff.setWriteOffAmount(OperationUtil.safeMultiply(reverseAmount,BigDecimal.valueOf(-1)));
        cashWriteOff.setWriteOffDate(ZonedDateTime.now());
        PeriodCO periodInfoBySetOfBooksId = organizationService.getPeriodBysetOfBooksIdAndDateTime(cashWriteOff.getSetOfBooksId(), cashWriteOff.getWriteOffDate());
        if (TypeConversionUtils.isNotEmpty(periodInfoBySetOfBooksId)) {
            cashWriteOff.setPeriodName(periodInfoBySetOfBooksId.getPeriodName());
        }else{
            throw new BizException(RespCode.SYS_PERIOD_NOT_FOUND);
        }
        // 所有时间需要全部刷新
        cashWriteOff.setRemark(remark);
        cashWriteOff.setApprovalOpinions(null);
        cashWriteOff.setApprovalId(null);
        cashWriteOff.setAttachmentOid(attachmentOid);
        this.updateAllColumnById(cashWriteOff);
    }

    /**
     * 核销反冲复核
     * @param id    核销反冲ID
     * @param operationType 操作类型 1:同意 -1:拒绝
     * @param approvalOpinions 复核意见
     */
    /*@SyncLock(lockPrefix = SyncLockPrefix.CSH_WRITE_OFF_REVERSE, waiting = true)*/
    public void cashWriteOffReverseRecheck(/*@LockedObject*/ Long id,
                                             Integer operationType,
                                           String approvalOpinions){
        CashWriteOff cashWriteOff = baseMapper.selectById(id);
        if(! CashWriteOffStatus.P.name().equals(cashWriteOff.getStatus())){
            throw new BizException(RespCode.PAYMENT_WRITE_OFF_REVERSE_REPETITIVE_OPERATION);
        }
        // 需要生成凭证，但IsAccount不为Y时，表示未生成凭证
        if(judgePostAccountingService(OrgInformationUtil.getCurrentCompanyOid()) && !"Y".equals(cashWriteOff.getIsAccount())){
            throw new BizException(RespCode.PAYMENT_WRITE_OFF_PLEASE_CREATE_ACCOUNT);
        }
        cashWriteOff.setId(id);
        cashWriteOff.setApprovalId(OrgInformationUtil.getCurrentUserId());
        cashWriteOff.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        cashWriteOff.setLastUpdatedDate(ZonedDateTime.now());
        if(operationType.equals(1)){
            cashWriteOff.setStatus(CashWriteOffStatus.Y.name());
            approvalOpinions = "【" + messageService.getMessageDetailByCode(RespCode.WRITE_OFF_RESERVED_APPROVED) + "】" + approvalOpinions;
        }else if(operationType.equals(-1)){
            cashWriteOff.setStatus(CashWriteOffStatus.N.name());
            approvalOpinions = "【" + messageService.getMessageDetailByCode(RespCode.WRITE_OFF_RESERVED_REJECTED) + "】" + approvalOpinions;
        }
        cashWriteOff.setApprovalOpinions(approvalOpinions);
        this.updateById(cashWriteOff);
    }

    /**
     * 判断机构是否允许支付往核算模块发送数据
     * @param companyOID 公司Oid
     * @return
     */
    public boolean judgePostAccountingService(UUID companyOID){
        Boolean flag = false;
        String parameterValueByParameterCode =
                organizationService.getParameterValueByParameterCode(ParameterCode.PAYMENT_ACCOUNTING_ENABLED, null, OrgInformationUtil.getCurrentCompanyId());
        if(parameterValueByParameterCode != null && "Y".equals(parameterValueByParameterCode)){
            return true;
        }
        return flag;
    }

    /*@SyncLock(lockPrefix = SyncLockPrefix.CSH_WRITE_OFF_REVERSE, waiting = true)*/
    public void auditChangeWriteOffStatus(/*@LockedObject*/ String documentType,
                                          /*@LockedObject*/ Long documentHeaderId,
                                          /*@LockedObject*/ Long tenantId,
                                          Long operatorId,
                                          Integer operationType){
        List<CashWriteOff> cashWriteOffByDocumentMsg = baseMapper.selectList(new EntityWrapper<CashWriteOff>()
                .eq("document_type",documentType)
                .eq("document_header_id",documentHeaderId)
                .eq("tenant_id",tenantId)
                .eq("operation_type","WRITE_OFF"));
        if(CollectionUtils.isNotEmpty(cashWriteOffByDocumentMsg)){
            List<AccountPostingHandleCO> accountPostingHandleCOs = new ArrayList<>();
            cashWriteOffByDocumentMsg.stream().forEach(cashWriteOff -> {
                if(! CashWriteOffStatus.P.name().equals(cashWriteOff.getStatus())){
                    throw new BizException(RespCode.PAYMENT_WRITE_OFF_TRY_APPROVED_AGAIN);
                }
                if(operationType.equals(1)){
                    changeWriteOffStatus(cashWriteOff.getId(),CashWriteOffStatus.Y.name(),operatorId,true);
                }else if(operationType.equals(-1)){
                    changeWriteOffStatus(cashWriteOff.getId(),CashWriteOffStatus.N.name(),operatorId,false);
                }
                AccountPostingHandleCO accountPostingHandleCO = new AccountPostingHandleCO();
                accountPostingHandleCO.setLastUpdatedBy(operatorId);
                accountPostingHandleCO.setSourceTransactionType( "CSH_WRITE_OFF");
                accountPostingHandleCO.setTenantId(tenantId);
                accountPostingHandleCO.setTransactionHeaderId(cashWriteOff.getId());
                accountPostingHandleCO.setTransactionDistId(null);
                accountPostingHandleCO.setTransactionLineId(null);
                accountPostingHandleCOs.add(accountPostingHandleCO);
            });
//            // 凭证过账处理
            if(operationType.equals(1)){
                //bo.liu 核算
                /*accountingService.updateAccountPostingBatch(accountPostingHandleCOs);*/
            }
        }
    }

    /**
     * 根据未核销金额范围获取单据核销信息
     * @param unWriteOffAmountFrom   未核销金额从
     * @param unWriteOffAmountTo     未核销金额至
     * @param setOfBooksId           账套ID
     * @return
     */
    public List<CashWriteOffDocumentAmountCO> listDocumentByWriteOffAmount(BigDecimal unWriteOffAmountFrom,
                                                                           BigDecimal unWriteOffAmountTo,
                                                                           Long setOfBooksId,
                                                                           Long tenantId){
        return baseMapper.listDocumentByWriteOffAmount(unWriteOffAmountFrom,unWriteOffAmountTo,setOfBooksId,tenantId);
    }

    /**
     * 根据未核销金额范围获取不满足条件的单据信息
     * @param unWriteOffAmountFrom   未核销金额从
     * @param unWriteOffAmountTo     未核销金额至
     * @param setOfBooksId           账套ID
     * @return
     */
    public List<Long> listExcludeDocumentByWriteOffAmount(BigDecimal unWriteOffAmountFrom,
                                                                              BigDecimal unWriteOffAmountTo,
                                                                              Long setOfBooksId,
                                                                              Long tenantId){
        return baseMapper.listExcludeDocumentByWriteOffAmount(unWriteOffAmountFrom,unWriteOffAmountTo,setOfBooksId,tenantId);
    }

    /**
     * 根据单据头信息获取核销金额
     * @param documentCategory   单据大类 PREPAYMENT_REQUISITION：预付款申请
     * @param documentIds         单据头ID
     * @return Map<Long,Double>   key为 documentLineId，value为核销金额
     */
    public Map<Long,BigDecimal> selectDocumentWriteOffAmountBatch(String documentCategory,
                                                         List<Long> documentIds){
        List<Map<String, Map>> longObjectMaps = baseMapper.selectDocumentWriteOffAmountBatch(documentCategory, documentIds);
        Map<Long,BigDecimal> result = new HashMap();
        longObjectMaps.stream().forEach(longObjectMap -> {
            result.put(TypeConversionUtils.parseLong(longObjectMap.get("documentLineId")),
                    BigDecimal.valueOf(TypeConversionUtils.parseDouble(longObjectMap.get("amount"),2)));
        });
        return result;
    }

    /**
     * 获取预付款单核销明细
     * @param prepaymentRequisitionId    预付款申请ID
     * @param documentNumber
     * @param documentFormName
     * @return
     */
    public List<CashWriteOffHistoryDTO> getPrepaymentWriteOffDetail(Long prepaymentRequisitionId,
                                                                    String documentNumber,
                                                                    String documentFormName,
                                                                    Page page){
        List<CashWriteOff> prepaymentWriteOffHistories = baseMapper.getPrepaymentWriteOffHistory(page,prepaymentRequisitionId);
        List<CashWriteOffHistoryDTO> result = new ArrayList<>();
        prepaymentWriteOffHistories.stream().forEach(his->{
            List<ExpensePaymentScheduleCO> paymentScheduleCOS = expenseService.getExpPublicReportScheduleByIds(Arrays.asList(his.getDocumentLineId()));
            CashWriteOffHistoryDTO dto = new CashWriteOffHistoryDTO();
            if(paymentScheduleCOS.size() == 1){
                ExpensePaymentScheduleCO paymentScheduleCO = paymentScheduleCOS.get(0);
                BeanUtils.copyProperties(paymentScheduleCO,dto);
                dto.setExpReportHeaderId(paymentScheduleCO.getExpReportHeaderId());
                dto.setDocumentNumber(paymentScheduleCO.getDocumentNumber());
                //设置序号
                Map<Long,Integer> map = expenseService.getExpPublicReportScheduleMapByHeaderId(dto.getExpReportHeaderId());
                dto.setDocumentLineNumber(map.get(paymentScheduleCO.getId()));
                dto.setDocumentFormName(paymentScheduleCO.getReportTypeName());
                ContactCO contactCO = organizationService.getUserById(paymentScheduleCO.getApplicantId());
                if(contactCO != null){
                    dto.setDocumentApplicantName(contactCO.getFullName());
                }
                dto.setRequisitionDate(paymentScheduleCO.getRequisitionDate());
                dto.setWriteOffDate(his.getWriteOffDate());
                dto.setCurrency(paymentScheduleCO.getCurrencyCode());
                dto.setAmount(paymentScheduleCO.getAmount());
                dto.setWriteOffAmount(his.getWriteOffAmount());
                dto.setRemark(his.getRemark());
                dto.setStatus(his.getStatus());
                if (CashWriteOffStatus.P.toString().equals(dto.getStatus())) {
                    if (OrgInformationUtil.getCurrentLanguage().equalsIgnoreCase("zh_cn")) {
                        dto.setStatusDescription("已生效");
                    } else {
                        dto.setStatusDescription("EFFECTIVE");
                    }
                    //dto.setStatusDescription(messageService.getMessageDetailByCode(RespCode.WRITE_OFF_STATUS_P));
                } else if (CashWriteOffStatus.Y.toString().equals(dto.getStatus())) {
                    if (OrgInformationUtil.getCurrentLanguage().equalsIgnoreCase("zh_cn")) {
                        dto.setStatusDescription("已审核");
                    } else {
                        dto.setStatusDescription("CHECKED");
                    }
                    //dto.setStatusDescription(messageService.getMessageDetailByCode(RespCode.WRITE_OFF_STATUS_Y));
                } else {
                    if (OrgInformationUtil.getCurrentLanguage().equalsIgnoreCase("zh_cn")) {
                        dto.setStatusDescription("未生效");
                    } else {
                        dto.setStatusDescription("UNEFFECTIVE");
                    }
                    //dto.setStatusDescription(messageService.getMessageDetailByCode(RespCode.WRITE_OFF_STATUS_N));
                }
                if(StringUtils.hasText(documentNumber)) {
                    if (StringUtils.hasText(documentFormName)) {
                        if (dto.getDocumentFormName().contains(documentFormName) && dto.getDocumentNumber().contains(documentNumber)) {
                            result.add(dto);
                        }
                    } else {
                        if(dto.getDocumentNumber().contains(documentNumber)) {
                            result.add(dto);
                        }
                    }
                }else{
                    if (StringUtils.hasText(documentFormName)) {
                        if (dto.getDocumentFormName().contains(documentFormName)) {
                            result.add(dto);
                        }
                    }else{
                        result.add(dto);
                    }
                }
            }
        });
        return result;
    }
}
