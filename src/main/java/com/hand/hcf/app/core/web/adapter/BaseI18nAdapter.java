package com.hand.hcf.app.core.web.adapter;


import com.hand.hcf.app.core.domain.DomainI18n;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import com.hand.hcf.app.core.web.dto.BaseI18nDomainDTO;

/**
 * Created by kai.zhang on 2017-10-19.
 */
public final class BaseI18nAdapter {

    public static <T extends DomainI18n, M extends BaseI18nDomainDTO> T i18nDtoToI18nDomain(T t, M m) {
        if(t != null && m != null) {
            t.setId(m.getId());
            t.setDeleted(m.getDeleted());
            t.setCreatedDate(m.getCreatedDate());
            t.setCreatedBy(m.getCreatedBy());
            t.setLastUpdatedDate(m.getLastUpdatedDate());
            t.setLastUpdatedBy(m.getLastUpdatedBy());
            t.setI18n(m.getI18n());
            t.setVersionNumber(m.getVersionNumber());
            if(t instanceof DomainI18nEnable){
                ((DomainI18nEnable) t).setEnabled(m.getEnabled());
            }
            return t;
        } else {
            throw new IllegalArgumentException("dto or domain object is null");
        }
    }

    public static <M extends BaseI18nDomainDTO, T extends DomainI18n> M i18nDomainToI18nDto(M m, T t) {
        if(m != null && t != null) {
            m.setId(t.getId());
            m.setDeleted(t.getDeleted());
            m.setCreatedDate(t.getCreatedDate());
            m.setCreatedBy(t.getCreatedBy());
            m.setLastUpdatedDate(t.getLastUpdatedDate());
            m.setLastUpdatedBy(t.getLastUpdatedBy());
            m.setI18n(t.getI18n());
            m.setVersionNumber(t.getVersionNumber());
            if(t instanceof DomainI18nEnable){
                m.setEnabled(((DomainI18nEnable) t).getEnabled());
            }
            return m;
        } else {
            throw new IllegalArgumentException("dto or domain object is null");
        }
    }
}
