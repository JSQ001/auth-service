package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.dto.DroolsRuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalNodeDTO;
import com.hand.hcf.app.workflow.brms.dto.RuleApproverDTO;
import com.hand.hcf.app.workflow.brms.service.DroolsService;
import com.hand.hcf.app.workflow.constant.WorkflowConstants;
import com.hand.hcf.app.workflow.domain.WorkFlowDocumentRef;
import com.hand.hcf.app.workflow.dto.FormValueDTO;
import com.hand.hcf.app.workflow.service.DefaultWorkflowIntegrationServiceImpl;
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
        Assert.notNull(workFlowDocumentRef.getDocumentCategory(), "workFlowDocumentRef.documentCategory null");
        Assert.notNull(workFlowDocumentRef.getApplicantOid(), "workFlowDocumentRef.applicantOid null");
        Assert.notNull(ruleApprovalNode.getRuleApprovalNodeOid(), "ruleApprovalNode.ruleApprovalNodeOid null");

        Integer entityType = workFlowDocumentRef.getDocumentCategory();
        UUID applicantOid = workFlowDocumentRef.getApplicantOid();
        UUID ruleApprovalNodeOid = ruleApprovalNode.getRuleApprovalNodeOid();
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
