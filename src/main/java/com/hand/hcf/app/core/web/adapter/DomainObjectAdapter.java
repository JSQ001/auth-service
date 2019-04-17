

package com.hand.hcf.app.core.web.adapter;

import com.hand.hcf.app.core.domain.Domain;
import com.hand.hcf.app.core.domain.DomainEnable;
import com.hand.hcf.app.core.domain.DomainLogic;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;

public final class DomainObjectAdapter {
    public static <T extends DomainObjectDTO, M extends Domain> T toDto(T t, M m) {
        if (t == null || m == null) {
            throw new IllegalArgumentException("dto or domain object is null");
        }
        t.setId(m.getId());
        t.setCreatedDate(m.getCreatedDate());
        t.setCreatedBy(m.getCreatedBy());
        t.setLastUpdatedDate(m.getLastUpdatedDate());
        t.setLastUpdatedBy(m.getLastUpdatedBy());
        t.setVersionNumber(m.getVersionNumber());
        if(m instanceof DomainEnable){
            t.setEnabled(((DomainEnable) m).getEnabled());
        }else if(m instanceof DomainLogicEnable){
            t.setEnabled(((DomainLogicEnable) m).getEnabled());
            t.setDeleted(((DomainLogicEnable) m).getDeleted());
        }else if(m instanceof DomainLogic){
            t.setDeleted(((DomainLogic) m).getDeleted());
        }
        return t;
    }

    public static <T extends DomainObjectDTO, M extends Domain> M toDomain(M m, T t) {
        if (m == null || t == null) {
            throw new IllegalArgumentException("dto or domain object is null");
        }
        m.setId(t.getId());
        m.setCreatedDate(t.getCreatedDate());
        m.setCreatedBy(t.getCreatedBy());
        m.setLastUpdatedDate(t.getLastUpdatedDate());
        m.setLastUpdatedBy(t.getLastUpdatedBy());
        m.setVersionNumber(t.getVersionNumber());
        if(m instanceof DomainEnable){
            ((DomainEnable) m).setEnabled(t.getEnabled());
        }else if(m instanceof DomainLogicEnable){
            ((DomainLogicEnable) m).setEnabled(t.getEnabled());
            ((DomainLogicEnable) m).setDeleted(t.getDeleted());
        }else if(m instanceof DomainLogic){
            ((DomainLogic) m).setDeleted(t.getDeleted());
        }
        return m;
    }
}
