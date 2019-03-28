package com.hand.hcf.app.base.system.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.system.domain.PasswordHistory;
import com.hand.hcf.app.base.system.persistence.PasswordHistoryMapper;
import com.hand.hcf.core.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PasswordHistoryService extends BaseService<PasswordHistoryMapper, PasswordHistory> {

    public PasswordHistory getPasswordHistory(UUID userOid){
       return selectOne(new EntityWrapper<PasswordHistory>()
        .eq("user_oid",userOid)
        .orderBy("created_date",false));
    }

    public List<PasswordHistory> listPasswordHistory(UUID userOid){
        return selectList(new EntityWrapper<PasswordHistory>()
                .eq("user_oid",userOid)
                .orderBy("created_date",false));
    }

}
