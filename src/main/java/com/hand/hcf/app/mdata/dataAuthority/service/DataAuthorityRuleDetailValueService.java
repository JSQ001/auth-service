package com.hand.hcf.app.mdata.dataAuthority.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthorityRuleDetailValue;
import com.hand.hcf.app.mdata.dataAuthority.persistence.DataAuthorityRuleDetailValueMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:50
 * @remark
 */
@Service
@AllArgsConstructor
public class DataAuthorityRuleDetailValueService extends BaseService<DataAuthorityRuleDetailValueMapper,DataAuthorityRuleDetailValue> {

    private final DataAuthorityRuleDetailValueMapper dataAuthorityRuleDetailValueMapper;

    /**
     * 添加数据权限规则明细值
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthorityRuleDetailValue createDataAuthorityRuleDetailValue(DataAuthorityRuleDetailValue entity){
        if(entity.getId() != null){
            throw new BizException(RespCode.SYS_ID_NOT_NULL);
        }
        dataAuthorityRuleDetailValueMapper.insert(entity);
        return entity;
    }

    @Transactional
    public DataAuthorityRuleDetailValue createDataAuthorityRuleDetailValue(String valueKey,Long dataAuthRuleDetailId,Long dataAuthorityId){
        DataAuthorityRuleDetailValue entity = new DataAuthorityRuleDetailValue();
        entity.setValueKey(valueKey);
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
    public List<DataAuthorityRuleDetailValue> batchCreateDataAuthorityRuleDetailValue(List<String> valueKeys
            ,Long dataAuthRuleDetailId
            ,Long dataAuthorityId){
        return valueKeys.stream().map(valueKey -> createDataAuthorityRuleDetailValue(valueKey,dataAuthRuleDetailId,dataAuthorityId)).collect(Collectors.toList());
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
     * 根据规则明细ID获取明细值
     * @param dataAuthRuleDetailId
     * @return
     */
    public List<String> queryAllDataAuthorityRuleDetailValues(Long dataAuthRuleDetailId){
        return dataAuthorityRuleDetailValueMapper.selectList(new EntityWrapper<DataAuthorityRuleDetailValue>()
                .eq("data_auth_rule_detail_id",dataAuthRuleDetailId)).stream().map(e -> e.getValueKey()).collect(Collectors.toList());
    }
}
