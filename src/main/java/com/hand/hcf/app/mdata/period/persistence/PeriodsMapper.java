package com.hand.hcf.app.mdata.period.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.period.domain.PeriodSet;
import com.hand.hcf.app.mdata.period.domain.Periods;
import com.hand.hcf.app.mdata.period.dto.PeriodsDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PeriodsMapper extends BaseMapper<Periods> {
    List<PeriodsDTO> findClosePeriodsByTenantIdAndPeriodSetId(Pagination page, @Param("periodSetId") Long periodSetId,
                                                              @Param("tenantId") Long tenantId, @Param("setOfBooksId") Long setOfBooksId);
    List<PeriodsDTO> findOpenPeriodsByTenantIdAndPeriodSetId(Pagination page, @Param("periodSetId") Long periodSetId,
                                                             @Param("tenantId") Long tenantId, @Param("setOfBooksId") Long setOfBooksId);
    List<PeriodSet> findPeriodSetCodeByYear(Integer year);

    Periods getPeriodBysetOfBooksIdAndDateTime(@Param("periodSetId") Long periodSetId, @Param("dateTime") String dateTime);

    List<Periods> findOpenPeriodsByBookID(@Param("setOfBooksId") Long periodSetId, Pagination page);

    List<Integer> getPeriodYearsForPeriodSetId(@Param("periodSetId") Long periodSetId);

    List<Periods> findPeriodsByIdAndName(@Param("setOfBooksId") Long setOfBooksId, @Param("periodName") String periodName, @Param("periodYear") Integer periodYear, @Param("tenantId") Long tenantId);
}
