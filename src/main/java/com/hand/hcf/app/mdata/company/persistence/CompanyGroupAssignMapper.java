package com.hand.hcf.app.mdata.company.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.domain.CompanyGroup;
import com.hand.hcf.app.mdata.company.domain.CompanyGroupAssign;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by silence on 2017/9/18.
 */
public interface CompanyGroupAssignMapper extends BaseMapper<CompanyGroupAssign> {
    public List<CompanyGroup> findCompanyGroupByCompanyId(Long companyId);

    public List<Company> findCompanyByCodeOrName(@Param("companyCode") String companyCode, @Param("companyName") String companyName);

    public List<Company> findCompaniesByInterval(@Param("companyFrom") String companyFrom, @Param("companyTo") String companyTo);
}
