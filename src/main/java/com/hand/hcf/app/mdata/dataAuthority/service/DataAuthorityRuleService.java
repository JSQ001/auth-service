package com.hand.hcf.app.mdata.dataAuthority.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.hand.hcf.app.common.co.CompanyCO;
import com.hand.hcf.app.common.co.SysCodeValueCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.service.BaseI18nService;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.DataAuthorityUtil;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.TypeConversionUtils;
import com.hand.hcf.app.mdata.base.util.OrgInformationUtil;
import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.service.CompanyService;
import com.hand.hcf.app.mdata.contact.dto.ContactDTO;
import com.hand.hcf.app.mdata.contact.dto.ContactQO;
import com.hand.hcf.app.mdata.contact.service.ContactService;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthorityRule;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthorityRuleDetail;
import com.hand.hcf.app.mdata.dataAuthority.domain.DataAuthorityRuleDetailValue;
import com.hand.hcf.app.mdata.dataAuthority.dto.DataAuthRuleDetailValueDTO;
import com.hand.hcf.app.mdata.dataAuthority.persistence.DataAuthorityRuleMapper;
import com.hand.hcf.app.mdata.department.domain.Department;
import com.hand.hcf.app.mdata.department.service.DepartmentGroupService;
import com.hand.hcf.app.mdata.department.service.DepartmentService;
import com.hand.hcf.app.mdata.externalApi.HcfOrganizationInterface;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.service.SetOfBooksService;
import com.hand.hcf.app.mdata.utils.RespCode;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DataAuthorityRuleService extends BaseService<DataAuthorityRuleMapper, DataAuthorityRule> {

    private final DataAuthorityRuleMapper dataAuthorityRuleMapper;
    private final DataAuthorityRuleDetailService dataAuthorityRuleDetailService;
    private final DataAuthorityRuleDetailValueService dataAuthorityRuleDetailValueService;
    private final BaseI18nService baseI18nService;
    private final DepartmentService departmentService;
    private final DepartmentGroupService departmentGroupService;
    private final CompanyService companyService;
    private final SetOfBooksService sobService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private HcfOrganizationInterface hcfOrganizationInterface;
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
            entity.getDataAuthorityRuleDetails().stream().forEach(e -> setDataAuthorityRuleDetailValueDTOs(e));
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
            dataAuthorityRuleDetails.stream().forEach(dataAuthorityRuleDetail -> {
                setDataAuthorityRuleDetailValueDTOs(dataAuthorityRuleDetail);
            });
            dataAuthorityRule.setDataAuthorityRuleDetails(dataAuthorityRuleDetails);
        });
        return dataAuthRules;
    }

    private void setDataAuthorityRuleDetailValueDTOs(DataAuthorityRuleDetail dataAuthorityRuleDetail){
        if("1004".equals(dataAuthorityRuleDetail.getDataScope())){
            List<Long> keyIds = dataAuthorityRuleDetail.getDataAuthorityRuleDetailValues().stream()
                    .map(e -> TypeConversionUtils.parseLong(e)).collect(Collectors.toList());
            if(DataAuthorityUtil.SOB_COLUMN.equals(dataAuthorityRuleDetail.getDataType())){
                List<SetOfBooks> setOfBooksListByIds = sobService.getSetOfBooksListByIds(keyIds);
                dataAuthorityRuleDetail.setDataAuthorityRuleDetailValueDTOs(setOfBooksToDetailValueDTO(
                        setOfBooksListByIds,getFiltrateMethodDescription(null,null)));
            }else if(DataAuthorityUtil.COMPANY_COLUMN.equals(dataAuthorityRuleDetail.getDataType())){
                List<Company> companiesByIds = companyService.selectBatchIds(keyIds);
                List<DataAuthRuleDetailValueDTO> collect = companiesByIds.stream().sorted(Comparator.comparing(e -> e.getCompanyCode())).map(e -> {
                    return DataAuthRuleDetailValueDTO.builder()
                            .valueKey(e.getId().toString())
                            .valueKeyCode(e.getCompanyCode())
                            .valueKeyDesc(e.getName()).filtrateMethodDesc(getFiltrateMethodDescription(null, null)).build();
                }).collect(Collectors.toList());
                dataAuthorityRuleDetail.setDataAuthorityRuleDetailValueDTOs(collect);
            }else if(DataAuthorityUtil.UNIT_COLUMN.equals(dataAuthorityRuleDetail.getDataType())){
                List<Department> departmentByDepartmentIds = departmentService.selectBatchIds(keyIds);
                List<DataAuthRuleDetailValueDTO> dataAuthRuleDetailValueDTOS = departmentToDetailValueDTO(departmentByDepartmentIds.stream().sorted(Comparator.comparing(e -> e.getDepartmentCode())).collect(Collectors.toList()),
                        getFiltrateMethodDescription(null, null));
                dataAuthorityRuleDetail.setDataAuthorityRuleDetailValueDTOs(dataAuthRuleDetailValueDTOS);
            }else if(DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataAuthorityRuleDetail.getDataType())){
                List<ContactDTO> contactDTOS = contactService.listDtoByQO(
                        ContactQO.builder().userIds(keyIds)
                                .hasCompany(false)
                                .hasDepartment(false).build());
                dataAuthorityRuleDetail.setDataAuthorityRuleDetailValueDTOs(
                        userToDetailValueDTO(contactDTOS,getFiltrateMethodDescription(null,null)));
            }
        }
    }

    private void deleteDataAuthRule(DataAuthorityRule dataAuthorityRule){
        dataAuthorityRule.setDeleted(true);
        dataAuthorityRule.setDataAuthorityRuleName(dataAuthorityRule.getDataAuthorityRuleName() + "_DELETED_" + RandomStringUtils.randomNumeric(6));
        dataAuthorityRuleMapper.updateById(dataAuthorityRule);
    }

    /**
     * 根据数据权限ID删除规则
     *
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

    public List getDataAuthRuleDetailValuesByDataType(Long ruleId,
                                                      String dataType,
                                                      String keyWord,
                                                      Page page){
        DataAuthorityRuleDetail dataAuthorityRuleDetail = dataAuthorityRuleDetailService.selectOne(new EntityWrapper<DataAuthorityRuleDetail>()
                .eq("data_authority_rule_id", ruleId).eq("data_type", dataType));
        Map<String, SysCodeValueCO> filtrateMethodMap = hcfOrganizationInterface.listAllSysCodeValueByCode("3103").stream().collect(Collectors.toMap(e -> e.getValue(), e -> e));
        switch (dataAuthorityRuleDetail.getDataScope()) {
            // 全部
            case "1001": {
                if (DataAuthorityUtil.SOB_COLUMN.equals(dataType)) {
                    Page<SetOfBooks> setOfBooksListByTenantId = sobService.getSetOfBooksListByTenantId(LoginInformationUtil.getCurrentTenantId(), null, null, keyWord, null, page);
                    page.setTotal(setOfBooksListByTenantId.getTotal());
                    return setOfBooksToDetailValueDTO(setOfBooksListByTenantId.getRecords(), getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                } else if (DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)) {
                    List<Company> companies = companyService.getCompaniesByTenantId(LoginInformationUtil.getCurrentTenantId(),keyWord ,page);
                    return companyToDetailValueDTO(companies, getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                } else if (DataAuthorityUtil.UNIT_COLUMN.equals(dataType)) {
                    List<Department> departmentInfoByTenantId = departmentService.getDepartmentInfoByTenantId(LoginInformationUtil.getCurrentTenantId(), keyWord, page);
                    return departmentToDetailValueDTO(departmentInfoByTenantId, getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                } else if (DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)) {
                    List<ContactDTO> contactDTOS = contactService.listDtoByQO(
                            ContactQO.builder().tenantId(LoginInformationUtil.getCurrentTenantId())
                                    .hasDepartment(false)
                                    .keyContact(keyWord)
                                    .hasCompany(false).build(),page);
                    return userToDetailValueDTO(contactDTOS, getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                }
                break;
            }
            // 当前
            case "1002": {
                page.setTotal(1);
                if (DataAuthorityUtil.SOB_COLUMN.equals(dataType)) {
                    List<SetOfBooks> setOfBookById = sobService.getSetOfBooksListByIdKeyWord(Collections.singletonList(OrgInformationUtil.getCurrentSetOfBookId()),keyWord);
                    return setOfBooksToDetailValueDTO(setOfBookById, getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                } else if (DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)) {
                    List<Company> companys = companyService.findCompanyByIdKeyWord(Collections.singletonList(OrgInformationUtil.getCurrentCompanyId()),keyWord);
                    return companyToDetailValueDTO(companys, getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                } else if (DataAuthorityUtil.UNIT_COLUMN.equals(dataType)) {
                    List<Department> units = departmentService.selectByEmpOidKeyWord(OrgInformationUtil.getCurrentUserOid().toString(),null,keyWord);
                    return departmentToDetailValueDTO(units, getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                } else if (DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)) {
                    List<ContactDTO> contactDTOs = contactService.getDtoByQOKeyWord(
                            Collections.singletonList(LoginInformationUtil.getCurrentUserId()),keyWord);
                    return userToDetailValueDTO(contactDTOs, getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                }
                break;
            }
            // 当前及下属
            case "1003": {
                if (DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)) {
                    Page<CompanyCO> sonAndOwnCompanyByCond = companyService.pageChildrenCompaniesByCondition(OrgInformationUtil.getCurrentCompanyId(),
                            false,null, null, null, null, keyWord, null, page);
                    return sonAndOwnCompanyByCond.getRecords().stream().map(e -> {
                        return DataAuthRuleDetailValueDTO.builder()
                                .valueKey(e.getId().toString())
                                .valueKeyCode(e.getCompanyCode())
                                .valueKeyDesc(e.getName())
                                .filtrateMethodDesc(getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod())).build();
                    }).collect(Collectors.toList());
                } else if (DataAuthorityUtil.UNIT_COLUMN.equals(dataType)) {
                    Long unitIdByUserId = departmentGroupService.selectByEmpOid(LoginInformationUtil.getCurrentUserOid().toString()).getDepartmentId();
                    Page departmentPage = departmentService.departmentChildrenById(unitIdByUserId,keyWord,true, page);
                    return departmentToDetailValueDTO(departmentPage.getRecords(), getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                }
                break;
            }
            // 手工选择
            case "1004": {
                List<String> dataAuthorityRuleDetailValues = dataAuthorityRuleDetailValueService.
                        queryAllDataAuthorityRuleDetailValues(dataAuthorityRuleDetail.getId());
                List<Long> keyIds = dataAuthorityRuleDetailValues.stream()
                        .map(e -> TypeConversionUtils.parseLong(e)).collect(Collectors.toList());
                if (DataAuthorityUtil.SOB_COLUMN.equals(dataType)) {
                    List<SetOfBooks> setOfBooksListByIds = sobService.getSetOfBooksListByIdKeyWord(keyIds,keyWord);
                    return setOfBooksToDetailValueDTO(PageUtil.pageHandler(
                            page, setOfBooksListByIds.stream().sorted(Comparator.comparing(e -> e.getSetOfBooksCode())).collect(Collectors.toList())), getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                } else if (DataAuthorityUtil.COMPANY_COLUMN.equals(dataType)) {
                    List<Company> companiesByIds = companyService.findCompanyByIdKeyWord(keyIds,keyWord);
                    List<DataAuthRuleDetailValueDTO> collect = companiesByIds.stream().sorted(Comparator.comparing(e -> e.getCompanyCode())).map(e -> {
                        return DataAuthRuleDetailValueDTO.builder()
                                .valueKey(e.getId().toString())
                                .valueKeyCode(e.getCompanyCode())
                                .valueKeyDesc(e.getName()).filtrateMethodDesc(getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod())).build();
                    }).collect(Collectors.toList());
                    return PageUtil.pageHandler(page, collect);
                } else if (DataAuthorityUtil.UNIT_COLUMN.equals(dataType)) {
                    List<Department> departmentByDepartmentIds = departmentService.selectByEmpOidKeyWord(null,keyIds,keyWord);
                    return departmentToDetailValueDTO(PageUtil.pageHandler(page,
                            departmentByDepartmentIds.stream().sorted(Comparator.comparing(e -> e.getDepartmentCode())).collect(Collectors.toList())), getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                } else if (DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)) {
                    List<ContactDTO> contactDTOS = contactService.getDtoByQOKeyWord(keyIds,keyWord);
                    return userToDetailValueDTO(PageUtil.pageHandler(page,
                            contactDTOS.stream().sorted(Comparator.comparing(e -> e.getEmployeeId())).collect(Collectors.toList())), getFiltrateMethodDescription(filtrateMethodMap, dataAuthorityRuleDetail.getFiltrateMethod()));
                }
                break;
            }
            default:
                return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private String getFiltrateMethodDescription(Map<String, SysCodeValueCO> filtrateMethodMap, String filtrateMethod) {
        if (StringUtils.isEmpty(filtrateMethod)) {
            filtrateMethod = "INCLUDE";
        }
        if(filtrateMethodMap == null){
            return "";
        }
        SysCodeValueCO sysCodeValueCO = filtrateMethodMap.get(filtrateMethod);
        if(sysCodeValueCO != null){
            return sysCodeValueCO.getName();
        }
        return "";
    }

    private List<DataAuthRuleDetailValueDTO> setOfBooksToDetailValueDTO(List<SetOfBooks> setOfBooksInfoDTOS, String filtrateMethodDesc) {
        if (CollectionUtils.isEmpty(setOfBooksInfoDTOS)) {
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

    private List<DataAuthRuleDetailValueDTO> companyToDetailValueDTO(List<Company> companySumDTOS, String filtrateMethodDesc) {
        if (CollectionUtils.isEmpty(companySumDTOS)) {
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

    private List<DataAuthRuleDetailValueDTO> departmentToDetailValueDTO(List<Department> departmentInfoDTOS, String filtrateMethodDesc) {
        if (CollectionUtils.isEmpty(departmentInfoDTOS)) {
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

    private List<DataAuthRuleDetailValueDTO> userToDetailValueDTO(List<ContactDTO> contactDTOS, String filtrateMethodDesc) {
        if (CollectionUtils.isEmpty(contactDTOS)) {
            return new ArrayList<>();
        }
        return contactDTOS.stream().map(e -> {
            return DataAuthRuleDetailValueDTO.builder()
                    .valueKey(e.getUserId().toString())
                    .valueKeyCode(e.getEmployeeId())
                    .valueKeyDesc(e.getFullName())
                    .filtrateMethodDesc(filtrateMethodDesc).build();
        }).collect(Collectors.toList());
    }

    /**
     * 获取待选择数据
     *
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
                                                                                        Page page) {

        List<Long> keyIds = null;
        if (ruleId != null) {
            DataAuthorityRuleDetail dataAuthorityRuleDetail = dataAuthorityRuleDetailService.selectOne(new EntityWrapper<DataAuthorityRuleDetail>()
                    .eq("data_authority_rule_id", ruleId).eq("data_type", dataType));
            if("1004".equals(dataAuthorityRuleDetail.getDataScope())){
                keyIds =
                        dataAuthorityRuleDetailValueService.queryAllDataAuthorityRuleDetailValues(dataAuthorityRuleDetail.getId()).stream()
                                .map(e -> TypeConversionUtils.parseLong(e)).collect(Collectors.toList());
            }
        }
        // 全部
        if("all".equals(scope) || "noChoose".equals(scope)){
            List<Long> ids = null;
            // 未选择
            if("noChoose".equals(scope)){
                ids = keyIds;
            }
            if (DataAuthorityUtil.SOB_COLUMN.equals(dataType)) {
                Page<SetOfBooks> setOfBooksListByTenantId = sobService.getSetOfBooksListByTenantId(LoginInformationUtil.getCurrentTenantId(), code, name, null, ids, page);
                page.setTotal(setOfBooksListByTenantId.getTotal());
                return setOfBooksToDetailValueDTO(setOfBooksListByTenantId.getRecords(), null);
            } else if (DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)) {
                List<ContactDTO> contactByTenantId = contactService.listDtoByQO(
                        ContactQO.builder().tenantId(LoginInformationUtil.getCurrentTenantId())
                                .employeeId(code)
                                .fullName(name)
                                .userIds(ids)
                                .inverseUser(true)
                                .hasCompany(false)
                                .hasDepartment(false).build(),page);
                return userToDetailValueDTO(contactByTenantId, null);
            }
            // 已选择
        } else if ("selected".equals(scope)) {
            if (DataAuthorityUtil.SOB_COLUMN.equals(dataType)) {
                if (CollectionUtils.isNotEmpty(keyIds)) {
                    List<SetOfBooks> setOfBooksListByIds = sobService.getSetOfBooksListByIds(keyIds, null, null);
                    return setOfBooksToDetailValueDTO(PageUtil.pageHandler(
                            page, setOfBooksListByIds.stream().sorted(Comparator.comparing(e -> e.getSetOfBooksCode())).collect(Collectors.toList())), null);
                }
                return Arrays.asList();
            } else if (DataAuthorityUtil.EMPLOYEE_COLUMN.equals(dataType)) {
                if (CollectionUtils.isNotEmpty(keyIds)) {
                    List<ContactDTO> contactDTOS = contactService.listDtoByQO(
                            ContactQO.builder().userIds(keyIds)
                                    .hasCompany(false)
                                    .hasDepartment(false).build());
                    return userToDetailValueDTO(PageUtil.pageHandler(page,
                            contactDTOS.stream().sorted(Comparator.comparing(e -> e.getEmployeeId())).collect(Collectors.toList())), null);
                }
                return Arrays.asList();
            }
        }
        return Arrays.asList();
    }
}
