package com.hand.hcf.app.mdata.responsibilityCenter.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.responsibilityCenter.domain.temp.ResponsibilityCenterTemp;
import com.hand.hcf.app.mdata.responsibilityCenter.persistence.ResponsibilityCenterTempMapper;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ResponsibilityCenterTempService extends BaseService<ResponsibilityCenterTempMapper, ResponsibilityCenterTemp> {
    /**
     * 删除两天以前的数据
     */
    public void deleteHistoryData() {
        baseMapper.delete(new EntityWrapper<ResponsibilityCenterTemp>()
                .le("created_date", ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(-2)));
    }

    /**
     * 查询导入结果
     * @param transactionOid 批次号
     * @return
     */
    public ImportResultDTO queryResultInfo(String transactionOid) {
        return baseMapper.queryInfo(transactionOid);
    }

    /**
     * 确认导入
     * @param transactionOid 批次号
     * @return
     */
    public Boolean confirmImport(String transactionOid) {
        baseMapper.confirmImport(transactionOid, OrgInformationUtil.getCurrentUserId(),OrgInformationUtil.getCurrentTenantId(), ZonedDateTime.now());
        //插入完成后清空临时表
        baseMapper.delete(new EntityWrapper<ResponsibilityCenterTemp>().eq("batch_number", transactionOid));
        return true;
    }

    public void updateExists(String batchNumber) {
        baseMapper.updateExists( batchNumber);
    }
}
