package com.hand.hcf.app.expense.policy.service;

import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.expense.policy.domain.ExpensePolicyFieldProperty;
import com.hand.hcf.app.expense.policy.persistence.ExpensePolicyFieldPropertyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @version: 1.0
 * @author: zhanhua.cheng@hand-china.com
 * @date: 2019/2/1 11:07
 */
@Transactional
@Service
public class ExpensePolicyFieldPropertyService extends BaseService<ExpensePolicyFieldPropertyMapper, ExpensePolicyFieldProperty> {

}
