package com.hand.hcf.app.mdata.dimension.service;

import com.hand.hcf.app.mdata.dimension.domain.DimensionItemAssignDepartment;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionItemAssignDepartmentMapper;
import com.hand.hcf.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DimensionItemAssignDepartmentService extends BaseService<DimensionItemAssignDepartmentMapper, DimensionItemAssignDepartment> {

    @Autowired
    private DimensionItemAssignDepartmentMapper dimensionItemAssignDepartmentMapper;

}

