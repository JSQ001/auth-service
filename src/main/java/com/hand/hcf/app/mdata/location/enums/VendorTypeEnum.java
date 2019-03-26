package com.hand.hcf.app.mdata.location.enums;

/**
 * Created by vance on 2017/3/9.
 */

public enum VendorTypeEnum {
    Ctrip_Air("ctrip_air"), Standard("standard");

    public String getVendorTypeName() {
        return vendorTypeName;
    }

    public void setVendorTypeName(String vendorTypeName) {
        this.vendorTypeName = vendorTypeName;
    }

    private String vendorTypeName;

    VendorTypeEnum(String vendorTypeName) {
        this.vendorTypeName = vendorTypeName;
    }

}
