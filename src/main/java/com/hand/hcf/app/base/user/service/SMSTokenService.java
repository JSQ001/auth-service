package com.hand.hcf.app.base.user.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.user.domain.SMSToken;
import com.hand.hcf.app.base.user.persistence.SMSTokenMapper;
import com.hand.hcf.app.core.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SMSTokenService extends BaseService<SMSTokenMapper, SMSToken> {

    public SMSToken getLatestTokenByToUserAndType(String toUSer, Integer typeId) {
        return super.selectOne(new EntityWrapper<SMSToken>()
                .eq("to_user", toUSer)
                .eq("type_id", typeId)
                .orderBy("expire_time", false))
                ;
    }

    public SMSToken findByTokenValueAndToUserAndTypeID(String tokenValue, String to, Integer typeID) {
        return super.selectOne(new EntityWrapper<SMSToken>()
                .eq("to_user", to)
                .eq("type_id", typeID)
                .eq("token_value", tokenValue))
                ;
    }

    public SMSToken findByTokenValueAndTypeID(String tokenValue, Integer typeID) {
        return super.selectOne(new EntityWrapper<SMSToken>()
                .eq("type_id", typeID)
                .eq("token_value", tokenValue))
                ;
    }

    public List<SMSToken> findByToUserAndTypeID(String to, Integer typeID) {
        return super.selectList(new EntityWrapper<SMSToken>()
                .eq("to_user", to).eq("type_id", typeID))
                ;
    }

}

