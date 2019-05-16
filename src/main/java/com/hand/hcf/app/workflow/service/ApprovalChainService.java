package com.hand.hcf.app.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.workflow.approval.dto.WorkflowTask;
import com.hand.hcf.app.workflow.brms.service.RuleService;
import com.hand.hcf.app.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.dto.chain.ApprovalChainDTO;
import com.hand.hcf.app.workflow.enums.ApprovalChainStatusEnum;
import com.hand.hcf.app.workflow.persistence.ApprovalChainMapper;
import com.hand.hcf.app.workflow.util.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Created by 魏建 on 2017/7/10.
 */
@Service
@Transactional
public class ApprovalChainService extends BaseService<ApprovalChainMapper, ApprovalChain> {
    @Autowired
    RuleService ruleService;

    /**
     * 根据任务id查找任务
     *
     * @param refApprovalChainId 任务id
     * @return 任务
     */
    public ApprovalChainDTO getApprovalChainByRefId(Long refApprovalChainId) {
        return baseMapper.getApprovalChainByRefId(refApprovalChainId);
    }

    /**
     * 根据单据和审批人查询任务
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @param approverOid 审批人oid
     * @return 任务
     */
    public ApprovalChain getApproverApprovalChain(Integer entityType, UUID entityOid, UUID approverOid) {
        return selectOne(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", ApprovalChainStatusEnum.NORMAL.getId())
                .eq("approver_oid", approverOid)
                .eq("current_flag", true)
        );
    }

    /**
     * 查找最近一次审批过的任务（同单据同审批人）
     * @author mh.z
     * @date 2019/04/25
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @param userOid 审批人oid
     * @param submitDate 单据实例的提交时间
     * @return 任务
     */
    public ApprovalChain getLastApprovalChain(Integer entityType, UUID entityOid, UUID userOid, ZonedDateTime submitDate) {
        EntityWrapper<ApprovalChain> entity = new EntityWrapper<ApprovalChain>();
        entity.eq("finish_flag", true);
        entity.eq("approver_oid", userOid);
        entity.eq("entity_oid", entityOid);
        entity.eq("entity_type", entityType);
        entity.ge("created_date", submitDate);
        entity.orderBy("last_updated_date", false);

        List<ApprovalChain> approvalChainList = selectList(entity);
        ApprovalChain approvalChain = null;

        if (approvalChainList.size() > 0) {
            approvalChain = approvalChainList.get(0);
        }

        return approvalChain;
    }

    /**
     * 返回下一个组编号
     * @version 1.0
     * @author mh.z
     * @date 2019/05/01
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @return 下一个组编号
     */
    public Integer getNextGroupNumber(Integer entityType, UUID entityOid) {
        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType);
        wrapper.orderBy("group_number", false);

        Page<ApprovalChain> page = selectPage(new Page(1, 1), wrapper);
        List<ApprovalChain> approvalChainList = page.getRecords();
        Integer groupNumber = null;

        if (approvalChainList.size() > 0) {
            ApprovalChain approvalChain = approvalChainList.get(0);
            groupNumber = approvalChain.getGroupNumber();
            groupNumber = groupNumber != null ? groupNumber + 1 : 0;
        } else {
            // 组编号从0开始
            groupNumber = 0;
        }

        return groupNumber;
    }

    /**
     * 查询待激活的任务
     * @version 1.0
     * @author mh.z
     * @date 2019/05/02
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @return 待激活的任务
     */
    public List<ApprovalChain> listNextApprovalChain(Integer entityType, UUID entityOid) {
        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();
        wrapper.eq("current_flag", false);
        wrapper.eq("finish_flag", false);
        wrapper.eq("status", ApprovalChainStatusEnum.NORMAL.getId());
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType);
        wrapper.orderBy("group_number", false);
        wrapper.orderBy("approval_order", true);

        Page<ApprovalChain> page = selectPage(new Page(1, 1), wrapper);
        List<ApprovalChain> approvalChainList = page.getRecords();

        if (approvalChainList.isEmpty()) {
            return approvalChainList;
        }

        ApprovalChain approvalChain = approvalChainList.get(0);
        Integer groupNumber = approvalChain.getGroupNumber();
        Integer approvalOrder = approvalChain.getApprovalOrder();

        wrapper = new EntityWrapper<ApprovalChain>();
        wrapper.eq("approval_order", approvalOrder);
        wrapper.eq("group_number", groupNumber);
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType);

        approvalChainList = selectList(wrapper);
        return approvalChainList;
    }

    /**
     * 查询同任务组的任务
     * @version 1.0
     * @author mh.z
     * @date 2019/05/02
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @param groupNumber 任务组编号
     * @param approvalOrder 审批顺序
     * @return 同任务组的任务
     */
    public List<ApprovalChain> listByGroupNumberAndApprovalOrder(Integer entityType, UUID entityOid,
                                                                 Integer groupNumber, Integer approvalOrder) {
        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();
        wrapper.eq("approval_order", approvalOrder);
        wrapper.eq("group_number", groupNumber);
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType);

        List<ApprovalChain> approvalChainList = selectList(wrapper);
        return approvalChainList;
    }

    /**
     * 根据类型和oid返回当前的approvalChain
     * author mh.z
     * @date 2019/04/17
     *
     * @param entityType
     * @param entityOid
     * @return
     */
    public List<ApprovalChain> listCurrrentByEntityTypeAndEntityOid(Integer entityType, UUID entityOid) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("current_flag", 1)
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", ApprovalChainStatusEnum.NORMAL.getId())
        );
    }

    /**
     * 保存任务
     *
     * @param approvalChain 任务
     */
    public void save(ApprovalChain approvalChain) {
        insertOrUpdate(approvalChain);
    }

    /**
     * 保存任务
     *
     * @param approvalChainList 任务列表
     * @return 任务列表
     */
    public List<ApprovalChain> saveAll(List<ApprovalChain> approvalChainList) {
        insertOrUpdateBatch(approvalChainList);
        return approvalChainList;
    }

    /**
     * 删除任务
     * @author mh.z
     * @date 2019/04/25
     *
     * @param entityType
     * @param entityOid
     */
    public void deleteByEntityTypeAndEntityOid(Integer entityType, UUID entityOid) {
        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();
        wrapper.eq("entity_type", entityType);
        wrapper.eq("entity_oid", entityOid);
        delete(wrapper);
    }

    /**
     * 统计任务数
     * @author mh.z
     * @date 2019/04/07
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @param ruleApprovalNodeOid  节点oid（null则统计整个实例满足条件的任务数）
     * @param approvalStatus 任务的审批状态
     * @return 任务数
     */
    public int countApprovalChain(Integer entityType, UUID entityOid, UUID ruleApprovalNodeOid, Integer approvalStatus) {
        CheckUtil.notNull(entityType, "entityType null");
        CheckUtil.notNull(entityOid, "entityOid null");
        CheckUtil.notNull(approvalStatus, "approvalStatus null");

        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();

        if (WorkflowTask.APPROVAL_STATUS_GENERAL.equals(approvalStatus)) {
            wrapper.eq("current_flag", false);
            wrapper.eq("finish_flag", false);
        } else if (WorkflowTask.APPROVAL_STATUS_APPROVAL.equals(approvalStatus)) {
            wrapper.eq("current_flag", true);
        } else if (WorkflowTask.APPROVAL_STATUS_APPROVED.equals(approvalStatus)) {
            wrapper.eq("finish_flag", true);
        } else {
            throw new IllegalArgumentException(String.format("approvalStatus(%s) invalid", approvalStatus));
        }

        wrapper.eq(ruleApprovalNodeOid != null, "rule_approval_node_oid", ruleApprovalNodeOid);
        wrapper.eq("status", ApprovalChainStatusEnum.NORMAL.getId());
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType);

        int taskTotal = selectCount(wrapper);
        return taskTotal;
    }

    /**
     * 清除任务
     * @author mh.z
     * @date 2019/04/07
     *
     * @param entityType 单据大类
     * @param entityOid 单据oid
     * @param ruleApprovalNodeOid 节点oid（null则清除整个实例满足条件的任务数）
     * @param currentFlag true当前任务，false不是当前任务，null不过滤该条件
     * @param finishFlag true已完成任务，false未完成任务，null不过滤该条件
     */
    public void clearApprovalChain(Integer entityType, UUID entityOid, UUID ruleApprovalNodeOid,
                                   Boolean currentFlag, Boolean finishFlag) {
        CheckUtil.notNull(entityType, "entityType null");
        CheckUtil.notNull(entityOid, "entityOid null");

        EntityWrapper<ApprovalChain> wrapper = new EntityWrapper<ApprovalChain>();
        wrapper.eq(ruleApprovalNodeOid != null, "rule_approval_node_oid", ruleApprovalNodeOid);
        wrapper.eq(currentFlag != null, "current_flag", currentFlag);
        wrapper.eq(finishFlag != null, "finish_flag", finishFlag);
        wrapper.eq("status", ApprovalChainStatusEnum.NORMAL.getId());
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType);
        List<ApprovalChain> approvalChainList = selectList(wrapper);

        if (approvalChainList.size() > 0) {
            for (ApprovalChain approvalChain : approvalChainList) {
                approvalChain.setStatus(ApprovalChainStatusEnum.INVALID.getId());
            }

            insertOrUpdateBatch(approvalChainList);
        }
    }

}
