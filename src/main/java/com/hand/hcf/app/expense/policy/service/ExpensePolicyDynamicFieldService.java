package com.hand.hcf.app.expense.policy.service;

import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicyDynamicField;
import com.hand.hcf.app.expense.policy.persistence.ExpensePolicyDynamicFieldMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/2/2 9:43
 */
@Transactional
@Service
public class ExpensePolicyDynamicFieldService extends BaseService<ExpensePolicyDynamicFieldMapper, ExpensePolicyDynamicField> {
    /**
     * 根据费用政策id获得动态字段
     * @param expExpensePolicyId
     * @return
     */
    public List<ExpensePolicyDynamicField> getDynamicFieldByPolicyId(Long expExpensePolicyId) {
        return  baseMapper.selectExpensePolicyDynamicFieldByPolicyId(expExpensePolicyId);
    }
}
