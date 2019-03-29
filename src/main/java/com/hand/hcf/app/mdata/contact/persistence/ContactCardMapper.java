package com.hand.hcf.app.mdata.contact.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.contact.domain.ContactCard;
import org.apache.ibatis.annotations.Param;

public interface ContactCardMapper extends BaseMapper<ContactCard> {
    Integer checkCardNoExist(@Param("tenantId") Long tenantId, @Param("cardNo") String cardNo, @Param("cardType") Integer cardType);
}
