package com.hand.hcf.app.payment.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.core.web.adapter.DomainObjectAdapter;
import com.hand.hcf.app.payment.domain.CashFlowItem;
import com.hand.hcf.app.payment.domain.CashTransactionClass;
import com.hand.hcf.app.payment.domain.CashTransactionData;
import com.hand.hcf.app.payment.domain.PaymentSystemCustomEnumerationType;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.externalApi.SupplierService;
import com.hand.hcf.app.payment.persistence.CashFlowItemMapper;
import com.hand.hcf.app.payment.persistence.CashTransactionClassMapper;
import com.hand.hcf.app.payment.persistence.CashTransactionDataMapper;
import com.hand.hcf.app.payment.utils.Constants;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.utils.SpecificationUtil;
import com.hand.hcf.app.payment.web.dto.AmountAndDocumentNumberDTO;
import com.hand.hcf.app.payment.web.dto.CashTransactionDataWebDTO;
import com.hand.hcf.app.payment.web.dto.PartnerBankInfo;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cbc on 2017/9/29.
 */
@Service
@Transactional
@AllArgsConstructor
public class CashTransactionDataService extends BaseService<CashTransactionDataMapper, CashTransactionData> {
    private static final Logger log = LoggerFactory.getLogger(CashTransactionDataService.class);
    private final CashTransactionClassMapper cashTransactionClassMapper;
    private final CashFlowItemMapper cashFlowItemMapper;

    private final PaymentOrganizationService organizationService;
    private final SupplierService supplierService;

    private final PaymentCompanyConfigService paymentCompanyConfigService;

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
     * @param page
     * @return
     */
    public List<CashTransactionDataWebDTO> getTransactionDataByCond(List<Long> paymentCompanyIds,
                                                                    String documentNumber,
                                                                    String documentCategory,
                                                                    Long employeeId,
                                                                    String requisitionDateFrom,
                                                                    String requisitionDateTo,
                                                                    String paymentMethodCategory,
                                                                    String partnerCategory,
                                                                    Long partnerId,
                                                                    BigDecimal amountFrom,
                                                                    BigDecimal amountTo,
                                                                    String documentTypeName,
                                                                    Page page) {
        ZonedDateTime requisitionZonedDateFrom = DateUtil.stringToZonedDateTime(requisitionDateFrom);
        ZonedDateTime requisitionZonedDateTo = DateUtil.stringToZonedDateTime(requisitionDateTo);
        if (requisitionZonedDateTo != null){
            requisitionZonedDateTo = requisitionZonedDateTo.plusDays(1);
        }
        //paymentCompanyIds为空时应该返回空数据，而不是所有公司数据
        List<CashTransactionData> dataList = new ArrayList<>();
        if (paymentCompanyIds != null && paymentCompanyIds.size() > 0) {
            dataList = baseMapper.selectPageCshTransactionData(
                    page,
                    new EntityWrapper<CashTransactionData>()
                            .like("document_number", documentNumber)
                            .eq(documentCategory != null, "document_category", documentCategory)
                            .eq(employeeId != null, "employee_id", employeeId)
                            .ge(requisitionZonedDateFrom != null, "requisition_date", requisitionZonedDateFrom)
                            .lt(requisitionZonedDateTo != null, "requisition_date", requisitionZonedDateTo)
                            .eq(paymentMethodCategory != null, "payment_method_category", paymentMethodCategory)
                            .eq(partnerCategory != null, "partner_category", partnerCategory)
                            .eq(partnerId != null, "partner_id", partnerId)
                            .ge(amountFrom != null, "amount", amountFrom)
                            .le(amountTo != null, "amount", amountTo)
                            .eq(documentTypeName != null, "document_category", documentTypeName)
                            .eq("frozen_flag", false)
                            .in("payment_company_id", paymentCompanyIds)
                            .orderBy("requisition_date", false)
            );
        }

        return toDto(dataList);
    }

    /**
     * 修改待付提交数据界面
     *
     * @param list
     * @return
     */
    public List<CashTransactionDataWebDTO> updateTransactionDataBatch(List<CashTransactionDataWebDTO> list) {
        if (list.stream().anyMatch(u -> u.getId() == null)) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }

        List<CashTransactionData> result = list.stream().map(u -> {
            CashTransactionData cashTransactionData = baseMapper.selectById(u.getId());
            if (u.getAccountNumber() != null && !u.getAccountNumber().isEmpty()) {
                cashTransactionData.setAccountNumber(u.getAccountNumber());
            }
            if (u.getPaymentMethodCategory() != null && !u.getPaymentMethodCategory().isEmpty()) {
                cashTransactionData.setPaymentMethodCategory(u.getPaymentMethodCategory());
            }
            cashTransactionData.setVersionNumber(u.getVersionNumber());
            return cashTransactionData;
        }).collect(Collectors.toList());
        this.updateBatchById(result);
        return list;
    }

    /**
     * 通用支付信息插入校验
     *
     * @param cashTransactionData
     * @return
     */
    public boolean createTransactionData(CashTransactionData cashTransactionData) {
        log.info("接受到的数据,单据信息为：{}", JSONObject.toJSONString(cashTransactionData));
        if (cashTransactionData.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        if (cashTransactionData.getEntityOid() == null || cashTransactionData.getEntityType() == null){
            throw new BizException(RespCode.PAYMENT_OID_IS_NOT_NULL);
        }
        checkCashTransactionData(cashTransactionData);
        //计划付款日期
        ZonedDateTime zonedDateTime = cashTransactionData.getRequisitionPaymentDate();
        if (zonedDateTime == null) {
            zonedDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        }
        //申请日期
        ZonedDateTime requisitionDate = cashTransactionData.getRequisitionDate();
        if (requisitionDate == null) {
            requisitionDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        }
        cashTransactionData.setRequisitionDate(requisitionDate);
        cashTransactionData.setRequisitionPaymentDate(zonedDateTime);
        // 提交金额为0
        cashTransactionData.setCommitedAmount(BigDecimal.ZERO);
        // 已付金额为0
        cashTransactionData.setPaidAmount(BigDecimal.ZERO);
        // 支付状态默认为 未支付
        cashTransactionData.setPaymentStatus("N");
        cashTransactionData.setAmount(TypeConversionUtils.roundHalfUp(cashTransactionData.getAmount()));
        cashTransactionData.setCreatedDate(ZonedDateTime.now());
        cashTransactionData.setLastUpdatedBy(cashTransactionData.getCreatedBy());
        cashTransactionData.setLastUpdatedDate(ZonedDateTime.now());
        cashTransactionData.setVersionNumber(1);
        return this.insert(cashTransactionData);
    }

    /**
     * @Author: bin.xie
     * @Description: 通用支付数据批量新增
     * @param: list
     * @return: void
     * @Date: Created in 2018/3/2 11:31
     * @Modified by
     */
//    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void saveTransactionDataBatch(List<CashTransactionData> list){
        log.info("接受到的数据记录数为：{},单据信息为：{}",list.size(), JSONObject.toJSONString(list));
        list.forEach(u -> this.createTransactionData(u));
    }
    /**
     * 校验数据
     *
     * @param cashTransactionData
     */
    private void checkCashTransactionData(CashTransactionData cashTransactionData) {

        /*if (cashTransactionData.getCreatedBy() == null) {
            log.error("创建人为空");
            throw new BizException(RespCode.PAYMENT_CREATE_BY_NOT_EXISTS);
        }*/
        // 校验可支付金额
        if (cashTransactionData.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("支付金额小于或等于0");
            throw new BizException(RespCode.PAYMENT_TOTAL_AMOUNT_ERROR);
        }

        // 校验业务大类
        checkDocumentCategory(cashTransactionData);
        // 付款机构ID
        CompanyCO paymentCompany = paymentCompanyConfigService.getCompanyByCompanyIdAndDocumentCategoryAndDocumentTypeId(cashTransactionData.getCompanyId(),
                cashTransactionData.getDocumentCategory(), cashTransactionData.getDocumentTypeId());
        Long paymentCompanyId = null;
        if (paymentCompany == null) {
            paymentCompanyId = cashTransactionData.getCompanyId();
            //throw new BizException(RespCode.PAYMENT_COMPANY_ID_NOT_EXISTS);
        }else{
            paymentCompanyId = paymentCompany.getId();
        }
        cashTransactionData.setPaymentCompanyId(paymentCompanyId);
        //收款方类型
        String partnerCategory = cashTransactionData.getPartnerCategory();
        if (! Constants.EMPLOYEE.equals(partnerCategory) && !Constants.VENDER.equals(partnerCategory)) {
            log.error("收款方类型错误");
            throw new BizException(RespCode.PAYMENT_PARTNER_CATEGORY_ERROR);
        }
        //付款方式类型
        organizationService.listAllSysCodeValueByCode(PaymentSystemCustomEnumerationType.CSH_PAYMENT_TYPE);

        //现金事务类型代码
        organizationService.listAllSysCodeValueByCode(PaymentSystemCustomEnumerationType.CSH_TRANSACTION_TYPE);


        //现金事务分类
        CashTransactionClass cashTransactionClass = cashTransactionClassMapper.selectById(cashTransactionData.getCshTransactionClassId());
        if (cashTransactionClass == null) {
            log.error("现金事务分类不存在");
            throw new BizException(RespCode.PAYMENT_CASH_TRANSACTION_CLASS_NOT_EXIST);
        }
        cashTransactionData.setCshTransactionTypeCode(cashTransactionClass.getTypeCode());
        //现金流量项id
        if (cashTransactionData.getCshFlowItemId() != null) {
            CashFlowItem cashFlowItem = cashFlowItemMapper.selectById(cashTransactionData.getCshFlowItemId());
            if (cashFlowItem == null) {
                log.error("现金流量项不存在");
                throw new BizException(RespCode.PAYMENT_CASH_FLOW_ITEM_NOT_EXIST);
            }
        }
        //收款方银行账户
        PartnerBankInfo payeeCompanyBank = new PartnerBankInfo();
        try {
            if (Constants.EMPLOYEE.equals(cashTransactionData.getPartnerCategory())) {
                payeeCompanyBank = organizationService.getEmployeeCompanyBankByCode(cashTransactionData.getPartnerId() ,cashTransactionData.getAccountNumber());
            }else{
                payeeCompanyBank = supplierService.getVenerCompanyBankByCode(cashTransactionData.getAccountNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取收款方银行信息错误");
            throw new BizException(RespCode.PAYMENT_COMPANY_BANK_INFO_ERROR);
        }

        if(payeeCompanyBank == null || payeeCompanyBank.getBankCode() == null){
            log.error("收款方银行信息为空");
            throw new BizException(RespCode.PAYMENT_PAYEE_COMPANY_BANK_INFO_NULL);
        }
        //币种
        /*String currency = OrganizationInterface.getCurrencyByOtherCurrency(cashTransactionData.getCurrency());
        if (currency == null) {
            throw new BizException(RespCode.PAYMENT_CURRENCY_NOT_EXISTS);
        }*/
        //银行校验，分行校验
    }

    private void checkDocumentCategory(CashTransactionData cashTransactionData) {
        String documentCategory = cashTransactionData.getDocumentCategory();
        switch (documentCategory) {
            case "EXP_REPORT":
                break;
            case "PAYMENT_REQUISITION":
                // 非报销单已核销金额设置为0
                cashTransactionData.setWriteOffAmount(BigDecimal.ZERO);
                break;
            case "ACP_REQUISITION":
                cashTransactionData.setWriteOffAmount(BigDecimal.ZERO);
                //设置付款申请单来源通用支付信息表ID
                if (cashTransactionData.getSourceHeaderId() == null || cashTransactionData.getSourceLineId() == null) {
                    log.error("付款申请单来源通用支付信息表信息不存在");
                    throw new BizException(RespCode.PAYMENT_ACP_REQUISITION_SOURCE_INFO_NOT_NULL);
                }
                List<CashTransactionData> cashTransactionDataList = baseMapper.selectList(new EntityWrapper<CashTransactionData>()
                        .eq("frozen_flag", true)
                        .eq("csh_transaction_type_code","PAYMENT")
                        .eq("document_header_id",cashTransactionData.getSourceHeaderId())
                        .eq("document_line_id",cashTransactionData.getSourceLineId()));
                if(cashTransactionDataList==null||cashTransactionDataList.isEmpty()||cashTransactionDataList.size()>1){
                    log.error("不存在付款单对应的来源单据信息");
                    throw new BizException(RespCode.PAYMENT_ACP_REQUISITION_SOURCE_INFO_NOT_EXISTS);
                }
                cashTransactionData.setSourceDataId(cashTransactionDataList.get(0).getId());
                break;
            default:
                break;
        }
    }

    /**
     * 统一支付结果查询：按照单据行信息查询
     *
     * @param documentCategory 业务大类
     * @param documentHeaderId 所属单据头id
     * @param documentLineId   待付行id
     * @return 结果
     */
    public BigDecimal queryByDocumentHeaderId(String documentCategory, Long documentHeaderId, Long documentLineId) {
        List<CashTransactionData> cashTransactionDataList = baseMapper.overrideSelectList(new EntityWrapper<CashTransactionData>()
                .eq("document_category", documentCategory)
                .eq("document_header_id", documentHeaderId)
                .eq(documentLineId != null, "document_line_id", documentLineId)
        );
        BigDecimal paymentAmount = BigDecimal.ZERO;
        for (CashTransactionData cashTransactionData : cashTransactionDataList) {
            paymentAmount = paymentAmount.add(cashTransactionData.getPaidAmount());
        }
        return paymentAmount;
    }

    public List<AmountAndDocumentNumberDTO> getTotalAmountAndDocumentNum(List<Long> paymentCompanyIds,
                                                                         String documentNumber,
                                                                         String documentCategory,
                                                                         Long employeeId,
                                                                         ZonedDateTime requisitionDateFrom,
                                                                         ZonedDateTime requisitionDateTo,
                                                                         String paymentMethodCategory,
                                                                         String partnerCategory,
                                                                         Long partnerId,
                                                                         BigDecimal amountFrom,
                                                                         BigDecimal amountTo,
                                                                         String documentTypeName) {
        List<CashTransactionData> dataList = new ArrayList<>();
        if (paymentCompanyIds != null && paymentCompanyIds.size() > 0) {
            dataList = baseMapper.selectTotalAmount(
                    new EntityWrapper<CashTransactionData>()
                            .like("document_number", documentNumber)
                            .eq(documentCategory != null, "document_category", documentCategory)
                            .eq(employeeId != null, "employee_id", employeeId)
                            .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                            .lt(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                            .eq(paymentMethodCategory != null, "payment_method_category", paymentMethodCategory)
                            .eq(partnerCategory != null, "partner_category", partnerCategory)
                            .eq(partnerId != null, "partner_id", partnerId)
                            .ge(amountFrom != null, "amount", amountFrom)
                            .le(amountTo != null, "amount", amountTo)
                            .in("payment_company_id", paymentCompanyIds)
                            .eq(documentTypeName != null, "document_category", documentTypeName)
                            .eq("frozen_flag", false)
            );
        }

        List<AmountAndDocumentNumberDTO> resultDTO = new ArrayList<>();

        dataList.forEach(u ->{
            BigDecimal totalAmount = TypeConversionUtils.roundHalfUp(u.getAmount().subtract(u.getCommitedAmount()));
            totalAmount = TypeConversionUtils.roundHalfUp(totalAmount.subtract(u.getWriteOffAmount()));
            Long documentNum = u.getPaidAmount().longValue();
            AmountAndDocumentNumberDTO build = AmountAndDocumentNumberDTO
                    .builder()
                    .totalAmount(totalAmount)
                    .currency(u.getCurrency())
                    .documentNumber(documentNum)
                    .build();
            resultDTO.add(build);
        });

        return resultDTO;
    }

    private  List<CashTransactionDataWebDTO> toDto(List<CashTransactionData> list){
        List<CashTransactionDataWebDTO> dtoList = new ArrayList<>();
        Map<String, String> documentTypeMap = new HashMap<>(16);
        Map<String, String> paymentTypeMap = new HashMap<>(16);
        Map<String, String> partnerTypeMap = new HashMap<>(16);
        Map<String, String> paymentStatusMap = new HashMap<>(16);
        Map<Long, String> empMap = new HashMap<>(16);
        Map<Long, String> empCodeMap = new HashMap<>(16);
        Map<Long, String> venMap = new HashMap<>(16);
        // 单据类型
        List<SysCodeValueCO> sysCodeValues = organizationService.listAllSysCodeValueByCode(
                PaymentSystemCustomEnumerationType.CSH_DOCUMENT_TYPE);
        sysCodeValues.forEach(e -> {
            documentTypeMap.put(e.getValue(), e.getName());
        });

        // 支付方式
        List<SysCodeValueCO> paymentType = organizationService.listAllSysCodeValueByCode(
                PaymentSystemCustomEnumerationType.CSH_PAYMENT_TYPE);
        paymentType.forEach( e ->{
            paymentTypeMap.put(e.getValue(), e.getName());
        });

        // 员工或供应商
        List<SysCodeValueCO> partnerType = organizationService.listAllSysCodeValueByCode(
                PaymentSystemCustomEnumerationType.CSH_PARTNER_CATEGORY);
        partnerType.forEach( e ->{
            partnerTypeMap.put(e.getValue(), e.getName());
        });

        // 支付状态
        List<SysCodeValueCO> paymentStatus = organizationService.listAllSysCodeValueByCode(
                PaymentSystemCustomEnumerationType.CSH_DATA_PAYMENT_STATUS);
        paymentStatus.forEach( e ->{
            paymentStatusMap.put(e.getValue(), e.getName());
        });
        for (CashTransactionData t : list){

            String oid = null;
            String partnerName = null;
            // 获取员工或供应商名字
            if (Constants.EMPLOYEE.equals(t.getPartnerCategory())) {
                if (!empMap.containsKey(t.getPartnerId())) {

                    List<ContactCO> partner = organizationService.listByUserIds(
                            Arrays.asList(t.getPartnerId()));
                    if (!CollectionUtils.isEmpty(partner)) {
                        empMap.put(t.getPartnerId(), partner.get(0).getFullName());
                        empCodeMap.put(t.getPartnerId(), partner.get(0).getEmployeeCode());
                    }
                }
                partnerName = empMap.get(t.getPartnerId());
            } else {

                if (!venMap.containsKey(t.getPartnerId())) {

                    VendorInfoCO venInfoCO = supplierService.getOneVendorInfoByArtemis(t.getPartnerId().toString());
                    if (venInfoCO != null) {
                        venMap.put(t.getPartnerId(), venInfoCO.getVenNickname());
                    }
                }
                partnerName = venMap.get(t.getPartnerId());
            }

            if (!empMap.containsKey(t.getEmployeeId())){
                List<ContactCO> partner = organizationService.listByUserIds(
                        Arrays.asList(t.getEmployeeId()));
                if (!CollectionUtils.isEmpty(partner)) {
                    empMap.put(t.getEmployeeId(), partner.get(0).getFullName());
                    empCodeMap.put(t.getEmployeeId(), partner.get(0).getEmployeeCode());
                }
            }
            t.setEmployeeName(empMap.get(t.getEmployeeId()));
            t.setEmployeeCode(empCodeMap.get(t.getEmployeeId()));
            BigDecimal payableAmount = TypeConversionUtils.roundHalfUp(t.getAmount().subtract(t.getWriteOffAmount()).subtract(t.getCommitedAmount()));

            CashTransactionDataWebDTO dataDTO = CashTransactionDataWebDTO
                    .builder()
                    .exchangeRate(t.getExchangeRate())
                    .documentNumber(t.getDocumentNumber())
                    .documentCategory(t.getDocumentCategory())
                    .employeeName(t.getEmployeeName())
                    .requisitionDate(t.getRequisitionDate())
                    .amount(t.getAmount())
                    .employeeCode(t.getEmployeeCode())
                    .payableAmount(payableAmount)
                    .currentPayAmount(payableAmount)
                    .paymentMethodCategory(t.getPaymentMethodCategory())
                    .partnerCategory(t.getPartnerCategory())
                    // 员工或供应商名字
                    .partnerName(partnerName)
                    .accountNumber(t.getAccountNumber())
                    .versionNumber(t.getVersionNumber())
                    .employeeId(t.getEmployeeId())
                    .currency(t.getCurrency())
                    .paymentStatus(t.getPaymentStatus())
                    .documentCategoryName(documentTypeMap.get(t.getDocumentCategory()))
                    .paymentMethodCategoryName(paymentTypeMap.get(t.getPaymentMethodCategory()))
                    .partnerCategoryName(partnerTypeMap.get(t.getPartnerCategory()))
                    .paymentStatusName(paymentStatusMap.get(t.getPaymentStatus()))
                    .documentTypeName(documentTypeMap.get(t.getDocumentCategory()))
                    .partnerOid(oid)
                    .frozenFlag(t.getFrozenFlag())
                    .accountName(t.getAccountName())
                    .partnerId(t.getPartnerId())
                    .partnerCode(t.getPartnerCode())
                    // 退款金额
                    .returnAmount(t.getReturnAmount())
                    // 可反冲金额
                    .ableReservedAmount(TypeConversionUtils.roundHalfUp(payableAmount).add(TypeConversionUtils.roundHalfUp(t.getReturnAmount())))
                    .cshTransactionClassName(t.getCshTransactionClassName())
                    .paymentCompanyId(t.getPaymentCompanyId())
                    .documentHeaderId(t.getDocumentHeaderId())
                    .documentLineId(t.getDocumentLineId())
                    .contractHeaderId(t.getContractHeaderId())
                    .cshTransactionClassId(t.getCshTransactionClassId())
                    .cshTransactionTypeCode(t.getCshTransactionTypeCode())
                    .cshFlowItemId(t.getCshFlowItemId())
                    .description(t.getRemark())
                    .writeOffAmount(t.getWriteOffAmount() == null ? BigDecimal.ZERO : t.getWriteOffAmount())
                    .writeOffTotalAmount(t.getWriteOffTotalAmount() == null ? BigDecimal.ZERO :t.getWriteOffTotalAmount())
                    .paidAmount(t.getPaidAmount() == null ? BigDecimal.ZERO : t.getPaidAmount())
                    .build();
            DomainObjectAdapter.toDto(dataDTO, t);
            dtoList.add(dataDTO);
        }
        return dtoList;
    }

    /**
     * @Description: 查询报账单的反冲金额或者已付金额的ID
     * @param: amountFrom
     * @param: amountTo
     * @param: flag  true--查询已付金额 false--查询反冲金额
     * @return
     * @Date: Created in 2018/6/29 12:26
     * @Modified by
     */
    public List<PublicReportAmountCO> listIdAndAmount(BigDecimal amountFrom, BigDecimal amountTo, Boolean flag){
        if(!flag) {
            List<PublicReportAmountCO> list = baseMapper.findPublicReserveAmountAndId(amountFrom, amountTo);
            return list;
        }else{
            List<PublicReportAmountCO> list = baseMapper.findPublicPaidAmountAndId(amountFrom, amountTo);
            return list;
        }
    }
    /**
     * @Author: bin.xie
     * @Description: 通过报账单id,查询该报账单可反冲的支付数据
     * @param: headerId
     * @param: page
     * @return
     * @Date: Created in 2018/5/8 15:08
     * @Modified by
     */
    public Page<CashTransactionDataWebDTO> selectPublicPage(Long headerId,
                                                         List<Long> dataIds,
                                                         Page page,
                                                         String partnerCategory,
                                                         Long partnerId,
                                                         BigDecimal amountFrom,
                                                         BigDecimal amountTo) {

        List<CashTransactionData> list =  baseMapper.selectPublicList(page,
                new EntityWrapper<CashTransactionData>()
                        .eq("v.document_header_id",headerId)
                        .and("(v.amount - v.commited_amount -v.write_off_amount) + v.return_amount > 0")
                        .eq(TypeConversionUtils.isNotEmpty(partnerCategory),"v.partner_category",partnerCategory)
                        .eq(TypeConversionUtils.isNotEmpty(partnerId),"v.partner_id",partnerId)
                        .ge(TypeConversionUtils.isNotEmpty(amountFrom),"(v.amount - v.commited_amount -v.write_off_amount) + v.return_amount",amountFrom)
                        .le(TypeConversionUtils.isNotEmpty(amountTo),"(v.amount - v.commited_amount -v.write_off_amount) + v.return_amount",amountTo)
                        .notIn("v.id",dataIds));

        page.setRecords(toDto(list));
        return page;
    }

    /**
     * @Author: bin.xie
     * @Description: 根据ID查询通用支付数据
     * @param: id
     * @return
     * @Date: Created in 2018/5/8 16:04
     * @Modified by
     */
    public CashTransactionData overrideSelectById(Long id){
        return baseMapper.overrideSelectById(id);
    }

    //bo.liu ExpenseReportCO 换成 PaymentExpenseReportCO
    /*@Lock(lockType = LockType.TRY_LOCK_LIST, name = SyncLockPrefix.CSH_TRANSACTION_DATA)*/
    public void updateByDocument(/*@LockListKey(value = "#root.![id]")*/ List<PaymentExpenseReportCO> dtos)  {
        for (PaymentExpenseReportCO dto : dtos) {
            // 从非冻结处取
            List<CashTransactionData> list =  baseMapper.selectPublicList(
                    new EntityWrapper<CashTransactionData>()
                            .eq("v.id",dto.getId()));

            if (CollectionUtils.isEmpty(list)) {
                throw new BizException(RespCode.PAYMENT_REQUISITION_NOT_CREATE_PAYMENT_DETAIL);
            }
            CashTransactionData cashTransactionData = list.get(0);
            // 校验可反冲金额 可反冲金额 = 原金额 - 已提交核销 - 已提交支付 + 已退款金额
            if (cashTransactionData.getAmount().subtract(cashTransactionData.getCommitedAmount())
                    .subtract(cashTransactionData.getWriteOffAmount())
                    .add(cashTransactionData.getReturnAmount())
                    .add(dto.getAmount()).compareTo(BigDecimal.ZERO)  == -1){
                log.info("本次反冲金额大于可反冲金额");
                throw new BizException(RespCode.PAYMENT_BACK_FLASH_AMOUT_GREATER_NORMAL_AMOUNT);
            }
            cashTransactionData.setLastUpdatedBy(dto.getUserId());
            cashTransactionData.setLastUpdatedDate(ZonedDateTime.now());
            cashTransactionData.setAmount(TypeConversionUtils.roundHalfUp(cashTransactionData.getAmount().add(dto.getAmount())));
            this.updateById(cashTransactionData);
        }
    }

    /**
     * @Author: bin.xie
     * @Description: 查询单据为报账单的支付明细数据
     * @param:
     * @return
     * @Date: Created in 2018/5/8 14:07
     * @Modified by
     */
    public CashTransactionDataWebDTO queryPublicById(Long id){
        List<CashTransactionData> list =  baseMapper.selectPublicList(
                new EntityWrapper<CashTransactionData>()
                        .eq("v.id",id));

        if (!CollectionUtils.isEmpty(list)){
            setAmountZero(list);
            return toDto(list).get(0);
        }
        return null;
    }

    public List<PublicReportLineAmountCO> listPublicAmount(Long headerId){
        List<CashTransactionData> list =  baseMapper.selectPublicList(
                new EntityWrapper<CashTransactionData>()
                        .eq("v.document_header_id",headerId));
        List<PublicReportLineAmountCO> amountDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)){
            setAmountZero(list);
            list.forEach( u -> {
                PublicReportLineAmountCO amountDTO = new PublicReportLineAmountCO();
                amountDTO.setPaidAmount(u.getPaidAmount());
                amountDTO.setDocumentLineId(u.getDocumentLineId());
                amountDTO.setReturnAmount(u.getReturnAmount());
                amountDTOList.add(amountDTO);
            });
            return amountDTOList;
        }
        return null;
    }


    public Boolean deleteTransactionDataByHeaderId(Long headerId){
        baseMapper.delete(
                new EntityWrapper<CashTransactionData>()
                        .eq("document_category",SpecificationUtil.PUBLIC_REPORT)
                        .eq("document_header_id",headerId));
        return true;
    }

    /**
     * @Description: 由于冻结的报账单查询金额可能为空，避免sql太复杂，因此对NULL 进行转换
     * @param: list
     * @return
     * @Date: Created in 2018/7/3 22:49
     * @Modified by
     */
    private List<CashTransactionData> setAmountZero(List<CashTransactionData> list){
        list.forEach(e->{
            if(e.getPaidAmount() == null){
                e.setPaidAmount(BigDecimal.ZERO);
            }
            if(e.getReturnAmount() == null){
                e.setReturnAmount(BigDecimal.ZERO);
            }
            if(e.getCommitedAmount() == null){
                e.setCommitedAmount(BigDecimal.ZERO);
            }
            if(e.getAmount() == null){
                e.setAmount(BigDecimal.ZERO);
            }
            if(e.getWriteOffAmount() == null){
                e.setWriteOffAmount(BigDecimal.ZERO);
            }
            if(e.getWriteOffTotalAmount() == null){
                e.setWriteOffTotalAmount(BigDecimal.ZERO);
            }
        });
        return list;
    }

    public List<PaymentDocumentAmountCO> listAmountByPrepaymentLineIds(List<Long> lineIds, String documentCategory){

        return baseMapper.listAmountByPrepaymentLineIds(lineIds, documentCategory);
    }
    
    /**
     * 根据单据头ID集合获取已付金额和已退款金额
     *
     * @param: ids 单据头ID集合
     * @param: documentCategory 单据大类
     * @return 
     */
    public List<PaymentDocumentAmountCO> listAmountByDocumentIds(List<Long> ids,
                                                                 String documentCategory,
                                                                 Long employeeId,
                                                                 Long companyId,
                                                                 Long documentTypeId){

        return baseMapper.findAmountByDocumentIds(ids, documentCategory, employeeId, companyId, documentTypeId);
    }
}
