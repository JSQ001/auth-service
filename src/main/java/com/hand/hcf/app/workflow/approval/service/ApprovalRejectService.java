package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.service.BrmsService;
import com.hand.hcf.app.workflow.constant.RuleConstants;
import com.hand.hcf.app.workflow.constant.SyncLockPrefix;
import com.hand.hcf.app.workflow.util.RespCode;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.domain.ApprovalHistory;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalReqDTO;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationTypeEnum;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.service.ApprovalHistoryService;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.app.workflow.service.WorkFlowEventPublishService;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ServiceUnavailableException;
import com.hand.hcf.core.redisLock.annotations.LockedObject;
import com.hand.hcf.core.redisLock.annotations.SyncLock;
import com.hand.hcf.core.redisLock.enums.CredentialTypeEnum;
import com.hand.hcf.core.service.MessageService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApprovalRejectService {
    public static Logger logger = LoggerFactory.getLogger(ApprovalRejectService.class);

    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private BrmsService brmsService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private WorkFlowEventPublishService workflowEventPublishService;

    @Autowired
    private MessageService messageService;

    /**
     * 审批驳回
     *
     * @param approverOid
     * @param approvalReqDTO
     * @return
     */
    @SyncLock(lockPrefix = SyncLockPrefix.APPROVAL, credential = CredentialTypeEnum.USER_OID)
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO rejectWorkflow(UUID approverOid, ApprovalReqDTO approvalReqDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);
        if (CollectionUtils.isNotEmpty(approvalReqDTO.getEntities())) {
            for (ApprovalReqDTO.Entity entity : approvalReqDTO.getEntities()) {
                try {
                    boolean canRejectDocument = rejectWorkflow(approverOid, entity.getEntityType(), UUID.fromString(entity.getEntityOid()), entity.getApproverOid() == null ? null : UUID.fromString(entity.getApproverOid()), approvalReqDTO.getApprovalTxt());
                    approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);
                    // 发送事件消息 修改各单据的审批状态 提交的时候不发布消息，撤回和审批，拒绝时，发布消息
                    WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(UUID.fromString(entity.getEntityOid()), entity.getEntityType());

                    // 只有提交状态的单据才可以审批通过和拒绝和撤回
                    if (!DocumentOperationEnum.APPROVAL.getId().equals(workFlowDocumentRef.getStatus())) {
                        throw new BizException(RespCode.STATUS_ERROR_200003);
                    }

                    // 支持任一人（任一审批人审批通过则单据被审批通过，所有审批人都审批驳回则单据才被驳回）会签规则
                    if (canRejectDocument) {
                        workFlowDocumentRef.setStatus(DocumentOperationEnum.APPROVAL_REJECT.getId());
                        workFlowDocumentRef.setRejectReason(approvalReqDTO.getApprovalTxt());
                        workFlowDocumentRef.setLastRejectType(DocumentOperationEnum.APPROVAL_REJECT.getId().toString());
                        workFlowDocumentRef.setRejectType(DocumentOperationEnum.APPROVAL_REJECT.getId().toString());
                        workFlowDocumentRef.setLastApproverOid(approverOid);
                        workFlowDocumentRef.setRejectReason(approvalReqDTO.getApprovalTxt());// 审批意见
                        workflowEventPublishService.publishEvent(workFlowDocumentRef);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("approval.rejectWorkflow.error", e);
                    approvalResDTO.setFailNum(approvalResDTO.getFailNum() + 1);
                    if (e instanceof BizException) {
                        String messageDetailByCode = messageService.getMessageDetailByCode(((BizException) e).getCode());
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), messageDetailByCode);
                    } else if (e instanceof ServiceUnavailableException) {
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), messageService.getMessageDetailByCode(RespCode.SERVICE_6001, e.getMessage()));
                    } else {
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), e.getMessage());
                    }
                }
            }
        }
        return approvalResDTO;
    }

    /**
     * 审批驳回：整单驳回
     * @param approverOid      当前登录人,可能是审批人本人,也可能是其代理人
     * @param entityType
     * @param entityOid
     * @param chainApproverOid 审批链上的审批人
     * @param approvalTxt
     * @return true可以最终决定驳回单据，false无法最终决定驳回单据
     */
    @Transactional
    @SyncLock(lockPrefix = SyncLockPrefix.APPROVAL, errorMessage = RespCode.SYS_REQUEST_BE_PROCESSING)
    public boolean rejectWorkflow(UUID approverOid, Integer entityType, @LockedObject UUID entityOid, UUID chainApproverOid, String approvalTxt) {
        ApprovalChain approvalChain;
        RuleApprovalNodeDTO ruleApprovalNode = new RuleApprovalNodeDTO();
        if (chainApproverOid != null && !chainApproverOid.equals(approverOid)) {
            approvalChain = approvalChainService.getByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndApproverOid(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), true, chainApproverOid);
        } else {
            approvalChain = approvalChainService.getByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndApproverOid(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), true, approverOid);
        }
        logger.debug("debug->rejectWorkflow->approvalChain：{}", approvalChain);
        if (approvalChain == null) {
            throw new RuntimeException(RuleConstants.CANNOT_FIND_CURRENT_APPROVAL);
        }
        //获取当前审批节点
        if (approvalChain.getRuleApprovalNodeOid() != null) {
            ruleApprovalNode = brmsService.getApprovalNode(approvalChain.getRuleApprovalNodeOid(), approvalChain.getApproverOid());
        }
        // 插入操作历史
        ApprovalHistory approvalHistory = new ApprovalHistory();
        logger.debug("debug->rejectWorkflow->approvalChain：{}", approvalChain);
        approvalHistory.setApportionmentFlag(approvalChain.getApportionmentFlag());
        approvalHistory.setRefApprovalChainId(approvalChain.getId());
        approvalHistory.setEntityType(entityType);
        approvalHistory.setEntityOid(entityOid);
        approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
        approvalHistory.setOperation(ApprovalOperationEnum.APPROVAL_REJECT.getId());
        //审批节点名称
        approvalHistory.setApprovalNodeName(ruleApprovalNode.getRemark());
        approvalHistory.setApprovalNodeOid(approvalChain.getRuleApprovalNodeOid());
        approvalHistory.setOperatorOid(approverOid);
        approvalHistory.setOperationDetail(approvalTxt);
        approvalHistory.setCreatedDate(ZonedDateTime.now());
        approvalHistory.setLastUpdatedDate(ZonedDateTime.now());
        approvalHistory.setLastUpdatedDate(ZonedDateTime.now().plusMinutes(1));
        approvalHistoryService.save(approvalHistory);

        boolean canRejectDocument = true;
        // 审批步骤作废
        if (RuleApprovalEnum.RULE_CONUTERSIGN_ALL_REJECT.getId().equals(approvalChain.getCountersignRule())) {
            // 支持任一人（任一审批人审批通过则单据被审批通过，所有审批人都审批驳回则单据才被驳回）会签规则
            //
            // 只修改当前的审批链
            approvalChain.setStatus(ApprovalChainStatusEnum.INVALID.getId());
            approvalChain.setLastUpdatedDate(ZonedDateTime.now());
            approvalChainService.save(approvalChain);

            List<ApprovalChain> chainList = approvalChainService.listByEntityTypeAndEntityOidAndStatus(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId());
            // 所有审批人都审批驳回才算单据被驳回
            canRejectDocument = CollectionUtils.isEmpty(chainList);
        } else {
            List<ApprovalChain> chainList = approvalChainService.listByEntityTypeAndEntityOidAndStatus(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId());
            if (CollectionUtils.isNotEmpty(chainList)) {
                for (ApprovalChain chain : chainList) {
                    chain.setStatus(ApprovalChainStatusEnum.INVALID.getId());
                    chain.setLastUpdatedDate(ZonedDateTime.now());
                }
            }
            approvalChainService.saveAll(chainList);
            canRejectDocument = true;
        }

        logger.info("整单驳回驳回标签entityOid->{}", entityOid);
        return canRejectDocument;
    }

}
