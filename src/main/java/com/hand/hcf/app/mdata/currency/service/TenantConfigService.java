package com.hand.hcf.app.mdata.currency.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.mdata.currency.domain.TenantConfig;
import com.hand.hcf.app.mdata.currency.dto.TenantConfigDTO;
import com.hand.hcf.app.mdata.currency.persistence.TenantConfigMapper;
import com.hand.hcf.app.core.service.BaseService;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description 汇率容差表(SysTenantConfig)表服务接口
 * @author wang.shuai
 * @date 2019-04-29 17:59:30
 */
@Service
public class TenantConfigService extends BaseService<TenantConfigMapper, TenantConfig> {

    @Autowired
    TenantConfigMapper tenantConfigMapper;

    @Autowired
    private MapperFacade mapperFacade;

    /**
     * 新增或修改汇率容差
     *
     * @param tenantConfig 汇率容差实体类
     * @return 修改后的汇率容差
     */
    public TenantConfigDTO updateTenantConfig(TenantConfig tenantConfig) {
        boolean tenantConfig1 = this.update(tenantConfig, new EntityWrapper<TenantConfig>().
                eq("tenant_id", tenantConfig.getTenantId()).
                eq("set_of_books_id", tenantConfig.getSetOfBooksId()));
        if (!tenantConfig1) {
            insert(tenantConfig);
        }
        return mapperFacade.map(tenantConfig, TenantConfigDTO.class);
    }

    /**
     * 根据账套id及租户id查询汇率容差
     *
     * @param tenantConfig 汇率容差实体类
     * @return 汇率容差实体类DTO
     */
    public TenantConfigDTO getTenantConfig(TenantConfigDTO tenantConfig) {
        TenantConfig tenantConfig1 = this.selectOne(new EntityWrapper<TenantConfig>().
                eq("tenant_id", tenantConfig.getTenantId()).
                eq("set_of_books_id", tenantConfig.getSetOfBooksId()));
        return mapperFacade.map(tenantConfig1, TenantConfigDTO.class);
    }
}