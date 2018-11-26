package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.base.domain.DataAuthorityRuleDetailValue;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.domain.DataAuthorityRule;
import com.hand.hcf.app.base.domain.DataAuthorityRuleDetail;
import com.hand.hcf.app.base.persistence.DataAuthorityRuleMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:49
 * @remark
 */
@Service
@AllArgsConstructor
public class DataAuthorityRuleService extends BaseService<DataAuthorityRuleMapper,DataAuthorityRule>{

    private final DataAuthorityRuleMapper dataAuthorityRuleMapper;
    private final DataAuthorityRuleDetailService dataAuthorityRuleDetailService;
    private final DataAuthorityRuleDetailValueService dataAuthorityRuleDetailValueService;
    private final BaseI18nService baseI18nService;

    /**
     * 添加数据权限规则
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthorityRule saveDataAuthorityRule(DataAuthorityRule entity){
        Integer count = dataAuthorityRuleMapper.selectCount(new EntityWrapper<DataAuthorityRule>()
                .eq("data_authority_id", entity.getDataAuthorityId())
                .eq("data_authority_rule_name", entity.getDataAuthorityRuleName())
                .eq("deleted",false)
                .ne(entity.getId() != null,"id",entity.getId()));
        if(count > 0){
            throw new BizException(RespCode.DATA_AUTHORITY_RULE_EXISTS);
        }
        if(entity.getId() != null){
            dataAuthorityRuleMapper.updateById(entity);
        }else {
            dataAuthorityRuleMapper.insert(entity);
        }
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRuleDetails())){
            dataAuthorityRuleDetailService.saveDataAuthorityRuleDetailBatch(entity.getDataAuthorityRuleDetails(),entity.getId(),entity.getDataAuthorityId());
        }
        return entity;
    }

    @Transactional
    public DataAuthorityRule saveDataAuthorityRule(DataAuthorityRule entity,Long dataAuthorityId){
        if(entity.getDataAuthorityId() == null){
            entity.setDataAuthorityId(dataAuthorityId);
        }
        return saveDataAuthorityRule(entity);
    }

    /**
     * 批量添加数据权限规则
     * @param entities
     * @return
     */
    @Transactional
    public List<DataAuthorityRule> saveDataAuthorityRuleBatch(List<DataAuthorityRule> entities){
        entities.forEach(entity -> saveDataAuthorityRule(entity));
        return entities;
    }

    @Transactional
    public List<DataAuthorityRule> saveDataAuthorityRuleBatch(List<DataAuthorityRule> entities,Long dataAuthorityId) {
        entities.forEach(entity -> saveDataAuthorityRule(entity, dataAuthorityId));
        return entities;
    }

    /**
     * 根据权限ID获取权限
     * @param dataAuthorityId
     * @return
     */
    public List<DataAuthorityRule> queryDataAuthorityRules(Long dataAuthorityId){
        List<DataAuthorityRule> dataAuthRules = dataAuthorityRuleMapper.selectList(new EntityWrapper<DataAuthorityRule>()
                .eq("data_authority_id", dataAuthorityId));
        dataAuthRules.forEach(dataAuthorityRule -> {
            Map<String, List<Map<String, String>>> i18nMap = baseI18nService.getI18nMap(DataAuthorityRule.class, dataAuthorityRule.getId());
            dataAuthorityRule.setI18n(i18nMap);
            List<DataAuthorityRuleDetail> dataAuthorityRuleDetails = dataAuthorityRuleDetailService.queryDataAuthorityRuleDetailsByRuleId(dataAuthorityRule.getId());
            dataAuthorityRule.setDataAuthorityRuleDetails(dataAuthorityRuleDetails);
        });
        return dataAuthRules;
    }

    private void deleteDataAuthRule(DataAuthorityRule dataAuthorityRule){
        dataAuthorityRule.setDeleted(true);
        dataAuthorityRule.setDataAuthorityRuleName(dataAuthorityRule.getDataAuthorityRuleName() + "_DELETED_" + RandomStringUtils.randomNumeric(6));
        dataAuthorityRuleMapper.updateById(dataAuthorityRule);
    }

    /**
     * 根据数据权限ID删除规则
     * @param authId
     */
    @Transactional
    public void deleteDataAuthRuleByAuthId(Long authId){
        List<DataAuthorityRule> dataAuths = dataAuthorityRuleMapper.selectList(new EntityWrapper<DataAuthorityRule>().eq("data_authority_id", authId));
        dataAuths.stream().forEach(dataAuthorityRule -> {
            deleteDataAuthRule(dataAuthorityRule);
        });
    }

    @Transactional
    public void deleteDataAuthRuleAndDetail(Long id){
        DataAuthorityRule dataAuthorityRule = dataAuthorityRuleMapper.selectById(id);
        deleteDataAuthRule(dataAuthorityRule);
        List<DataAuthorityRuleDetail> ruleDetails = dataAuthorityRuleDetailService.selectList(new EntityWrapper<DataAuthorityRuleDetail>().eq("data_authority_rule_id", id));
        if(CollectionUtils.isNotEmpty(ruleDetails)){
            dataAuthorityRuleDetailValueService.delete(new EntityWrapper<DataAuthorityRuleDetailValue>().in("data_auth_rule_detail_id",ruleDetails.stream().map(ruleDetail -> ruleDetail.getId()).collect(Collectors.toList())));
            dataAuthorityRuleDetailService.delete(new EntityWrapper<DataAuthorityRuleDetail>().eq("data_authority_rule_id", id));
        }
    }
}
