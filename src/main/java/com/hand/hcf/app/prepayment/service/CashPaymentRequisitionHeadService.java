package com.hand.hcf.app.prepayment.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.SupplierImplementControllerImpl;
import com.hand.hcf.app.prepayment.domain.CashPayRequisitionType;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionHead;
import com.hand.hcf.app.prepayment.domain.CashPaymentRequisitionLine;
import com.hand.hcf.app.prepayment.domain.enumeration.Constants;
import com.hand.hcf.app.prepayment.externalApi.*;
import com.hand.hcf.app.prepayment.persistence.CashPaymentRequisitionHeadMapper;
import com.hand.hcf.app.prepayment.utils.ContractOperationType;
import com.hand.hcf.app.prepayment.utils.RespCode;
import com.hand.hcf.app.prepayment.web.adapter.CashPaymentRequisitionHeaderAdapter;
import com.hand.hcf.app.prepayment.web.adapter.CashPaymentRequisitionLineAdapter;
import com.hand.hcf.app.prepayment.web.dto.*;
import com.hand.hcf.app.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.workflow.dto.ApprovalResultCO;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import com.hand.hcf.core.domain.SystemCustomEnumerationType;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.security.domain.PrincipalLite;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.MessageService;
import com.hand.hcf.core.util.DateUtil;
import com.hand.hcf.core.util.PageUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.toList;

/**
 * Created by cbc on 2017/10/26.
 */
@Service
@Transactional
public class CashPaymentRequisitionHeadService extends BaseService<CashPaymentRequisitionHeadMapper, CashPaymentRequisitionHead> {
    private static final Logger log = LoggerFactory.getLogger(CashPaymentRequisitionHeadService.class);
    @Autowired
    private CashPaymentRequisitionLineService cashPaymentRequisitionLineService;
    @Autowired
    private CashPaymentRequisitionHeadMapper cashPaymentRequisitionHeadMapper;
    @Autowired
    private CashPayRequisitionTypeService cashSobPayReqTypeService;
    @Autowired
    private PrepaymentLogService prepaymentLogService;
    @Autowired
    private CashPayRequisitionTypeService cashPayRequisitionTypeService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MapperFacade mapper;
    @Autowired
    private PrepaymentHcfOrganizationInterface prepaymentHcfOrganizationInterface;
    @Autowired
    private ExpenseModuleInterface expenseModuleInterface;
    @Autowired
    private PaymentModuleInterface paymentModuleInterface;
    //@Autowired  合同
    //private ContractClient contractService;
    @Autowired
    private SupplierImplementControllerImpl supplierClient;

    @Value("${spring.application.name:}")
    private  String applicationName;

    @Autowired
    private WorkflowControllerImpl workflowClient;

    @Autowired
    private VendorModuleInterface vendorModuleInterface;

    @Autowired
    private CashPaymentRequisitionHeaderAdapter cashPaymentRequisitionHeaderAdapter;
    @Autowired
    private CashPaymentRequisitionLineAdapter cashPaymentRequisitionLineAdapter;
    /**
     * 预付款单提交
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public Boolean submit(WorkFlowDocumentRefCO workFlowDocumentRef) {
        CashPaymentRequisitionHead head = cashPaymentRequisitionHeadMapper.selectById(workFlowDocumentRef.getDocumentId());
        //校验状态
        if (head.getStatus() != DocumentOperationEnum.GENERATE.getId() && head.getStatus() != DocumentOperationEnum.WITHDRAW.getId() && head.getStatus() != DocumentOperationEnum.APPROVAL_REJECT.getId()) {
            //只有新增，拒绝，撤回的单据可以提交
            throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_SUBMIT_ERROR);
        }
        if(head.getAdvancePaymentAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BizException("","预付款单金额必须大于0");
        }
        List<CashPaymentRequisitionLine> lines =cashPaymentRequisitionLineService.getLinesByHeadID(workFlowDocumentRef.getDocumentId());
        //校验：预付款单行是空不能提交
        if(lines == null || lines.size() == 0){
            throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_HEAD_LINE_IS_NULL);
        }
        List<BigDecimal> amounts = lines.stream().map(CashPaymentRequisitionLine::getFunctionAmount).collect(Collectors.toList());
        BigDecimal lineAmount = BigDecimal.ZERO;
        for (BigDecimal a : amounts) {
            lineAmount = a.add(lineAmount);
        }
        //校验头行金额
        if (!head.getAdvancePaymentAmount().equals(lineAmount)) {
            throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_HEAD_LINE_AMOUNT_NOT_TRUE);
        }
         lines.stream().forEach(line -> {
             //校验收款方银行账户
             PartnerBankInfo payeeCompanyBank = new PartnerBankInfo();
             try {
                 if ("EMPLOYEE".equals(line.getPartnerCategory())) {
                     payeeCompanyBank = prepaymentHcfOrganizationInterface.getEmployeeCompanyBankByCode(line.getPartnerId() ,line.getAccountNumber());
                 }else{
                     payeeCompanyBank = vendorModuleInterface.getVenerCompanyBankByCode(line.getAccountNumber());
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
         });

        //校验及关联合同
        checkAndRelationContract(lines);
        CashPayRequisitionType prepaymentType = cashPayRequisitionTypeService.getCashPayRequisitionTypeById(workFlowDocumentRef.getDocumentTypeId());
        //校验关联申请
        checkRelationApplication(workFlowDocumentRef, head, lines, prepaymentType);

        //将formOID更新
        head.setFormOid(prepaymentType.getFormOid());
        if (StringUtils.isEmpty(prepaymentType.getFormOid())) {
            head.setIfWorkflow(false);
        } else {
            head.setIfWorkflow(true);
        }
        Long startw = System.currentTimeMillis();

        String documentOidStr = head.getDocumentOid();
        UUID documentOid = documentOidStr != null ? UUID.fromString(documentOidStr) : null;
        String unitOidStr = head.getUnitOid();
        UUID unitOid = unitOidStr != null ? UUID.fromString(unitOidStr) : null;
        String applicationOidStr = head.getApplicationOid();
        UUID applicationOid = applicationOidStr != null ? UUID.fromString(applicationOidStr) : null;
        String formOidStr = prepaymentType.getFormOid();
        UUID formOid = formOidStr != null ? UUID.fromString(formOidStr) : null;
        // 设置调用提交工作流方法的参数
        ApprovalDocumentCO submitData = new ApprovalDocumentCO();
        submitData.setDocumentId(head.getId()); // 单据id
        submitData.setDocumentOid(documentOid); // 单据oid
        submitData.setDocumentNumber(head.getRequisitionNumber()); // 单据编号
        submitData.setDocumentName(null); // 单据名称
        submitData.setDocumentCategory(head.getDocumentType()); // 单据类别
        submitData.setDocumentTypeId(head.getPaymentReqTypeId()); // 单据类型id
        submitData.setDocumentTypeCode(prepaymentType.getTypeCode()); // 单据类型代码
        submitData.setDocumentTypeName(prepaymentType.getTypeName()); // 单据类型名称
        submitData.setCurrencyCode(head.getCurrency()); // 币种
        submitData.setAmount(head.getAdvancePaymentAmount()); // 原币金额
        submitData.setFunctionAmount(head.getAdvancePaymentAmount()); // 本币金额
        submitData.setCompanyId(head.getCompanyId()); // 公司id
        submitData.setUnitOid(unitOid); // 部门oid
        submitData.setApplicantOid(applicationOid); // 申请人oid
        submitData.setApplicantDate(head.getCreatedDate()); // 申请日期
        submitData.setRemark(head.getDescription()); // 备注
        submitData.setSubmittedBy(OrgInformationUtil.getCurrentUserOid()); // 提交人
        submitData.setFormOid(formOid); // 表单oid
        submitData.setDestinationService(applicationName); // 注册到Eureka中的名称

        //调用工作流的三方接口进行提交
        ApprovalResultCO submitResult = workflowClient.submitWorkflow(submitData);

        if (Boolean.TRUE.equals(submitResult.getSuccess())){
            Integer approvalStatus = submitResult.getStatus();

            if (DocumentOperationEnum.APPROVAL.getId().equals(approvalStatus)) {
                head.setSubmitDate(ZonedDateTime.now());
                head.setRequisitionDate(ZonedDateTime.now());
                head.setStatus(DocumentOperationEnum.APPROVAL.getId());// 修改为审批中
                updateById(head);
            } else {
                updateDocumentStatus(approvalStatus, head.getId(), "", OrgInformationUtil.getCurrentUserId());
            }
        } else {
            throw new BizException(submitResult.getError());
        }

        log.info("预付款行整体提交,耗时:{}ms", System.currentTimeMillis() - startw);
        return true;
    }

    /**
     * 校验预付款是否申请单 20181218
     * @param workFlowDocumentRef
     * @param head
     * @param lines
     * @param prepaymentType
     */
    private void checkRelationApplication(WorkFlowDocumentRefCO workFlowDocumentRef, CashPaymentRequisitionHead head, List<CashPaymentRequisitionLine> lines, CashPayRequisitionType prepaymentType) {
        if (prepaymentType.getNeedApply()  != null && prepaymentType.getNeedApply().booleanValue()) {//需要申请，此时行必定都关联了申请
            //预付款行的金额
            Map<Long, List<CashPaymentRequisitionLine>> map = lines.stream().collect(
                    Collectors.groupingBy(CashPaymentRequisitionLine::getRefDocumentId)
            );
            Set<Long> applicationIds = map.keySet();
            for (Long applicationId : applicationIds) {
                List<CashPaymentRequisitionLine> cashLines = map.get(applicationId);
                //取过来的总金额
           //     Map<String, Double> stringDoubleMap = hcfOrganizationInterface.getApplicationAmountById(applicationId);
                //已关联金额
                Map<String, Double> arlMap = new HashMap<>();
                //已关联金额从申请单关联表里面取
             //   List<PrepaymentRequisitionRelease> releases = hcfOrganizationInterface.queryByRelated(applicationId);
//                arlMap = releases.stream().collect(
//                        Collectors.groupingBy(PrepaymentRequisitionRelease::getCurrencyCode, summingDouble(PrepaymentRequisitionRelease::AmountToDouble)));
                //预付款行的金额
                Map<String, Double> doubleMap = cashLines.stream().collect(
                        Collectors.groupingBy(CashPaymentRequisitionLine::getCurrency,summingDouble(CashPaymentRequisitionLine::AmountToDouble)));
                Set<String> result = new HashSet<>();
                Set<String> keySet = arlMap.keySet();
                Set<String> keySet1 = doubleMap.keySet();
                //取并集
                result.clear();
                result.addAll(keySet);
                result.addAll(keySet1);
                //全量币种的map
                for (String currency : result) {
                    doubleMap.put(currency, (doubleMap.get(currency) == null ? 0D : doubleMap.get(currency)) + (arlMap.get(currency) == null ? 0D : arlMap.get(currency)));
                }
                for (Map.Entry<String, Double> entry1 : doubleMap.entrySet()) {
                    //预付款行某个币种，总金额
                    Double m1value = entry1.getValue() == null ? 0D : entry1.getValue();
                    //申请单总金额
//                    Double m2value = stringDoubleMap.get(entry1.getKey()) == null ? 0D : stringDoubleMap.get(entry1.getKey());
//                    if (m2value < m1value) {
//                        throw new BizException(RespCode.PREPAY_PREPAYMENT_LINE_AMOUNT_TOO_BIG);
//                    }
                }
            }
            //关联申请
            createToApplication(lines, workFlowDocumentRef.getCreatedBy(), head);
        }
    }

    /**
     * 20181218
     * 检查合同状态，并关联合同
     * @param lines
     */
    private void checkAndRelationContract(List<CashPaymentRequisitionLine> lines) {
        if(lines != null){
            List<Long> contractIds = lines.stream().filter(m -> m.getContractId() != null).map(CashPaymentRequisitionLine::getContractId).distinct().collect(toList());
            if(contractIds != null &&  contractIds.size() > 0){
                //jiu.zhao 修改三方接口 20190328
                /*List<ContractHeaderCO> contractHeaderDTOList = contractService.listContractHeadersByIds(contractIds);
                if(!org.springframework.util.StringUtils.isEmpty(contractHeaderDTOList.get(0))) {
                    contractHeaderDTOList.stream().forEach(contract ->{
                        if ("6001".equals(contract.getStatus())) {
                            //HOLD(6001),暂挂中
                            throw new BizException(RespCode.PREPAY_PUBLIC_REPORT_SCHEDULE_STATUS, new Object[]{contractHeaderDTOList.get(0).getContractNumber(), RespCode.PREPAY_CONTRACT_STATUS_HOLD});
                        } else if ("6002".equals(contract.getStatus())) {
                            //CANCEL(6002),已取消
                            throw new BizException(RespCode.PREPAY_PUBLIC_REPORT_SCHEDULE_STATUS, new Object[]{contractHeaderDTOList.get(0).getContractNumber(), RespCode.PREPAY_CONTRACT_STATUS_CANCEL});
                        } else if ("6003".equals(contract.getStatus())) {
                            //FINISH(6003),已完成
                            throw new BizException(RespCode.PREPAY_PUBLIC_REPORT_SCHEDULE_STATUS, new Object[]{contractHeaderDTOList.get(0).getContractNumber(), RespCode.PREPAY_CONTRACT_STATUS_FINISH});
                        }
                    });
                }*/
                List<ContractDocumentRelationCO> list = new ArrayList<>();
                // 关联合同
                lines.stream().filter(m -> m.getContractId() != null).forEach(line ->{
                    ContractDocumentRelationCO relation = new ContractDocumentRelationCO();
                    relation.setAmount(line.getAmount());
                    relation.setContractHeadId(line.getContractId());
                    relation.setContractLineId(line.getContractLineId());
                    relation.setCreatedBy(line.getCreatedBy());
                    relation.setCreatedDate(line.getCreatedDate());
                    relation.setCurrencyCode(line.getCurrency());
                    relation.setDocumentType("PREPAYMENT_REQUISITION");
                    relation.setExchangeRate(line.getExchangeRate());
                    relation.setDocumentHeadId(line.getPaymentRequisitionHeaderId());
                    relation.setDocumentLineId(line.getId());
                    relation.setFunctionAmount(line.getFunctionAmount());
                    list.add(relation);
                });
                if (CollectionUtils.isNotEmpty(list)) {
                    ContractModuleInterface.contractDocumentRelationBatch(list, ContractOperationType.CREATE, true);
                }
            }
        }
    }
    /**
     * 条件查询预付款单头信息--前台使用
     *
     * @param requisitionNumber
     * @param paymentReqTypeId
     * @param employeeId
     * @param requisitionDateFrom
     * @param requisitionDateTo
     * @param submitDateFrom
     * @param submitDateTo
     * @param advancePaymentAmountFrom
     * @param advancePaymentAmountTo
     * @param status
     * @param ifWorkflow
     * @param checkBy
     * @param page
     * @return
     */
    public List<CashPaymentRequisitionHead> getCashPaymentRequisition(
            String requisitionNumber,
            Long paymentReqTypeId,
            Long employeeId,
            ZonedDateTime requisitionDateFrom,
            ZonedDateTime requisitionDateTo,
            ZonedDateTime submitDateFrom,
            ZonedDateTime submitDateTo,
            Double advancePaymentAmountFrom,
            Double advancePaymentAmountTo,
            String status,
            Boolean ifWorkflow,
            Long checkBy,
            String remark,
            Double noWritedAmountFrom,
            Double noWritedAmountTo,
            Page page) {

        if(noWritedAmountFrom != null && noWritedAmountTo != null){
            if(noWritedAmountFrom > noWritedAmountTo){
                return new ArrayList<>();
            }
        }
        //未核销从，至，等待三方接口
        //jiu.zhao 支付
        /*List<CashWriteOffDocumentAmountCO> documentAmountDTOList = paymentModuleInterface.getCashWriteOffDocumentAmountDTOByInput(noWritedAmountFrom, noWritedAmountTo, null);
//        if(CollectionUtils.isEmpty(documentAmountDTOList)){
//            return new ArrayList<>();
//        }
        Map<Long, CashWriteOffDocumentAmountCO> writeOffDocumentAmountDTOMap = documentAmountDTOList.stream().collect(Collectors.toMap(CashWriteOffDocumentAmountCO::getDocumentHeaderId, (p) -> p));
*/


        Wrapper<CashPaymentRequisitionHead> wrapper = new EntityWrapper<CashPaymentRequisitionHead>()
                .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                .like("requisition_number", requisitionNumber)
                .eq(paymentReqTypeId != null, "payment_req_type_id", paymentReqTypeId)
                .eq("created_by", OrgInformationUtil.getCurrentUserId())
                .eq(employeeId != null, "employee_id", employeeId)
                .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                .ge(submitDateFrom != null, "submit_date", submitDateFrom)
                .le(submitDateTo != null, "submit_date", submitDateTo)
               // .between(advancePaymentAmountFrom != null && advancePaymentAmountTo != null, "advance_payment_amount", advancePaymentAmountFrom, advancePaymentAmountTo)
                .ge(advancePaymentAmountFrom != null,"advance_payment_amount", advancePaymentAmountFrom)
                .le(advancePaymentAmountTo != null,"advance_payment_amount", advancePaymentAmountTo)
                .eq(status != null, "status", status)
                .eq(ifWorkflow != null, "if_workflow", ifWorkflow)
                .eq(checkBy != null, "check_by", checkBy)
                .like(StringUtils.isNotEmpty(remark), "description", remark);
        if (noWritedAmountFrom == null && noWritedAmountTo == null) {
            wrapper = wrapper
                    .orderBy("requisition_number", false);
        }else {
            // 获取不满足条件的单据信息
            String ids = "";
            //jiu.zhao 支付
            /*List<Long> excludeDocumentList = paymentModuleInterface.getExcludeDocumentCashWriteOffAmountDTOByInput(noWritedAmountFrom, noWritedAmountTo, null);
            if(CollectionUtils.isNotEmpty(excludeDocumentList)){
                ids = StringUtils.join(excludeDocumentList, ",");
            }*/
            wrapper = wrapper
                    .notExists(! "".equals(ids),"select 1 from dual where id in (" + ids + ")")
                    .ge(noWritedAmountFrom!=null,"advance_payment_amount",noWritedAmountFrom)
                    .le(noWritedAmountTo!=null,"advance_payment_amount",noWritedAmountTo)
                    .eq(status!=null,"status",status)
                    .orderBy("requisition_number", false)
            ;
        }
        Page pageResult = this.selectPage(page,wrapper);
        List<CashPaymentRequisitionHead> records = pageResult.getRecords();
        List<CashPaymentRequisitionHeaderCO> result = new ArrayList<>();


        //不重复的调接口：创建人
        Map<Long,String> createdMap = new HashMap<Long,String>();
        //不重复的调接口：预付款单类型
        Map<Long,String> typeMap = new HashMap<Long,String>();
        //不重复的调接口：员工
        Map<Long,String> empMap = new HashMap<Long,String>();



        for(CashPaymentRequisitionHead head:records){
            if(createdMap.get(head.getCreatedBy())!=null){
                head.setCreateByName(createdMap.get(head.getCreatedBy()));
            }else {
                ContactCO dto = prepaymentHcfOrganizationInterface.getUserById(head.getCreatedBy());
                String name = dto.getFullName() == null ? "" : dto.getFullName();
                createdMap.put(head.getCreatedBy(),name);
                head.setCreateByName(createdMap.get(head.getCreatedBy()));
            }


            if(typeMap.get(head.getPaymentReqTypeId())!=null){
                head.setTypeName(typeMap.get(head.getPaymentReqTypeId()));
            }else {
                typeMap.put(head.getPaymentReqTypeId(),cashSobPayReqTypeService.selectById(head.getPaymentReqTypeId()).getTypeName());
                head.setTypeName(typeMap.get(head.getPaymentReqTypeId()));
            }


            if(empMap.get(head.getEmployeeId())!=null){
                head.setEmployeeName(empMap.get(head.getEmployeeId()));
            }else {
                ContactCO co = prepaymentHcfOrganizationInterface.getUserById(head.getEmployeeId());
                String name = co.getFullName() == null ? "" : co.getFullName();
                empMap.put(head.getEmployeeId(),name);
                head.setEmployeeName(empMap.get(head.getEmployeeId()));
            }
            //jiu.zhao 支付
            /*CashWriteOffDocumentAmountCO documentAmountDTO = writeOffDocumentAmountDTOMap.get(head.getId());
            if(documentAmountDTO!=null){
                head.setNoWritedAmount(documentAmountDTO.getUnWriteOffAmount() == null ? null : documentAmountDTO.getUnWriteOffAmount());
                head.setWritedAmount(documentAmountDTO.getWriteOffAmount() == null ? null : documentAmountDTO.getWriteOffAmount());
            }else {
                head.setWritedAmount(BigDecimal.ZERO);
                head.setNoWritedAmount(head.getAdvancePaymentAmount());
            }*/

            CompanyCO company = prepaymentHcfOrganizationInterface.getCompanyById(head.getCompanyId());
            //获取本位币币种
            SetOfBooksInfoCO setOfBooksInfoCO = prepaymentHcfOrganizationInterface.getSetOfBookById(company.getSetOfBooksId());
            head.setCurrency(setOfBooksInfoCO.getFunctionalCurrencyCode());

        }






        return records;
    }


    public List<CashPaymentRequisitionHeaderCO> getCashPaymentRequisitionByInput(String requisitionNumber, Long paymentReqTypeId, String empInfo, ZonedDateTime requisitionDateFrom, ZonedDateTime requisitionDateTo, Double advancePaymentAmountFrom, Double advancePaymentAmountTo, String status, Page page) {


        //通过员工工号或姓名模糊查询userId
        List<Long> userIds = new ArrayList<>();
        List<ContactCO> list = prepaymentHcfOrganizationInterface.listByKeyWord(empInfo);
        if (CollectionUtils.isNotEmpty(list)) {
            userIds = list.stream().map(ContactCO::getId).collect(Collectors.toList());
        }
        Page pageResult = this.selectPage(
                page,
                new EntityWrapper<CashPaymentRequisitionHead>()
                        .like("requisition_number", requisitionNumber)
                        .eq(paymentReqTypeId != null, "payment_req_type_id", paymentReqTypeId)
                        .in(CollectionUtils.isNotEmpty(userIds), "employee_id", userIds)
                        .ge(requisitionDateFrom != null, "requisition_date", requisitionDateFrom)
                        .le(requisitionDateTo != null, "requisition_date", requisitionDateTo)
                        .ge(advancePaymentAmountFrom != null, "advance_payment_amount", advancePaymentAmountFrom)
                        .le(advancePaymentAmountTo != null, "advance_payment_amount", advancePaymentAmountTo)
                        .eq(status != null, "status", status)
                        .orderBy("requisition_number")
        );
        List<CashPaymentRequisitionHead> records = pageResult.getRecords();
        List<CashPaymentRequisitionHeaderCO> result = new ArrayList<>();
        for (CashPaymentRequisitionHead head : records) {
            result.add(cashPaymentRequisitionHeaderAdapter.toDTO(head));
        }
        return result;
    }

    /*    *
     * 保存预付款单头信息
     *
     * @param params*/

    @Transactional
    public CashPaymentRequisitionHeaderCO saveCashPaymentRequisition(CashPaymentRequisitionHeaderCO dto) {
        if (dto.getIfWorkflow()) {//如果走工作流。form_oid必输
            if (dto.getFormOid() == null) {
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_FORM_OID_NOT_EXIT);
            }

        }
        CashPaymentRequisitionHead head = CashPaymentRequisitionHeaderAdapter.toDomain(dto);
        if (head.getId() != null) {
            this.updateById(head);
            CashPaymentRequisitionHeaderCO cashPaymentRequisitionHeaderDTO = new CashPaymentRequisitionHeaderCO();
            BeanUtils.copyProperties(head, cashPaymentRequisitionHeaderDTO);
            return cashPaymentRequisitionHeaderDTO;
        }
        head.setDocumentOid(UUID.randomUUID().toString());
        if (dto.getFormOid() != null) {
            head.setFormOid(dto.getFormOid());
        }


        PrincipalLite userBean = OrgInformationUtil.getUser();
        head.setTenantId(userBean.getTenantId());
        head.setCompanyId(dto.getCompanyId());
        head.setRequisitionNumber(prepaymentHcfOrganizationInterface.getPrepaymentCode());
        head.setStatus(1001);//新建的是编辑状态
        head.setEmployeeId(dto.getEmployeeId()!=null?dto.getEmployeeId():Long.valueOf(userBean.getId()));
        ContactCO userCO = prepaymentHcfOrganizationInterface.getUserById(dto.getEmployeeId());
        head.setApplicationOid(userCO != null ? userCO.getUserOid() : userBean.getUserOid().toString());
        head.setEmpOid(head.getApplicationOid());
        head.setRequisitionDate(ZonedDateTime.now());
        //优化1
        DepartmentCO department = prepaymentHcfOrganizationInterface.getUnitsByUnitId(dto.getUnitId());
        head.setUnitOid(department.getDepartmentOid().toString());
        //新增的时候金额为0
        head.setAdvancePaymentAmount(BigDecimal.ZERO);
        this.insert(head);

        BeanUtils.copyProperties(head, dto);
        return dto;

    }

    @Transactional
    public List<CashPaymentRequisitionLineCO> insertOrUpdateLine(List<CashPaymentRequisitionLineCO> lineCOS) {
        List<CashPaymentRequisitionLineCO> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(lineCOS)) {
            return null;
        }
        lineCOS.forEach(lineCO -> {
            lineCO.setPartnerCode(lineCO.getPartnerId().toString());
            //判断付款方式是否是固定系统值列表
            if (!lineCO.getPaymentMethodCategory().equals("ONLINE_PAYMENT") && !lineCO.getPaymentMethodCategory().equals("OFFLINE_PAYMENT") && !lineCO.getPaymentMethodCategory().equals("EBANK_PAYMENT")) {
                throw new BizException(RespCode.PREPAY_PAYMENT_METHOD_ERROR);
            }

            CashPaymentRequisitionLine line = cashPaymentRequisitionLineAdapter.toDomain(lineCO);
            if (line.getCshTransactionClassId() != null) {
                //jiu.zhao 修改三方接口 20190328
                /*CashDefaultFlowItemCO itemDTO = new CashDefaultFlowItemCO();
                try {
                    itemDTO = paymentModuleInterface.selectTransactionClassAndFlowItemById(line.getCshTransactionClassId());

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BizException(RespCode.PREPAY_GET_FLOW_ITEM_ERROR);
                }
                if (itemDTO != null) {
                    line.setCashFlowId(itemDTO.getCashFlowItemId());
                    line.setCashFlowCode(itemDTO.getCashFlowItemCode());
                }*/
            }
            CashPaymentRequisitionHead head = cashPaymentRequisitionHeadMapper.selectById(line.getPaymentRequisitionHeaderId());
            //回写头金额
            if (head == null) {
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_HEAD_NOT_EXIT);
            }
            if (line.getId() == null) {
                line.setTenantId(OrgInformationUtil.getCurrentTenantId());
                line.setCompanyId(OrgInformationUtil.getCurrentCompanyId());
                cashPaymentRequisitionLineService.insert(line);
                head.setAdvancePaymentAmount(head.getAdvancePaymentAmount().add ((line.getFunctionAmount()) == null ? BigDecimal.ZERO : line.getFunctionAmount()));
            } else {
                BigDecimal now = line.getFunctionAmount() == null ? BigDecimal.ZERO : line.getFunctionAmount();
                BigDecimal before = cashPaymentRequisitionLineService.selectById(line).getFunctionAmount();
                if (org.springframework.util.StringUtils.isEmpty(before)) {
                    before = BigDecimal.ZERO;
                }
                line.setLastUpdatedDate(ZonedDateTime.now());
                line.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
                cashPaymentRequisitionLineService.updateAllColumnById(line);
                head.setAdvancePaymentAmount(head.getAdvancePaymentAmount().subtract(before).add(now));
            }

            cashPaymentRequisitionHeadMapper.updateById(head);
            list.add(cashPaymentRequisitionLineAdapter.toDTO(line));
        });
        return list;
    }

    /**
     * 根据头id查询头行信息
     *
     * @param headId
     * @param page
     * @return
     */
    public CashPaymentParamCO getCashPaymentRequisitionByHeadId(Long headId, Page page) {
        CashPaymentRequisitionHead head = this.selectById(headId);
        Page<CashPaymentRequisitionLine> linePage = cashPaymentRequisitionLineService.selectPage(
                page,
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq(headId != null, "payment_requisition_header_id", headId)
        );
        List<CashPaymentRequisitionLineCO> lineCOS = new ArrayList<>();
        linePage.getRecords().forEach(
                line -> {
                    lineCOS.add(cashPaymentRequisitionLineAdapter.toDTO(line));
                }
        );

        return CashPaymentParamCO
                .builder()
                .head(cashPaymentRequisitionHeaderAdapter.toDTO(head))
                .line(lineCOS)
                .build();
    }

    public void commit(CashPaymentParamCO param) {
        CashPaymentRequisitionHead head = CashPaymentRequisitionHeaderAdapter.toDomain(param.getHead());
    }

    public CashPaymentRequisitionHead saveCashPaymentRequisitionHead(CashPaymentRequisitionHead cashPaymentRequisitionHead) {
        this.insert(cashPaymentRequisitionHead);
        return cashPaymentRequisitionHead;
    }

    @Transactional
    public CashPaymentRequisitionHead updateCashPaymentRequisitionHead(CashPaymentRequisitionHead cashPaymentRequisitionHead) {
        if (cashPaymentRequisitionHead.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        this.updateById(cashPaymentRequisitionHead);
        return cashPaymentRequisitionHead;
    }


    /*根据预付款单头id查询各个币种金额之和*/
    public Map<String, BigDecimal> getAmountGroupByCodeByHeadId(Long headId) {
        Map<String, BigDecimal> map = new HashMap<>();
        cashPaymentRequisitionLineService.selectList(
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq("payment_requisition_header_id", headId)
        ).forEach(
                line -> {
                    if (map.get(line.getCurrency()) == null) {
                        map.put(line.getCurrency(), line.getAmount());
                    } else {
                        map.put(line.getCurrency(), line.getAmount().add(map.get(line.getCurrency())));
                    }
                }
        );
        map.put("totalFunctionAmount", getFunctionAmountByHeadId(headId));
        return map;
    }

    /*根据预付款单头id查询本位币金额之和*/
    public BigDecimal getFunctionAmountByHeadId(Long headId) {
        BigDecimal functionAmount = BigDecimal.ZERO;
        List<BigDecimal> functionAmounts = cashPaymentRequisitionLineService.selectList(
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq("payment_requisition_header_id", headId)
        ).stream().map(CashPaymentRequisitionLine::getFunctionAmount).collect(Collectors.toList());
        for (BigDecimal d : functionAmounts) {
            functionAmount = functionAmount.add(d);
        }
        return functionAmount;
    }


    /*根据预付款单头id删除预付款单头行*/
    @Transactional
    public Boolean deleteHeadAndLineByHeadId(Long headId) {
        Boolean flagHead = this.deleteById(headId);
        List<CashPaymentRequisitionLine> lines = cashPaymentRequisitionLineService.selectList(
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq("payment_requisition_header_id", headId)
        );
        Boolean flagLine = true;
        if (CollectionUtils.isNotEmpty(lines)) {
            flagLine = cashPaymentRequisitionLineService.deleteBatchIds(
                    lines.stream().map(CashPaymentRequisitionLine::getPaymentRequisitionHeaderId).collect(Collectors.toList())
            );
        }

        return flagHead && flagLine;
    }

    /*单独删除预付款单行信息*/
    @Transactional
    public Boolean deleteLine(Long lineId) {
        //删除行时，回写头上的金额
        CashPaymentRequisitionLine line = cashPaymentRequisitionLineService.selectById(lineId);
        CashPaymentRequisitionHead head = cashPaymentRequisitionHeadMapper.selectById(line.getPaymentRequisitionHeaderId());
        head.setAdvancePaymentAmount(head.getAdvancePaymentAmount().subtract(line.getFunctionAmount()));
        cashPaymentRequisitionHeadMapper.updateById(head);
        return cashPaymentRequisitionLineService.deleteById(lineId);
    }


    /**
     * 条件查询收款方
     * @param name
     * @param empFlag
     * @param pageFlag
     *   @param page
     *  @return
     */
    public Page<ReceivablesDTO> getReceivablesByName(String name, Integer empFlag, Boolean pageFlag, Page page) {
        Page<ReceivablesDTO> dtoPage = new Page<>();
        if(!pageFlag){
            page.setCurrent(0);
            page.setSize(9999);
        }else {
            page.setCurrent(page.getCurrent());
        }
        if (empFlag .equals( 1001 )) {
            try {
                //dtoPage = hcfOrganizationInterface.getContactBankAccountDTO(OrgInformationUtil.getCurrentTenantId(), name, page.getCurrent(), page.getSize());
                List<ReceivablesDTO> receivablesDTOS = new ArrayList<>();
                //jiu.zhao 修改三方接口 20190328
                /*List<ContactCO> contactCOS = hcfOrganizationInterface.pageConditionNameAndIgnoreIds(null,name,null,null,page).getRecords();
                if(contactCOS != null){
                    contactCOS.stream().forEach(e->{
                        ReceivablesDTO receivablesDTO = new ReceivablesDTO();
                        receivablesDTO.setId(e.getId());
                        receivablesDTO.setIsEmp(false);
                        receivablesDTO.setCode(e.getEmployeeCode());//员工代码
                        receivablesDTO.setName(e.getFullName()); //员工名
                        receivablesDTO.setSign(receivablesDTO.getId()+"_"+receivablesDTO.getIsEmp());//唯一标识
                        receivablesDTOS.add(receivablesDTO);
                    });
                }*/
                dtoPage.setRecords(receivablesDTOS);
                //bo.liu 修改三方接口 201900404
               /*page.setTotal(contactCOS.getTotal());*/
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException(RespCode.PREPAY_USER_BANK_ERROR);
            }
        }else{
           Page<VendorInfoCO> pageVendorInfoDTOs = vendorModuleInterface.getBankInfoByCompanyId(OrgInformationUtil.getCurrentCompanyId(), name,page);
            List<ReceivablesDTO> receivablesDTOS = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(pageVendorInfoDTOs.getRecords())) {
                dtoPage.setTotal(pageVendorInfoDTOs.getTotal());
                pageVendorInfoDTOs.getRecords().stream().forEach(e -> {
                    ReceivablesDTO receivablesDTO = new ReceivablesDTO();
                    receivablesDTO.setId(e.getId());//供应商id
                    receivablesDTO.setIsEmp(false);
                    receivablesDTO.setName(e.getVenNickname());//供应商名称
                    receivablesDTO.setCode(e.getVenderCode());//供应商code
                    receivablesDTO.setSign(receivablesDTO.getId()+"_"+receivablesDTO.getIsEmp());//唯一标识
                    List<VendorBankAccountCO> venBankAccountBeans = e.getVenBankAccountBeans();
                    List<BankInfo> bankInfos = new ArrayList<>();
                    if(CollectionUtils.isNotEmpty(venBankAccountBeans)){
                        venBankAccountBeans.forEach(
                                venBankAccountBean->{
                                    BankInfo bankInfo = new BankInfo();
                                    bankInfo.setBankName(venBankAccountBean.getBankName());//银行名称
                                    bankInfo.setNumber(venBankAccountBean.getBankAccount());//银行账号
                                    bankInfo.setBankCode(venBankAccountBean.getBankCode());//开户行代码
                                    bankInfo.setPrimary(venBankAccountBean.getPrimaryFlag());
                                    bankInfo.setBankNumberName(venBankAccountBean.getVenBankNumberName());//银行账户名
                                    bankInfos.add(bankInfo);
                                }
                        );
                    }
                    receivablesDTO.setBankInfos(bankInfos);
                    receivablesDTOS.add(receivablesDTO);

                });
                dtoPage.setRecords(receivablesDTOS);
                page.setTotal(pageVendorInfoDTOs.getTotal());
            }
        }

        return dtoPage;
    }

    /**
     * 条件查询收款方 增加代码查询条件
     */
    public Page<ReceivablesDTO> getReceivablesByNameAndCode(String name, String code, Integer empFlag, Boolean pageFlag, Page page) {
        Page<ReceivablesDTO> dtoPage = new Page<>();
        if(!pageFlag){
            page.setCurrent(0);
            page.setSize(9999);
        }else {
            page.setCurrent(page.getCurrent()-1);
        }
        if (empFlag .equals( 1001 )) {
            try {

               // dtoPage = hcfOrganizationInterface.getContactBankAccountDTO(OrgInformationUtil.getCurrentTenantId(), name,code, page.getCurrent(), page.getSize());
                List<ReceivablesDTO> receivablesDTOS = new ArrayList<>();
                Page<ContactCO> contactCOS = prepaymentHcfOrganizationInterface.pageConditionNameAndIgnoreIds(code,name,null,null,page);
                if(CollectionUtils.isNotEmpty(contactCOS.getRecords())){
                    dtoPage.setTotal(contactCOS.getTotal());
                    contactCOS.getRecords().stream().forEach(e->{
                        ReceivablesDTO receivablesDTO = new ReceivablesDTO();
                        receivablesDTO.setId(e.getId());
                        receivablesDTO.setIsEmp(false);
                        receivablesDTO.setCode(e.getEmployeeCode());//员工代码
                        receivablesDTO.setName(e.getFullName()); //员工名
                        receivablesDTO.setSign(receivablesDTO.getId()+"_"+receivablesDTO.getIsEmp());//唯一标识
                        receivablesDTOS.add(receivablesDTO);
                    });
                }
                dtoPage.setRecords(receivablesDTOS);
                page.setTotal(contactCOS.getTotal());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException(RespCode.PREPAY_USER_BANK_ERROR);
            }
        }else{
            Page<VendorInfoCO> pageVendorInfoDTOs =vendorModuleInterface.getBankInfoByCompanyAndVendorInfo(OrgInformationUtil.getCurrentCompanyId(), name,code,page);
            List<ReceivablesDTO> receivablesDTOS = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(pageVendorInfoDTOs.getRecords())) {
                dtoPage.setTotal(pageVendorInfoDTOs.getTotal());
                pageVendorInfoDTOs.getRecords().stream().forEach(e -> {
                    ReceivablesDTO receivablesDTO = new ReceivablesDTO();
                    receivablesDTO.setId(e.getId());//供应商id
                    receivablesDTO.setIsEmp(false);
                    receivablesDTO.setName(e.getVenNickname());//供应商名称
                    receivablesDTO.setCode(e.getVenderCode());//供应商code
                    receivablesDTO.setSign(receivablesDTO.getId()+"_"+receivablesDTO.getIsEmp());//唯一标识
                    List<VendorBankAccountCO> venBankAccountBeans = e.getVenBankAccountBeans();
                    List<BankInfo> bankInfos = new ArrayList<>();
                    if(CollectionUtils.isNotEmpty(venBankAccountBeans)){
                        venBankAccountBeans.forEach(
                                venBankAccountBean->{
                                    BankInfo bankInfo = new BankInfo();
                                    bankInfo.setBankName(venBankAccountBean.getBankName());//银行名称
                                    bankInfo.setNumber(venBankAccountBean.getBankAccount());//银行账号
                                    bankInfo.setBankCode(venBankAccountBean.getBankCode());//开户行代码
                                    bankInfo.setPrimary(venBankAccountBean.getPrimaryFlag());
                                    bankInfo.setBankNumberName(venBankAccountBean.getVenBankNumberName());//银行账户名
                                    bankInfos.add(bankInfo);
                                }
                        );
                    }
                    receivablesDTO.setBankInfos(bankInfos);
                    receivablesDTOS.add(receivablesDTO);

                });
                dtoPage.setRecords(receivablesDTOS);
                page.setTotal(pageVendorInfoDTOs.getTotal());
            }
        }

        return dtoPage;
    }


    /*根据预付款单据oid查询单据信息---为工作流提供*/
    public CashPaymentRequisitionHeaderCO selectByOid(String oid) {
        List<CashPaymentRequisitionHead> list = cashPaymentRequisitionHeadMapper.selectList(
                new EntityWrapper<CashPaymentRequisitionHead>()
                        .eq("document_oid", oid)
        );
        CashPaymentRequisitionHead head = CollectionUtils.isEmpty(list) ? null : list.get(0);
        if (head == null) {
            return null;
        }
        CashPaymentRequisitionHeaderCO cashPaymentRequisitionHeaderDTO = new CashPaymentRequisitionHeaderCO();
        BeanUtils.copyProperties(head, cashPaymentRequisitionHeaderDTO);
        CashPaymentRequisitionLine line = cashPaymentRequisitionLineService.selectOne(new EntityWrapper<CashPaymentRequisitionLine>().eq("payment_requisition_header_id",head.getId()));
        cashPaymentRequisitionHeaderDTO.setCurrency(line.getCurrency());
        CashPayRequisitionType type = cashPayRequisitionTypeService.selectById(head.getPaymentReqTypeId());
        cashPaymentRequisitionHeaderDTO.setTypeName(type.getTypeName());
        cashPaymentRequisitionHeaderDTO.setPaymentMethod(type.getPaymentMethodCategoryName());
        cashPaymentRequisitionHeaderDTO.setIfApplication(type.getNeedApply());
        cashPaymentRequisitionHeaderDTO.setFormOid(type.getFormOid());
        return cashPaymentRequisitionHeaderDTO;
    }

    /*根据单据oid更改单据状态*/
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void updateDocumentStatusByOid(String oid, int status, Long userId, Boolean isWorkflow) {
        /*CashPaymentRequisitionHeaderCO dto = selectByOid(oid);
        if (dto == null) {
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("approvalRemark", "");
        ExceptionDetail exceptionDetail = new ExceptionDetail();
        exceptionDetail.setErrorCode("0000");
        try {
            exceptionDetail = updateStatus(status, dto.getId(), map, userId, isWorkflow);
        } catch (BizException e) {
            e.printStackTrace();
            String errorMsg = messageService.getMessageDetailByCode(e.getCode(), e.getArgs());
            exceptionDetail.setMessage(StringUtils.isEmpty(errorMsg) ? e.getMsg() : errorMsg);
            exceptionDetail.setErrorCode(e.getCode());
        } catch (Exception e) {
            e.printStackTrace();
            exceptionDetail.setErrorCode("-1");
            exceptionDetail.setMessage("SYSTEM ERROR!");
        }
        return exceptionDetail;*/

        CashPaymentRequisitionHeaderCO dto = selectByOid(oid);
        if (dto != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("approvalRemark", "");
            updateStatus(status, dto.getId(), map, userId, isWorkflow);
        }

        return ;
    }

    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    /*修改预付款单据状态, 工作流监听事件消息*/
    public void updateDocumentStatus(int status, Long headId, String approvalRemark, Long userId) {
        CashPaymentRequisitionHead head = cashPaymentRequisitionHeadMapper.selectById(headId);
        CashPayRequisitionType requisitionType = cashSobPayReqTypeService.selectById(head.getPaymentReqTypeId());
//        ExceptionDetail detail = new ExceptionDetail();
//        detail.setErrorCode("0000");
        if (head == null || requisitionType == null) {
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        if (status == DocumentOperationEnum.APPROVAL_PASS.getId()) {
            //只有提交状态的单据才可以审批通过和拒绝
            //if (head.getStatus() != DocumentOperationEnum.APPROVAL.getId()) {
            // modify by mh.z 20190108 解决机器人（只有一个节点）审批通过但单据状态没变，
            // BUG产生的原因是prepayment发请求给artemis提交单据，由于只有机器人一个节点且审批通过，
            // 这时候artemis发单据审批通过的广播。因为这时候单据状态是编辑中（artemis提交单据成功后
            // prepayment才会更新单据状态成审批中），所以prepayment收到广播后校验单据状态不通过报错没修改单据状态。
            // modify by mh.z 20190111 解决单据驳回状态下提交后机器人（只有一个节点）审批通过但单据状态没变
            // TODO 目前还没有想到其它更好的方式修改上面注释描述的BUG
            if (head.getStatus() != DocumentOperationEnum.GENERATE.getId()
                    && head.getStatus() != DocumentOperationEnum.APPROVAL_REJECT.getId()
                    && head.getStatus() != DocumentOperationEnum.APPROVAL.getId()) {
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_STATUS_NOT_ALLOW);
            }
            if (status == DocumentOperationEnum.APPROVAL_PASS.getId()) {
                if (StringUtils.isEmpty(requisitionType.getFormOid())) {//不走工作流的审批通过
                    head.setCheckBy(OrgInformationUtil.getCurrentUserId());
                }
                head.setApprovedBy(userId);
                head.setApprovalDate(ZonedDateTime.now());
            }
            //审批通过，则推送支付平台
            pushToPayment(head);
        } else if (status == DocumentOperationEnum.WITHDRAW.getId() || status == DocumentOperationEnum.APPROVAL_REJECT.getId()) {
            head.setFormOid("");
            //驳回，撤回时，取消关联合同
            toContract(head, ContractOperationType.LOGICAL_DELETE, true);
            //驳回，撤回时，释放与申请单的关联
            if (requisitionType.getNeedApply() != null || requisitionType.getNeedApply().booleanValue()) {
//                hcfOrganizationInterface.releasePrepaymentRequisitionRelease(head.getId());
                expenseModuleInterface.releasePrepaymentRequisitionRelease(head.getId());
            }
        }
        head.setApprovalRemark(approvalRemark);
        head.setStatus(status);
        cashPaymentRequisitionHeadMapper.updateById(head);
    }

    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    /*修改预付款单据状态*/
    public void updateStatus(int status, Long id, Map<String, String> approvalRemark, Long userId, Boolean isWorkflow) {
        CashPaymentRequisitionHead head = cashPaymentRequisitionHeadMapper.selectById(id);
        CashPayRequisitionType requisitionType = cashSobPayReqTypeService.selectById(head.getPaymentReqTypeId());
//        ExceptionDetail detail = new ExceptionDetail();
//        detail.setErrorCode("0000");
        CommonApprovalHistoryCO commonApprovalHistoryCO = new CommonApprovalHistoryCO();

        if (head == null) {
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        commonApprovalHistoryCO.setEntityOid(UUID.fromString(head.getDocumentOid()));
        commonApprovalHistoryCO.setEntityType(head.getDocumentType());
        commonApprovalHistoryCO.setOperatorOid(OrgInformationUtil.getCurrentUserOid());
        commonApprovalHistoryCO.setOperation(status);
        if (status == DocumentOperationEnum.APPROVAL.getId()) {

            head.setSubmitDate(ZonedDateTime.now());
            head.setRequisitionDate(ZonedDateTime.now());
            //提交时校验头行金额
            BigDecimal lineAmount = BigDecimal.ZERO;
            List<CashPaymentRequisitionLine> lines = cashPaymentRequisitionLineService.selectList(
                    new EntityWrapper<CashPaymentRequisitionLine>()
                            .eq("payment_requisition_header_id", head.getId())
            );
            List<BigDecimal> amounts = lines.stream().map(CashPaymentRequisitionLine::getFunctionAmount).collect(Collectors.toList());
            for (BigDecimal a : amounts) {
                lineAmount = a.add(lineAmount);
            }
            if (!head.getAdvancePaymentAmount().equals(lineAmount)) {
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_HEAD_LINE_AMOUNT_NOT_TRUE);
            }
            if (head.getStatus() != DocumentOperationEnum.GENERATE.getId() && head.getStatus() != DocumentOperationEnum.WITHDRAW.getId() && head.getStatus() != DocumentOperationEnum.APPROVAL_REJECT.getId()) {
                //只有新增，拒绝，撤回的单据可以提交
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_SUBMIT_ERROR);
            }
            //提交的单据必须有行信息
            if (CollectionUtils.isEmpty(lines)) {
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_HEAD_LINE_IS_NULL);
            }
//            historyDTO.setOperation(DocumentOperationEnum.APPROVAL.getId());
            //将formOID更新
            head.setFormOid(requisitionType.getFormOid());

            //提交时，关联合同
            Boolean aBoolean = toContract(head, ContractOperationType.CREATE, isWorkflow);
//            if (!"0000".equals(exceptionDetail.getErrorCode()) && isWorkflow) {
//                return exceptionDetail;
//            }
        //调用申请单有关的三方接口都还没有
            if (requisitionType.getNeedApply()) {//需要申请，此时行必定都关联了申请
                //预付款行的金额
                Map<Long, List<CashPaymentRequisitionLine>> map = lines.stream().collect(
                        Collectors.groupingBy(CashPaymentRequisitionLine::getRefDocumentId)
                );

                Set<Long> applicationIds = map.keySet();

                for (Long applicationId : applicationIds) {
                    //获取一个申请单对应的所有预付款单
                    List<CashPaymentRequisitionLine> cashLines = map.get(applicationId);

                    //取过来的总金额
//                    Map<String, Double> stringDoubleMap = HcfOrganizationInterface.getApplicationAmountById(applicationId);
                    List<ApplicationAmountCO> applicationAmounts = expenseModuleInterface.getApplicationAmountById(applicationId);
                    Map<String, Double> stringDoubleMap = applicationAmounts.stream().collect(
                            Collectors.groupingBy(ApplicationAmountCO::getCurrencyCode, summingDouble(ApplicationAmountCO::AmountToDouble)));
                    //已关联金额qw
//                    Map<String, Double> arlMap = new HashMap<>();
                    Map<String, Double> arlMap = applicationAmounts.stream().collect(
                            Collectors.groupingBy(ApplicationAmountCO::getCurrencyCode, summingDouble(ApplicationAmountCO::RelatedAmountToDouble)));

                    //已关联金额从申请单关联表里面取
//                    List<PrepaymentRequisitionRelease> releases = HcfOrganizationInterface.queryByRelated(applicationId);
//                    arlMap = releases.stream().collect(
//                            Collectors.groupingBy(PrepaymentRequisitionReleaseCO::getCurrencyCode, summingDouble(PrepaymentRequisitionReleaseCO::AmountToDouble)));
                    //预付款行的金额
                    Map<String, Double> doubleMap = cashLines.stream().collect(
                            Collectors.groupingBy(CashPaymentRequisitionLine::getCurrency,summingDouble(CashPaymentRequisitionLine::AmountToDouble)));
                    Set<String> result = new HashSet<>();
                    Set<String> keySet = arlMap.keySet();
                    Set<String> keySet1 = doubleMap.keySet();
                    //取并集
                    result.clear();
                    result.addAll(keySet);
                    result.addAll(keySet1);
                    //全量币种的map
                    for (String currency : result) {
                        doubleMap.put(currency, (doubleMap.get(currency) == null ? 0D : doubleMap.get(currency)) + (arlMap.get(currency) == null ? 0D : arlMap.get(currency)));
                    }
                    for (Map.Entry<String, Double> entry1 : doubleMap.entrySet()) {
                        //预付款行某个币种，总金额
                        Double m1value = entry1.getValue() == null ? 0D : entry1.getValue();
                        //申请单总金额
                        Double m2value = stringDoubleMap.get(entry1.getKey()) == null ? 0D : stringDoubleMap.get(entry1.getKey());
                        if (m2value < m1value) {
                            throw new BizException(RespCode.PREPAY_PREPAYMENT_LINE_AMOUNT_TOO_BIG);
                        }
                    }

                }
                createToApplication(lines, userId, head);
            }


        } else if (status == DocumentOperationEnum.APPROVAL_PASS.getId() || status == DocumentOperationEnum.APPROVAL_REJECT.getId()) {
            //只有提交状态的单据才可以审批通过和拒绝
            if (head.getStatus() != DocumentOperationEnum.APPROVAL.getId()) {
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_STATUS_NOT_ALLOW);
            }
            commonApprovalHistoryCO.setOperationDetail(approvalRemark.get("approvalRemark") == null ? "" : approvalRemark.get("approvalRemark"));//提交的时候，可以传审批备注
            head.setApprovalRemark(approvalRemark.get("approvalRemark"));
            if (status == DocumentOperationEnum.APPROVAL_PASS.getId()) {
                if (StringUtils.isEmpty(requisitionType.getFormOid())) {//不走工作流的审批通过
                    head.setCheckBy(OrgInformationUtil.getCurrentUserId());
                }
                head.setApprovedBy(userId);
                head.setApprovalDate(ZonedDateTime.now());
            } else {
                head.setFormOid("");
                //拒绝时，取消关联合同
                Boolean aBoolean = toContract(head, ContractOperationType.LOGICAL_DELETE, isWorkflow);
//                if (!"0000".equals(exceptionDetail.getErrorCode()) && isWorkflow) {
//                    return exceptionDetail;
//                }
                //拒绝时，释放与申请单的关联
                if (requisitionType.getNeedApply()) {
                    try {
//                        hcfOrganizationInterface.releasePrepaymentRequisitionRelease(head.getId());
                        expenseModuleInterface.releasePrepaymentRequisitionRelease(head.getId());
                    } catch (Exception e) {
                        throw new BizException(RespCode.PREPAY_RE_APPLICATION_ERROR);
                    }
                }
            }
        } else if (status == DocumentOperationEnum.WITHDRAW.getId()) {
            //只有提交状态的单据才可以撤回
            if (head.getStatus() != DocumentOperationEnum.APPROVAL.getId()) {
                throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_RETURN_ERROR);
            }
            head.setFormOid("");
            //撤回时，取消关联合同
            Boolean aBoolean = toContract(head, ContractOperationType.LOGICAL_DELETE, isWorkflow);
//            if (!"0000".equals(exceptionDetail.getErrorCode()) && isWorkflow) {
//                return exceptionDetail;
//            }
            //撤回时，释放与申请单的关联
            if (requisitionType.getNeedApply()) {
                try {
//                    hcfOrganizationInterface.releasePrepaymentRequisitionRelease(head.getId());
                    expenseModuleInterface.releasePrepaymentRequisitionRelease(head.getId());
                } catch (Exception e) {
                    throw new BizException(RespCode.PREPAY_RE_APPLICATION_ERROR);
                }
            }
        }
        head.setStatus(status);
        head.setId(id);
        if (StringUtils.isEmpty(requisitionType.getFormOid())) {
            head.setIfWorkflow(false);
        } else {
            head.setIfWorkflow(true);
        }
        if (isWorkflow && DocumentOperationEnum.GENERATE.getId().equals(status)) {
            //错误异常回滚时，取消关联合同
            Boolean aBoolean = toContract(head, ContractOperationType.ROLLBACK, isWorkflow);
//            if (!"0000".equals(exceptionDetail.getErrorCode())) {
//                return exceptionDetail;
//            }
        }
        if (DocumentOperationEnum.APPROVAL_PASS.getId().equals(head.getStatus())) {
            //审批通过，则推送支付平台
            pushToPayment(head);
        }
        //调用工作流模块，保存审批历史
        prepaymentHcfOrganizationInterface.saveHistory(commonApprovalHistoryCO);
        cashPaymentRequisitionHeadMapper.updateById(head);
//        if (i != 0) {
//            detail.setMessage("Success");
//        } else {
//            detail.setErrorCode("-2");
//            detail.setMessage("update prepayment error");
//        }
        return ;
    }


    /**
     * 提交调用申请单id
     *
     * @param lines
     * @param userId
     * @param dataHead
     */
    @LcnTransaction
//    @Transactional(rollbackFor = Exception.class)
    private void createToApplication(List<CashPaymentRequisitionLine> lines, Long userId, CashPaymentRequisitionHead dataHead) {
        List<PrepaymentRequisitionReleaseCO> requisitionReleases = new ArrayList<>();
        getListRelease(lines, userId, dataHead, requisitionReleases);
//        hcfOrganizationInterface.createPrepaymentRequisitionRelease(requisitionReleases);
        expenseModuleInterface.createPrepaymentRequisitionRelease(requisitionReleases);
    }


    /**
     * 创建关联集合
     *
     * @param lines
     * @param userId
     * @param dataHead
     * @param requisitionReleases
     * @return
     */
    public List<PrepaymentRequisitionReleaseCO> getListRelease(List<CashPaymentRequisitionLine> lines, Long userId, CashPaymentRequisitionHead dataHead, List<PrepaymentRequisitionReleaseCO> requisitionReleases) {
        Long setOfBooksId = OrgInformationUtil.getCurrentSetOfBookId();
        for (CashPaymentRequisitionLine line : lines) {
            PrepaymentRequisitionReleaseCO release = PrepaymentRequisitionReleaseCO.builder()
                    .amount(line.getAmount())
                    .createdBy(userId)
                    .createdDate(ZonedDateTime.now())
                    .currencyCode(line.getCurrency())
                    .exchangeRate(line.getExchangeRate())
                    .functionalAmount(line.getFunctionAmount())
                    .relatedDocumentCategory("CSH_PREPAYMENT")
                    .relatedDocumentId(line.getPaymentRequisitionHeaderId())
                    .relatedDocumentLineId(line.getId())
                    .setOfBooksId(setOfBooksId)
                    .sourceDocumentCategory("EXP_REQUISITION")
                    .sourceDocumentId(line.getRefDocumentId())
                    .status("Y")
                    .tenantId(dataHead.getTenantId())
                    .build();
            requisitionReleases.add(release);
        }
        return requisitionReleases;
    }


    /*根据头Id查询行信息*/
    public List<CashPaymentRequisitionLineCO> selectLineByHeadId(Long headId, Page page) {
        List<CashPaymentRequisitionLineCO> list = new ArrayList<>();

        Map<Long, String> empMap = new HashMap<>(16);
        Map<Long, String> venMap = new HashMap<>(16);

        Page<CashPaymentRequisitionLine> lines = cashPaymentRequisitionLineService.selectPage(page, new EntityWrapper<CashPaymentRequisitionLine>()
                .eq("payment_requisition_header_id", headId)
                .orderBy("created_date")
        );

        if (CollectionUtils.isNotEmpty(lines.getRecords())) {
            //jiu.zhao 支付
            //Map<Long, List<PublicReportWriteOffCO>> map = paymentModuleInterface.getPrepaymentLineWriteInfo(headId, lines.getRecords().stream().map(CashPaymentRequisitionLine::getId).collect(Collectors.toList()));
            lines.getRecords().forEach(
                    line -> {
                        CashPaymentRequisitionLineCO lineCO = new CashPaymentRequisitionLineCO();
                        lineCO = cashPaymentRequisitionLineAdapter.toDTO(line);
                        if (line.getPartnerCategory().equals("EMPLOYEE")) {
                            if (!empMap.containsKey(line.getPartnerId())) {

                                ContactCO userCO = prepaymentHcfOrganizationInterface.getUserById(line.getPartnerId());
                                empMap.put(line.getPartnerId(),userCO.getFullName());
                                lineCO.setPartnerName(empMap.get(line.getPartnerId()));
                            }
                        } else {

                            if (!venMap.containsKey(line.getPartnerId())) {

                                VendorInfoCO vendorInfoByArtemis = supplierClient.getVendorInfoByArtemis(line.getPartnerId().toString());
                                if (vendorInfoByArtemis != null) {
                                    venMap.put(line.getPartnerId(), vendorInfoByArtemis.getVenNickname());
                                    lineCO.setPartnerName(venMap.get(line.getPartnerId()));
                                }
                            }
                        }

                        // bo.liu 支付
//                        List<PublicReportWriteOffCO> reportWriteOffCOS = map.get(line.getId() + "");
//                        lineCO.setReportWriteOffDTOS(reportWriteOffCOS);//获取核销信息
//                        List<PaymentDocumentAmountCO> payReturnAmountByLines = paymentModuleInterface.getPayReturnAmountByLines(Arrays.asList(line.getId()));
//                        if (CollectionUtils.isEmpty(payReturnAmountByLines)) {
//                            lineCO.setPayAmount(BigDecimal.ZERO);
//                            lineCO.setPayAmount(BigDecimal.ZERO);
//                        } else {
//                            PaymentDocumentAmountCO paymentDocumentAmountCO = payReturnAmountByLines.get(0);
//                            lineCO.setPayAmount(paymentDocumentAmountCO.getPayAmount() != null ?
//                                    paymentDocumentAmountCO.getPayAmount() : BigDecimal.ZERO);
//                            lineCO.setReturnAmount(paymentDocumentAmountCO.getReturnAmount() != null ?
//                                    paymentDocumentAmountCO.getReturnAmount() : BigDecimal.ZERO);
//                        }


                        list.add(lineCO);
                    }
            );


        }
        return list;
    }


    /*根据单据oid批量获取单据信息*/
    public List<CashPaymentRequisitionHeaderCO> selectHeadersByDocumentOids(List<UUID> documentOids) {
        List<CashPaymentRequisitionHeaderCO> list = new ArrayList<>();
        List<CashPaymentRequisitionHead> heads = cashPaymentRequisitionHeadMapper.selectList(
                new EntityWrapper<CashPaymentRequisitionHead>()
                        .in("document_oid", documentOids)
        );
        if (CollectionUtils.isNotEmpty(heads)) {
            heads.forEach(head -> {
                list.add(cashPaymentRequisitionHeaderAdapter.toDTO(head));
            });
        }
        return list;
    }

    /**
     * @author mh.z
     * @date 2019/02/19
     * @description 获取未审批/已审批的预付款
     *
     * @param beginDate 提交日期从
     * @param endDate 提交日期至
     * @param finished true已审批，false未审批
     * @param fullName 名称
     * @param businessCode 编号
     * @param typeId 单据类型
     * @param amountFrom 本币金额从
     * @param amountTo 本币金额至
     * @param description 备注
     * @param userOid 申请人
     * @param pageable
     * @return
     */
    public Page<CashPaymentRequisitionHeaderCO> listApprovalPrepayment(String beginDate, String endDate, boolean finished, String fullName, String businessCode,
                                                                       Long typeId, Double amountFrom, Double amountTo, String description, String userOid, Pageable pageable) throws ParseException {
        Page<CashPaymentRequisitionHeaderCO> page  = new Page<CashPaymentRequisitionHeaderCO>();
        // 当前用户就是审批人
        String approverOidStr = OrgInformationUtil.getCurrentUserOid().toString();
        // 获取未审批/已审批的单据
        List<String> documentOidStrList = workflowClient.listApprovalDocument(Constants.PREPAYMENT_DOCUMENT_TYPE,
                approverOidStr, finished, beginDate, endDate);
        // 若没有满足条件的单据则不继续执行代码
        if (documentOidStrList.isEmpty()) {
            return page;
        }

        CashPrepaymentQueryDTO cashPrepaymentQueryDTO = new CashPrepaymentQueryDTO();
        // 单据参数
        cashPrepaymentQueryDTO.setDocumentOid(documentOidStrList);
        // 提交人参数
        List<String> userList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(userOid)) {
            userList.add(userOid);
        }
        cashPrepaymentQueryDTO.setUserList(userList);

        // 编号
        cashPrepaymentQueryDTO.setBusinessCode(businessCode);
        // 金额
        cashPrepaymentQueryDTO.setAmountFrom(amountFrom);
        cashPrepaymentQueryDTO.setAmountTo(amountTo);
        // 单据类型
        cashPrepaymentQueryDTO.setTypeId(typeId);
        // 描述
        cashPrepaymentQueryDTO.setDescription(description);

        page = selectHeadersByInput(cashPrepaymentQueryDTO, PageUtil.getPage(pageable));
        return page;
    }

    /**
     * 根据条件查询预付款单据信息：为工作流条件查询我的待审批单据使用
     *
     * @param cashPrepaymentQueryDTO
     * @return
     * @throws ParseException
     */
    public Page<CashPaymentRequisitionHeaderCO> selectHeadersByInput(CashPrepaymentQueryDTO cashPrepaymentQueryDTO, Page page) throws ParseException {
        List<CashPaymentRequisitionHeaderCO> cashPaymentRequisitionHeaderDTOS = new ArrayList<>();
        List<CashPaymentRequisitionHead> heads = new ArrayList<>();
        if (cashPrepaymentQueryDTO.getDateTo() != null) {//将结束日期加1
            String endDate = cashPrepaymentQueryDTO.getDateTo();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(endDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date tomorrow = c.getTime();
            cashPrepaymentQueryDTO.setSubmitDateTo(DateUtil.dateToZoneDateTime(tomorrow));
        }
        if (cashPrepaymentQueryDTO.getDateFrom() != null) {
            String strartDate = cashPrepaymentQueryDTO.getDateFrom();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(strartDate);
            cashPrepaymentQueryDTO.setSubmitDateFrom(DateUtil.dateToZoneDateTime(date));
        }
        /*if (CollectionUtils.isEmpty(cashPrepaymentQueryDTO.getUserList())) {
            return page;
        }*/
        if (CollectionUtils.isNotEmpty(cashPrepaymentQueryDTO.getDocumentOid())) {
            heads = cashPaymentRequisitionHeadMapper.selectPage(page,
                    new EntityWrapper<CashPaymentRequisitionHead>()
                            .in("document_oid", cashPrepaymentQueryDTO.getDocumentOid())
                            .like(StringUtils.isNotEmpty(cashPrepaymentQueryDTO.getBusinessCode()), "requisition_number", cashPrepaymentQueryDTO.getBusinessCode())
                            .like(StringUtils.isNotEmpty(cashPrepaymentQueryDTO.getDescription()), "description", cashPrepaymentQueryDTO.getDescription())
                            .ge(cashPrepaymentQueryDTO.getAmountFrom() != null, "advance_payment_amount", cashPrepaymentQueryDTO.getAmountFrom())
                            .le(cashPrepaymentQueryDTO.getAmountTo() != null, "advance_payment_amount", cashPrepaymentQueryDTO.getAmountTo())
                            .eq(cashPrepaymentQueryDTO.getTypeId() != null, "payment_req_type_id", cashPrepaymentQueryDTO.getTypeId())
                            .in(!CollectionUtils.isEmpty(cashPrepaymentQueryDTO.getUserList()), "application_oid", cashPrepaymentQueryDTO.getUserList())
                            .ge(cashPrepaymentQueryDTO.getSubmitDateFrom() != null, "submit_date", cashPrepaymentQueryDTO.getSubmitDateFrom())
                            .le(cashPrepaymentQueryDTO.getSubmitDateTo() != null, "submit_date", cashPrepaymentQueryDTO.getSubmitDateTo()).orderBy("requisition_number",false)

            );
            heads.stream().forEach(
                    head -> {
                        CashPaymentRequisitionHeaderCO dto = new CashPaymentRequisitionHeaderCO();
                        BeanUtils.copyProperties(head, dto);
                        CashPayRequisitionType type = cashPayRequisitionTypeService.selectById(head.getPaymentReqTypeId());
                        dto.setTypeName(type.getTypeName());
                        dto.setPaymentMethod(prepaymentHcfOrganizationInterface.getSysCodeValue(SystemCustomEnumerationType.CSH_PAYMENT_TYPE,
                                type.getPaymentMethodCategory(), RespCode.SYS_CODE_TYPE_NOT_EXIT).get(type.getPaymentMethodCategory()));
                        dto.setIfApplication(type.getNeedApply());
                        dto.setFormOid(type.getFormOid());
                        if (head.getSubmitDate() != null) {
                            dto.setStringSubmitDate(head.getSubmitDate().toString().substring(0, 10));
                        }
                        cashPaymentRequisitionHeaderDTOS.add(dto);
                    }
            );
        }
        if (CollectionUtils.isNotEmpty(cashPaymentRequisitionHeaderDTOS)) {
            page.setRecords(cashPaymentRequisitionHeaderDTOS);
        }
        return page;
    }

    /*
     工作流审批通过     推送单据到支付模块
     * */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public boolean pushToPayment(CashPaymentRequisitionHead header) {

        if(header == null){
            return Boolean.FALSE;
        }
        List<CashTransactionDataCreateCO> list = new ArrayList<>();
        List<CashPaymentRequisitionLine> lines =cashPaymentRequisitionLineService.getLinesByHeadID(header.getId());
        //根据头查询行信息
        CashPaymentRequisitionHeaderCO headerDTO = cashPaymentRequisitionHeaderAdapter.toDTO(header);
        lines.stream().forEach( line -> {
            CashTransactionDataCreateCO dataDto = new CashTransactionDataCreateCO();
            dataDto.setEntityOid(header.getDocumentOid());
            dataDto.setEntityType(801003);
            dataDto.setTenantId(header.getTenantId());
            dataDto.setAmount(line.getAmount());
            dataDto.setDocumentCategory("PREPAYMENT_REQUISITION");//业务大类：预付款单
            dataDto.setDocumentHeaderId(header.getId());
            dataDto.setDocumentNumber(header.getRequisitionNumber());
            dataDto.setEmployeeId(header.getEmployeeId());
            dataDto.setEmployeeName(prepaymentHcfOrganizationInterface.getUserById(header.getEmployeeId()).getFullName());
            dataDto.setRequisitionDate(header.getRequisitionDate());
            dataDto.setDocumentLineId(line.getId());
            dataDto.setCompanyId(header.getCompanyId());
            // 付款公司
            dataDto.setPaymentCompanyId(null);
            dataDto.setCurrency(line.getCurrency());
            dataDto.setExchangeRate(line.getExchangeRate());
            dataDto.setPartnerCategory(line.getPartnerCategory());
            dataDto.setPartnerId(line.getPartnerId());
            dataDto.setPartnerCode(line.getPartnerCode());
            dataDto.setPartnerName(line.getPartnerName());
            dataDto.setAccountName(line.getAccountName());
            dataDto.setAccountNumber(line.getAccountNumber());
            dataDto.setBankCode(line.getBankBranchCode());
            dataDto.setBankBranchName(line.getBankBranchName());
            dataDto.setBankBranchCode(line.getBankBranchCode());
            dataDto.setPaymentMethodCategory(line.getPaymentMethodCategory());
            dataDto.setRequisitionPaymentDate(line.getRequisitionPaymentDate());
            //dataDto.setCshTransactionTypeCode(PaymentModuleInterface.selectCashTransactionClassById(line.getCshTransactionClassId()).getTypeCode());
            dataDto.setCshTransactionClassId(line.getCshTransactionClassId());
            dataDto.setCshFlowItemId(line.getCashFlowId());
            dataDto.setContractHeaderId(line.getContractId());
            dataDto.setRemark(line.getDescription());
            dataDto.setFrozenFlag(false);
            dataDto.setDocumentTypeId(header.getPaymentReqTypeId());
            dataDto.setDocumentTypeName(headerDTO.getTypeName());
            dataDto.setApplicationLineId(line.getRefDocumentId());
            String s = JSONObject.toJSONString(dataDto);
            list.add(dataDto);
        });
        boolean pushResult = paymentModuleInterface.setPushPrepaymentToPayment(list);
        if(pushResult){
            header.setStatus(DocumentOperationEnum.APPROVAL_PASS.getId());
            this.updateById(header);
        }
        return pushResult;
    }

    /*推送单据到支付模块
     * */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public boolean pushToPayment_bak(Long headId) {
        List<CashTransactionDataCreateCO> list = new ArrayList<>();
        CashPaymentRequisitionHead header = cashPaymentRequisitionHeadMapper.selectById(headId);
        if (header == null) {
            throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_HEAD_NOT_EXIT);
        }
//        if(header.getStatus()!=DocumentOperationEnum.APPROVAL_PASS.getID()){
//            throw new BizException(RespCode.PREPAY_ONLY_PASS_CAN_PUSH);
//        }
        //根据头查询行信息
        List<CashPaymentRequisitionLine> lines = cashPaymentRequisitionLineService.selectList(
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq("payment_requisition_header_id", header.getId())
        );
        if (CollectionUtils.isEmpty(lines)) {
            throw new BizException(RespCode.PREPAY_CASHPAYREQUISITION_HEAD_LINE_IS_NULL);
        }
        CashPaymentRequisitionHeaderCO headerDTO = cashPaymentRequisitionHeaderAdapter.toDTO(header);
        for (CashPaymentRequisitionLine line : lines) {
            CashTransactionDataCreateCO dataDto = new CashTransactionDataCreateCO();
            dataDto.setEntityOid(header.getDocumentOid());
            dataDto.setEntityType(801003);
            dataDto.setTenantId(header.getTenantId());
            dataDto.setAmount(line.getAmount());
            dataDto.setDocumentCategory("PREPAYMENT_REQUISITION");//业务大类：预付款单
            dataDto.setDocumentHeaderId(header.getId());
            dataDto.setDocumentNumber(header.getRequisitionNumber());
            dataDto.setEmployeeId(header.getEmployeeId());
            dataDto.setEmployeeName(prepaymentHcfOrganizationInterface.getUserById(header.getEmployeeId()).getFullName());
            dataDto.setRequisitionDate(header.getRequisitionDate());
            dataDto.setDocumentLineId(line.getId());
            dataDto.setCompanyId(header.getCompanyId());
            // 付款公司
            dataDto.setPaymentCompanyId(null);
            dataDto.setCurrency(line.getCurrency());
            dataDto.setExchangeRate(line.getExchangeRate());
            dataDto.setPartnerCategory(line.getPartnerCategory());
            dataDto.setPartnerId(line.getPartnerId());
            dataDto.setPartnerCode(line.getPartnerCode());
            dataDto.setPartnerName(line.getPartnerName());
            dataDto.setAccountName(line.getAccountName());
            dataDto.setAccountNumber(line.getAccountNumber());
            dataDto.setBankCode(line.getBankBranchCode());
            dataDto.setBankBranchName(line.getBankBranchName());
            dataDto.setBankBranchCode(line.getBankBranchCode());
            dataDto.setPaymentMethodCategory(line.getPaymentMethodCategory());
            dataDto.setRequisitionPaymentDate(line.getRequisitionPaymentDate());
            //jiu.zhao 支付
            //dataDto.setCshTransactionTypeCode(paymentModuleInterface.selectCashTransactionClassById(line.getCshTransactionClassId()).getTypeCode());
            dataDto.setCshTransactionClassId(line.getCshTransactionClassId());
            dataDto.setCshFlowItemId(line.getCashFlowId());
            dataDto.setContractHeaderId(line.getContractId());
            dataDto.setRemark(line.getDescription());
            dataDto.setFrozenFlag(false);
            dataDto.setDocumentTypeId(header.getPaymentReqTypeId());
            dataDto.setDocumentTypeName(headerDTO.getTypeName());
            dataDto.setApplicationLineId(line.getRefDocumentId());
            String s = JSONObject.toJSONString(dataDto);
            list.add(dataDto);
        }
        boolean b = paymentModuleInterface.setPushPrepaymentToPayment(list);
        if(b){
            header.setStatus(DocumentOperationEnum.APPROVAL_PASS.getId());
            this.updateById(header);
        }
        return b;
    }


    /**
     * 根据合同编号查询预付款单头行信息--合同模块需要的接口
     *
     * @param contractNumber
     * @return
     */
    public List<CashPaymentParamCO> getByContractNumber(String contractNumber) {
        List<CashPaymentRequisitionLine> lines = cashPaymentRequisitionLineService.selectList(
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq(StringUtils.isNotEmpty(contractNumber), "contract_number", contractNumber)
                        .orderBy("payment_requisition_header_id")
        );
        if (CollectionUtils.isEmpty(lines)) {
            return new ArrayList<>();
        }
        List<CashPaymentParamCO> dtos = new ArrayList<>();
        for (CashPaymentRequisitionLine line : lines) {
            CashPaymentParamCO dto = new CashPaymentParamCO();
            // 过滤掉编辑中1001 驳回1005
            // 应该是要调用合同queryContractAssociated获取关联数据
            List<CashPaymentRequisitionHead> cashPaymentRequisitionHead =
                    cashPaymentRequisitionHeadMapper.selectList(new EntityWrapper<CashPaymentRequisitionHead>()
                            .eq("id", line.getPaymentRequisitionHeaderId())
                            .ne("status", "1001")
                            .ne("status","1005")
                            .ne("status","1003") //撤回
                    );
            if (cashPaymentRequisitionHead.size()>0 && CollectionUtils.isNotEmpty(cashPaymentRequisitionHead)) {
                CashPaymentRequisitionHeaderCO headerDTO = cashPaymentRequisitionHeaderAdapter.toDTO(cashPaymentRequisitionHead.get(0));
                dto.setHead(headerDTO);
                List<CashPaymentRequisitionLineCO> lineDTOS = new ArrayList<>();
                List<CashPaymentRequisitionLine> list = cashPaymentRequisitionLineService.selectList(
                        new EntityWrapper<CashPaymentRequisitionLine>()
                                .eq("payment_requisition_header_id", headerDTO.getId())
                );
                for (CashPaymentRequisitionLine requisitionLine : list) {
                    if (requisitionLine.getContractLineId() != null && contractNumber.equals(requisitionLine.getContractNumber())) {//只添加预付款单行上有此合同行的数据
                        CashPaymentRequisitionLineCO lineDTO = cashPaymentRequisitionLineAdapter.toDTO(requisitionLine);
                        lineDTOS.add(lineDTO);
                    }
                }
                dto.setLine(lineDTOS);
                dtos.add(dto);
            }
        }

        return dtos;
    }


    /**
     * 根据头oid判断有没有头行
     *
     * @param oid
     * @return
     */
    public Boolean HeadHasLine(String oid) {
        List<CashPaymentRequisitionHead> heads = cashPaymentRequisitionHeadMapper.selectList(
                new EntityWrapper<CashPaymentRequisitionHead>()
                        .eq("document_oid", oid)
        );
        if (CollectionUtils.isEmpty(heads)) {
            return false;
        }
        List<CashPaymentRequisitionLine> lines = cashPaymentRequisitionLineService.selectList(
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq("payment_requisition_header_id", heads.get(0).getId())
        );
        if (CollectionUtils.isNotEmpty(lines)) {
            return true;
        }
        return false;
    }


    //根据单据编号查询预付款单信息
    public CashPaymentParamCO getHeadAndLineByCode(String code, Page page) {
        List<CashPaymentRequisitionHead> heads = cashPaymentRequisitionHeadMapper.selectList(
                new EntityWrapper<CashPaymentRequisitionHead>()
                        .eq("requisition_number", code)
        );
        if (CollectionUtils.isEmpty(heads)) {
            return null;
        }
        CashPaymentParamCO paramDTO = getCashPaymentRequisitionByHeadId(heads.get(0).getId(), page);
        return paramDTO;
    }


    //预付款单关联合同
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public Boolean toContract(CashPaymentRequisitionHead head, String type, Boolean isWorkflow) {
        /*List<CashPaymentRequisitionLine> lines = cashPaymentRequisitionLineService.selectList(
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq("payment_requisition_header_id", head.getId())
        );
        ExceptionDetail exceptionDetail = new ExceptionDetail();
        exceptionDetail.setErrorCode("0000");
        List<ContractDocumentRelationCO> list = new ArrayList<>();
        for (CashPaymentRequisitionLine line : lines) {
            if (line.getContractId() != null) {
                ContractDocumentRelationCO relation = new ContractDocumentRelationCO();
                relation.setAmount(line.getAmount());
                relation.setContractHeadId(line.getContractId());
                relation.setContractLineId(line.getContractLineId());
                relation.setCreatedBy(line.getCreatedBy());
                relation.setCreatedDate(line.getCreatedDate());
                relation.setCurrencyCode(line.getCurrency());
                relation.setDocumentType("PREPAYMENT_REQUISITION");
                relation.setExchangeRate(line.getExchangeRate());
                relation.setDocumentHeadId(head.getId());
                relation.setDocumentLineId(line.getId());
                relation.setFunctionAmount(line.getFunctionAmount());
                list.add(relation);
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            exceptionDetail = ContractModuleInterface.contractDocumentRelationBatch(list, type, isWorkflow);
        }
        return exceptionDetail;*/

        Boolean aBoolean = null;

        List<CashPaymentRequisitionLine> lines = cashPaymentRequisitionLineService.selectList(
                new EntityWrapper<CashPaymentRequisitionLine>()
                        .eq("payment_requisition_header_id", head.getId())
        );
        List<ContractDocumentRelationCO> list = new ArrayList<>();
        for (CashPaymentRequisitionLine line : lines) {
            if (line.getContractId() != null) {
                ContractDocumentRelationCO relation = new ContractDocumentRelationCO();
                relation.setAmount(line.getAmount());
                relation.setContractHeadId(line.getContractId());
                relation.setContractLineId(line.getContractLineId());
                relation.setCreatedBy(line.getCreatedBy());
                relation.setCreatedDate(line.getCreatedDate());
                relation.setCurrencyCode(line.getCurrency());
                relation.setDocumentType("PREPAYMENT_REQUISITION");
                relation.setExchangeRate(line.getExchangeRate());
                relation.setDocumentHeadId(head.getId());
                relation.setDocumentLineId(line.getId());
                relation.setFunctionAmount(line.getFunctionAmount());
                list.add(relation);
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            aBoolean = ContractModuleInterface.contractDocumentRelationBatch(list, type, isWorkflow);
        }
        return aBoolean;
    }


    /**
     * 根据申请单头id和预付款单头id查询相应金额
     *
     * @param requisitionHeadId
     * @param prepaymentHeadId
     * @return
     */
    public Map<String, Double> getAmountByRequisitionAndPrepayment(Long requisitionHeadId, Long prepaymentHeadId, Long setOfBooksId) {
        //用setOfBooksId调三方接口取本位币
        List<CurrencyDTO> list = cashPaymentRequisitionHeadMapper.getAmountByHeadIdAndRefHeadId(requisitionHeadId, prepaymentHeadId);
        Map<String, Double> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (CurrencyDTO dto : list) {
                if (map.get(dto.getCurrency()) != null) {
                    map.put(dto.getCurrency(), map.get(dto.getCurrency()) + dto.getAmount());
                } else {
                    map.put(dto.getCurrency(), dto.getAmount());
                }
            }
        }


//        List<CashPaymentRequisitionLine> lines = cashPaymentRequisitionLineService.selectList(
//                new EntityWrapper<CashPaymentRequisitionLine>()
//                        .eq("ref_document_id", requisitionHeadId)
//                        .eq("is_enabled", true)
//                        .eq("is_deleted", false)
//                        .groupBy("payment_requisition_header_id")
//
//        );
//        if(CollectionUtils.isEmpty(lines) ){
//            return new HashMap<>();
//        }
//        Double amount = 0D;
//        if(CollectionUtils.isNotEmpty(lines)){
//            for(CashPaymentRequisitionLine line:lines){
//                //状态不是新建，驳回，撤回的单子的金额
//                int status = cashPaymentRequisitionHeadMapper.selectById(line.getPaymentRequisitionHeaderId()).getStatus();
//                if(DocumentOperationEnum.APPROVAL.getID()==status
//                        ||
//                  DocumentOperationEnum.APPROVAL_PASS.getID()==status
//                ){
//                    Map<String, Double> doubleMap = getAmountGroupByCodeByHeadId(line.getPaymentRequisitionHeaderId());
//                    doubleMap.remove("totalFunctionAmount");
//                    getTotalAmount(map,doubleMap);
//                }
//            }
//        }
//        if(prepaymentHeadId!=null){
//            Map<String, Double> prepaymentAmountMap = getAmountGroupByCodeByHeadId(prepaymentHeadId);
//            prepaymentAmountMap.remove("totalFunctionAmount");
//            getTotalAmount(map,prepaymentAmountMap);
//        }
        return map;
    }


    public void getTotalAmount(Map<String, Double> map, Map<String, Double> detailMap) {
        if (map.isEmpty()) {//第一次，把值直接赋给map
            map.putAll(detailMap);
        } else {//后面的每次遍历map的key
            for (String key : detailMap.keySet()) {
                if (map.get(key) == null) {//如果map里面没有，则设置
                    map.put(key, detailMap.get(key));
                } else {//如果map里面有，则相加
                    map.put(key, map.get(key) + detailMap.get(key));
                }
            }
        }

    }

    /**
     * 预付款财务查询
     * @return
     */
    public Page<CashPaymentRequisitionHead> getHeadByQuery(
            Long companyId,
            String requisitionNumber,
            Long typeId,
            Integer status,
            Long unitId,
            Long applyId,
            ZonedDateTime applyDateFrom,
            ZonedDateTime applyDateTo,
            Double amountFrom,
            Double amountTo,
            Double noWriteAmountFrom,
            Double noWriteAmountTo,
            String remark,
            Page<CashPaymentRequisitionHead> page
    ) {
        if(noWriteAmountFrom != null && noWriteAmountTo != null){
            if(noWriteAmountFrom > noWriteAmountTo){
                return page;
            }
        }

        Wrapper<CashPaymentRequisitionHead> wrapper = new EntityWrapper<CashPaymentRequisitionHead>()
                .eq("tenant_id",OrgInformationUtil.getCurrentTenantId())
                .eq(companyId != null, "company_id", companyId)
                .like(StringUtils.isNotEmpty(requisitionNumber), "requisition_number", requisitionNumber)
                .eq(typeId != null, "payment_req_type_id", typeId)
                .eq(status != null, "status", status)
                .eq(unitId != null, "unit_id", unitId)
                .eq(applyId != null, "employee_id", applyId)
                .ge(applyDateFrom != null, "requisition_date", applyDateFrom)
                .le(applyDateTo != null, "requisition_date", applyDateTo)
                .ge(amountFrom != null, "advance_payment_amount", amountFrom)
                .le(amountTo != null, "advance_payment_amount", amountTo)
                .like(StringUtils.isNotEmpty(remark), "description", remark);
        if (noWriteAmountFrom == null && noWriteAmountTo == null) {
            wrapper = wrapper
                    .orderBy("requisition_number", false);
        }else {
            // 获取不满足条件的单据信息
            String ids = "";
            //jiu.zhao 支付
            /*List<Long> excludeDocumentList = paymentModuleInterface.getExcludeDocumentCashWriteOffAmountDTOByInput(noWriteAmountFrom, noWriteAmountTo, null);
            if(CollectionUtils.isNotEmpty(excludeDocumentList)){
                ids = StringUtils.join(excludeDocumentList, ",");
            }*/
            wrapper = wrapper
//                    .in("id", documentAmountDTOList.stream().map(CashWriteOffDocumentAmountCO::getDocumentHeaderId).collect(Collectors.toList()))
//                    .orNew()
//                    .in("status",statusInteger)
//                    .and()
                    .notExists(! "".equals(ids),"select 1 from dual where id in (" + ids + ")")
                    .ge(noWriteAmountFrom!=null,"advance_payment_amount",noWriteAmountFrom)
                    .le(noWriteAmountTo!=null,"advance_payment_amount",noWriteAmountTo)
                    .eq(status!=null,"status",status)
                    .orderBy("requisition_number", false)
            ;
        }
        List<CashPaymentRequisitionHead> records = baseMapper.listHeaderAndTypName(page, wrapper);

        if (CollectionUtils.isEmpty(records)){
            return page;
        }
        //未核销从，至
        //jiu.zhao 支付
        List<CashWriteOffDocumentAmountCO> documentAmountDTOList;
        //List<CashWriteOffDocumentAmountCO> documentAmountDTOList = paymentModuleInterface.getCashWriteOffDocumentAmountDTOByInput(noWriteAmountFrom, noWriteAmountTo, null);

        Map<Long, CashWriteOffDocumentAmountCO> writeOffDocumentAmountDTOMap = null;
        //Map<Long, CashWriteOffDocumentAmountCO> writeOffDocumentAmountDTOMap = documentAmountDTOList.stream().collect(Collectors.toMap(CashWriteOffDocumentAmountCO::getDocumentHeaderId, (p) -> p));

        List<Long> longs = records.stream().filter(head -> DocumentOperationEnum.APPROVAL_PASS.getId().equals(head.getStatus())).map(CashPaymentRequisitionHead::getId).collect(Collectors.toList());
        List<PaymentDocumentAmountCO> payAndReturnAmount = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(longs)) {
            //payAndReturnAmount = paymentModuleInterface.getPrepaymentPayAndReturnAmount(longs, true, null, null, null);
        }

        // 公司
        Set<Long> ids = records.stream().map(CashPaymentRequisitionHead::getCompanyId).collect(Collectors.toSet());
        List<CompanyCO> companySumCO = prepaymentHcfOrganizationInterface.listCompanyById(new ArrayList<>(ids));
        Map<Long, String> companyMap = companySumCO.stream().collect(Collectors.toMap(CompanyCO::getId, CompanyCO::getName, (k1, k2) -> k1));
        // 部门
        ids = records.stream().map(CashPaymentRequisitionHead::getUnitId).collect(Collectors.toSet());
        List<DepartmentCO> departments = prepaymentHcfOrganizationInterface.getDepartmentByDepartmentIds(new ArrayList<>(ids));
        Map<Long, String> unitMap = departments.stream().collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getName, (k1, k2) -> k1));

        // 员工
        Set<Long> empIds = records.stream().map(CashPaymentRequisitionHead::getEmployeeId).collect(Collectors.toSet());
        ids = records.stream().map(CashPaymentRequisitionHead::getCreatedBy).collect(Collectors.toSet());
        ids.addAll(empIds);
        List<ContactCO> users = prepaymentHcfOrganizationInterface.listByUserIdsConditionByKeyWord(new ArrayList<>(ids),null);
        Map<Long, String> empMap = users.stream().collect(Collectors.toMap(ContactCO::getId, ContactCO::getFullName, (k1, k2) -> k1));



        for (CashPaymentRequisitionHead head : records) {

            // 部门
            if(unitMap.containsKey(head.getUnitId())){
                head.setUnitName(unitMap.get(head.getUnitId()));
            }
            // 公司
            if(companyMap.containsKey(head.getCompanyId())){
                head.setCompanyName(companyMap.get(head.getCompanyId()));
            }
            // 创建人
            if (empMap.containsKey(head.getCreatedBy())){
                head.setCreateByName(empMap.get(head.getCreatedBy()));
            }
            // 员工
            if(empMap.containsKey(head.getEmployeeId())){
                head.setEmployeeName(empMap.get(head.getEmployeeId()));
            }
            CashWriteOffDocumentAmountCO documentAmountCO = writeOffDocumentAmountDTOMap.get(head.getId());
            if (documentAmountCO != null) {
                head.setNoWritedAmount(documentAmountCO.getUnWriteOffAmount() != null ?
                        documentAmountCO.getUnWriteOffAmount() : BigDecimal.ZERO);
                head.setWritedAmount(documentAmountCO.getWriteOffAmount() != null ?
                        documentAmountCO.getWriteOffAmount() : BigDecimal.ZERO);
            } else {
                head.setWritedAmount(BigDecimal.ZERO);
                head.setNoWritedAmount(head.getAdvancePaymentAmount());
            }

            List<PaymentDocumentAmountCO> amountCOS = payAndReturnAmount.stream().filter(
                    d -> d.getDocumentId().equals(head.getId())
            ).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(amountCOS)) {
                head.setPaidAmount(BigDecimal.ZERO);
                head.setReturnAmount(BigDecimal.ZERO);
            } else {
                head.setPaidAmount(amountCOS.get(0).getPayAmount() != null ?
                        amountCOS.get(0).getPayAmount() : BigDecimal.ZERO);
                head.setReturnAmount(amountCOS.get(0).getReturnAmount() != null ?
                        amountCOS.get(0).getReturnAmount() : BigDecimal.ZERO);
            }

        }
        page.setRecords(records);
        return page;
    }


    public List<CashPaymentRequisitionLine> getHeaderAndLineByLine(List<Long> lineIds) {

        List<CashPaymentRequisitionLine> cashPaymentRequisitionLines = cashPaymentRequisitionLineService.selectBatchIds(lineIds);
        if (CollectionUtils.isNotEmpty(cashPaymentRequisitionLines)) {
            for (CashPaymentRequisitionLine line : cashPaymentRequisitionLines) {
                CashPaymentRequisitionHead head = this.selectById(line.getPaymentRequisitionHeaderId());
                CashPaymentRequisitionHeaderCO headerCO = cashPaymentRequisitionHeaderAdapter.toDTO(head);
                line.setPrepaymentHead(headerCO);
            }
        }
        return cashPaymentRequisitionLines;
    }

    /**
     * @author mh.z
     * @date 2019/02/19
     * @description 根据oid获取用户
     *
     * @param userOid
     * @return
     */
    public ContactCO getUserByOid(String userOid) {
        if (userOid == null) {
            throw new IllegalArgumentException("userOid null");
        }

        ContactCO userCO = prepaymentHcfOrganizationInterface.getEmployeeByOid(userOid);
        return userCO;
    }

    /**
     * @author mh.z
     * @date 2019/02/19
     * @description 根据oid获取表单
     *
     * @param formOid
     * @return
     */
    public ApprovalFormCO getApprovalFormByOid(String formOid) {
        if (formOid == null) {
            throw new IllegalArgumentException("formOid null");
        }

        ApprovalFormCO approvalFormCO = workflowClient.getApprovalFormByOid(formOid);
        return approvalFormCO;
    }

    public List<ContactCO> listUsersByCreatedCashPaymentRequisitions() {
        List<Long> userList = baseMapper.selectList(
                new EntityWrapper<CashPaymentRequisitionHead>()
                        .eq("created_by", OrgInformationUtil.getCurrentUserId())
        ).stream().map(CashPaymentRequisitionHead::getEmployeeId).distinct().filter(e -> e != null).collect(Collectors.toList());
        if (userList.size() == 0) {
            return new ArrayList<>();
        }
        return prepaymentHcfOrganizationInterface.listUsersByIds(userList);
    }
}
