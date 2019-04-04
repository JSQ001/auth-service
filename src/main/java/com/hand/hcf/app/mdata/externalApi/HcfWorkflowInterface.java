package com.hand.hcf.app.mdata.externalApi;

import com.hand.hcf.app.common.co.ApprovalFormCO;
import com.hand.hcf.app.workflow.implement.web.WorkflowControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 工作流三方接口
 * @author shouting.cheng
 * @date 2019/1/22
 */
@Service
public class HcfWorkflowInterface {

    @Autowired
    private WorkflowControllerImpl workflowClient;

    /**
     * 根据单据类型id获取单据类型名称
     * @param id
     * @return
     */
    public ApprovalFormCO getFormNameByFormId(Long id) {

        List<ApprovalFormCO> approvalFormCOList = workflowClient.listApprovalFormsByIds(Arrays.asList(id));
        if (approvalFormCOList.size() != 0) {
            return approvalFormCOList.get(0);
        } else {
            return null;
        }
    }

    public List<ApprovalFormCO> listApprovalFormByFormIds(List<Long> ids) {
        return workflowClient.listApprovalFormsByIds(ids);
    }
}
