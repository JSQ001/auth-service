package com.hand.hcf.app.mdata.location.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.mdata.location.domain.VendorAliasDetail;
import com.hand.hcf.app.mdata.location.persistence.VendorAliasDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 21:00 2018/5/17
 * @Modified by
 */
@Service
public class VendorAliasDetailService {
    @Autowired
    VendorAliasDetailMapper mapper;

    public List<VendorAliasDetail> findByCodeAndVendorType(String code, String vendorType){
        List<VendorAliasDetail> list = mapper.selectList(
                new EntityWrapper<VendorAliasDetail>()
                        .eq("code",code)
                        .eq("vendor_type",vendorType));
        return list;
    }

    public List<VendorAliasDetail> findByCityAliasCodeAndVendorType(String code,String vendorType){
        List<VendorAliasDetail> list = mapper.selectList(
                new EntityWrapper<VendorAliasDetail>()
                        .eq("CITY_ALIAS_CODE",code)
                        .eq("vendor_type",vendorType));
        return list;
    }
    public List<VendorAliasDetail> findByCountryAliasCodeAndVendorType(String code,String vendorType){
        List<VendorAliasDetail> list = mapper.selectList(
                new EntityWrapper<VendorAliasDetail>()
                        .eq("COUNTRY_ALIAS_CODE",code)
                        .eq("vendor_type",vendorType));
        return list;
    }
}
