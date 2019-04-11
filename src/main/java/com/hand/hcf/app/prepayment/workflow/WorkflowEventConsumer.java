package com.hand.hcf.app.prepayment.workflow;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/10.
 * 用于监听工作流的事件，主要是用于工作流审批后，相应的更新单据的状态
 */

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.ApprovalNotificationCO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.prepayment.service.CashPaymentRequisitionHeadService;
import com.hand.hcf.app.workflow.dto.ApprovalResultCO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkflowEventConsumer  {
    @Autowired
    private CashPaymentRequisitionHeadService cashPaymentRequisitionHeadService;

    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResultCO approve(@RequestBody ApprovalNotificationCO approvalNoticeCO) {
        Long documentId = approvalNoticeCO.getDocumentId();
        Integer approvalStatus = approvalNoticeCO.getDocumentStatus();
        Long userId = OrgInformationUtil.getCurrentUserId();

        //更新单据的状态为审批通过/撤回/驳回
        cashPaymentRequisitionHeadService.updateDocumentStatus(approvalStatus, documentId, "", userId);

        ApprovalResultCO approvalResultCO = new ApprovalResultCO();
        approvalResultCO.setSuccess(true);
        approvalResultCO.setStatus(approvalStatus);
        approvalResultCO.setError(null);
        return approvalResultCO;
    }
}
