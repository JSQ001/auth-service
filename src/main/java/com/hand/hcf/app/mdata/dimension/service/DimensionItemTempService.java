package com.hand.hcf.app.mdata.dimension.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.dimension.domain.temp.DimensionItemTemp;
import com.hand.hcf.app.mdata.dimension.persistence.DimensionItemTempMapper;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.web.dto.ImportResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
public class DimensionItemTempService extends BaseService<DimensionItemTempMapper, DimensionItemTemp> {

    public ImportResultDTO queryResultInfo(String transactionID) {
        ImportResultDTO integerMap = baseMapper.queryInfo(transactionID);

        return integerMap;
    }

    @Transactional
    public Boolean confirmImport(String transactionID) {

        baseMapper.confirmImport(transactionID, OrgInformationUtil.getCurrentUserId(), ZonedDateTime.now());
        //插入完成后清空临时表
        baseMapper.delete(new EntityWrapper<DimensionItemTemp>().eq("batch_number", transactionID));
        return true;
    }

    @Transactional
    public void updateExists(String batchNumber){
        baseMapper.updateExists( batchNumber);
    }

    /**
     * 删除两天以前的数据
     */
    public void deleteHistoryData(){
        baseMapper.delete(new EntityWrapper<DimensionItemTemp>().le("created_date",ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(-2)));
    }

}

