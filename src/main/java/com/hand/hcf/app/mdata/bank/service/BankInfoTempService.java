package com.hand.hcf.app.mdata.bank.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.web.dto.ImportResultDTO;
import com.hand.hcf.app.mdata.bank.domain.BankInfoTempDomain;
import com.hand.hcf.app.mdata.bank.persistence.BankInfoTempMapper;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/21 10:42
 */
@Service
public class BankInfoTempService extends BaseService<BankInfoTempMapper, BankInfoTempDomain> {
    public ImportResultDTO queryResultInfo(String transactionID) {
        ImportResultDTO integerMap = baseMapper.queryInfo(transactionID);

        return integerMap;
    }

    @Transactional
    public void updateExists(String batchNumber){
        baseMapper.updateExists(batchNumber);
        baseMapper.updateBranchExists(batchNumber);
    }

    @Transactional
    public Boolean confirmImport(String transactionID) {

        baseMapper.confirmImport(transactionID, OrgInformationUtil.getCurrentTenantId(), ZonedDateTime.now());

        return true;
    }
    /**
     * 删除两天以前的数据
     */
    public void deleteHistoryData(){
        baseMapper.delete(new EntityWrapper<BankInfoTempDomain>().le("created_date", ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(-2)));
    }

    public List<Long> getImportBankInfoIds(String transactionID){
        return baseMapper.getImportBankInfoIds(transactionID);
    }

}
