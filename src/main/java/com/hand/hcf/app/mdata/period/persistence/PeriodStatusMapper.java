package com.hand.hcf.app.mdata.period.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.period.domain.PeriodStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface PeriodStatusMapper extends BaseMapper<PeriodStatus> {
    List<PeriodStatus> findPeriodStatusByTenantIdAndPeriodSetId(@Param("periodSetId") Long periodSetId,
                                                                @Param("tenantId") Long tenantId,
                                                                @Param("setOfBooksId") Long setOfBooksId,
                                                                @Param("periodSeq") Integer periodSeq);
}
