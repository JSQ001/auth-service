package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.common.co.CommonApprovalHistoryCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.security.domain.PrincipalLite;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CashTransactionData;
import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import com.hand.hcf.app.payment.domain.PaymentDetail;
import com.hand.hcf.app.payment.domain.PaymentSystemCustomEnumerationType;
import com.hand.hcf.app.payment.domain.enumeration.PaymentDocumentOperationEnum;
import com.hand.hcf.app.payment.externalApi.PaymentAccountingService;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.CashTransactionDetailMapper;
import com.hand.hcf.app.payment.utils.Constants;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.utils.SpecificationUtil;
import com.hand.hcf.app.payment.web.dto.BacklashDTO;
import com.hand.hcf.app.payment.web.dto.BacklashUpdateDTO;
import com.hand.hcf.app.payment.web.dto.PayDocumentDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cbc on 2018/4/3.
 */
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class BacklashService {

    private final PaymentAccountingService accountingService;
    private final PaymentOrganizationService organizationService;

    private final CashTransactionDetailService detailService;
    private final CashTransactionDataService dataService ;
    private final DetailLogService detailLogService;
    private final CashTransactionLogService cashTransactionLogService;
    private final CashTransactionDetailMapper detailMapper;
    /**
     *  获取反冲数据
     *
     * @param billcode
     * @param documentNumber
     *@param documentTypeId
     * @param partnerId
     * @param amountFrom
     * @param amountTo
     * @param payDateFrom
     * @param payDateTo
     * @param employeeId
     * @param page  @return
     */
    public Page<CashTransactionDetail> getBacklash(String billcode,
                                                   String documentNumber,
                                                   String documentTypeId,
                                                   Long partnerId,
                                                   BigDecimal amountFrom,
                                                   BigDecimal amountTo,
                                                   ZonedDateTime payDateFrom,
                                                   ZonedDateTime payDateTo,
                                                   Long employeeId,
                                                   String partnerCategory,
                                                   String partnerName,
                                                   String sign,
                                                   Long tenantId,
                                                   Page page) {

        // 查询当前账套下的公司
        Long setBooksId = OrgInformationUtil.getCurrentSetOfBookId();

        /*Page<AccountingMatchGroupValueDTO> pageCompanys = accountingService.getCompanyByInfo(null, null, null, setBooksId, new Page(1, 100000));

        if (CollectionUtils.isEmpty(pageCompanys.getRecords())){
            page.setRecords(new ArrayList());
            return page;
        }
        List<Long> companyIds = pageCompanys
                .getRecords()
                .stream()
                .map(e -> TypeConversionUtils.parseLong(e.getId())).collect(Collectors.toList());*/
        List<Long> ids = new ArrayList<>();
        StringBuffer notExistsSQL = new StringBuffer("\n select 1 from  csh_transaction_detail d \n");
        notExistsSQL.append("WHERE d.ref_cash_detail_id =  temp.id \n");
        notExistsSQL.append("AND d.operation_type in ('" + SpecificationUtil.RETURN + "','"
                + SpecificationUtil.RESERVED+"') \n");
        notExistsSQL.append("AND d.payment_status in ('" + SpecificationUtil.PAYING + "','"
                + SpecificationUtil.PAYSUCCESS+"') \n");


        //排除核销反冲过的数据
        List<Long> writeOffDetailsId = new ArrayList<>();
        writeOffDetailsId = detailMapper.getWriteOffDetailsId();

//        ////排除核销关联的支付明细数据
//        List<Long> writesOffDetailIds = new ArrayList<>();
//        writesOffDetailIds = writeOffService.selectList(
//                new EntityWrapper<CashWriteOff>()
//                        .eq("status", "Y")
//                        .eq("tenant_id", OrgInformationUtil.getCurrentTenantID())
//        ).stream().map(CashWriteOff::getCshTransactionDetailId).collect(Collectors.toList());
//
//
//        ids.addAll(writesOffDetailIds);
        ids.addAll(writeOffDetailsId);
        if(!CollectionUtils.isEmpty(ids)){
            ids.stream().distinct().collect(Collectors.toList());
        }


        if(!StringUtils.isEmpty(sign)){
            String[] str = sign.split("_");
            partnerId = Long.parseLong(str[0]);
            partnerCategory = "true".equalsIgnoreCase(str[1])? Constants.EMPLOYEE : Constants.VENDER;
        }


        Page<CashTransactionDetail> pageResult = detailService.selectPage(
                page,
                new EntityWrapper<CashTransactionDetail>()
                        .eq("operation_type", SpecificationUtil.PAYMENT)
                        .eq("payment_status", SpecificationUtil.PAYSUCCESS)
                        .eq("refund_status", SpecificationUtil.NOREFUND)
                        .like(billcode != null, "billcode", billcode)
                        .like(documentNumber != null, "document_number", documentNumber)
                        .eq(documentTypeId != null, "document_category", documentTypeId)
                        .eq(partnerId != null, "partner_id", partnerId)
                        .ge(amountFrom != null, "amount", amountFrom)
                        .le(amountTo != null, "amount", amountTo)
                        .ge(payDateFrom != null, "pay_date", payDateFrom)
                        .lt(payDateTo != null, "pay_date", payDateTo)
                        .eq(!StringUtils.isEmpty(partnerCategory),"partner_category",partnerCategory)
                        .eq(employeeId != null, "employee_id", employeeId)
                       // .eq(tenantId != null,"tenant_id",tenantId)
                        /*.in("document_company_id",companyIds)*/
                        .like(!StringUtils.isEmpty(partnerName),"partner_name",partnerName)
                        .notIn(!CollectionUtils.isEmpty(ids),"id",ids)
                        .notExists(notExistsSQL.toString())
                        .orderBy("billcode")

        );

        Map<Long,String> empMap = new HashMap<>();
        Map<Long,String> venMap = new HashMap<>();

        //将单据类型名称和收款方名称放进去在前台显示
        if(!CollectionUtils.isEmpty(pageResult.getRecords())){
            pageResult.setRecords(detailService.getNames(pageResult.getRecords()));
        }
        return pageResult;
    }


    @Transactional
    public BacklashDTO toBacklash(Long detailId){
        CashTransactionDetail detail = detailService.selectById(detailId);


        //操作反冲数据
        operation(detail);

        //返回反冲单据信息
        BacklashDTO backlashDTO = backInfo(detail);

        return backlashDTO;
    }


    public BacklashDTO backInfo(CashTransactionDetail detail){
        BacklashDTO backlashDTO = new BacklashDTO();

        //查询出data信息
        CashTransactionData data = dataService.selectById(detail.getCshTransactionDataId());

        //单据信息
        //付款单据DTO
        PayDocumentDTO payDocumentDTO = PayDocumentDTO.builder()
                .currency(detail.getCurrency())
                .documentApplicant(detail.getEmployeeName())
                .documentCode(detail.getDocumentNumber())
                .documentTotalAmount(data.getAmount())
                .documentId(detail.getDocumentId())
                .documentTypeCode(data.getDocumentTypeName())
                .documentTypeName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_DOCUMENT_TYPE,
                        data.getDocumentCategory()).getName())
                .documentDate(detail.getRequisitionDate())
                .build();

        //原单据信息
        CashTransactionDetail transactionDetail = detailService.selectById(detail.getRefCashDetailId());
        List<SysCodeValueCO> sysCodeValues = organizationService.listAllSysCodeValueByCode(PaymentSystemCustomEnumerationType.CSH_PARTNER_CATEGORY);
        Map<String, String> sysCodeMap = new HashMap<>();
        sysCodeValues.forEach(e -> sysCodeMap.put(e.getValue(), e.getName()));
        transactionDetail.setPartnerCategoryName(sysCodeMap.get(transactionDetail.getPartnerCategory()));
        detail.setPartnerCategoryName(sysCodeMap.get(detail.getPartnerCategory()));
        backlashDTO.setPayDocumentDTO(payDocumentDTO);
        backlashDTO.setDetail(transactionDetail);
        backlashDTO.setBackDetail(detail);

        return backlashDTO;
    }

    private void operation(CashTransactionDetail detail){
        PrincipalLite userBean = OrgInformationUtil.getUser();
        PrincipalLite user = OrgInformationUtil.getUser();
        detail.setAmount(TypeConversionUtils.roundHalfUp(detail.getAmount().multiply(BigDecimal.valueOf(-1))));
        detail.setCreatedBy(userBean.getId());
        detail.setCreatedDate(ZonedDateTime.now());
        detail.setVersionNumber(1);
        detail.setLastUpdatedBy(userBean.getId());
        detail.setLastUpdatedDate(ZonedDateTime.now());
        detail.setRefundDate(ZonedDateTime.now());//退款和反冲相斥，这里反冲日期存此字段
        detail.setRefCashDetailId(detail.getId());//来源明细id
        detail.setId(null);
        detail.setRemark("");
        detail.setOperationType(SpecificationUtil.RESERVED);
        detail.setRefBillCode(detail.getBillcode());
        detail.setBillcode(organizationService.getCoding("BILL_CODE", detail.getPaymentCompanyId()));
        detail.setPaymentStatus(SpecificationUtil.NEWPAY);//反冲编辑中

        // 退款状态
        detail.setPaymentReturnStatus(SpecificationUtil.NORETURN);
        // 反冲状态
        detail.setReservedStatus(SpecificationUtil.NO_RESERVED);
        detailService.insert(detail);
        //插入反冲日志表
        detailLogService.insertLog(detail.getId(), OrgInformationUtil.getCurrentUserId(), PaymentDocumentOperationEnum.GENERATE.getId(),"");
    }



    public BacklashDTO updateBacklash(BacklashUpdateDTO backlashDTO){
        CashTransactionDetail detail = detailService.selectById(backlashDTO.getId());
        detail.setRemark(backlashDTO.getRemarks());
        String join = org.apache.commons.lang3.StringUtils.join(backlashDTO.getAttachmentOidS(), ",");
        detail.setBackFlashAttachmentOids(join);

        boolean b = detailService.updateById(detail);
        if(b){
            BacklashDTO dto = backInfo(detail);
            return dto;
        }
        return null;
    }


    /*@Lock(name = SyncLockPrefix.CSH_TRANSACTION_DATA, keys = {"#detail.refCashDetailId"}, lockType = LockType.TRY_LOCK)*/
    public Boolean submitBacklash(CashTransactionDetail detail) {
        //校验提交的数据
        //提交的时候对数据加锁
        detail.setPayDate(ZonedDateTime.now());
        detail.setPaymentStatus(SpecificationUtil.PAYING);
        detailService.updateById(detail);
        //提交插入日志表
        detailLogService.insertLog(detail.getId(), OrgInformationUtil.getCurrentUserId(), PaymentDocumentOperationEnum.APPROVAL.getId(),detail.getRemark());
        return true;

    }


    private CashTransactionDetail submitCheck(CashTransactionDetail detail){
        //只有状态是1001或1005且是反冲的数据允许提交
        if(
                (
                detail.getPaymentStatus().equals(SpecificationUtil.NEWPAY)
                        ||
                detail.getPaymentStatus().equals(SpecificationUtil.PAYFAILURE)
                )
                        &&
                detail.getOperationType().equals(SpecificationUtil.RESERVED)){

        }else {
            throw new BizException(RespCode.PAYMENT_DETAIL_FLASH_NOT_ALLOW_SUBMIT);
        }
        List<String> list = Arrays.asList(SpecificationUtil.RETURN, SpecificationUtil.RESERVED);
        List<String> status = Arrays.asList(SpecificationUtil.PAYING, SpecificationUtil.PAYSUCCESS);
        //原单据状态
        List<CashTransactionDetail> details = detailService.selectList(
                new EntityWrapper<CashTransactionDetail>()
                        .eq("ref_cash_detail_id", detail.getRefCashDetailId())
                        .in("operation_type", list)
                        .in("payment_status", status)
        );
        if(!CollectionUtils.isEmpty(details)){
            throw new BizException(RespCode.PAYMENT_NOT_ALLOW_BACKLASH);
        }
        return detail;

    }




    public Page<CashTransactionDetail> getBacklashByInput(
            Long userId,
            String backBillCode,
            String billCode,
            ZonedDateTime backFlashDateFrom,
            ZonedDateTime backFlashDateTo,
            BigDecimal backlashAmountFrom,
            BigDecimal backlashAmountTo,
            String backlashStatus,
            Page page

    ){

        Page selectPage = detailService.selectPage(page,
                new EntityWrapper<CashTransactionDetail>()
                        .eq("created_by", userId)
                        .eq("operation_type", SpecificationUtil.RESERVED)
                        .eq(!StringUtils.isEmpty(backlashStatus),"payment_status",backlashStatus)
                        .like(!StringUtils.isEmpty(backBillCode),"billcode",backBillCode)
                        .like(!StringUtils.isEmpty(billCode), "ref_bill_code", billCode)
                        .ge(backlashAmountFrom != null, "amount", backlashAmountFrom)
                        .le(backlashAmountTo != null, "amount", backlashAmountTo)
                        .ge(backFlashDateFrom != null, "refund_date", backFlashDateFrom)
                        .le(backFlashDateTo != null, "refund_date", backFlashDateTo)
                        .orderBy("billcode")
        );
        return selectPage;
    }



    public void check(CashTransactionDetail detail){
        //只允许状态的提交的数据允许驳回和通过
        if(!detail.getPaymentStatus().equals(SpecificationUtil.PAYING)){
            throw new BizException(RespCode.PAYMENT_DETAIL_SUBMIT_ALLOW);
        }
        return;
    }



    //复核驳回&复核通过
    public Boolean notRecheck(String recheckRemark,String status,Long detailId,String backlashRemark){
        CashTransactionDetail detail = detailService.selectById(detailId);
        CashTransactionDetail sourceDetail = detailService.selectById(detail.getRefCashDetailId());
        Integer logStatus = null;
        Boolean accountStatus = false;
        switch (status){
            case SpecificationUtil.PAYSUCCESS:
                /*
                * 支付明细表中此条明细处理状态更新为 1004：已处理 。原支付明细反冲状态更新为 3：被反冲。*/
                Boolean profile = detailService.getProfile();
                accountStatus = profile;
                detail.setAccountStatus(accountStatus);
                sourceDetail.setReservedStatus(SpecificationUtil.IS_RESERVED);//被反冲
                logStatus = PaymentDocumentOperationEnum.APPROVAL_PASS.getId();
                break;
            case SpecificationUtil.PAYFAILURE:
                /**
                 * 将此条反冲支付水流置为 驳回 状态，支付明细表中此条明细处理状态更新为 9001:此时退款和反冲都可以查到 。
                 * 驳回的数据退回到付款反冲界面。
                 */
                logStatus = PaymentDocumentOperationEnum.APPROVAL_REJECT.getId();
                sourceDetail.setReservedStatus(SpecificationUtil.NO_RESERVED);//原单据还原为未反冲
                break;
            default:
                break;

        }


        detailService.updateById(sourceDetail);
        check(detail);
        detail.setPaymentStatus(status);
        if(StringUtils.isEmpty(backlashRemark)){
            detail.setRemark("");
        }else {
            detail.setRemark(backlashRemark);
        }
        detail.setPaymentStatus(status);
        //插入日志表
        detailLogService.insertLog(detailId, OrgInformationUtil.getCurrentUserId(),logStatus,recheckRemark);
        boolean b = detailService.updateById(detail);
        if (status.equals(SpecificationUtil.PAYSUCCESS)) {
            // 支付操作日志记录原明细
            cashTransactionLogService.createPayOperatorLog(
                    detail.getRefCashDetailId(),
                    SpecificationUtil.LOG_PAY_RESERVED,
                    (detail.getRemark() == null ? "" : detail.getRemark() + "--") + "反冲金额：" + detail.getAmount(),
                    detail.getCreatedBy());
            // 复核通过需要生成凭证
            // 支付明细数据
            CashTransactionData data = dataService.selectById(detail.getCshTransactionDataId());
            List<PaymentDetail> paymentDetails = new ArrayList<>();
            PaymentDetail paymentDetail = detailService.getPaymentDetail(detail, data,
                    organizationService.getById(data.getCompanyId()).getSetOfBooksId());
            paymentDetails.add(paymentDetail);
            // 往核算模块发送数据
            // 往审批日志新增记录
            List<CommonApprovalHistoryCO> historyCOS = new ArrayList<>();
            CommonApprovalHistoryCO approvalHistoryDTO = detailService.setApprovalHistoryCO(detail,"反冲", PaymentDocumentOperationEnum.RESERVED.getId(),recheckRemark);
            historyCOS.add(approvalHistoryDTO);
            detailService.sendOtherService(historyCOS, paymentDetails, accountStatus);
        }
        return b;
    }


    /**
     *    查询反冲复核的单据
     * 付款反冲复核人员可以查看到提交人与当前员工属于同一公司的数据。
     */
    public Page<CashTransactionDetail> getRecheck(
            String billCode,
            String refBillCode,
            ZonedDateTime backFlashDateFrom,
            ZonedDateTime backFlashDateTo,
            BigDecimal amountFrom,
            BigDecimal amountTo,
            String status,
            Page page
            ){
        // 查询当前账套下的公司
        Long setBooksId = OrgInformationUtil.getCurrentSetOfBookId();

        /*Page<AccountingMatchGroupValueDTO> pageCompanys = accountService.getCompanyByInfo(null, null, null, setBooksId, new Page(1, 100000));

        if (CollectionUtils.isEmpty(pageCompanys.getRecords())){
            page.setRecords(new ArrayList());
            return page;
        }
        List<Long> companyIds = pageCompanys
                .getRecords()
                .stream()
                .map(e -> TypeConversionUtils.parseLong(e.getId())).collect(Collectors.toList());*/

        Page<CashTransactionDetail> result = detailService.selectPage(page,
                new EntityWrapper<CashTransactionDetail>()
                        .eq("payment_status", status)
                        .like(!StringUtils.isEmpty(billCode), "billcode", billCode)
                        .like(!StringUtils.isEmpty(refBillCode), "ref_bill_code", refBillCode)
                        .ge(backFlashDateFrom != null, "refund_date", backFlashDateFrom)
                        .le(backFlashDateTo != null, "refund_date", backFlashDateTo)
                        .ge(amountFrom != null, "amount", amountFrom)
                        .le(amountTo != null, "amount", amountTo)
                        /*.in("document_company_id",companyIds)*/
                        .eq("operation_type", SpecificationUtil.RESERVED)
//                        .eq("applicant_company_id",OrgInformationUtil.getCurrentCompanyID())
                        .orderBy("billcode")
        );
        if(!CollectionUtils.isEmpty(result.getRecords())){
            for(CashTransactionDetail detail:result.getRecords()){
                detail.setCreatedByName(organizationService.listByUserIds(Arrays.asList(detail.getCreatedBy())).get(0).getFullName());
            }
        }


        return result;
    }



    //准备反冲
    public BacklashDTO getReadyByDetailId(Long detailId, Boolean flag){

        BacklashDTO backlashDTO = new BacklashDTO();
        CashTransactionDetail detail = detailService.selectById(detailId);
        //查询出data信息
        CashTransactionData data = dataService.selectById(detail.getCshTransactionDataId());

        //单据信息
        //付款单据DTO
        PayDocumentDTO payDocumentDTO = PayDocumentDTO.builder()
                .currency(detail.getCurrency())
                .documentApplicant(detail.getEmployeeName())
                .documentCode(detail.getDocumentNumber())
                .documentTotalAmount(data.getAmount())
                .documentId(detail.getDocumentId())
                .documentTypeCode(data.getDocumentTypeName())
                .documentTypeName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_DOCUMENT_TYPE,
                        data.getDocumentCategory()).getName())
                .documentDate(detail.getRequisitionDate())
                .build();
        detail.setPartnerCategoryName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_PARTNER_CATEGORY,
                detail.getPartnerCategory()).getName());
        backlashDTO.setDetail(detail);
        backlashDTO.setPayDocumentDTO(payDocumentDTO);

        //反冲单据
        List<CashTransactionDetail> backlashDetails = detailService.selectList(
                new EntityWrapper<CashTransactionDetail>()
                        .eq("ref_cash_detail_id", detail.getId())
                        .eq("operation_type", SpecificationUtil.RESERVED)
        );
        if(!CollectionUtils.isEmpty(backlashDetails)){
            CashTransactionDetail cashTransactionDetail = backlashDetails.get(0);

            //设置附件oid成list
            if(!StringUtils.isEmpty(cashTransactionDetail.getBackFlashAttachmentOids())){
                String str[] = cashTransactionDetail.getBackFlashAttachmentOids().split(",");
                List<String> list = Arrays.asList(str);
                cashTransactionDetail.setBacklashAttachmentOID(list);
                List<AttachmentCO> attachmentByOIDS = organizationService.listByOids(list);
                cashTransactionDetail.setBacklashAttachments(attachmentByOIDS);
            }
            backlashDTO.setBackDetail(cashTransactionDetail);
        }
        backlashDTO.setFlashFlag(flag);
        return backlashDTO;
    }

    //根据反冲id设置反冲附件信息
    public CashTransactionDetail addAttachmentInfo(Long backlashId){
        CashTransactionDetail backlashDetail = detailService.selectById(backlashId);
        //设置附件oid成list
        if(!StringUtils.isEmpty(backlashDetail.getBackFlashAttachmentOids())){
            String str[] = backlashDetail.getBackFlashAttachmentOids().split(",");
            List<String> list = Arrays.asList(str);
            backlashDetail.setBacklashAttachmentOID(list);
            List<AttachmentCO> attachmentByOIDS = organizationService.listByOids(list);
            backlashDetail.setBacklashAttachments(attachmentByOIDS);
        }
        return backlashDetail;
    }







}
