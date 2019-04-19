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
import com.hand.hcf.app.base.codingrule.persistence.CodingRuleDetailMapper;
import com.hand.hcf.app.base.system.constant.CacheConstants;
import com.hand.hcf.app.base.system.constant.SyncLockPrefix;
import com.hand.hcf.app.base.util.DataFilteringUtil;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.redisLock.annotations.LockedObject;
import com.hand.hcf.app.core.redisLock.annotations.SyncLock;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = {CacheConstants.CODING_RULE_DETAIL})
public class CodingRuleDetailService extends ServiceImpl<CodingRuleDetailMapper, CodingRuleDetail> {


    @Autowired
    private SysCodeService sysCodeService;

    @Autowired
    private CodingRuleValueService codingRuleValueService;

    @Autowired
    private CodingRuleObjectService codingRuleObjectService;

    @Autowired
    private CodingRuleService codingRuleService;

    /**
     * 新增一个编码规则明细
     *
     * @param codingRuleDetail
     * @return
     */
    @CacheEvict(key = "#codingRuleDetail.codingRuleId.toString()")
    @SyncLock(lockPrefix = SyncLockPrefix.CODING_RULE,waiting = true,timeOut = 3000)
    public CodingRuleDetail insertCodingRuleDetail(@LockedObject(lockKeyField = "codingRuleId") CodingRuleDetail codingRuleDetail) {
        if (codingRuleDetail.getId() != null) {
            //创建数据不允许有ID
            throw new BizException(RespCode.ID_NOT_ALLOWED_21001);
        }
        if (codingRuleDetail.getTenantId() == null) {
            codingRuleDetail.setTenantId(LoginInformationUtil.getCurrentTenantId());
        }
        if (codingRuleDetail.getEnabled() != null) {
            codingRuleDetail.setEnabled(codingRuleDetail.getEnabled());
        }
        //校验
        checkForException(codingRuleDetail);
        this.insert(codingRuleDetail);
        //更新编码规则detailSynthesis
        updateDetailSynthesis(codingRuleDetail);
        return codingRuleDetail;
    }

    /**
     * 更新一个编码规则明细
     *
     * @param codingRuleDetail
     * @return
     */
    @CacheEvict(key = "#codingRuleDetail.codingRuleId.toString()")
    @SyncLock(lockPrefix = SyncLockPrefix.CODING_RULE,waiting = true,timeOut = 3000)
    public CodingRuleDetail updateCodingRuleDetail(@LockedObject(lockKeyField = "codingRuleId") CodingRuleDetail codingRuleDetail) {
        if (codingRuleDetail.getId() == null) {
            //更新数据ID必填
            throw new BizException(RespCode.ID_REQUIRED_21002);
        }
        if (codingRuleDetail.getEnabled() != null) {
            codingRuleDetail.setEnabled(codingRuleDetail.getEnabled());
        }
        //校验
        checkForException(codingRuleDetail);
        this.updateById(codingRuleDetail);
        //更新编码规则detailSynthesis
        updateDetailSynthesis(codingRuleDetail);
        return codingRuleDetail;
    }

    /**
     * 根据id删除一个编码规则明细
     *
     * @param codingRuleDetail
     */
    @CacheEvict(key = "#codingRuleDetail.codingRuleId.toString()")
    @SyncLock(lockPrefix = SyncLockPrefix.CODING_RULE,waiting = true,timeOut = 3000)
    public void deleteCodingRuleDetail(@LockedObject(lockKeyField = "codingRuleId") CodingRuleDetail codingRuleDetail) {
        CodingRule codingRule = codingRuleService.selectById(codingRuleDetail.getCodingRuleId());
        if (codingRule.getEnabled()) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_OPERATION);
        }
        List<CodingRuleValue> codingRuleValues =
            codingRuleValueService.getCodingRuleByCodingRuleId(codingRuleDetail.getCodingRuleId());
        if (codingRuleValues.size() != 0) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_IS_USED);
        }
        codingRuleDetail.setDeleted(true);
        this.updateById(codingRuleDetail);
        //更新编码规则detailSynthesis
        updateDetailSynthesis(codingRuleDetail);
    }

    /**
     * 获取编码规则明细-生成单据编号时使用
     *
     * @param codingRuleId 编码规则id
     * @return
     */
    //jiu.zhao redis
    //@Cacheable(key = "#codingRuleId.toString()")
    public List<CodingRuleDetail> getCodingRuleDetailByCond(Long codingRuleId) {
        return baseMapper.selectList(new EntityWrapper<CodingRuleDetail>()
            .where("deleted = false")
            .eq("enabled", true)
            .eq("coding_rule_id", codingRuleId)
            .orderBy("sequence_number")
        );
    }

    /**
     * 通用查询-分页
     *
     * @param codingRuleId 编码规则id
     * @param isEnabled    是否启用
     * @param page         页码
     * @return
     */
    public Page<CodingRuleDetail> getCodingRuleDetailByCond(
        Long codingRuleId,
        Boolean isEnabled,
        Page page) {
        List<CodingRuleDetail> codingRuleDetails = baseMapper.selectPage(page, new EntityWrapper<CodingRuleDetail>()
            .where("deleted = false")
            .eq(isEnabled != null, "enabled", isEnabled)
            .eq(codingRuleId != null, "coding_rule_id", codingRuleId)
            .orderBy("sequence_number")
        );
        codingRuleDetails.stream().forEach(u -> {
            setCodingRuleDetail(u);
        });
        page.setRecords(codingRuleDetails);
        return page;
    }

    public void setCodingRuleDetail(CodingRuleDetail u) {
        SysCodeValue sysCodeValue = sysCodeService.getValueBySysCodeAndValue(CodingRuleTypeEnum.SEGMENT_TYPE.getId().toString(), u.getSegmentType());
        u.setSegmentName(sysCodeValue == null ? "" : sysCodeValue.getName());
    }

    /**
     * 插入和更新时共用代码
     * 同一规则中顺序号不能重复，如果已经有用明细生成过值不能进行更新和插入
     *
     * @param codingRuleDetail
     * @return
     */
    public void checkForException(CodingRuleDetail codingRuleDetail) {
        if (StringUtils.isNotBlank(codingRuleDetail.getSegmentValue())) {
            DataFilteringUtil.getDataFilterCode(codingRuleDetail.getSegmentValue());
        }
        CodingRule codingRule = codingRuleService.selectById(codingRuleDetail.getCodingRuleId());
        if (codingRule.getEnabled()) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_OPERATION);
        }
        List<CodingRuleValue> codingRuleValues =
            codingRuleValueService.getCodingRuleByCodingRuleId(codingRuleDetail.getCodingRuleId());
        if (codingRuleValues.size() != 0) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_IS_USED);
        }
        CodingRuleDetail codingRuleDetailOld = this.selectOne(new EntityWrapper<CodingRuleDetail>()
            .where("deleted = false")
            .eq("coding_rule_id", codingRuleDetail.getCodingRuleId())
            .eq("sequence_number", codingRuleDetail.getSequence()));
        CodingRuleDetail codingRuleDetailSegmentType = this.selectOne(new EntityWrapper<CodingRuleDetail>()
            .where("deleted = false")
            .eq("coding_rule_id", codingRuleDetail.getCodingRuleId())
            .eq("segment_type", codingRuleDetail.getSegmentType()));
        if (codingRuleDetailOld != null && !codingRuleDetailOld.getId().equals(codingRuleDetail.getId())) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_SEQUENCE_NOT_UNIQUE);
        }
        if (codingRuleDetailSegmentType != null && !codingRuleDetailSegmentType.getId().equals(codingRuleDetail.getId())) {
            throw new BizException(RespCode.BUDGET_CODING_RULE_DETAIL_SEGMENT_TYPE_NOT_UNIQUE);
        }
    }

    /**
     * 更新编码规则中detail_synthesis字段
     *
     * @param codingRuleDetail
     */
    /**
     * 更新编码规则中detail_synthesis字段
     *
     * @param codingRuleDetail
     */
    public void updateDetailSynthesis(CodingRuleDetail codingRuleDetail) {
        String detailSynthesis = null;
        List<CodingRuleDetail> codingRuleDetailList = getCodingRuleDetailByCond(codingRuleDetail.getCodingRuleId());
        CodingRule codingRule = codingRuleService.selectById(codingRuleDetail.getCodingRuleId());
        CodingRuleObject codingRuleObject = codingRuleObjectService.selectById(codingRule.getCodingRuleObjectId());
        if (codingRuleDetailList != null && codingRuleDetailList.size() != 0) {
            //编码规则明细合成值
            detailSynthesis = "";
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
            for (CodingRuleDetail codingRuleDetailOld : codingRuleDetailList) {
                if (segmentTypeFixedFields.equals(codingRuleDetailOld.getSegmentType())) {
                    detailSynthesis += codingRuleDetailOld.getSegmentValue();
                }
                if (segmentTypeDateFormat.equals(codingRuleDetailOld.getSegmentType())) {
                    detailSynthesis += codingRuleDetailOld.getDateFormat();
                }
                if (segmentTypeDocumentTypeCode.equals(codingRuleDetailOld.getSegmentType())) {
                    detailSynthesis += codingRuleObject.getDocumentTypeCode();
                }
                if (segmentTypeCompanyCode.equals(codingRuleDetailOld.getSegmentType())) {
                    if ("".equals(codingRuleObject.getCompanyCode())) {
                        //如果无应用公司先写死
                        detailSynthesis += "tenant";
                    } else {
                        detailSynthesis += codingRuleObject.getCompanyCode();
                    }
                }
                if (segmentTypeSerialNumber.equals(codingRuleDetailOld.getSegmentType())) {
                    //有序列号不管几位都作为判断条件
                    detailSynthesis += "SerialNumber";
                }
            }
        }
        if (StringUtils.isNotBlank(detailSynthesis)) {
            if (detailSynthesis.toCharArray().length > 50) {
                throw new BizException(RespCode.BUDGET_CODING_RULE_DETAILSYNTHESIS);
            }
        }
        codingRule.setDetailSynthesis(detailSynthesis);
        codingRuleService.updateDetailSynthesis(codingRule);
    }
}
