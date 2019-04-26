package com.hand.hcf.app.payment.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.payment.domain.CompanyBank;
import com.hand.hcf.app.payment.domain.CompanyBankAuth;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by 刘亮 on 2017/9/28.
 */
public interface CompanyBankAuthMapper extends BaseMapper<CompanyBankAuth> {

    List<Long> getBankAccountIds(
            @Param("companyCode") String companyCode,
            @Param("companyName") String companyName
    );

    List<CompanyBankAuth> getCompanyBankAuthByAuth(
            @Param("empOID") String empOID,
            @Param("departmentId") Long departmentId,
            @Param("companyId") Long companyId,
            @Param("bankAccountIds") List<Long> bankAccountIds,
            Pagination page
    );
    List<CompanyBank> getCompanyBankByAuthNoPage(
            @Param("empOID") String empOID,
            @Param("departmentId") Long departmentId,
            @Param("companyId") Long companyId
//        @Param("paymentCode") String paymentCode,
//        @Param("currencyCode") String currencyCode,
//        @Param("paymentCompanyId") Long paymentCompanyId
    );

    List<Long> getPaymentCompanyInfo(
            @Param("empOID") String empOID,
            @Param("departmentId") Long departmentId,
            @Param("companyId") Long companyId,
            @Param("bankAccountIds") List<Long> bankAccountIds,
            @Param("currentDate") ZonedDateTime currentDate,
            Pagination page
    );
}
