/**
* Created by Transy on 2017/5/18.
*/
package com.hand.hcf.app.base.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.domain.CompanySecurity;
import org.apache.ibatis.annotations.Select;

public interface CompanySecurityMapper extends BaseMapper<CompanySecurity> {

    @Select("select * from art_company_security where company_oid=#{0}")
    CompanySecurity testMapper(String id);
}
