package com.hand.hcf.app.mdata.supplier.web.adapter;

import com.hand.hcf.app.common.co.VendorBankAccountCO;
import com.hand.hcf.app.mdata.supplier.domain.VendorBankAccount;
import com.hand.hcf.app.mdata.supplier.service.dto.VendorBankAccountforStatusDTO;
import org.springframework.beans.BeanUtils;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/4 16:38
 */
public class VendorBankAccountAdapter {

    public static VendorBankAccount vendorBankAccountCOToVendorBankAccount(VendorBankAccountCO vendorBankAccountCO) {
        if (vendorBankAccountCO == null) {
            return null;
        }
        VendorBankAccount vendorBankAccount = new VendorBankAccount();
        BeanUtils.copyProperties(vendorBankAccountCO, vendorBankAccount);
        vendorBankAccount.setVendorInfoId(Long.valueOf(vendorBankAccountCO.getVenInfoId()));
        vendorBankAccount.setVendorId(vendorBankAccountCO.getVenNickOid());
        vendorBankAccount.setStatus(vendorBankAccountCO.getVenType());
        vendorBankAccount.setOpeningBank(vendorBankAccountCO.getBankOpeningBank());
        vendorBankAccount.setOpeningBankCity(vendorBankAccountCO.getBankOpeningCity());
        vendorBankAccount.setOpeningBankLineNum(vendorBankAccountCO.getBankOperatorNumber());
        vendorBankAccount.setRemark(vendorBankAccountCO.getNotes());
        return vendorBankAccount;
    }

    public static VendorBankAccountforStatusDTO vendorBankAccountToVendorBankAccountCO(VendorBankAccount vendorBankAccount) {
        if (vendorBankAccount == null) {
            return null;
        }
        VendorBankAccountforStatusDTO vendorBankAccountCO = new VendorBankAccountforStatusDTO();
        BeanUtils.copyProperties(vendorBankAccount, vendorBankAccountCO);
        vendorBankAccountCO.setVenInfoId(vendorBankAccount.getVendorInfoId().toString());
        vendorBankAccountCO.setVenNickOid(vendorBankAccount.getVendorId());
        vendorBankAccountCO.setVenType(vendorBankAccount.getStatus());
        vendorBankAccountCO.setBankOpeningBank(vendorBankAccount.getBankName());
        vendorBankAccountCO.setBankOpeningCity(vendorBankAccount.getOpeningBankCity());
        vendorBankAccountCO.setBankOperatorNumber(vendorBankAccount.getBankCode());
        vendorBankAccountCO.setNotes(vendorBankAccount.getRemark());
        vendorBankAccountCO.setWebUpdateDate(vendorBankAccount.getLastUpdatedDate());
        vendorBankAccountCO.setCreateTime(vendorBankAccount.getCreatedDate());
        vendorBankAccountCO.setUpdateTime(vendorBankAccount.getLastUpdatedDate());
        vendorBankAccountCO.setBankAddress(vendorBankAccount.getBankAddress());
        vendorBankAccountCO.setVendorBankStatus(vendorBankAccount.getVendorBankStatus());
        return vendorBankAccountCO;
    }
}
