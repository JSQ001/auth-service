package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.helioscloud.atlantis.domain.DataAuthorityRuleDetailValue;
import com.helioscloud.atlantis.persistence.DataAuthorityRuleDetailValueMapper;
import com.helioscloud.atlantis.util.RespCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:50
 * @remark
 */
@Service
@AllArgsConstructor
public class DataAuthorityRuleDetailValueService extends BaseService<DataAuthorityRuleDetailValueMapper,DataAuthorityRuleDetailValue>{

    private final DataAuthorityRuleDetailValueMapper dataAuthorityRuleDetailValueMapper;

    /**
     * 添加数据权限规则明细值
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthorityRuleDetailValue createDataAuthorityRuleDetailValue(DataAuthorityRuleDetailValue entity){
        if(entity.getId() != null){
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        dataAuthorityRuleDetailValueMapper.insert(entity);
        return entity;
    }

    @Transactional
    public DataAuthorityRuleDetailValue createDataAuthorityRuleDetailValue(DataAuthorityRuleDetailValue entity,Long dataAuthRuleDetailId,Long dataAuthorityId){
        if(entity.getDataAuthRuleDetailId() == null){
            entity.setDataAuthRuleDetailId(dataAuthRuleDetailId);
        }
        if(entity.getDataAuthorityId() == null){
            entity.setDataAuthorityId(dataAuthorityId);
        }
        return createDataAuthorityRuleDetailValue(entity);
    }

    /**
     * 批量添加数据权限规则明细值
     * @param dataAuthorityRuleDetailValues
     * @return
     */
    @Transactional
    public List<DataAuthorityRuleDetailValue> batchCreateDataAuthorityRuleDetailValue(List<DataAuthorityRuleDetailValue> dataAuthorityRuleDetailValues){
        dataAuthorityRuleDetailValues.forEach(dataAuthorityRuleDetailValue -> createDataAuthorityRuleDetailValue(dataAuthorityRuleDetailValue));
        return dataAuthorityRuleDetailValues;
    }

    @Transactional
    public List<DataAuthorityRuleDetailValue> batchCreateDataAuthorityRuleDetailValue(List<DataAuthorityRuleDetailValue> dataAuthorityRuleDetailValues
            ,Long dataAuthRuleDetailId
            ,Long dataAuthorityId){
        dataAuthorityRuleDetailValues.forEach(dataAuthorityRuleDetailValue -> createDataAuthorityRuleDetailValue(dataAuthorityRuleDetailValue,dataAuthRuleDetailId,dataAuthorityId));
        return dataAuthorityRuleDetailValues;
    }

    /**
     * 根据规则明细ID删除明细值
     * @param dataAuthRuleDetailId
     */
    @Transactional
    public void deleteDataAuthorityRuleDetailValuesByDetailId(Long dataAuthRuleDetailId){
        dataAuthorityRuleDetailValueMapper.delete(new EntityWrapper<DataAuthorityRuleDetailValue>()
                .eq("data_auth_rule_detail_id",dataAuthRuleDetailId));
    }

    /**
     * 根据规则明细获取明细值 - 分页
     * @param dataAuthRuleDetailId
     * @param page
     * @return
     */
    public List<DataAuthorityRuleDetailValue> queryDataAuthorityRuleDetailValues(Long dataAuthRuleDetailId, Page page){
        return dataAuthorityRuleDetailValueMapper.selectPage(page,new EntityWrapper<DataAuthorityRuleDetailValue>()
                .eq("data_auth_rule_detail_id",dataAuthRuleDetailId));
    }

    /**
     * 根据规则明细ID获取明细值
     * @param dataAuthRuleDetailId
     * @return
     */
    public List<DataAuthorityRuleDetailValue> queryAllDataAuthorityRuleDetailValues(Long dataAuthRuleDetailId){
        return dataAuthorityRuleDetailValueMapper.selectList(new EntityWrapper<DataAuthorityRuleDetailValue>()
                .eq("data_auth_rule_detail_id",dataAuthRuleDetailId));
    }
}
