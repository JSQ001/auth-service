package com.hand.hcf.app.expense.policy.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicyDynamicField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/1/29 11:44
 */
public interface ExpensePolicyDynamicFieldMapper extends BaseMapper<ExpensePolicyDynamicField> {
    List<ExpensePolicyDynamicField> selectExpensePolicyDynamicFieldByPolicyId(@Param("policyId") Long policyId);
}
