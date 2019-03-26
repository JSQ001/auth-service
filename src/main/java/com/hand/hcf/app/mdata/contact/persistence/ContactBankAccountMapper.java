package com.hand.hcf.app.mdata.contact.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.contact.domain.ContactBankAccount;
import org.apache.ibatis.annotations.Param;

public interface ContactBankAccountMapper extends BaseMapper<ContactBankAccount> {
    Integer checkBankCardNoExists(@Param("tenantId") Long tenantId, @Param("bankAccountNo") String cardNo);
}
