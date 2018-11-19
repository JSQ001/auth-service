package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.domain.DataAuthorityRule;
import com.hand.hcf.app.base.domain.DataAuthorityRuleDetail;
import com.hand.hcf.app.base.persistence.DataAuthorityRuleMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 添加数据权限规则
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthorityRule createDataAuthorityRule(DataAuthorityRule entity){
        if(entity.getId() != null){
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        Integer count = dataAuthorityRuleMapper.selectCount(new EntityWrapper<DataAuthorityRule>()
                .eq("data_authority_id", entity.getDataAuthorityId())
                .eq("data_authority_rule_name", entity.getDataAuthorityRuleName())
                .eq("deleted",false));
        if(count > 0){
            throw new BizException(RespCode.DATA_AUTHORITY_RULE_EXISTS);
        }
        dataAuthorityRuleMapper.insert(entity);
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRuleDetails())){
            dataAuthorityRuleDetailService.createDataAuthorityRuleDetailBatch(entity.getDataAuthorityRuleDetails(),entity.getId(),entity.getDataAuthorityId());
        }
        return entity;
    }

    @Transactional
    public DataAuthorityRule createDataAuthorityRule(DataAuthorityRule entity,Long dataAuthorityId){
        if(entity.getDataAuthorityId() == null){
            entity.setDataAuthorityId(dataAuthorityId);
        }
        return createDataAuthorityRule(entity);
    }

    /**
     * 批量添加数据权限规则
     * @param entities
     * @return
     */
    @Transactional
    public List<DataAuthorityRule> createDataAuthorityRuleBatch(List<DataAuthorityRule> entities){
        entities.forEach(entity -> createDataAuthorityRule(entity));
        return entities;
    }

    @Transactional
    public List<DataAuthorityRule> createDataAuthorityRuleBatch(List<DataAuthorityRule> entities,Long dataAuthorityId){
        entities.forEach(entity -> createDataAuthorityRule(entity,dataAuthorityId));
        return entities;
    }

    /**
     * 更新数据权限规则
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthorityRule updateDataAuthorityRule(DataAuthorityRule entity){
        if(entity.getId() == null){
            throw new BizException(RespCode.ID_NULL);
        }
        Integer count = dataAuthorityRuleMapper.selectCount(new EntityWrapper<DataAuthorityRule>()
                .eq("data_authority_id", entity.getDataAuthorityId())
                .eq("data_authority_rule_name", entity.getDataAuthorityRuleName())
                .ne("id",entity.getId())
                .eq("deleted",false));
        if(count > 0){
            throw new BizException(RespCode.DATA_AUTHORITY_RULE_EXISTS);
        }
        dataAuthorityRuleMapper.updateById(entity);
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRuleDetails())){
            dataAuthorityRuleDetailService.updateDataAuthorityRuleDetailBatch(entity.getDataAuthorityRuleDetails());
        }
        return entity;
    }

    /**
     * 批量更新数据权限规则
     * @param entities
     * @return
     */
    @Transactional
    public List<DataAuthorityRule> updateDataAuthorityRuleBatch(List<DataAuthorityRule> entities){
        entities.forEach(entity -> updateDataAuthorityRule(entity));
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
            List<DataAuthorityRuleDetail> dataAuthorityRuleDetails = dataAuthorityRuleDetailService.queryDataAuthorityRuleDetailsByRuleId(dataAuthorityRule.getId());
            dataAuthorityRule.setDataAuthorityRuleDetails(dataAuthorityRuleDetails);
        });
        return dataAuthRules;
    }
}
