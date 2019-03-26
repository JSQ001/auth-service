package com.hand.hcf.app.mdata.bank.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.bank.domain.BankInfoTempDomain;
import com.hand.hcf.core.web.dto.ImportErrorDTO;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/21 10:37
 */
public interface BankInfoTempMapper extends BaseMapper<BankInfoTempDomain> {
    void updateExists(@Param("batchNumber") String batchNumber);

    void updateBranchExists(@Param("batchNumber") String batchNumber);

    ImportResultDTO queryInfo(@Param("transactionID") String transactionID);

    List<ImportErrorDTO> queryErrorData(@Param("transactionId") String transactionId);

    void confirmImport(@Param("transactionId") String transactionId,
                       @Param("tenantId") Long tenantId,
                       @Param("currentDate") ZonedDateTime currentDate);

    List<Long> getImportBankInfoIds(@Param("transactionId") String transactionId);
}