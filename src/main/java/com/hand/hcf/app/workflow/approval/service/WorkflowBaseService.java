package com.hand.hcf.app.workflow.approval.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.implement.WorkflowPassTaskAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowRejectTaskAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowSubmitInstanceAction;
import com.hand.hcf.app.workflow.approval.implement.WorkflowWithdrawInstanceAction;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalChainDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApproverDTO;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.service.RuleApprovalNodeService;
import com.hand.hcf.app.workflow.brms.service.RuleService;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.domain.ApprovalHistory;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.AssembleBrmsParamsRespDTO;
import com.hand.hcf.app.workflow.dto.FormValueDTO;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationTypeEnum;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.service.ApprovalHistoryService;
import com.hand.hcf.app.workflow.service.DefaultWorkflowIntegrationServiceImpl;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author mh.z
 */
@Service
public class WorkflowBaseService {
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private DefaultWorkflowIntegrationServiceImpl defaultWorkflowIntegrationService;

    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private ApprovalHistoryService approvalHistoryService;

    @Autowired
    private RuleApprovalNodeService ruleApprovalNodeService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private WorkflowFindUserService workflowFindUserService;

    /**
     * 根据实例和用户查找任务并返回
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance
     * @param user
     * @return
     */
    public WorkflowTask findTask(WorkflowInstance instance, WorkflowUser user) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(user, "user null");
        Assert.notNull(instance.getEntityType(), "instance.entityType null");
        Assert.notNull(instance.getEntityOid(), "instance.entityOid null");
        Assert.notNull(user.getUserOid(), "user.userOid null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();
        UUID userOid = user.getUserOid();

        ApprovalChain approvalChain = approvalChainService.getByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndApproverOid(
                entityType, entityOid, ApprovalChainStatusEnum.NORMAL.getId(), true, userOid);
        WorkflowTask task = WorkflowTask.toTask(approvalChain);

        if (task != null) {
            // 获取节点
            UUID ruleApprovalNodeOid = approvalChain.getRuleApprovalNodeOid();
            RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(ruleApprovalNodeOid);
            WorkflowNode node = WorkflowNode.toNode(ruleApprovalNode);
            // node
            node.setInstance(instance);
            task.setNode(node);
            // instance
            task.setInstance(instance);
            // user
            task.setUser(user);
        }

        return task;
    }

    /**
     * 返回下一个节点
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance
     * @param node
     * @return
     */
    public WorkflowNode findNext(WorkflowInstance instance, WorkflowNode node) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(instance.getFormOid(), "instance.formOid null");
        Assert.notNull(instance.getApplicantOid(), "instance.applicantOid null");
        UUID formOid = instance.getFormOid();
        UUID applicantOid = instance.getApplicantOid();

        UUID ruleApprovalChainOid = null;
        Integer ruleApprovalNodeSequence = null;

        if (node == null) {
            RuleApprovalChainDTO ruleApprovalChainDTO = ruleService.getApprovalChainByFormOid(formOid, applicantOid,
                    false, false, false);
            ruleApprovalChainOid = ruleApprovalChainDTO.getRuleApprovalChainOid();
            ruleApprovalNodeSequence = 0;
        } else {
            ruleApprovalChainOid = node.getChainOid();
            ruleApprovalNodeSequence = node.getSequence();
        }

        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getNextByRuleApprovalChainOid(ruleApprovalChainOid, ruleApprovalNodeSequence);
        WorkflowNode nextNode = WorkflowNode.toNode(ruleApprovalNode);
        nextNode.setInstance(instance);
        return nextNode;
    }

    /**
     * 返回满足审批条件的用户
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node
     * @return
     */
    public List<WorkflowUser> findUsers(WorkflowNode node) {
        Assert.notNull(node, "node null");
        Assert.notNull(node.getId(), "node.id null");
        Assert.notNull(node.getInstance(), "node.instance null");

        Long nodeId = node.getId();
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.selectById(nodeId);
        WorkflowInstance instance = node.getInstance();
        Long instanceId = instance.getId();
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.selectById(instanceId);

        AssembleBrmsParamsRespDTO assembleBrmsParamsRespDTO = defaultWorkflowIntegrationService.assembleNewBrmsParams(workFlowDocumentRef);
        List<FormValueDTO> formValueDTOList = assembleBrmsParamsRespDTO.getFormValueDTOS();
        Map<String, Object> entityData = assembleBrmsParamsRespDTO.getEntityData();
        // 获取满足条件的规则审批人
        List<RuleApproverDTO> ruleUserList = workflowFindUserService.getRuleUserList(workFlowDocumentRef, ruleApprovalNode, formValueDTOList, entityData);
        // 根据规则审批人找到具体用户
        List<UUID> userOidList = workflowFindUserService.getUserOidList(workFlowDocumentRef, ruleApprovalNode, ruleUserList, formValueDTOList);

        List<WorkflowUser> userList = new ArrayList<WorkflowUser>();
        WorkflowUser user = null;
        for (UUID userOid : userOidList) {
            user = new WorkflowUser(userOid);
            userList.add(user);
        }

        return userList;
    }

    /**
     * 统计任务数
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node
     * @param status
     * @return
     */
    public int countTasks(WorkflowNode node, String status) {
        Assert.notNull(node, "node null");
        Assert.notNull(status, "status null");
        Assert.notNull(node.getInstance(), "node.instance null");
        Assert.notNull(node.getNodeOid(), "node.nodeOid null");
        WorkflowInstance instance = node.getInstance();
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();
        UUID nodeOid = node.getNodeOid();

        int taskTotal = doCountTasks(entityType, entityOid, nodeOid, status);
        return taskTotal;
    }

    /**
     * 统计任务数
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance
     * @param status
     * @return
     */
    public int countTasks(WorkflowInstance instance, String status) {
        Assert.notNull(instance, "instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        int taskTotal = doCountTasks(entityType, entityOid, null, status);
        return taskTotal;
    }

    /**
     * 清除跟节点关联的所有未完成任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node
     */
    public void clearUnfinishedTasks(WorkflowNode node) {
        Assert.notNull(node, "node null");
        Assert.notNull(node.getNodeOid(), "node.nodeOid null");
        Assert.notNull(node.getInstance(), "node.instance null");
        UUID nodeOid = node.getNodeOid();
        WorkflowInstance instance = node.getInstance();
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        doClearTask(entityType, entityOid, nodeOid, true);
    }

    /**
     * 清除跟节点关联的所有任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node
     */
    public void clearAllTasks(WorkflowNode node) {
        Assert.notNull(node, "node null");
        Assert.notNull(node.getNodeOid(), "node.nodeOid null");
        Assert.notNull(node.getInstance(), "node.instance null");
        UUID nodeOid = node.getNodeOid();
        WorkflowInstance instance = node.getInstance();
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        doClearTask(entityType, entityOid, nodeOid, null);
    }

    /**
     * 清除跟单据关联的所有未完成任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance
     */
    public void clearUnfinishedTasks(WorkflowInstance instance) {
        Assert.notNull(instance, "instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        doClearTask(entityType, entityOid, null, true);
    }

    /**
     * 清除跟单据关联的所有任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance
     */
    public void clearAllTasks(WorkflowInstance instance) {
        Assert.notNull(instance, "instance null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        doClearTask(entityType, entityOid, null, null);
    }

    /**
     * 更新任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param task
     */
    public void updateTask(WorkflowTask task) {
        Assert.notNull(task, "task null");
        Assert.notNull(task.getId(), "task.id null");
        Assert.notNull(task.getStatus(), "task.status null");
        Assert.notNull(task.getEnabled(), "task.enabled null");
        Long taskId = task.getId();
        String taskStatus = task.getStatus();
        Boolean taskEnabled = task.getEnabled();

        ApprovalChain approvalChain = approvalChainService.selectById(taskId);
        // 设置状态
        approvalChain.setStatus(WorkflowTask.getEnabledValue(taskEnabled));
        WorkflowTask.getStatusValue(approvalChain, taskStatus);
        // 更新任务
        approvalChainService.save(approvalChain);
    }

    /**
     * 更新实例
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance
     * @return
     */
    public WorkflowInstance updateInstance(WorkflowInstance instance) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(instance.getId(), "instance.id null");
        Assert.notNull(instance.getStatus(), "instance.status null");
        Long instanceId = instance.getId();
        String instanceStatus = instance.getStatus();

        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.selectById(instanceId);
        // 设置实例的状态
        workFlowDocumentRef.setStatus(WorkflowInstance.getStatusValue(instanceStatus));
        // 更新实例的状态
        workFlowDocumentRefService.updateById(workFlowDocumentRef);

        instance = WorkflowInstance.toInstance(workFlowDocumentRef);
        return instance;
    }

    /**
     * 保存任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node
     * @param user
     */
    public void saveTask(WorkflowNode node, WorkflowUser user) {
        Assert.notNull(node, "node null");
        Assert.notNull(user, "user null");
        Assert.notNull(node.getInstance(), "node.instance null");
        Assert.notNull(node.getType(), "node.type null");

        WorkflowInstance instance = node.getInstance();
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();
        UUID nodeOid = node.getNodeOid();
        Integer nodeSequence = node.getSequence();
        String countersign = node.getCountersign();
        String nodeType = node.getType();
        UUID userOid = user.getUserOid();
        ZonedDateTime now = ZonedDateTime.now();
        Assert.notNull(entityType, "node.instance.entityType null");
        Assert.notNull(entityOid, "node.instance.entityOid null");
        Assert.notNull(nodeOid, "node.nodeOid null");
        Assert.notNull(nodeSequence, "node.sequence null");
        Assert.notNull(countersign, "node.countersign null");
        Assert.notNull(userOid, "user.userOid null");

        ApprovalChain approvalChain = new ApprovalChain();
        approvalChain.setEntityType(entityType);
        approvalChain.setEntityOid(entityOid);
        approvalChain.setSequence(nodeSequence);
        approvalChain.setCountersignType(null);
        // 会签规则
        approvalChain.setCountersignRule(WorkflowNode.getCountersignValue(countersign));

        approvalChain.setApproverOid(userOid);
        approvalChain.setCurrentFlag(true);
        approvalChain.setFinishFlag(false);
        approvalChain.setStatus(ApprovalChainStatusEnum.NORMAL.getId());

        if (WorkflowNode.TYPE_NOTICE.equals(nodeType)) {
            approvalChain.setNoticed(true);
        } else {
            approvalChain.setNoticed(false);
        }


        approvalChain.setApportionmentFlag(false);
        approvalChain.setRuleApprovalNodeOid(nodeOid);
        approvalChain.setProxyFlag(false);
        approvalChain.setAddSign(null);
        approvalChain.setInvoiceAllowUpdateType(RuleApprovalEnum.NODE_INVOICE_ALLOW_UPDATE_TYPE_NOT_ALLOW.getId());
        approvalChain.setSourceApprovalChainId(null);
        approvalChain.setAllFinished(false);
        approvalChain.setCreatedDate(now);
        approvalChain.setLastUpdatedDate(now);

        // 保存任务
        approvalChainService.save(approvalChain);
    }

    /**
     * 保存历史
     * @author mh.z
     * @date 2019/04/07
     *
     * @param task
     * @param actionName
     * @param remark
     */
    public void saveHistory(WorkflowTask task, String actionName, String remark) {
        Assert.notNull(task, "task null");
        Assert.notNull(actionName, "actionName null");
        Assert.notNull(task.getInstance(), "task.instance null");
        Assert.notNull(task.getUser(), "task.user null");
        Assert.notNull(task.getNode(), "task.node null");

        WorkflowInstance instance = task.getInstance();
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();
        WorkflowNode node = task.getNode();
        UUID nodeOid = node.getNodeOid();
        WorkflowUser user = task.getUser();
        UUID userOid = user.getUserOid();
        Assert.notNull(entityType, "task.instance.entityType null");
        Assert.notNull(entityOid, "task.instance.entityOid null");
        Assert.notNull(nodeOid, "task.node.nodeOid null");
        Assert.notNull(userOid, "task.user.userOid null");

        ApprovalHistory approvalHistory = new ApprovalHistory();
        ZonedDateTime now = ZonedDateTime.now();
        approvalHistory.setEntityType(entityType);
        approvalHistory.setEntityOid(entityOid);

        if (WorkflowPassTaskAction.ACTION_NAME.equals(actionName)) {
            // 审批通过
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.APPROVAL_PASS.getId());
        } else if (WorkflowRejectTaskAction.ACTION_NAME.equals(actionName)) {
            // 审批驳回
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.APPROVAL.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.APPROVAL_REJECT.getId());
        } else {
            throw new IllegalArgumentException(String.format("actionName(%s) invalid", actionName));
        }

        approvalHistory.setRefApprovalChainId(task.getId());
        approvalHistory.setApprovalNodeOid(nodeOid);
        approvalHistory.setApprovalNodeName(node.getName());
        approvalHistory.setOperationDetail(remark);
        approvalHistory.setOperatorOid(userOid);
        approvalHistory.setCreatedDate(now);
        approvalHistory.setLastUpdatedDate(now);
        approvalHistoryService.insert(approvalHistory);
    }

    /**
     * 保存历史
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance
     * @param user
     * @param actionName
     * @param remark
     */
    public void saveHistory(WorkflowInstance instance, WorkflowUser user, String actionName, String remark) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(user, "user null");
        Assert.notNull(instance.getEntityType(), "instance.entityType null");
        Assert.notNull(instance.getEntityOid(), "instance.entityOid null");
        Assert.notNull(user.getUserOid(), "user.userOid null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();
        UUID userOid = user.getUserOid();

        ApprovalHistory approvalHistory = new ApprovalHistory();
        ZonedDateTime now = ZonedDateTime.now();
        approvalHistory.setEntityType(entityType);
        approvalHistory.setEntityOid(entityOid);

        if (WorkflowSubmitInstanceAction.ACTION_NAME.equals(actionName)) {
            // 提交工作流
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.SELF.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.SUBMIT_FOR_APPROVAL.getId());
        } else if (WorkflowWithdrawInstanceAction.ACTION_NAME.equals(actionName)) {
            // 撤回工作流
            approvalHistory.setOperationType(ApprovalOperationTypeEnum.SELF.getId());
            approvalHistory.setOperation(ApprovalOperationEnum.WITHDRAW.getId());
        } else {
            throw new IllegalArgumentException(String.format("actionName(%s) invalid", actionName));
        }

        approvalHistory.setOperationDetail(remark);
        approvalHistory.setOperatorOid(userOid);
        approvalHistory.setCreatedDate(now);
        approvalHistory.setLastUpdatedDate(now);
        approvalHistoryService.insert(approvalHistory);
    }

    /**
     * 加上写锁
     * @author mh.z
     * @date 2019/04/07
     *
     * @param instance
     */
    public void lockInstance(WorkflowInstance instance) {
        Assert.notNull(instance, "instance null");
        Assert.notNull(instance.getEntityType(), "instance.entityType null");
        Assert.notNull(instance.getEntityOid(), "instance.entityOid null");
        Integer entityType = instance.getEntityType();
        UUID entityOid = instance.getEntityOid();

        EntityWrapper<WorkFlowDocumentRef> wrapper = new EntityWrapper<WorkFlowDocumentRef>();
        wrapper.eq("document_oid", entityOid);
        wrapper.eq("document_category", entityType);
        workFlowDocumentRefService.updateForSet("version_number = version_number", wrapper);
    }

    private int doCountTasks(Integer entityType, UUID entityOid, UUID ruleApprovalNodeOid, String status) {
        Assert.notNull(entityType, "entityType null");
        Assert.notNull(entityOid, "entityOid null");
        Assert.notNull(status, "status null");

        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();

        if (WorkflowTask.STATUS_GENERAL.equals(status)) {
            wrapper.eq("current_flag", false);
            wrapper.eq("finish_flag", false);
        } else if (WorkflowTask.STATUS_APPROVAL.equals(status)) {
            wrapper.eq("current_flag", true);
        } else if (WorkflowTask.STATUS_APPROVED.equals(status)) {
            wrapper.eq("finish_flag", true);
        } else {
            throw new IllegalArgumentException(String.format("status(%s) invalid", status));
        }

        wrapper.eq(ruleApprovalNodeOid != null, "rule_approval_node_oid", ruleApprovalNodeOid);
        wrapper.eq("status", ApprovalChainStatusEnum.NORMAL.getId());
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType);

        int taskTotal = approvalChainService.selectCount(wrapper);
        return taskTotal;
    }

    private void doClearTask(Integer entityType, UUID entityOid, UUID ruleApprovalNodeOid, Boolean currentFlag) {
        Assert.notNull(entityType, "entityType null");
        Assert.notNull(entityOid, "entityOid null");

        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();
        wrapper.eq(ruleApprovalNodeOid != null, "rule_approval_node_oid", ruleApprovalNodeOid);
        wrapper.eq(currentFlag != null, "current_flag", currentFlag);
        wrapper.eq("status", ApprovalChainStatusEnum.NORMAL.getId());
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType);
        List<ApprovalChain> approvalChainList = approvalChainService.selectList(wrapper);

        if (approvalChainList.size() > 0) {
            for (ApprovalChain approvalChain : approvalChainList) {
                approvalChain.setStatus(ApprovalChainStatusEnum.INVALID.getId());
            }

            approvalChainService.saveAll(approvalChainList);
        }
    }

}
