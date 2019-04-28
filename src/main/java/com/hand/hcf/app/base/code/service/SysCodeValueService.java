package com.hand.hcf.app.base.code.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.code.domain.SysCode;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.domain.SysCodeValueTemp;
import com.hand.hcf.app.base.code.enums.SysCodeEnum;
import com.hand.hcf.app.base.code.persistence.SysCodeMapper;
import com.hand.hcf.app.base.code.persistence.SysCodeValeMapper;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.tenant.persistence.TenantMapper;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
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
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private SysCodeMapper sysCodeMapper;

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
            SysCode sysCode = sysCodeMapper.selectById(temps.get(0).getCodeId());
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
            initOtherTenantBatch(sysCode, collect);
        }
        tempService.deleteTemp(transactionUUID);
        return true;
    }
    private void initOtherTenantBatch(SysCode sysCode, List<SysCodeValue> sysCodeValues) {
        Tenant tenant = tenantMapper.selectById(sysCode.getTenantId());
        if (tenant.getSystemFlag() && sysCode.getTypeFlag() == SysCodeEnum.INIT) {
            List<SysCode> codes = sysCodeMapper.selectList(new EntityWrapper<SysCode>()
                    .eq("code", sysCode.getCode()));
            if (!org.springframework.util.CollectionUtils.isEmpty(codes)) {
                sysCodeValues.forEach(v -> {
                    List<SysCodeValue> collect = codes
                            .stream()
                            .filter(e -> !e.getTenantId().equals(sysCode.getTenantId()))
                            .map(e -> {
                        SysCodeValue tmp = new SysCodeValue();
                        tmp.setCodeId(e.getId());
                        tmp.setName(v.getName());
                        tmp.setRemark(v.getRemark());
                        tmp.setValue(v.getValue());
                        return tmp;
                    }).collect(Collectors.toList());

                    this.insertBatch(collect);
                });
            }
        }
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
            if(e != null){
                e.setId(null);
                e.setCodeId(sysCode.getId());
                this.insert(e);
            }
        });
        return true;
    }
}
