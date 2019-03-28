package com.hand.hcf.app.mdata.contact.web.adapter;

import com.hand.hcf.app.common.co.ContactCO;
import com.hand.hcf.app.mdata.contact.dto.UserDTO;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/2/26 17:35
 * @remark
 */
public class UserAdapter {

    public static ContactCO getUserCOByUserDTO(UserDTO userDTO){
        if (userDTO == null) {
            return null;
        }

        ContactCO ContactCO = new ContactCO();
        ContactCO.setId(userDTO.getId());
        ContactCO.setCompanyId(userDTO.getCompanyId());
        ContactCO.setEmail(userDTO.getEmail());
        ContactCO.setFullName(userDTO.getFullName());
        ContactCO.setGender(userDTO.getGender().toString());
        ContactCO.setPhoneNumber(userDTO.getMobile());
        ContactCO.setStatus(userDTO.getStatus().toString());
        ContactCO.setTenantId(userDTO.getTenantId());
        ContactCO.setTitle(userDTO.getTitle());
        ContactCO.setUserOid(userDTO.getUserOid().toString());
        ContactCO.setEmployeeCode(userDTO.getEmployeeId());
        return ContactCO;
    }

}
