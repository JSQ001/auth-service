package com.hand.hcf.app.ant.excel.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.ant.excel.domain.temp.ExcelTemplateTempDomain;
import com.hand.hcf.app.core.web.dto.ImportErrorDTO;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/21
 */
public interface  ExcelTemplateTempMapper extends BaseMapper<ExcelTemplateTempDomain>{

    ImportResultDTO queryImportResultInfo(@Param("transactionOid") String transactionOid);

    List<ImportErrorDTO> queryErrorData(@Param("transactionId") String transactionId);
}
