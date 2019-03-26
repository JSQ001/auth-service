package com.hand.hcf.app.mdata.supplier.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.supplier.domain.VendorBankAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: hand
 * @Description:
 * @Date: 2018/4/4 17:01
 */
public interface VendorBankAccountMapper extends BaseMapper<VendorBankAccount> {

    /**
     * 获取 根据供应商标识和当前登陆所属公司 下的银行信息
     *
     * @param companyOid
     * @param vendorInfoId
     * @return
     */
    List<VendorBankAccount> selectVendorBankAccountsByCompanyOidAndVendorInfoId(@Param("companyOid") String companyOid,
                                                                                @Param("vendorInfoId") String vendorInfoId);

    /**
     * 租户级别 根据银行账号获取 供应商银行账号
     *
     * @param tenantId
     * @param bankAccount
     * @return
     */
    List<VendorBankAccount> selectVendorBankAccountsByTenantIdAndBankAccount(@Param("tenantId") String tenantId,
                                                                             @Param("bankAccount") String bankAccount);

    /**
     * 根据银行账号和供应商id获取 供应商银行账号
     *
     * @param bankAccount
     * @param vendorInfoId
     * @return
     */
    List<VendorBankAccount> selectVendorBankAccountsByBankAccountAndVendorInfoId(@Param("bankAccount") String bankAccount,
                                                                                 @Param("vendorInfoId") String vendorInfoId);

    /**
     * 获取 根据供应商标识和当前登陆所属公司 下启用主账号的银行信息
     *
     * @param vendorInfoId
     * @param primaryFlag
     * @param status
     * @return
     */
    List<VendorBankAccount> selectVendorBankAccountsByConditions(@Param("vendorInfoId") String vendorInfoId,
                                                                 @Param("primaryFlag") Boolean primaryFlag,
                                                                 @Param("status") Integer status);

    /**
     * 根据供应商id 分页查询银行信息
     *
     * @param vendorInfoId
     * @param page
     * @return
     */
    List<VendorBankAccount> selectVendorBankAccountsByPages(@Param("vendorInfoId") String vendorInfoId,
                                                            @Param("status") Integer status,
                                                            Pagination page);


}
