package com.hand.hcf.app.prepayment.externalApi;


import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.VendorBankAccountCO;
import com.hand.hcf.app.common.co.VendorInfoCO;
import com.hand.hcf.app.mdata.implement.web.SupplierImplementControllerImpl;
import com.hand.hcf.app.prepayment.web.dto.PartnerBankInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 刘亮 on 2018-01-23
 */
@Service
public class VendorModuleInterface {


    private static final Logger log = LoggerFactory.getLogger(VendorModuleInterface.class);
    //TODO 供应商模块连接
    @Autowired
    private SupplierImplementControllerImpl supplierClient;


    //根据公司oid和供应商name查询供应商银行信息
    private static final String GET_VENDOR_BANK_INFO_BY_COMPANY_AND_VENDOR_NAME = "api/ven/artemis/search";

    private static final String GET_VENDOR_BANK_INFO_BY_COMPANY_AND_VENDOR_NAME_NEW = "/api/implement/vendorInfo/bankAccount/page";


    //新接口
    public Page<VendorInfoCO> getBankInfoByCompanyId(Long companyId, String vendorName, Page page) {
        /*Page<VendorInfoCO> list = supplierClient.pageVendorInfosByConditions(companyId, vendorName,page);
        return list;*/
        //jiu.zhao 修改三方接口
        return this.supplierClient.pageVendorInfosByConditions(companyId, vendorName, page.getCurrent() - 1, page.getSize());
    }

    /**
     * 根据租户id和供应商名称、供应商代码[模糊]分页查询供应商，包含银行账号信息
     *
     * @param tenantId
     * @param venNickname
     * @param vendorCode
     * @param page
     * @return
     */

    public Page<VendorInfoCO> pageVendorInfosByTenantIdAndNameAndCode(Long tenantId, String venNickname, String vendorCode, Page page) {
        /*return supplierClient.pageVendorInfosByTenantIdAndNameAndCode(tenantId,venNickname,vendorCode,page);*/
        //bo.liu 修改三方接口
        return supplierClient.pageVendorInfosByTenantIdAndNameAndCode(tenantId, venNickname, vendorCode, page.getCurrent() - 1, page.getSize());
    }


    /**
     * 根据公司id和供应商名称，代码[模糊]分页查询供应商，包含银行账号信息
     * 返回结果按代码升序
     *
     * @param companyId
     * @param vendorName
     * @param page
     */
    public Page<VendorInfoCO> getBankInfoByCompanyAndVendorInfo(Long companyId, String vendorName, String vendorCode, Page page) {
        /*Page<VendorInfoCO> list = supplierClient.pageVendorInfosByConditions(companyId, vendorName,vendorCode,page);
        return list;*/
        //jiu.zhao 修改三方接口
        return this.supplierClient.pageVendorInfosByConditions(companyId, vendorName, vendorCode, page.getCurrent() - 1, page.getSize());
    }

    public PartnerBankInfo getVenerCompanyBankByCode(String companyBankNumber) {
        PartnerBankInfo companyBank = new PartnerBankInfo();
        try {
            List<VendorBankAccountCO> list = supplierClient.listVendorBankAccountsByBankAccount(companyBankNumber);

            // 如果账户，户名，开户行行号，开户行户名有一个为空则返回 空
            if (list == null || list.size() == 0) {
                return null;
            }
            VendorBankAccountCO dto = list.get(0);
            // 开户行坐落地 为开户地 + 开户行城市
            String accountLocation = "";
            if (null != dto.getBankAddress()) {
                accountLocation = accountLocation + dto.getBankAddress();
            }
            if (null != dto.getBankOpeningCity()) {
                accountLocation = accountLocation + dto.getBankOpeningCity();
            }

            companyBank.setId(dto.getId());
            // 总行名称
            companyBank.setBankName(dto.getBankName());
            // 户名
            companyBank.setBankAccountName(dto.getVenBankNumberName());
            // 账户号
            companyBank.setBankAccountNo(dto.getBankAccount());
            // 开户坐落地
            companyBank.setAccountLocation(accountLocation);
            // 支行名称
            companyBank.setBranchName(dto.getBankName());
            // 银行代码
            companyBank.setBankCode(dto.getBankCode());
            return companyBank;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
