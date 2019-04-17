package com.hand.hcf.app.mdata.responsibilityCenter.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.temp.ResponsibilityCenterTemp;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public interface ResponsibilityCenterTempMapper extends BaseMapper<ResponsibilityCenterTemp>{
    ImportResultDTO queryInfo(@Param("transactionID") String transactionID);

    void confirmImport(@Param("transactionID") String transactionID,
                       @Param("userId") Long userId,
                       @Param("tenantId") Long tenantId,
                       @Param("currentDate") ZonedDateTime currentDate);

    void updateExists(@Param("batchNumber") String batchNumber);
}
