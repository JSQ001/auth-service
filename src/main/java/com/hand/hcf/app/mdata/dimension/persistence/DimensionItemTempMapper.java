package com.hand.hcf.app.mdata.dimension.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.core.web.dto.ImportErrorDTO;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.dimension.domain.temp.DimensionItemTemp;
import org.apache.ibatis.annotations.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface DimensionItemTempMapper extends BaseMapper<DimensionItemTemp> {

    void updateExists(@Param("batchNumber") String batchNumber);

    ImportResultDTO queryInfo(@Param("transactionID") String transactionID);

    List<ImportErrorDTO> queryErrorData(@Param("transactionID") String transactionID);

    void confirmImport(@Param("transactionID") String transactionID,
                       @Param("userId") Long userId,
                       @Param("currentDate") ZonedDateTime currentDate);
}
