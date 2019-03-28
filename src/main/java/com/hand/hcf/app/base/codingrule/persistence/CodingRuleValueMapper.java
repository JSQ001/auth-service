package com.hand.hcf.app.base.codingrule.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleValue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dong.liu on 2017-08-24
 */
public interface CodingRuleValueMapper extends BaseMapper<CodingRuleValue> {
    List<CodingRuleValue> getBudgetCodingRuleValueByCond(
            @Param("documentTypeCode") String documentTypeCode,
            @Param("codingRuleId") Long codingRuleId,
            @Param("companyCode") String companyCode
    );
}
