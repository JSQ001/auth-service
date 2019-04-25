package com.hand.hcf.app.payment.externalApi;

import com.hand.hcf.app.common.co.ExpensePaymentScheduleCO;
import com.hand.hcf.app.expense.report.implement.web.ExpenseReportControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description: 调用费用模块三方接口
 * @version: 1.0
 * @author: qianjun.gong@hand-china.com
 * @date: 2019/4/10
 */

@Service
public class ExpenseService {

    @Autowired
    private ExpenseReportControllerImpl expenseReportClient;

    public List<ExpensePaymentScheduleCO> getExpPublicReportScheduleByIds(List<Long> ids){
        return expenseReportClient.getExpPublicReportScheduleByIds(ids);
    }

    public Map<Long,Integer> getExpPublicReportScheduleMapByHeaderId(Long headerId){
        return expenseReportClient.getExpPublicReportScheduleMapByHeaderId(headerId);
    }

}
