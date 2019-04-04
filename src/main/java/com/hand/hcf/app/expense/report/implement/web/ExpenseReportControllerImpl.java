package com.hand.hcf.app.expense.report.implement.web;

import com.hand.hcf.app.expense.report.service.ExpenseReportHeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.soap.Addressing;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/3/29 15:50
 * @version: 1.0.0
 */
@RestController
public class ExpenseReportControllerImpl {

    @Autowired
    private ExpenseReportHeaderService expenseReportHeaderService;

    /**
     * 更新报账单状态
     * @param headerId     申请单头ID
     * @param status       状态
     */
    public void updateDocumentStatus(@RequestParam("headerId") Long headerId,
                                     @RequestParam("status") Integer status,
                                     @RequestParam(value = "approvalText",required = false) String approvalText) {
        expenseReportHeaderService.updateDocumentStatus(headerId, status, approvalText);
    }
}
