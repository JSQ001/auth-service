package com.hand.hcf.app.mdata.company.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.domain.CompanyLevel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 刘亮 on 2017/9/4.
 */
public interface CompanyLevelMapper extends BaseMapper<CompanyLevel> {
    List<Company> selectByCompanyLevelCode(@Param("levelId") Long levelId);
}
