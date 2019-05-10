package com.hand.hcf.app.workflow.approval.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.mdata.implement.web.ContactControllerImpl;
import com.hand.hcf.app.workflow.approval.constant.MessageConstants;
import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.approval.enums.BackTypeEnum;
import com.hand.hcf.app.workflow.approval.implement.WorkflowMoveNodeAction;
import com.hand.hcf.app.workflow.brms.constant.RuleConstants;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.dto.ReturnNode;
import com.hand.hcf.app.workflow.brms.service.RuleApprovalNodeService;
import com.hand.hcf.app.workflow.brms.service.RuleService;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.ApprovalResDTO;
import com.hand.hcf.app.workflow.dto.backnode.BackNodesDTO;
import com.hand.hcf.app.workflow.dto.backnode.SendBackDTO;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;
import com.hand.hcf.app.workflow.enums.ApprovalOperationEnum;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 工作流驳回逻辑
 *
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowReturnService {
    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private WorkflowMainService workflowMainService;

    @Autowired
    private WorkflowActionService workflowActionService;

    @Autowired
    private ApprovalChainService approvalChainService;


    @Autowired
    private RuleApprovalNodeService ruleApprovalNodeService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private ContactControllerImpl contactClient;


    /**
     * 退回审批
     *
     * @param userOid
     * @param
     * @return
     * @author polus
     */
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResDTO returnWorkflow(UUID userOid, SendBackDTO dto) {
        ApprovalResDTO approvalResDTO = new ApprovalResDTO();
        approvalResDTO.setSuccessNum(0);
        approvalResDTO.setFailNum(0);

        // 退回审批
        doReturnWorkflow(userOid, dto);
        // 累加成功数
        approvalResDTO.setSuccessNum(approvalResDTO.getSuccessNum() + 1);


        return approvalResDTO;
    }

    /**
     * 处理退回审批
     *
     * @param dto
     * @author polus
     */
    protected void doReturnWorkflow(UUID userOid, SendBackDTO dto) {
        Assert.notNull(dto.getEntityType(), "entityType null");
        Assert.notNull(dto.getEntityOid(), "entityOid null");
        Assert.notNull(userOid, "userOid null");
        UUID entityOid = dto.getEntityOid();
        Integer entityType = dto.getEntityType();
        String remark = dto.getApprovalText();
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid, entityType);
        WorkflowInstance instance = new WorkflowInstance(workFlowDocumentRef);
        WorkflowUser user = new WorkflowUser(userOid);

        // 同个实例的提交/撤回/通过/驳回等操作不支持并发
        workflowBaseService.lockInstance(instance);

        // 查找任务
        WorkflowTask task = workflowBaseService.findTask(instance, user);
        if (task == null) {
            throw new BizException(MessageConstants.NOT_FIND_THE_TASK);
        }

        //根据退回节点新建任务
        RuleApprovalNode backApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(dto.getApprovalNodeOid());
        WorkflowNode backNode = new WorkflowNode(backApprovalNode, instance);


        // 清除跟该实例关联的退回节点前到当前节点之间的所有任务
        doClearTask(entityType, entityOid, task.getNode().getRuleApprovalNode().getSequenceNumber()
                , backApprovalNode.getSequenceNumber());


        WorkFlowDocumentRef doc=instance.getWorkFlowDocumentRef();
        if((dto.getBackTypeEnum()!=null && dto.getBackTypeEnum().equals(BackTypeEnum.CURRENT))
                || (backApprovalNode.getReturnRule().equals(BackTypeEnum.CURRENT.getValue()))) {
            doc.setJumpNodeId(backApprovalNode.getId());
            instance.setWorkFlowDocumentRef(doc);
        }

        WorkflowMoveNodeAction action = new WorkflowMoveNodeAction(workflowActionService, instance, backNode);
        workflowMainService.runWorkflow(instance, action);

        ContactCO contactCO = contactClient.getByUserOid(instance.getLastApproverOid());
        remark="退回至 "+instance.getLastNodeName()+" "+contactCO.getEmployeeCode()+"-"+contactCO.getFullName()+" "+remark;

        // 保存退回的历史
        workflowBaseService.saveHistory(task, ApprovalOperationEnum.APPROVAL_RETURN.getId().toString(), remark);


    }

    /**
     * 清除任务
     *
     * @param entityType
     * @param entityOid
     * @author polus
     * @date 2019/04/23
     */
    private void doClearTask(Integer entityType, UUID entityOid, Integer currentNode, Integer backNode) {
        Assert.notNull(entityType, "entityType null");
        Assert.notNull(entityOid, "entityOid null");
        Assert.notNull(currentNode, "currentNode null");
        Assert.notNull(backNode, "backNode null");

        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();
        wrapper.eq("status", ApprovalChainStatusEnum.NORMAL.getId());
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType)
                .ge("sequence_number", backNode)
                .le("sequence_number", currentNode);
        List<ApprovalChain> approvalChainList = approvalChainService.selectList(wrapper);

        if (approvalChainList.size() > 0) {
            for (ApprovalChain approvalChain : approvalChainList) {
                approvalChain.setStatus(ApprovalChainStatusEnum.INVALID.getId());
            }

            approvalChainService.saveAll(approvalChainList);
        }
    }

    /**
     * 审批控件退回节点
     *
     * @param entityOid
     * @author polus
     * @date 2019/04/21
     */
    public BackNodesDTO listApprovalNodeByBack(Integer entityType, UUID entityOid) {
        BackNodesDTO backNodesDTO = new BackNodesDTO();
        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.getRuleApprovalNode(
                UUID.fromString(workFlowDocumentRefService.getByDocumentOidAndDocumentCategory(entityOid, entityType)
                        .getApprovalNodeOid())
        );

        backNodesDTO.setBackFlag(ruleApprovalNode.getReturnRule());
        backNodesDTO.setAllowBackNode(ruleApprovalNode.getReturnFlag());
        backNodesDTO.setReturnType(ruleApprovalNode.getReturnType());


        if (ruleApprovalNode.getReturnFlag()) {
            List<ReturnNode> returnNodes = new ArrayList<>();
            List<RuleApprovalNode> ruleApprovalNodes = ruleApprovalNodeService.listReturnNode(ruleApprovalNode.getRuleApprovalNodeOid());

            ruleApprovalNodes.forEach(n -> {
                ReturnNode returnNode = new ReturnNode();
                returnNode.setRemark(n.getRemark());
                returnNode.setBackable(Boolean.TRUE);
                returnNode.setRuleApprovalNodeOid(n.getRuleApprovalNodeOid());
                boolean add = returnNodes.add(returnNode);
            });

            if (ruleApprovalNode.getReturnType().equals(RuleConstants.RULE_RETURN_CUSTOM_NODE)) {
                Set<UUID> selectNodes = ruleService.getRuleApprovalNodeReturnNodes(ruleApprovalNode)
                        .stream().map(n -> n.getRuleApprovalNodeOid()).collect(Collectors.toSet());
                returnNodes.forEach(n -> {
                    if (selectNodes.contains(n.getRuleApprovalNodeOid())) {
                        n.setBackable(Boolean.TRUE);
                    }else {
                        n.setBackable(Boolean.FALSE);
                    }
                });

            }
            backNodesDTO.setApprovalNodeDTOList(returnNodes);

        }
        return backNodesDTO;
    }


}
