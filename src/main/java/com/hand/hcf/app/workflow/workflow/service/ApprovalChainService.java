package com.hand.hcf.app.workflow.workflow.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.workflow.workflow.domain.ApprovalChain;
import com.hand.hcf.app.workflow.workflow.dto.ApprovalChainDTO;
import com.hand.hcf.app.workflow.workflow.persistence.ApprovalChainMapper;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by 魏建 on 2017/7/10.
 */
@Service
@Transactional
public class ApprovalChainService extends BaseService<ApprovalChainMapper, ApprovalChain> {

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndFinishFlagAndCountersignType(Integer entityType, UUID entityOid, Integer status, boolean currentFlag, boolean finishFlag, Integer countersignType) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .eq("current_flag", currentFlag)
                .eq("finish_flag", finishFlag)
                .eq("countersign_type", countersignType));
    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndFinishFlagAndCountersignTypeNotNullAndSequence(Integer entityType, UUID entityOid, Integer status, boolean currentFlag, boolean finishFlag, Integer sequence) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .eq("current_flag", currentFlag)
                .eq("finish_flag", finishFlag)
                .eq("sequence_number", sequence));

    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndCountersignTypeAndApproverOidNotAndSequence(Integer entityType, UUID entityOid, Integer status, Integer ruleConutersignAny, UUID approverOid, Integer sequence) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .eq("countersign_type", ruleConutersignAny)
                .eq("sequence_number", sequence)
                .ne("approver_oid", approverOid));
    }

    /**
     * 当前的一组加签审批链审批完,查询下一组加签审批链
     */
    public List<ApprovalChain> listNextSequenceApprovalChain(Integer entityType, UUID entityOid, Integer status, boolean currentFlag, boolean finishFlag) {
        //先查询下一组未完成的加签审批链的sequence
        ApprovalChain approvalChain = selectOne(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .eq("current_flag", currentFlag)
                .eq("finish_flag", finishFlag)
                .isNotNull("countersign_type")
                .orderBy("sequence_number", true));

        if (approvalChain != null) {
            //在查询这组加签审批链
            return selectList(new EntityWrapper<ApprovalChain>()
                            .eq("entity_type", entityType)
                            .eq("entity_oid", entityOid)
                            .eq("status", status)
                            .eq("current_flag", currentFlag)
                            .eq("finish_flag", finishFlag)
                            .isNotNull("countersign_type")
                            .eq("sequence_number", approvalChain.getSequence())
                            .orderBy("sequence_number", true));
        }
        return null;
    }

    public ApprovalChain getTop1ByEntityTypeAndEntityOidAndStatusOrderBySequenceDesc(Integer entityType, UUID entityOid, Integer status) {
        return  selectOne(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .orderBy("sequence_number", false));

    }


    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndCountersignTypeNotNullAndSequenceGreaterThan(Integer entityType, UUID entityOid, Integer status, Integer sequence) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .isNotNull("countersign_type")
                .gt("sequence_number", sequence));
    }

    public ApprovalChain getTop1ByEntityTypeAndEntityOidAndCurrentFlagOrderByLastModifiedDateDesc(Integer entityType, UUID entityOid, boolean currentFlag, Integer status) {
        return  selectOne(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("current_flag", currentFlag)
                .eq("status", status)
                .orderBy("last_updated_date", false));
    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidInAndStatus(Integer entityType, List<UUID> entityOids, Integer status) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .in("entity_oid", entityOids)
                .eq("status", status)
        );
    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidIn(Integer entityType, List<UUID> entityOids) {
         return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .in("entity_oid", entityOids)
        );
    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatus(Integer entityType, UUID entityOid, Integer status) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
        );
    }

    public List<ApprovalChain> saveAll(List<ApprovalChain> approvalChainList) {
        insertOrUpdateBatch(approvalChainList);
        return approvalChainList;
    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndSourceApprovalChainId(Integer entityType, UUID entityOid, Integer id, Long sourceApprovalChainId) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", id)
                .eq("source_approval_chain_id",sourceApprovalChainId)
        );
    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndIsAddSignTrue(Integer entityType, UUID entityOid, Integer status) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .eq("add_sign",true)
        );
    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndApproverOidInAndCountersignTypeNotNullAndIsNoticeIsFalse(Integer entityType, UUID entityOid, Integer status, List<UUID> approvalUserOids) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .in("approver_oid",approvalUserOids)
                .isNotNull("countersign_type")
                .eq("noticed",false)
        );
    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndApproverOidInAndIsNoticeIsFalse(Integer entityType, UUID entityOid, Integer status, List<UUID> approvalUserOids) {
           return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .in("approver_oid",approvalUserOids)
                .eq("noticed",false)
        );

    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndApproverOidInAndCountersignTypeIsNullAndIsNoticeIsFalse(Integer entityType, UUID entityOid, Integer status, List<UUID> approvalUserOids) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .in("approver_oid",approvalUserOids)
                .isNull("countersign_type")
                .eq("noticed",false)
        );
    }

    //查询当前审批的ApprovalChain
    public ApprovalChain getByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndApproverOid(Integer entityType, UUID entityOid, Integer status, boolean currentFlag, UUID approverOid) {
        return selectOne(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .eq("approver_oid",approverOid)
                .eq("current_flag",currentFlag)
        );
    }

    public List<ApprovalChain> listByEntityTypeAndEntityOidAndStatusAndCurrentFlagAndApportionmentFlagFalseAndCountersignRule(Integer entityType, UUID entityOid, Integer status, boolean currentFlag, Integer countersignRule) {
        return selectList(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .eq("apportionment_flag",false)
                .eq("current_flag",currentFlag)
                .eq("countersign_rule",countersignRule)
        );
    }

    public ApprovalChain getTop1ByEntityTypeAndEntityOidAndStatusAndCurrentFlagOrderBySequenceDesc(Integer entityType, UUID entityOid, Integer status, boolean currentFlag) {
        return selectOne(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .eq("current_flag",currentFlag)
                .orderBy("sequence_number",false)
        );
    }

    public ApprovalChain getTop1ByEntityTypeAndEntityOidAndStatusAndFinishFlagIsFalseAndSequenceGreaterThanOrderBySequenceAsc(Integer entityType, UUID entityOid, Integer status, Integer sequence) {
        return selectOne(new EntityWrapper<ApprovalChain>()
                .eq("entity_type", entityType)
                .eq("entity_oid", entityOid)
                .eq("status", status)
                .eq("finish_flag",false)
                .gt("sequence_number",sequence)
                .orderBy("sequence_number",true)
        );
    }

    public void save(ApprovalChain approvalChain) {
        insertOrUpdate(approvalChain);
    }



    public ApprovalChainDTO getApprovalChainByRefId(Long refApprovalChainId) {
        return baseMapper.getApprovalChainByRefId(refApprovalChainId);
    }

    /**
     * 通过chainID 获取审批链
     *
     * @param approvalChainId
     * @return
     */
    public ApprovalChain getApprovalChainById(Long approvalChainId) {
        return baseMapper.getApprovalChainById(approvalChainId);
    }

    public void updateAllFinshTrueById(Long id) {
        baseMapper.updateAllFinshTrueById(id);
    }
}
