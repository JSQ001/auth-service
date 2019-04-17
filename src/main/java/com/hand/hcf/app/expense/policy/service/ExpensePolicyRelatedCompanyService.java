package com.hand.hcf.app.expense.policy.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicyRelatedCompany;
import com.hand.hcf.app.expense.policy.persistence.ExpensePolicyRelatedCompanyMapper;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/2/1 10:30
 */
@Transactional
@Service
public class ExpensePolicyRelatedCompanyService extends BaseService<ExpensePolicyRelatedCompanyMapper, ExpensePolicyRelatedCompany> {

    /**
     * 根据费用政策id获得关联公司
     * @param expExpensePolicyId
     * @return
     */
    public List<ExpensePolicyRelatedCompany> getRelatedCompanyByPolicyId(Long expExpensePolicyId) {
        List<ExpensePolicyRelatedCompany> relatedCompanies = new ArrayList<ExpensePolicyRelatedCompany>();
        relatedCompanies = baseMapper.selectList(new EntityWrapper<ExpensePolicyRelatedCompany>()
                .eq("exp_expense_policy_id", expExpensePolicyId));
        return relatedCompanies;
    }
}
