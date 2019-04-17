package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.workflow.constant.SyncLockPrefix;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalReqDTO;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.app.workflow.service.WorkFlowEventPublishService;
import com.hand.hcf.app.workflow.util.ExceptionCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ServiceUnavailableException;
import com.hand.hcf.app.core.redisLock.annotations.SyncLock;
import com.hand.hcf.app.core.redisLock.enums.CredentialTypeEnum;
import com.hand.hcf.app.core.service.MessageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ApprovalPassService {
    private static final Logger logger = LoggerFactory.getLogger(ApprovalPassService.class);

    @Autowired
    private ApprovalRuleService approvalRuleService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkFlowEventPublishService workflowEventPublishService;

    @Autowired
    private MessageService messageService;

    /**
     * 审批通过
     *
     * @param approverOid
     * @param approvalReqDTO
     * @return
     */
    @SyncLock(lockPrefix = SyncLockPrefix.APPROVAL, credential = CredentialTypeEnum.USER_OID)
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO passWorkflow(UUID approverOid, ApprovalReqDTO approvalReqDTO, UUID formOid) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);
        if (CollectionUtils.isNotEmpty(approvalReqDTO.getEntities())) {
            for (ApprovalReqDTO.Entity entity : approvalReqDTO.getEntities()) {
                try {
                    WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(UUID.fromString(entity.getEntityOid()), entity.getEntityType());
                    // 只有提交状态的单据才可以审批通过和拒绝和撤回
                    if (!DocumentOperationEnum.APPROVAL.getId().equals(workFlowDocumentRef.getStatus())) {
                        throw new BizException(ExceptionCode.STATUS_ERROR_200003);
                    }
                    approvalRuleService.passWorkflow(approverOid, entity.getApproverOid() == null ? null : UUID.fromString(entity.getApproverOid()), approvalReqDTO.getApprovalTxt(), false, entity.isPriceAuditor(), approvalResDTO, workFlowDocumentRef);
                    approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);

                    workFlowDocumentRef.setRejectReason(approvalReqDTO.getApprovalTxt());// 审批意见
                    // 发送事件消息 修改各单据的审批状态 提交的时候不发布消息，撤回和审批，拒绝时，发布消息
                    workflowEventPublishService.publishEvent(workFlowDocumentRef);

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.warn("approval.pass.error:", e);
                    approvalResDTO.setFailNum(approvalResDTO.getFailNum() + 1);
                    if (e instanceof BizException) {
                        String messageDetailByCode = messageService.getMessageDetailByCode(((BizException) e).getCode(), ((BizException) e).getArgs());
                        if (StringUtils.isEmpty(messageDetailByCode)) {
                            approvalResDTO.getFailReason().put(entity.getEntityOid(), e.getMessage());
                        } else {
                            approvalResDTO.getFailReason().put(entity.getEntityOid(), messageDetailByCode);
                        }
                    } else if (e instanceof ServiceUnavailableException) {
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), messageService.getMessageDetailByCode(ExceptionCode.SERVICE_6001, e.getMessage()));
                    } else {
                        approvalResDTO.getFailReason().put(entity.getEntityOid(), e.getMessage());
                    }
                }
            }
        }
        return approvalResDTO;
    }

}
