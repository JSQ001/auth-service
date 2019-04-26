package com.hand.hcf.app.mdata.supplier.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hcf.app.common.co.VendorInfoCO;

import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: jaixing.che
 * @date: 2019/3/19
 */
@Data
public class VendorInfoforStatusDTO extends VendorInfoCO {

    private String vendorStatus;

    private String vendorBankStatus;
    /**
     * 是否启用
     */
    private String venTypeString;
    /**
     * 供应商模板导入功能 获取供应商类别数组
     */
    private String vendorIndustryInfoString;
    /**
     * 供应商模板导入功能 获取启用日期
     */
    private String effectiveDateString;

    private List<VendorBankAccountforStatusDTO> venBankAccountForStatusBeans;

    //private List<venVendorVndustryInfo> venVendorVndustryInfoBeans;
    private  List<String> venVendorIndustryInfoList;
    public String getvendorStatus() {
        return this.vendorStatus;
    }

    public void setVenBankAccountForStatusBeans(final List<VendorBankAccountforStatusDTO> venBankAccountBeans) {
        this.venBankAccountForStatusBeans = venBankAccountBeans;
    }

//    public List<venVendorVndustryInfo> getVenVendorVndustryInfoBeans() {
//        return venVendorVndustryInfoBeans;
//    }
//
//    public void setVenVendorVndustryInfoBeans(List<venVendorVndustryInfo> venVendorVndustryInfoBeans) {
//        this.venVendorVndustryInfoBeans = venVendorVndustryInfoBeans;
//    }

    public List<String> getVenVendorIndustryInfoList() {
        return venVendorIndustryInfoList;
    }

    public void setVenVendorIndustryInfoList(List<String> venVendorIndustryInfoList) {
        this.venVendorIndustryInfoList = venVendorIndustryInfoList;
    }
}
