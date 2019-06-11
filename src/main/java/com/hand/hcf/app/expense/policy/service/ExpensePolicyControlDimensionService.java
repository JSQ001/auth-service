package com.hand.hcf.app.expense.policy.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicyControlDimension;
import com.hand.hcf.app.expense.policy.persistence.ExpensePolicyControlDimensionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/30 9:28
 */
@Service
@Transactional
public class ExpensePolicyControlDimensionService extends BaseService<ExpensePolicyControlDimensionMapper, ExpensePolicyControlDimension> {
    /**
     * 根据费用政策id获得控制维度
     * @param expExpensePolicyId
     * @return
     */
    public List<ExpensePolicyControlDimension> getControlDimensionByPolicyId(Long expExpensePolicyId) {
        List<ExpensePolicyControlDimension> controlDimensions = new ArrayList<ExpensePolicyControlDimension>();
        controlDimensions = baseMapper.selectList(new EntityWrapper<ExpensePolicyControlDimension>()
                .eq("exp_expense_policy_id", expExpensePolicyId));
        return controlDimensions;
    }
}
