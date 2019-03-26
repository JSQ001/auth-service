package com.hand.hcf.app.mdata.supplier.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.supplier.domain.RelationVendorCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/14 16:48
 */
public interface RelationVendorCompanyMapper extends BaseMapper<RelationVendorCompany> {

    /**
     * 指定供应商是否分配到指定公司
     *
     * @param vendorInfoId
     * @param companyId
     * @return
     */
    List<RelationVendorCompany> selectRelationVendorCompanysByVendorInfoIdAndCompanyId(@Param("vendorInfoId") Long vendorInfoId,
                                                                                       @Param("companyId") Long companyId);

    /**
     * 分页获取供应商下的分配公司
     *
     * @param infoId
     * @param page
     * @return
     */
    List<RelationVendorCompany> selectRelationVendorCompanyByPage(@Param("infoId") Long infoId, Pagination page);

    /**
     * 根据供应商获取对应的分配公司
     *
     * @param infoId
     * @return
     */
    List<RelationVendorCompany> selectRelationVendorCompanysByVendorInfoId(@Param("infoId") Long infoId);
}
