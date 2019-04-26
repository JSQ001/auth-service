package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.*;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.core.util.DateUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.core.web.adapter.DomainObjectAdapter;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.implement.web.ContactControllerImpl;
import com.hand.hcf.app.payment.domain.*;
import com.hand.hcf.app.payment.domain.enumeration.PaymentConstants;
import com.hand.hcf.app.payment.domain.enumeration.PaymentDocumentOperationEnum;
import com.hand.hcf.app.payment.externalApi.PaymentOrganizationService;
import com.hand.hcf.app.payment.persistence.PaymentRequisitionHeaderMapper;
import com.hand.hcf.app.payment.persistence.PaymentRequisitionTypesMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import com.hand.hcf.app.payment.web.dto.PaymentRequisitionHeaderWebDTO;
import com.hand.hcf.app.workflow.dto.ApprovalDocumentCO;
import com.hand.hcf.app.workflow.dto.ApprovalResultCO;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 10:23 2018/1/24
 * @Modified by
 */
@Service
public class PaymentRequisitionHeaderService extends BaseService<PaymentRequisitionHeaderMapper,PaymentRequisitionHeader> {
    private static final Logger log  = LoggerFactory.getLogger(PaymentRequisitionHeaderService.class);
    private final PaymentRequisitionTypesMapper paymentRequisitionTypesMapper;
    private final PaymentRequisitionLineService paymentRequisitionLineService;
    private static final String ACP_CODE = "ACP_REQUISITION";
    private final CashDataRelationAcpService cashDataRelationAcpService;
    private final CashTransactionDataService cashTransactionDataService;
    private final PaymentOrganizationService organizationService;
    private final WorkflowControllerImpl workflowClient;// 工作流服务
    private final WorkflowControllerImpl workflowInterface;
    private final ContactControllerImpl userClient;

    @Value("${spring.application.name:}")
    private  String applicationName;

    public PaymentRequisitionHeaderService(PaymentRequisitionTypesMapper paymentRequisitionTypesMapper,
                                           PaymentRequisitionLineService paymentRequisitionLineService,
                                           CashDataRelationAcpService cashDataRelationAcpService,
                                           CashTransactionDataService cashTransactionDataService,
                                           PaymentOrganizationService organizationService,
                                           WorkflowControllerImpl workflowClient,
                                           WorkflowControllerImpl workflowInterface,
                                           ContactControllerImpl userClient) {
        this.paymentRequisitionTypesMapper = paymentRequisitionTypesMapper;
        this.paymentRequisitionLineService = paymentRequisitionLineService;
        this.cashDataRelationAcpService = cashDataRelationAcpService;
        this.cashTransactionDataService = cashTransactionDataService;
        this.organizationService = organizationService;
        this.workflowClient = workflowClient;
        this.workflowInterface = workflowInterface;
        this.userClient = userClient;
    }

    /**
     * @Author: bin.xie
     * @Description: 新增付款申请单头信息
     * @param: paymentRequisitionHeader
     * @return: com.hand.hcf.app.payment.domain.PaymentRequisitionHeader
     * @Date: Created in 2018/1/24 10:40
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRequisitionHeader createHeader(PaymentRequisitionHeader paymentRequisitionHeader){
        if (paymentRequisitionHeader.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        //校验其他非空字段
        emptyCheck(paymentRequisitionHeader);
        paymentRequisitionHeader.setStatus(PaymentDocumentOperationEnum.GENERATE.getId());
        //自动生成单据编号
        String requisitionNumber = organizationService.getCoding(ACP_CODE, paymentRequisitionHeader.getCompanyId());

        paymentRequisitionHeader.setRequisitionNumber(requisitionNumber);

        paymentRequisitionHeader.setDocumentOid(UUID.randomUUID().toString());

        PaymentRequisitionTypes paymentRequisitionTypes = paymentRequisitionTypesMapper.selectById(paymentRequisitionHeader.getAcpReqTypeId());
        paymentRequisitionHeader.setFormOid(paymentRequisitionTypes.getFormOid());
        // 单据类型
        paymentRequisitionHeader.setDocumentType(TypeConversionUtils.parseLong(paymentRequisitionTypes.getFormType()));
        ContactCO userCO = organizationService.getUserById(paymentRequisitionHeader.getEmployeeId());
        String userOid = userCO == null ? OrgInformationUtil.getCurrentUserOid().toString() : userCO.getUserOid();
        paymentRequisitionHeader.setApplicantOid(userOid);
        paymentRequisitionHeader.setUnitOid(organizationService.getDepartmentByEmpOid(userOid).getDepartmentOid().toString());
        paymentRequisitionHeader.setAcpReqTypeName(paymentRequisitionTypes.getDescription());
        baseMapper.insert(paymentRequisitionHeader);
        return paymentRequisitionHeader;
    }

    /**
     * @Author: bin.xie
     * @Description: 修改付款申请单头信息
     * @param: paymentRequisitionHeader
     * @return: com.hand.hcf.app.payment.domain.PaymentRequisitionHeader
     * @Date: Created in 2018/1/24 10:57
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRequisitionHeader updateHeader(PaymentRequisitionHeader paymentRequisitionHeader){
        if (paymentRequisitionHeader.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        PaymentRequisitionHeader oldHeader = operateCheck(paymentRequisitionHeader.getId(),0);
        paymentRequisitionHeader.setDocumentOid(oldHeader.getDocumentOid());
        // 单据类型
        paymentRequisitionHeader.setDocumentType(oldHeader.getDocumentType());
        paymentRequisitionHeader.setStatus(oldHeader.getStatus());
        paymentRequisitionHeader.setRequisitionNumber(oldHeader.getRequisitionNumber());

        this.updateById(paymentRequisitionHeader);
        return paymentRequisitionHeader;
    }
    /**
     * @Author: bin.xie
     * @Description: 根据头ID删除付款申请单头信息 同时删除行信息
     * @param: id  头ID
     * @return: void
     * @Date: Created in 2018/1/24 11:13
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteHeaderById(Long id){
        operateCheck(id,-1);
        this.deleteById(id);
        PaymentRequisitionLine line = new PaymentRequisitionLine();
        line.setHeaderId(id);
        paymentRequisitionLineService.delete(new EntityWrapper<>(line));
    }

    /**
     * @Author: bin.xie
     * @Description: 分页查询
     * @param: requisitionNumber 付款申请单编号
     * @param: employeeId 员工ID
     * @param: acpReqTypeId 付款申请单类型ID
     * @param: status 状态代码
     * @param: requisitionDateFrom 申请日期从
     * @param: requisitionDateTo 申请日期至
     * @param: functionAmountFrom 金额从
     * @param: functionAmountTo 金额至
     * @param: page
     * @param: description 描述
     * @return: java.util.List<com.hand.hcf.app.payment.domain.PaymentRequisitionHeader>
     * @Date: Created in 2018/1/24 12:00
     * @Modified by
     */
    @Transactional(readOnly = true)
    public List<PaymentRequisitionHeaderWebDTO> getHeaderByCondition(String requisitionNumber,
                                                                     Long employeeId,
                                                                     Long acpReqTypeId,
                                                                     String status,
                                                                     String requisitionDateFrom,
                                                                     String requisitionDateTo,
                                                                     BigDecimal functionAmountFrom,
                                                                     BigDecimal functionAmountTo,
                                                                     Page page,
                                                                     String description){
        ZonedDateTime requisitionZonedDateFrom = DateUtil.stringToZonedDateTime(requisitionDateFrom);
        ZonedDateTime requisitionZonedDateTo = DateUtil.stringToZonedDateTime(requisitionDateTo);
        if (requisitionZonedDateTo != null){
            requisitionZonedDateTo = requisitionZonedDateTo.plusDays(1);
        }
        List<PaymentRequisitionHeader> lists = baseMapper.listHeaders(page,new EntityWrapper<PaymentRequisitionHeader>()
                .like(requisitionNumber != null,"t.requisition_number",requisitionNumber)
                .eq(status != null ,"t.status",status)
                .eq(acpReqTypeId != null ,"t.acp_req_type_id",acpReqTypeId)
                .eq("t.created_by", OrgInformationUtil.getCurrentUserId())
                .eq(employeeId != null, "t.employee_id", employeeId)
                .ge(requisitionZonedDateFrom != null,"t.requisition_date",requisitionZonedDateFrom)
                .lt(requisitionZonedDateTo != null,"t.requisition_date",requisitionZonedDateTo)
                .ge(functionAmountFrom != null,"t.function_amount",functionAmountFrom)
                .le(functionAmountTo != null,"t.function_amount",functionAmountTo)
                .like(description != null,"t.description",description)
                .orderBy("t.id",false));
        return toDTO(lists,false, false);
    }

    /**
     * @Author: bin.xie
     * @Description: 根据操作类型去更改状态
     * @param: id  主键
     * @param: operateType  操作类型 -1---删除 0--修改 1002--提交 1004--审核 1005--审批驳回 6002--取消 6003--完成
     * @return: com.hand.hcf.app.payment.domain.PaymentRequisitionHeader
     * @Date: Created in 2018/1/24 15:03
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRequisitionHeader operateHeader(Long id, Integer operateType){
        PaymentRequisitionHeader header = operateCheck(id,operateType);
        header.setStatus(operateType);
        header.setVersionNumber(header.getVersionNumber() + 1);
        header.setLastUpdatedDate(ZonedDateTime.now());
        header.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        baseMapper.updateById(header);
        return header;
    }

    /**
     * @Author: bin.xie
     * @Description: 付款申请单非空检验
     * @param: paymentRequisitionHeader 头信息
     * @return: void
     * @Date: Created in 2018/1/24 10:33
     * @Modified by
     */
    public void emptyCheck(PaymentRequisitionHeader paymentRequisitionHeader){
        if (paymentRequisitionHeader.getCompanyId() == null) {
            throw new BizException(RespCode.SYS_COLUMN_SHOULD_NOT_BE_EMPTY,new String[]{"公司"});
        }
        if (paymentRequisitionHeader.getAcpReqTypeId() == null) {
            throw new BizException(RespCode.SYS_COLUMN_SHOULD_NOT_BE_EMPTY,new String[]{"借款申请单类型"});
        }
        if (paymentRequisitionHeader.getDescription() == null) {
            throw new BizException(RespCode.SYS_COLUMN_SHOULD_NOT_BE_EMPTY,new String[]{"事由说明"});
        }
        if (paymentRequisitionHeader.getRequisitionDate() == null) {
            throw new BizException(RespCode.SYS_COLUMN_SHOULD_NOT_BE_EMPTY,new String[]{"申请日期"});
        }
        if (paymentRequisitionHeader.getEmployeeId() == null) {
            throw new BizException(RespCode.SYS_COLUMN_SHOULD_NOT_BE_EMPTY,new String[]{"申请员工"});
        }
        if (paymentRequisitionHeader.getUnitId() == null) {
            throw new BizException(RespCode.SYS_COLUMN_SHOULD_NOT_BE_EMPTY,new String[]{"部门"});
        }
    }

    /**
     * @Author: bin.xie
     * @Description:  根据付款申请单头ID进行校验
     * @param: id
     * @param: operateType 操作类型 -1---删除 0--修改 1002--提交 1004--审核 1005--审批驳回 6002--取消 6003--完成
     * @return: ContractHeader
     * @Date: Created in 2018/1/24 10:59
     * @Modified by
     */
    public PaymentRequisitionHeader operateCheck(Long id, Integer operateType) {
        PaymentRequisitionHeader paymentRequisitionHeader = baseMapper.selectById(id);
        if (paymentRequisitionHeader == null) {
            throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_NOT_EXISTS);
        }

        Integer status = paymentRequisitionHeader.getStatus();
        check(operateType,status);
        return paymentRequisitionHeader;
    }

    /**
     * @Author: bin.xie
     * @Description: 根据ID查询付款申请单信息
     * @param: id
     * @return: com.hand.hcf.app.payment.domain.PaymentRequisitionHeader
     * @Date: Created in 2018/1/24 15:04
     * @Modified by
     */
    @Transactional(readOnly = true)
    public PaymentRequisitionHeaderWebDTO getHeaderById(Long id, Boolean flag){
        return toDTO(Arrays.asList(baseMapper.getHeaderById(id)),flag, true).get(0);
    }



    /**
     * @Author: bin.xie
     * @Description: 实体类转DTO
     * @param: paymentRequisitionHeader
     * @return: com.hand.hcf.app.payment.PaymentRequisitionHeaderDTO
     * @Date: Created in 2018/1/24 16:20
     * @Modified by
     */
    private List<PaymentRequisitionHeaderWebDTO> toDTO(List<PaymentRequisitionHeader> lists,
                                                       Boolean setLines,
                                                       Boolean setAttachment){
        Map<Long, String> unitMap = null;
        if (CollectionUtils.isEmpty(lists)){
            return new ArrayList<>();
        }
        // 公司
        Set<Long> ids = lists.stream().map(PaymentRequisitionHeader::getCompanyId).collect(Collectors.toSet());
        List<CompanyCO> companySumCOs = organizationService.listByIds(new ArrayList<>(ids));
        Map<Long, String> companyMap = companySumCOs.stream().collect(Collectors.toMap(CompanyCO::getId, CompanyCO::getName, (k1, k2) -> k1));
        // 部门
        ids = lists.stream().map(PaymentRequisitionHeader::getUnitId).collect(Collectors.toSet());
        List<DepartmentCO> departments = organizationService.listPathByIds(new ArrayList<>(ids));
        if(departments != null){
            unitMap = departments.stream().collect(Collectors.toMap(DepartmentCO::getId, DepartmentCO::getName, (k1, k2) -> k1));

        }
        // 员工
        Set<Long> empIds = lists.stream().map(PaymentRequisitionHeader::getEmployeeId).collect(Collectors.toSet());
        ids = lists.stream().map(PaymentRequisitionHeader::getCreatedBy).collect(Collectors.toSet());
        ids.addAll(empIds);
        List<ContactCO> users = organizationService.listByUserIds(new ArrayList<>(ids));
        Map<Long, String> empMap = users.stream().collect(Collectors.toMap(ContactCO::getId, ContactCO::getFullName, (k1, k2) -> k1));

        List<PaymentRequisitionHeaderWebDTO> dtoLists = new ArrayList<>();
        for (PaymentRequisitionHeader paymentRequisitionHeader : lists) {
            PaymentRequisitionHeaderWebDTO paymentRequisitionHeaderWebDTO = new PaymentRequisitionHeaderWebDTO();
            BeanUtils.copyProperties(paymentRequisitionHeader, paymentRequisitionHeaderWebDTO);
            DomainObjectAdapter.toDto(paymentRequisitionHeaderWebDTO, paymentRequisitionHeader);
            // 由于付款申请单无币种字段，金额为本位币金额，行上的数据存在多个币种，因此默认为人民币
            paymentRequisitionHeaderWebDTO.setCurrency("CNY");
            //先判断userMap存在数据不，不存在就去查
            // 部门
            if(unitMap != null) {
                if (unitMap.containsKey(paymentRequisitionHeaderWebDTO.getUnitId())) {
                    paymentRequisitionHeaderWebDTO.setUnitName(unitMap.get(paymentRequisitionHeaderWebDTO.getUnitId()));
                }
            }
            // 公司
            if(companyMap.containsKey(paymentRequisitionHeaderWebDTO.getCompanyId())){
                paymentRequisitionHeaderWebDTO.setCompanyName(companyMap.get(paymentRequisitionHeaderWebDTO.getCompanyId()));
            }
            // 创建人
            if (empMap.containsKey(paymentRequisitionHeaderWebDTO.getCreatedBy())){
                paymentRequisitionHeaderWebDTO.setCreatedName(empMap.get(paymentRequisitionHeaderWebDTO.getCreatedBy()));
            }
            // 员工
            if(empMap.containsKey(paymentRequisitionHeaderWebDTO.getEmployeeId())){
                paymentRequisitionHeaderWebDTO.setEmployeeName(empMap.get(paymentRequisitionHeaderWebDTO.getEmployeeId()));
            }
            paymentRequisitionHeaderWebDTO.setAcpReqTypeName(paymentRequisitionHeader.getAcpReqTypeName());
            paymentRequisitionHeaderWebDTO.setRequisitionDate(paymentRequisitionHeader.getRequisitionDate());

            if(paymentRequisitionHeader.getSubmitDate()!=null){
                paymentRequisitionHeaderWebDTO.setSubmitDate(paymentRequisitionHeader.getSubmitDate().substring(0,10));
            }
            if (setLines) {
                paymentRequisitionHeaderWebDTO.setPaymentRequisitionLineDTO(
                        paymentRequisitionLineService.getLinesByHeaderId(
                                paymentRequisitionHeader.getId(), paymentRequisitionHeader.getStatus()));

                paymentRequisitionHeaderWebDTO.setPaymentRequisitionNumberDTO(
                        paymentRequisitionLineService.sumAmountByCurrency(paymentRequisitionHeader.getId()));

            }
            // 设置附件
            if (setAttachment) {
                if (StringUtils.hasText(paymentRequisitionHeader.getAttachmentOid())) {
                    List<String> asList = Arrays.asList(paymentRequisitionHeader.getAttachmentOid().split(","));
                    paymentRequisitionHeaderWebDTO.setListAttachmentOid(asList);
                    List<AttachmentCO> attachments = organizationService.listByOids(asList);
                    paymentRequisitionHeaderWebDTO.setAttachments(attachments);
                }
            }
            dtoLists.add(paymentRequisitionHeaderWebDTO);
        }
        return dtoLists;
    }

    /**
     * @Author: bin.xie
     * @Description: 付款申请单头行保存
     * @param: PaymentRequisitionHeaderWebDTO 付款申请单DTO(含头信息及其所有行信息)
     * @return: com.hand.hcf.app.payment.PaymentRequisitionHeaderWebDTO
     * @Date: Created in 2018/4/2 13:17
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRequisitionHeaderWebDTO saveDocumentDetail(PaymentRequisitionHeaderWebDTO paymentRequisitionHeaderWebDTO){
        PaymentRequisitionHeader header = new PaymentRequisitionHeader();
        if (paymentRequisitionHeaderWebDTO.getId() == null){
            BeanUtils.copyProperties(paymentRequisitionHeaderWebDTO,header);
            this.createHeader(header);
            paymentRequisitionHeaderWebDTO.setId(header.getId());
            paymentRequisitionHeaderWebDTO.getPaymentRequisitionLineDTO().stream().forEach(u->
            {
                u.setHeaderId(header.getId());
                u.setId(null);
                PaymentRequisitionLine line = new PaymentRequisitionLine();
                BeanUtils.copyProperties(u,line);
                paymentRequisitionLineService.createLine(line);
            });
        }else{
            BeanUtils.copyProperties(paymentRequisitionHeaderWebDTO,header);
            header.setStatus(paymentRequisitionHeaderWebDTO.getStatus());
            if (paymentRequisitionHeaderWebDTO.getPaymentRequisitionLineDTO() != null) {
                paymentRequisitionHeaderWebDTO.getPaymentRequisitionLineDTO().stream().forEach(u ->
                {
                    u.setHeaderId(header.getId());
                    PaymentRequisitionLine line = new PaymentRequisitionLine();
                    BeanUtils.copyProperties(u, line);
                    if (line.getId() == null) {
                        paymentRequisitionLineService.createLine(line);
                    } else {
                        paymentRequisitionLineService.updateLine(line);
                    }
                });
            }
        }
        updateHeaderAmount(header);
        return paymentRequisitionHeaderWebDTO;
    }

    /**
     * @Author: bin.xie
     * @Description: 根据头ID更新付款申请单头金额
     * @param: headerId
     * @return: void
     * @Date: Created in 2018/4/2 13:18
     *
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateHeaderAmount(PaymentRequisitionHeader header){
        operateCheck(header.getId(),0);

        BigDecimal totalAmountByHeaderId = paymentRequisitionLineService.getTotalAmountByHeaderId(header.getId());
        header.setFunctionAmount(totalAmountByHeaderId);
        this.updateById(header);
    }

    /**
     * @Author: bin.xie
     * @Description: 根据付款申请单行ID删除行信息
     * @param: lineId
     * @return: void
     * @Date: Created in 2018/4/2 13:18
     * @Modified by
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteLineByLineId(Long lineId){
        Long headerId = paymentRequisitionLineService.deleteLineById(lineId);
        updateHeaderAmount(baseMapper.selectById(headerId));
    }


    /**
     * 查看付款申请单是否有行信息
     * @param oid
     * @return
     */
    public Boolean getHasLineByOid(String oid){
        List<PaymentRequisitionHeader> headers = baseMapper.selectList(
                new EntityWrapper<PaymentRequisitionHeader>()
                        .eq("document_oid", oid));
        if(CollectionUtils.isEmpty(headers)){
            return false;
        }
        List<PaymentRequisitionLine> lines = paymentRequisitionLineService.selectList(
                new EntityWrapper<PaymentRequisitionLine>()
                        .eq("header_id", headers.get(0).getId()));
        if(CollectionUtils.isEmpty(lines)){
            return false;
        }
        return true;
    }


    public PaymentRequisitionHeaderWebDTO getHeadByOID(String oid){
        List<PaymentRequisitionHeader> headers = baseMapper.listHeaders(
                new EntityWrapper<PaymentRequisitionHeader>()
                        .eq("t.document_oid", oid));
        if(CollectionUtils.isEmpty(headers)){
            return  null;
        }
        PaymentRequisitionHeaderWebDTO dto = toDTO(headers, false,false).get(0);
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByOid(String oid,Long userId,Integer status,Integer versionNumber,String rejectType,String approvalComment){

        /*ExceptionDetail messageDTO = new ExceptionDetail();
        PaymentRequisitionHeader dataHead = null;
        //根据操作类型及单据OID检验该操作是否可以继续进行
        dataHead = operateCheck(oid, status);


        // 提交
        submitHeader(dataHead, userId);

        if(PaymentDocumentOperationEnum.APPROVAL_REJECT.getId().equals(status)
                ||
                PaymentDocumentOperationEnum.WITHDRAW.getId().equals(status)
                ||
                PaymentDocumentOperationEnum.GENERATE.getId().equals(status)){//驳回，撤回, 回滚
            dataHead.setFormOid("");
            dataHead.setRejectType(rejectType);
            dataHead.setLastRejectType(rejectType);
            dataHead.setRejectReason(approvalComment);

            log.info("删除该付款申请单关联报账单记录");
            cashDataRelationAcpService.deleteByAcp(dataHead.getId());
        }
        if (status.equals(PaymentDocumentOperationEnum.APPROVAL_PASS.getId())){
            // 通过
           passHeader(dataHead, userId);

        }
        dataHead.setLastUpdatedBy(userId);
        dataHead.setStatus(status);
        this.updateById(dataHead);
        messageDTO.setMessage("SUCCESS");
        messageDTO.setErrorCode("0000");
        return messageDTO;*/


        PaymentRequisitionHeader dataHead = null;
        //根据操作类型及单据OID检验该操作是否可以继续进行
        dataHead = operateCheck(oid, status);


        // 提交
        submitHeader(dataHead, userId);

        if(PaymentDocumentOperationEnum.APPROVAL_REJECT.getId().equals(status)
                ||
                PaymentDocumentOperationEnum.WITHDRAW.getId().equals(status)
                ||
                PaymentDocumentOperationEnum.GENERATE.getId().equals(status)){//驳回，撤回, 回滚
            dataHead.setFormOid("");

            log.info("删除该付款申请单关联报账单记录");
            cashDataRelationAcpService.deleteByAcp(dataHead.getId());
        }
        if (status.equals(PaymentDocumentOperationEnum.APPROVAL_PASS.getId())){
            // 通过
            passHeader(dataHead, userId);

        }
        dataHead.setLastUpdatedBy(userId);
        dataHead.setStatus(status);
        this.updateById(dataHead);

        return ;
    }

    private CashTransactionData setCashTransactionData(PaymentRequisitionLine u,
                                                       Long typeId,
                                                       String documentNumber,
                                                       Long employeeId,
                                                       Long companyId,
                                                       Long userId,
                                                       UUID documentOid,
                                                       Integer documentType) {
        CashTransactionData cashTransactionData = new CashTransactionData();
        CashTransactionData queryData = cashTransactionDataService.selectById(u.getCshTransactionId());

        BeanUtils.copyProperties(queryData,cashTransactionData);
        // id
        cashTransactionData.setId(null);
        // 业务大类
        cashTransactionData.setDocumentCategory("ACP_REQUISITION");
        // 单据类型ID
        cashTransactionData.setDocumentTypeId(typeId);
        // 单据头ID
        cashTransactionData.setDocumentHeaderId(u.getHeaderId());
        // 单据行ID
        cashTransactionData.setDocumentLineId(u.getId());
        // 单据编号
        cashTransactionData.setDocumentNumber(documentNumber);
        // 申请人
        cashTransactionData.setEmployeeId(employeeId);
        /*获取用户名称*/
        List<Long> userIds = new ArrayList<>();
        userIds.add(employeeId);
        List<ContactCO> userLists  = organizationService.listByUserIds(userIds);
        // 申请人名称
        cashTransactionData.setEmployeeName(userLists.get(0).getFullName());
        // 单据机构
        cashTransactionData.setCompanyId(companyId);
        // 付款机构ID
        cashTransactionData.setPaymentCompanyId(null);
        // 金额
        cashTransactionData.setAmount(TypeConversionUtils.roundHalfUp(u.getAmount()));
        // 是否冻结
        cashTransactionData.setFrozenFlag(false);
        // 币种
        cashTransactionData.setCurrency(u.getCurrencyCode());
        // 汇率
        cashTransactionData.setExchangeRate(u.getExchangeRate());
        // 描述
        cashTransactionData.setRemark(u.getLineDescription());
        // 计划付款日期
        cashTransactionData.setRequisitionPaymentDate(u.getSchedulePaymentDate());
        // 付款方式类型
        cashTransactionData.setPaymentMethodCategory(u.getPaymentMethodCategory());
        // 租户ID
        cashTransactionData.setTenantId(queryData.getTenantId());
        cashTransactionData.setPartnerId(u.getPartnerId());
        // 收款方代码
        cashTransactionData.setPartnerCode(queryData.getPartnerCode());
        // 收款方名称
        cashTransactionData.setPartnerName(queryData.getPartnerName());
        //创建人
        cashTransactionData.setCreatedBy(userId);
        // 来源头ID
        cashTransactionData.setSourceHeaderId(u.getRefDocumentId());
        // 来源行ID
        cashTransactionData.setSourceLineId(u.getRefDocumentLineId());
        // 来源待付数据ID
        cashTransactionData.setSourceDataId(u.getCshTransactionId());
        cashTransactionData.setEntityOid(documentOid.toString());
        cashTransactionData.setEntityType(documentType);
        return cashTransactionData;

    }

    /**
     * @Author mh.z
     * @Date 2019/02/25
     * @Description 获取未审批/已审批的付款申请单
     *
     * @param finished
     * @param beginDate
     * @param endDate
     * @param userOid
     * @param businessCode
     * @param typeId
     * @param amountFrom
     * @param amountTo
     * @param description
     * @param pageable
     * @return
     */
    public List<PaymentRequisitionHeaderWebDTO> listApprovalPayment(boolean finished, String beginDate, String endDate, String userOid, String businessCode,
                                                                    Long typeId, Double amountFrom, Double amountTo, String description, Pageable pageable) {
        List<PaymentRequisitionHeaderWebDTO> paymentRequisitionHeaderWebDTOList = new ArrayList<PaymentRequisitionHeaderWebDTO>();
        // 当前用户就是审批人
        String approverOidStr = OrgInformationUtil.getCurrentUserOid().toString();
        // 获取未审批/已审批的单据
        List<String> documentOidStrList = workflowInterface.listApprovalDocument(PaymentConstants.ACP_REQUISITION_ENTITY_TYPE,
                approverOidStr, finished, beginDate, endDate);

        // 若没有满足条件的单据则返回个空列表
        if (documentOidStrList.isEmpty()) {
            return paymentRequisitionHeaderWebDTOList;
        }

        PaymentRequisitionQueryCO paymentRequisitionQueryCO = new PaymentRequisitionQueryCO();
        // 单据参数
        paymentRequisitionQueryCO.setDocumentOid(documentOidStrList);
        // 提交人参数
        List<String> userList = new ArrayList<String>();
        if (!StringUtils.isEmpty(userOid)) {
            userList.add(userOid);
        }
        paymentRequisitionQueryCO.setUserList(userList);

        // 编号
        paymentRequisitionQueryCO.setBusinessCode(businessCode);
        // 金额
        BigDecimal amountFromDecimal = amountFrom != null ? BigDecimal.valueOf(amountFrom) : null;
        BigDecimal amountToDecimal = amountTo != null ? BigDecimal.valueOf(amountTo) : null;
        paymentRequisitionQueryCO.setAmountFrom(amountFromDecimal);
        paymentRequisitionQueryCO.setAmountTo(amountToDecimal);
        // 单据类型
        paymentRequisitionQueryCO.setTypeId(typeId);
        // 备注
        paymentRequisitionQueryCO.setDescription(description);

        // 分页参数
        Page page = null;
        if (pageable != null) {
            page = PageUtil.getPage(pageable);
        }
        paymentRequisitionQueryCO.setPage(page);

        paymentRequisitionHeaderWebDTOList = getHeadByInput(paymentRequisitionQueryCO);
        return paymentRequisitionHeaderWebDTOList;
    }

    /**
     * 条件查询付款申请单
     * @param queryCO
     * @return
     */
    public List<PaymentRequisitionHeaderWebDTO> getHeadByInput(PaymentRequisitionQueryCO queryCO){
        List<PaymentRequisitionHeaderWebDTO> headerDTOS = new ArrayList<>();
        List<PaymentRequisitionHeader> headers = new ArrayList<>();
        /*if(CollectionUtils.isEmpty(queryDTO.getUserList())){
            return headerDTOS;
        }*/
        if(CollectionUtils.isNotEmpty(queryCO.getDocumentOid())){
            headers = baseMapper.listHeaders(queryCO.getPage(),
                    new EntityWrapper<PaymentRequisitionHeader>()
                            .in("t.document_oid", queryCO.getDocumentOid())
                            .like(StringUtils.hasText(queryCO.getBusinessCode()), "t.requisition_number", queryCO.getBusinessCode())
                            .ge(queryCO.getAmountFrom() != null, "t.function_amount", queryCO.getAmountFrom())
                            .le(queryCO.getAmountTo() != null, "t.function_amount", queryCO.getAmountTo())
                            .eq(queryCO.getTypeId() != null, "t.acp_req_type_id", queryCO.getTypeId())
                            .in(CollectionUtils.isNotEmpty(queryCO.getUserList()), "t.applicant_oid", queryCO.getUserList())
                            .ge(StringUtils.hasText(queryCO.getDateFrom()), "t.submit_date", queryCO.getDateFrom())
                            .le(StringUtils.hasText(queryCO.getDateTo()), "t.submit_date", queryCO.getDateTo())
                            .like(StringUtils.hasText(queryCO.getDescription()), "t.description", queryCO.getDescription())
                            .orderBy("t.id",false));
            headerDTOS = toDTO(headers,false, false);
            if (headerDTOS.size() > 0) {
                //分页信息放到列表第一项
                headerDTOS.get(0).setPage(queryCO.getPage());
            }
        }
        return headerDTOS;

    }

    /**
     * @Author: bin.xie
     * @Description:  付款申请单状态校验
     * @param: 单据Oid
     * @param: operateType 操作状态 -1---删除 0--修改 1002--提交 1004--审核 1005--审批驳回 6002--取消 6003--完成
     * @return: ContractHeader
     * @Date: Created in 2018/1/24 10:59
     * @Modified by
     */
    public PaymentRequisitionHeader operateCheck(String oid, Integer operateType) {
        List<PaymentRequisitionHeader> headers = baseMapper.selectList(
                new EntityWrapper<PaymentRequisitionHeader>()
                        .eq("document_oid", oid));
        PaymentRequisitionHeader paymentRequisitionHeader = headers.get(0);
        if (paymentRequisitionHeader == null) {
            throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_NOT_EXISTS);
        }

        Integer status = paymentRequisitionHeader.getStatus();
        check(operateType,status);

        return paymentRequisitionHeader;
    }

    public void check(Integer operateType,Integer status){
        switch (operateType){
            //点击删除
            case -1:
                if (!status.equals(PaymentDocumentOperationEnum.GENERATE.getId()) && !status.equals(PaymentDocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(PaymentDocumentOperationEnum.CANCEL.getId()) && !status.equals(PaymentDocumentOperationEnum.WITHDRAW.getId())) {
                    throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_OPERATE,new String[]{"新建、审批驳回、已取消、已撤回","删除"});
                }
                break;
            //更改
            case 0:
                if (!status.equals(PaymentDocumentOperationEnum.GENERATE.getId()) && !status.equals(PaymentDocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(PaymentDocumentOperationEnum.CANCEL.getId()) && !status.equals(PaymentDocumentOperationEnum.WITHDRAW.getId())) {
                    throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_OPERATE,new String[]{"新建、审批驳回、已取消、已撤回","更改"});
                }
                break;
            // 提交 至审核中
            case 1002:
                if (!status.equals(PaymentDocumentOperationEnum.GENERATE.getId()) && !status.equals(PaymentDocumentOperationEnum.APPROVAL_REJECT.getId())
                        && !status.equals(PaymentDocumentOperationEnum.CANCEL.getId()) && !status.equals(PaymentDocumentOperationEnum.WITHDRAW.getId())
                        && !status.equals(PaymentDocumentOperationEnum.HOLD.getId())) {
                    throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_OPERATE,new String[]{"新建、审批驳回、已取消、已撤回、暂挂","提交"});
                }
                break;
            // 审核
            case 1004:
                if (!status.equals(PaymentDocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_OPERATE,new String[]{"审批中","审批"});
                }
                break;
            // 审批驳回
            case 1005:
                if (!status.equals(PaymentDocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_OPERATE,new String[]{"审批中","审批驳回"});
                }
                break;
            // 取消
            case 6002:
                if (!status.equals(PaymentDocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_OPERATE,new String[]{"审批中","取消"});
                }
                break;
            // 完成
            case 6003:
                if (!status.equals(PaymentDocumentOperationEnum.APPROVAL_PASS.getId())) {
                    throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_OPERATE,new String[]{"审批中","完成"});
                }
                break;
            // 暂挂
            case 6001:
                if (!status.equals(PaymentDocumentOperationEnum.APPROVAL.getId())) {
                    throw new BizException(RespCode.PAYMENT_ACP_ACP_REQUISITION_OPERATE,new String[]{"审批中","暂挂"});
                }
                break;
            default:
                break;
        }
    }

    private void submitHeader(PaymentRequisitionHeader dataHead, Long userId){

        log.info("付款申请单提交!");

        //将行信息关联的报账单传输给报账单模块用于关联
        List<PaymentRequisitionLine> lineList = paymentRequisitionLineService.selectList(
                new EntityWrapper<PaymentRequisitionLine>()
                        .eq("header_id",dataHead.getId()));
        //封装传输数据
        List<CashDataRelationAcp> paymentRequisitionInfoDTOS = new ArrayList<CashDataRelationAcp>();
        lineList.stream().forEach( u ->{
            CashDataRelationAcp paymentRequisitionInfoDTO = new CashDataRelationAcp();
            //金额
            paymentRequisitionInfoDTO.setAmount(u.getAmount());
            //创建人为当前登陆人
            paymentRequisitionInfoDTO.setCreatedBy(userId);
            //单据头ID
            paymentRequisitionInfoDTO.setDocumentHeadId(u.getHeaderId());
            //单据行ID
            paymentRequisitionInfoDTO.setDocumentLineId(u.getId());
            //报账单头ID
            paymentRequisitionInfoDTO.setReportHeadId(u.getRefDocumentId());
            //报账单行ID
            paymentRequisitionInfoDTO.setReportLineId(u.getRefDocumentLineId());
            paymentRequisitionInfoDTOS.add(paymentRequisitionInfoDTO);
        });
        log.info("校验付款申请单关联金额");
        cashDataRelationAcpService.createData(paymentRequisitionInfoDTOS);
        // 查询单据类型ID
        PaymentRequisitionTypes types = paymentRequisitionTypesMapper.selectById(dataHead.getAcpReqTypeId());
        dataHead.setFormOid(types.getFormOid());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dataHead.setSubmitDate(sdf.format(new Date()));
        // 提交时申请日期更改为当前日期
        dataHead.setRequisitionDate(ZonedDateTime.now());
    }


    private void passHeader(PaymentRequisitionHeader dataHead, Long userId){
        log.info("付款申请单审批通过，生成待付款数据");
        //将行信息生成代付数据
        List<PaymentRequisitionLine> lineList = paymentRequisitionLineService.selectList(
                new EntityWrapper<PaymentRequisitionLine>()
                        .eq("header_id",dataHead.getId()));
        for (PaymentRequisitionLine u : lineList){

            CashTransactionData cashTransactionData = setCashTransactionData(u
                    ,dataHead.getAcpReqTypeId()
                    ,dataHead.getRequisitionNumber()
                    ,dataHead.getEmployeeId()
                    ,dataHead.getCompanyId()
                    ,userId
                    ,UUID.fromString(dataHead.getDocumentOid())
                    ,TypeConversionUtils.parseInt(dataHead.getDocumentType())
            );
            cashTransactionDataService.createTransactionData(cashTransactionData);
        }
    }

//    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public Boolean submit(WorkFlowDocumentRefCO workFlowDocumentRef) {
        //查询付款单
        PaymentRequisitionHeader head = this.selectById(workFlowDocumentRef.getDocumentId());
        //校验状态
        if (!PaymentDocumentOperationEnum.GENERATE.getId().equals(head.getStatus()) && !  PaymentDocumentOperationEnum.WITHDRAW.getId().equals(head.getStatus()) && !PaymentDocumentOperationEnum.APPROVAL_REJECT.getId().equals(head.getStatus())) {
            //只有新增，拒绝，撤回的单据可以提交
            throw new BizException("只有新建或撤回或拒绝的单据允许提交！");
        }
        //校验：单行是空不能提交
//        Boolean headHasLine = this.getHasLineByOid(workFlowDocumentRef.getDocumentOid().toString());

        //将行信息关联的报账单传输给报账单模块用于关联
        List<PaymentRequisitionLine> lines = paymentRequisitionLineService.selectList(
                new EntityWrapper<PaymentRequisitionLine>()
                        .eq("header_id",head.getId()));
        if(CollectionUtils.isEmpty(lines)){
            throw new BizException("error","没有行信息，不可提交");
        }
        //封装传输数据
        List<CashDataRelationAcp> paymentRequisitionInfoDTOS = new ArrayList<CashDataRelationAcp>();
        lines.forEach( u ->{
            CashDataRelationAcp paymentRequisitionInfoDTO = new CashDataRelationAcp();
            //金额
            paymentRequisitionInfoDTO.setAmount(u.getAmount());
            //创建人为当前登陆人
            paymentRequisitionInfoDTO.setCreatedBy(OrgInformationUtil.getCurrentUserId());
            //单据头ID
            paymentRequisitionInfoDTO.setDocumentHeadId(u.getHeaderId());
            //单据行ID
            paymentRequisitionInfoDTO.setDocumentLineId(u.getId());
            //报账单头ID
            paymentRequisitionInfoDTO.setReportHeadId(u.getRefDocumentId());
            //报账单行ID
            paymentRequisitionInfoDTO.setReportLineId(u.getRefDocumentLineId());
            paymentRequisitionInfoDTOS.add(paymentRequisitionInfoDTO);
        });
        log.info("校验付款申请单关联金额");
        cashDataRelationAcpService.createData(paymentRequisitionInfoDTOS);

        List<UUID> countersignApproverOIDs =new ArrayList<>();
        if(CollectionUtils.isNotEmpty(workFlowDocumentRef.getCountersignApproverOIDs())){
            workFlowDocumentRef.getCountersignApproverOIDs().forEach(oid->{
                countersignApproverOIDs.add(oid);
            });
        }
        workFlowDocumentRef.setRemark(head.getDescription());
        workFlowDocumentRef.setAmount(head.getFunctionAmount());
        workFlowDocumentRef.setFunctionAmount(head.getFunctionAmount());
        workFlowDocumentRef.setCurrencyCode(lines.get(0).getCurrencyCode());
        // 查询单据类型ID
        PaymentRequisitionTypes types = paymentRequisitionTypesMapper.selectById(head.getAcpReqTypeId());
//        将formOID更新
        head.setFormOid(types.getFormOid());
        // 设置单据类型的名称和代码
        workFlowDocumentRef.setDocumentTypeName(types.getDescription());
        workFlowDocumentRef.setDocumentTypeCode(types.getAcpReqTypeCode());
        Long startw = System.currentTimeMillis();
        //单据验证通过，调用工作流进行提交
        workFlowDocumentRef.setDestinationService(applicationName);
        workFlowDocumentRef.setStatus(PaymentDocumentOperationEnum.APPROVAL.getId());//审批中
        workFlowDocumentRef.setEventId("");
        workFlowDocumentRef.setEventConfirmStatus(false);
        workFlowDocumentRef.setRejectType("");
        workFlowDocumentRef.setRejectReason("");
        workFlowDocumentRef.setLastRejectType("");
        workFlowDocumentRef.setSubmittedBy(OrgInformationUtil.getCurrentUserOid());
        //调用工作流的三方接口进行提交
        ApprovalDocumentCO submitData = new ApprovalDocumentCO();
        submitData.setDocumentId(workFlowDocumentRef.getDocumentId()); // 单据id
        submitData.setDocumentOid(workFlowDocumentRef.getDocumentOid()); // 单据oid
        submitData.setDocumentNumber(workFlowDocumentRef.getDocumentNumber()); // 单据编号
        submitData.setDocumentName(workFlowDocumentRef.getDocumentName()); // 单据名称
        submitData.setDocumentCategory(workFlowDocumentRef.getDocumentCategory()); // 单据类别
        submitData.setDocumentTypeId(workFlowDocumentRef.getDocumentTypeId()); // 单据类型id
        submitData.setDocumentTypeCode(workFlowDocumentRef.getDocumentTypeCode()); // 单据类型代码
        submitData.setDocumentTypeName(workFlowDocumentRef.getDocumentTypeName()); // 单据类型名称
        submitData.setCurrencyCode(workFlowDocumentRef.getCurrencyCode()); // 币种
        submitData.setAmount(workFlowDocumentRef.getAmount()); // 原币金额
        submitData.setFunctionAmount(workFlowDocumentRef.getFunctionAmount()); // 本币金额
        submitData.setCompanyId(workFlowDocumentRef.getCompanyId()); // 公司id
        submitData.setUnitOid(workFlowDocumentRef.getUnitOid()); // 部门oid
        submitData.setApplicantOid(workFlowDocumentRef.getApplicantOid()); // 申请人oid
        submitData.setApplicantDate(workFlowDocumentRef.getApplicantDate()); // 申请日期
        submitData.setRemark(workFlowDocumentRef.getRemark()); // 备注
        submitData.setSubmittedBy(workFlowDocumentRef.getSubmittedBy()); // 提交人
        submitData.setFormOid(UUID.fromString(types.getFormOid())); // 表单oid
        submitData.setDestinationService(workFlowDocumentRef.getDestinationService()); // 注册到Eureka中的名称
        ApprovalResultCO submitResult = workflowClient.submitWorkflow(submitData);

        if (Boolean.TRUE.equals(submitResult.getSuccess())){
            Integer approvalStatus = submitResult.getStatus();

            if (PaymentDocumentOperationEnum.APPROVAL.getId().equals(approvalStatus)) {
                //提交成功，则处理单据的状态  1002 审批中  1001 编辑中
                head.setRequisitionDate(ZonedDateTime.now());
                head.setStatus(PaymentDocumentOperationEnum.APPROVAL.getId());// 修改为审批中
                this.updateById(head);
            }
        } else {
            throw new BizException(submitResult.getError());
        }
        log.info("预付款行整体提交,耗时:{}ms", System.currentTimeMillis() - startw);
        return  true;
    }

    @Transactional
    public void updateDocumentStatus(WorkflowMessageCO workflowMessage) {
        int status = workflowMessage.getStatus();
        //获取付款申请单
        PaymentRequisitionHeader head = this.selectById(workflowMessage.getDocumentId());
        // 查询单据类型ID
        PaymentRequisitionTypes requisitionType = paymentRequisitionTypesMapper.selectById(head.getAcpReqTypeId());
//        将formOID更新
        if (head == null || requisitionType == null) {
            throw new BizException(RespCode.SYS_DATA_NOT_EXISTS);
        }
        if (status == PaymentDocumentOperationEnum.APPROVAL_PASS.getId()) {
            //只有提交状态的单据才可以审批通过和拒绝
            if (!head.getStatus().equals(PaymentDocumentOperationEnum.APPROVAL.getId())) {
                throw new BizException("只有提交的单据才可以被通过或拒绝！");
            }
            if (status == PaymentDocumentOperationEnum.APPROVAL_PASS.getId()) {
                // 通过
                passHeader(head, workflowMessage.getUserId());
            }
        }else if (status == PaymentDocumentOperationEnum.WITHDRAW.getId() || status == PaymentDocumentOperationEnum.APPROVAL_REJECT.getId()) {
            head.setFormOid("");

            log.info("删除该付款申请单关联报账单记录");
            cashDataRelationAcpService.deleteByAcp(head.getId());
        }
        head.setLastUpdatedBy(workflowMessage.getUserId());
        head.setStatus(status);
        this.updateById(head);
    }


    /**
     * @author mh.z
     * @date 2019/02/25
     * @description 根据oid获取表单
     *
     * @param formOid
     * @return
     */
    public ApprovalFormCO getApprovalFormByOid(UUID formOid) {
        if (formOid == null) {
            throw new IllegalArgumentException("formOid null");
        }

        String formOidStr = formOid.toString();
        ApprovalFormCO approvalFormCO = workflowClient.getApprovalFormByOid(formOidStr);
        return approvalFormCO;
    }

    /**
     * @author mh.z
     * @date 2019/02/25
     * @description 根据oid获取用户
     *
     * @param userOid
     * @return
     */
    public ContactCO getUserByOid(UUID userOid) {
        if (userOid == null) {
            throw new IllegalArgumentException("userOid null");
        }

        /*String userOidStr = userOid.toString();*/
        ContactCO userCO = userClient.getByUserOid(userOid);
        return userCO;
    }


    public List<ContactCO> listUsersByCreatedPaymentRequisitionHeaders() {
        List<Long> userList = baseMapper.selectList(
                new EntityWrapper<PaymentRequisitionHeader>()
                        .eq("created_by", OrgInformationUtil.getCurrentUserId())
        ).stream().map(PaymentRequisitionHeader::getEmployeeId).distinct().filter(e -> e != null).collect(Collectors.toList());
        if (userList.size() == 0) {
            return new ArrayList<>();
        }
        return organizationService.listByUserIds(userList);
    }


    /**
     * getHeadersByCond : 根据付款申请单头表ID查询对应行表关联的报账单行表ID集合
     */
    public List<Long> getReportLineIds(Long headId){
        return baseMapper.getReportLineIds(headId);
    }


    /**
     * 付款申请单财务查询
     */
    @Transactional(readOnly = true)
    public List<PaymentRequisitionHeaderWebDTO> getHeaderByCond(String requisitionNumber,
                                                                Long companyId,
                                                                Long acpReqTypeId,
                                                                Long employeeId,
                                                                String status,
                                                                Long unitId,
                                                                String requisitionDateFrom,
                                                                String requisitionDateTo,
                                                                BigDecimal payAmountFrom,
                                                                BigDecimal payAmountTo,
                                                                BigDecimal functionAmountFrom,
                                                                BigDecimal functionAmountTo,
                                                                String description,
                                                                boolean dataAuthFlag,
                                                                Page mybatisPage){
        ZonedDateTime requisitionZonedDateFrom = DateUtil.stringToZonedDateTime(requisitionDateFrom);
        ZonedDateTime requisitionZonedDateTo = DateUtil.stringToZonedDateTime(requisitionDateTo);
        if (requisitionZonedDateTo != null){
            requisitionZonedDateTo = requisitionZonedDateTo.plusDays(1);
        }
        String dataAuthLabel = null;
        if(dataAuthFlag){
            Map<String,String> map = new HashMap<>();
            map.put(DataAuthorityUtil.TABLE_NAME,"csh_acp_requisition_hds");
            map.put(DataAuthorityUtil.SOB_COLUMN,"set_of_books_id");
            map.put(DataAuthorityUtil.COMPANY_COLUMN,"company_id");
            map.put(DataAuthorityUtil.UNIT_COLUMN,"unit_id");
            map.put(DataAuthorityUtil.EMPLOYEE_COLUMN,"employee_id");
            dataAuthLabel = DataAuthorityUtil.getDataAuthLabel(map);
        }
        List<PaymentRequisitionHeader> lists = baseMapper.queryHeaders(mybatisPage,new EntityWrapper<PaymentRequisitionHeader>()
                .like(requisitionNumber != null, "t.requisition_number",requisitionNumber)
                .eq(companyId != null,"t.company_id",companyId)
                .eq(acpReqTypeId != null,"t.acp_req_type_id",acpReqTypeId)
                .eq(employeeId != null,"t.employee_id",employeeId)
                .eq(status != null,"t.status",status)
                .eq(unitId !=null,"t.unit_id",unitId)
                .ge(requisitionZonedDateFrom != null,"t.requisition_date",requisitionZonedDateFrom)
                .lt(requisitionZonedDateTo != null,"t.requisition_date",requisitionZonedDateTo)
                .ge(functionAmountFrom != null,"t.payAmount",payAmountFrom)
                .le(functionAmountTo != null,"t.payAmount",payAmountTo)
                .ge(functionAmountFrom != null,"t.function_amount",functionAmountFrom)
                .le(functionAmountTo != null,"t.function_amount",functionAmountTo)
                .like(description != null,"t.description",description)
                .and(TypeConversionUtils.isNotEmpty(dataAuthLabel),dataAuthLabel)
                .orderBy("t.id",false));
        return toDTO(lists,false, false);
    }
}

