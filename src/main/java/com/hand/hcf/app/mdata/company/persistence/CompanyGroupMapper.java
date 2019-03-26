package com.hand.hcf.app.mdata.company.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.common.co.CompanyGroupCO;
import com.hand.hcf.app.mdata.company.domain.CompanyGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by silence on 2017/9/18.
 */
public interface CompanyGroupMapper extends BaseMapper<CompanyGroup> {
    List<CompanyGroup> findCompanyGroupByConditions(@Param("setOfBooksId") Long setOfBooksId, @Param("companyGroupCode") String companyGroupCode, @Param("companyGroupName") String companyGroupName, @Param("enabled") Boolean enabled, @Param("tenantId") Long tenantId, Pagination page);

    List<CompanyGroupCO> listCompanyGroup(@Param("companyId") Long companyId,
                                          @Param("language") String language);


    CompanyGroupCO getCompanyGroupAndCompanyIdsByCompanyGroupId(@Param("id") Long id,
                                                                @Param("language") String language,
                                                                @Param("enabled") Boolean enabled);

    List<Long> listAssignCompanyIds(@Param("id") Long id, @Param("enabled") Boolean enabled);
}
