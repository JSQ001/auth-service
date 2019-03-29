package com.hand.hcf.app.mdata.currency.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.currency.domain.CurrencyRate;
import com.hand.hcf.app.mdata.currency.domain.CurrencyStatus;
import com.hand.hcf.app.mdata.currency.persistence.CurrencyStatusMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Service
public class CurrencyStatusService extends BaseService<CurrencyStatusMapper, CurrencyStatus> {

    private static final Logger log = LoggerFactory.getLogger(CurrencyStatusService.class);

    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private CurrencyStatusMapper currencyStatusMapper;

    public CurrencyStatus insertOrUpdateCurrencyStatus(String currencyCode, Boolean enable, Boolean enableAutoUpdate, Long setOfBooksID, Long tenantID) {
        CurrencyStatus currencyStatus = this.selectOne(new EntityWrapper<CurrencyStatus>().eq("currency_code", currencyCode).eq("set_of_books_id", setOfBooksID).eq("tenant_id", tenantID));
        //insert
        if (null == currencyStatus) {
            currencyStatus = new CurrencyStatus();
            currencyStatus.setCreatedBy(OrgInformationUtil.getCurrentUserId());
            currencyStatus.setCreatedDate(ZonedDateTime.now());
        }

        //common
        currencyStatus.setCurrencyCode(currencyCode);
        currencyStatus.setEnabled(enable);
        currencyStatus.setEnableAutoUpdate(enableAutoUpdate);
        currencyStatus.setSetOfBooksId(setOfBooksID);
        currencyStatus.setTenantId(tenantID);
        currencyStatus.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        currencyStatus.setLastUpdatedDate(ZonedDateTime.now());
        this.insertOrUpdate(currencyStatus);
        return currencyStatus;

    }

    public CurrencyStatus insertOrUpdateCurrencyStatus(CurrencyStatus currencyStatus) {
        this.insertOrUpdateCurrencyStatus(currencyStatus.getCurrencyCode(), currencyStatus.getEnabled(), currencyStatus.getEnableAutoUpdate(),
                currencyStatus.getSetOfBooksId(), currencyStatus.getTenantId());
        return currencyStatus;
    }


    public CurrencyStatus enableCurrencyStatus(UUID currencyRateOid, Boolean enable) {
        CurrencyRate currencyRate = currencyRateService.selectOne(new EntityWrapper<CurrencyRate>().eq("currency_rate_oid", currencyRateOid));
        if (null == currencyRate) {
            throw new BizException(RespCode.CURRENCY_5003);
        }
        CurrencyStatus currencyStatus = this.selectOne(new EntityWrapper<CurrencyStatus>().eq("currency_code", currencyRate.getCurrencyCode()).eq("set_of_books_id", currencyRate.getSetOfBooksId()).eq("tenant_id", currencyRate.getTenantId()));
        if (null == currencyStatus) {
            throw new BizException(RespCode.CURRENCY_5003);
        }
        currencyStatus.setEnabled(enable);
        currencyStatus.setLastUpdatedDate(ZonedDateTime.now());
        currencyStatus.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        this.updateById(currencyStatus);
        return currencyStatus;
    }

    public Boolean updateEnableAutoStatus(Long tenantId, Long setOfBooksId, String currencyCode, Boolean enableAutoUpdate) {
        List<CurrencyStatus> currencyStatusList = new ArrayList<CurrencyStatus>();
        //currencyCode不为空 ，更新单个币种状态
        if (!StringUtils.isEmpty(currencyCode)) {
            //账套未开启了自动汇率更新服务
            if (!checkAllEnableAutoStatusTrue(tenantId, setOfBooksId)) {
                throw new BizException(RespCode.CURRENCY_5004, new Object[]{tenantId, setOfBooksId});
            }
            CurrencyStatus currencyStatus = this.selectOne(new EntityWrapper<CurrencyStatus>().eq("set_of_books_id", setOfBooksId).eq("tenant_id", tenantId).eq("currency_code", currencyCode));
            if (null == currencyStatus) {
                throw new BizException(RespCode.CURRENCY_5011, new Object[]{tenantId, setOfBooksId, currencyCode});
            }
            currencyStatusList.add(currencyStatus);
            //currencyCode为空，更新账套下的所有币种 "自动更新"启用状态
        } else {
            currencyStatusList = this.selectList(new EntityWrapper<CurrencyStatus>().eq("set_of_books_id", setOfBooksId).eq("tenant_id", tenantId));
        }
        if (CollectionUtils.isEmpty(currencyStatusList)) {
            log.error("currencyStatusList is null ,select by setOfBooksId ={},tenantId={}", setOfBooksId, tenantId);
            return false;
        }
        currencyStatusList.stream().forEach(u -> {
            u.setEnableAutoUpdate(enableAutoUpdate);
            u.setLastUpdatedDate(ZonedDateTime.now());
            u.setLastUpdatedBy(OrgInformationUtil.getCurrentUserId());
        });
        return this.updateBatchById(currencyStatusList);
    }

    public Boolean checkAllEnableAutoStatusTrue(Long tenantId, Long setOfBooksId) {
        List<CurrencyStatus> currencyStatusList = this.selectList(new EntityWrapper<CurrencyStatus>().eq("set_of_books_id", setOfBooksId).eq("tenant_id", tenantId));
        if (CollectionUtils.isEmpty(currencyStatusList)) {
            log.error("currencyStatusList is null ,select by setOfBooksId ={},tenantId={}", setOfBooksId, tenantId);
            return false;
        }
        return currencyStatusList.stream().anyMatch(u -> u.getEnableAutoUpdate());
    }

    public List<Long> selectActiveSetOfBooksId(Boolean enable, Boolean enableAutoUpdate) {
        return currencyStatusMapper.selectActiveSetOfBooksIds(enable, enableAutoUpdate);
    }
}
