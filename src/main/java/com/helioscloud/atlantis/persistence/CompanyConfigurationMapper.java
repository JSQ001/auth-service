/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

/**
* Created by Transy on 2017/5/18.
*/
package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.CompanyConfiguration;
import org.apache.ibatis.annotations.Select;

public interface CompanyConfigurationMapper extends BaseMapper<CompanyConfiguration> {

    @Select("select * from art_company_configuration where company_oid=#{0}")
    CompanyConfiguration testMapper(String id);
}
