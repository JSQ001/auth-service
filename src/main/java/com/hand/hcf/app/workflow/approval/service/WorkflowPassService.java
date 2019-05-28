package com.hand.hcf.app.workflow.approval.service;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowPassTaskAction;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalReqDTO;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

/**
 * 工作流通过逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowPassService {
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkflowMainService workflowMainService;

    @Autowired
    private WorkflowActionService workflowActionService;

    /**
     * 通过审批
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param userOid 审批人oid
     * @param approvalReqDTO 要审批通过的单据
     * @return 审批的结果
     */
    //@LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO passWorkflow(UUID userOid, ApprovalReqDTO approvalReqDTO) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);

        List<ApprovalReqDTO.Entity> entityList = approvalReqDTO.getEntities();
        if (CollectionUtils.isEmpty(entityList)) {
            return approvalResDTO;
        }

        Integer entityType = null;
        UUID entityOid = null;
        String approvalText = approvalReqDTO.getApprovalTxt();

        for (ApprovalReqDTO.Entity entity : entityList) {
            entityType = entity.getEntityType();
            entityOid = UUID.fromString(entity.getEntityOid());
            // 通过审批
            doPassWorkflow(entityType, entityOid, userOid, approvalText);
            // 累加成功数
            approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);
        }

        return approvalResDTO;
    }

    /**
     * 通过审批
     * @version 1.0
     * @author mh.z
     * @date 2019/04/07
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @param userOid 审批人oid
     * @param approvalText 审批意见
     */
    protected void doPassWorkflow(Integer entityType, UUID entityOid, UUID userOid, String approvalText) {
        Assert.notNull(entityType, "entityType null");
        Assert.notNull(entityOid, "entityOid null");
        Assert.notNull(userOid, "userOid null");

        // 查找要通过的单据
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService
                .getByDocumentOidAndDocumentCategory(entityOid, entityType);
        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        WorkflowUser user = new WorkflowUser(userOid);
        WorkflowPassTaskAction action = new WorkflowPassTaskAction(workflowActionService, instance, user, approvalText);

        // 通过任务
        workflowMainService.runWorkflowAndSendMessage(instance, action);
    }

}
