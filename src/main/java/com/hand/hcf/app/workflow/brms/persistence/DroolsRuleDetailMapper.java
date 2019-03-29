package com.hand.hcf.app.workflow.brms.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.workflow.brms.domain.DroolsRuleDetail;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 */
@SuppressWarnings("ALL")
public interface DroolsRuleDetailMapper extends BaseMapper<DroolsRuleDetail> {

    DroolsRuleDetail getDroolsRuleDetailByOid(@Param("droolsRuleDetailOid") UUID droolsRuleDetailOid);

    List<DroolsRuleDetail> findByRuleConditionAndApprover(@Param("ruleConditionOid") UUID ruleConditionOid, @Param("ruleConditionApproverOid") UUID ruleConditionApproverOid);

    List<DroolsRuleDetail> findByApprover(@Param("ruleConditionApproverOid") UUID ruleConditionApproverOid);

    DroolsRuleDetail findByRuleConditionOid(UUID ruleConditionOid);

    DroolsRuleDetail findByDroolsRuleDetailOid(UUID droolsRuleDetailOid);

    List<DroolsRuleDetail> findByDroolsRuleDetailOidIn(Collection<UUID> droolsRuleDetailOids);
}
