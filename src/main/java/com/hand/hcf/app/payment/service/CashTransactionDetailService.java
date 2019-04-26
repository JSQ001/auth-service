package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.SqlHelper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.*;
import com.hand.hcf.app.payment.domain.enumeration.PaymentDocumentOperationEnum;
import com.hand.hcf.app.payment.externalApi.PaymentAccountingService;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.externalApi.SupplierService;
import com.hand.hcf.app.payment.persistence.CashPaymentMethodMapper;
import com.hand.hcf.app.payment.persistence.CashTransactionDetailMapper;
import com.hand.hcf.app.payment.utils.*;
import com.hand.hcf.app.payment.web.dto.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
public class CashTransactionDetailService extends BaseService<CashTransactionDetailMapper, CashTransactionDetail> {

    private final PaymentOrganizationService organizationService;
    private final SupplierService supplierService;
    private final PaymentAccountingService accountingService;

    private final static Logger logger = LoggerFactory.getLogger(CashTransactionDetailService.class);
    private final CashTransactionDataService cashTransactionDataService;
    private final CashTransactionLogService cashTransactionLogService;
    private final CashPaymentMethodMapper cashPaymentMethodMapper;
    private final DetailLogService detailLogService;
    private static final String BILL_CODE = "BILL_CODE";

    private final CompanyBankService companyBankService;

    /*private final FundService fundService;*/

    private final CashFlowItemService cashFlowItemService;


//    private final ExpenseReportService expenseReportService;
    /**
     * @Author:
     * @Description: 确认支付时，先做校验，然后将通用表里面的数据插入到明细表
     * @param: data  通用支付明细
     * @param: cashPayDTO  支付方式
     * @param: currentAmount 本次支付金额
     * @param: isRefund  是否退票
     * @return: java.lang.Boolean
     * @Date: Created in 2018/2/5 10:52
     * @Modified by bin.xie
     */
    public CashTransactionDetail insertDetail(String batchNum, CashTransactionData data, CashPayDTO cashPayDTO, BigDecimal currentAmount, Boolean accountFlag) {

        //查询可支付金额
        BigDecimal payableAmount = TypeConversionUtils.roundHalfUp(getPableAmount(data.getId()));

        //为插入前的数据做校验
        check(payableAmount, currentAmount);

        //插入数据到明细表
        CashTransactionDetail detail = insert(batchNum,data, cashPayDTO, currentAmount, accountFlag);

        //插入明细表后,插入日志

        insertLog(detail.getId());


        //插入后修改通用表的数据
        //update(data, cashPayDTO, currentAmount, detail);

        return detail;
    }


    //此方法，校验插入前的数据
    public void check(BigDecimal payableAmount, BigDecimal currentAmount) {
//        本次支付的金额是否超过可支付金额
        if ((payableAmount.compareTo(currentAmount) == -1)) {
            throw new BizException(RespCode.PAYMENT_CURRENCY_AMOUNT_EXCEEDS_AMOUNT);
        }
    }

    /**
     * @Author:
     * @Description: 此方法用于将通用表的数据和确认支付dto的数据，插入明细表
     * @param: data 通用表的数据
     * @param: cashPayDTO 确认支付dto的数据
     * @param: currentAmount 本次支付金额
     * @param: isRefund  是否退票
     * @return: com.hand.hcf.app.payment.domain.CashTransactionDetail
     * @Date: Created in 2018/2/5 10:53
     * @Modified by bin.xie
     */
    public CashTransactionDetail insert(String batchNum, CashTransactionData data, CashPayDTO cashPayDTO, BigDecimal currentAmount, Boolean accountFlag) {

        CashTransactionDetail detail = new CashTransactionDetail();
        //忽略转换的字段
        BeanUtils.copyProperties(data, detail, new String[]{"id", "versionNumber","remark"});

        // 设置操作类型为支付
        detail.setOperationType(SpecificationUtil.PAYMENT);
        // 单据的申请日期
        detail.setRequisitionDate(data.getRequisitionDate());
        // 单据的计划付款日期
        detail.setScheduleDate(data.getRequisitionPaymentDate());
        // 设置是否生成凭证
        detail.setAccountStatus(false);
        // 单据Oid
        detail.setEntityOid(data.getEntityOid());
        // 业务类型
        detail.setEntityType(data.getEntityType());
        // 线上支付
        if (cashPayDTO.getPaymentMethodCategory().equals(SpecificationUtil.ONLINE_PAYMENT)) {
            // 1. 线上支付标志
            detail.setEbankingFlag(true);
            detail.setPaymentMethodCategory(SpecificationUtil.ONLINE_PAYMENT);
            detail.setPaymentStatus(SpecificationUtil.PAYING);
        }
        detail.setEbankingFlag(false);
        detail.setPayDate(ZonedDateTime.now());

       // 线下支付
        if (cashPayDTO.getPaymentMethodCategory().equals(SpecificationUtil.OFFLINE_PAYMENT)) {
            //付款日期
            detail.setPayDate(ZonedDateTime.now());
            detail.setPaymentMethodCategory(SpecificationUtil.OFFLINE_PAYMENT);
            detail.setPaymentStatus(SpecificationUtil.PAYSUCCESS);
            detail.setPayDate(cashPayDTO.getPayDate());
            //支票号
            detail.setChequeNumber(cashPayDTO.getChequeNumber());
            // 线下支付支付则生成凭证
            detail.setAccountStatus(accountFlag);
        }


        // 如果为落地文件
        if (cashPayDTO.getPaymentMethodCategory().equals(SpecificationUtil.EBANK_PAYMENT)) {
            //1. 记录文件名
            detail.setPaymentFileName("读取落地文件名");
            detail.setPaymentMethodCategory(SpecificationUtil.EBANK_PAYMENT);
            detail.setPaymentStatus(SpecificationUtil.PAYING);
        }

        // 支付请求日期
        detail.setRequestTime(ZonedDateTime.now());
        // 设置批次号
        detail.setPaymentBatchNumber(batchNum);

        // 付款流水号
        detail.setBillcode(organizationService.getCoding(BILL_CODE, detail.getPaymentCompanyId()));

        // 退票状态：未退票
        detail.setRefundStatus(SpecificationUtil.NOREFUND);


        // 单据公司:业务单据的头公司--待对接后根据单据编号查公司
        detail.setDocumentCompanyId(data.getCompanyId());

        //根据单据所在公司落地其所属账套
        CompanyCO companyCO = organizationService.getById(data.getCompanyId());
        detail.setSetOfBooksId(companyCO == null ? null : companyCO.getSetOfBooksId());

        detail.setDocumentLineDescription(data.getRemark());

        // 单据类型id：业务单据具体类型id。
        detail.setDocumentTypeId(data.getDocumentTypeId());
        detail.setDocumentTypeName(data.getDocumentTypeName());

        // 单据头id---从通用信息表里面拿
        detail.setDocumentId(data.getDocumentHeaderId());


        //退款状态 未退款
        detail.setPaymentReturnStatus(SpecificationUtil.NORETURN);

        // 币种
        detail.setCurrency(cashPayDTO.getCurrency());
        // 汇率
        detail.setExchangeRate(cashPayDTO.getExchangeRate());
        // 金额=待付行的本次付款金额
        detail.setAmount(TypeConversionUtils.roundHalfUp(currentAmount));

        // 现金流量项id
        detail.setCashFlowItemId(data.getCshFlowItemId());

        // 计划付款日期
        detail.setScheduleDate(data.getRequisitionPaymentDate());

        // 付款方式id--操作付款用户点击付款时手工选择的明细付款方式id
        detail.setPaymentTypeId(cashPayDTO.getPaymentTypeId());
        // 付款方式code--
        try{
            detail.setPaymentTypeCode(cashPaymentMethodMapper.selectById(cashPayDTO.getPaymentTypeId()).getPaymentMethodCode());
        }catch (Exception e){
            e.printStackTrace();
            throw new BizException(RespCode.PAYMENT_GET_PAYMENT_METHOD_ERROR);
        }
        // 付款方式：用户所选的付款方式
        detail.setPaymentTypeName(cashPayDTO.getPaymentDescription());
        // 付款方银行账号：drawee_account_number，必输，付款银行账号。
        detail.setDraweeAccountNumber(cashPayDTO.getPayCompanyBankNumber());
        // 付款出纳
        detail.setDraweeId(OrgInformationUtil.getCurrentUserId());

        //付款方银行账户
        CompanyBank payCompanyBank = null;
        //收款方银行账户
        PartnerBankInfo payeeCompanyBank = null;
        try {
            payCompanyBank = companyBankService.selectCompanyBankByBankAccountNumber(cashPayDTO.getPayCompanyBankNumber());
            if (Constants.EMPLOYEE.equals(data.getPartnerCategory())) {
                payeeCompanyBank = organizationService.getEmployeeCompanyBankByCode(data.getPartnerId() ,data.getAccountNumber());
            }else{
                payeeCompanyBank = supplierService.getVenerCompanyBankByCode(data.getAccountNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(RespCode.PAYMENT_COMPANY_BANK_INFO_ERROR);
        }
        if(payCompanyBank==null){
            throw new BizException(RespCode.PAYMENT_PAY_COMPANY_BANK_INFO_NULL);
        }
        if(payeeCompanyBank == null || payeeCompanyBank.getBankCode() == null){
            throw new BizException(RespCode.PAYMENT_PAYEE_COMPANY_BANK_INFO_NULL);
        }
        /** 付款方信息 */
        // 付款开户公司:付款账号的所属公司
        detail.setDraweeCompanyId(payCompanyBank.getCompanyId());

        //付款方开户行行号
        detail.setDraweeBankNumber(payCompanyBank.getBankCode());

        // 付款方银行户名：drawee_account_name，必输，付款银行户名。
        detail.setDraweeAccountName(payCompanyBank.getBankAccountName());

        //付款方开户行所在省：drawee_bank_province_code，必输，付款账号所属明细银行所在省份代码。
        detail.setDraweeBankProvinceCode(payCompanyBank.getProvinceCode());

        //付款方开户行所在市：drawee_bank_city_code，必输，付款账号所属明细银行所在城市代码。
        detail.setDraweeBankCityCode(payCompanyBank.getCityCode());

        /**收款账户信息*/
        // 收款方银行账号
        detail.setPayeeAccountNumber(data.getAccountNumber());

        // 收款方开户行行号
        detail.setPayeeBankNumber(payeeCompanyBank.getBankCode());
        // 收款方开户行名称 取支行名称
        detail.setPayeeBankName(payeeCompanyBank.getBranchName());

        //收款方开户行所在地
        detail.setPayeeBankAddress(payeeCompanyBank.getAccountLocation());

        // 收款方银行户名
        detail.setPayeeAccountName(payeeCompanyBank.getBankAccountName());


        // 接口响应码：response_code，必输，支付接口读取数据时回写的响应代码。
        detail.setResponseCode("接口响应码");
        // 接口响应信息：response_message，必输，支付接口读取数据时回写的响应信息。
        detail.setResponseMessage("接口响应信息");

        // 抽档状态：read_flag，非必输，支付系统读取数据的状态，空：未处理，Y：抽档成功，F：抽档失败。抽档失败
        // 实际结果码：result_code，非必输，支付系统回写的实际支付结果代码。
        // 实际结果信息：result_message，非必输，支付系统回写的实际支付结果信息。
        // 对账状态码：acc_check_code，非必输，未来对账功能备用。
        // 对账日期：acc_check_date，非必输，未来对账功能备用。
        detail.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        detail.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        detail.setCreatedDate(ZonedDateTime.now());
        detail.setLastUpdatedDate(ZonedDateTime.now());
        detail.setVersionNumber(1);
        if("Y".equals(detail.getRefundStatus())){
            detail.setPayDate(ZonedDateTime.now());
            this.updateById(detail);
        }else {
            // 明细表插入通用表id
            detail.setCshTransactionDataId(data.getId());
            detail.setRemark(cashPayDTO.getRemark());
            //插入明细表
            baseMapper.insert(detail);
        }
        return baseMapper.selectById(detail);
    }



    //此方法用于插入明细表后加入插入日志信息
    private void insertLog(Long detailID) {
        cashTransactionLogService.createCashTransactionLog(detailID, SpecificationUtil.LOG_NEW,this.selectById(detailID).getRemark()==null?"":this.selectById(detailID).getRemark(), (this.selectById(detailID).getResponseMessage() != null ? this.selectById(detailID).getResponseMessage().getBytes() : new byte[0]));
    }


    /**
     *
     *  批量支付
     * @param insertDetailDTO
     * @return
     */
    /*@Lock(lockType = LockType.TRY_LOCK_LIST, name = SyncLockPrefix.CSH_TRANSACTION_DATA)*/
    public Boolean insertDetailBatch(/*@LockListKey("#root.dataIds")*/ InsertDetailDTO insertDetailDTO){
        List<BigDecimal> currentAmount = insertDetailDTO.getCurrentAmount();
        for (BigDecimal amount : currentAmount){
            if (amount.setScale(2,BigDecimal.ROUND_HALF_UP).compareTo(BigDecimal.ZERO) <= 0){
                throw new BizException(RespCode.PAYMENT_AMOUNT_MUST_GREATER_ZERO);
            }
        }
        List<Long> dataIds = insertDetailDTO.getDataIds();
        Boolean profile = getProfile();
        // 支付操作
        CashPayDTO cashPayDTO = insertDetailDTO.getCashPayDTO();
        List<BigDecimal> currentAmounts = insertDetailDTO.getCurrentAmount();
        List<Integer> versionNumbers = insertDetailDTO.getVersionNumbers();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random r = new Random();
        String batchNum = sdf.format(new Date()) + r.nextInt(10000);
        if (dataIds.size() != versionNumbers.size() || dataIds.size()!=currentAmounts.size()) {
            throw new BizException(RespCode.SYS_PARAMETER_FORMAT_ERROR);
        }
        List<PaymentDetail> paymentDetails = new ArrayList<>();
        List<CommonApprovalHistoryCO> historyDTOS = new ArrayList<>();
        List<CashTransactionDetail> cashDetails = new ArrayList<>();
        for (int i = 0; i < dataIds.size(); i++) {
            CashTransactionData data = cashTransactionDataService.selectById(dataIds.get(i));
            if (!data.getVersionNumber().equals(versionNumbers.get(i))) {
                throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
            }
            CashTransactionDetail cashTransactionDetail = insertDetail(batchNum,data, cashPayDTO, TypeConversionUtils.roundHalfUp(currentAmounts.get(i)) ,profile);
            PaymentDetail paymentDetail= getPaymentDetail(cashTransactionDetail,data,
                    organizationService.getById(data.getCompanyId()).getSetOfBooksId());
            paymentDetails.add(paymentDetail);
            CommonApprovalHistoryCO approvalHistoryCO = setApprovalHistoryCO(cashTransactionDetail,"支付", PaymentDocumentOperationEnum.PAYMENT.getId(),null);
            historyDTOS.add(approvalHistoryCO);

            cashDetails.add(cashTransactionDetail);
        }
        if (cashPayDTO.getPaymentMethodCategory().equals(SpecificationUtil.OFFLINE_PAYMENT)) {
            sendOtherService(historyDTOS,paymentDetails, profile);
        }

        //调用资金接口插入资金付款接口表
        /*saveTransactionDataBatch(cashDetails);*/
        return true;
    }

    /**
     * 调用资金接口插入资金付款接口表
     * @return
     */
//    @LcnTransaction
    /*private Boolean saveTransactionDataBatch(List<CashTransactionDetail> cashDetails) {
        List<FundPaymentInInterfaceCO> list = new ArrayList<>();
        for (int i = 0; i < cashDetails.size(); i++) {
            CashTransactionDetail detail = cashDetails.get(i);

            FundPaymentInInterfaceCO fundPaymentInInterfaceCO = new FundPaymentInInterfaceCO();
            CompanyCO companyCO = organizationService.getById(detail.getDocumentCompanyId());
            fundPaymentInInterfaceCO.setSetOfBooksId(detail.getSetOfBooksId());
            fundPaymentInInterfaceCO.setTenantId(detail.getTenantId());
            fundPaymentInInterfaceCO.setSourceSystem(SourceSystemEnum.HEC);
            fundPaymentInInterfaceCO.setSourceDocumentNum(detail.getDocumentNumber());
            fundPaymentInInterfaceCO.setBillCode(detail.getBillcode());
            fundPaymentInInterfaceCO.setDocumentCompany(companyCO == null ? "" : companyCO.getCompanyCode());
            fundPaymentInInterfaceCO.setGatherAccount(detail.getPayeeAccountNumber());
            fundPaymentInInterfaceCO.setGatherAccountName(detail.getPayeeAccountName());
            fundPaymentInInterfaceCO.setGatherBankNum(detail.getPayeeBankNumber().substring(0, 3));
            fundPaymentInInterfaceCO.setGatherBankName(null);
            fundPaymentInInterfaceCO.setGatherBranchBankNum(detail.getPayeeBankNumber());
            fundPaymentInInterfaceCO.setGatherBranchBankName(detail.getPayeeBankName());
            if ("EMPLOYEE".equals(detail.getPartnerCategory())) {
                fundPaymentInInterfaceCO.setPropFlag(PropFlagEnum.PRIVATE);
            } else if ("VENDER".equals(detail.getPartnerCategory())) {
                fundPaymentInInterfaceCO.setPropFlag(PropFlagEnum.BUSINESS);
            }
            fundPaymentInInterfaceCO.setCardSign(CardSignEnum.BANK_CARD);
            fundPaymentInInterfaceCO.setAmount(detail.getAmount());
            fundPaymentInInterfaceCO.setCurrency(detail.getCurrency());
            fundPaymentInInterfaceCO.setSummary(detail.getDocumentLineDescription());
            //制单人-费控（先传申请人，后续确认是否增加制单人在detail表）
            fundPaymentInInterfaceCO.setCreatedByFk(detail.getEmployeeName());
            fundPaymentInInterfaceCO.setPayedFlag(0);
            //传现金流量项code
            CashFlowItem cashFlowItem = cashFlowItemService.selectById(detail.getCashFlowItemId());
            if (cashFlowItem != null) {
                fundPaymentInInterfaceCO.setPaymentPurpose(cashFlowItem.getFlowCode());
            }
            list.add(fundPaymentInInterfaceCO);
        }
        try {
            return fundService.saveTransactionDataBatch(list);
        } catch (Exception e) {
            throw new BizException(RespCode.PAYMENT_TRANSFER_FUND_SERVICE_ERROR);
        }
    }*/


    //支付中确定成功
    /*支付中确定支付成功后，需要修改通用表的已支付数据，修改明细表的支付状态（支付成功），支付日期
    * */
    public Boolean PaySuccess(CashPayingDTO payingDTO, ZonedDateTime payDate) {
        List<Long> detailIds = payingDTO.getDetailIds();
        Boolean accountStatus = getProfile();
        List<Integer> versionNumbers = payingDTO.getVersionNumbers();
        if (detailIds.size() != versionNumbers.size()) {
            throw new BizException(RespCode.PAYMENT_INCORRECT_DATA_TRANSMISSION);
        }
        List<PaymentDetail> paymentDetails = new ArrayList<PaymentDetail>();
        List<CommonApprovalHistoryCO> historyDTOS = new ArrayList<>();
        for (int i = 0; i < detailIds.size(); i++) {
            CashTransactionDetail detail = baseMapper.selectById(detailIds.get(i));
            if(!SpecificationUtil.PAYING.equals(detail.getPaymentStatus())){
                throw new BizException(RespCode.PAYMENT_PAYING_NOT_ALLOW);
            }
            if (!detail.getVersionNumber().equals(versionNumbers.get(i))) {
                throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
            }

            // 明细表支付状态：支付成功
            detail.setPaymentStatus(SpecificationUtil.PAYSUCCESS);

            // 生成凭证
            detail.setAccountStatus(accountStatus);

            detail.setPayDate(payDate);//设置支付时间为前台传过来的值

            //修改WHO字段
            detail.setLastUpdatedDate(ZonedDateTime.now());
            detail.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
            this.updateById(detail);

            // 新建日志
            cashTransactionLogService.createCashTransactionLog(detail.getId(), SpecificationUtil.LOG_ENSURE_SUCCESS,"",(detail.getResponseMessage() != null ? detail.getResponseMessage().getBytes() : new byte[0]));

            PaymentDetail paymentDetail= getPaymentDetail(detail,cashTransactionDataService.selectById(detail.getCshTransactionDataId()),
                    organizationService.getById(detail.getPaymentCompanyId()).getSetOfBooksId());

            paymentDetails.add(paymentDetail);
            CommonApprovalHistoryCO approvalHistoryCO = setApprovalHistoryCO(detail,"支付", PaymentDocumentOperationEnum.PAYMENT.getId(),null);
            historyDTOS.add(approvalHistoryCO);

        }
        sendOtherService(historyDTOS,paymentDetails, accountStatus);
        return true;
    }


    /**
     * 支付中确定支付失败后，需要修改通用表已提交的数据，
     * 修改明细表的支付状态（支付失败）
     *
     * @return
     */
    public Boolean PayFail(CashPayingDTO cashPayingDTO) {

        List<Long> detailIds = cashPayingDTO.getDetailIds();
        List<Integer> versionNumbers = cashPayingDTO.getVersionNumbers();
        if (detailIds.size() != versionNumbers.size()) {
            throw new BizException(RespCode.PAYMENT_INCORRECT_DATA_TRANSMISSION);
        }
        for (int i = 0; i < detailIds.size(); i++) {

            CashTransactionDetail detail = baseMapper.selectById(detailIds.get(i));
            if(!SpecificationUtil.PAYING.equals(detail.getPaymentStatus())){
                throw new BizException(RespCode.PAYMENT_PAYING_NOT_ALLOW);
            }
            if (!detail.getVersionNumber().equals(versionNumbers.get(i))) {
                throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
            }

            // 明细支付状态：支付失败
            detail.setPaymentStatus(SpecificationUtil.PAYFAILURE);

            // 如果是退票的支付失败，需要将退票状态置为N
            detail.setRefundStatus(SpecificationUtil.NOREFUND);
            detail.setRefundDate(null);

            // who字段修改
            detail.setLastUpdatedDate(ZonedDateTime.now());
            detail.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
            // 是否凭证生成
            detail.setAccountStatus(false);
            this.updateById(detail);
            cashTransactionLogService.createCashTransactionLog(detail.getId(), SpecificationUtil.LOG_ENSURE_FAIL,"",(detail.getResponseMessage() != null ? detail.getResponseMessage().getBytes() : new byte[0]));
        }
        return true;
    }


    /**
     * 支付失败或者退票处理查询
     *
     * @param billcode
     * @param documentCategory
     * @param documentNumber
     * @param employeeId
     * @param partnerCategory
     * @param partnerId
     * @param customerBatchNo
     * @param paymentStatus
     * @param refundStatus
     * @param page
     * @return
     */
    public Page<CashTransactionDetail> getCashTransactionDetail(List<Long> paymentCompanyIds,
                                                                String billcode,
                                                                String documentCategory,
                                                                String documentNumber,
                                                                Long employeeId,
                                                                ZonedDateTime requisitionDateFrom,
                                                                ZonedDateTime requisitionDateTo,
                                                                BigDecimal amountFrom,
                                                                BigDecimal amountTo,
                                                                String partnerCategory,
                                                                Long partnerId,
                                                                ZonedDateTime payDateFrom,
                                                                ZonedDateTime payDateTo,
                                                                String customerBatchNo,
                                                                Page page,
                                                                String paymentStatus,
                                                                String refundStatus,
                                                                String paymentTypeCode,
                                                                String paymentMethodCategory) {

        Page<CashTransactionDetail> detailPage = this.selectPage(
                page,
                new EntityWrapper<CashTransactionDetail>()
                        .where("operation_type = {0}",SpecificationUtil.PAYMENT)
                        .in(paymentCompanyIds != null,"payment_company_id",paymentCompanyIds)
                        .eq(employeeId != null, "employee_id", employeeId)
                        .eq("operation_type",SpecificationUtil.PAYMENT)
                        .eq(partnerCategory != null, "partner_category", partnerCategory)
                        .eq(paymentMethodCategory!=null,"payment_method_category",paymentMethodCategory)
                        .eq(partnerId != null, "partner_id", partnerId)
                        .ge(payDateFrom != null, "pay_date", payDateFrom)
                        .le(payDateTo != null, "pay_date", payDateTo)
                        .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                        .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                        .ge(amountFrom != null, "amount", amountFrom)
                        .le(amountTo != null, "amount", amountTo)
                        .eq(paymentTypeCode != null, "payment_type_code", paymentTypeCode)
                        .like("customer_batch_no", customerBatchNo)
                        .like("document_number", documentNumber)
                        .eq(documentCategory != null, "document_category", documentCategory)
                        .like("billcode", billcode)
                        .andNew("payment_status={0}", SpecificationUtil.PAYSUCCESS)
                        .eq("refund_status", SpecificationUtil.YESREFUND)
                        .or("(payment_status={0}", SpecificationUtil.PAYFAILURE)
                        .and(paymentStatus != null, "payment_status={0}", paymentStatus)
                        .and("1=1)")
                        .orderBy("pay_date", false)
        );
        // 获取类型名称
        getNames(detailPage.getRecords());

        return detailPage;
    }


    //支付中页签默认查询
    public Page<CashTransactionDetail> payingGetCashTransactionDetail(List<Long> paymentCompanyIds,
                                                                      String billcode,
                                                                      String documentCategory,
                                                                      String documentNumber,
                                                                      Long employeeId,
                                                                      ZonedDateTime requisitionDateFrom,
                                                                      ZonedDateTime requisitionDateTo,
                                                                      BigDecimal amountFrom,
                                                                      BigDecimal amountTo,
                                                                      String partnerCategory,
                                                                      Long partnerId,
                                                                      ZonedDateTime payDateFrom,
                                                                      ZonedDateTime payDateTo,
                                                                      String customerBatchNo,
                                                                      Page page,
                                                                      String paymentTypeCode,
                                                                      String paymentMethodCategory) {

        List<String> status = new ArrayList<>();
        status.add(SpecificationUtil.TOPAY);
        status.add(SpecificationUtil.PAYING); //操作状态为待处理，和处理中

        String operationType = SpecificationUtil.PAYMENT; //操作类型必须为支付

        Page<CashTransactionDetail> detailPage = this.selectPage(
                page,
                new EntityWrapper<CashTransactionDetail>()
                        .where("operation_type = {0}",operationType)
                        .in(paymentCompanyIds != null,"payment_company_id",paymentCompanyIds)
                        .in("payment_status",status)
                        //.in("payment_status", status)
                        .eq(paymentMethodCategory!=null,"payment_method_category",paymentMethodCategory)
                        .like("billcode", billcode)
                        .eq(documentCategory != null, "document_category", documentCategory)
                        .like("document_number", documentNumber)
                        .eq(paymentTypeCode!=null,"payment_type_code",paymentTypeCode)
                        .eq(employeeId != null, "employee_id", employeeId)
                        .eq(partnerCategory != null, "partner_category", partnerCategory)
                        .eq(partnerId != null, "partner_id", partnerId)
                        .ge(payDateFrom != null, "pay_date", payDateFrom)
                        .le(payDateTo != null, "pay_date", payDateTo)
                        .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                        .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                        .ge(amountFrom != null, "amount", amountFrom)
                        .le(amountTo != null, "amount", amountTo)
                        .like("customer_batch_no", customerBatchNo)
                        .orderBy("pay_date", false)
        );
        // 获取类型名称
        getNames(detailPage.getRecords());
        return detailPage;
    }

    /**
     * 已付查询
     * 已付查询页签只显示明细表中支付状态为“支付成功”，且退票状态为“未退票”的数据。
     *
     *
     * @param paymentTypeCode  //付款方式类型代码
     * @param billcode         //付款流水号
     * @param documentCategory //业务大类(单据类型)
     * @param documentNumber   //单据编号
     * @param employeeId       //申请人id
     * @param partnerCategory  //收款方类型
     * @param partnerId        //收款方id
     * @param customerBatchNo  //付款批次号
     * @param requisitionDateFrom  //申请日期从
     * @param requisitionDateTo    //申请日期至
     * @param amountFrom            //支付金额从
     * @param amountTo              //支付金额至
     * @param payDateFrom           //支付日期从
     * @param payDateTo             //支付日期至
     * @param page
     * @return
     */
    public List<CashTransactionDetail> getAlreadyPaid(List<Long> paymentCompanyIds,
                                                      String paymentTypeCode,
                                                      String billcode,
                                                      String documentCategory,
                                                      String documentNumber,
                                                      Long employeeId,
                                                      String partnerCategory,
                                                      Long partnerId,
                                                      String customerBatchNo,
                                                      Page page,
                                                      ZonedDateTime requisitionDateFrom,
                                                      ZonedDateTime requisitionDateTo,
                                                      BigDecimal amountFrom,
                                                      BigDecimal amountTo,
                                                      ZonedDateTime payDateFrom,
                                                      ZonedDateTime payDateTo,
                                                      String paymentMethodCategory) {
        List<CashTransactionDetail> cashTransactionDetailList = baseMapper.selectPage(page,
                new EntityWrapper<CashTransactionDetail>()
                        .in(paymentCompanyIds != null,"payment_company_id",paymentCompanyIds)
                        .eq(paymentMethodCategory!=null,"payment_method_category",paymentMethodCategory)
                        .eq(paymentTypeCode != null, "payment_type_code", paymentTypeCode)
                        .eq("payment_status", SpecificationUtil.PAYSUCCESS)
                        .eq("refund_status", SpecificationUtil.NOREFUND)
                        .eq("operation_type",SpecificationUtil.PAYMENT)
                        .like(billcode != null, "billcode", billcode, SqlLike.DEFAULT)
                        .eq(documentCategory != null, "document_category", documentCategory)
                        .like(documentNumber != null, "document_number", documentNumber, SqlLike.DEFAULT)
                        .eq(employeeId != null, "employee_id", employeeId)
                        .eq(partnerCategory != null, "partner_category", partnerCategory)
                        .eq(partnerId != null, "partner_id", partnerId)
                        .ge(payDateFrom != null, "pay_date", payDateFrom)
                        .le(payDateTo != null, "pay_date", payDateTo)
                        .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                        .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                        .ge(amountFrom != null, "amount", amountFrom)
                        .le(amountTo != null, "amount", amountTo)
                        .like(customerBatchNo != null, "customer_batch_no", customerBatchNo, SqlLike.DEFAULT)
                        .orderBy("pay_date", false)
        );

        return getNames(cashTransactionDetailList);
    }

    /**
     * 修改支付失败与退票处理数据
     *
     * @param list
     * @return
     */
    public List<CashTransactionDetail> updateCashTransactionDetail(List<CashTransactionDetail> list) {
        if (list.stream().anyMatch(u -> u.getId() == null)) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        list.stream().forEach(u -> {
            cashTransactionLogService.createCashTransactionLog(u.getId(), "PAY_MODIFY",u.getRemark()==null?"":u.getRemark(),(u.getResponseMessage() != null ? u.getResponseMessage().getBytes() : new byte[0]));
        });
        this.updateBatchById(list);
        return list;
    }

    /**
     * 支付失败与退票 取消支付
     *
     * @param list
     * @return
     */
    public List<CashTransactionDetail> cancelPay(List<CashTransactionDetail> list) {

        list.stream().forEach(u -> {
            u.setPaymentStatus(SpecificationUtil.CANCE);
            this.updateById(u);
            cashTransactionLogService.createCashTransactionLog(u.getId(), "PAY_MODIFY",u.getRemark()==null?"":u.getRemark(),(u.getResponseMessage() != null ? u.getResponseMessage().getBytes() : new byte[0]));
        });
        return list;
    }

    /**
     * 支付失败与退票中 重新支付
     *
     * @param rePayDTO
     * @return
     */
    public void RePay(RePayDTO rePayDTO) {
        List<Long> ids = rePayDTO.getDetails().stream().map(CashTransactionDetail::getId).collect(Collectors.toList());
        List<CashTransactionDetail> details = baseMapper.selectBatchIds(ids);
        if (details.size() != ids.size()){
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        CashPayDTO payDTO = rePayDTO.getPayDTO();
        String curreny = details.get(0).getCurrency();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random r = new Random();
        String batchNum = sdf.format(new Date()) + r.nextInt(10000);
        for(CashTransactionDetail u : details) {
                if(!curreny.equals(u.getCurrency())){
                    throw new BizException(RespCode.PAYMENT_NOT_SAME_CURRENCY_CAN_NOT_PAY_CONCURRENTLY);
                }
                // 原明细更改为重新支付 开始
                u.setPaymentStatus(SpecificationUtil.REPAY);

                this.updateById(u);

                //原单据修改完毕 然后根据原单据新增一条明细
                u.setId(null);
                // 状态为支付中
                u.setPaymentStatus(SpecificationUtil.PAYING);
                u.setPaymentTypeId(payDTO.getPaymentTypeId());
                u.setDraweeAccountNumber(payDTO.getPayCompanyBankNumber());
                u.setCurrency(payDTO.getCurrency());
                u.setRequestTime(ZonedDateTime.now());
                try{
                    u.setPaymentTypeCode(cashPaymentMethodMapper.selectById(payDTO.getPaymentTypeId()).getPaymentMethodCode());
                }catch (Exception e){
                    e.printStackTrace();
                    throw new BizException(RespCode.PAYMENT_GET_PAYMENT_METHOD_ERROR);
                }
                //付款方式：用户所选的付款方式
                u.setPaymentTypeName(payDTO.getPaymentDescription());
                //付款方银行账号
                u.setDraweeAccountNumber(payDTO.getPayCompanyBankNumber());
                //付款方银行户名
                u.setDraweeAccountName(payDTO.getPayCompanyBankName());
                //付款出纳
                u.setDraweeId(OrgInformationUtil.getCurrentUserId());
                //退票日期为空
                u.setRefundDate(null);
                u.setRefundStatus(SpecificationUtil.NOREFUND);
                u.setExchangeRate(payDTO.getExchangeRate());
                u.setRemark(payDTO.getRemark());
                u.setPaymentBatchNumber(batchNum);
                //付款流水号
                u.setBillcode(organizationService.getCoding(BILL_CODE, u.getPaymentCompanyId()));
                u.setPayDate(ZonedDateTime.now());
                u.setCreatedDate(ZonedDateTime.now());
                u.setCreatedBy(OrgInformationUtil.getCurrentUserId());
                u.setLastUpdatedDate(ZonedDateTime.now());
                u.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
                u.setVersionNumber(1);
                baseMapper.insert(u);
                cashTransactionLogService.createCashTransactionLog(u.getId(), SpecificationUtil.LOG_REPAY, u.getRemark()==null?"":u.getRemark(),(u.getResponseMessage() != null ? u.getResponseMessage().getBytes() : new byte[0]));

        }
    }

    //获取可支付金额方法
    public BigDecimal getPableAmount(Long dataId) {
        //可支付金额=总金额-已提交金额-已核销金额
        CashTransactionData data = cashTransactionDataService.overrideSelectById(dataId);
        BigDecimal payableAmount = data.getAmount().subtract(data.getCommitedAmount()).subtract(data.getWriteOffAmount());
        return payableAmount;
    }


    /**
     * 报销单核销借款：查询借款详细信息-分页
     *
     * @param tenantId         租户id
     * @param companyId        公司id
     * @param partnerCategory  收款方类型
     * @param partnerId        收款方id
     * @param formId           报账单formID
     * @param exportHeaderId   报账单头ID
     * @param contractHeaderId 计划付款行关联合同头ID
     * @param documentType     单据类型
     * @param documentLineId   计划付款行ID
     * @return 结果
     */
    public List<CashPrepaymentQueryDTO> queryPrepaymentResultPage(Long tenantId,
                                          Long companyId,
                                          String partnerCategory,
                                          Long partnerId,
                                          Long formId,
                                          Long exportHeaderId,
                                          Long contractHeaderId,
                                          String documentType,
                                          Long documentLineId,
                                          String currencyCode,
                                          Page page) {
        //获取核销依据：关联相同合同/相同申请单
        /*CustomFormAssignInfoDTO customFormWriteOffInfo = expenseReportService.getCustomFormWriteOffInfo(formId, exportHeaderId);*/
        PaymentDetailWriteOffCondition condition = new PaymentDetailWriteOffCondition();
        condition.setTenantId(tenantId);
        condition.setCompanyId(companyId);
        condition.setPartnerCategory(partnerCategory);
        condition.setPartnerId(partnerId);
        /*condition.setSameContract(customFormWriteOffInfo.getSameContract());*/
        condition.setContractHeaderId(contractHeaderId);
        /*condition.setSameApplicationForm(customFormWriteOffInfo.getSameApplicationForm());*/
        condition.setDocumentType(documentType);
        condition.setDocumentHeaderId(exportHeaderId);
        condition.setDocumentLineId(documentLineId);
        condition.setCurrencyCode(currencyCode);
        // 若需关联相同申请单，且报账单未关联申请单，则直接返回空
        /*if(customFormWriteOffInfo.getSameApplicationForm() && CollectionUtils.isEmpty(customFormWriteOffInfo.getApplicationIdList())){
            return null;
        }else{
            condition.setApplicationIdList(customFormWriteOffInfo.getApplicationIdList());
        }*/
        List<CashPrepaymentQueryDTO> prepaymentResult = baseMapper.getPrepaymentResult(condition,page);
        return prepaymentResult;
    }

    public CashTransactionDetailWebDTO getDetailById(Long id) {
        return baseMapper.getDetailById(id);
    }

    public List<AmountAndDocumentNumberDTO> getTotalAmountAndDocumentNum(List<Long> paymentCompanyIds,String billcode, String documentNumber, String documentCategory, Long employeeId, ZonedDateTime requisitionDateFrom, ZonedDateTime requisitionDateTo, String paymentTypeCode, String partnerCategory, Long partnerId, BigDecimal amountFrom, BigDecimal amountTo, ZonedDateTime payDateFrom, ZonedDateTime payDateTo, String customerBatchNo, String paymentStatus,Boolean isRefundOrFail,String paymentMethodCategory) {
        List<String> payStatus = null;
        List<AmountAndDocumentNumberDTO> resultDTO = new ArrayList<>();
        if(SpecificationUtil.PARTPAY.equals(paymentStatus)){
           payStatus = Arrays.asList(SpecificationUtil.PARTPAY, SpecificationUtil.TOPAY);
        }else{
            payStatus = Arrays.asList(paymentStatus);
        }
        if(isRefundOrFail){
            resultDTO = baseMapper.getTotalAmountAndDocumentNum(
                    new EntityWrapper<CashTransactionDetail>()
                            .in(paymentCompanyIds != null,"payment_company_id",paymentCompanyIds)
                            .eq(employeeId != null, "employee_id", employeeId)
                            .eq(partnerCategory != null, "partner_category", partnerCategory)
                            .eq(paymentMethodCategory!=null,"payment_method_category",paymentMethodCategory)
                            .eq("operation_type",SpecificationUtil.PAYMENT)
                            .eq(partnerId != null, "partner_id", partnerId)
                            .ge(payDateFrom != null, "pay_date", payDateFrom)
                            .le(payDateTo != null, "pay_date", payDateTo)
                            .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                            .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                            .ge(amountFrom != null, "amount", amountFrom)
                            .le(amountTo != null, "amount", amountTo)
                            .eq(paymentTypeCode != null, "payment_type_code", paymentTypeCode)
                            .like("customer_batch_no", customerBatchNo)
                            .like("document_number", documentNumber)
                            .eq(documentCategory != null, "document_category", documentCategory)
                            .like("billcode", billcode)
                            .andNew("payment_status={0}", SpecificationUtil.PAYSUCCESS)
                            .eq("refund_status", SpecificationUtil.YESREFUND)
                            .or("(payment_status={0}", SpecificationUtil.PAYFAILURE)
                            .and(paymentStatus != null, "payment_status={0}", paymentStatus)
                            .and("1=1)")
            );
        }else {
            resultDTO = baseMapper.getTotalAmountAndDocumentNum(
                    new EntityWrapper<CashTransactionDetail>()
                            .in(paymentCompanyIds != null,"payment_company_id",paymentCompanyIds)
                            .like("billcode", billcode)
                            .like("document_number", documentNumber)
                            .eq(documentCategory != null, "document_category", documentCategory)
                            .eq("operation_type",SpecificationUtil.PAYMENT)
                            .eq(employeeId != null, "employee_id", employeeId)
                            .in(paymentStatus != null, "payment_status", payStatus)
                            .eq(paymentMethodCategory!=null,"payment_method_category",paymentMethodCategory)
                            .eq("refund_status", "N")
                            .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                            .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                            .eq(paymentTypeCode != null, "payment_type_code", paymentTypeCode)
                            .eq(partnerCategory != null, "partner_category", partnerCategory)
                            .eq(partnerId != null, "partner_id", partnerId)
                            .ge(amountFrom != null, "amount", amountFrom)
                            .le(amountTo != null, "amount", amountTo)
                            .ge(payDateFrom != null, "pay_date", payDateFrom)
                            .le(payDateTo != null, "pay_date", payDateTo)
                            .ge(amountFrom != null, "amount", amountFrom)
                            .le(amountTo != null, "amount", amountTo)
                            .like("customer_batch_no", customerBatchNo)
            );
        }
        return resultDTO;
    }


    public List<CashTransactionDetail> getDetailsByDataId(Long dataId){
        List<CashTransactionDetail> list = baseMapper.selectList(
                new EntityWrapper<CashTransactionDetail>()
                    .eq("csh_transaction_data_id",dataId)
                    .eq("payment_status",SpecificationUtil.PAYSUCCESS)
                    .eq("refund_status",SpecificationUtil.NOREFUND)

        );
        list.stream().forEach(cashTransactionDetail -> {
            String employeeCode = organizationService.getUserById(cashTransactionDetail.getEmployeeId()).getEmployeeCode();
            cashTransactionDetail.setEmployeeCode(employeeCode);
        });
        return list;
    }


    /*根据支付明细id查支付支付流水*/
    public PaymentOfFlowDetail getDetailFlowById(Long id){
        CashTransactionDetail detail = baseMapper.selectById(id);
        CashTransactionData data = cashTransactionDataService.selectById(detail.getCshTransactionDataId());
        if(detail==null || data ==null){
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        PaymentOfFlowDetail paymentOfFlowDetail = new PaymentOfFlowDetail();
        paymentOfFlowDetail.setPayStatus(detail.getPaymentStatus());

        //付款单据DTO
        paymentOfFlowDetail.setPayDocumentDTO(PayDocumentDTO.builder()
                .currency(detail.getCurrency())
                .documentApplicant(detail.getEmployeeName())
                .documentCode(detail.getDocumentNumber())
                .documentTotalAmount(data.getAmount())
                .documentId(detail.getDocumentId())
                .documentTypeCode(data.getDocumentTypeName())
                .documentCategory(detail.getDocumentCategory())
                .documentTypeName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_DOCUMENT_TYPE,
                        data.getDocumentCategory()).getName())
                .documentDate(detail.getRequisitionDate())
                .build())
        ;
        //支付明细
        PayDetailDTO payDetailDTO =  new PayDetailDTO();
        BeanUtils.copyProperties(detail,payDetailDTO);
        payDetailDTO.setPayAmount(detail.getAmount());
        payDetailDTO.setDraweeName(organizationService.listByUserIds(Arrays.asList(detail.getDraweeId())).get(0).getFullName());
        payDetailDTO.setDraweeCompanyName(organizationService.getById(detail.getDraweeCompanyId()).getName());

        //返回收款方类型名称
        payDetailDTO.setPartnerCategoryName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_PARTNER_CATEGORY,
                detail.getPartnerCategory()).getName());

        paymentOfFlowDetail.setPayDetailDTO(payDetailDTO);
        //财务信息
        FinancialDTO financialDTO = new FinancialDTO();
        paymentOfFlowDetail.setFinancialDTO(financialDTO);

        //核销历史
        List<WriteOffHistoryDTO> writeOffHistoryDTOS = new ArrayList<>();
        paymentOfFlowDetail.setWriteOffHistoryDTOS(writeOffHistoryDTOS);



        //操作日志
        List<CashTransactionLog> logs = cashTransactionLogService.selectList(new EntityWrapper<CashTransactionLog>()

                            .eq("payment_detail_id",detail.getId())
        );
        List<OperationDTO> operationDTOS = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(logs)){
            logs.forEach(log->{
                OperationDTO operationDTO = new OperationDTO();
                operationDTO.setOperationMan(organizationService.listByUserIds(Arrays.asList(log.getUserId())).get(0).getFullName());
                operationDTO.setOperationTime(log.getOperationTime());
                operationDTO.setOperationType(log.getOperationType());
                operationDTO.setRemark(log.getRemark());
                operationDTO.setOperationTypeName(organizationService.getSysCodeValueByCodeAndValue(PaymentSystemCustomEnumerationType.CSH_LOG_OPERATION_TYPE,
                        log.getOperationType()).getName());

                operationDTOS.add(operationDTO);
            });
        }
        paymentOfFlowDetail.setOperationDTO(operationDTOS);
        return paymentOfFlowDetail;
    }

    //退票
    public CashTransactionDetail refund(CashTransactionDetail dto,Date refundDate) {
        CashTransactionDetail detail = baseMapper.selectById(dto.getId());
        if (null == detail){
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        if (detail.getVersionNumber() == null || detail.getVersionNumber().compareTo(dto.getVersionNumber()) != 0){
            throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
        }
        //查询该支付是否有退票或者反冲
        List<String> typeList = new ArrayList<String>();
        typeList.add(SpecificationUtil.RESERVED);
        typeList.add(SpecificationUtil.RETURN);

        List<String> statusList = new ArrayList<>();
        statusList.add(SpecificationUtil.PAYING);
        statusList.add(SpecificationUtil.PAYSUCCESS);
        List<CashTransactionDetail> list = baseMapper.selectList(new EntityWrapper<CashTransactionDetail>()
            .in("operation_type",typeList)
            .in("payment_status",statusList)
            .eq("ref_cash_detail_id",detail.getId()));
        if (list.size() > 0){
            throw new BizException(RespCode.PAYMENT_DETAIL_NOT_REFUND);
        }
        Boolean accountStatus = getProfile();
        detail.setRefundStatus(SpecificationUtil.YESREFUND);
        detail.setRefundDate(ZonedDateTime.ofInstant(refundDate.toInstant(), ZoneId.systemDefault()));
        this.updateById(detail);

        cashTransactionLogService.createCashTransactionLog(detail.getId(),SpecificationUtil.LOG_PAY_REFUND,"",(detail.getResponseMessage() != null ? detail.getResponseMessage().getBytes() : new byte[0]));

        // 生成一笔退票的数据
        detail.setRefCashDetailId(detail.getId());
        detail.setRefBillCode(detail.getBillcode());
        detail.setId(null);
        detail.setBillcode(organizationService.getCoding(BILL_CODE, detail.getPaymentCompanyId())); // 支付明细
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random r = new Random();
        String batchNum = sdf.format(new Date()) + r.nextInt(10000);
        detail.setPaymentBatchNumber(batchNum); // 支付批次号
        detail.setAmount(detail.getAmount().multiply(BigDecimal.valueOf(-1))); // 金额
        detail.setRefundDate(null); // 退票日期
        detail.setRefundStatus(SpecificationUtil.YESREFUND); // 退票状态
        detail.setOperationType(SpecificationUtil.REFUND); // 操作类型
        detail.setPaymentStatus(SpecificationUtil.PAYSUCCESS); // 处理状态
        detail.setRemark(null); // 描述
        detail.setRequestTime(ZonedDateTime.now());
        // 单据Oid
        detail.setEntityOid(detail.getEntityOid());
        // 业务类型
        detail.setEntityType(detail.getEntityType());
        detail.setPayDate(ZonedDateTime.ofInstant(refundDate.toInstant(), ZoneId.systemDefault())); // 日期
        detail.setCreatedDate(ZonedDateTime.now());
        detail.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        detail.setLastUpdatedDate(ZonedDateTime.now());
        detail.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        detail.setVersionNumber(1);
        detail.setAccountStatus(accountStatus);
        baseMapper.insert(detail);
        cashTransactionLogService.createCashTransactionLog(detail.getId(),SpecificationUtil.LOG_NEW,detail.getRemark()==null?"":detail.getRemark(),(detail.getResponseMessage() != null ? detail.getResponseMessage().getBytes() : new byte[0]));
        List<PaymentDetail> paymentDetails = new ArrayList<>();
        PaymentDetail paymentDetail= getPaymentDetail(detail,cashTransactionDataService.selectById(detail.getCshTransactionDataId()),
                organizationService.getById(detail.getPaymentCompanyId()).getSetOfBooksId());

        paymentDetails.add(paymentDetail);

        // 往审批日志新增记录
        List<CommonApprovalHistoryCO> historyCOs = new ArrayList<>();
        CommonApprovalHistoryCO approvalHistoryDTO = setApprovalHistoryCO(detail,"退票", PaymentDocumentOperationEnum.REFUND.getId(),null);
        historyCOs.add(approvalHistoryDTO);

        // 往核算发送数据
        sendOtherService(historyCOs,paymentDetails, accountStatus);
        return detail;
        }

    public  PaymentDetail getPaymentDetail(CashTransactionDetail cashTransactionDetail, CashTransactionData data, Long setOfBookId){
        PaymentDetail paymentDetail = PaymentDetail.builder()
                .tenantId(cashTransactionDetail.getTenantId())
                .amount(cashTransactionDetail.getAmount())
                .billCode(cashTransactionDetail.getBillcode())
                .cashFlowItemId(cashTransactionDetail.getCashFlowItemId())
                .cshTransactionClassId(cashTransactionDetail.getCshTransactionClassId())
                .currency(cashTransactionDetail.getCurrency())
                .customerBatchNo(cashTransactionDetail.getPaymentBatchNumber())
                .documentCategory(cashTransactionDetail.getDocumentCategory())
                .documentCompanyId(cashTransactionDetail.getDocumentCompanyId())
                .documentNumber(cashTransactionDetail.getDocumentNumber())
                .doucmentExchangeRate(data.getExchangeRate())
                .draweeAccountNumber(cashTransactionDetail.getDraweeAccountNumber())
                .draweeCompanyId(cashTransactionDetail.getDraweeCompanyId())
                .employeeId(cashTransactionDetail.getEmployeeId())
                .partnerCategory(cashTransactionDetail.getPartnerCategory())
                .exchangeRate(cashTransactionDetail.getExchangeRate())
                .partnerCode(cashTransactionDetail.getPartnerCode())
                .partnerId(cashTransactionDetail.getPartnerId())
                .payDate(cashTransactionDetail.getPayDate())
                .paymentCompanyId(cashTransactionDetail.getPaymentCompanyId())
                .draweeCompanyId(cashTransactionDetail.getDraweeCompanyId())
                .paymentFileName(cashTransactionDetail.getPaymentFileName())
                .paymentMethodCategory(cashTransactionDetail.getPaymentMethodCategory())
                .paymentReturnStatus(cashTransactionDetail.getPaymentReturnStatus())
                .paymentStatus(cashTransactionDetail.getPaymentStatus())
                .paymentTypeId(cashTransactionDetail.getPaymentTypeId())
                .payPeriod(DateUtil.ZonedDateTimeToString(cashTransactionDetail.getPayDate()).substring(0,7))
                .refundStatus(cashTransactionDetail.getRefundStatus())
                .remark(cashTransactionDetail.getRemark())
                .setOfBooksId(setOfBookId)
                .id(cashTransactionDetail.getId())
                .accountDate(ZonedDateTime.now())
                .accountPeriod((DateUtil.ZonedDateTimeToString(ZonedDateTime.now()).substring(0,7)))
                .draweeAccountName(cashTransactionDetail.getDraweeAccountName())
                .documentDate(cashTransactionDetail.getRequisitionDate())
                .documentLineId(cashTransactionDetail.getDocumentLineId())
                .contractHeaderId(cashTransactionDetail.getContractHeaderId())
                .sourceDataId(cashTransactionDetail.getCshTransactionDataId())
                .sourceBillCode(cashTransactionDetail.getRefBillCode())
                .operationType(cashTransactionDetail.getOperationType())
                .reservedStatus(cashTransactionDetail.getReservedStatus())
                .partnerAccountNumber(cashTransactionDetail.getPayeeAccountNumber())
                .partnerAccountName(cashTransactionDetail.getPayeeAccountName())
                .attribute1(data.getAttribute1())
                .attribute2(data.getAttribute2())
                .attribute3(data.getAttribute3())
                .attribute4(data.getAttribute4())
                .attribute5(data.getAttribute5())
                .build();
        return paymentDetail;
    }

    @Transactional(readOnly = true)
    public List<CashTransactionDetail> getDetailByContractHeaderId(Page page, Long contractHeaderId) {

        return baseMapper.getDetailByContractHeaderId(page,contractHeaderId);
    }

    /**
     * @Author: bin.xie
     * @Description: 条件查询待退款数据(分页)
     * @param: page 分页参数
     * @param: billcode 支付流水号
     * @param: payDateFrom 支付日期从
     * @param: payDateTo 支付日期至
     * @param: amountFrom 金额从
     * @param: amountTo 金额至
     * @param: documentNumber 单据编号
     * @param: employeeId 员工ID
     * @param: documentCategory 单据类型
     * @param: partnerCategory   原单据收款方 退款的退款方 177601,EMPLOYEE
     * @return: java.util.List<com.hand.hcf.app.payment.domain.CashTransactionDetail>
     * @Date: Created in 2018/4/3 17:16
     * @Modified by
     */
    public List<CashTransactionDetail> getRefundDetailsByCondition(Page page,
                                                                   String billcode,
                                                                   String payDateFrom,
                                                                   String payDateTo,
                                                                   BigDecimal amountFrom,
                                                                   BigDecimal amountTo,
                                                                   String documentNumber,
                                                                   Long employeeId,
                                                                   String documentCategory,
                                                                   String partnerCategory,
                                                                   Long partnerId){
        ZonedDateTime payZonedDateFrom = DateUtil.stringToZonedDateTime(payDateFrom);
        ZonedDateTime payZonedDateTo = DateUtil.stringToZonedDateTime(payDateTo);
        if (payZonedDateTo != null){
            payZonedDateTo = payZonedDateTo.plusDays(1);
        }
        //退票状态为未退票
        String refundStatus = SpecificationUtil.NOREFUND;
        //操作状态 为支付
        String operationType = SpecificationUtil.PAYMENT;
        //支付状态 支取成功的
        String paymentStatus = SpecificationUtil.PAYSUCCESS;

        StringBuffer notExistsSQL = new StringBuffer("\n select 1 from  csh_transaction_detail d \n");
        notExistsSQL.append("WHERE d.ref_cash_detail_id =  c.id \n");
        notExistsSQL.append("AND d.operation_type = '" + SpecificationUtil.RESERVED + "'\n");
        notExistsSQL.append("AND d.payment_status in ('" + SpecificationUtil.PAYING + "','"
                + SpecificationUtil.PAYSUCCESS+"') \n");

        //查询
        List<CashTransactionDetail> list = baseMapper.selectRefundByPage(page,new EntityWrapper<CashTransactionDetail>()
                .eq("employee_id",employeeId)
                .eq("refund_status",refundStatus)
                .eq("operation_type",operationType)
                .eq("payment_status",paymentStatus)
                .and("(amount - refundAmountCommit> 0)")
                .like(StringUtils.hasText(billcode),"billcode",billcode)
                .ge(payZonedDateFrom != null, "pay_date", payZonedDateFrom)
                .lt(payZonedDateTo != null, "pay_date", payZonedDateTo)
                .ge(amountFrom != null,"amount",amountFrom)
                .le(amountTo != null,"amount",amountTo)
                .like(StringUtils.hasText(documentNumber),"document_number",documentNumber)
                .eq(StringUtils.hasText(documentCategory),"document_category",documentCategory)
                .eq(StringUtils.hasText(partnerCategory),"partner_category",partnerCategory)
                .eq(partnerId != null,"partner_id",partnerId)
                .notExists(notExistsSQL.toString())
                .orderBy("billcode",false));


        return getNames(list);
    }

    /**
     * @Author: bin.xie
     * @Description: 保存退款数据
     * @param: id
     * @param: versionNumber
     * @return: void
     * @Date: Created in 2018/4/8 15:02
     * @Modified by
     */
    public CashTransactionDetail saveRefundData(CashTransactionDetail cashTransactionDetail) {
        //查询该支付明细记录
        Page page = new Page(0,1);

        CashTransactionDetail queryDeatil = baseMapper.selectRefundByPage(page,
                new EntityWrapper<CashTransactionDetail>().eq("id",cashTransactionDetail.getId())).get(0);
        //校验版本
        if (!cashTransactionDetail.getVersionNumber().equals(queryDeatil.getVersionNumber())){
            throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
        }
        //如果前台传进来的可支付金额比查询出来的金额大则报错
        if (queryDeatil.getAbledRefundAmount().compareTo(cashTransactionDetail.getAbledRefundAmount()) == -1){
            throw new BizException(RespCode.PAYMENT_PAY_REFUND_AMOUNT_ERROR);
        }
        //收款方银行账户(原支付付款方)
        CompanyBank payCompanyBank = new CompanyBank();
        //退款方银行账户(原支付收款方)
        PartnerBankInfo payeeCompanyBank = new PartnerBankInfo();
        try {
            payCompanyBank = companyBankService.selectCompanyBankByBankAccountNumber(cashTransactionDetail.getDraweeAccountNumber());
            if (Constants.EMPLOYEE.equals(cashTransactionDetail.getPartnerCategory())) {
                payeeCompanyBank = organizationService.getEmployeeCompanyBankByCode(cashTransactionDetail.getPartnerId() ,cashTransactionDetail.getPayeeAccountNumber());
            }else{
                payeeCompanyBank = supplierService.getVenerCompanyBankByCode(cashTransactionDetail.getPayeeAccountNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(RespCode.PAYMENT_COMPANY_BANK_INFO_ERROR);
        }
        if(payCompanyBank==null){
            throw new BizException(RespCode.PAYMENT_PAY_COMPANY_BANK_INFO_NULL);
        }
        if(payeeCompanyBank == null || payeeCompanyBank.getBankCode() == null){
            throw new BizException(RespCode.PAYMENT_PAYEE_COMPANY_BANK_INFO_NULL);
        }
        //收款开户公司:付款账号的所属公司
        cashTransactionDetail.setDraweeCompanyId(payCompanyBank.getCompanyId());
        //收款出纳
        cashTransactionDetail.setDraweeId(OrgInformationUtil.getCurrentUserId());
        //收款方开户行行号
        cashTransactionDetail.setDraweeBankNumber(payCompanyBank.getBankCode());
        //收款方开户行所在省
        cashTransactionDetail.setDraweeBankProvinceCode(payCompanyBank.getProvinceCode());
        //收款方开户行所在市
        cashTransactionDetail.setDraweeBankCityCode(payCompanyBank.getCityCode());
        //收款方户名
        cashTransactionDetail.setDraweeAccountName(payCompanyBank.getBankAccountName());
        cashTransactionDetail.setRequestTime(ZonedDateTime.now());
        /**退款账户信息*/

        //退款方银行账号
        cashTransactionDetail.setPayeeAccountNumber(cashTransactionDetail.getPayeeAccountNumber());

        // 退款方开户行行号
        cashTransactionDetail.setPayeeBankNumber(payeeCompanyBank.getBankCode());
        // 退款方开户行名称 取支行名称
        cashTransactionDetail.setPayeeBankName(payeeCompanyBank.getBranchName());

        // 退款方开户行所在地
        cashTransactionDetail.setPayeeBankAddress(payeeCompanyBank.getAccountLocation());

        // 退款方银行户名
        cashTransactionDetail.setPayeeAccountName(payeeCompanyBank.getBankAccountName());

        //原支付ID
        cashTransactionDetail.setRefCashDetailId(queryDeatil.getId());
        //退款金额
        cashTransactionDetail.setAmount(TypeConversionUtils.roundHalfUp(cashTransactionDetail.getAbledRefundAmount()));
        //操作类型
        cashTransactionDetail.setOperationType(SpecificationUtil.RETURN);
        //付款状态
        cashTransactionDetail.setPaymentStatus(SpecificationUtil.NEWPAY);
        // 退款状态
        cashTransactionDetail.setPaymentReturnStatus(SpecificationUtil.NORETURN);
        // 反冲状态
        cashTransactionDetail.setReservedStatus(SpecificationUtil.NO_RESERVED);
        cashTransactionDetail.setId(null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Random r = new Random();
        String billCode = organizationService.getCoding(BILL_CODE, cashTransactionDetail.getPaymentCompanyId());
        String no = sdf.format(new Date()) + r.nextInt(10000);
        //新支付流水号
        cashTransactionDetail.setBillcode(billCode);
        //新批次号
        cashTransactionDetail.setPaymentBatchNumber(no);
        //原支付流水号
        cashTransactionDetail.setRefBillCode(queryDeatil.getBillcode());
        // 需要生成凭证
        cashTransactionDetail.setAccountStatus(false);
        // 单据Oid
        cashTransactionDetail.setEntityOid(queryDeatil.getEntityOid());
        // 业务类型
        cashTransactionDetail.setEntityType(queryDeatil.getEntityType());
        // 单据申请日期
        cashTransactionDetail.setRequisitionDate(queryDeatil.getRequisitionDate());
        baseMapper.insert(cashTransactionDetail);
        //记录日志
        detailLogService.insertLog(cashTransactionDetail.getId(), OrgInformationUtil.getCurrentUserId(),
                PaymentDocumentOperationEnum.GENERATE.getId(),null);
        return cashTransactionDetail;
    }

    /**
     * @Author: bin.xie
     * @Description: 更新退款数据
     * @param: cashTransactionDetail
     * @return: com.hand.hcf.app.payment.domain.CashTransactionDetail
     * @Date: Created in 2018/4/8 20:59
     * @Modified by
     */
    public CashTransactionDetail updateData(CashTransactionDetail cashTransactionDetail) {
        //查询该支付明细记录
        Page page = new Page(0,1);
        //原支付明细
        CashTransactionDetail queryDeatil = baseMapper.selectRefundByPage(page,
                new EntityWrapper<CashTransactionDetail>().eq("id",cashTransactionDetail.getRefCashDetailId())).get(0);
        //查询该支付明细
        CashTransactionDetail dto = baseMapper.selectById(cashTransactionDetail.getId());
        //校验版本
        if (!cashTransactionDetail.getVersionNumber().equals(dto.getVersionNumber())){
            throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
        }
        //校验状态
        if (!SpecificationUtil.NEWPAY.equals(dto.getPaymentStatus()) && !SpecificationUtil.PAYFAILURE.equals(dto.getPaymentStatus())){
            throw new BizException(RespCode.PAYMENT_DETAIL_UPDATE_ALLOW);
        }
        //如果前台传进来的可支付金额比原出来的金额大则报错
        if ((queryDeatil.getAbledRefundAmount().add(dto.getAmount())).compareTo(
                cashTransactionDetail.getAbledRefundAmount()) == -1){
            throw new BizException(RespCode.PAYMENT_PAY_REFUND_AMOUNT_ERROR);
        }

        //收款方银行账户(原支付付款方)
        CompanyBank payCompanyBank = new CompanyBank();
        //退款方银行账户(原支付收款方)
        PartnerBankInfo payeeCompanyBank = new PartnerBankInfo();
        try {
            payCompanyBank = companyBankService.selectCompanyBankByBankAccountNumber(cashTransactionDetail.getDraweeAccountNumber());
            if (Constants.EMPLOYEE.equals(cashTransactionDetail.getPartnerCategory())) {
                payeeCompanyBank = organizationService.getEmployeeCompanyBankByCode(cashTransactionDetail.getPartnerId() ,cashTransactionDetail.getPayeeAccountNumber());
            }else{
                payeeCompanyBank = supplierService.getVenerCompanyBankByCode(cashTransactionDetail.getPayeeAccountNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(RespCode.PAYMENT_COMPANY_BANK_INFO_ERROR);
        }
        if(payCompanyBank==null){
            throw new BizException(RespCode.PAYMENT_PAY_COMPANY_BANK_INFO_NULL);
        }
        if(payeeCompanyBank == null || payeeCompanyBank.getBankCode() == null){
            throw new BizException(RespCode.PAYMENT_PAYEE_COMPANY_BANK_INFO_NULL);
        }
        //收款开户公司:付款账号的所属公司
        cashTransactionDetail.setDraweeCompanyId(payCompanyBank.getCompanyId());
        //收款出纳
        cashTransactionDetail.setDraweeId(OrgInformationUtil.getCurrentUserId());
        //收款方开户行行号：drawee_bank_number，必输，付款账号所属明细银行联行号。
        cashTransactionDetail.setDraweeBankNumber(payCompanyBank.getBankCode());

        //收款方开户行所在省：drawee_bank_province_code，必输，付款账号所属明细银行所在省份代码。
        cashTransactionDetail.setDraweeBankProvinceCode(payCompanyBank.getProvinceCode());

        //收款方开户行所在市：drawee_bank_city_code，必输，付款账号所属明细银行所在城市代码。
        cashTransactionDetail.setDraweeBankCityCode(payCompanyBank.getCityCode());
        //收款方户名
        cashTransactionDetail.setDraweeAccountName(payCompanyBank.getBankAccountName());

        /**退款账户信息*/

        // 退款方银行账号：payee_account_number，必输，收款银行账号
        cashTransactionDetail.setPayeeAccountNumber(cashTransactionDetail.getPayeeAccountNumber());

        // 退款方开户行行号
        cashTransactionDetail.setPayeeBankNumber(payeeCompanyBank.getBankCode());
        // 退款方开户行名称 取支行名称
        cashTransactionDetail.setPayeeBankName(payeeCompanyBank.getBranchName());

        // 退款方开户行所在地
        cashTransactionDetail.setPayeeBankAddress(payeeCompanyBank.getAccountLocation());

        // 退款方银行户名
        cashTransactionDetail.setPayeeAccountName(payeeCompanyBank.getBankAccountName());

        //退款金额
        cashTransactionDetail.setAmount(TypeConversionUtils.roundHalfUp(cashTransactionDetail.getAmount()));
        //操作类型
        cashTransactionDetail.setOperationType(SpecificationUtil.RETURN);

        //原支付流水
        cashTransactionDetail.setRefCashDetailId(queryDeatil.getId());
        //原支付流水号
        cashTransactionDetail.setRefBillCode(queryDeatil.getBillcode());

        cashTransactionDetail.setLastUpdatedDate(ZonedDateTime.now());
        cashTransactionDetail.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        // 单据申请日期
        cashTransactionDetail.setRequisitionDate(queryDeatil.getRequisitionDate());
        this.updateById(cashTransactionDetail);

        return cashTransactionDetail;


    }

    public CashTransactionDetail selectRefundById(Long id){
        Page page = new Page(0,1);
        List<CashTransactionDetail> list = baseMapper.selectRefundByPage(page,new EntityWrapper<CashTransactionDetail>().eq("id",id));

        return getNames(list).get(0);
    }

    /**
     * @Author: bin.xie
     * @Description: 支付明细数据获取各种类型名称
     * @param: list
     * @return: java.util.List<com.hand.hcf.app.payment.domain.CashTransactionDetail>
     * @Date: Created in 2018/4/4 15:47
     * @Modified by
     */
    public List<CashTransactionDetail> getNames(List<CashTransactionDetail> list){
        Map<Long, String> venMap = new HashMap<>(16);
        Map<Long, String> empMap = new HashMap<>(16);
        Map<Long, String> empCodeMap = new HashMap<>(16);
        Map<String, String> sysCodeValues1Map = new HashMap<>(16);
        Map<String, String> sysCodeValues2Map = new HashMap<>(16);
        Map<String, String> sysCodeValues3Map = new HashMap<>(16);
        // 收款方类型
        List<SysCodeValueCO> sysCodeValues1 = organizationService.listAllSysCodeValueByCode(
                PaymentSystemCustomEnumerationType.CSH_PARTNER_CATEGORY);
        sysCodeValues1.forEach(e ->
            sysCodeValues1Map.put(e.getValue(), e.getName()));

        //返回支付状态名称
        List<SysCodeValueCO> sysCodeValues2 = organizationService.listAllSysCodeValueByCode(
                PaymentSystemCustomEnumerationType.CSH_PAYMENT_STATUS);

        sysCodeValues2.forEach(e -> sysCodeValues2Map.put(e.getValue(), e.getName()));

        //返回单据类型
        List<SysCodeValueCO> sysCodeValues3 = organizationService.listAllSysCodeValueByCode(
                PaymentSystemCustomEnumerationType.CSH_DOCUMENT_TYPE);

        sysCodeValues3.forEach(e -> sysCodeValues3Map.put(e.getValue(), e.getName()));

        for (CashTransactionDetail cashTransactionDetail : list){
            cashTransactionDetail.setAmount(TypeConversionUtils.roundHalfUp(cashTransactionDetail.getAmount()));
            String partnerName;
            //获取员工或供应商名字
            if (Constants.EMPLOYEE.equals(cashTransactionDetail.getPartnerCategory())) {
                if (!empMap.containsKey(cashTransactionDetail.getPartnerId())) {

                    List<ContactCO> partner = organizationService.listByUserIds(
                            Collections.singletonList(cashTransactionDetail.getPartnerId()));


                    if (!org.springframework.util.CollectionUtils.isEmpty(partner)) {
                        empMap.put(cashTransactionDetail.getPartnerId(),partner.get(0).getFullName());
                        empCodeMap.put(cashTransactionDetail.getPartnerId(), partner.get(0).getEmployeeCode());
                    }
                }
                partnerName = empMap.get(cashTransactionDetail.getPartnerId());
            } else {

                if (!venMap.containsKey(cashTransactionDetail.getPartnerId())) {

                    VendorInfoCO venInfoCO = supplierService.getOneVendorInfoByArtemis(cashTransactionDetail.getPartnerId().toString());

                    venMap.put(cashTransactionDetail.getPartnerId(),venInfoCO.getVenNickname());
                }
                partnerName = venMap.get(cashTransactionDetail.getPartnerId());
            }

            if (!empMap.containsKey(cashTransactionDetail.getEmployeeId())){
                List<ContactCO> partner = organizationService.listByUserIds(
                        Collections.singletonList(cashTransactionDetail.getEmployeeId()));
                if (!org.springframework.util.CollectionUtils.isEmpty(partner)) {
                    empMap.put(cashTransactionDetail.getEmployeeId(), partner.get(0).getFullName());
                    empCodeMap.put(cashTransactionDetail.getEmployeeId(), partner.get(0).getEmployeeCode());
                }
            }
            cashTransactionDetail.setEmployeeName(empMap.get(cashTransactionDetail.getEmployeeId()));
            cashTransactionDetail.setEmployeeCode(empCodeMap.get(cashTransactionDetail.getEmployeeId()));
            //收款方名称
            cashTransactionDetail.setPartnerName(partnerName);
            // 收款方类型
            cashTransactionDetail.setPartnerCategoryName(sysCodeValues1Map.get(cashTransactionDetail.getPartnerCategory()));
            // 支付状态
            cashTransactionDetail.setPaymentStatusName(sysCodeValues2Map.get(cashTransactionDetail.getPaymentStatus()));

            // 单据类型
            cashTransactionDetail.setDocumentTypeName(sysCodeValues3Map.get(cashTransactionDetail.getDocumentCategory()));
            cashTransactionDetail.setDocumentCategoryName(sysCodeValues3Map.get(cashTransactionDetail.getDocumentCategory()));
        }
        return list;
    }

    //根据支付流水号查询单据
    public List<CashTransactionDetail> getDetailsByBillCode(String billCode){
        List<CashTransactionDetail> list = baseMapper.selectList(
                new EntityWrapper<CashTransactionDetail>()
                        .eq("billcode", billCode)
        );
        return list;
    }


    /**
     * @Author: bin.xie
     * @Description: 分页查询的我退款数据
     * @param: page
     * @param: billcode
     * @param: returnDateFrom
     * @param: returnDateTo
     * @param: amountFrom
     * @param: amountTo
     * @param: refBillCode
     * @param: draweeAccountNumber
     * @param: payeeAccountNumber
     * @param: partnerCategory 177601,EMPlOYEE
     * @return: java.util.List<com.hand.hcf.app.payment.domain.CashTransactionDetail>
     * @Date: Created in 2018/4/9 11:26
     * @Modified by
     */
    public List<CashTransactionDetail>  getMyRefundByCondition(Page page,
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
                                                               String  backFlashStatus){
        ZonedDateTime returnZonedDateFrom = DateUtil.stringToZonedDateTime(returnDateFrom);
        ZonedDateTime returnZonedDateTo = DateUtil.stringToZonedDateTime(returnDateTo);
        if (returnZonedDateTo != null){
            returnZonedDateTo = returnZonedDateTo.plusDays(1);
        }
        //操作状态 为退款
        String operationType = SpecificationUtil.RETURN;
        //员工
        Long employeeId = OrgInformationUtil.getCurrentUserId();
        //查询
        List<CashTransactionDetail> list = baseMapper.selectPage(page,new EntityWrapper<CashTransactionDetail>()
                .eq("employee_id",employeeId)
                .eq("operation_type",operationType)
                .like(StringUtils.hasText(billcode),"billcode",billcode)
                .like(StringUtils.hasText(refBillCode),"ref_bill_code",refBillCode)
                .ge(returnZonedDateFrom != null, "pay_date", returnZonedDateFrom)
                .lt(returnZonedDateTo != null, "pay_date", returnZonedDateTo)
                .ge(amountFrom != null,"amount",amountFrom)
                .le(amountTo != null,"amount",amountTo)
                .eq(StringUtils.hasText(backFlashStatus),"payment_status",backFlashStatus)
                .eq(StringUtils.hasText(draweeAccountNumber),"drawee_account_number",draweeAccountNumber)
                .eq(StringUtils.hasText(payeeAccountNumber),"payee_account_number",payeeAccountNumber)
                .eq(StringUtils.hasText(partnerCategory),"partner_category",partnerCategory)
                .eq(partnerId != null,"partner_id",partnerId)
                .orderBy("billcode",false));
        return getNames(list);
    }

    /**
     * @Author: bin.xie
     * @Description: 根据退款明细ID，查询其数据，及其来源明细数据
     * @param: id
     * @return: com.hand.hcf.app.payment.web.dto.CashTransactionDetailRefundDTO
     * @Date: Created in 2018/4/9 13:16
     * @Modified by
     */
    public CashTransactionDetailRefundDTO queryMyRefundById(Long id) {
        Page page = new Page(0,1);
        //查出当前退款明细
        List<CashTransactionDetail> newList = baseMapper.selectRefundByPage(page,new EntityWrapper<CashTransactionDetail>().eq("id",id));

        CashTransactionDetail newCashTransactionDetail = getNames(newList).get(0);
        //设置附件oid成list
        if(!StringUtils.isEmpty(newCashTransactionDetail.getBackFlashAttachmentOids())){
            String[] str = newCashTransactionDetail.getBackFlashAttachmentOids().split(",");
            List<String> list = Arrays.asList(str);
            newCashTransactionDetail.setBacklashAttachmentOID(list);
            List<AttachmentCO> attachmentByOIDS = organizationService.listByOids(list);
            newCashTransactionDetail.setBacklashAttachments(attachmentByOIDS);
        }
        //查询来源明细
        List<CashTransactionDetail> oldList = baseMapper.selectRefundByPage(page,
               new EntityWrapper<CashTransactionDetail>().eq("id",newCashTransactionDetail.getRefCashDetailId()));
        CashTransactionDetail oldCashTransactionDetail = getNames(oldList).get(0);
        CashTransactionDetailRefundDTO dto = new CashTransactionDetailRefundDTO();
        dto.setNewCashTransactionDetail(newCashTransactionDetail);
        dto.setOldCashTransactionDetail(oldCashTransactionDetail);
        return dto;
    }

    /**
     * @Author: bin.xie
     * @Description: 根据ID删除退款明细数据
     * @param: id
     * @return: void
     * @Date: Created in 2018/4/9 14:41
     * @Modified by
     */
    public void deleteRefundById(Long id) {
        CashTransactionDetail  cashTransactionDetail = baseMapper.selectById(id);
        //判断数据存在
        if (cashTransactionDetail == null) {
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        //校验状态
        if (!SpecificationUtil.NEWPAY.equals(cashTransactionDetail.getPaymentStatus())
                && !SpecificationUtil.PAYFAILURE.equals(cashTransactionDetail.getPaymentStatus())){
            throw new BizException(RespCode.PAYMENT_DETAIL_UPDATE_ALLOW);
        }
        //校验操作类型
        if (!SpecificationUtil.RETURN.equals(cashTransactionDetail.getOperationType())){
            throw new BizException(RespCode.PAYMENT_DETAIL_DELETE_ALLOW);
        }
        if (StringUtils.hasText(cashTransactionDetail.getBackFlashAttachmentOids())){
            String [] ids = cashTransactionDetail.getBackFlashAttachmentOids().split(",");
            try {
                organizationService.deleteByOids(Arrays.asList(ids));
            }catch (Exception e){
                logger.error("删除关联附件失败！");
            }
        }
        baseMapper.deleteById(id);
        //删除操作日志
        detailLogService.delete(new EntityWrapper<DetailLog>().eq("detail_id",id));
    }

    /**
     * @Author: bin.xie
     * @Description: 退款数据进行提交、复核、拒绝操作
     * @param: dto
     * @return: com.hand.hcf.app.payment.domain.CashTransactionDetail
     * @Date: Created in 2018/4/9 16:31
     * @Modified by
     */
    public CashTransactionDetail operate(CashTransactionDetail dto) {
        CashTransactionDetail  cashTransactionDetail = baseMapper.selectById(dto.getId());
        if (cashTransactionDetail == null) {
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        //校验版本
        if (!dto.getVersionNumber().equals(cashTransactionDetail.getVersionNumber())){
            throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
        }
        //校验操作状态是否为退款
        if (!SpecificationUtil.RETURN.equals(cashTransactionDetail.getOperationType())){
            throw new BizException(RespCode.PAYMENT_DETAIL_DELETE_ALLOW);
        }
        //校验状态 当提交时必须为编辑中或者驳回
        if (SpecificationUtil.PAYING.equals(dto.getPaymentStatus())) {
            if (!SpecificationUtil.NEWPAY.equals(cashTransactionDetail.getPaymentStatus())
                    && !SpecificationUtil.PAYFAILURE.equals(cashTransactionDetail.getPaymentStatus())) {
                throw new BizException(RespCode.PAYMENT_DETAIL_UPDATE_ALLOW);
            }
            //校验是否正在反冲
            List<String> strList = new ArrayList<String>();
            //已复核
            strList.add(SpecificationUtil.PAYSUCCESS);
            //复核中
            strList.add(SpecificationUtil.PAYING);

            List<CashTransactionDetail> reverseList = baseMapper.selectList(new EntityWrapper<CashTransactionDetail>()
                .eq("operation_type",SpecificationUtil.RESERVED)
                .in("payment_status",strList)
                .eq("ref_cash_detail_id",cashTransactionDetail.getRefCashDetailId()));

            if (reverseList.size() > 0){
                //反冲中的数据不允许进行退款操作
                throw new BizException(RespCode.PAYMENT_PAY_REFUND_SUBMITED_ERROR);
            }
            //提交时校验退款金额
            //先查询原支付明细的金额
            CashTransactionDetail  oldCashTransactionDetail = baseMapper.selectById(dto.getRefCashDetailId());

            List<CashTransactionDetail> oldList = baseMapper.selectList(new EntityWrapper<CashTransactionDetail>()
                .eq("ref_cash_detail_id",oldCashTransactionDetail.getId())
                .in("payment_status",strList));
            //金额校验
            BigDecimal amount = BigDecimal.ZERO;
            for (CashTransactionDetail u : oldList){
                amount = amount.add(u.getAmount());
            }
            if(SpecificationUtil.PAYFAILURE.equals(dto.getPaymentStatus())){
                amount = amount.add(dto.getAmount());
            }
            if (amount.compareTo(oldCashTransactionDetail.getAmount()) == 1){
                throw  new BizException(RespCode.PAYMENT_PAY_REFUND_AMOUNT_ERROR);
            }
            //提交将状态置为支付中
            cashTransactionDetail.setPaymentStatus(SpecificationUtil.PAYING);
            //记录日志
            detailLogService.insertLog(cashTransactionDetail.getId(), OrgInformationUtil.getCurrentUserId(),
                    PaymentDocumentOperationEnum.APPROVAL.getId(),null);
        }
        //校验状态 当复核时必须为复核中
        Boolean flag = false;
        if (SpecificationUtil.PAYSUCCESS.equals(dto.getPaymentStatus())) {
            if (!SpecificationUtil.PAYING.equals(cashTransactionDetail.getPaymentStatus())) {
                throw new BizException(RespCode.PAYMENT_DETAIL_PASS_ALLOW);
            }
            //收款方银行账户(原支付付款方)
            CompanyBank payCompanyBank = new CompanyBank();
            try {
                payCompanyBank = companyBankService.selectCompanyBankByBankAccountNumber(dto.getDraweeAccountNumber());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException(RespCode.PAYMENT_COMPANY_BANK_INFO_ERROR);
            }
            if(payCompanyBank==null){
                throw new BizException(RespCode.PAYMENT_PAY_COMPANY_BANK_INFO_NULL);
            }
            //收款开户公司:付款账号的所属公司
            cashTransactionDetail.setDraweeCompanyId(payCompanyBank.getCompanyId());

            //收款方开户行行号：drawee_bank_number，必输，付款账号所属明细银行联行号。
            cashTransactionDetail.setDraweeBankNumber(payCompanyBank.getBankCode());

            //收款方开户行所在省：drawee_bank_province_code，必输，付款账号所属明细银行所在省份代码。
            cashTransactionDetail.setDraweeBankProvinceCode(payCompanyBank.getCity());

            //收款方开户行所在市：drawee_bank_city_code，必输，付款账号所属明细银行所在城市代码。
            cashTransactionDetail.setDraweeBankCityCode(payCompanyBank.getCity());
            //收款方户名
            cashTransactionDetail.setDraweeAccountName(payCompanyBank.getBankAccountName());
            //收款方账号
            cashTransactionDetail.setDraweeAccountNumber(dto.getDraweeAccountNumber());
            // 退款日期
            cashTransactionDetail.setPayDate(dto.getPayDate());
            //备注
            cashTransactionDetail.setRemark(dto.getRemark());
            //状态设置为成功
            cashTransactionDetail.setPaymentStatus(SpecificationUtil.PAYSUCCESS);
            Boolean accountStatus = getProfile();
            flag = accountStatus;
            // 需要生成凭证
            cashTransactionDetail.setAccountStatus(accountStatus);
            //查询原单据
            CashTransactionDetail oldCashTransactionDetail = baseMapper.selectById(cashTransactionDetail.getRefCashDetailId());
            //原单据已退款金额
            List<CashTransactionDetail> retrunList = baseMapper.selectList(new EntityWrapper<CashTransactionDetail>()
                .eq("ref_cash_detail_id",oldCashTransactionDetail.getId())
                .eq("payment_status",SpecificationUtil.PAYSUCCESS));
            BigDecimal amount = BigDecimal.ZERO;
            for (CashTransactionDetail u : retrunList){
                amount = amount.add(u.getAmount());
            }
            //原单据已退款金额+本次退款金额 跟原单据总金额比较 如果相等置为全部退款，反正部分退款
            amount = amount.add(cashTransactionDetail.getAmount());
            if (oldCashTransactionDetail.getAmount().compareTo(amount) == 0){
                oldCashTransactionDetail.setPaymentReturnStatus(SpecificationUtil.YESRETURN);
            }else{
                oldCashTransactionDetail.setPaymentReturnStatus(SpecificationUtil.PARTRETURN);
            }
            //更新原单据记录
            oldCashTransactionDetail.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
            oldCashTransactionDetail.setLastUpdatedDate(ZonedDateTime.now());
            this.updateById(oldCashTransactionDetail);
            //记录日志
            detailLogService.insertLog(cashTransactionDetail.getId(), OrgInformationUtil.getCurrentUserId(),
                    PaymentDocumentOperationEnum.APPROVAL_PASS.getId(),dto.getApprovedMsg());
            // 支付操作日志记录原明细
            cashTransactionLogService.createPayOperatorLog(
                    cashTransactionDetail.getRefCashDetailId(),
                    SpecificationUtil.LOG_PAY_RETURN,
                    (cashTransactionDetail.getRemark() == null ? "" : cashTransactionDetail.getRemark() + "--") + "退款金额：" + cashTransactionDetail.getAmount(),
                    cashTransactionDetail.getCreatedBy());
        }

        //校验状态 当驳回时必须为复核
        if (SpecificationUtil.PAYFAILURE.equals(dto.getPaymentStatus())) {
            if (!SpecificationUtil.PAYING.equals(cashTransactionDetail.getPaymentStatus())) {
                throw new BizException(RespCode.PAYMENT_DETAIL_REJECT_ALLOW);
            }
            //驳回将状态置为失败
            cashTransactionDetail.setPaymentStatus(SpecificationUtil.PAYFAILURE);
            //记录日志
            detailLogService.insertLog(cashTransactionDetail.getId(), OrgInformationUtil.getCurrentUserId(),
                    PaymentDocumentOperationEnum.APPROVAL_REJECT.getId(),dto.getApprovedMsg());
        }
        //设置状态
        cashTransactionDetail.setPaymentStatus(dto.getPaymentStatus());
        this.updateById(cashTransactionDetail);
        // 复核通过需要生成凭证
        if (SpecificationUtil.PAYSUCCESS.equals(dto.getPaymentStatus())){
            // 支付明细数据
            CashTransactionData data = cashTransactionDataService.selectById(cashTransactionDetail.getCshTransactionDataId());

            List<PaymentDetail> paymentDetails = new ArrayList<>();
            PaymentDetail paymentDetail= getPaymentDetail(cashTransactionDetail,data,
                    organizationService.getById(data.getCompanyId()).getSetOfBooksId());
            paymentDetails.add(paymentDetail);

            // 往审批日志新增记录
            List<CommonApprovalHistoryCO> historyCOs = new ArrayList<>();
            CommonApprovalHistoryCO approvalHistoryCO = setApprovalHistoryCO(cashTransactionDetail,"退款", PaymentDocumentOperationEnum.RETURN.getId(),dto.getApprovedMsg());
            historyCOs.add(approvalHistoryCO);

            sendOtherService(historyCOs,paymentDetails, flag);
        }
        return cashTransactionDetail;
    }

    /**
     * @Author: bin.xie
     * @Description: 分页查询未复核的退款数据
     * @param: page 分页参数
     * @param: billcode 流水号
     * @param: returnDateFrom 退款日期从
     * @param: returnDateTo 退款日期至
     * @param: amountFrom 金额从
     * @param: amountTo 金额至
     * @param: refBillCode 原付款流水号
     * @param: draweeAccountNumber 收款方
     * @param: payeeAccountNumber 退款方
     * @param: partnerCategory
     * @param: backFlashStatus 状态
     * @return
     * @Date: Created in 2018/5/7 14:49
     * @Modified by
     */
    public List<CashTransactionDetail> selectUncheckData(Page page,
                                                         String billcode,
                                                         String    returnDateFrom,//退款日期从
                                                         String    returnDateTo, //退款日期至
                                                         BigDecimal amountFrom,
                                                         BigDecimal amountTo,
                                                         String refBillCode,
                                                         String draweeAccountNumber,
                                                         String payeeAccountNumber,
                                                         String partnerCategory,
                                                         Long partnerId,
                                                         String backFlashStatus) {
        ZonedDateTime returnZonedDateFrom = DateUtil.stringToZonedDateTime(returnDateFrom);
        ZonedDateTime returnZonedDateTo = DateUtil.stringToZonedDateTime(returnDateTo);
        if (returnZonedDateTo != null){
            returnZonedDateTo = returnZonedDateTo.plusDays(1);
        }
        //操作状态 为退款
        String operationType = SpecificationUtil.RETURN;
        //员工
        Long companyId = OrgInformationUtil.getCurrentCompanyId();
        //查询
        List<CashTransactionDetail> list = baseMapper.selectPage(page,new EntityWrapper<CashTransactionDetail>()
                .eq("payment_company_id",companyId)
                .eq("operation_type",operationType)
                .like(StringUtils.hasText(billcode),"billcode",billcode)
                .like(StringUtils.hasText(refBillCode),"ref_bill_code",refBillCode)
                .ge(returnZonedDateFrom != null, "pay_date", returnZonedDateFrom)
                .lt(returnZonedDateTo != null, "pay_date", returnZonedDateTo)
                .ge(amountFrom != null,"amount",amountFrom)
                .le(amountTo != null,"amount",amountTo)
                .eq("payment_status",SpecificationUtil.PAYING)
                .eq(StringUtils.hasText(backFlashStatus),"payment_status",backFlashStatus)
                .eq(StringUtils.hasText(draweeAccountNumber),"drawee_account_number",draweeAccountNumber)
                .eq(StringUtils.hasText(payeeAccountNumber),"payee_account_number",payeeAccountNumber)
                .eq(StringUtils.hasText(partnerCategory),"partner_category",partnerCategory)
                .eq(partnerId != null ,"partner_id",partnerId)
                .orderBy("billcode",false));
        return getNames(list);
    }

    /**
     * @Author: bin.xie
     * @Description: 分页查询已复核的退款数据
     * @param: page
     * @param: billcode
     * @param: returnDateFrom
     * @param: returnDateTo
     * @param: amountFrom
     * @param: amountTo
     * @param: refBillCode
     * @param: draweeAccountNumber
     * @param: payeeAccountNumber
     * @param: partnerCategory 177601,EMPLOYEE
     * @param: backFlashStatus
     * @return: java.util.List<com.hand.hcf.app.payment.domain.CashTransactionDetail>
     * @Date: Created in 2018/4/16 10:14
     * @Modified by
     */
    public List<CashTransactionDetail> selectCheckedData(Page page,
                                                         String billcode,
                                                         String    returnDateFrom,//退款日期从
                                                         String    returnDateTo, //退款日期至
                                                         BigDecimal amountFrom,
                                                         BigDecimal amountTo,
                                                         String refBillCode,
                                                         String draweeAccountNumber,
                                                         String payeeAccountNumber,
                                                         String partnerCategory,
                                                         Long partnerId,
                                                         String backFlashStatus) {
        ZonedDateTime returnZonedDateFrom = DateUtil.stringToZonedDateTime(returnDateFrom);
        ZonedDateTime returnZonedDateTo = DateUtil.stringToZonedDateTime(returnDateTo);
        if (returnZonedDateTo != null){
            returnZonedDateTo = returnZonedDateTo.plusDays(1);
        }
        //操作状态 为退款
        String operationType = SpecificationUtil.RETURN;
        //员工
        Long companyId = OrgInformationUtil.getCurrentCompanyId();

        //查询
        List<CashTransactionDetail> list = baseMapper.selectPage(page,new EntityWrapper<CashTransactionDetail>()
                .eq("payment_company_id",companyId)
                .eq("operation_type",operationType)
                .like(StringUtils.hasText(billcode),"billcode",billcode)
                .like(StringUtils.hasText(refBillCode),"ref_bill_code",refBillCode)
                .ge(returnZonedDateFrom != null, "pay_date", returnZonedDateFrom)
                .lt(returnZonedDateTo != null, "pay_date", returnZonedDateTo)
                .ge(amountFrom != null,"amount",amountFrom)
                .lt(amountTo != null,"amount",amountTo)
                .eq("payment_status",SpecificationUtil.PAYSUCCESS)
                .eq(StringUtils.hasText(backFlashStatus),"payment_status",backFlashStatus)
                .eq(StringUtils.hasText(draweeAccountNumber),"drawee_account_number",draweeAccountNumber)
                .eq(StringUtils.hasText(payeeAccountNumber),"payee_account_number",payeeAccountNumber)
                .eq(StringUtils.hasText(partnerCategory),"partner_category",partnerCategory)
                .eq(partnerId != null,"partner_id", partnerId)
                .orderBy("billcode",false));
        return getNames(list);
    }

    /**
     * @Author: bin.xie
     * @Description: 根据合同ID查询支付明细
     * @param: headerId
     * @return: java.util.List<com.hand.hcf.app.payment.domain.CashTransactionDetail>
     * @Date: Created in 2018/4/19 15:30
     * @Modified by
     */
    public List<CashTransactionDetail> listCashDetailByHeaderId(Long headerId) {
        List<CashTransactionDetail> list = baseMapper.selectList(new EntityWrapper<CashTransactionDetail>()
                .eq("contract_header_id",headerId));
        return getNames(list);
    }
    /**
     * @Author: bin.xie
     * @Description: 退款提交
     * @param: cashTransactionDetail
     * @return: com.hand.hcf.app.payment.domain.CashTransactionDetail
     * @Date: Created in 2018/4/20 14:53
     * @Modified by
     */
    /*@Lock(name = SyncLockPrefix.CSH_TRANSACTION_DATA, keys = {"#dto.refCashDetailId"}, lockType = LockType.TRY_LOCK)*/
    public CashTransactionDetail submitRefund(CashTransactionDetail dto){

        CashTransactionDetail  cashTransactionDetail = baseMapper.selectById(dto.getId());

        if (cashTransactionDetail == null) {
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        //校验版本
        if (!dto.getVersionNumber().equals(cashTransactionDetail.getVersionNumber())){
            throw new BizException(RespCode.SYS_VERSION_NUMBER_CHANGED);
        }
        //校验操作状态是否为退款
        if (!SpecificationUtil.RETURN.equals(cashTransactionDetail.getOperationType())){
            throw new BizException(RespCode.PAYMENT_DETAIL_DELETE_ALLOW);
        }
        //校验状态 当提交时必须为编辑中或者驳回
        if (!SpecificationUtil.NEWPAY.equals(cashTransactionDetail.getPaymentStatus())
                && !SpecificationUtil.PAYFAILURE.equals(cashTransactionDetail.getPaymentStatus())) {
            throw new BizException(RespCode.PAYMENT_DETAIL_UPDATE_ALLOW);
        }
        //先查询原支付明细的金额
        CashTransactionDetail  oldCashTransactionDetail = baseMapper.selectById(dto.getRefCashDetailId());
        //原支付明细加锁
        //校验是否正在反冲
        List<String> strList = new ArrayList<String>();
        //已复核
        strList.add(SpecificationUtil.PAYSUCCESS);
        //复核中
        strList.add(SpecificationUtil.PAYING);

        List<CashTransactionDetail> reverseList = baseMapper.selectList(
                new EntityWrapper<CashTransactionDetail>()
                        .eq("operation_type",SpecificationUtil.RESERVED)
                        .in("payment_status",strList)
                        .eq("ref_cash_detail_id",cashTransactionDetail.getRefCashDetailId()));
        if (reverseList.size() > 0){
            //反冲中的数据不允许进行退款操作
            throw new BizException(RespCode.PAYMENT_PAY_REFUND_SUBMITED_ERROR);
        }
        //校验退款金额

        List<CashTransactionDetail> oldList = baseMapper.selectList(
                new EntityWrapper<CashTransactionDetail>()
                        .eq("ref_cash_detail_id",oldCashTransactionDetail.getId())
                        .in("payment_status",strList));
        //金额校验
        BigDecimal amount = BigDecimal.ZERO;
        for (CashTransactionDetail u : oldList){
            amount = amount.add(u.getAmount());
        }
        amount = amount.add(dto.getAmount());
        if (amount.compareTo(oldCashTransactionDetail.getAmount()) == 1){
            throw  new BizException(RespCode.PAYMENT_PAY_REFUND_AMOUNT_ERROR);
        }

        //提交将状态置为支付中
        cashTransactionDetail.setPaymentStatus(SpecificationUtil.PAYING);
        //记录日志
        detailLogService.insertLog(cashTransactionDetail.getId(), OrgInformationUtil.getCurrentUserId(),
                PaymentDocumentOperationEnum.APPROVAL.getId(),null);
        cashTransactionDetail.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        cashTransactionDetail.setLastUpdatedDate(ZonedDateTime.now());
        //设置状态
        cashTransactionDetail.setPaymentStatus(dto.getPaymentStatus());
        this.updateById(cashTransactionDetail);
        return cashTransactionDetail;
    }
    /**
     * @Author: bin.xie
     * @Description:
     * @param: type 0-支付 1-退款
     * @param: flag  0-申请人为当前登陆人 1-当前登陆的公司
     * @return: java.util.List<com.hand.hcf.app.payment.PartnerSelectDTO>
     * @Date: Created in 2018/4/28 15:47
     * @Modified by
     */
    public List<PartnerSelectDTO> listPartner(int type,int flag){
        String operatorType;
        if (type == 1){
            operatorType = SpecificationUtil.RETURN;
        }else{
            operatorType = SpecificationUtil.PAYMENT;
        }
        List<PartnerSelectDTO> dtos = baseMapper.listPartner(
                new EntityWrapper()
                        .eq(flag == 0,"employee_id", OrgInformationUtil.getCurrentUserId())
                        .eq(type == 0,"payment_status",SpecificationUtil.PAYSUCCESS)
                        .eq(flag == 1,"payment_company_id", OrgInformationUtil.getCurrentCompanyId())
                        .ne(type == 0,"payment_return_status",SpecificationUtil.IS_RESERVED)
                        .eq("operation_type",operatorType));
        String partnerName = null;
        Map<Long, String> empMap = new HashMap<>();
        Map<Long, String> venMap = new HashMap<>();
        List<PartnerSelectDTO> list = new ArrayList<>();
        for (PartnerSelectDTO t : dtos) {
            //获取员工或供应商名字
            if (Constants.EMPLOYEE.equals(t.getPartnerCategory())) {
                if (!empMap.containsKey(t.getPartnerId())) {

                    List<ContactCO> partner = organizationService.listByUserIds(
                            Arrays.asList(t.getPartnerId()));

                    empMap.put(t.getPartnerId(), partner.get(0).getFullName());
                }
                partnerName = empMap.get(t.getPartnerId());
            } else {

                if (!venMap.containsKey(t.getPartnerId())) {

                    VendorInfoCO venInfoCO = supplierService.getOneVendorInfoByArtemis(t.getPartnerId().toString());

                    venMap.put(t.getPartnerId(), venInfoCO.getVenNickname());
                }
                partnerName = venMap.get(t.getPartnerId());
            }

            //收款方名称
            t.setPartnerName(partnerName);

        }
        return dtos;
    }

    @Override
    public Page<CashTransactionDetail> selectPage(Page<CashTransactionDetail> page, Wrapper<CashTransactionDetail> wrapper){
        SqlHelper.fillWrapper(page, wrapper);
        page.setRecords(baseMapper.overrideSelectPage(page, wrapper));
        return  page;
    }

    public CommonApprovalHistoryCO setApprovalHistoryCO(CashTransactionDetail detail, String msgType, Integer operation, String msg){
        CommonApprovalHistoryCO co = new CommonApprovalHistoryCO();
        co.setEntityOid(UUID.fromString(detail.getEntityOid()));
        co.setEntityType(detail.getEntityType());
        co.setOperation(operation);
        co.setOperationDetail((TypeConversionUtils.parseString(msg) == null ? "": (msg + "--")) + msgType + "金额：" + detail.getAmount());
        co.setOperatorOid(OrgInformationUtil.getCurrentUserOid());
        return co;
    }

    /**
     * 支付等相关操作成功生成相关日志、生成相关凭证
     * @param historyCOs 日志明细
     * @param paymentDetails 凭证明细
     */
    public  void sendOtherService(List<CommonApprovalHistoryCO> historyCOs, List<PaymentDetail> paymentDetails, Boolean accountStatus){
        List<ApprovalHistoryCO> logs;
        try {
            logs = organizationService.createLogsByPaymentService(historyCOs);
        }catch (Exception e){
            throw new BizException(RespCode.PAYMENT_LOGS_CREATE_FAILURE);
        }
        // 往核算模块发送数据
        //bo.liu 核算
        /*String result = accountingService.sendCashTransactionDetails(OrgInformationUtil.getCurrentUserId(), paymentDetails, accountStatus);
        if (!"SUCCESS".equals(result)) {
            logger.error("生成凭证信息失败！开始回滚日志信息");
            // 往核算发送失败时需要删除已创建的日志记录
            try {
                organizationService.deletLogsByPaymentService(logs);
            } catch (Exception e) {
                logger.error("删除生成的日志信息失败！");
                throw new BizException(RespCode.PAYMENT_LOGS_DELETE_FAILURE);
            }
            throw new BizException(RespCode.PAYMENT_FAILURE_ACCOUNTING_MODULE, new String[]{result});
        }*/
    }
    /**
     * @Description: 根据报账单头ID查询报账单支付信息
     * @param: page
     * @param: headerId
     * @return
     * @Date: Created in 2018/6/15 16:29
     * @Modified by
     */
    @Transactional(readOnly = true)
    public List<CashTransactionDetail> getDetailByPublicHeaderId(Page page, Long headerId,String billCode) {
        List<CashTransactionDetail> list = baseMapper.getDetailByPublicHeaderId(page,headerId,
                new EntityWrapper<CashTransactionDetail>()
                        .eq("a.payment_status",SpecificationUtil.PAYSUCCESS)
                        .in("a.operation_type",Arrays.asList(SpecificationUtil.PAYMENT,SpecificationUtil.RETURN,SpecificationUtil.RESERVED))
                        .like(TypeConversionUtils.isNotEmpty(billCode),"a.billcode",billCode)
        );
        return getNames(setDraweeName(list), true);
    }

    public Map<Long, List<PublicReportWriteOffCO>> selectIdByPrePayment(Long headerId, List<Long> lineIds){
        List<PublicReportWriteOffCO> list = baseMapper.selectIdByPrePayment(headerId, lineIds);

        return list.stream().collect(Collectors.groupingBy(PublicReportWriteOffCO::getLineId));
    }



    /**
     * @Description: 根据单据类型，头ID查询单据的支付信息
     * @param: page
     * @param: headerId
     * @return
     * @Date: Created in 2018/6/15 16:29
     * @Modified by
     */
    @Transactional(readOnly = true)
    public List<CashTransactionDetail> getPaymentDetailsByDocumentId(Page page,
                                                                     Long headerId,
                                                                     String billCode,
                                                                     String documentCategory) {
        List<CashTransactionDetail> list = baseMapper.selectPage(page,
                new EntityWrapper<CashTransactionDetail>()
                        .eq("document_id",headerId)
                        .eq("payment_status",SpecificationUtil.PAYSUCCESS)
                        .eq("document_category",documentCategory)
                        .in("operation_type",Arrays.asList(SpecificationUtil.PAYMENT,SpecificationUtil.RETURN,SpecificationUtil.RESERVED))
                        .like(TypeConversionUtils.isNotEmpty(billCode),"billcode",billCode)
        );

        return getNames(setDraweeName(list), true);
    }

    /**
     * @Description: 设置出纳名称
     * @param: list
     * @return
     * @Date: Created in 2018/7/10 14:01
     * @Modified by
     */
    private List<CashTransactionDetail> setDraweeName(List<CashTransactionDetail> list){
        if(CollectionUtils.isNotEmpty(list)){
            List<Long> userIds = list.stream().map(CashTransactionDetail::getDraweeId).collect(Collectors.toList());
            List<ContactCO> userInfoCOs = organizationService.listByUserIds(userIds);
            list.forEach(e -> {
                List<ContactCO> filterList = userInfoCOs.stream().filter(v -> v.getId().equals(e.getDraweeId())).collect(Collectors.toList());
                if (filterList != null && filterList.size() > 0) {
                    e.setDraweeName(filterList.get(0).getFullName());
                }
            });
        }
        return list;
    }

    public Boolean getProfile(){
        // 判断当前登陆人所在机构是否允许支付往核算模块发送数据
        String success = "Y";
        String parameterValueByParameterCode =
                organizationService.getParameterValueByParameterCode(ParameterCode.PAYMENT_ACCOUNTING_ENABLED, null, OrgInformationUtil.getCurrentCompanyId());
        if(success.equals(parameterValueByParameterCode)){
            return true;
        }else{
            return Boolean.FALSE;
        }
    }

    private List<CashTransactionDetail> getNames(List<CashTransactionDetail> list, Boolean queryCompanyName){
        if (CollectionUtils.isEmpty(list)){
            return list;
        }
        if (queryCompanyName) {
            list = getNames(list);
            // 返回付款公司id的集合
            List<Long> companyIds = list
                    .stream()
                    .map(CashTransactionDetail::getPaymentCompanyId)
                    .collect(Collectors.toList());
            // 查询所有的付款公司
            List<CompanyCO> companySumCOs = organizationService.listByIds(companyIds);
            // 将公司集合转换为Map
            Map<Long, CompanyCO> collect = companySumCOs
                    .stream()
                    .collect(Collectors.toMap(CompanyCO::getId, e -> e, (k1, k2) -> k1));
            list.forEach(e ->
                e.setPaymentCompanyName(collect.containsKey(e.getPaymentCompanyId()) ?
                        collect.get(e.getPaymentCompanyId()).getName() : null));
            return list;
        }else{
            return getNames(list);
        }
    }

    /**
     * 资金回写支付明细表
     * @param cos
     * @return
     */
//    @LcnTransaction
    public Boolean updateCashTransactionDetailByFund(List<CashTransactionDetailCO> cos) {
        if (CollectionUtils.isEmpty(cos)) {
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        List<CashTransactionDetail> details = new ArrayList<>(8);
        for (CashTransactionDetailCO co: cos) {
            if (StringUtil.isNullOrEmpty(co.getBillcode())) {
                throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
            }
            CashTransactionDetail detail = this.selectOne(
                    new EntityWrapper<CashTransactionDetail>().eq("billcode", co.getBillcode())
            );
            if (detail == null) {
                throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
            }
            detail.setPaymentFileName(co.getPaymentFileName());
            detail.setPaymentBatchNumber(co.getPaymentBatchNumber());
            detail.setPaymentLineNumber(co.getPaymentLineNumber());

            detail.setDraweeCompanyId(co.getDraweeCompanyId());

            detail.setPaymentTypeId(co.getPaymentTypeId());
            detail.setPaymentTypeCode(co.getPaymentTypeCode());
            detail.setPaymentTypeName(co.getPaymentTypeName());

            detail.setPayDate(co.getPayDate());
            detail.setPaymentStatus(co.getPaymentStatus());
            detail.setRefundStatus(co.getRefundStatus());

            detail.setDraweeId(co.getDraweeId());
            detail.setDraweeAccountNumber(co.getDraweeAccountNumber());
            detail.setDraweeAccountName(co.getDraweeAccountName());
            detail.setDraweeBankNumber(co.getDraweeBankNumber());
            detail.setDraweeBankProvinceCode(co.getDraweeBankProvinceCode());
            detail.setDraweeBankCityCode(co.getDraweeBankCityCode());

            detail.setResponseCode(co.getResponseCode());
            detail.setResponseMessage(co.getResponseMessage());

            detail.setReturnState(co.getReturnState());
            detail.setResultCode(co.getResultCode());
            detail.setResultMessage(co.getResultMessage());
            detail.setAccCheckCode(co.getAccCheckCode());
            detail.setAccCheckDate(co.getAccCheckDate());
            detail.setReturnNumber(co.getReturnNumber());

            details.add(detail);
        }

        updateBatchById(details);

        return true;
    }

    /**
     *   根据已付金额来确定报销单数据
     * @param
     * @return
     */
    public List<CashTransactionDetailCO> queryCashTransactionDetailByReport(BigDecimal paidAmountFrom, BigDecimal paidAmountTo,String backlashFlag) {

       return baseMapper.queryCashTransactionDetailByReport(new EntityWrapper<CashTransactionDetail>()
                                                        .gt(paidAmountFrom!=null,"t.amount",paidAmountFrom)
                                                        .lt(paidAmountTo!=null,"t.amount",paidAmountTo)
                                                        .eq(!StringUtils.isEmpty(backlashFlag),"t.reserved_status",backlashFlag)
                                                         );
    }
}
