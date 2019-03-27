package com.hand.hcf.app.workflow.brms.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.workflow.brms.domain.RuleApprover;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.persistence.RuleApproverMapper;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RuleApproverService extends BaseService<RuleApproverMapper,RuleApprover> {

    /**
     * get one RuleApprover by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    public RuleApprover getRuleApprover(UUID ruleApproverOid) {

        return selectOne(new EntityWrapper<RuleApprover>()
        .eq("rule_approver_oid",ruleApproverOid));
    }

    public int delete(UUID ruleApproverOid) {
        return delete(Arrays.asList(ruleApproverOid));
    }

    public int delete(List<UUID> ruleApproverOids) {
        return updateRuleApproverStatus(ruleApproverOids, RuleApprovalEnum.DELETED.getId());
    }

    private int updateRuleApproverStatus(List<UUID> ruleApproverOids, Integer status) {
        List<RuleApprover> ruleApprovers = selectList(new EntityWrapper<RuleApprover>()
                .in("rule_approver_oid",ruleApproverOids)
        .eq("status",RuleApprovalEnum.VALID.getId())
        .orderBy("id"));

        if (CollectionUtils.isEmpty(ruleApprovers)) {
            return 0;
        }
        ruleApprovers.stream().forEach(ruleApprover -> {
            ruleApprover.setStatus(status);
            insertOrUpdate(ruleApprover);
        });
        return ruleApprovers.size();
    }

    public RuleApprover save(RuleApprover ruleApprover) {
        if (StringUtils.isEmpty(ruleApprover.getRuleApproverOid())) {
            ruleApprover.setRuleApproverOid(UUID.randomUUID());
            ruleApprover.setStatus(RuleApprovalEnum.VALID.getId());
        }
        ruleApprover.setCreatedDate(ZonedDateTime.now());
         insertOrUpdate(ruleApprover);
         return ruleApprover;
    }

    public List<RuleApprover> findByRuleApprovalNodeOid(UUID ruleApprovalNodeOid) {
        return selectList(new EntityWrapper<RuleApprover>()
                .eq("rule_approval_node_oid",ruleApprovalNodeOid)
                .eq("status",RuleApprovalEnum.VALID.getId())
        );
    }

    public RuleApprover findByDuplicateRuleApprover(RuleApprover ruleApprover) {
        return selectOne(new EntityWrapper<RuleApprover>()
                        .eq("name",ruleApprover.getName())
                        .eq("code",ruleApprover.getCode())
                        .eq("remark",ruleApprover.getRemark())
                        .eq("approver_type",ruleApprover.getApproverType())
                        );
    }

    public List<RuleApprover> findByRuleApprovalNodeOidsIn(List<UUID> ruleApprovalNodeOids) {
        if (CollectionUtils.isEmpty(ruleApprovalNodeOids)) {
            return null;
        }
        return selectList(new EntityWrapper<RuleApprover>()
                .in("rule_approval_node_oid",ruleApprovalNodeOids)
                .eq("status",RuleApprovalEnum.VALID.getId())
                .orderBy("id"));
    }
}
