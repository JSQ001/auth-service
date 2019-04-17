package com.hand.hcf.app.expense.travel.service;

import com.hand.hcf.app.expense.travel.domain.TravelApplicationTypeAssignDepartment;
import com.hand.hcf.app.expense.travel.persistence.TravelApplicationTypeAssignDepartmentMapper;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shouting.cheng
 * @date 2019/3/4
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TravelApplicationTypeAssignDepartmentService extends BaseService<TravelApplicationTypeAssignDepartmentMapper, TravelApplicationTypeAssignDepartment> {
}
