package com.hand.hcf.app.base.codingrule.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.base.codingrule.domain.CodingRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dong.liu on 2017-08-23
 */
public interface CodingRuleMapper extends BaseMapper<CodingRule> {
    public List<CodingRule> getBudgetCodingRuleByCond(@Param("ew") Wrapper<CodingRule> wrapper);

    public void updateDetailSynthesis(@Param("codingRule") CodingRule codingRule);
}
