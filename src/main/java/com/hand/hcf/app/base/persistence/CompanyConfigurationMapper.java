

/**
* Created by Transy on 2017/5/18.
*/
package com.hand.hcf.app.base.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.domain.CompanyConfiguration;
import org.apache.ibatis.annotations.Select;

public interface CompanyConfigurationMapper extends BaseMapper<CompanyConfiguration> {

    @Select("select * from art_company_configuration where company_oid=#{0}")
    CompanyConfiguration testMapper(String id);
}
