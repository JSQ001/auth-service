package com.hand.hcf.app.mdata.dimension.service;

import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItemAssignUserGroup;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionItemAssignUserGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DimensionItemAssignUserGroupService extends BaseService<DimensionItemAssignUserGroupMapper, DimensionItemAssignUserGroup> {

    @Autowired
    private DimensionItemAssignUserGroupMapper dimensionItemAssignUserGroupMapper;

}

