package com.hand.hcf.app.mdata.supplier.service.dto;

import com.hand.hcf.app.common.co.VendorBankAccountCO;


/**
 * @description:
 * @version: 1.0
 * @author: jaixing.che
 * @date: 2019/3/20
 */
public class VendorBankAccountforStatusDTO extends VendorBankAccountCO {
    private String vendorBankStatus;

    public String getVendorBankStatus() {
        return this.vendorBankStatus;
    }

    public void setVendorBankStatus(final String vendorStatus) {
        this.vendorBankStatus = vendorStatus;
    }
}
