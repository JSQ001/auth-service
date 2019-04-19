package com.hand.hcf.app.workflow.approval.service;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.constant.SyncLockPrefix;
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
import com.hand.hcf.app.workflow.util.ExceptionCode;
import com.hand.hcf.app.core.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ApprovalWithdrawService {
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private WorkFlowEventPublishService workflowEventPublishService;

    /**
     * 审批撤回
     *
     * @param userOid
     * @param approvalReqDTO
     * @return
     */
    @Transactional
    //@LcnTransaction
   //@SyncLock(lockPrefix = SyncLockPrefix.APPROVAL, credential = CredentialTypeEnum.USER_OID)
    public ApprovalResDTO withdrawWorkflow(UUID userOid, ApprovalReqDTO approvalReqDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);
        if (approvalReqDTO.getEntities() != null) {
            List<WorkFlowDocumentRef> documentRefList = new ArrayList<>();
            approvalReqDTO.getEntities().stream().forEach(entity -> {
                WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(UUID.fromString(entity.getEntityOid()), entity.getEntityType());
                if (workFlowDocumentRef != null) {
                    try {
                        // 只有提交状态的单据才可以审批通过和拒绝和撤回
                        if (!DocumentOperationEnum.APPROVAL.getId().equals(workFlowDocumentRef.getStatus())) {
                            throw new BizException(ExceptionCode.STATUS_ERROR_200003);
                        }
                        workFlowDocumentRef.setLastApproverOid(userOid);
                        documentRefList.add(workFlowDocumentRef);
                        if (DocumentOperationEnum.APPROVAL_PASS.getId().equals(workFlowDocumentRef.getStatus())) {
                            throw new RuntimeException("已审批通过的单据，不允许撤回，单据Oid：" + workFlowDocumentRef.getDocumentOid());
                        }
                        // 将审批流数据设置为无效
                        withdrawBase(workFlowDocumentRef, approvalReqDTO.getApprovalTxt());
                        approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        approvalResDTO.setFailNum(approvalResDTO.getFailNum() + 1);
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), "Cannot parse entity type: " + entity.getEntityType());
                        if (e instanceof BizException) {
                            BizException biz = (BizException) e;
                            throw new BizException(biz.getCode());
                        } else {
                            throw new BizException("", e.getMessage());
                        }
                    }
                }
            });
            if (documentRefList != null) {
                // 发布消息事件，修改单据的状态为 撤回
                documentRefList.stream().forEach(doc -> {
                    doc.setStatus(DocumentOperationEnum.WITHDRAW.getId());
                    doc.setRejectType(DocumentOperationEnum.WITHDRAW.getId().toString());
                    doc.setLastRejectType(DocumentOperationEnum.WITHDRAW.getId().toString());
                    doc.setRejectReason(approvalReqDTO.getApprovalTxt());
                    this.workflowEventPublishService.publishEvent(doc);
                });
            }
        }
        return approvalResDTO;
    }

    @Transactional
    protected void withdrawBase(WorkFlowDocumentRef workFlowDocumentRef, String txt) {
        UUID entityOid = workFlowDocumentRef.getDocumentOid();
        Integer entityType = workFlowDocumentRef.getDocumentCategory();
        UUID userOid = workFlowDocumentRef.getLastApproverOid();
        // 操作历史
        ApprovalHistory approvalHistory = new ApprovalHistory();
        approvalHistory.setEntityType(entityType);
        approvalHistory.setEntityOid(entityOid);
        approvalHistory.setOperationType(ApprovalOperationTypeEnum.SELF.getId());
        approvalHistory.setOperation(ApprovalOperationEnum.WITHDRAW.getId());
        approvalHistory.setOperationDetail(txt);
        approvalHistory.setOperatorOid(userOid);
        approvalHistory.setCreatedDate(ZonedDateTime.now());
        approvalHistory.setLastUpdatedDate(ZonedDateTime.now());
        if (workFlowDocumentRef.getApprovalNodeOid() != null) {
            approvalHistory.setApprovalNodeOid(UUID.fromString(workFlowDocumentRef.getApprovalNodeOid()));
        }
        approvalHistory.setApprovalNodeName(workFlowDocumentRef.getApprovalNodeName());
        approvalHistoryService.save(approvalHistory);
        // 审批步骤
        List<ApprovalChain> chainList = approvalChainService.listByEntityTypeAndEntityOidAndStatus(entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId());
        if (CollectionUtils.isNotEmpty(chainList)) {
            for (ApprovalChain approvalChain : chainList) {
                approvalChain.setStatus(ApprovalChainStatusEnum.INVALID.getId());
                approvalChain.setLastUpdatedDate(ZonedDateTime.now());
            }
        }
        approvalChainService.saveAll(chainList);
    }

}
