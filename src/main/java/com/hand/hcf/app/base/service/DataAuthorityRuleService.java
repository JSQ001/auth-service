package com.hand.hcf.app.base.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.base.domain.DataAuthorityRuleDetailValue;
import com.hand.hcf.app.base.dto.DataAuthRuleDetailValueDTO;
import com.hand.hcf.app.base.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.client.com.CompanyService;
import com.hand.hcf.app.client.com.CompanySumDTO;
import com.hand.hcf.app.client.department.DepartmentInfoDTO;
import com.hand.hcf.app.client.department.DepartmentService;
import com.hand.hcf.app.client.org.CustomEnumerationItemDTO;
import com.hand.hcf.app.client.org.ObjectIdsDTO;
import com.hand.hcf.app.client.sob.SetOfBooksInfoDTO;
import com.hand.hcf.app.client.sob.SobService;
import com.hand.hcf.app.client.user.UserInfoDTO;
import com.hand.hcf.app.client.user.UserService;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseI18nService;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.domain.DataAuthorityRule;
import com.hand.hcf.app.base.domain.DataAuthorityRuleDetail;
import com.hand.hcf.app.base.persistence.DataAuthorityRuleMapper;
import com.hand.hcf.core.util.DataAuthorityUtil;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.TypeConversionUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final DepartmentService departmentService;
    private final CompanyService companyService;
    private final SobService sobService;
    private final UserService userService;
    private final HcfOrganizationInterface hcfOrganizationInterface;

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
            throw new BizException(RespCode.AUTH_DATA_AUTHORITY_RULE_EXISTS);
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
    public List<DataAuthorityRule> queryDataAuthorityRules(Long dataAuthorityId,Long ruleId){
        List<DataAuthorityRule> dataAuthRules = dataAuthorityRuleMapper.selectList(new EntityWrapper<DataAuthorityRule>()
                .eq("data_authority_id", dataAuthorityId)
                .eq(ruleId != null,"id",ruleId));
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

    public List<DataAuthRuleDetailValueDTO> getDataAuthRuleDetailValuesByDataType(Long ruleId,
                                                                                  String dataType,
                                                                                  String keyWord,
                                                                                  Page page){
        DataAuthorityRuleDetail dataAuthorityRuleDetail = dataAuthorityRuleDetailService.selectOne(new EntityWrapper<DataAuthorityRuleDetail>()
                .eq("data_authority_rule_id", ruleId).eq("data_type", dataType));
        Map<String, CustomEnumerationItemDTO> filtrateMethodMap = hcfOrganizationInterface.getSysCodeValues("3103").stream().collect(Collectors.toMap(e -> e.getValue(), e -> e));
        switch (dataAuthorityRuleDetail.getDataScope()){
            // 全部
            case "1001" :{
                if(DataAuthorityUtil.SOB_COLUMN.equals(dataType)){
                    Page<SetOfBooksInfoDTO> setOfBooksListByTenantId = sobService.getSetOfBooksListByTenantIdAndKeyWord(LoginInformationUtil.getCurrentTenantID(),keyWord,null, page);
                    page.setTotal(setOfBooksListByTenantId.getTotal());
                    return setOfBooksToDetailValueDTO(setOfBooksListByTenantId.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)){
                    Page<CompanySumDTO> companiesByTenantId = companyService.getCompaniesByTenantId(LoginInformationUtil.getCurrentTenantID(), keyWord, page);
                    page.setTotal(companiesByTenantId.getTotal());
                    return companyToDetailValueDTO(companiesByTenantId.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.UNIT_COLUMN.equals(dataType)){
                    Page<DepartmentInfoDTO> departmentInfoByTenantId = departmentService.getDepartmentInfoByTenantId(LoginInformationUtil.getCurrentTenantID(), keyWord, page);
                    page.setTotal(departmentInfoByTenantId.getTotal());
                    return departmentToDetailValueDTO(departmentInfoByTenantId.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)){
                    Page<UserInfoDTO> usersByTenantId = userService.getUsersByTenantIdAndKeyWord(LoginInformationUtil.getCurrentTenantID(),keyWord,null, page);
                    page.setTotal(usersByTenantId.getTotal());
                    return userToDetailValueDTO(usersByTenantId.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }
                break;
            }
            // 当前
            case "1002":{
                page.setTotal(1);
                if(DataAuthorityUtil.SOB_COLUMN.equals(dataType)){
                    SetOfBooksInfoDTO setOfBookById = sobService.getSetOfBookById(LoginInformationUtil.getCurrentSetOfBookID());
                    return setOfBooksToDetailValueDTO(Arrays.asList(setOfBookById),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)){
                    CompanySumDTO companyById = companyService.findCompanyById(LoginInformationUtil.getCurrentCompanyID());
                    return companyToDetailValueDTO(Arrays.asList(companyById),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.UNIT_COLUMN.equals(dataType)){
                    Long unitIdByUserId = departmentService.findDepartmentByUserOid(LoginInformationUtil.getCurrentUserOID().toString()).getDepartmentId();
                    return departmentToDetailValueDTO(departmentService.getDepartmentByDepartmentIds(Arrays.asList(unitIdByUserId)),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)){
                    UserInfoDTO userInfoDTO = userService.selectUsersByUserId(LoginInformationUtil.getCurrentUserID());
                    return userToDetailValueDTO(Arrays.asList(userInfoDTO),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }
                break;
            }
            // 当前及下属
            case "1003":{
                if(DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)){
                    Page<CompanySumDTO> sonAndOwnCompanyByCond = companyService.getSonAndOwnCompanyByCond(LoginInformationUtil.getCurrentCompanyID(),
                            null, null, null, null, keyWord, page);
                    page.setTotal(sonAndOwnCompanyByCond.getTotal());
                    return companyToDetailValueDTO(sonAndOwnCompanyByCond.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.UNIT_COLUMN.equals(dataType)){
                    Long unitIdByUserId = departmentService.findDepartmentByUserOid(LoginInformationUtil.getCurrentUserOID().toString()).getDepartmentId();
                    Page<DepartmentInfoDTO> unitChildrenAndOwnByUnitId = departmentService.getUnitChildrenAndOwnByUnitId(unitIdByUserId, keyWord,page);
                    page.setTotal(unitChildrenAndOwnByUnitId.getTotal());
                    return departmentToDetailValueDTO(unitChildrenAndOwnByUnitId.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)){
                    return new ArrayList<>();
                }
                break;
            }
            // 手工选择
            case "1004":{
                List<String> dataAuthorityRuleDetailValues = dataAuthorityRuleDetailValueService.
                        queryAllDataAuthorityRuleDetailValues(dataAuthorityRuleDetail.getId());
                List<Long> keyIds = dataAuthorityRuleDetailValues.stream()
                        .map(e -> TypeConversionUtils.parseLong(e)).collect(Collectors.toList());
                if(DataAuthorityUtil.SOB_COLUMN.equals(dataType)){
                    Page<SetOfBooksInfoDTO> setOfBooksListByIds = sobService.getSetOfBooksListByIdsResultPage(keyIds, keyWord,page);
                    page.setTotal(setOfBooksListByIds.getTotal());
                    return setOfBooksToDetailValueDTO(setOfBooksListByIds.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)){
                    Page<CompanySumDTO> companiesByIds = companyService.findCompanyListByIdListResultPage(keyIds, keyWord, page);
                    page.setTotal(companiesByIds.getTotal());
                    return companyToDetailValueDTO(companiesByIds.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.UNIT_COLUMN.equals(dataType)){
                    Page<DepartmentInfoDTO> departmentInfoDTOPage = departmentService.selectDepartmentsByIdsResultPage(keyIds, keyWord, page);
                    page.setTotal(departmentInfoDTOPage.getTotal());
                    return departmentToDetailValueDTO(departmentInfoDTOPage.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }else if(DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)){
                    Page<UserInfoDTO> userInfoDTOS = userService.selectUsersByUserIdsAndKeyWordResultPage(keyIds,keyWord,page);
                    return userToDetailValueDTO(userInfoDTOS.getRecords(),getFiltrateMethodDescription(filtrateMethodMap,dataAuthorityRuleDetail.getFiltrateMethod()));
                }
                break;
            }
            default: return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private String getFiltrateMethodDescription(Map<String, CustomEnumerationItemDTO> filtrateMethodMap,String filtrateMethod){
        if(StringUtils.isEmpty(filtrateMethod)){
            filtrateMethod = "INCLUDE";
        }
        CustomEnumerationItemDTO customEnumerationItemDTO = filtrateMethodMap.get(filtrateMethod);
        if(customEnumerationItemDTO != null){
            return customEnumerationItemDTO.getMessageKey();
        }
        return "";
    }

    private List<DataAuthRuleDetailValueDTO> setOfBooksToDetailValueDTO(List<SetOfBooksInfoDTO> setOfBooksInfoDTOS, String filtrateMethodDesc){
        if(CollectionUtils.isEmpty(setOfBooksInfoDTOS)){
            return new ArrayList<>();
        }
        return setOfBooksInfoDTOS.stream().map(e -> {
            return DataAuthRuleDetailValueDTO.builder()
                    .valueKey(e.getId().toString())
                    .valueKeyCode(e.getSetOfBooksCode())
                    .valueKeyDesc(e.getSetOfBooksName())
                    .filtrateMethodDesc(filtrateMethodDesc).build();
        }).collect(Collectors.toList());
    }

    private List<DataAuthRuleDetailValueDTO> companyToDetailValueDTO(List<CompanySumDTO> companySumDTOS, String filtrateMethodDesc){
        if(CollectionUtils.isEmpty(companySumDTOS)){
            return new ArrayList<>();
        }
        return companySumDTOS.stream().map(e -> {
            return DataAuthRuleDetailValueDTO.builder()
                    .valueKey(e.getId().toString())
                    .valueKeyCode(e.getCompanyCode())
                    .valueKeyDesc(e.getName())
                    .filtrateMethodDesc(filtrateMethodDesc).build();
        }).collect(Collectors.toList());
    }

    private List<DataAuthRuleDetailValueDTO> departmentToDetailValueDTO(List<DepartmentInfoDTO> departmentInfoDTOS, String filtrateMethodDesc){
        if(CollectionUtils.isEmpty(departmentInfoDTOS)){
            return new ArrayList<>();
        }
        return departmentInfoDTOS.stream().map(e -> {
            return DataAuthRuleDetailValueDTO.builder()
                    .valueKey(e.getId().toString())
                    .valueKeyCode(e.getDepartmentCode())
                    .valueKeyDesc(e.getName())
                    .filtrateMethodDesc(filtrateMethodDesc).build();
        }).collect(Collectors.toList());
    }

    private List<DataAuthRuleDetailValueDTO> userToDetailValueDTO(List<UserInfoDTO> userInfoDTOS, String filtrateMethodDesc){
        if(CollectionUtils.isEmpty(userInfoDTOS)){
            return new ArrayList<>();
        }
        return userInfoDTOS.stream().map(e -> {
            return DataAuthRuleDetailValueDTO.builder()
                    .valueKey(e.getId().toString())
                    .valueKeyCode(e.getEmployeeID())
                    .valueKeyDesc(e.getFullName())
                    .filtrateMethodDesc(filtrateMethodDesc).build();
        }).collect(Collectors.toList());
    }

    /**
     * 获取待选择数据
     * @param ruleId
     * @param dataType
     * @param page
     * @return
     */
    public List<DataAuthRuleDetailValueDTO> getDataAuthRuleDetailSelectValuesByDataType(Long ruleId,
                                                                                        String dataType,
                                                                                        String scope,
                                                                                        String code,
                                                                                        String name,
                                                                                        Page page){

        List<Long> keyIds = null;
        if(ruleId != null){
            DataAuthorityRuleDetail dataAuthorityRuleDetail = dataAuthorityRuleDetailService.selectOne(new EntityWrapper<DataAuthorityRuleDetail>()
                    .eq("data_authority_rule_id", ruleId).eq("data_type", dataType));
            if("1004".equals(dataAuthorityRuleDetail.getDataScope())){
                keyIds =
                        dataAuthorityRuleDetailValueService.queryAllDataAuthorityRuleDetailValues(dataAuthorityRuleDetail.getDataAuthorityRuleId()).stream()
                                .map(e -> TypeConversionUtils.parseLong(e)).collect(Collectors.toList());
            }
        }
        // 全部
        if("all".equals(scope) || "notChoose".equals(scope)){
            List<Long> ids = null;
            // 未选择
            if("notChoose".equals(scope)){
                ids = keyIds;
            }
            if(DataAuthorityUtil.SOB_COLUMN.equals(dataType)){
                Page<SetOfBooksInfoDTO> setOfBooksListByTenantId = sobService.getSetOfBooksListByTenantId(LoginInformationUtil.getCurrentTenantID(), code, name, ids, page);
                page.setTotal(setOfBooksListByTenantId.getTotal());
                return setOfBooksToDetailValueDTO(setOfBooksListByTenantId.getRecords(),null);
            }else if(DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)){
                Page<UserInfoDTO> usersByTenantId = userService.getUsersByTenantId(LoginInformationUtil.getCurrentTenantID(), code, name, ids, page);
                page.setTotal(usersByTenantId.getTotal());
                return userToDetailValueDTO(usersByTenantId.getRecords(),null);
            }
        // 已选择
        }else if("selected".equals(scope)){
            if(DataAuthorityUtil.SOB_COLUMN.equals(dataType)){
                if(CollectionUtils.isNotEmpty(keyIds)){
                    List<SetOfBooksInfoDTO> setOfBooksListByIds = sobService.getSetOfBooksListByIds(keyIds);
                    return setOfBooksToDetailValueDTO(PageUtil.pageHandler(
                            page, setOfBooksListByIds.stream().sorted(Comparator.comparing(e -> e.getSetOfBooksCode())).collect(Collectors.toList())),null);
                }
                return Arrays.asList();
            }else if(DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)){
                if(CollectionUtils.isNotEmpty(keyIds)){
                    List<UserInfoDTO> userInfoDTOS = userService.selectUsersByUserIds(keyIds);
                    return userToDetailValueDTO(PageUtil.pageHandler(page,
                            userInfoDTOS.stream().sorted(Comparator.comparing(e->e.getEmployeeID())).collect(Collectors.toList())),null);
                }
                return Arrays.asList();
            }
        }
        return Arrays.asList();
    }
}
