package com.hand.hcf.app.workflow.approval.service;

import com.hand.hcf.app.workflow.brms.service.RuleApprovalNodeService;
import com.hand.hcf.app.workflow.brms.service.RuleService;
import com.hand.hcf.app.workflow.service.ApprovalChainService;
import com.hand.hcf.app.workflow.service.ApprovalFormService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 提供给工作流动作使用的服务类
 * @author mh.z
 * @date 2019/04/26
 */
@Getter
@Service
public class WorkflowActionService {
    @Autowired
    private WorkflowBaseService workflowBaseService;

    @Autowired
    private WorkflowInitNodeService workflowInitNodeService;

    @Autowired
    private RuleApprovalNodeService ruleApprovalNodeService;

    @Autowired
    private ApprovalChainService approvalChainService;

    @Autowired
    private ApprovalFormService approvalFormService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private WorkflowRepeatApproveService workflowRepeatApproveService;
}
