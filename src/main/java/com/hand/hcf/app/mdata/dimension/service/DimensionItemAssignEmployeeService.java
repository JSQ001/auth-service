package com.hand.hcf.app.mdata.dimension.service;

import com.hand.hcf.app.mdata.dimension.domain.DimensionItemAssignEmployee;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionItemAssignEmployeeMapper;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DimensionItemAssignEmployeeService extends BaseService<DimensionItemAssignEmployeeMapper, DimensionItemAssignEmployee> {
}
