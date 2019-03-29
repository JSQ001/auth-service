package com.hand.hcf.app.mdata.contact.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.contact.domain.Phone;
import com.hand.hcf.app.mdata.contact.enums.PhoneType;
import com.hand.hcf.app.mdata.contact.persistence.PhoneMapper;
import com.hand.hcf.core.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class PhoneService extends BaseService<PhoneMapper, Phone> {

    public Phone getOneByContactId(Long contactId){
        return selectOne(new EntityWrapper<Phone>().eq("contact_id", contactId));
    }

    public Set<Phone> getPhones(Long contactId) {
        return new HashSet(selectList(new EntityWrapper<Phone>()
                .eq("contact_id", contactId)));
    }

    public boolean delete(Long contactId, String phoneNumber) {
        return super.delete(new EntityWrapper<Phone>()
                .eq("contact_id", contactId)
                .eq("phone_number", phoneNumber));
    }

    public boolean update(Long contactId, String phoneNumber, boolean primary) {
        Phone p = new Phone();
        p.setPrimaryFlag(primary);

        return super.update(p, new EntityWrapper<Phone>()
                .eq("contact_id", contactId)
                .eq("phone_number", phoneNumber));
    }

    public boolean updateByContactId(Long contactId,String phoneNumber,String countryCode){
        Phone p = getOneByContactId(contactId);
        if(p == null){
            return false;
        }
        p.setCountryCode(countryCode);
        p.setPhoneNumber(phoneNumber);
        return super.updateById(p);
}

    /**
     * 查询国家编码
     *
     * @param mobile 用户手机号
     * @return 通过手机号查询国家编码
     */
   public String selectCountryCodeByMobile( String mobile){
        return baseMapper.selectCountryCodeByMobile(mobile);
    }

    public String getMobile(Set<Phone> phones) {
        for (Phone phone : phones) {
            if (phone.getPrimaryFlag()) {
                return phone.getPhoneNumber();
            }
        }
        return null;
    }

    public String getMobile(Long contactId) {
        return getMobile(getPhones(contactId));
    }

    public void setMobile(Long contactId,String mobile){
        setMobile(contactId,getPhones(contactId),mobile);
    }

    //此方法只用于更新登录账号时，处理绑定的phone，认为参数mobile就是最新的登录账号
    public void setMobile(Long contactId, Set<Phone> phones, String mobile) {
        if(StringUtils.isBlank(mobile)) {
            return;
        }
        setMobile(contactId,phones,mobile,null);
    }

    public void setMobile(Long contactId,String mobile,String countryCode){
        setMobile(contactId,getPhones(contactId),mobile,countryCode);
    }

    public void setMobile(Long contactId, Set<Phone> phones, String mobile, String countryCode) {
        //当前修改人
        Long lastUpdatedBy = OrgInformationUtil.getCurrentUserId();

        boolean exist = false;
        Phone primaryPhone = null;
        for (Phone phone : phones) {
            phone.setContactId(contactId);
            //找出相同的号码 ，将其设置 primary
            if (phone.getPhoneNumber().equals(mobile)) {
                exist = true;
                phone.setPrimaryFlag(true);
                phone.setDidiEnable(true);
                if (StringUtils.isNotEmpty(countryCode)) {
                    phone.setCountryCode(countryCode);
                }
                if(!phone.getPrimaryFlag()){
                    phone.setLastUpdatedBy(lastUpdatedBy);
                    phone.setLastUpdatedDate(ZonedDateTime.now());
                }
            } else {
                //其他号码设置为 非primary
                if(phone.getPrimaryFlag()){
                    phone.setLastUpdatedBy(lastUpdatedBy);
                    phone.setLastUpdatedDate(ZonedDateTime.now());
                }
                phone.setPrimaryFlag(false);
            }
        }
        //若是一个新号码 ， 添加一个主号码
        if (!exist && StringUtils.isNotBlank(mobile)) {
            primaryPhone = new Phone();
            primaryPhone.setContactId(contactId);
            primaryPhone.setPhoneNumber(mobile);
            primaryPhone.setPrimaryFlag(true);
            primaryPhone.setDidiEnable(true);
            primaryPhone.setTypeNumber(PhoneType.MOBILE_PHONE.getId());
            if (StringUtils.isNotEmpty(countryCode)) {
                primaryPhone.setCountryCode(countryCode);
            }
            primaryPhone.setLastUpdatedBy(lastUpdatedBy);
            primaryPhone.setLastUpdatedDate(ZonedDateTime.now());
            phones.add(primaryPhone);
        }
        super.insertOrUpdateBatch(new ArrayList<>(phones));
    }

    public String getCountryCode(Set<Phone> phones){
        String countryCode = null;
        for (Phone phone : phones) {
            if (phone.getPrimaryFlag()) {
                countryCode = phone.getCountryCode();
                if(StringUtils.isBlank(countryCode)){
                    countryCode = "CN";
                }
            }
        }
        return countryCode;
    }

    public String verifyPhoneExsits(String mobile){
       return baseMapper.verifyPhoneExsits(mobile);
    }


}
