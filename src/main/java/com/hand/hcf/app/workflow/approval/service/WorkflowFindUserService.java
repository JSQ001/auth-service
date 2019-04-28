package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.workflow.approval.dto.WorkflowInstance;
import com.hand.hcf.app.workflow.approval.dto.WorkflowNode;
import com.hand.hcf.app.workflow.approval.dto.WorkflowUser;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.dto.DroolsRuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApproverDTO;
import com.hand.hcf.app.workflow.brms.service.DroolsService;
import com.hand.hcf.app.workflow.brms.service.RuleApprovalNodeService;
import com.hand.hcf.app.workflow.constant.WorkflowConstants;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.AssembleBrmsParamsRespDTO;
import com.hand.hcf.app.workflow.dto.FormValueDTO;
import com.hand.hcf.app.workflow.service.DefaultWorkflowIntegrationServiceImpl;
import com.hand.hcf.app.workflow.service.WorkFlowDocumentRefService;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 工作流找人逻辑
 * @author mh.z
 * @date 2019/04/07
 */
@Service
public class WorkflowFindUserService {
    @Autowired
    private DefaultWorkflowIntegrationServiceImpl defaultWorkflowIntegrationService;

    @Autowired
    private DroolsService droolsService;

    @Autowired
    private RuleApprovalNodeService ruleApprovalNodeService;

    @Autowired
    private WorkFlowDocumentRefService workFlowDocumentRefService;

    /**
     * 返回满足审批条件的用户
     * @author mh.z
     * @date 2019/04/07
     *
     * @param node 节点
     * @return 满足审批条件的用户
     */
    public List<WorkflowUser> findUsers(WorkflowNode node) {
        CheckUtil.notNull(node, "node null");
        Long nodeId = CheckUtil.notNull(node.getId(), "node.id null");
        WorkflowInstance instance = CheckUtil.notNull(node.getInstance(), "node.instance null");

        RuleApprovalNode ruleApprovalNode = ruleApprovalNodeService.selectById(nodeId);
        Long instanceId = instance.getId();
        WorkFlowDocumentRef workFlowDocumentRef = workFlowDocumentRefService.selectById(instanceId);

        AssembleBrmsParamsRespDTO assembleBrmsParamsRespDTO = defaultWorkflowIntegrationService
                .assembleNewBrmsParams(workFlowDocumentRef);
        List<FormValueDTO> formValueDTOList = assembleBrmsParamsRespDTO.getFormValueDTOS();
        Map<String, Object> entityData = assembleBrmsParamsRespDTO.getEntityData();
        // 获取满足条件的规则审批人
        List<RuleApproverDTO> ruleUserList = getRuleUserList(
                workFlowDocumentRef, ruleApprovalNode, formValueDTOList, entityData);
        // 根据规则审批人找到具体用户
        List<UUID> userOidList = getUserOidList(
                workFlowDocumentRef, ruleApprovalNode, ruleUserList, formValueDTOList);

        List<WorkflowUser> userList = new ArrayList<WorkflowUser>();
        WorkflowUser user = null;
        for (UUID userOid : userOidList) {
            user = new WorkflowUser(userOid);
            userList.add(user);
        }

        return userList;
    }

    /**
     *  返回根据规则审批人找到的具体用户
     *
     * @param workFlowDocumentRef
     * @param ruleApprovalNode
     * @param ruleApproverDTOList
     * @param formValueDTOList
     * @return
     */
    protected List<UUID> getUserOidList(WorkFlowDocumentRef workFlowDocumentRef, RuleApprovalNode ruleApprovalNode,
                                        List<RuleApproverDTO> ruleApproverDTOList, List<FormValueDTO> formValueDTOList) {
        Assert.notNull(workFlowDocumentRef, "workFlowDocumentRef null");
        Assert.notNull(ruleApprovalNode, "ruleApprovalNode null");
        Assert.notNull(ruleApproverDTOList, "ruleApproverDTOList null");
        Assert.notNull(formValueDTOList, "formValueDTOList null");
        Assert.notNull(workFlowDocumentRef.getApplicantOid(), "workFlowDocumentRef.applicantOid null");

        UUID applicantOid = workFlowDocumentRef.getApplicantOid();
        Integer selfApprovalRule = ruleApprovalNode.getSelfApprovalRule();
        RuleApprovalNodeDTO ruleApprovalNodeDTO = new RuleApprovalNodeDTO();
        ruleApprovalNodeDTO.setSelfApprovalRule(selfApprovalRule);
        DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO = new DroolsRuleApprovalNodeDTO();

        //
        Map<String, Set<UUID>> userOidSetMap = defaultWorkflowIntegrationService.getApproverUserOids(
                ruleApproverDTOList, formValueDTOList, applicantOid, droolsRuleApprovalNodeDTO);
        //
        Set<UUID> userOidSet = userOidSetMap.get(WorkflowConstants.COUNTERSIGN_APPROVER);
        //
        List<UUID> userOidList = defaultWorkflowIntegrationService.buildApproverByRule(
                ruleApprovalNodeDTO, userOidSetMap, applicantOid, new ArrayList<UUID>(userOidSet));

        return userOidList;
    }

    /**
     * 返回满足条件的规则审批人
     *
     * @param workFlowDocumentRef
     * @param ruleApprovalNode
     * @param formValueDTOList
     * @param entityData
     * @return
     */
    protected List<RuleApproverDTO> getRuleUserList(WorkFlowDocumentRef workFlowDocumentRef, RuleApprovalNode ruleApprovalNode,
                                                    List<FormValueDTO> formValueDTOList, Map<String, Object> entityData) {
        Assert.notNull(workFlowDocumentRef, "workFlowDocumentRef null");
        Assert.notNull(ruleApprovalNode, "ruleApprovalNode null");
        Assert.notNull(formValueDTOList, "formValueDTOList null");
        Assert.notNull(entityData, "entityData null");
        Assert.notNull(workFlowDocumentRef.getDocumentCategory(), "workFlowDocumentRef.entityType null");
        Assert.notNull(workFlowDocumentRef.getDocumentOid(), "workFlowDocumentRef.entityOid null");
        Assert.notNull(workFlowDocumentRef.getFormOid(), "workFlowDocumentRef.formOid null");
        Assert.notNull(workFlowDocumentRef.getApplicantOid(), "workFlowDocumentRef.applicantOid null");
        Assert.notNull(ruleApprovalNode.getRuleApprovalNodeOid(), "ruleApprovalNode.ruleApprovalNodeOid null");

        Integer entityType = workFlowDocumentRef.getDocumentCategory();
        UUID entityOid = workFlowDocumentRef.getDocumentOid();
        UUID formOid = workFlowDocumentRef.getFormOid();
        UUID applicantOid = workFlowDocumentRef.getApplicantOid();
        UUID ruleApprovalNodeOid = ruleApprovalNode.getRuleApprovalNodeOid();

        DroolsRuleApprovalNodeDTO droolsRuleApprovalNodeDTO = new DroolsRuleApprovalNodeDTO();
        droolsRuleApprovalNodeDTO.setRuleApprovalNodeOid(ruleApprovalNodeOid);
        droolsRuleApprovalNodeDTO.setFormValues(formValueDTOList);
        droolsRuleApprovalNodeDTO.setApplicantOid(applicantOid);
        droolsRuleApprovalNodeDTO.setFormOid(formOid);
        droolsRuleApprovalNodeDTO.setEntityType(entityType);
        droolsRuleApprovalNodeDTO.setEntityOid(entityOid);
        droolsRuleApprovalNodeDTO.setEntityData(entityData);

        // 执行drool规则引擎
        RuleApprovalNodeDTO ruleApprovalNodeDTO = droolsService.invokeDroolsRuleForNormalApprovalNode(droolsRuleApprovalNodeDTO);
        List<RuleApproverDTO> ruleApproverDTOList = ruleApprovalNodeDTO.getRuleApprovers();
        return ruleApproverDTOList;
    }

}
