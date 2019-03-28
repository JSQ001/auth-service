/**
* Created by Transy on 2017/5/18.
*/
package com.hand.hcf.app.mdata.company.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.company.domain.CompanySecurity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CompanySecurityMapper extends BaseMapper<CompanySecurity> {

    @Select("select * from sys_company_security where company_oid=#{0}")
    CompanySecurity testMapper(String id);

    CompanySecurity findTenantCompanySecurity(@Param("tenantId") Long tenantId);

    List<CompanySecurity> findTenantHisCompanySecurity();

    List<CompanySecurity> findRepeatCodeCompanySecurity();
}
