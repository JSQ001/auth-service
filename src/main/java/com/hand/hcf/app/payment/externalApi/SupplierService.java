package com.hand.hcf.app.payment.externalApi;

import com.hand.hcf.app.common.co.VendorBankAccountCO;
import com.hand.hcf.app.common.co.VendorInfoCO;
import com.hand.hcf.app.mdata.implement.web.SupplierImplementControllerImpl;
import com.hand.hcf.app.payment.web.dto.PartnerBankInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 调用供应商模块三方接口
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2018/12/25
 */
@Service
public class SupplierService {
    @Autowired
    private SupplierImplementControllerImpl supplierClient;

    /**
     * 根据ID查询供应商信息
     * @param id
     * @return
     */
    public VendorInfoCO getOneVendorInfoByArtemis(String id){
        return supplierClient.getOneVendorInfoByArtemis(id);
    }

    /**
     * 根据银行账号获取 银行信息
     *
     * @param companyBankNumber
     * @return
     */
    public PartnerBankInfo getVenerCompanyBankByCode(String companyBankNumber){
        PartnerBankInfo companyBank = new PartnerBankInfo();
        try {
            List<VendorBankAccountCO> list = supplierClient.listVendorBankAccountsByBankAccount(companyBankNumber);

            // 如果账户，户名，开户行行号，开户行户名有一个为空则返回 空
            if (list == null || list.size() == 0){
                return null;
            }
            VendorBankAccountCO co = list.get(0);
            // 开户行坐落地 为开户地 + 开户行城市
            String accountLocation = "";
            if (null != co.getBankAddress()){
                accountLocation = accountLocation + co.getBankAddress();
            }
            if (null != co.getBankOpeningCity()){
                accountLocation = accountLocation + co.getBankOpeningCity();
            }

            companyBank.setId(co.getId());
            // 总行名称
            companyBank.setBankName(co.getBankName());
            // 户名
            companyBank.setBankAccountName(co.getVenBankNumberName());
            // 账户号
            companyBank.setBankAccountNo(co.getBankAccount());
            // 开户坐落地
            companyBank.setAccountLocation(accountLocation);
            // 支行名称
            companyBank.setBranchName(co.getBankName());
            // 银行代码
            companyBank.setBankCode(co.getBankCode());
            return companyBank;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
