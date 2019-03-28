package com.hand.hcf.app.mdata.period.service;

import com.hand.hcf.app.mdata.period.domain.PeriodStatus;
import com.hand.hcf.app.mdata.period.persistence.PeriodStatusMapper;
import com.hand.hcf.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodStatusService extends BaseService<PeriodStatusMapper, PeriodStatus> {

    @Autowired
    private PeriodStatusMapper periodStatusMapper;

    public List<PeriodStatus> findPeriodStatusByTenantIdAndPeriodSetId(Long periodSetId, Long tenantId, Long setOfBooksId, Integer periodSeq) {
        return periodStatusMapper.findPeriodStatusByTenantIdAndPeriodSetId(periodSetId, tenantId, setOfBooksId, periodSeq);
    }
}
