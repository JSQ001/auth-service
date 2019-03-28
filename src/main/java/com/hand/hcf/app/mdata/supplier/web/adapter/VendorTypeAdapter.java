package com.hand.hcf.app.mdata.supplier.web.adapter;

import com.hand.hcf.app.common.dto.VendorTypeCO;
import com.hand.hcf.app.mdata.supplier.domain.VendorType;
import org.springframework.beans.BeanUtils;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/11 11:01
 */
public class VendorTypeAdapter {

    public static VendorType vendorTypeCOToVendorType(VendorTypeCO vendorTypeCO) {
        if (vendorTypeCO == null) {
            return null;
        }
        VendorType vendorType = new VendorType();
        BeanUtils.copyProperties(vendorTypeCO, vendorType);
        vendorType.setCode(vendorTypeCO.getVendorTypeCode());
        return vendorType;
    }

    public static VendorTypeCO vendorTypeToVendorTypeCO(VendorType vendorType) {
        if (vendorType == null) {
            return null;
        }
        VendorTypeCO vendorTypeCO = new VendorTypeCO();
        BeanUtils.copyProperties(vendorType, vendorTypeCO);
        vendorTypeCO.setVendorTypeCode(vendorType.getCode());
        return vendorTypeCO;
    }
}
