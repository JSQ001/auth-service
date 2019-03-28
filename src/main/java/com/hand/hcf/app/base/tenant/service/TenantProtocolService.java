package com.hand.hcf.app.base.tenant.service;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.tenant.domain.TenantProtocol;
import com.hand.hcf.app.base.tenant.persistence.TenantProtocolMapper;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TenantProtocolService extends BaseService<TenantProtocolMapper,TenantProtocol> {


    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    TenantService tenantService;
    /**
     * 新建或则修改自定义租户协议
     *
     * @param tenantProtocol
     * @return
     */
    public TenantProtocol inputTenantProtocol(TenantProtocol tenantProtocol, Long userId) {
        Long id;
        this.protocolCommonCheck(tenantProtocol);
        List<TenantProtocol> protocolList = selectList(new EntityWrapper<TenantProtocol>().eq("tenant_id", tenantProtocol.getTenantId()));
        if (CollectionUtils.isEmpty(protocolList)) {
            //新建
            tenantProtocol.setCreatedDate(ZonedDateTime.now());
            tenantProtocol.setCreatedBy(userId);
            insert(tenantProtocol);
            id = tenantProtocol.getId();
        } else if (protocolList.size() == 1) {
            //修改
            TenantProtocol exist = protocolList.get(0);
            exist.setTitle(tenantProtocol.getTitle());
            exist.setContent(tenantProtocol.getContent());
            exist.setEnabled(tenantProtocol.getEnabled());
            exist.setDeleted(tenantProtocol.getDeleted());
            exist.setTenantOnly(tenantProtocol.getTenantOnly());
            exist.setLastUpdatedBy(userId);
            exist.setLastUpdatedDate(ZonedDateTime.now());
            updateById(exist);
            id = exist.getId();
        } else {
            //一个租户多个自定义协议，报错
            throw new BizException(RespCode.TENANT_MULTI_PROTOCOL);
        }
        baseI18nService.insertOrUpdateI18n(tenantProtocol.getI18n(), TenantProtocol.class, id);
        return baseI18nService.selectOneBaseTableInfoWithI18n(id, TenantProtocol.class);
    }


    public TenantProtocol findOneByTenantId(Long tenantId) {
        TenantProtocol tenantProtocol = null;
        List<TenantProtocol> protocolList = selectList(new EntityWrapper<TenantProtocol>().eq("tenant_id", tenantId));
        if (CollectionUtils.isNotEmpty(protocolList) && protocolList.size() == 1) {
            tenantProtocol = protocolList.get(0);
            tenantProtocol = baseI18nService.selectOneBaseTableInfoWithI18n(tenantProtocol.getId(), TenantProtocol.class);
        }
        return tenantProtocol;
    }

    public TenantProtocol findOneById(Long id) {
        TenantProtocol tenantProtocol = selectById(id);
        if (tenantProtocol == null) {
            return null;
        }
        tenantProtocol = baseI18nService.selectOneBaseTableInfoWithI18n(tenantProtocol.getId(), TenantProtocol.class);
        return tenantProtocol;
    }

    private void protocolCommonCheck(TenantProtocol tenantProtocol) {
        String title = tenantProtocol.getTitle();
        if (StringUtils.isEmpty(title) || title.length() > 50) {
            throw new BizException(RespCode.TENANT_PROTOCOL_TITLE_TO0_LONG);
        }
        if (tenantProtocol.getI18n() != null && tenantProtocol.getI18n().get("title") != null) {
            List<Map<String, String>> titles = tenantProtocol.getI18n().get("title");
            titles.stream().map(u -> {
                String titleValue = u.get("value");
                if (StringUtils.isEmpty(titleValue) || titleValue.length() > 50) {
                    throw new BizException(RespCode.TENANT_PROTOCOL_TITLE_TO0_LONG);
                }
                return u;
            }).collect(Collectors.toList());
        }
        String content = tenantProtocol.getContent();
        if (StringUtils.isEmpty(content) || content.length() > 10000) {
            throw new BizException(RespCode.TENANT_PROTOCOL_CONTENT_TO0_LONG);
        }
    }

}
