package com.hand.hcf.app.workflow.brms.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.workflow.brms.domain.DroolsRuleDetailResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 */
public interface DroolsRuleDetailResultMapper extends BaseMapper<DroolsRuleDetailResult> {

    /**
     * 根据DroolsResultOid查找DroolsResultDetail
     *
     * @param droolsRuleDetailOid
     * @return
     */
    List<DroolsRuleDetailResult> findDroolResultDetailResult(@Param(value = "droolsRuleDetailOid") UUID droolsRuleDetailOid);

    DroolsRuleDetailResult findByDroolsRuleDetailResultOid( UUID doorlsRuleDetailResultOids);
}
