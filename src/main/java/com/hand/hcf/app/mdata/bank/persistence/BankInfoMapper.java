package com.hand.hcf.app.mdata.bank.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.bank.domain.BankInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 银行信息数据存储层
 * Created by Strive on 18/1/26.
 */
public interface BankInfoMapper extends BaseMapper<BankInfo> {
    /**
     * 根据租户id验证银行编码是否存在
     * @param tenantId：租户id
     * @param bankCode：银行编码
     * @return
     */
    Integer checkBankCodeIsExist(@Param("tenantId") Long tenantId, @Param("bankCode") String bankCode);

    /**
     * 根据租户id和条件查询银行信息
     * @param isAll：是否查询所有
     * @param tenantId：租户id  0为系统银行
     * @param systemTenantId：系统系统
     * @param keyword：银行分支银行
     * @param bankCode：银行编码
     * @parma countryCode：国家编码
     * @param openAccount：开户地址
     * @param page：分页对象
     * @return
     */
    List<BankInfo> findByTenantIdAndBankBranchNameContaining(@Param("isAll") Boolean isAll, @Param("tenantId") Long tenantId, @Param("systemTenantId") Long systemTenantId, @Param("keyword") String keyword, @Param("bankCode") String bankCode,
                                                             @Param("countryCode") String countryCode, @Param("openAccount") String openAccount, @Param("cityCode") String cityCode, @Param("swiftCode") String swiftCode, @Param("enable") Boolean enable, Pagination page);

    /**
     * 查询所有银行编码
     * @return：银行编码
     */
    List<String> findAllCountryCode();

    /**
     * 根据租户id和支行名称银行信息
     * @param tenantId：租户id
     * @param bankBranchName：分支银行名称
     * @return
     */
    BankInfo findByTenantIdAndBankBranchName(@Param("tenantId") Long tenantId, @Param("bankBranchName") String bankBranchName);

    /**
     * 分页查询未删除银行信息
     *
     * @param page：分页对象
     * @return
     */
    List<BankInfo> findAllBankInfo(Pagination page);

    /**
     * 根据租户id和银行编码查询银行信息
     * @param tenantId：租户id
     * @param bankCode：银行编码
     * @return
     */
    BankInfo findByTenantIdAndBankCode(@Param("tenantId") Long tenantId, @Param("bankCode") String bankCode);
}
