package com.hand.hcf.app.ant.taxreimburse.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxCalculationTempDomain;
import com.hand.hcf.app.core.web.dto.ImportErrorDTO;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金计提临时类mapper
 * @date 2019/6/25 11:29
 */
public interface ExpTaxCalculationTempDomainMapper extends BaseMapper<ExpTaxCalculationTempDomain> {

    ImportResultDTO queryInfo(@Param("transactionId") String transactionId);

    List<ImportErrorDTO> queryErrorData(@Param("transactionId") String transactionId);
}
