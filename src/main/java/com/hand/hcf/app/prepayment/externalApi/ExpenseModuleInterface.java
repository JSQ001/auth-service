package com.hand.hcf.app.prepayment.externalApi;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.ApplicationAmountCO;
import com.hand.hcf.app.common.co.ApplicationTypeCO;
import com.hand.hcf.app.common.co.ApplicationTypeForOtherCO;
import com.hand.hcf.app.common.co.PrepaymentRequisitionReleaseCO;
import com.hand.hcf.app.expense.application.implement.web.ApplicationControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/3/14
 */
@Service
public class ExpenseModuleInterface {

    @Autowired
    private ApplicationControllerImpl expenseApplicationClient;

    /**
     * 根据所选范围查询账套下符合条件的费用申请单类型
     * @param applicationTypeForOtherCO
     * @param page
     * @return
     */
    public Page<ApplicationTypeCO> queryApplicationTypeByCond(ApplicationTypeForOtherCO applicationTypeForOtherCO, Page page){
        Page<ApplicationTypeCO> applicationTypeCOList = new Page<>();
        //jiu.zhao TODO
        /*Page<ApplicationTypeCO> result = expenseApplicationClient.queryApplicationTypeByCond(applicationTypeForOtherCO, page);
        applicationTypeCOList.setRecords(result.getRecords());
        applicationTypeCOList.setTotal(result.getTotal());*/
        return applicationTypeCOList;
    }

    public List<ApplicationAmountCO> getApplicationAmountById(Long applicationId) {
        return expenseApplicationClient.getApplicationAmountById(applicationId);
    }

    public void releasePrepaymentRequisitionRelease(Long prepaymentId) {
        expenseApplicationClient.releasePrepaymentRequisitionRelease(prepaymentId);
    }

    public void createPrepaymentRequisitionRelease(List<PrepaymentRequisitionReleaseCO> prepaymentRequisitionReleaseCO) {
        expenseApplicationClient.createPrepaymentRequisitionRelease(prepaymentRequisitionReleaseCO);
    }

}
