package com.hand.hcf.app.mdata.dimension.service;

import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.dimension.domain.DimensionItemGroupAssignItem;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionItemGroupAssignItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DimensionItemGroupAssignItemService extends BaseService<DimensionItemGroupAssignItemMapper, DimensionItemGroupAssignItem> {

    private final Logger log = LoggerFactory.getLogger(DimensionItemGroupAssignItemService.class);

}

