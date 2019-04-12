package com.hand.hcf.app.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.dto.ApprovalChainDTO;
import com.hand.hcf.app.workflow.dto.ApprovalHistoryDTO;
import com.hand.hcf.app.workflow.dto.CheckAuditNoticeDTO;
import com.hand.hcf.app.workflow.dto.PDFApprovalHistory;
import com.hand.hcf.app.workflow.dto.UserApprovalDTO;
import com.hand.hcf.app.workflow.dto.WebApprovalHistoryDTO;
import com.hand.hcf.app.workflow.domain.ApprovalHistory;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationTypeEnum;
import com.hand.hcf.app.workflow.enums.CounterSignTypeEnum;
import com.hand.hcf.app.workflow.externalApi.BaseClient;
import com.hand.hcf.app.workflow.persistence.ApprovalHistoryMapper;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.MessageService;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hcf.app.workflow.constant.RuleConstants.APPROVER_TYPE_ROBOT_NAME;

@Service
@Transactional
public class ApprovalHistoryService extends BaseService<ApprovalHistoryMapper, ApprovalHistory> {

    @Autowired
    private BaseClient baseClient;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private MessageService messageService;

    @Autowired
    MapperFacade mapper;

    private static final Map<Integer, String> operationMsgMap = new HashMap<Integer, String>() {{
        put(ApprovalOperationEnum.SUBMIT_FOR_APPROVAL.getId(), "pdf.approval.operation.enum.submited");
        put(ApprovalOperationEnum.WITHDRAW.getId(), "pdf.approval.operation.enum.withdrawn");
        put(ApprovalOperationEnum.REPAYMENT_F_SUBMIT.getId(), "pdf.approval.operation.enum.submitted.repayment");
        put(ApprovalOperationEnum.STAFF_REPLY.getId(), "pdf.approval.operation.enum.bill.comment.self");
        put(ApprovalOperationEnum.ADD_COUNTERSIGN.getId(), "pdf.approval.operation.enum.sign");
        put(ApprovalOperationEnum.APPROVAL_PASS.getId(), "pdf.approval.operation.enum.approval");
        put(ApprovalOperationEnum.APPROVAL_REJECT.getId(), "pdf.approval.operation.enum.rejected");
        put(ApprovalOperationEnum.APPROVAL_INVOICE_REJECT.getId(), "pdf.approval.operation.enum.invoice.rejected");
        put(ApprovalOperationEnum.AUDIT_PASS.getId(), "pdf.approval.operation.enum.reviewed");
        put(ApprovalOperationEnum.AUDIT_REJECT.getId(), "pdf.approval.operation.enum.review.failed");
        put(ApprovalOperationEnum.AUDIT_RECEIVE.getId(), "pdf.approval.operation.enum.receive");
        put(ApprovalOperationEnum.AUDIT_NOTICE.getId(), "pdf.approval.operation.enum.notice");
        put(ApprovalOperationEnum.FINANCE_LOANED.getId(), "pdf.approval.operation.enum.paid");
        put(ApprovalOperationEnum.PAYMENT_IN_PROCESS.getId(), "pdf.approval.operation.enum.paying");
        put(ApprovalOperationEnum.REVIEWED_AMOUNT.getId(), "review.amount.history.title");
        put(ApprovalOperationEnum.REVIEWED_RATE.getId(), "review.rate.history.title");
        put(ApprovalOperationEnum.REVIEWED_AMOUNT_RATE.getId(), "review.amount.rate.history.title");
        put(ApprovalOperationEnum.AUDIT_SEND.getId(), "pdf.approval.operation.enum.send");
        put(ApprovalOperationEnum.AUDIT_BACK.getId(), "pdf.approval.operation.enum.back");
        put(ApprovalOperationEnum.RECEIPT_PASS.getId(), "pdf.approval.operation.enum.invoiced");
        put(ApprovalOperationEnum.RECEIPT_REJECT.getId(), "pdf.approval.operation.enum.invoice.failed");
        put(ApprovalOperationEnum.FIN_UPLOAD_EXPENSE_ATTACHMENT.getId(), "pdf.approval.operation.enum.finance.upload.expense.attachment");
        put(ApprovalOperationEnum.FIN_DELETE_EXPENSE_ATTACHMENT.getId(), "pdf.approval.operation.enum.finance.delete.expense.attachment");
    }};

    private static final Map<Integer, String> roleMsgMap = new HashMap<Integer, String>() {{
        put(ApprovalOperationTypeEnum.SELF.getId(), "申请人");
        put(ApprovalOperationTypeEnum.APPROVAL.getId(), "审批人");
        put(ApprovalOperationTypeEnum.AUDIT.getId(), "财务");
        put(ApprovalOperationTypeEnum.RECEIPT.getId(), "开票");
        put(ApprovalOperationTypeEnum.SYSTEM.getId(), "系统");
    }};

    /**
     * 保存审批历史
     *
     * @param approvalHistory
     */
    public void save(ApprovalHistory approvalHistory) {
        insertOrUpdate(approvalHistory);
    }

    /**
     * 保存审批历史
     *
     * @param approvalHistoryDTOs
     */
    public void save(List<ApprovalHistoryDTO> approvalHistoryDTOs) {
        if (CollectionUtils.isNotEmpty(approvalHistoryDTOs)) {
            List<ApprovalHistory> approvalHistorys = approvalHistoryDTOs.stream().map(a -> approvalHistoryDTOToApprovalHistory(a)).collect(Collectors.toList());
            saveApprovalHistoryList(approvalHistorys);
        }
    }

    /**
     * 保存审批历史
     *
     * @param approvalHistorys
     */
    public void saveApprovalHistoryList(List<ApprovalHistory> approvalHistorys) {
        if (CollectionUtils.isNotEmpty(approvalHistorys)) {
            approvalHistorys.stream().forEach(a -> save(a));
        }
    }

    /**
     * 查询审批历史
     *
     * @param entityType 1001 申请类型审批 1002 报销单类型审批
     * @param entityOid  申请或者报销单的oid
     * @return
     */
    public List<ApprovalHistoryDTO> listApprovalHistory(Integer entityType, UUID entityOid) {
        List<ApprovalHistory> approvalHistoryList = listByEntityTypeAndEntityOidOrderByIdDesc(entityType, entityOid);
        List<ApprovalHistoryDTO> approvalHistoryDTOList = approvalHistoryListToApprovalHistoryDTOList(approvalHistoryList);
        approvalHistoryDTOList.stream().map(p -> {
            if (p.getOperation().equals(ApprovalOperationEnum.APPROVAL_REJECT.getId()) || p.getOperation().equals(ApprovalOperationEnum.APPROVAL_PASS.getId())) {
                ApprovalChainDTO approvalChainDTO = approvalChainService.getApprovalChainByRefId(p.getRefApprovalChainId());
                if (approvalChainDTO != null) {
                    p.setChainApproverOid(approvalChainDTO.getApproverOid());
                }
            }
            return p;
        }).collect(Collectors.toList());

        //人员信息查询
        //组装审批人信息
        Set<UUID> userOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getOperatorOid() != null;
        }).map(ApprovalHistoryDTO::getOperatorOid).collect(Collectors.toSet());

        //有代提时，取当前申请人
        Set<UUID> currentApplicantOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getCurrentApplicantOid() != null;
        }).map(ApprovalHistoryDTO::getCurrentApplicantOid).collect(Collectors.toSet());
        userOids.addAll(currentApplicantOids);

        //组装关联的审批链的申请人信息
        Set<UUID> chainApproversOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getChainApproverOid() != null;
        }).map(ApprovalHistoryDTO::getChainApproverOid).collect(Collectors.toSet());
        userOids.addAll(chainApproversOids);

        List<UserApprovalDTO> userSearchViewDTOs = baseClient.listByUserOids(new ArrayList(userOids));
        Map<UUID, UserApprovalDTO> userSearchViewDTOMap = userSearchViewDTOs.stream().collect(Collectors.toMap(UserApprovalDTO::getUserOid, (p) -> p));
        for (ApprovalHistoryDTO approvalHistoryDTO : approvalHistoryDTOList) {
            //用于防止Json重复引用
            UserApprovalDTO operator = new UserApprovalDTO();
            if (userSearchViewDTOMap != null) {
                //审批人
                UserApprovalDTO sourceOperator = userSearchViewDTOMap.get(approvalHistoryDTO.getOperatorOid());
                if (sourceOperator != null) {
                    mapper.map(sourceOperator, operator);
                }
                if (RuleConstants.APPROVER_TYPE_ROBOT_OID.equals(String.valueOf(approvalHistoryDTO.getOperatorOid()))) {
                    if (Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
                        operator.setFullName(APPROVER_TYPE_ROBOT_NAME);
                    } else {
                        operator.setFullName(RuleConstants.APPROVER_TYPE_ROBOT_NAME_ENGLISH);
                    }
                    operator.setEmployeeCode("");
                }
                approvalHistoryDTO.setOperator(operator);
                //当前申请人
                approvalHistoryDTO.setCurrentApplicant(userSearchViewDTOMap.get(approvalHistoryDTO.getCurrentApplicantOid()));
                //审批链的申请人
                approvalHistoryDTO.setChainApprover(userSearchViewDTOMap.get(approvalHistoryDTO.getChainApproverOid()));
            }
            String operationDescription = messageService.getMessageDetailByCode(ApprovalOperationEnum.getMessageKeyByID(approvalHistoryDTO.getOperation()));
            approvalHistoryDTO.setOperationDescription(operationDescription);
        }

        return approvalHistoryDTOList;
    }

    public List<ApprovalHistory> listByEntityOidAndOperationNotInOrderByIdDesc(UUID entityOid, UUID operatorOid, List<Integer> operations) {
        return selectList(new EntityWrapper<ApprovalHistory>()
                .eq("entity_oid", entityOid)
                .notIn("operation", operations)
                .orderBy("id", false));
    }

    /**
     * 查询审批历史(用户不相关)
     *
     * @param entityType 1001 申请类型审批 1002 报销单类型审批
     * @param entityOid  申请或者报销单的oid
     * @return
     */
    public List<ApprovalHistoryDTO> listApprovalHistoryContainParticipantOperation(Integer entityType, UUID entityOid, UUID operatorOid) {
        List<Integer> operations = new ArrayList<>();
        operations.add(ApprovalOperationEnum.PARTICIPANT_CLOSE.getId());
        operations.add(ApprovalOperationEnum.PARTICIPANT_RESTART.getId());
        List<ApprovalHistory> approvalHistoryList = listByEntityOidAndOperationNotInOrderByIdDesc(entityOid, operatorOid, operations);
        List<ApprovalHistoryDTO> approvalHistoryDTOList = approvalHistoryListToApprovalHistoryDTOList(approvalHistoryList);
        approvalHistoryDTOList.stream().map(p -> {
            if (p.getOperation().equals(ApprovalOperationEnum.APPROVAL_REJECT.getId()) || p.getOperation().equals(ApprovalOperationEnum.APPROVAL_PASS.getId())) {
                ApprovalChainDTO approvalChainDTO = approvalChainService.getApprovalChainByRefId(p.getRefApprovalChainId());
                if (approvalChainDTO != null) {
                    p.setChainApproverOid(approvalChainDTO.getApproverOid());
                }
            }
            return p;
        }).collect(Collectors.toList());

        //人员查询
        //组装审批人信息
        Set<UUID> userOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getOperatorOid() != null;
        }).map(ApprovalHistoryDTO::getOperatorOid).collect(Collectors.toSet());

        //有代提时，取当前申请人
        Set<UUID> currentApplicantOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getCurrentApplicantOid() != null;
        }).map(ApprovalHistoryDTO::getCurrentApplicantOid).collect(Collectors.toSet());
        userOids.addAll(currentApplicantOids);

        //组装关联的审批链的申请人信息
        Set<UUID> chainApproversOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getChainApproverOid() != null;
        }).map(ApprovalHistoryDTO::getChainApproverOid).collect(Collectors.toSet());
        userOids.addAll(chainApproversOids);

        List<UserApprovalDTO> userSearchViewDTOs = baseClient.listByUserOids(new ArrayList<>(userOids));
        Map<UUID, UserApprovalDTO> userSearchViewDTOMap = userSearchViewDTOs.stream().collect(Collectors.toMap(UserApprovalDTO::getUserOid, (p) -> p));
        for (ApprovalHistoryDTO approvalHistoryDTO : approvalHistoryDTOList) {
            //用于防止Json重复引用
            UserApprovalDTO operator = new UserApprovalDTO();
            if (userSearchViewDTOMap != null) {
                //审批人
                UserApprovalDTO sourceOperator = userSearchViewDTOMap.get(approvalHistoryDTO.getOperatorOid());
                if (sourceOperator != null) {
                    mapper.map(sourceOperator, operator);
                    if (RuleConstants.APPROVER_TYPE_ROBOT_OID.equals(String.valueOf(operator.getUserOid()))) {
                        if (!Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
                            operator.setFullName(RuleConstants.APPROVER_TYPE_ROBOT_NAME_ENGLISH);
                            operator.setEmployeeCode("");
                        }
                    }
                    approvalHistoryDTO.setOperator(operator);
                }
                //当前申请人
                approvalHistoryDTO.setCurrentApplicant(userSearchViewDTOMap.get(approvalHistoryDTO.getCurrentApplicantOid()));
                //审批链的申请人
                approvalHistoryDTO.setChainApprover(userSearchViewDTOMap.get(approvalHistoryDTO.getChainApproverOid()));
            }
            String operationDescription = messageService.getMessageDetailByCode(ApprovalOperationEnum.getMessageKeyByID(approvalHistoryDTO.getOperation()));
            approvalHistoryDTO.setOperationDescription(operationDescription);
        }

        //根据创建时间排序 时间相同根据ID排序
        Collections.sort(approvalHistoryDTOList, new Comparator<ApprovalHistoryDTO>() {
            @Override
            public int compare(ApprovalHistoryDTO o1, ApprovalHistoryDTO o2) {
                if (o1.getCreatedDate().toInstant().toEpochMilli() > o2.getCreatedDate().toInstant().toEpochMilli()) {
                    return 1;
                } else if (o1.getCreatedDate().toInstant().toEpochMilli() < o2.getCreatedDate().toInstant().toEpochMilli()) {
                    return -1;
                } else if (o1.getCreatedDate().toInstant().toEpochMilli() == o2.getCreatedDate().toInstant().toEpochMilli()) {
                    if (o1.getId() > o2.getId()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
                return 0;
            }
        });
        Collections.reverse(approvalHistoryDTOList);
        return approvalHistoryDTOList;
    }


    public List<ApprovalHistory> listByEntityTypeAndEntityOidInOrderByCreatedDateDescIdDesc(Integer entityType, List<UUID> entityOids) {
        return selectList(new EntityWrapper<ApprovalHistory>()
                .in("entity_oid", entityOids)
                .notIn("entity_type", entityType)
                .orderBy("created_date", false)
                .orderBy("id", false));
    }


    public List<ApprovalHistoryDTO> listApprovalHistories(Integer entityType, List<UUID> entityOids) {
        List<ApprovalHistory> approvalHistoryList = listByEntityTypeAndEntityOidInOrderByCreatedDateDescIdDesc(entityType, entityOids);
        List<ApprovalHistoryDTO> approvalHistoryDTOList = approvalHistoryListToApprovalHistoryDTOList(approvalHistoryList);
        approvalHistoryDTOList = approvalHistoryDTOList.stream().map(p -> {
            if (p.getOperation().equals(ApprovalOperationEnum.APPROVAL_REJECT.getId()) || p.getOperation().equals(ApprovalOperationEnum.APPROVAL_PASS.getId())) {
                ApprovalChainDTO approvalChainDTO = approvalChainService.getApprovalChainByRefId(p.getRefApprovalChainId());
                if (approvalChainDTO != null) {
                    p.setChainApproverOid(approvalChainDTO.getApproverOid());
                }
            }
            return p;
        }).collect(Collectors.toList());

        //组装操作人信息
        Set<UUID> userOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getOperatorOid() != null;
        }).map(ApprovalHistoryDTO::getOperatorOid).collect(Collectors.toSet());
        //组装提交时申请人信息
        Set<UUID> currentApplicantOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getCurrentApplicantOid() != null;
        }).map(ApprovalHistoryDTO::getCurrentApplicantOid).collect(Collectors.toSet());
        userOids.addAll(currentApplicantOids);
        //组装关联的审批链的申请人信息
        Set<UUID> chainApproversOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getChainApproverOid() != null;
        }).map(ApprovalHistoryDTO::getChainApproverOid).collect(Collectors.toSet());
        userOids.addAll(chainApproversOids);

        //统一查询人员
        List<UserApprovalDTO> userSearchViewDTOs = baseClient.listByUserOids(new ArrayList<>(userOids));

        Map<UUID, UserApprovalDTO> userSearchViewDTOMap = userSearchViewDTOs.stream().collect(Collectors.toMap(UserApprovalDTO::getUserOid, (p) -> p));

        for (ApprovalHistoryDTO approvalHistoryDTO : approvalHistoryDTOList) {
            //用于防止Json重复引用
            UserApprovalDTO operator = new UserApprovalDTO();

            if (userSearchViewDTOMap != null) {
                UserApprovalDTO sourceOperator = userSearchViewDTOMap.get(approvalHistoryDTO.getOperatorOid());
                if (sourceOperator != null) {
                    mapper.map(sourceOperator, operator);
                    if (RuleConstants.APPROVER_TYPE_ROBOT_OID.equals(String.valueOf(operator.getUserOid()))) {
                        if (!Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
                            operator.setFullName(RuleConstants.APPROVER_TYPE_ROBOT_NAME_ENGLISH);
                            operator.setEmployeeCode("");
                        }
                    }
                    approvalHistoryDTO.setOperator(operator);
                }
                approvalHistoryDTO.setCurrentApplicant(userSearchViewDTOMap.get(approvalHistoryDTO.getCurrentApplicantOid()));
                approvalHistoryDTO.setChainApprover(userSearchViewDTOMap.get(approvalHistoryDTO.getChainApproverOid()));
            }
            String operationDescription = messageService.getMessageDetailByCode(ApprovalOperationEnum.getMessageKeyByID(approvalHistoryDTO.getOperation()));
            approvalHistoryDTO.setOperationDescription(operationDescription);
        }

        return approvalHistoryDTOList;
    }

    /**
     * 查询单据审批历史
     *
     * @param entityOid
     * @return
     */
    public List<ApprovalHistory> listApprovalHistory(UUID entityOid) {
        return selectList(new EntityWrapper<ApprovalHistory>()
                .eq("entity_oid", entityOid)
                .orderBy("id", false));

    }

    /**
     * 查询审批历史
     *
     * @param entityType
     * @param entityOids
     * @return
     */
    public List<ApprovalHistory> listApprovalHistory(Integer entityType, List<UUID> entityOids) {
       return selectList(new EntityWrapper<ApprovalHistory>()
                .in("entity_oid", entityOids)
                .eq("entity_type",entityType)
                .orderBy("id", false));
    }


    public void readInApprovalHistory(ApprovalHistoryDTO dto) {
        ApprovalHistory approvalHistory = new ApprovalHistory();
        approvalHistory.setOperatorOid(dto.getOperatorOid());
        approvalHistory.setEntityType(dto.getEntityType());
        approvalHistory.setOperation(dto.getOperation());
        approvalHistory.setOperationType(dto.getOperationType());
        approvalHistory.setOperationDetail(dto.getOperationDetail());
        approvalHistory.setEntityOid(dto.getEntityOid());
        save(approvalHistory);
    }

    /**
     * 创建审批历史
     * domain 属性必要参数：
     * <ul>
     * <li>OperatorOid :操作人</li>
     * <li>setEntityOid</li>
     * <li>setEntityType ：单据类型</li>
     * <li>setOperation：action（行为）定义</li>
     * <li>setOperationType：action执行人</li>
     * <li>setOperationDetail：操作说明</li>
     * </ul>
     *
     * @param dto
     * @return
     */
    public ApprovalHistoryDTO createApprovalHistory(ApprovalHistoryDTO dto) {
        ApprovalHistory approvalHistory = approvalHistoryDTOToApprovalHistory(dto);
        save(approvalHistory);
        return approvalHistoryToApprovalHistoryDTO(approvalHistory);
    }

    /**
     * 批量创建审批历史
     * domain 属性必要参数：
     * <ul>
     * <li>OperatorOid :操作人</li>
     * <li>setEntityOid</li>
     * <li>setEntityType ：单据类型</li>
     * <li>setOperation：action（行为）定义</li>
     * <li>setOperationType：action执行人</li>
     * <li>setOperationDetail：操作说明</li>
     * </ul>
     *
     * @param dtos
     * @return
     */
    public List<ApprovalHistoryDTO> batchCreateApprovalHistory(List<ApprovalHistoryDTO> dtos) {
        List<ApprovalHistory> approvalHistorys = convertToApprovalHistoryList(dtos);
        for (ApprovalHistory approvalHistory : approvalHistorys) {
            approvalHistory.setId(null);
        }
        saveApprovalHistoryList(approvalHistorys);
       return approvalHistoryListToApprovalHistoryDTOList(approvalHistorys);
    }

    /**
     * 根据实体类型、实体Oid、操作类型、操作查询审批流历史
     *
     * @param entityType：实体类型
     * @param entityOid：实体Oid
     * @param operationType：操作类型
     * @param operation：操作
     * @return
     */
    @Transactional
    public ApprovalHistoryDTO listApprovalHistory(Integer entityType, UUID entityOid, Integer operationType, Integer operation) {
        ApprovalHistory approvalHistory =selectOne(new EntityWrapper<ApprovalHistory>()
                .in("entity_oid", entityOid)
                .eq("entity_type",entityType)
                .eq("operation_type",operationType)
                .eq("operation",operation)
                .orderBy("id", false));
        return approvalHistoryToApprovalHistoryDTO(approvalHistory);
    }

    public ApprovalHistoryDTO getApprovalHistoryByOperation(Integer entityType, UUID entityOid, Integer operation) {
        ApprovalHistory approvalHistory = selectOne(new EntityWrapper<ApprovalHistory>()
                .in("entity_oid", entityOid)
                .eq("entity_type",entityType)
                .eq("operation",operation)
                .orderBy("id", false));
        return approvalHistoryToApprovalHistoryDTO(approvalHistory);
    }

    /**
     * 查询单据提交时间
     *
     * @param entityType
     * @param entityOids
     * @return
     */
    public Map<UUID, ZonedDateTime> getSubmittedDate(Integer entityType, List<UUID> entityOids) {
        Map<UUID, ZonedDateTime> submitDateMap = new HashMap<>();
        //审批历史的提交时间
        List<ApprovalHistory> approvalHistories = this.listApprovalHistory(entityType, entityOids.stream().filter(u -> u != null).collect(Collectors.toList()));
        Map<UUID, List<ApprovalHistory>> approvalHistoryMap = approvalHistories.stream().collect(Collectors.groupingBy(ApprovalHistory::getEntityOid));
        for (Map.Entry<UUID, List<ApprovalHistory>> uuidListEntry : approvalHistoryMap.entrySet()) {
            List<ApprovalHistory> histories = uuidListEntry.getValue();
            Optional<ApprovalHistory> approvalHistoryOptional = histories.stream().filter(u -> u.getOperation().equals(ApprovalOperationEnum.SUBMIT_FOR_APPROVAL.getId()) && u.getOperationType().equals(ApprovalOperationTypeEnum.SELF.getId())).findFirst();
            if (approvalHistoryOptional.isPresent()) {
                submitDateMap.put(uuidListEntry.getKey(), approvalHistoryOptional.get().getCreatedDate());
            }
        }
        return submitDateMap;
    }


    private void robotNameMapping(PDFApprovalHistory pdfApprovalHistory) {
        if (pdfApprovalHistory != null && ApprovalOperationTypeEnum.SYSTEM.getId().equals(pdfApprovalHistory.getOperationType())) {
            pdfApprovalHistory.setRoleName(APPROVER_TYPE_ROBOT_NAME);
            pdfApprovalHistory.setOperator(APPROVER_TYPE_ROBOT_NAME);
        }
    }

    private String getPDFOperation(ApprovalHistory history, Locale locale) {
        String result = "";
        String operationMsgKey = operationMsgMap.get(history.getOperation());
        if (ApprovalOperationTypeEnum.SELF.getId().equals(history.getOperationType()) && ApprovalOperationEnum.APPROVAL_PASS.getId().equals(history.getOperation())) {
            operationMsgKey = "pdf.approval.operation.enum.self.approved";
        }
        // 提交和审批的时候如果有加签，前面需要显示"加签 "
        if (history.getCountersignType() != null && !ApprovalOperationEnum.ADD_COUNTERSIGN.getId().equals(history.getOperation())) {
            result = messageSource.getMessage("pdf.approval.operation.enum.sign", null, locale) + " ";
        }
        if (operationMsgKey != null) {
            result += messageSource.getMessage(operationMsgKey, null, locale);
        }
        return result;
    }

    /**
     * 处理operationDetail, 如果加签类型是顺序加签，则将detail中的逗号（，）替换成箭头（->）
     *
     * @param history
     */
    private void detailForSign(ApprovalHistory history) {
        String detail = history.getOperationDetail();
        if (detail != null && CounterSignTypeEnum.COUNTER_SIGN_TYPE_ALL_BY_ORDER.getValue().equals(history.getCountersignType())) {
            history.setOperationDetail(detail.replace(",", "->"));
        }
    }

    /**
     * 判定改审批记录是否是加签
     *
     * @param approvalHistory
     * @return
     */
    private String isAddSign(ApprovalHistory approvalHistory) {
        //countersignType为null的操作都不是加签操作
        return (approvalHistory.getCountersignType() != null || ApprovalOperationEnum.ADD_COUNTERSIGN.getId().equals(approvalHistory.getOperation())) ? "加签 " : "";
    }

    public List<ApprovalHistory> listByEntityTypeAndEntityOidOrderByIdDesc(Integer entityType, UUID entityOid) {
       return selectList(new EntityWrapper<ApprovalHistory>()
                .in("entity_oid", entityOid)
                .eq("entity_type", entityType)
                .orderBy("id", false));
    }


    /**
     * 获取审批历史的加签信息
     * 公共方法
     *
     * @param
     * @return
     */
    public String getAddSignDetailHistory(Integer countersignType, String operationDetail) {
        final Integer AND_RELATIONSHIP = 2;
        StringBuilder result = new StringBuilder("(");
        if (AND_RELATIONSHIP.equals(countersignType)) {
            result.append((operationDetail == null) ? "" : operationDetail.replace(",", "->"));
        } else {
            result.append(operationDetail);
        }
        result.append(")");
        return result.toString();
    }

    /**
     * 判定改审批记录是否是加签
     * 公共方法
     *
     * @return
     */
    public String isAddSignHistory(Integer countersignType, Integer operation) {
        //countersignType为null的操作都不是加签操作
        return (countersignType != null || ApprovalOperationEnum.ADD_COUNTERSIGN.getId().equals(operation)) ? "加签 " : "";
    }

    /**
     * 判断报销单记录是否需要显示小喇叭
     *
     * @param entityOids
     * @return
     */
    public Map<UUID, Boolean> checkAuditNotice(List<UUID> entityOids) {
        //最近"财务通知"、"员工回复"操作历史
        List<CheckAuditNoticeDTO> lastBillCommentOperations = baseMapper.checkAuditNotice(entityOids, Arrays.asList(ApprovalOperationEnum.AUDIT_NOTICE.getId(), ApprovalOperationEnum.STAFF_REPLY.getId()));
        Map<UUID, Long> lastBillCommentMap = null;
        if (CollectionUtils.isNotEmpty(lastBillCommentOperations)) {
            lastBillCommentMap = lastBillCommentOperations.stream().collect(Collectors.toMap(u -> u.getEntityOid(), u -> u.getId()));
        }
        //最近"单据提交"操作历史
        List<CheckAuditNoticeDTO> lastBillCommitOperations = baseMapper.checkAuditNotice(entityOids, Arrays.asList(ApprovalOperationEnum.SUBMIT_FOR_APPROVAL.getId()));

        Map<UUID, Boolean> targetMap = new HashMap<UUID, Boolean>();
        if (CollectionUtils.isNotEmpty(lastBillCommitOperations)) {
            for (CheckAuditNoticeDTO billCommitOperation : lastBillCommitOperations) {
                //"财务通知"、"员工回复"操作历史id > "单据提交"操作历史    noticeFlag 置为true
                if (lastBillCommentMap != null && lastBillCommentMap.get(billCommitOperation.getEntityOid()) != null && lastBillCommentMap.get(billCommitOperation.getEntityOid()) > billCommitOperation.getId()) {
                    targetMap.put(billCommitOperation.getEntityOid(), true);
                    //noticeFlag 置为false
                } else {
                    targetMap.put(billCommitOperation.getEntityOid(), false);
                }
            }
        }
        return targetMap;
    }




    public Boolean deleteByIds(List<Long> ids) {
        return deleteBatchIds(ids);
    }

    public List<ApprovalHistoryDTO> getLatestApprovalHistory(List<UUID> entityOids) {
        List<ApprovalHistory> approvalHistoryList = baseMapper.getApprovalHistoryByEntityOids(entityOids);
        List<ApprovalHistoryDTO> approvalHistoryDTOList = approvalHistoryListToApprovalHistoryDTOList(approvalHistoryList);

        //组装操作人信息
        Set<UUID> userOids = approvalHistoryDTOList.stream().filter(e -> {
            return e.getOperatorOid() != null;
        }).map(ApprovalHistoryDTO::getOperatorOid).collect(Collectors.toSet());

        //统一查询人员
        List<UserApprovalDTO> userSearchViewDTOs = baseClient.listByUserOids(new ArrayList<>(userOids));

        Map<UUID, UserApprovalDTO> userSearchViewDTOMap = userSearchViewDTOs.stream().collect(Collectors.toMap(UserApprovalDTO::getUserOid, (p) -> p));

        for (ApprovalHistoryDTO approvalHistoryDTO : approvalHistoryDTOList) {
            //用于防止Json重复引用
            UserApprovalDTO operator = new UserApprovalDTO();

            if (userSearchViewDTOMap != null) {
                UserApprovalDTO sourceOperator = userSearchViewDTOMap.get(approvalHistoryDTO.getOperatorOid());
                if (sourceOperator != null) {
                    BeanUtils.copyProperties(sourceOperator, operator);
                    if (RuleConstants.APPROVER_TYPE_ROBOT_OID.equals(String.valueOf(operator.getUserOid()))) {
                        if (!Constants.DEFAULT_LANGUAGE.equals(OrgInformationUtil.getCurrentLanguage())) {
                            operator.setFullName(RuleConstants.APPROVER_TYPE_ROBOT_NAME_ENGLISH);
                        }
                    }
                    approvalHistoryDTO.setOperator(operator);
                }
            }
            String operationDescription =messageService.getMessageDetailByCode(ApprovalOperationEnum.getMessageKeyByID(approvalHistoryDTO.getOperation()));
            approvalHistoryDTO.setOperationDescription(operationDescription);
        }

        return approvalHistoryDTOList;
    }

    public void approvalAction(List<WebApprovalHistoryDTO> list, List<ApprovalHistoryDTO> approvalHistoryDtoList, ApprovalHistoryService approvalHistoryService) {
        approvalHistoryDtoList.stream().forEach(u -> {
            WebApprovalHistoryDTO webApprovalHistoryDTO = new WebApprovalHistoryDTO();
            //审批动作
            String result = null;
            switch (u.getOperationType()) {
                case 1001:
                    if (ApprovalOperationEnum.SUBMIT_FOR_APPROVAL.getId().equals(u.getOperation())) {
                        result = "提交";
                    } else if (ApprovalOperationEnum.WITHDRAW.getId().equals(u.getOperation())) {
                        result = "撤回申请";
                    } else if (ApprovalOperationEnum.REPAYMENT_F_SUBMIT.getId().equals(u.getOperation())) {
                        result = "预算日记账还款提交";
                    } else if (ApprovalOperationEnum.APPROVAL_PASS.getId().equals(u.getOperation())) {
                        result = "自审批通过";
                    } else if (ApprovalOperationEnum.ADD_COUNTERSIGN.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + approvalHistoryService.getAddSignDetailHistory(u.getCountersignType(), u.getOperationDetail());
                    }
                    break;
                case 1002:
                    if (ApprovalOperationEnum.APPROVAL_PASS.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "审批通过";
                    } else if (ApprovalOperationEnum.APPROVAL_REJECT.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "审批驳回";
                    } else if (ApprovalOperationEnum.ADD_COUNTERSIGN.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + approvalHistoryService.getAddSignDetailHistory(u.getCountersignType(), u.getOperationDetail());
                    }
                    break;
                case 1003:
                    if (ApprovalOperationEnum.AUDIT_PASS.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "审核通过";
                    } else if (ApprovalOperationEnum.AUDIT_REJECT.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "审核驳回";
                    } else if (ApprovalOperationEnum.FINANCE_LOANED.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "财务付款";//4001
                    } else if (ApprovalOperationEnum.PAYMENT_IN_PROCESS.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "财务付款中";//4000
                    } else if (ApprovalOperationEnum.REVIEWED_AMOUNT.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "核定金额修改";//7001
                    } else if (ApprovalOperationEnum.REVIEWED_RATE.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "核定汇率修改";//7002
                    } else if (ApprovalOperationEnum.REVIEWED_AMOUNT_RATE.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "核定金额和汇率修改";//7003
                    } else if (ApprovalOperationEnum.ADD_COUNTERSIGN.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + approvalHistoryService.getAddSignDetailHistory(u.getCountersignType(), u.getOperationDetail());
                    }
                    break;
                case 1004:
                    if (ApprovalOperationEnum.RECEIPT_PASS.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "开票通过";
                    } else if (ApprovalOperationEnum.RECEIPT_REJECT.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.isAddSignHistory(u.getCountersignType(), u.getOperation()) + "开票驳回";
                    } else if (ApprovalOperationEnum.ADD_COUNTERSIGN.getId().equals(u.getOperation())) {
                        result = approvalHistoryService.getAddSignDetailHistory(u.getCountersignType(), u.getOperationDetail());
                    }
                    break;
            }
            //设置审批动作描述
            webApprovalHistoryDTO.setOperationRemark(result);
            //员工名称
            webApprovalHistoryDTO.setEmployeeName(u.getOperator().getFullName());
            //员工工号
            webApprovalHistoryDTO.setEmployeeId(u.getOperator().getEmployeeCode());
            //设置审批动作
            webApprovalHistoryDTO.setOperation(u.getOperation());
            //设置审批类型
            webApprovalHistoryDTO.setOperationType(u.getOperationType());
            //审批时间
            webApprovalHistoryDTO.setLastUpdatedDate(u.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            //审批意见
            webApprovalHistoryDTO.setOperationDetail(u.getOperationDetail());
            //审批节点
            webApprovalHistoryDTO.setApprovalNodeName(u.getApprovalNodeName());
            list.add(webApprovalHistoryDTO);
        });
    }

    /**
     * 查询审批人重复审批通过的审批历史
     *
     * @param entityType
     * @param entityOid
     * @param operation
     * @return
     */
    public List<ApprovalHistory> listByEntityTypeAndEntityOidAndOperationAndCountersignTypeNotNull(Integer entityType, UUID entityOid, Integer operation) {
        return
        baseMapper.getByEntityTypeAndEntityOidAndOperationAndCountersignTypeNotNull(entityType, entityOid, operation);
    }

    /**
     * 查询审批人重复审批通过的审批历史
     *
     * @return
     */
    public List<ApprovalHistory> listByEntityTypeAndEntityOidAndOperationAndCountersignTypeIsNull(Integer entityType, UUID entityOid, Integer operation) {
        return baseMapper.getByEntityTypeAndEntityOidAndOperationAndCountersignTypeIsNull(entityType, entityOid, operation);
    }

    /**
     * 查询所有审批通过的审批历史
     *
     * @return
     */
    public List<ApprovalHistory> listByEntityTypeAndEntityOidAndOperation(Integer entityType, UUID entityOid, Integer operation) {
        return baseMapper.getByEntityTypeAndEntityOidAndOperation(entityType, entityOid, operation);
    }


    /**
     * to domain
     *
     * @param approvalHistory
     * @return
     */
    public static ApprovalHistoryDTO approvalHistoryToApprovalHistoryDTO(ApprovalHistory approvalHistory) {
        if (approvalHistory == null) {
            return null;
        }
        ApprovalHistoryDTO approvalHistoryDTO = new ApprovalHistoryDTO();
        approvalHistoryDTO.setId(approvalHistory.getId());
        approvalHistoryDTO.setCreatedDate(approvalHistory.getCreatedDate());
        approvalHistoryDTO.setEntityOid(approvalHistory.getEntityOid());
        approvalHistoryDTO.setEntityType(approvalHistory.getEntityType());
        approvalHistoryDTO.setLastUpdatedDate(approvalHistory.getLastUpdatedDate());
        approvalHistoryDTO.setOperation(approvalHistory.getOperation());
        approvalHistoryDTO.setOperationDetail(approvalHistory.getOperationDetail());
        approvalHistoryDTO.setOperationType(approvalHistory.getOperationType());
        approvalHistoryDTO.setOperatorOid(approvalHistory.getOperatorOid());
        approvalHistoryDTO.setCurrentApplicantOid(approvalHistory.getCurrentApplicantOid());
        approvalHistoryDTO.setStepID(approvalHistory.getStepID());
        approvalHistoryDTO.setRemark(approvalHistory.getRemark());
        approvalHistoryDTO.setCountersignType(approvalHistory.getCountersignType());
        approvalHistoryDTO.setApportionmentFlag(approvalHistory.isApportionmentFlag());
        approvalHistoryDTO.setRefApprovalChainId(approvalHistory.getRefApprovalChainId());
        approvalHistoryDTO.setApprovalNodeName(approvalHistory.getApprovalNodeName());
        return approvalHistoryDTO;
    }

    /**
     * to domain list
     *
     * @param approvalHistoryList
     * @return
     */
    public static List<ApprovalHistoryDTO> approvalHistoryListToApprovalHistoryDTOList(List<ApprovalHistory> approvalHistoryList) {
        if (approvalHistoryList == null) {
            return null;
        }
        return approvalHistoryList.stream().map(h->approvalHistoryToApprovalHistoryDTO(h)).collect(Collectors.toList());
    }

    /**
     * to domain
     *
     * @param approvalHistoryDTO
     * @return
     */
    public static ApprovalHistory approvalHistoryDTOToApprovalHistory(ApprovalHistoryDTO approvalHistoryDTO) {
        if (approvalHistoryDTO == null) {
            return null;
        }

        ApprovalHistory approvalHistory = new ApprovalHistory();
        approvalHistory.setId(approvalHistoryDTO.getId());
        approvalHistory.setCreatedDate(approvalHistoryDTO.getCreatedDate());
        approvalHistory.setEntityOid(approvalHistoryDTO.getEntityOid());
        approvalHistory.setEntityType(approvalHistoryDTO.getEntityType());
        approvalHistory.setLastUpdatedDate(approvalHistoryDTO.getLastUpdatedDate());
        approvalHistory.setOperation(approvalHistoryDTO.getOperation());
        approvalHistory.setOperationDetail(approvalHistoryDTO.getOperationDetail());
        approvalHistory.setOperationType(approvalHistoryDTO.getOperationType());
        approvalHistory.setOperatorOid(approvalHistoryDTO.getOperatorOid());
        approvalHistory.setStepID(approvalHistoryDTO.getStepID());
        approvalHistory.setRemark(approvalHistoryDTO.getRemark());
        return approvalHistory;
    }

    /**
     * to domain list
     *
     * @param approvalHistoryDTOList
     * @return
     */
    public static List<ApprovalHistory> convertToApprovalHistoryList(List<ApprovalHistoryDTO> approvalHistoryDTOList) {
        if (approvalHistoryDTOList == null) {
            return null;
        }
        List<ApprovalHistory> approvalHistoryList = new ArrayList<>();
        for (ApprovalHistoryDTO approvalHistoryDTO : approvalHistoryDTOList) {
            approvalHistoryList.add(approvalHistoryDTOToApprovalHistory(approvalHistoryDTO));
        }
        return approvalHistoryList;
    }
}
