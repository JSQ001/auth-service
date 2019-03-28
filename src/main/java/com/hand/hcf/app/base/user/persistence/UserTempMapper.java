package com.hand.hcf.app.base.user.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.user.domain.UserTempDomain;
import com.hand.hcf.core.web.dto.ImportErrorDTO;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface UserTempMapper extends BaseMapper<UserTempDomain> {

    void insertToUser(@Param("batchNumber") String batchNumber,
                      @Param("userId") Long userId,
                      @Param("currentDate") ZonedDateTime currentDate,
                      @Param("tenantId") Long tenantId);


    void insertToContact(@Param("batchNumber") String batchNumber,
                         @Param("userId") Long userId,
                         @Param("currentDate") ZonedDateTime currentDate);

    void insertToPhone(@Param("batchNumber") String batchNumber,
                       @Param("userId") Long userId,
                       @Param("currentDate") ZonedDateTime currentDate);

    void insertToDepartmentUser(@Param("batchNumber") String batchNumber,
                                @Param("userId") Long userId,
                                @Param("currentDate") ZonedDateTime currentDate);

    void updateEmployeeIdExists(@Param("batchNumber") String batchNumber);

    void updateEmailExists(@Param("batchNumber") String batchNumber);

    void updatePhoneExists(@Param("batchNumber") String batchNumber);

    UUID selectUserOidByEmployeeIdAndTenantId(@Param("employeeId") String employeeId);

    ImportResultDTO queryInfo(@Param("transactionId") String transactionId);

    List<ImportErrorDTO> queryErrorData(@Param("transactionId") String transactionId);

    Boolean varifyBatchNumberExsits(@Param("transactionId") String transactionId);
}
