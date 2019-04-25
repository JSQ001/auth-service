package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.payment.domain.CompanyBank;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 刘亮 on 2017/9/8.
 */
public interface CompanyBankMapper extends BaseMapper<CompanyBank> {

    List<BasicCO> pageCompanyBankByInfoResultBasic(@Param("setOfBooksId") Long SetOfBooksId,
                                                   @Param("name") String name,
                                                   @Param("code") String code,
                                                   @Param("companyIds") List<Long> companyIds,
                                                   Page page);
}
