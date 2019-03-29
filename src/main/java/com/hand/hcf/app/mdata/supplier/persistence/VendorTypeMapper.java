package com.hand.hcf.app.mdata.supplier.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.supplier.domain.VendorType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/11 11:06
 */
@Component
public interface VendorTypeMapper extends BaseMapper<VendorType> {

    /**
     * 分页查询供应商类型信息
     *
     * @param name
     * @param code
     * @param companyId
     * @param tenantId
     * @param enabled
     * @param page
     * @return
     */
    List<VendorType> selectVendorTypesByPages(@Param("code") String code,
                                              @Param("name") String name,
                                              @Param("companyId") Long companyId,
                                              @Param("tenantId") Long tenantId,
                                              @Param("enabled") Boolean enabled,
                                              Pagination page);

    /**
     * 根据供应商类型 代码 获取相应信息
     *
     * @param code
     * @param name
     * @param tenantId
     * @return
     */
    List<VendorType> selectVendorTypeByCode(@Param("code") String code,
                                            @Param("name") String name,
                                            @Param("tenantId") Long tenantId);
}
