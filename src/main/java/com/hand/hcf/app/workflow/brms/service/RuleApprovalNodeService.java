package com.hand.hcf.app.workflow.brms.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalNode;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.persistence.RuleApprovalNodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
// comment by mh.z 20190226 频繁更新后缓存的数据不能及时更新（导致更新时报版本后不一致）
//@CacheConfig(cacheNames = CacheNames.BRMS_ENTITY_RULE_APPROVAL_NODE)
public class RuleApprovalNodeService extends BaseService<RuleApprovalNodeMapper, RuleApprovalNode> {

    /**
     * get one RuleApprovalNode by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    // comment by mh.z 20190226
    //@Cacheable(key = "#a0.toString()")
    public RuleApprovalNode getRuleApprovalNode(UUID ruleApprovalNodeOid) {
        return selectOne(new EntityWrapper<RuleApprovalNode>()
                .eq("rule_approval_node_oid", ruleApprovalNodeOid));
    }

    // comment by mh.z 20190226
    //@CacheEvict(key = "#a0.toString()")
    public int delete(UUID ruleApprovalNodeOid) {
        return updateRuleApprovalNodeStatus(Arrays.asList(ruleApprovalNodeOid), RuleApprovalEnum.DELETED.getId());
    }

    private int updateRuleApprovalNodeStatus(List<UUID> ruleApprovalNodeOids, Integer status) {
        List<RuleApprovalNode> ruleApprovalNodes = selectList(new EntityWrapper<RuleApprovalNode>()
                .in("rule_approval_node_oid", ruleApprovalNodeOids)
                .eq("status",  RuleApprovalEnum.VALID.getId()));
        if (CollectionUtils.isEmpty(ruleApprovalNodes)) {
            return 0;
        }
        ruleApprovalNodes.stream().forEach(ruleApprovalNode -> {
            ruleApprovalNode.setStatus(status);
            insertOrUpdate(ruleApprovalNode);
        });
        return ruleApprovalNodes.size();
    }

    // comment by mh.z 20190226
    //@CacheEvict(key = "#a0.ruleApprovalNodeOid")
    public RuleApprovalNode save(RuleApprovalNode ruleApprovalNode) {
        if (StringUtils.isEmpty(ruleApprovalNode.getRuleApprovalNodeOid())) {
            ruleApprovalNode.setRuleApprovalNodeOid(UUID.randomUUID());
            ruleApprovalNode.setStatus(RuleApprovalEnum.VALID.getId());
        }
        //default
        if (StringUtils.isEmpty(ruleApprovalNode.getNullableRule())) {
            ruleApprovalNode.setNullableRule(RuleApprovalEnum.RULE_NULLABLE_SKIP.getId());
        }
        if (StringUtils.isEmpty(ruleApprovalNode.getCountersignRule())) {
            ruleApprovalNode.setCountersignRule(RuleApprovalEnum.RULE_CONUTERSIGN_ALL.getId());
        }
        if (StringUtils.isEmpty(ruleApprovalNode.getRepeatRule())) {
            ruleApprovalNode.setRepeatRule(RuleApprovalEnum.RULE_REPEAR_SKIP.getId());
        }
        if (StringUtils.isEmpty(ruleApprovalNode.getSelfApprovalRule())) {
            /**
             * 修改自审批规则默认值
             *
             * 1.审批节点 默认替换为部门经理
             * 2.知会节点 默认不替换
             */
            if (RuleApprovalEnum.NODE_TYPE_APPROVAL == RuleApprovalEnum.parse(ruleApprovalNode.getTypeNumber())) {
                ruleApprovalNode.setSelfApprovalRule(RuleApprovalEnum.RULE_SELFAPPROVAL_SUPERIOR_MANAGER.getId());
            } else {
                ruleApprovalNode.setSelfApprovalRule(RuleApprovalEnum.RULE_SELFAPPROVAL_NOT_SKIP.getId());
            }
        }
        if (StringUtils.isEmpty(ruleApprovalNode.getInvoiceAllowUpdateType())) {
            ruleApprovalNode.setInvoiceAllowUpdateType(RuleApprovalEnum.NODE_INVOICE_ALLOW_UPDATE_TYPE_NOT_ALLOW.getId());
        }
        insertOrUpdate(ruleApprovalNode);
        return ruleApprovalNode;
    }

    /*public int updateSequence(UUID ruleApprovalNodeOid ,Integer sequenceNumber) {
        return ruleApprovalNodeRepository.updateSequence(ruleApprovalNodeOid,sequenceNumber);
    }*/

    public List<RuleApprovalNode> listByRuleApprovalChainOid(UUID ruleApprovalChainOid) {
        return  selectList(new EntityWrapper<RuleApprovalNode>()
                .eq("rule_approval_chain_oid", ruleApprovalChainOid)
                // modify by mh.z 20190222 必须按序号升序排，否则调整审批节点会造成顺序错乱
                //.eq("status", RuleApprovalEnum.VALID.getId()));
               .eq("status", RuleApprovalEnum.VALID.getId()).orderBy("sequence_number"));
                // END modify by mh.z
    }

    public List<RuleApprovalNode> listByRuleApprovalChainOidsIn(List<UUID> ruleApprovalChainOids) {
        if (CollectionUtils.isEmpty(ruleApprovalChainOids)) {
            return null;
        }
        return selectList(new EntityWrapper<RuleApprovalNode>()
                .in("rule_approval_chain_oid", ruleApprovalChainOids)
                .eq("status", RuleApprovalEnum.VALID.getId()).orderBy("sequence_number"));
    }


    public RuleApprovalNode getNextByRuleApprovalChainOid(UUID ruleApprovalChainOid, Integer lastSequence) {
        return selectOne(new EntityWrapper<RuleApprovalNode>()
                .eq("rule_approval_chain_oid", ruleApprovalChainOid)
                .eq("status", RuleApprovalEnum.VALID.getId())
                .gt("sequence_number",lastSequence)
                .orderBy("sequence_number")
        );

    }

    /**
     * 返回指定节点前可退回的节点
     * @author mh.z
     * @date 2019/04/15
     *
     * @param ruleApprovalNodeOid
     * @return
     */
    public List<RuleApprovalNode> listReturnNode(UUID ruleApprovalNodeOid) {
        if (ruleApprovalNodeOid == null) {
            throw new IllegalArgumentException("ruleApprovalNodeOid null");
        }

        RuleApprovalNode ruleApprovalNode = getRuleApprovalNode(ruleApprovalNodeOid);
        UUID ruleApprovalChainOid = ruleApprovalNode.getRuleApprovalChainOid();
        Integer sequenceNumber = ruleApprovalNode.getSequenceNumber();

        EntityWrapper<RuleApprovalNode> wrapper = new EntityWrapper<RuleApprovalNode>();
        wrapper.eq("type_number", RuleApprovalEnum.NODE_TYPE_APPROVAL.getId());
        wrapper.le("sequence_number", sequenceNumber);
        wrapper.eq("status",  RuleApprovalEnum.VALID.getId());
        wrapper.eq("rule_approval_chain_oid", ruleApprovalChainOid);
        wrapper.orderBy("sequence_number", true);

        List<RuleApprovalNode> ruleApprovalNodeList = selectList(wrapper);
        return ruleApprovalNodeList;
    }

}
