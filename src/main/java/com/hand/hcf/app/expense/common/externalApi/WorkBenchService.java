package com.hand.hcf.app.expense.common.externalApi;

import com.hand.hcf.app.apply.workbench.BusinessDataCO;
import com.hand.hcf.app.apply.workbench.WorkbenchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkBenchService {

    @Autowired
    WorkbenchClient workbenchClient;

    /**
     * 报账单对接工作台接口
     * @param businessDataCO
     */
    public void pushReportDataToWorkBranch(BusinessDataCO businessDataCO){
        workbenchClient.pushData(businessDataCO);
    }
}
