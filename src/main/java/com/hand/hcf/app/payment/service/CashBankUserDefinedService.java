package com.hand.hcf.app.payment.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.payment.domain.CashBankData;
import com.hand.hcf.app.payment.domain.CashBankUserDefined;
import com.hand.hcf.app.payment.persistence.CashBankUserDefinedMapper;
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
public class CashBankUserDefinedService extends BaseService<CashBankUserDefinedMapper, CashBankUserDefined> {


    private final CashBankDataService cashBankDataService;

    private final BaseI18nService baseI18nService;

    CashBankUserDefinedService(CashBankDataService cashBankDataService, BaseI18nService baseI18nService) {
        this.cashBankDataService = cashBankDataService;
        this.baseI18nService = baseI18nService;
    }

    /**
     * 新增一个银行
     * <p>
     * CashBankUserDefined     * @return
     */
    public CashBankUserDefined createCshBank(CashBankUserDefined cashBankUserDefined) {
        if (cashBankUserDefined.getId() != null) {
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        cashBankUserDefined.setTenantId(OrgInformationUtil.getCurrentTenantId());
        //一个租户下自定义银行代码不能重复
        CashBankUserDefined cashBankUserDefined_code = this.selectOne(new EntityWrapper<CashBankUserDefined>()
                .eq("deleted", Boolean.valueOf(false))
                .eq("bank_code", cashBankUserDefined.getBankCode())
                .eq("tenant_id", cashBankUserDefined.getTenantId())
        );
        if (cashBankUserDefined_code != null) {
            throw new BizException(RespCode.PAYMENT_USER_DEFINED_BANK_CODE_NOT_UNIQUE);
        }
        //自定义银行不能与通用银行代码重复
        CashBankData cashBankData_code = cashBankDataService.selectOne(new EntityWrapper<CashBankData>()
                .eq("deleted", Boolean.valueOf(false))
                .eq("bank_code", cashBankUserDefined.getBankCode())
        );
        if (cashBankData_code != null) {
            throw new BizException(RespCode.PAYMENT_USER_DEFINED_BANK_CODE_NOT_REPEAT_WITH_UNIVERSAL_BANK);
        }
        this.insert(cashBankUserDefined);
        return cashBankUserDefined;

    }

    /**
     * 批量增加银行
     *
     * @param cashBankUserDefineds
     * @return
     */
    @Transactional
    public List<CashBankUserDefined> createCshBankBatch(List<CashBankUserDefined> cashBankUserDefineds) {
        for (CashBankUserDefined cashBankUserDefined : cashBankUserDefineds) {
            createCshBank(cashBankUserDefined);
        }
        return cashBankUserDefineds;
    }

    /**
     * 更新银行
     *
     * @param cashBankUserDefined
     * @return
     */
    public CashBankUserDefined updateCshBank(CashBankUserDefined cashBankUserDefined) {
        if (cashBankUserDefined.getId() == null) {
            throw new BizException(RespCode.SYS_ID_NULL);
        }
        this.updateById(cashBankUserDefined);
        return cashBankUserDefined;
    }

    /**
     * 批量更新银行
     *
     * @param cashBankUserDefineds
     * @return
     */
    @Transactional
    public List<CashBankUserDefined> updateCshBankBatch(List<CashBankUserDefined> cashBankUserDefineds) {
        for (CashBankUserDefined cashBankUserDefined : cashBankUserDefineds) {
            updateCshBank(cashBankUserDefined);
        }
        return cashBankUserDefineds;
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
    public List<CashBankUserDefined> getCshBankDataByCond(
            String bankCode,
            String bankName,
            String countryCode,
            String provinceCode,
            String cityCode,
            String districtCode,
            Boolean isEnabled,
            Page page) {
        List<CashBankUserDefined> list = baseMapper.selectPage(page, new EntityWrapper<CashBankUserDefined>()
                .where("deleted = false")
                .eq("tenant_id", OrgInformationUtil.getCurrentTenantId())
                .like(bankName != null, "bank_name", bankName)
                .eq(isEnabled != null, "enabled", isEnabled)
                .like(bankCode != null, "bank_code", bankCode)
                .eq(countryCode != null, "country_code", cityCode)
                .eq(provinceCode != null, "province_code", provinceCode)
                .eq(cityCode != null, "city_code", cityCode)
                .eq(districtCode != null, "district_code", districtCode)
                .orderBy("bank_code")
        );
        return baseI18nService.selectListTranslatedTableInfoWithI18nByEntity(list, CashBankUserDefined.class);
    }

    /**
     * 逻辑删除
     *
     * @param id
     */
    public void deleteCshBankById(Long id) {
        CashBankUserDefined cashBankUserDefined = this.selectById(id);
        cashBankUserDefined.setDeleted(Boolean.valueOf(true));
        this.updateById(cashBankUserDefined);
    }
}
