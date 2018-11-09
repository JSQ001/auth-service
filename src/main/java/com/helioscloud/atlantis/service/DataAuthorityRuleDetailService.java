package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.DataAuthorityRuleDetail;
import com.helioscloud.atlantis.domain.DataAuthorityRuleDetailValue;
import com.helioscloud.atlantis.persistence.DataAuthorityRuleDetailMapper;
import com.helioscloud.atlantis.util.RespCode;
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
public class DataAuthorityRuleDetailService extends BaseService<DataAuthorityRuleDetailMapper,DataAuthorityRuleDetail>{

    private final DataAuthorityRuleDetailMapper dataAuthorityRuleDetailMapper;

    private final DataAuthorityRuleDetailValueService dataAuthorityRuleDetailValueService;

    /**
     * 新建数据权限规则明细
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthorityRuleDetail createDataAuthorityRuleDetail(DataAuthorityRuleDetail entity){
        if(entity.getId() != null){
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        dataAuthorityRuleDetailMapper.insert(entity);
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRuleDetailValues())){
            dataAuthorityRuleDetailValueService.batchCreateDataAuthorityRuleDetailValue(entity.getDataAuthorityRuleDetailValues(),entity.getId(),entity.getDataAuthorityId());
        }
        return entity;
    }

    @Transactional
    public DataAuthorityRuleDetail createDataAuthorityRuleDetail(DataAuthorityRuleDetail entity,Long dataAuthRuleId,Long dataAuthorityId){
        if(entity.getDataAuthorityRuleId() == null){
            entity.setDataAuthorityRuleId(dataAuthRuleId);
        }
        if(entity.getDataAuthorityId() == null){
            entity.setDataAuthorityId(dataAuthorityId);
        }
        return createDataAuthorityRuleDetail(entity);
    }

    /**
     * 批量新建数据权限规则明细
     * @param entities
     * @return
     */
    @Transactional
    public List<DataAuthorityRuleDetail> createDataAuthorityRuleDetailBatch(List<DataAuthorityRuleDetail> entities){
        entities.forEach(entity -> createDataAuthorityRuleDetail(entity));
        return entities;
    }

    @Transactional
    public List<DataAuthorityRuleDetail> createDataAuthorityRuleDetailBatch(List<DataAuthorityRuleDetail> entities,Long dataAuthRuleId,Long dataAuthorityId){
        entities.forEach(entity -> createDataAuthorityRuleDetail(entity,dataAuthRuleId,dataAuthorityId));
        return entities;
    }

    /**
     * 更新数据权限规则明细
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthorityRuleDetail updateDataAuthorityRuleDetail(DataAuthorityRuleDetail entity){
        if(entity.getId() ==null){
            throw new BizException(RespCode.ID_NULL);
        }
        dataAuthorityRuleDetailMapper.updateById(entity);
        // 删除明细值
        dataAuthorityRuleDetailValueService.deleteDataAuthorityRuleDetailValuesByDetailId(entity.getId());
        // 明细值不为空时，插入数据
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRuleDetailValues())){
            dataAuthorityRuleDetailValueService.batchCreateDataAuthorityRuleDetailValue(entity.getDataAuthorityRuleDetailValues(),entity.getId(),entity.getDataAuthorityId());
        }
        return entity;
    }

    /**
     * 批量更新数据权限规则明细
     * @param entities
     * @return
     */
    @Transactional
    public List<DataAuthorityRuleDetail> updateDataAuthorityRuleDetailBatch(List<DataAuthorityRuleDetail> entities){
        entities.forEach(entity -> updateDataAuthorityRuleDetail(entity));
        return entities;
    }

    /**
     * 根据规则ID获取明细信息
     * @param ruleId
     * @return
     */
    public List<DataAuthorityRuleDetail> queryDataAuthorityRuleDetailsByRuleId(Long ruleId){
        List<DataAuthorityRuleDetail> ruleDetails = dataAuthorityRuleDetailMapper.selectList(new EntityWrapper<DataAuthorityRuleDetail>()
                .eq("data_authority_rule_id", ruleId));
        ruleDetails.forEach(ruleDetail -> {
            List<DataAuthorityRuleDetailValue> dataAuthorityRuleDetailValues = dataAuthorityRuleDetailValueService.queryAllDataAuthorityRuleDetailValues(ruleDetail.getId());
            ruleDetail.setDataAuthorityRuleDetailValues(dataAuthorityRuleDetailValues);
        });
        return ruleDetails;
    }
}
