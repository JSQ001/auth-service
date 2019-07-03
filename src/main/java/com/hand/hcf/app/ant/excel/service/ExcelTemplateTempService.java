package com.hand.hcf.app.ant.excel.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.ant.excel.domain.temp.ExcelTemplateTempDomain;
import com.hand.hcf.app.ant.excel.persistence.ExcelTemplateTempMapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @description:
 * @version: 1.0
 * @author: bo.liu02@hand-china.com
 * @date: 2019/6/21
 */
@Service
public class ExcelTemplateTempService extends BaseService<ExcelTemplateTempMapper, ExcelTemplateTempDomain> {

    /**
     * 删除两天以前的数据
     */
    public void deleteHistoryData(){
        baseMapper.delete(new EntityWrapper<ExcelTemplateTempDomain>().le("created_date", ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(-2)));
    }

    public ImportResultDTO queryImportResultInfo(String transactionUUID) {
        return baseMapper.queryImportResultInfo(transactionUUID);
    }

}
