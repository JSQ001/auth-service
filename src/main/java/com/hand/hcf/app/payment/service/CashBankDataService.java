package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.payment.domain.CashBankData;
import com.hand.hcf.app.payment.persistence.CashBankDataMapper;
import com.hand.hcf.app.payment.utils.RespCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

;

/**
 * @author dong.liu on 2017-11-07
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CashBankDataService extends BaseService<CashBankDataMapper, CashBankData> {

    private final CashBankDataMapper cashBankMapper;

    private final BaseI18nService baseI18nService;

    CashBankDataService(CashBankDataMapper cashBankMapper, BaseI18nService baseI18nService) {
        this.cashBankMapper = cashBankMapper;
        this.baseI18nService = baseI18nService;
    }

    /**
     * 新增一个银行
     * <p>
     * cashBankData
     *
     * @return
     */
    public CashBankData createCshBank(CashBankData cashBankData) {
        if (cashBankData.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        //处理null值为""
        if(cashBankData.getDistrictName()==null){
            cashBankData.setDistrictName("");
        }
        CashBankData cashBankData_code = this.selectOne(new EntityWrapper<CashBankData>()
                .eq("deleted", Boolean.valueOf(false))
                .eq("bank_code", cashBankData.getBankCode())
        );
        if (cashBankData_code != null) {
            throw new BizException(RespCode.PAYMENT_UNIVERSAL_BANK_CODE_NOT_UNIQUE);
        }
        cashBankMapper.insert(cashBankData);
        return cashBankData;

    }

    /**
     * 批量增加银行
     *
     * @param cashBankDatas
     * @return
     */
    @Transactional
    public List<CashBankData> createCshBankBatch(List<CashBankData> cashBankDatas) {
        for (CashBankData cashBankData : cashBankDatas) {
            createCshBank(cashBankData);
        }
        return cashBankDatas;
    }

    /**
     * 更新银行
     *
     * @param cashBankData
     * @return
     */
    public CashBankData updateCshBank(CashBankData cashBankData) {
        if (cashBankData.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        this.updateById(cashBankData);
        return cashBankData;
    }

    /**
     * 批量更新银行
     *
     * @param cashBankDatas
     * @return
     */
    @Transactional
    public List<CashBankData> updateCshBankBatch(List<CashBankData> cashBankDatas) {
        for (CashBankData cashBankData : cashBankDatas) {
            updateCshBank(cashBankData);
        }
        return cashBankDatas;
    }

    /**
     * 通用查询-分页
     *
     * @param bankCode  银行代码
     * @param bankName  银行名称
     * @param isEnabled 是否启用
     * @param page      页码
     * @return
     */
    public List<CashBankData> getCshBankDataByCond(
            String bankCode,
            String bankName,
            Boolean isEnabled,
            Page page) {
        List<CashBankData> list = cashBankMapper.selectPage(page, new EntityWrapper<CashBankData>()
                .where("deleted = false")
                .like(bankName != null, "bank_name", bankName)
                .eq(isEnabled != null, "enabled", isEnabled)
                .like(bankCode != null, "bank_code",bankCode)
                .orderBy("bank_code")
        );
        return baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(list, CashBankData.class);
    }

}
