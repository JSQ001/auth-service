package com.hand.hcf.app.prepayment.workflow;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/10.
 * 用于监听工作流的事件，主要是用于工作流审批后，相应的更新单据的状态
 */

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.WorkflowMessageCO;
import com.hand.hcf.app.common.enums.DocumentOperationEnum;
import com.hand.hcf.app.common.event.WorkflowCustomRemoteEvent;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionHeadService;
import com.hand.hcf.core.security.domain.PrincipalLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkflowEventConsumer  {
    @Value("${spring.application.name:}")
    private  String applicationName;

    private static final Logger logger = LoggerFactory.getLogger(WorkflowEventConsumer.class);

    @Autowired
    private CashPaymentRequisitionHeadService cashPaymentRequisitionHeadService;

    /**
     * 该监听用于 工作流的撤回，审批拒绝(驳回)，审批通过 时 修改单据的相应状态
     * @param workflowCustomRemoteEvent
     */

    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public void workFlowConsumer(WorkflowCustomRemoteEvent workflowCustomRemoteEvent) {

        logger.info("预付款接收到工作流事件消息：" + workflowCustomRemoteEvent);
        WorkflowMessageCO workflowMessage = workflowCustomRemoteEvent.getWorkflowMessage();
       // UserBean userBean = workflowMessage.getUserBean();
        PrincipalLite userBean = workflowMessage.getUserBean();
        OrgInformationUtil.setAuthentication(userBean);
        //增加一层判断，只有目标服务为自己的服务时，且状态 为审批通过，撤回，驳回 时 才处理
        if((applicationName+":**").equalsIgnoreCase(workflowCustomRemoteEvent.getDestinationService()) && DocumentOperationEnum.APPROVAL.getId().compareTo( workflowMessage.getStatus()) <= 0){
                //更新单据的状态为审批通过/撤回/驳回
                cashPaymentRequisitionHeadService.updateDocumentStatus(workflowMessage.getStatus(),workflowMessage.getDocumentId(),workflowMessage.getApprovalText(),workflowMessage.getUserId());
        }
    }
}
