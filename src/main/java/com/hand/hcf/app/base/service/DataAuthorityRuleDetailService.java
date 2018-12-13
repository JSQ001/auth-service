package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.hand.hcf.app.base.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.client.org.CustomEnumerationItemDTO;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.domain.DataAuthorityRuleDetail;
import com.hand.hcf.app.base.domain.DataAuthorityRuleDetailValue;
import com.hand.hcf.app.base.persistence.DataAuthorityRuleDetailMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
public class DataAuthorityRuleDetailService extends BaseService<DataAuthorityRuleDetailMapper,DataAuthorityRuleDetail>{

    private final DataAuthorityRuleDetailMapper dataAuthorityRuleDetailMapper;

    private final DataAuthorityRuleDetailValueService dataAuthorityRuleDetailValueService;

    private final HcfOrganizationInterface hcfOrganizationInterface;

    private final ThreadLocal<Map<String,Map<String,CustomEnumerationItemDTO>>> systemCodeCache = new ThreadLocal<>();
    /**
     * 新建数据权限规则明细
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthorityRuleDetail saveDataAuthorityRuleDetail(DataAuthorityRuleDetail entity){
        if("1004".equals(entity.getDataScope()) && CollectionUtils.isEmpty(entity.getDataAuthorityRuleDetailValues())){
            throw new BizException(RespCode.AUTH_DATA_AUTHORITY_RULE_DETAIL_VALUE_NONE);
        }
        Integer integer = dataAuthorityRuleDetailMapper.selectCount(new EntityWrapper<DataAuthorityRuleDetail>()
                .eq("data_authority_rule_id", entity.getDataAuthorityRuleId())
                .eq("data_type", entity.getDataType())
                .eq("deleted", false)
                .ne(entity.getId() != null,"id",entity.getId()));
        if(integer > 0){
            throw new BizException(RespCode.AUTH_DATA_AUTHORITY_RULE_DETAIL_EXISTS);
        }
        if(entity.getId() != null){
            dataAuthorityRuleDetailMapper.updateAllColumnById(entity);
            // 删除明细值
            dataAuthorityRuleDetailValueService.deleteDataAuthorityRuleDetailValuesByDetailId(entity.getId());
        }else {
            dataAuthorityRuleDetailMapper.insert(entity);
        }
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRuleDetailValues())){
            dataAuthorityRuleDetailValueService.batchCreateDataAuthorityRuleDetailValue(entity.getDataAuthorityRuleDetailValues(),entity.getId(),entity.getDataAuthorityId());
        }
        return entity;
    }

    @Transactional
    public DataAuthorityRuleDetail saveDataAuthorityRuleDetail(DataAuthorityRuleDetail entity,Long dataAuthRuleId,Long dataAuthorityId){
        if(entity.getDataAuthorityRuleId() == null){
            entity.setDataAuthorityRuleId(dataAuthRuleId);
        }
        if(entity.getDataAuthorityId() == null){
            entity.setDataAuthorityId(dataAuthorityId);
        }
        return saveDataAuthorityRuleDetail(entity);
    }

    /**
     * 批量新建数据权限规则明细
     * @param entities
     * @return
     */
    @Transactional
    public List<DataAuthorityRuleDetail> saveDataAuthorityRuleDetailBatch(List<DataAuthorityRuleDetail> entities){
        entities.forEach(entity -> saveDataAuthorityRuleDetail(entity));
        return entities;
    }

    @Transactional
    public List<DataAuthorityRuleDetail> saveDataAuthorityRuleDetailBatch(List<DataAuthorityRuleDetail> entities,Long dataAuthRuleId,Long dataAuthorityId){
        entities.forEach(entity -> saveDataAuthorityRuleDetail(entity,dataAuthRuleId,dataAuthorityId));
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
        if(CollectionUtils.isNotEmpty(ruleDetails)){
            ruleDetails.stream().forEach(ruleDetail -> {
                if(systemCodeCache.get() == null) {
                    systemCodeCache.set(new HashMap<>());
                }
                // 数据范围
                if(! systemCodeCache.get().containsKey("3102")){
                    systemCodeCache.get().put("3102",hcfOrganizationInterface.getSysCodeValues("3102").stream().collect(Collectors.toMap(e -> e.getValue(),e -> e)));
                }
                CustomEnumerationItemDTO customEnumerationItemDTO = systemCodeCache.get().get("3102").get(ruleDetail.getDataScope());
                ruleDetail.setDataScopeDesc(customEnumerationItemDTO.getMessageKey());
                if(StringUtils.isNotEmpty(ruleDetail.getFiltrateMethod())){
                    // 数据取值规则
                    if(! systemCodeCache.get().containsKey("3103")){
                        systemCodeCache.get().put("3103",hcfOrganizationInterface.getSysCodeValues("3103").stream().collect(Collectors.toMap(e -> e.getValue(),e -> e)));
                    }
                    customEnumerationItemDTO = systemCodeCache.get().get("3103").get(ruleDetail.getFiltrateMethod());
                    ruleDetail.setFiltrateMethodDesc(customEnumerationItemDTO.getMessageKey());
                }
            });
        }
        ruleDetails.forEach(ruleDetail -> {
            List<String> dataAuthorityRuleDetailValues = dataAuthorityRuleDetailValueService.queryAllDataAuthorityRuleDetailValues(ruleDetail.getId());
            ruleDetail.setDataAuthorityRuleDetailValues(dataAuthorityRuleDetailValues);
        });
        return ruleDetails;
    }
}
