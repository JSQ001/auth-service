package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.cloudhelios.atlantis.exception.BizException;
import com.cloudhelios.atlantis.service.BaseService;
import com.cloudhelios.atlantis.util.DataAuthorityUtil;
import com.cloudhelios.atlantis.util.LoginInformationUtil;
import com.cloudhelios.atlantis.util.TypeConversionUtils;
import com.cloudhelios.atlantis.web.dto.DataAuthValuePropertyDTO;
import com.helioscloud.atlantis.domain.DataAuthority;
import com.helioscloud.atlantis.domain.DataAuthorityRule;
import com.helioscloud.atlantis.domain.DataAuthorityRuleDetail;
import com.helioscloud.atlantis.domain.DataAuthorityRuleDetailValue;
import com.helioscloud.atlantis.persistence.DataAuthorityMapper;
import com.helioscloud.atlantis.util.RespCode;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/10/12 16:48
 * @remark
 */
@Service
@AllArgsConstructor
public class DataAuthorityService extends BaseService<DataAuthorityMapper,DataAuthority>{

    private final DataAuthorityMapper dataAuthorityMapper;
    private final DataAuthorityRuleService dataAuthorityRuleService;
    private final DataAuthorityRuleDetailService dataAuthorityRuleDetailService;
    private final DataAuthorityRuleDetailValueService dataAuthorityRuleDetailValueService;

    /**
     * 创建数据权限
     */
    @Transactional
    public DataAuthority createDataAuthority(DataAuthority entity){
        if(entity.getId() !=null){
            throw new BizException(RespCode.ID_NOT_NULL);
        }
        dataAuthorityMapper.insert(entity);
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRules())){
            dataAuthorityRuleService.createDataAuthorityRuleBatch(entity.getDataAuthorityRules(),entity.getId());
        }
        return entity;
    }

    /**
     * 创建数据权限规则，并对数据权限进行保存
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthority saveDataAuthorityAndCreateRule(DataAuthority entity){
        if(entity.getId() !=null){
            dataAuthorityMapper.updateById(entity);
        }else{
            dataAuthorityMapper.insert(entity);
        }
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRules())){
            dataAuthorityRuleService.createDataAuthorityRuleBatch(entity.getDataAuthorityRules(),entity.getId());
        }
        return entity;
    }

    /**
     * 更新数据权限
     * 每次更新，都需要删除原来数据，全部重新添加
     * @param entity
     * @return
     */
    @Transactional
    public DataAuthority updateDataAuthorityAndResetDetailById(DataAuthority entity){
        Long id = entity.getId();
        if(id ==null){
            throw new BizException(RespCode.ID_NULL);
        }
        // 需要把明细数据全部清除掉，重新保存
        dataAuthorityRuleService.delete(new EntityWrapper<DataAuthorityRule>().eq("data_authority_id",id));
        dataAuthorityRuleDetailService.delete(new EntityWrapper<DataAuthorityRuleDetail>().eq("data_authority_id",id));
        dataAuthorityRuleDetailValueService.delete(new EntityWrapper<DataAuthorityRuleDetailValue>().eq("data_authority_id",id));
        dataAuthorityMapper.updateById(entity);
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRules())){
            dataAuthorityRuleService.createDataAuthorityRuleBatch(entity.getDataAuthorityRules(),entity.getId());
        }
        return entity;
    }

    public DataAuthority updateDataAuthorityById(DataAuthority entity){
        Long id = entity.getId();
        if(id ==null){
            throw new BizException(RespCode.ID_NULL);
        }
        dataAuthorityMapper.updateById(entity);
        if(CollectionUtils.isNotEmpty(entity.getDataAuthorityRules())){
            dataAuthorityRuleService.updateDataAuthorityRuleBatch(entity.getDataAuthorityRules());
        }
        return entity;
    }

    /**
     * 删除数据权限
     * @param id
     */
    @Transactional
    public void deleteDataAuthorityById(Long id){
        // ... 校验是否被引用
        boolean isCited = false;

        if(isCited){
            throw new BizException(RespCode.DATA_AUTHORITY_CITED);
        }
        DataAuthority dataAuthority = dataAuthorityMapper.selectById(id);
        dataAuthority.setDeleted(true);
        dataAuthority.setDataAuthorityCode(dataAuthority.getDataAuthorityCode() + "_DELETED_" + RandomStringUtils.randomNumeric(6));
        dataAuthorityRuleService.delete(new EntityWrapper<DataAuthorityRule>().eq("data_authority_id",id));
        dataAuthorityRuleDetailService.delete(new EntityWrapper<DataAuthorityRuleDetail>().eq("data_authority_id",id));
        dataAuthorityRuleDetailValueService.delete(new EntityWrapper<DataAuthorityRuleDetailValue>().eq("data_authority_id",id));
        dataAuthorityMapper.updateById(dataAuthority);
    }

    /**
     * 批量删除数据权限
     * @param ids
     */
    @Transactional
    public void deleteDataAuthorityByIds(List<Long> ids){
        ids.forEach(id -> deleteDataAuthorityById(id));
    }

    /**
     * 数据权限查询
     * @param dataAuthorityCode
     * @param dataAuthorityName
     * @param page
     * @return
     */
    public List<DataAuthority> getDataAuthorityByCond(String dataAuthorityCode,
                                                      String dataAuthorityName,
                                                      Page page){
        return dataAuthorityMapper.selectPage(page, new EntityWrapper<DataAuthority>()
                .eq("tenant_id", LoginInformationUtil.getCurrentTenantID())
                .like(TypeConversionUtils.isNotEmpty(dataAuthorityCode), "data_authority_code", dataAuthorityCode)
                .like(TypeConversionUtils.isNotEmpty(dataAuthorityName), "data_authority_name", dataAuthorityName)
                .eq("deleted", false)
                .orderBy("enabled desc,data_authority_code asc"));
    }

    /**
     * 获取数据权限，并获取明细信息
     * @param id
     * @return
     */
    public DataAuthority getDataAuthorityById(Long id){
        DataAuthority dataAuthority = dataAuthorityMapper.selectById(id);
        List<DataAuthorityRule> dataAuthorityRules = dataAuthorityRuleService.queryDataAuthorityRules(id);
        dataAuthority.setDataAuthorityRules(dataAuthorityRules);
        return dataAuthority;
    }

    public List<DataAuthority> getDataAuthorityByIds(Set<Long> ids){
        return ids.stream().map(id -> getDataAuthorityById(id)).collect(Collectors.toList());
    }

    /**
     * 根绝登录人信息获取相关数据权限
     * @return
     */
    public List<Map<String,List<DataAuthValuePropertyDTO>>> getDataAuthValuePropertiesByRequest(){
        // ... 需要根据登录人信息获取 关联的数据权限
        Set<Long> dataAuthIds = new HashSet();
        List<Map<String,List<DataAuthValuePropertyDTO>>> list = new ArrayList<>();
        dataAuthIds.add(1L);
        if(CollectionUtils.isNotEmpty(dataAuthIds)) {
            List<DataAuthority> dataAuthorityByIds = getDataAuthorityByIds(dataAuthIds);
            // 由于规则与规则之间为or关系，且多个数据结构之间也为or管线，所以只要有一个规则所有取值范围为全部，则表示其他规则全部无效，直接取全部值
            // 考虑到数据权限配置的数据量不会很大，在获取明细值之前判断，减少查询时间
            boolean all = dataAuthorityByIds.stream().anyMatch(dataAuthority -> {
                List<DataAuthorityRule> dataAuthorityRules = dataAuthority.getDataAuthorityRules();
                if (CollectionUtils.isNotEmpty(dataAuthorityRules)) {
                    return dataAuthorityRules.stream().anyMatch(dataAuthorityRule -> {
                        return dataAuthorityRule.getDataAuthorityRuleDetails().stream().allMatch(dataAuthorityRuleDetail -> "1001".equals(dataAuthorityRuleDetail.getDataScope()));
                    });
                }
                return false;
            });
            if (all) {
                return list;
            }
            dataAuthIds.stream().forEach(dataAuthId -> {
                DataAuthority dataAuthorityById = getDataAuthorityById(dataAuthId);
                if (dataAuthorityById != null && dataAuthorityById.getEnabled()) {
                    Map<String, List<DataAuthValuePropertyDTO>> dataAuthRulePropertiesMap = new HashMap<>();
                    List<DataAuthorityRule> dataAuthorityRules = dataAuthorityById.getDataAuthorityRules();
                    if (CollectionUtils.isNotEmpty(dataAuthorityRules)) {
                        dataAuthorityRules.stream().forEach(dataAuthorityRule -> {
                            List<DataAuthorityRuleDetail> dataAuthorityRuleDetails = dataAuthorityRule.getDataAuthorityRuleDetails();
                                List<DataAuthValuePropertyDTO> collect = dataAuthorityRuleDetails.stream().map(dataAuthorityRuleDetail -> {
                                DataAuthValuePropertyDTO dataAuthValuePropertyDTO = new DataAuthValuePropertyDTO();
                                dataAuthValuePropertyDTO.setDataType(dataAuthorityRuleDetail.getDataType());
                                dataAuthValuePropertyDTO.setFiltrateMethod(dataAuthorityRuleDetail.getFiltrateMethod());
                                // 全部
                                if ("1001".equals(dataAuthorityRuleDetail.getDataScope())) {
                                    dataAuthValuePropertyDTO.setAllFlag(true);
                                    return dataAuthValuePropertyDTO;
                                }
                                dataAuthValuePropertyDTO.setAllFlag(false);
                                // 当前 当前及下属
                                if ("1002".equals(dataAuthorityRuleDetail.getDataScope()) || "1003".equals(dataAuthorityRuleDetail.getDataScope())) {
                                    dataAuthValuePropertyDTO.setValueKeyList(
                                            getDataValueKeyByDataTypeAndDataScope(dataAuthorityRuleDetail.getDataType(), dataAuthorityRuleDetail.getDataScope()));
                                    // 手工选择
                                } else if ("1004".equals(dataAuthorityRuleDetail.getDataScope())) {
                                    dataAuthValuePropertyDTO.setValueKeyList(dataAuthorityRuleDetail.getDataAuthorityRuleDetailValues()
                                            .stream().map(dataAuthorityRuleDetailValue -> dataAuthorityRuleDetailValue.getValueKey()).collect(Collectors.toList()));
                                }
                                return dataAuthValuePropertyDTO;
                            }).collect(Collectors.toList());
                            dataAuthRulePropertiesMap.put(dataAuthorityRule.getDataAuthorityRuleName(), collect);
                        });
                    }
                    list.add(dataAuthRulePropertiesMap);
                }
            });
        }
        return list;
    }


    private List<String> getDataValueKeyByDataTypeAndDataScope(String dataType,String dataScope){
        List<String> list = new ArrayList<>();
        // 账套
        if(DataAuthorityUtil.SOB_COLUMN.equals(dataType)){
            // 当前
//            if("1002".equals(dataScope)) {
                list.add(LoginInformationUtil.getCurrentSetOfBookID().toString());
            // 当前及下属
//            }else if("1003".equals(dataScope)){
//                list.add(LoginInformationUtil.getCurrentSetOfBookID().toString());
//            }
        // 公司
        }else if(DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)){
            Long currentCompanyID = LoginInformationUtil.getCurrentCompanyID();
            // 当前
            if("1002".equals(dataScope)) {
                list.add(currentCompanyID.toString());
            // 当前及下属
            }else if("1003".equals(dataScope)){
                Set<Long> set = new HashSet<>();
                set.add(currentCompanyID);
                Set<Long> companyChildrenIdByCompanyIds = getCompanyChildrenIdByCompanyIds(set, null);
                list.add(currentCompanyID.toString());
                list.addAll(companyChildrenIdByCompanyIds.stream().map(id -> id.toString()).collect(Collectors.toList()));
            }
        // 部门
        }else if(DataAuthorityUtil.UNIT_COLUMN.equals(dataType)){
            Long unitIdByUserId = dataAuthorityMapper.getUnitIdByUserId(LoginInformationUtil.getCurrentUserID());
            // 当前
            if("1002".equals(dataScope)) {
                list.add(unitIdByUserId.toString());
            // 当前及下属
            }else if("1003".equals(dataScope)){
                Set<Long> set = new HashSet<>();
                set.add(unitIdByUserId);
                Set<Long> unitChildrenIdByUnitIds = getUnitChildrenIdByUnitIds(set, null);
                list.add(unitIdByUserId.toString());
                list.addAll(unitChildrenIdByUnitIds.stream().map(id -> id.toString()).collect(Collectors.toList()));
            }
        // 员工
        }else if(DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)){
            list.add(LoginInformationUtil.getCurrentUserID().toString());
        }
        return list;
    }

    /**
     * 根据公司ID获取下属公司
     * @param companyIds    公司ID
     * @param summaryIds
     * @return
     */
    private Set<Long> getCompanyChildrenIdByCompanyIds(Set<Long> companyIds,Set<Long> summaryIds){
        if(summaryIds == null){
            summaryIds = new HashSet<>();
        }
        if(CollectionUtils.isEmpty(companyIds)){
            return summaryIds;
        }
        // 获取子公司
        Set<Long> companyChildrenIdByCompanyIds = dataAuthorityMapper.getCompanyChildrenIdByCompanyIds(companyIds);
        // 当子公司集合不为空
        if(CollectionUtils.isNotEmpty(companyChildrenIdByCompanyIds)){
            // 添加本次查询的
            boolean b = summaryIds.addAll(companyChildrenIdByCompanyIds);
            if(b){
                getCompanyChildrenIdByCompanyIds(companyChildrenIdByCompanyIds,summaryIds);
            }
        }
        return summaryIds;
    }

    /**
     * 根据公司ID获取下属公司
     * @param unitIds    公司ID
     * @param summaryIds
     * @return
     */
    private Set<Long> getUnitChildrenIdByUnitIds(Set<Long> unitIds,Set<Long> summaryIds){
        if(summaryIds == null){
            summaryIds = new HashSet<>();
        }
        if(CollectionUtils.isEmpty(unitIds)){
            return summaryIds;
        }
        // 获取子部门
        Set<Long> unitChildrenIdByUnitIds = dataAuthorityMapper.getUnitChildrenIdByUnitIds(unitIds);
        // 当子部门集合不为空
        if(CollectionUtils.isNotEmpty(unitChildrenIdByUnitIds)){
            // 添加本次查询的
            boolean b = summaryIds.addAll(unitChildrenIdByUnitIds);
            if(b){
                getCompanyChildrenIdByCompanyIds(unitChildrenIdByUnitIds,summaryIds);
            }
        }
        return summaryIds;
    }



}
