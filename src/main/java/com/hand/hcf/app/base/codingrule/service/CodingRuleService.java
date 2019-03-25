package com.hand.hcf.app.base.codingrule.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.base.code.domain.SysCodeValue;
import com.hand.hcf.app.base.code.service.SysCodeService;
import com.hand.hcf.app.base.codingrule.domain.CodingRule;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleDetail;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleObject;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleValue;
import com.hand.hcf.app.base.codingrule.domain.enums.CodingRuleTypeEnum;
import com.hand.hcf.app.base.codingrule.persistence.CodingRuleMapper;
import com.hand.hcf.app.base.system.constant.CacheConstants;
import com.hand.hcf.app.base.util.DataFilteringUtil;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.util.LoginInformationUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@CacheConfig(cacheNames = {CacheConstants.CODING_RULE})
public class CodingRuleService extends ServiceImpl<CodingRuleMapper, CodingRule> {

    @Autowired
    private SysCodeService sysCodeService;

    @Autowired
    private CodingRuleValueService codingRuleValueService;

    @Autowired
    private BaseI18nService baseI18nService;

    @Autowired
    private CodingRuleObjectService codingRuleObjectService;

    @Autowired
    private CodingRuleDetailService codingRuleDetailService;

    @Autowired
    private CodingRuleMapper codingRuleMapper;


    /**
     * 新增
     *
     * @param codingRule
     * @return
     */
    public CodingRule insetCodingRule(CodingRule codingRule) {
        DataFilteringUtil.getDataFilterCode(codingRule.getCodingRuleCode());
        if (codingRule.getId() != null) {
            //创建数据不允许有ID
            throw new BizException(RespCode.ID_NOT_ALLOWED_21001);
        }
        if (codingRule.getEnabled() != null) {
            codingRule.setEnabled(codingRule.getEnabled());
        }
        //后台校验-编码规则只能有一个启用
        CodingRule enabledCodingRule = getCodingRuleByCodingObjectId(codingRule.getCodingRuleObjectId());
        if (enabledCodingRule != null && codingRule.getEnabled()) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_ONE_ENABLED);
        }
        List<CodingRule> codingRules = this.selectList(new EntityWrapper<CodingRule>()
            .where("deleted = false")
            .eq("coding_rule_code", codingRule.getCodingRuleCode())
            .eq("coding_rule_object_id", codingRule.getCodingRuleObjectId()));
        if (codingRules.size() > 0) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_CODE_NOT_UNIQUE);
        }
        this.insert(codingRule);
        return codingRule;
    }

    /**
     * 更新
     *
     * @param codingRule
     * @return
     */
    @CacheEvict(key = "#codingRule.codingRuleObjectId.toString()")
    public CodingRule updateCodingRule(CodingRule codingRule) {
        if (codingRule.getId() == null) {
            //更新数据ID必填
            throw new BizException(RespCode.ID_REQUIRED_21002);
        }
        if (codingRule.getTenantId() == null) {
            codingRule.setTenantId(LoginInformationUtil.getCurrentTenantId());
        }
        if (codingRule.getEnabled() != null) {
            codingRule.setEnabled(codingRule.getEnabled());
        }
        //后台校验-编码规则只能有一个启用
        CodingRule codingRuleUpdate = getCodingRuleByCodingObjectId(codingRule.getCodingRuleObjectId());
        if (codingRuleUpdate != null && codingRule.getEnabled() && !codingRule.getId().equals(codingRuleUpdate.getId())) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_ONE_ENABLED);
        }

        if (codingRule.getEnabled()) {
            checkUniqueException(codingRule);
        }

        List<CodingRuleValue> codingRuleValues =
            codingRuleValueService.getCodingRuleByCodingRuleId(codingRule.getId());
        if (codingRuleValues.size() != 0) {
            //已使用这条编码规则后重置频率不能更改
            CodingRule codingRuleOld = baseMapper.selectById(codingRule.getId());
            if (!codingRuleOld.getResetFrequence().equals(codingRule.getResetFrequence())) {
                throw new BizException(RespCode.BUDGET_CODING_RULE_IS_USED);
            }
        }
        this.updateById(codingRule);
        return codingRule;
    }


    /**
     * 根据id删除
     *
     * @param codingRule
     */
    @CacheEvict(condition = "#codingRule!=null", key = "#codingRule.getCodingRuleObjectId().toString()")
    public void deleteCodingRule(CodingRule codingRule) {
        if (codingRule != null) {
            codingRule.setCodingRuleCode(codingRule.getCodingRuleCode() + "_DELETE_" + RandomStringUtils.randomNumeric(6));
            updateById(codingRule);
        } else {
            throw new BizException(RespCode.BUDGET_CODING_NOT_FOUND);
        }
    }

    /**
     * 获取一个启用的编码规则-新增编码规则时校验
     *
     * @param codingObjectId
     * @return
     */
    public CodingRule getCodingRuleByCodingObjectId(Long codingObjectId) {
        List<CodingRule> codingRuleObjects =
            this.selectList(new EntityWrapper<CodingRule>()
                .where("deleted = false")
                .eq("enabled", true)
                .eq("coding_rule_object_id", codingObjectId)
            );
        return codingRuleObjects.size() == 0 ? null : codingRuleObjects.get(0);
    }

    /**
     * 获取一个启用的编码规则-生成单据编号时使用
     *
     * @param codingObjectId 编码规则定义id
     * @return
     */
    @Cacheable(key = "#codingObjectId.toString()")
    public CodingRule getCodingRuleByCond(Long codingObjectId) {
        List<CodingRule> codingRuleObjects =
            this.selectList(new EntityWrapper<CodingRule>()
                .eq("deleted", false)
                .eq("enabled", true)
                .eq("coding_rule_object_id", codingObjectId)
            );
        if (codingRuleObjects.size() != 1) {
            //编码规则启用多个或没有!
            throw new BizException(RespCode.BUDGET_CODING_RULE_ENABLED_EXCEPTION);
        }
        return codingRuleObjects.get(0);
    }

    /**
     * 通用查询-分页
     *
     * @param codingRuleObjectId 编码规则定义id
     * @param codingRuleCode     编码规则代码
     * @param codingRuleName     编码规则名称
     * @param isEnabled          是否启用
     * @param page               页码
     * @return
     */
    public Page<CodingRule> getCodingRuleByCond(
        Long codingRuleObjectId,
        String codingRuleCode,
        String codingRuleName,
        Boolean isEnabled,
        Page page) {
        List<CodingRule> codingRules = baseMapper.selectPage(page, new EntityWrapper<CodingRule>()
            .where("deleted = false")
            .like(codingRuleCode != null, "coding_rule_code", codingRuleCode)
            .like(codingRuleName != null, "coding_rule_name", codingRuleName)
            .eq(isEnabled != null, "enabled", isEnabled)
            .eq(codingRuleObjectId != null, "coding_rule_object_id", codingRuleObjectId)
            .orderBy("coding_rule_code")
        );
        codingRules.stream().forEach(u -> setCodingRuleById(u));
        page.setRecords(codingRules);
        return page;
    }

    public void setCodingRuleById(CodingRule u) {
        Map<String, List<Map<String, String>>> i18nMap = baseI18nService.getI18nMap(CodingRule.class, u.getId());
        u.setI18n(i18nMap);
        SysCodeValue sysCodeValue = sysCodeService.getValueBySysCodeAndValue(CodingRuleTypeEnum.RESET_FREQUENCE.getId().toString(), u.getResetFrequence());
        u.setResetFrequenceName(sysCodeValue == null ? "" : sysCodeValue.getName());
    }

    private void checkUniqueException(CodingRule codingRule) {
        CodingRuleObject codingRuleObject = codingRuleObjectService.selectById(codingRule.getCodingRuleObjectId());
        List<CodingRuleDetail> codingRuleDetailList = codingRuleDetailService.getCodingRuleDetailByCond(codingRule.getId());
        //校验有没有编码规则明细
        if (codingRuleDetailList == null || codingRuleDetailList.size() == 0) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_NOT_FOUND);
        } else {
            //编码规则明细合成值
            String detailSynthesis = "";
            //固定字段
            String segmentTypeFixedFields = "10";
            //日期格式
            String segmentTypeDateFormat = "20";
            //单据类型代码
            String segmentTypeDocumentTypeCode = "30";
            //公司代码
            String segmentTypeCompanyCode = "40";
            //序列号
            String segmentTypeSerialNumber = "50";
            boolean hasCompanyCodeFlag = false;
            boolean hasSerialNumberFlag = false;
            boolean hasDateFormatFlag = false;
            for (CodingRuleDetail codingRuleDetail : codingRuleDetailList) {
                if (segmentTypeFixedFields.equals(codingRuleDetail.getSegmentType())) {
                    detailSynthesis += codingRuleDetail.getSegmentValue();
                }
                if (segmentTypeDateFormat.equals(codingRuleDetail.getSegmentType())) {
                    if (StringUtils.isNotBlank(codingRuleDetail.getDateFormat())) {
                        //如果重置频率是每月，日期格式必须带有年月
                        if ("PERIOD".equals(codingRule.getResetFrequence())) {
                            Integer indexYY = StringUtils.indexOf(codingRuleDetail.getDateFormat(), "YY");
                            Integer indexMM = StringUtils.indexOf(codingRuleDetail.getDateFormat(), "MM");
                            if (indexYY.equals(-1) || indexMM.equals(-1)) {
                                throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_DATE_FORMAT_MONTH);
                            }
                        }
                        //如果重置频率是每年，日期格式必须带有年
                        if ("YEAR".equals(codingRule.getResetFrequence())) {
                            Integer indexYY = StringUtils.indexOf(codingRuleDetail.getDateFormat(), "YY");
                            if (indexYY.equals(-1)) {
                                throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_DATE_FORMAT_YEAR);
                            }
                        }
                    }
                    detailSynthesis += codingRuleDetail.getDateFormat();
                    hasDateFormatFlag = true;
                }
                if (segmentTypeDocumentTypeCode.equals(codingRuleDetail.getSegmentType())) {
                    detailSynthesis += codingRuleObject.getDocumentTypeCode();
                }
                if (segmentTypeCompanyCode.equals(codingRuleDetail.getSegmentType())) {
                    if ("".equals(codingRuleObject.getCompanyCode())) {
                        //如果无应用公司先写死
                        detailSynthesis += "tenant";
                    } else {
                        detailSynthesis += codingRuleObject.getCompanyCode();
                    }
                    hasCompanyCodeFlag = true;
                }
                if (segmentTypeSerialNumber.equals(codingRuleDetail.getSegmentType())) {
                    //有序列号不管几位都作为判断条件
                    detailSynthesis += "SerialNumber";
                    hasSerialNumberFlag = true;
                }
            }
            //明细中必须有序列号
            if (!hasSerialNumberFlag) {
                throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_SEQUENCE_NOT_FOUND);
            }
            //当重置频率不为从不(即每月和每年)时明细中必须要有日期格式
            if (!("NEVER".equals(codingRule.getResetFrequence())) && !hasDateFormatFlag) {
                throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_DATE_FORMAT_NOT_FOUND);
            }
            //当编码规则定义有应用公司时明细必须有公司代码
            if (StringUtils.isNotEmpty(codingRuleObject.getCompanyCode()) && !hasCompanyCodeFlag) {
                throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_SEGMENT_TYPE_40);
            }
            List<CodingRule> codingRuleList = this.selectList(new EntityWrapper<CodingRule>()
                .where("deleted=false")
                .eq("tenant_id", LoginInformationUtil.getCurrentTenantId())
                .isNotNull("detail_synthesis")
            );
            for (CodingRule codingRuleDetailSynthesis : codingRuleList) {
                if (codingRuleDetailSynthesis.getDetailSynthesis().equals(detailSynthesis)) {
                    if (!(codingRuleDetailSynthesis.getId().equals(codingRule.getId()))){
                        throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_SYNTHESIS_NOT_UNIQUE);
                    }
                }
            }
            codingRule.setDetailSynthesis(detailSynthesis);
        }
    }

    public void updateDetailSynthesis(CodingRule codingRule){
        codingRuleMapper.updateDetailSynthesis(codingRule);
    }
}
