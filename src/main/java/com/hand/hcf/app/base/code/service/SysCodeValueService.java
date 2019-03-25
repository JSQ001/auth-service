package com.hand.hcf.app.base.code.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.code.domain.SysCode;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.domain.SysCodeValueTemp;
import com.hand.hcf.app.base.code.persistence.SysCodeValeMapper;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  值列表值
 * </p>
 *
 * @Author: bin.xie
 * @Date: 2018/12/25
 */
@Service
public class SysCodeValueService extends BaseService<SysCodeValeMapper, SysCodeValue> {
    @Autowired
    private BaseI18nService baseI18nService;
    @Autowired
    private SysCodeValueTempService tempService;

    public SysCodeValue getById(Long id) {
        SysCodeValue sysCodeValue = baseI18nService.selectOneTranslatedTableInfoWithI18n(id, SysCodeValue.class);
        return sysCodeValue;
    }

    public List<SysCodeValue> listValueBySysCodeIdConditionEnabled(Long id, Boolean enabled) {
        List<SysCodeValue> sysCodeValues = this.selectList(new EntityWrapper<SysCodeValue>()
                .eq("code_id", id)
                .eq(enabled != null, "enabled", enabled));
        return sysCodeValues;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateItemStatusByIds(List<SysCodeValue> sysCodeValues, Boolean enable) {
        sysCodeValues.stream().forEach(e -> {
            e.setEnabled(enable == null ? false : enable);
        });
        return this.updateBatchById(sysCodeValues);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmTemp(String transactionUUID) {
        List<SysCodeValueTemp> temps = tempService.selectList(new EntityWrapper<SysCodeValueTemp>()
                .eq("batch_number", transactionUUID)
                .eq("error_flag", 0));
        if (!CollectionUtils.isEmpty(temps)) {
            List<SysCodeValue> collect = temps.stream().map(e -> {
                SysCodeValue item = new SysCodeValue();
                item.setEnabled("Y".equals(e.getEnabledStr()));
                item.setCodeId(e.getCodeId());
                item.setName(e.getName());
                item.setValue(e.getValue());
                item.setRemark(e.getRemark());
                return item;
            }).collect(Collectors.toList());
            this.insertBatch(collect);
        }
        tempService.deleteTemp(transactionUUID);
        return true;
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean initTenantBySysCode(SysCode sysCode,Long codeId) {
        List<SysCodeValue> sysCodeValues = this.selectList(new EntityWrapper<SysCodeValue>()
                .eq("code_id", codeId)
                .orderBy("value"));
        if (CollectionUtils.isEmpty(sysCodeValues)){
            return true;
        }
        List<SysCodeValue> result = baseI18nService.selectListTranslatedTableInfoWithI18n(sysCodeValues.stream().map(SysCodeValue::getId).collect(Collectors.toList()), SysCodeValue.class);
        result.forEach(e -> {
            e.setId(null);
            e.setCodeId(sysCode.getId());
            this.insert(e);
        });
        return true;
    }
}
