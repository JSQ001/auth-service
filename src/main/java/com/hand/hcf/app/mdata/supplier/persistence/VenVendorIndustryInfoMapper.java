package com.hand.hcf.app.mdata.supplier.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.supplier.domain.VendorIndustryInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: jaixing.che
 * @date: 2019/3/20
 */
@Component
public interface VenVendorIndustryInfoMapper extends BaseMapper<VendorIndustryInfo> {
    /**
     * 根据供应商 id
     *
     * @param vendorId
     */
    List<VendorIndustryInfo> selectVenVendorIndustryInfoByVenderId(@Param("vendorId") Long vendorId, @Param("industryId") Long industryId);

     void deleteVenVendorIndustryInfoByVenderId (@Param("vendorId") Long vendorId);
}
