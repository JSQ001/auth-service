package com.hand.hcf.app.mdata.contact.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.core.web.dto.ImportErrorDTO;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.contact.domain.MdataUserTempDomain;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

public interface MdataUserTempMapper extends BaseMapper<MdataUserTempDomain> {


    void updateEmployeeIdExists(@Param("batchNumber") String batchNumber);

    void updateEmailExists(@Param("batchNumber") String batchNumber);

    void updatePhoneExists(@Param("batchNumber") String batchNumber);

    UUID selectUserOidByEmployeeIdAndTenantId(@Param("employeeId") String employeeId);

    ImportResultDTO queryInfo(@Param("transactionId") String transactionId);

    List<ImportErrorDTO> queryErrorData(@Param("transactionId") String transactionId);

    Integer varifyBatchNumberExsits(@Param("transactionId") String transactionId);
}
