package com.hand.hcf.app.workflow.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.workflow.constant.ApprovalFormPropertyConstants;
import com.hand.hcf.app.workflow.domain.ApprovalFormProperty;
import com.hand.hcf.app.workflow.dto.ApprovalAddSignScopeDTO;
import com.hand.hcf.app.workflow.dto.ApprovalFormPropertyRuleDTO;
import com.hand.hcf.app.workflow.enums.CounterSignTypeEnum;
import com.hand.hcf.app.workflow.enums.FilterRuleEnum;
import com.hand.hcf.app.workflow.enums.FilterTypeRuleEnum;
import com.hand.hcf.app.workflow.enums.ProxyStrategyEnum;
import com.hand.hcf.app.workflow.persistence.ApprovalFormPropertyMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApprovalFormPropertyService extends BaseService<ApprovalFormPropertyMapper, ApprovalFormProperty> {

    public ApprovalFormPropertyRuleDTO selectByFormOid(UUID formOid) {
        List<ApprovalFormProperty> approvalFormPropertyList = listByOid(formOid);
        ApprovalFormPropertyRuleDTO approvalFormPropertyRuleDTO = new ApprovalFormPropertyRuleDTO();
        if (CollectionUtils.isEmpty(approvalFormPropertyList)) {
            return approvalFormPropertyRuleDTO;
        }
        approvalFormPropertyRuleDTO.setFormOid(formOid);
        approvalFormPropertyRuleDTO.setEnableCounterSign(Boolean.valueOf(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.ENABEL_ADD_SIGN)));
        approvalFormPropertyRuleDTO.setCounterSignRule(CounterSignTypeEnum.parse(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.COUNTERSIGN_TYPE)));
        approvalFormPropertyRuleDTO.setEnableCounterSignForSubmitter(Boolean.valueOf(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.ENABEL_ADD_SIGN_FOR_SUBMITTER)));
        approvalFormPropertyRuleDTO.setCounterSignRuleForSubmitter(CounterSignTypeEnum.parse(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.COUNTERSIGN_TYPE_FOR_SUBMITTER)));
        approvalFormPropertyRuleDTO.setFilterTypeRule(FilterTypeRuleEnum.parse(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.FILTER_TYPE_RULE)));
        approvalFormPropertyRuleDTO.setFilterRule(FilterRuleEnum.parse(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.FILTER_RULE)));
        approvalFormPropertyRuleDTO.setProxyStrategy(ProxyStrategyEnum.parse(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.PROXY_STRATEGY)));
        approvalFormPropertyRuleDTO.setEnableAmountFilter(Boolean.parseBoolean(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.AMOUNT_FILTER)));
        approvalFormPropertyRuleDTO.setEnableExpenseTypeFilter(Boolean.parseBoolean(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.EXPENSETYPE_FILTER)));
        if (StringUtils.isNotEmpty(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.APPROVAL_ADD_SIGN_SCOPE))) {
            approvalFormPropertyRuleDTO.setApprovalAddSignScope(
                    JSON.parseObject(extractPropertyValue(approvalFormPropertyList, ApprovalFormPropertyConstants.APPROVAL_ADD_SIGN_SCOPE), ApprovalAddSignScopeDTO.class));
        }

        return approvalFormPropertyRuleDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createCustomFormPropertyRule(ApprovalFormPropertyRuleDTO approvalFormPropertyRuleDTO) {

        if (approvalFormPropertyRuleDTO.getFormOid() == null) {
            throw new BizException("form_oid必传,请检查参数!");
        }
        //新增时不应该存在数据
        List<ApprovalFormProperty> approvalFormPropertyList = listByOid(approvalFormPropertyRuleDTO.getFormOid());
        if (CollectionUtils.isNotEmpty(approvalFormPropertyList)) {
            throw new BizException("表单Oid:" + approvalFormPropertyRuleDTO.getFormOid() + "存在相关属性数据,无法执行新增操作!");
        }
        processInsertOrUpdate(approvalFormPropertyRuleDTO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCustomFormPropertyRule(ApprovalFormPropertyRuleDTO approvalFormPropertyRuleDTO) {

        if (approvalFormPropertyRuleDTO.getFormOid() == null) {
            throw new BizException("form_oid必传,请检查参数!");
        }
        //修改时必须要存在数据
        List<ApprovalFormProperty> approvalFormPropertyList = listByOid(approvalFormPropertyRuleDTO.getFormOid());
        if (CollectionUtils.isEmpty(approvalFormPropertyList)) {
            throw new BizException("表单Oid:" + approvalFormPropertyRuleDTO.getFormOid() + "不存在相关属性数据,无法执行修改操作!");
        }
        processInsertOrUpdate(approvalFormPropertyRuleDTO);
    }

    /**
     * 仅根据form_oid查询CustomFormProperty
     *
     * @param customFormOid
     * @return
     */
    private List<ApprovalFormProperty> listByOid(UUID customFormOid) {

        return selectList(new EntityWrapper<ApprovalFormProperty>()
                .eq("form_oid", customFormOid));

    }

    /**
     * 行转列
     *
     * @param approvalFormPropertyList
     * @param propertyField
     * @return
     */
    private String extractPropertyValue(List<ApprovalFormProperty> approvalFormPropertyList, String propertyField) {
        List<ApprovalFormProperty> propertyList = approvalFormPropertyList.stream().filter(p -> p.getPropertyName().equalsIgnoreCase(propertyField)).collect(Collectors.toList());
        if (propertyList.size() > 1) {
            throw new BizException("一个表单对应" + propertyField + "属性至多匹配一条记录!");
        }
        for (ApprovalFormProperty approvalFormProperty : propertyList) {
            return approvalFormProperty.getPropertyValue();
        }
        return null;
    }

    private boolean deleteByPropertyNameAndFormOid(String propertyName, UUID formOid) {
        return delete(new EntityWrapper<ApprovalFormProperty>()
                .eq("property_name", propertyName)
                .eq("form_oid", formOid));
    }

    /**
     * 根据参数选择是否要处理数据
     *
     * @param approvalFormPropertyRuleDTO
     */
    private void processInsertOrUpdate(ApprovalFormPropertyRuleDTO approvalFormPropertyRuleDTO) {
        if (approvalFormPropertyRuleDTO.getEnableCounterSign() != null) {
            insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.ENABEL_ADD_SIGN, approvalFormPropertyRuleDTO.getEnableCounterSign().toString(), approvalFormPropertyRuleDTO.getFormOid(), false);
        }
        if (approvalFormPropertyRuleDTO.getCounterSignRule() != null) {
            insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.COUNTERSIGN_TYPE, approvalFormPropertyRuleDTO.getCounterSignRule().toString(), approvalFormPropertyRuleDTO.getFormOid(), CounterSignTypeEnum.parse(approvalFormPropertyRuleDTO.getCounterSignRule().toString()).equals(CounterSignTypeEnum.COUNTER_SIGN_TYPE_NULL.getValue()));
        }
        if (approvalFormPropertyRuleDTO.getEnableCounterSignForSubmitter() != null) {
            insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.ENABEL_ADD_SIGN_FOR_SUBMITTER, approvalFormPropertyRuleDTO.getEnableCounterSignForSubmitter().toString(), approvalFormPropertyRuleDTO.getFormOid(), false);
        }
        if (approvalFormPropertyRuleDTO.getCounterSignRule() != null) {
            insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.COUNTERSIGN_TYPE_FOR_SUBMITTER, approvalFormPropertyRuleDTO.getCounterSignRuleForSubmitter().toString(), approvalFormPropertyRuleDTO.getFormOid(), CounterSignTypeEnum.parse(approvalFormPropertyRuleDTO.getCounterSignRuleForSubmitter().toString()).equals(CounterSignTypeEnum.COUNTER_SIGN_TYPE_NULL.getValue()));
        }
        if (approvalFormPropertyRuleDTO.getFilterTypeRule() != null) {
            insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.FILTER_TYPE_RULE, approvalFormPropertyRuleDTO.getFilterTypeRule().toString(), approvalFormPropertyRuleDTO.getFormOid(), FilterTypeRuleEnum.parse(approvalFormPropertyRuleDTO.getFilterTypeRule().toString()).equals(FilterTypeRuleEnum.FILTER_TYPE_RULE_NULL.getValue()));
            //特殊关联逻辑:如果filterTypeRule选择了不跳过或者不选,需要把filterRule清除
            if (FilterTypeRuleEnum.parse(approvalFormPropertyRuleDTO.getFilterTypeRule().toString()).equals(FilterTypeRuleEnum.FILTER_TYPE_RULE_NOT_FILTER.getValue())
                    || FilterTypeRuleEnum.parse(approvalFormPropertyRuleDTO.getFilterTypeRule().toString()).equals(FilterTypeRuleEnum.FILTER_TYPE_RULE_NULL.getValue())) {

                deleteByPropertyNameAndFormOid(ApprovalFormPropertyConstants.FILTER_RULE, approvalFormPropertyRuleDTO.getFormOid());
            }
        }
        if (approvalFormPropertyRuleDTO.getFilterRule() != null) {
            insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.FILTER_RULE, approvalFormPropertyRuleDTO.getFilterRule().toString(), approvalFormPropertyRuleDTO.getFormOid(), FilterRuleEnum.parse(approvalFormPropertyRuleDTO.getFilterRule().toString()).equals(FilterRuleEnum.FILTER_RULE_NULL.getValue()));
        }
        if (approvalFormPropertyRuleDTO.getProxyStrategy() != null) {
            insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.PROXY_STRATEGY, approvalFormPropertyRuleDTO.getProxyStrategy().toString(), approvalFormPropertyRuleDTO.getFormOid(), ProxyStrategyEnum.parse(approvalFormPropertyRuleDTO.getProxyStrategy().toString()).equals(ProxyStrategyEnum.PROXY_STRATEGY_NULL.getValue()));
        }
        insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.AMOUNT_FILTER, String.valueOf(approvalFormPropertyRuleDTO.isEnableAmountFilter()), approvalFormPropertyRuleDTO.getFormOid(), false);
        insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.EXPENSETYPE_FILTER, String.valueOf(approvalFormPropertyRuleDTO.isEnableExpenseTypeFilter()), approvalFormPropertyRuleDTO.getFormOid(), false);
        if (approvalFormPropertyRuleDTO.getApprovalAddSignScope() != null) {
            insertOrUpdateCustomFormProperty(ApprovalFormPropertyConstants.APPROVAL_ADD_SIGN_SCOPE, String.valueOf(JSONObject.toJSONString(approvalFormPropertyRuleDTO.getApprovalAddSignScope())), approvalFormPropertyRuleDTO.getFormOid(), false);
        }
    }

    public ApprovalFormProperty getByFormOidAndPropertyName(UUID formOid, String propertyName) {
       return selectOne(new EntityWrapper<ApprovalFormProperty>()
                .eq("property_name", propertyName)
                .eq("form_oid", formOid));
    }

    public String getPropertyValueByFormOidAndPropertyName(UUID formOid, String propertyName) {
        ApprovalFormProperty approvalFormProperty =getByFormOidAndPropertyName(formOid, propertyName);
        if(approvalFormProperty != null){
            return approvalFormProperty.getPropertyValue();
        }
        return null;
    }

    /**
     * 新增/修改通用方法,原因:修改时可能有新增动作
     * insertOrUpdate customFormProperty
     *
     * @param propertyName
     * @param propertyValue
     * @param customFormOid
     * @param deleteFlag    删除标记:若前端给10,则删除,为了维持原实现逻辑的数据
     */
    private void insertOrUpdateCustomFormProperty(String propertyName, String propertyValue, UUID customFormOid, boolean deleteFlag) {

        if (deleteFlag) {
            deleteByPropertyNameAndFormOid(propertyName, customFormOid);
        } else {
            List<ApprovalFormProperty> approvalFormPropertyList = selectList(new EntityWrapper<ApprovalFormProperty>()
                    .eq("property_name", propertyName)
                    .eq("form_oid", customFormOid));

            if (CollectionUtils.isEmpty(approvalFormPropertyList)) {
                insert(ApprovalFormProperty.builder().formOid(customFormOid).propertyName(propertyName).propertyValue(propertyValue).build());
            } else if (approvalFormPropertyList.size() == 1) {
                ApprovalFormProperty approvalFormProperty = approvalFormPropertyList.get(0);
                approvalFormProperty.setPropertyValue(propertyValue);
                updateById(approvalFormProperty);
            } else {
                throw new BizException("表单Oid:" + customFormOid.toString() + "对应" + propertyName + "属性至多匹配一条记录!");
            }
        }
    }

    /**
     * 表单配置复制
     * @param sourceFormOid
     * @param targetFormOid
     */
    public void synchronizeCustomFormProperty(UUID sourceFormOid,UUID targetFormOid) {
        if(sourceFormOid == null || targetFormOid == null){
            return;
        }
        List<ApprovalFormProperty> sourceApprovalFormProperty = listByOid(sourceFormOid);
        if(CollectionUtils.isNotEmpty(sourceApprovalFormProperty)){
            Map<String, String> copyCustomFormPropertyMap = sourceApprovalFormProperty.stream().filter(s -> ApprovalFormPropertyConstants.copyApprovalFormProperty.contains(s.getPropertyName())).collect(Collectors.toMap(ApprovalFormProperty::getPropertyName, ApprovalFormProperty::getPropertyValue));
            //删除原表单中相应的配置
            ApprovalFormPropertyConstants.copyApprovalFormProperty.stream().forEach(c ->{
                deleteByPropertyNameAndFormOid(c,targetFormOid);
            });
            if(copyCustomFormPropertyMap != null && copyCustomFormPropertyMap.size() > 0){
                for (Map.Entry<String, String> entry : copyCustomFormPropertyMap.entrySet()) {
                    copyCustomFormProperty(targetFormOid,entry.getKey(),entry.getValue());
                }
            }
        }
    }


    /**
     * 根据formOid和propertyName,更新propertyValue
     * @param targetFormOid
     * @param propertyName
     * @param propertyValue
     */
    private void copyCustomFormProperty(UUID targetFormOid, String propertyName, String propertyValue) {
        //新增
        ApprovalFormProperty newApprovalFormProperty = new ApprovalFormProperty();
        newApprovalFormProperty.setFormOid(targetFormOid);
        newApprovalFormProperty.setPropertyName(propertyName);
        newApprovalFormProperty.setPropertyValue(propertyValue);
        insert(newApprovalFormProperty);
    }


    /**
     * 导入表单属性listV2
     * 带system columns的初始化
     *
     * @param approvalFormPropertyList
     * @param formOids
     */
    public void saveList(List<ApprovalFormProperty> approvalFormPropertyList, List<UUID> formOids) {
        formOids.stream().forEach(formOid -> {
            List<ApprovalFormProperty> propertyList = new ArrayList<>();
            approvalFormPropertyList.stream().forEach(approvalFormProperty1 -> {
                ApprovalFormProperty approvalFormProperty = new ApprovalFormProperty();
                approvalFormProperty.setFormOid(formOid);
                approvalFormProperty.setPropertyName(approvalFormProperty1.getPropertyName());
                approvalFormProperty.setPropertyValue(approvalFormProperty1.getPropertyValue());
                propertyList.add(approvalFormProperty);
            });
            insertOrUpdateBatch(propertyList);
        });
    }


    public Map<String ,Object> deleteCustomFormProperty(UUID formOid ,List<String> propertyNames){
        Map<String ,Object> result = new HashedMap();
        String success = "success";
        try {
            List<ApprovalFormProperty> approvalFormPropertyList =selectList(new EntityWrapper<ApprovalFormProperty>()
                    .eq("form_oid",formOid)
                    .in("property_name",propertyNames));
            deleteBatchIds(approvalFormPropertyList);
            result.put(success ,Boolean.TRUE);
        } catch (Exception e){
            result.put(success ,Boolean.FALSE);
            String failReason = "failReason";
            result.put(failReason ,e.getMessage());
        }
        return result;
    }

}
