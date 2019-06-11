package com.hand.hcf.app.workflow.brms.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.core.ValidationError;
import com.hand.hcf.app.core.exception.core.ValidationException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.workflow.brms.domain.RuleConditionRelation;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import com.hand.hcf.app.workflow.brms.persistence.RuleConditionRelationMapper;
import com.hand.hcf.app.workflow.brms.util.cache.CacheNames;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@CacheConfig(cacheNames = CacheNames.BRMS_ENTITY_RULE_CONDITION_RELATION)
public class RuleConditionRelationService extends BaseService<RuleConditionRelationMapper,RuleConditionRelation> {



    @Transactional(readOnly = true)
    public List<RuleConditionRelation> findEntityOid(UUID entityOid) {
        return  selectList(new EntityWrapper<RuleConditionRelation>()
        .eq("entity_oid",entityOid)
        .eq("status",RuleApprovalEnum.VALID.getId()));
    }

    @Transactional(readOnly = true)
    public List<RuleConditionRelation> findEntityOidIn(List<UUID> entityOids) {
        if (CollectionUtils.isEmpty(entityOids)) {
            return null;
        }
        return  selectList(new EntityWrapper<RuleConditionRelation>()
                .in("entity_oid",entityOids)
                .eq("status",RuleApprovalEnum.VALID.getId()));
    }

    public RuleConditionRelation findByRuleConditionOidAndEntityType(UUID ruleConditionOid, Integer entityType) {
        List<RuleConditionRelation> list =  selectList(new EntityWrapper<RuleConditionRelation>()
                .eq("rule_condition_oid",ruleConditionOid)
                .eq("entity_type",entityType)
                .eq("status",RuleApprovalEnum.VALID.getId()));

        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() != 1) {
                throw new ValidationException(new ValidationError("approvalRule", "ruleConditionRelation size error , ruleConditionRelation:" + ruleConditionOid.toString() + " ,entityType:" + entityType));
            }
            return list.get(0);

        }
        return null;
    }

    public List<RuleConditionRelation> findByRuleConditionOidsAndEntityType(List<UUID> ruleConditionOids, Integer entityType) {
        if (CollectionUtils.isEmpty(ruleConditionOids)) {
            return null;
        }
        return selectList(new EntityWrapper<RuleConditionRelation>()
                .in("rule_condition_oid",ruleConditionOids)
                .eq("entity_type",entityType)
                .eq("status",RuleApprovalEnum.VALID.getId()));
    }

    public int deleteByEntityOid(UUID entityOid) {
        return deleteByEntityOid(Arrays.asList(entityOid));
    }

    public int deleteByEntityOid(List<UUID> entityOids) {
        List<RuleConditionRelation> ruleConditionRelations = selectList(new EntityWrapper<RuleConditionRelation>()
                .in("entity_oid",entityOids)
                .eq("status",RuleApprovalEnum.VALID.getId()));

        return updateRuleConditionRelationStatus(ruleConditionRelations, RuleApprovalEnum.DELETED.getId());
    }

    @CacheEvict(key="#ruleConditionOid.toString()")
    public int deleteByRuleConditionOid(UUID ruleConditionOid) {
        return deleteByRuleConditionOid(Arrays.asList(ruleConditionOid));
    }

    public int deleteByRuleConditionOid(List<UUID> ruleConditionOids) {
        List<RuleConditionRelation> ruleConditionRelations = selectList(new EntityWrapper<RuleConditionRelation>()
                .in("rule_condition_oid",ruleConditionOids)
                .eq("status",RuleApprovalEnum.VALID.getId()));
        return updateRuleConditionRelationStatus(ruleConditionRelations, RuleApprovalEnum.DELETED.getId());
    }

    private int updateRuleConditionRelationStatus(List<RuleConditionRelation> ruleConditionRelations, Integer status) {
        if (CollectionUtils.isEmpty(ruleConditionRelations)) {
            return 0;
        }
        ruleConditionRelations.stream().forEach(ruleConditionRelation -> {
            ruleConditionRelation.setStatus(status);
            conditionRelationSave(ruleConditionRelation);
        });
        return ruleConditionRelations.size();
    }

    @CacheEvict(key="#ruleConditionRelation.ruleConditionOid.toString()")
    public void conditionRelationSave(RuleConditionRelation ruleConditionRelation){
        insertOrUpdate(ruleConditionRelation);
    }

    @CacheEvict(key="#ruleSceneRelation.ruleConditionOid.toString()")
    public RuleConditionRelation save(RuleConditionRelation ruleSceneRelation) {
        ruleSceneRelation.setStatus(RuleApprovalEnum.VALID.getId());
        ruleSceneRelation.setCreatedDate(ZonedDateTime.now());
        insertOrUpdate(ruleSceneRelation);
        return ruleSceneRelation;
    }

    //@Cacheable(key = "#ruleConditionOid.toString()")
    public RuleConditionRelation findByRuleConditionOid(UUID ruleConditionOid) {
        if (ruleConditionOid == null) {
            return null;
        }
        return selectOne(new EntityWrapper<RuleConditionRelation>()
                .in("rule_condition_oid",ruleConditionOid)
        );
    }

    /**
     * @author mh.z
     * @date 2019/04/17
     *
     * @param entityType
     * @param entityOid
     * @return
     */
    public List<RuleConditionRelation> listByEntityTypeAndEntityOid(Integer entityType, UUID entityOid) {
        EntityWrapper<RuleConditionRelation> wrapper = new EntityWrapper<RuleConditionRelation>();
        wrapper.eq("status", RuleApprovalEnum.VALID.getId());
        wrapper.eq("entity_oid", entityOid);
        wrapper.eq("entity_type", entityType);

        List<RuleConditionRelation> ruleConditionRelationList = selectList(wrapper);
        return ruleConditionRelationList;
    }

}
