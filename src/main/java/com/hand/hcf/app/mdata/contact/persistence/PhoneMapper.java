package com.hand.hcf.app.mdata.contact.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.contact.domain.Phone;
import org.apache.ibatis.annotations.Param;

public interface PhoneMapper extends BaseMapper<Phone> {

    /**
     * 查询国家编码
     *
     * @param mobile 用户手机号
     * @return 通过手机号查询国家编码
     */
    String selectCountryCodeByMobile(@Param("mobile") String mobile);

    /**
     * 验证是否与在职员工手机号冲突
     *
     * @param mobile 用户手机号
     * @return null为不冲突
     */
    String verifyPhoneExsits(@Param("mobile") String mobile);
}
