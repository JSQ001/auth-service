package com.hand.hcf.app.workflow.brms.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.workflow.brms.domain.RuleApprovalChain;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.persistence.RuleApprovalChainMapper;
import com.hand.hcf.app.workflow.brms.util.cache.CacheNames;
import com.hand.hcf.core.service.BaseService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@CacheConfig(cacheNames = CacheNames.BRMS_ENTITY_RULE_APPROVAL_CHAIN)
public class RuleApprovalChainService extends BaseService<RuleApprovalChainMapper, RuleApprovalChain> {


    @Transactional(readOnly = true)
    public Page<RuleApprovalChain> listAllWithInvalid(Page page) {
        Page<RuleApprovalChain> result = selectPage(page);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<RuleApprovalChain> listAll(Page page) {
        Page<RuleApprovalChain> result = selectPage(page, new EntityWrapper<RuleApprovalChain>()
                .eq("status", RuleApprovalEnum.VALID.getId()));
        return result;
    }

    @Transactional(readOnly = true)
    public Page<RuleApprovalChain> pageAllByCompanyOid(UUID companyOid, Page page) {
        Page<RuleApprovalChain> result = selectPage(page, new EntityWrapper<RuleApprovalChain>()
                .eq("company_oid", companyOid)
                .eq("status", RuleApprovalEnum.VALID.getId()));
        return result;
    }

    @Transactional(readOnly = true)
    public List<RuleApprovalChain> listAllByCompanyOid(UUID companyOid) {
        return selectList(new EntityWrapper<RuleApprovalChain>()
                .eq("company_oid", companyOid)
                .eq("status", RuleApprovalEnum.VALID.getId()));
    }

    public Optional<RuleApprovalChain> getByRuleApprovalChainOid(UUID ruleApprovalChainOid) {
        return Optional.ofNullable(selectOne(new EntityWrapper<RuleApprovalChain>()
                .eq("rule_approval_chain_oid", ruleApprovalChainOid)));
    }

    /**
     * get one RuleApprovalChain by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    @Cacheable(key = "#ruleApprovalChainOid.toString()")
    public RuleApprovalChain getByOid(UUID ruleApprovalChainOid) {
        Optional<RuleApprovalChain> opt = getByRuleApprovalChainOid(ruleApprovalChainOid);
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public RuleApprovalChain getByRuleSceneOid(UUID ruleSceneOid) {
        Optional<RuleApprovalChain> opt = Optional.ofNullable(selectOne(new EntityWrapper<RuleApprovalChain>()
                .eq("rule_scene_oid", ruleSceneOid)));
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<RuleApprovalChain> getByRuleSceneOids(List<UUID> ruleSceneOids) {
        if (CollectionUtils.isEmpty(ruleSceneOids)) {
            return null;
        }
        return selectList(new EntityWrapper<RuleApprovalChain>()
                .in("rule_scene_oid", ruleSceneOids));
    }

    @CacheEvict(key = "#ruleApprovalChainOid.toString()")
    public int delete(UUID ruleApprovalChainOid) {
        Optional<RuleApprovalChain> opt = getByRuleApprovalChainOid(ruleApprovalChainOid);
        if (opt.isPresent() && opt.get().getStatus().equals(RuleApprovalEnum.VALID.getId())) {
            RuleApprovalChain ruleApprovalChain = opt.get();
            ruleApprovalChain.setStatus(RuleApprovalEnum.INVALID.getId());
            ruleApprovalChain.setCreatedDate(ZonedDateTime.now());
            insertOrUpdate(ruleApprovalChain);
            return 1;
        }
        return 0;
    }

    @CacheEvict(key = "#ruleApprovalChain.ruleApprovalChainOid.toString()")
    public RuleApprovalChain save(RuleApprovalChain ruleApprovalChain) {
        if (StringUtils.isEmpty(ruleApprovalChain.getRuleApprovalChainOid())) {
            ruleApprovalChain.setRuleApprovalChainOid(UUID.randomUUID());
            ruleApprovalChain.setStatus(RuleApprovalEnum.VALID.getId());
        }
        ruleApprovalChain.setCreatedDate(ZonedDateTime.now());
        insertOrUpdate(ruleApprovalChain);
        return ruleApprovalChain;
    }
}
