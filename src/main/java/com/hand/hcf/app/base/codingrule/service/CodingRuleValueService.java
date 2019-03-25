package com.hand.hcf.app.base.codingrule.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleValue;
import com.hand.hcf.app.base.codingrule.persistence.CodingRuleValueMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CodingRuleValueService extends ServiceImpl<CodingRuleValueMapper, CodingRuleValue> {


    /**
     * 新增一个编码规则值
     *
     * @param codingRuleValue
     * @return
     */
    @Transactional
    public CodingRuleValue insertOrUpdateCodingRuleValue(CodingRuleValue codingRuleValue) {
        if (codingRuleValue.getId() != null) {
            this.insert(codingRuleValue);
        } else {
            this.updateById(codingRuleValue);
        }
        return codingRuleValue;
    }


    /**
     * 获取编码规则下的编码规则值
     *
     * @param id 编码规则id
     * @return
     */
    public List<CodingRuleValue> getCodingRuleByCodingRuleId(Long id) {
        return baseMapper.selectList(new EntityWrapper<CodingRuleValue>()
            .where("deleted = false")
            .eq("coding_rule_id", id)
            .orderBy("id")
        );
    }

    /**
     * 获取编码规则值
     *
     * @param documentTypeCode 单据类型代码
     * @param codingRuleId     编码规则id
     * @return
     */
    public List<CodingRuleValue> getBudgetCodingRuleValueByCond(
        String documentTypeCode,
        String companyCode,
        Long codingRuleId) {
        return baseMapper.getBudgetCodingRuleValueByCond(documentTypeCode, codingRuleId, companyCode);
    }
}
