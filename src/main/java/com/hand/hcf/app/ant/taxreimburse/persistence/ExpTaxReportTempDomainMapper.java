package com.hand.hcf.app.ant.taxreimburse.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.taxreimburse.domain.ExpTaxReportTempDomain;
import com.hand.hcf.app.core.web.dto.ImportErrorDTO;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xu.chen02@hand-china.com
 * @version 1.0
 * @description: 税金申报数据导入临时domain--mapper
 * @date 2019/6/18 18:30
 */
public interface ExpTaxReportTempDomainMapper extends BaseMapper<ExpTaxReportTempDomain> {


    Integer varifyBatchNumberExsits(@Param("transactionId") String transactionId);

    ImportResultDTO queryInfo(@Param("transactionId") String transactionId);

    List<ImportErrorDTO> queryErrorData(@Param("transactionId") String transactionId);
}
