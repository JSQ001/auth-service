package com.hand.hcf.app.expense.common.workflow;

/**
 * Created by maohui.zhuang@hand-china.com on 2018/12/24.
 * 用于监听工作流的事件，主要是用于工作流审批后，相应的更新单据的状态
 */

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.hand.hcf.app.common.co.ApprovalNotificationCO;
import com.hand.hcf.app.expense.adjust.service.ExpenseAdjustHeaderService;
import com.hand.hcf.app.expense.application.service.ApplicationHeaderService;
import com.hand.hcf.app.expense.common.domain.enums.ExpenseDocumentTypeEnum;
import com.hand.hcf.app.expense.report.service.ExpenseReportHeaderService;
import com.hand.hcf.app.expense.travel.service.TravelApplicationHeaderService;
import com.hand.hcf.app.workflow.dto.ApprovalResultCO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseWorkflowEventConsumer {
    @Autowired
    private ApplicationHeaderService applicationHeaderService;

    @Autowired
    private ExpenseAdjustHeaderService adjustHeaderService;

    @Autowired
    private TravelApplicationHeaderService travelApplicationHeaderService;

    @Autowired
    private ExpenseReportHeaderService expenseReportHeaderService;

    //@LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public ApprovalResultCO approve(@RequestBody ApprovalNotificationCO approvalNoticeCO) {
        Long documentId = approvalNoticeCO.getDocumentId();
        Integer documentCategory = approvalNoticeCO.getDocumentCategory();
        Integer approvalStatus = approvalNoticeCO.getDocumentStatus();

        //更新单据的状态为审批通过/撤回/驳回
        if (ExpenseDocumentTypeEnum.EXPENSE_ADJUST.getKey().equals(documentCategory)) {
            // 费用调整单
            adjustHeaderService.updateDocumentStatus(documentId, approvalStatus, "");
        } else if (ExpenseDocumentTypeEnum.EXP_REQUISITION.getKey().equals(documentCategory)) {
            // 费用申请单
            applicationHeaderService.updateDocumentStatus(documentId, approvalStatus, "");
        } else if (ExpenseDocumentTypeEnum.TRAVEL_APPLICATION.getKey().equals(documentCategory)) {
            // 差旅申请单
            travelApplicationHeaderService.updateDocumentStatus(documentId, approvalStatus, "");
        } else if (ExpenseDocumentTypeEnum.PUBLIC_REPORT.getKey().equals(documentCategory)) {
            // 报账单
            expenseReportHeaderService.updateDocumentStatus(documentId, approvalStatus, "");
        }

        ApprovalResultCO approvalResultCO = new ApprovalResultCO();
        approvalResultCO.setSuccess(true);
        approvalResultCO.setStatus(approvalStatus);
        approvalResultCO.setError(null);
        return approvalResultCO;
    }
}
