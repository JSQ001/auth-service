package com.hand.hcf.app.base.code.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.code.domain.SysCodeValueTemp;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/26
 */
public interface SysCodeValueTempMapper extends BaseMapper<SysCodeValueTemp> {
    void checkData(@Param("batchNumber") String batchNumber,
                   @Param("codeId") Long customEnumerationId);

    ImportResultDTO queryImportResultInfo(@Param("transactionOid") String transactionOid);
}
