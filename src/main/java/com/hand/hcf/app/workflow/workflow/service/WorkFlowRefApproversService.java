package com.hand.hcf.app.workflow.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.workflow.workflow.domain.WorkFlowApprovers;
import com.hand.hcf.app.workflow.workflow.persistence.WorkFlowApproversMapper;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by dick on 2018/12/06.
 */
@Service
@Transactional
public class WorkFlowRefApproversService extends BaseService<WorkFlowApproversMapper, WorkFlowApprovers> {

    private static final Logger log = LoggerFactory.getLogger(WorkFlowRefApproversService.class);
    @Autowired
    private WorkFlowApproversMapper workFlowApproversMapper;


    /**
     * 批量保存
     */
    public List<WorkFlowApprovers> createBatchSysWorkflowApprovers(List<WorkFlowApprovers> workflowApproversList) {
        //校验
        if (workflowApproversList != null && workflowApproversList.size() > 0 ) {
            WorkFlowApprovers app = workflowApproversList.get(0);
            // 先删除
            this.deleteByRefIdAndNodeOid(app.getWorkFlowDocumentRefId(),app.getApproveNodeOid());
            // 再保存
            this.insertBatch(workflowApproversList,10);
            return workflowApproversList;
        }
        return null;
    }


    /**
     * @param workflowApprovers
     * @return
     */
    @Transactional
    public WorkFlowApprovers createSysWorkflowApprovers(WorkFlowApprovers workflowApprovers) {
        //校验
        if (workflowApprovers == null || workflowApprovers.getId() != null) {
            throw new BizException("ID不允许为空");
        }
        workflowApprovers.setCreatedBy(OrgInformationUtil.getCurrentUserId());
        ZonedDateTime now = ZonedDateTime.now();
        workflowApprovers.setCreatedDate(now);
        workflowApprovers.setLastUpdatedDate(now);
        workflowApprovers.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        workflowApprovers.setVersionNumber(1);
        workFlowApproversMapper.insert(workflowApprovers);
        return workflowApprovers;
    }

    /**
     * 更新workFlowDocumentRef
     *
     * @param workflowApprovers
     * @return
     */
    @Transactional
    public WorkFlowApprovers updateSysWorkflowApprovers(WorkFlowApprovers workflowApprovers) {
        //校验
        if (workflowApprovers == null || workflowApprovers.getId() == null) {
            throw new BizException("ID不允许为空");
        }
        //校验ID是否在数据库中存在
        WorkFlowApprovers rr = workFlowApproversMapper.selectById(workflowApprovers.getId());
        if (rr == null) {
            throw new BizException("数据不存在");
        }
        workflowApprovers.setCreatedBy(rr.getCreatedBy());
        workflowApprovers.setCreatedDate(rr.getCreatedDate());
        ZonedDateTime now = ZonedDateTime.now();
        workflowApprovers.setLastUpdatedDate(now);
        workflowApprovers.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        workflowApprovers.setVersionNumber(workflowApprovers.getVersionNumber() + 1);
        this.updateById(workflowApprovers);
        return workflowApprovers;
    }

    /**
     * @param id 删除
     * @return
     */
    @Transactional
    public void deleteWorkflowApprovers(Long id) {
        if (id != null) {
            this.deleteById(id);
        }
    }

    /**
     * @param workflowDocumentRefId 单据工作流审批关联表ID
     * @param approveNodeOid        ,审批节点Oid
     * @return
     */
    @Transactional
    public Integer deleteByRefIdAndNodeOid(Long workflowDocumentRefId, String approveNodeOid) {
        return workFlowApproversMapper.delete(new EntityWrapper<WorkFlowApprovers>()
                .eq("workflow_document_ref_id", workflowDocumentRefId)
                .eq("approve_node_oid", approveNodeOid));
    }

    /**
     * 单据工作流审批关联表ID,审批节点Oid，获取所有审批人
     *
     * @return
     */
    public List<WorkFlowApprovers> getWorkflowApproversByRefIdAndNodeOid(Long workflowDocumentRefId, String  approveNodeOid) {
        return workFlowApproversMapper.selectList(new EntityWrapper<WorkFlowApprovers>()
                .eq("workflow_document_ref_id", workflowDocumentRefId)
                .eq("approve_node_oid", approveNodeOid));
    }


}
